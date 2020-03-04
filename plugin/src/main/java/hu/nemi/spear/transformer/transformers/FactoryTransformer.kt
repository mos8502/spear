package hu.nemi.spear.transformer.transformers

import org.codehaus.groovy.control.CompilerConfiguration
import org.objectweb.asm.*
import java.util.regex.Pattern

class FactoryTransformer(classWriter: ClassWriter, private val typeDescriptor: String) :
    ClassVisitor(CompilerConfiguration.ASM_API_VERSION, classWriter) {
    private val getDescriptor = "()$typeDescriptor"
    private val descriptorPattern = Pattern.compile("^L(.*);\$")
    private lateinit var className: String

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        className = requireNotNull(name)
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        if (name == "get" && descriptor == getDescriptor) {
            super.visitMethod(access, name, descriptor, signature, exceptions)
                .buildGetter()
            return super.visitMethod(access, "getOriginal", descriptor, signature, exceptions)
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }

    private fun MethodVisitor.buildGetter() {
        val packageName = className.substring(0, className.lastIndexOf('/'))
        val typeNameMatcher = descriptorPattern.matcher(typeDescriptor)
        require(typeNameMatcher.matches())
        val dependencyName = typeNameMatcher.group(1)
        val dependencySimpleName =
            dependencyName.substring(dependencyName.lastIndexOf('/') + 1, dependencyName.length)
        val adapterName = "$packageName/${dependencySimpleName}Adapter"
        val adapterDescriptor = "L$adapterName;"

        visitCode()
        val label0 = Label()
        visitLabel(label0)
        visitLineNumber(26, label0)
        visitFieldInsn(Opcodes.GETSTATIC, adapterName, "INSTANCE", adapterDescriptor)
        visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            adapterName,
            "getShadow",
            "()Ljava/lang/Object;",
            false
        )
        visitTypeInsn(Opcodes.CHECKCAST, dependencyName)
        visitVarInsn(Opcodes.ASTORE, 1)
        val label1 = Label()
        visitLabel(label1)
        visitLineNumber(27, label1)
        visitVarInsn(Opcodes.ALOAD, 1)
        val label2 = Label()
        visitJumpInsn(Opcodes.IFNULL, label2)
        val label3 = Label()
        visitLabel(label3)
        visitLineNumber(28, label3)
        visitVarInsn(Opcodes.ALOAD, 1)
        visitInsn(Opcodes.ARETURN)
        visitLabel(label2)
        visitLineNumber(30, label2)
        visitFrame(Opcodes.F_APPEND, 1, arrayOf<Any>(dependencyName), 0, null)
        visitVarInsn(Opcodes.ALOAD, 0)
        visitMethodInsn(Opcodes.INVOKEVIRTUAL, className, "getOriginal", "()$typeDescriptor", false)
        visitInsn(Opcodes.ARETURN)
        visitMaxs(1, 2)
        visitEnd()
    }
}