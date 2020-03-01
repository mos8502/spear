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

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        this.name = name
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
}

private const val DAGGER_MODULE_DESCRIPTOR = "Ldagger/Module;"