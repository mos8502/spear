package hu.nemi.spear.transformer.extractor

import kotlinx.metadata.jvm.KotlinClassMetadata
import org.codehaus.groovy.control.CompilerConfiguration
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor

class ClassInfoExtractor : ClassVisitor(CompilerConfiguration.ASM_API_VERSION) {
    private val isObjectVisitor =
        IsObjectVisitor()
    private val kotlinMetadataVisitor =
        KotlinMetadataVisitor()
    private val moduleAdapterVisitor =
        ModuleAdapterVisitor()
    var name: String? = null
        private set
    val isModuleAdapter: Boolean
        get() = moduleAdapterVisitor.isModuleAdapter
    var isModule: Boolean = false
        private set
    val isObject: Boolean
        get() = isObjectVisitor.isObject
    val moduleName: String?
        get() = moduleAdapterVisitor.moduleName
    var isFactory: Boolean = false
        private set
    var creates: String? = null
        private set

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        this.name = name
        isFactory = interfaces?.contains(DAGGER_FACTORY) == true
        if (isFactory) {
            creates = extractCreates(requireNotNull(signature))
        }
    }

    override fun visitEnd() {
        super.visitEnd()
        val classMetadata = kotlinMetadataVisitor.metadata as? KotlinClassMetadata.Class ?: return
        classMetadata.accept(isObjectVisitor)
        classMetadata.accept(moduleAdapterVisitor)
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor? {
        when (descriptor) {
            KOTLIN_METADATA_DESCRIPTOR -> return kotlinMetadataVisitor
            DAGGER_MODULE_DESCRIPTOR -> isModule = true
        }
        return super.visitAnnotation(descriptor, visible)
    }

    private fun extractCreates(signature: String): String {
        var start = signature.indexOf(DAGGER_FACTORY)
        require(start >= 0)
        start += DAGGER_FACTORY.length + 1
        var end = start
        var braces = 1
        while (braces > 0 && end < signature.length) {
            braces = when (signature[end]) {
                '<' -> braces + 1
                '>' -> braces - 1
                else -> braces
            }
            end++
        }
        require(end > start)
        return signature.substring(start, end - 1)
    }
}

private const val DAGGER_MODULE_DESCRIPTOR = "Ldagger/Module;"
private const val DAGGER_FACTORY = "dagger/internal/Factory"