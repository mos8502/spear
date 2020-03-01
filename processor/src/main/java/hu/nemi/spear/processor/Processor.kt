package hu.nemi.spear.processor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import hu.nemi.spear.adapter.ModelAdapter
import javax.annotation.processing.*
import javax.annotation.processing.Processor
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes(CLASS_DAGGER_MODULE)
class Processor : AbstractProcessor() {
    private var environemnt: ProcessingEnvironment? = null
    private var daggerModule: TypeElement? = null

    override fun init(environemnt: ProcessingEnvironment?) {
        super.init(environemnt)
        this.environemnt = environemnt
        daggerModule = environemnt?.elementUtils?.getTypeElement(CLASS_DAGGER_MODULE)
    }

    override fun process(elements: Set<TypeElement>?, round: RoundEnvironment?): Boolean {
        val modules = round?.getElementsAnnotatedWith(daggerModule ?: return false)

        modules?.asSequence()
                ?.filter { it.kind == ElementKind.CLASS }
                ?.filter(Element::isObject)
                ?.mapNotNull { it as? TypeElement }
                ?.forEach(::buildModuleAdapter)

        return false
    }

    private fun buildModuleAdapter(element: TypeElement) {
        val adapterName = ClassName.bestGuess("${element.qualifiedName}Adapter")
        val modelName = ClassName.bestGuess(element.qualifiedName.toString())
        val modelAdapterName = ModelAdapter::class.asClassName()
        val adapterSpec = FileSpec.builder(adapterName.packageName, adapterName.simpleName)
                .addType(TypeSpec.objectBuilder(adapterName)
                        .addSuperinterface(modelAdapterName.parameterizedBy(modelName))
                        .addFunction(FunSpec.builder("replaceWith")
                                .addModifiers(KModifier.OVERRIDE)
                                .receiver(modelName)
                                .addParameter("with", modelName)
                                .returns(Unit::class)
                                .addStatement("throw java.lang.UnsupportedOperationException()")
                                .build())
                        .addFunction(FunSpec.builder("reset")
                                .addModifiers(KModifier.OVERRIDE)
                                .receiver(modelName)
                                .returns(Unit::class)
                                .addStatement("throw java.lang.UnsupportedOperationException()")
                                .build())
                        .build())
                .build()
        adapterSpec.writeTo(environemnt?.filer ?: return)
    }
}

private const val CLASS_DAGGER_MODULE = "dagger.Module"
