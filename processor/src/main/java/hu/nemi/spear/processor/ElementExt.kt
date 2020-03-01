package hu.nemi.spear.processor

import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import javax.lang.model.element.Element

val Element.isObject: Boolean
    get() {
        val metaAnnotation = getAnnotation(Metadata::class.java) ?: return false
        val header = KotlinClassHeader(
            kind = metaAnnotation.kind,
            metadataVersion = metaAnnotation.metadataVersion,
            bytecodeVersion = metaAnnotation.bytecodeVersion,
            data1 = metaAnnotation.data1,
            data2 = metaAnnotation.data2,
            extraString = null,
            packageName = null,
            extraInt = null)
        val metadata = KotlinClassMetadata.read(header)
                as? KotlinClassMetadata.Class
            ?: return false

        val visitor = IsObjectVisitor()
        metadata.accept(visitor)

        return visitor.isObject
    }