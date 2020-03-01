package hu.nemi.spear.transformer

import hu.nemi.spear.transformer.extractor.ClassInfoExtractor
import hu.nemi.spear.transformer.transformers.ModuleAdapterTransformer
import hu.nemi.spear.transformer.transformers.ModuleTrasnformer
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.net.URL
import java.net.URLClassLoader

class ClassInstrumenter(private val runtimeClasspath: List<URL>) {

    fun instrument(inputBytes: ByteArray): ByteArray {
        val classReader = ClassReader(inputBytes)
        val classWriter = object : ClassWriter(COMPUTE_MAXS or COMPUTE_FRAMES) {
            override fun getClassLoader(): ClassLoader =
                URLClassLoader(runtimeClasspath.toTypedArray())
        }

        return with(ClassInfoExtractor()) {
            classReader.accept(this, ClassReader.SKIP_FRAMES)

            if (isObject && isModule) {
                classReader.accept(
                    ModuleTrasnformer(
                        classWriter
                    ), ClassReader.SKIP_FRAMES
                )
                classWriter.toByteArray()
            } else if (isObject && isModuleAdapter) {
                classReader.accept(
                    ModuleAdapterTransformer(
                        classWriter,
                        requireNotNull(moduleName)
                    ), ClassReader.SKIP_FRAMES
                )
                classWriter.toByteArray()
            } else {
                inputBytes
            }
        }
    }
}