package hu.nemi.spear.transformer.extractor

import kotlinx.metadata.*

class ModuleAdapterVisitor : KmClassVisitor() {
    private val moduleNameVisitor = object : KmTypeVisitor() {
        override fun visitClass(name: ClassName) {
            moduleName = name
        }
    }

    var isModuleAdapter: Boolean = false
        private set

    var moduleName: String? = null
        private set

    override fun visitSupertype(flags: Flags): KmTypeVisitor? = object : KmTypeVisitor() {

        override fun visitClass(name: ClassName) {
            isModuleAdapter = isModuleAdapter || name == ADAPTER_CLASS_NAME
        }

        override fun visitArgument(flags: Flags, variance: KmVariance): KmTypeVisitor? =
            if (isModuleAdapter) moduleNameVisitor else null
    }
}

private const val ADAPTER_CLASS_NAME = "hu/nemi/spear/adapter/ModelAdapter"