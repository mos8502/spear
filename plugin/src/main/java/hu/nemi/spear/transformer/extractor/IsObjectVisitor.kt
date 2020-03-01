package hu.nemi.spear.transformer.extractor

import kotlinx.metadata.ClassName
import kotlinx.metadata.Flag
import kotlinx.metadata.Flags
import kotlinx.metadata.KmClassVisitor

/**
 * Check whether the class being visited is and object and final
 */
class IsObjectVisitor : KmClassVisitor() {
    var isObject: Boolean = false
        private set

    override fun visit(flags: Flags, name: ClassName) {
        isObject = Flag.Class.IS_OBJECT(flags)
    }
}