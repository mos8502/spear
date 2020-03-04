package hu.nemi.spear.processor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import hu.nemi.spear.adapter.ModelAdapter
import javax.annotation.Generated
import javax.annotation.processing.*
import javax.annotation.processing.Processor
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes(DAGGER_MODULE, JAVAX_GENERATED)
class Processor : AbstractProcessor() {
    private lateinit var environment: ProcessingEnvironment
    private lateinit var daggerModule: TypeElement

    override fun init(environemnt: ProcessingEnvironment) {
        super.init(environemnt)
        this.environment = environemnt
        daggerModule = environemnt.elementUtils.getTypeElement(DAGGER_MODULE)
    }

    override fun process(elements: Set<TypeElement>, round: RoundEnvironment): Boolean {
        round.getElementsAnnotatedWith(daggerModule)
            ?.asSequence()
            ?.filter { it.kind == ElementKind.CLASS }
            ?.filter(Element::isObject)
            ?.mapNotNull { it as? TypeElement }
            ?.forEach(::buildModuleAdapter)

        round.getElementsAnnotatedWith(Generated::class.java)
            ?.asSequence()
            ?.mapNotNull { it.asType().accept(GetFactory, environment) }
            ?.forEach(::buildDependencyAdapter)

        return false
    }

    private fun buildDependencyAdapter(descriptor: FactoryDescriptor) {
        val factory = environment.typeUtils.asElement(descriptor.factory) as TypeElement
        val creates = environment.typeUtils.asElement(descriptor.creates) as TypeElement

        val adapterName = "${creates.simpleName}Adapter"
        val packageName = factory.asClassName().packageName

        val superClass = ClassName.bestGuess("hu.nemi.spear.adapter.DependencyAdapter")
            .parameterizedBy(creates.asClassName())

        val adapterSpec = FileSpec.builder(packageName, adapterName)
            .addType(
                TypeSpec.objectBuilder(adapterName)
                    .superclass(superClass)
                    .build()
            )
            .build()

        adapterSpec.writeTo(environment.filer)
    }

    private fun buildModuleAdapter(element: TypeElement) {
        val adapterName = ClassName.bestGuess("${element.qualifiedName}Adapter")
        val modelName = ClassName.bestGuess(element.qualifiedName.toString())
        val modelAdapterName = ModelAdapter::class.asClassName()
        val adapterSpec = FileSpec.builder(adapterName.packageName, adapterName.simpleName)
            .addType(
                TypeSpec.objectBuilder(adapterName)
                    .addSuperinterface(modelAdapterName.parameterizedBy(modelName))
                    .addFunction(
                        FunSpec.builder("replaceWith")
                            .addModifiers(KModifier.OVERRIDE)
                            .receiver(modelName)
                            .addParameter("with", modelName)
                            .returns(Unit::class)
                            .addStatement("throw java.lang.UnsupportedOperationException()")
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("reset")
                            .addModifiers(KModifier.OVERRIDE)
                            .receiver(modelName)
                            .returns(Unit::class)
                            .addStatement("throw java.lang.UnsupportedOperationException()")
                            .build()
                    )
                    .build()
            )
            .build()
        adapterSpec.writeTo(environment.filer)
    }
}

private const val DAGGER_MODULE = "dagger.Module"
private const val JAVAX_GENERATED = "javax.annotation.Generated"
