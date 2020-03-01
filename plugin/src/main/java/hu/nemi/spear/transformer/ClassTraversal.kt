package hu.nemi.spear.transformer

import com.android.build.api.transform.*
import org.apache.commons.io.FileUtils
import org.gradle.api.logging.Logging
import org.gradle.internal.impldep.org.apache.ivy.util.FileUtil
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.util.jar.JarFile

class ClassTraversal(private val config: TransformConfig) {
    private val logger = Logging.getLogger(ClassTraversal::class.java)
    private val instrumenter =
        ClassInstrumenter(buildRuntimeClasspath())

    fun perform() {
        for (input in config.invocation.inputs) {
            transformInput(input)
        }
    }

    private fun transformInput(input: TransformInput) {
        transformDirectoryInputs(input.directoryInputs)
        transformJarInputs(input.jarInputs)
    }

    private fun transformJarInputs(jarInputs: Collection<JarInput>) {
        jarInputs.forEach { jarInput -> transformJarInput(jarInput) }
    }


    private fun transformDirectoryInputs(directoryInputs: Collection<DirectoryInput>) {
        directoryInputs.forEach { directoryInput -> transformDirectoryInput(directoryInput) }
    }

    private fun transformDirectoryInput(input: DirectoryInput) {
        val outDir = config.invocation.outputProvider.getContentLocation(
            input.name,
            input.contentTypes,
            input.scopes,
            Format.DIRECTORY
        )

        if (config.invocation.isIncremental) {
            input.changedFiles.forEach { changedFile ->
                when (changedFile.value) {
                    Status.ADDED, Status.CHANGED -> {
                        val relativeFile = normalizedRelativeFilePath(input.file, changedFile.key)
                        val destFile = File(outDir, relativeFile)
                        changedFile.key.inputStream().use { inputStream ->
                            destFile.outputStream().use { outputStream ->
                                if (canInstrument(relativeFile)) {
                                    processClass(relativeFile, inputStream, outputStream)
                                } else {
                                    copyStream(inputStream, outputStream)
                                }
                            }
                        }
                    }
                    Status.REMOVED -> {
                        val relativeFile = normalizedRelativeFilePath(input.file, changedFile.key)
                        val destFile = File(outDir, relativeFile)
                        FileUtil.forceDelete(destFile)
                    }
                    Status.NOTCHANGED, null -> Unit
                }
            }
        } else {
            ensureDirectory(outDir)
            FileUtils.cleanDirectory(outDir)
            FileUtils.iterateFiles(input.file, null, true).forEach { file ->
                val relativeFile = normalizedRelativeFilePath(input.file, file)
                val destFile = File(outDir, relativeFile)
                ensureDirectory(destFile.parentFile)
                file.inputStream().use { inputStream ->
                    destFile.outputStream().use { outputStream ->
                        if (canInstrument(relativeFile)) {
                            processClass(relativeFile, inputStream, outputStream)
                        } else {
                            copyStream(inputStream, outputStream)
                        }
                    }

                }
            }
        }
    }


    private fun transformJarInput(input: JarInput) {
        val outDir = config.invocation.outputProvider.getContentLocation(
            input.name,
            input.contentTypes,
            input.scopes,
            Format.DIRECTORY
        )

        when {
            !config.invocation.isIncremental || input.status == Status.ADDED || input.status == Status.CHANGED -> {
                ensureDirectory(outDir)
                FileUtils.cleanDirectory(outDir)
                val jarFile = JarFile(input.file)
                jarFile.entries().asSequence().filter { !it.isDirectory }
                    .forEach { entry ->
                        val outFile = File(outDir, entry.name)
                        ensureDirectory(outFile.parentFile)
                        jarFile.getInputStream(entry).use { inputStream ->
                            outFile.outputStream().use { outputStream ->
                                if (canInstrument(entry.name)) {
                                    processClass(entry.name, inputStream, outputStream)
                                } else {
                                    copyStream(inputStream, outputStream)
                                }
                            }
                        }
                    }
            }
            input.status == Status.REMOVED && outDir.exists() -> FileUtils.forceDelete(outDir)
        }
    }

    private fun processClass(
        path: String,
        inputStream: InputStream,
        outputStream: OutputStream
    ) {
        val inputBytes = inputStream.readBytes()
        val outputBytes = try {
            instrumenter.instrument(inputBytes)
        } catch (error: Throwable) {
            logger.error("Failed to instrument $path", error)
            inputBytes
        }
        outputStream.write(outputBytes)
    }

    private fun canInstrument(path: String): Boolean =
        path.toLowerCase().endsWith(".class")

    private fun copyStream(inputStream: InputStream, outputStream: OutputStream) {
        inputStream.copyTo(outputStream)
    }

    private fun normalizedRelativeFilePath(parent: File, file: File): String {
        val parts = mutableListOf<String>()
        var current = file
        while (current != parent) {
            parts.add(current.name)
            current = current.parentFile
        }
        return parts.asReversed().joinToString("/")
    }

    private fun ensureDirectory(file: File) {
        if (!((file.isDirectory && file.canWrite()) || file.mkdirs())) {
            throw IOException("Can't write or create ${file.path}")
        }
    }

    private fun buildRuntimeClasspath(): List<URL> {
        val allTransformInputs = config.invocation.inputs + config.invocation.referencedInputs
        val allJarsAndDirs = allTransformInputs.map { ti ->
            (ti.directoryInputs + ti.jarInputs).map { i -> i.file }
        }
        val allClassesAtRuntime = config.androidClasspath + allJarsAndDirs.flatten()
        return allClassesAtRuntime.map { file -> file.toURI().toURL() }
    }
}
