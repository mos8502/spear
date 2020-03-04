package hu.nemi.spear.processor

import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror

data class FactoryDescriptor(val factory: DeclaredType, val creates: TypeMirror)