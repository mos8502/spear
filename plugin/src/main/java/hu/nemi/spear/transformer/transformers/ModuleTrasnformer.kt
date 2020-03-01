package hu.nemi.spear.transformer.transformers

import org.codehaus.groovy.control.CompilerConfiguration
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*

class ModuleTrasnformer(classWriter: ClassWriter) :
    ClassVisitor(CompilerConfiguration.ASM_API_VERSION, classWriter) {

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        // remove final modifier
        super.visit(
            version,
            access and ACC_FINAL.inv(),
            name,
            signature,
            superName,
            interfaces
        )
    }

    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor? =
        if (name == "INSTANCE") {
            // remove final modifier from instance field
            super.visitField(access and ACC_FINAL.inv(), name, descriptor, signature, value)
        } else {
            super.visitField(access, name, descriptor, signature, value)
        }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor? =
        // remove final from methods
        super.visitMethod(
            access and ACC_FINAL.inv() and ACC_PRIVATE.inv() or ACC_PUBLIC,
            name,
            descriptor,
            signature,
            exceptions
        )
}