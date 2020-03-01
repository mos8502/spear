package hu.nemi.spear.transformer.extractor

import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import org.codehaus.groovy.control.CompilerConfiguration
import org.objectweb.asm.AnnotationVisitor

class KotlinMetadataVisitor : AnnotationVisitor(CompilerConfiguration.ASM_API_VERSION) {
    private var kind: Int? = null
    private var metadataVersion: IntArray? = null
    private var bytecodeVersion: IntArray? = null
    private var data1: Array<String>? = null
    private var data2: Array<String>? = null
    var metadata: KotlinClassMetadata? = null
        private set

    override fun visit(name: String?, value: Any?) {
        when (name) {
            NAME_KIND -> kind = value as? Int
            NAME_METADATA_VERSION -> metadataVersion = value as? IntArray
            NAME_BYTECODE_VERSION -> bytecodeVersion = value as? IntArray
            else -> super.visit(name, value)
        }
    }

    override fun visitArray(name: String?): AnnotationVisitor =
        when (name) {
            NAME_DATA_1 -> object : AnnotationVisitor(api) {
                override fun visit(name: String?, value: Any?) {
                    val stringValue = value as? String ?: return
                    data1 = data1?.plus(stringValue) ?: arrayOf(stringValue)
                }
            }
            NAME_DATA_2 -> object : AnnotationVisitor(api) {
                override fun visit(name: String?, value: Any?) {
                    val stringValue = value as? String ?: return
                    data2 = data2?.plus(stringValue) ?: arrayOf(stringValue)
                }
            }
            else -> super.visitArray(name)
        }

    override fun visitEnd() {
        super.visitEnd()
        val header = KotlinClassHeader(
            kind = kind,
            metadataVersion = metadataVersion,
            bytecodeVersion = bytecodeVersion,
            data1 = data1,
            data2 = data2,
            extraString = null,
            packageName = null,
            extraInt = null
        )
        metadata = KotlinClassMetadata.read(header)
    }
}

const val KOTLIN_METADATA_DESCRIPTOR = "Lkotlin/Metadata;"

private const val NAME_KIND = "k"
private const val NAME_METADATA_VERSION = "mv"
private const val NAME_BYTECODE_VERSION = "bv"
private const val NAME_DATA_1 = "d1"
private const val NAME_DATA_2 = "d2"
