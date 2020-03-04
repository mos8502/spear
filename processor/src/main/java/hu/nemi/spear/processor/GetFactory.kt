package hu.nemi.spear.processor

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.util.SimpleTypeVisitor7

object GetFactory : SimpleTypeVisitor7<FactoryDescriptor?, ProcessingEnvironment>() {

    override fun visitDeclared(
        declaredType: DeclaredType,
        environment: ProcessingEnvironment
    ): FactoryDescriptor? {
        val element = environment.typeUtils.asElement(declaredType)
        require(element is TypeElement) { "expected TypeElement" }

        val factory = element.interfaces.asSequence()
            .firstOrNull {
                val typeElement = environment.typeUtils.asElement(it) as TypeElement
                typeElement.qualifiedName.toString() == "dagger.internal.Factory"
            } ?: return null

        require(factory is DeclaredType) { "expected DeclaredType"}

        return FactoryDescriptor(
            declaredType,
            factory.typeArguments.first()
        )
    }
}