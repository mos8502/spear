package hu.nemi.spear.transformer.transformers

import org.codehaus.groovy.control.CompilerConfiguration
import org.objectweb.asm.*

class ModuleAdapterTransformer(classWriter: ClassWriter, private val moduleName: String) :
    ClassVisitor(CompilerConfiguration.ASM_API_VERSION, classWriter) {
    private val moduleDescriptor = "L$moduleName;"

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor? {
        val visitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name == "reset" && descriptor == "($moduleDescriptor)V") {
            visitor.generateReset(moduleName)
        } else if (name == "replaceWith" && descriptor == "($moduleDescriptor$moduleDescriptor)V") {
            visitor.generateReplaceWith(moduleName)
        } else {
            return visitor
        }

        return null
    }

    private fun MethodVisitor.generateReset(moduleName: String) {
        visitCode()
        val label0 = Label()
        visitLabel(label0)
        visitLineNumber(17, label0)
        visitTypeInsn(
            Opcodes.NEW,
            moduleName
        )
        visitInsn(Opcodes.DUP)
        visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            moduleName,
            "<init>",
            "()V",
            false
        )
        visitFieldInsn(
            Opcodes.PUTSTATIC,
            moduleName,
            "INSTANCE",
            moduleDescriptor
        )
        val label1 = Label()
        visitLabel(label1)
        visitLineNumber(18, label1)
        visitInsn(Opcodes.RETURN)
        visitMaxs(2, 2)
        visitEnd()
    }

    private fun MethodVisitor.generateReplaceWith(moduleName: String) {
        visitCode()
        val label0 = Label()
        visitLabel(label0)
        visitLineNumber(11, label0)
        visitVarInsn(Opcodes.ALOAD, 1)
        visitVarInsn(Opcodes.ALOAD, 2)
        val label1 = Label()
        visitJumpInsn(Opcodes.IF_ACMPEQ, label1)
        val label2 = Label()
        visitLabel(label2)
        visitLineNumber(12, label2)
        visitVarInsn(Opcodes.ALOAD, 2)
        visitFieldInsn(
            Opcodes.PUTSTATIC,
            moduleName,
            "INSTANCE",
            moduleDescriptor
        )
        visitLabel(label1)
        visitLineNumber(14, label1)
        visitFrame(Opcodes.F_SAME, 0, null, 0, null)
        visitInsn(Opcodes.RETURN)
        visitMaxs(2, 3)
        visitEnd()
    }
}
