package hu.nemi.spear.transformer

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.variant.VariantInfo
import com.android.build.gradle.BaseExtension
import org.gradle.api.Project

class DaggerTransform(private val project: Project) : Transform() {
    // only interested in classes
    private val typeClasses = setOf(QualifiedContent.DefaultContentType.CLASSES)
    // only wish to transform classes in the app
    private val scopes = setOf(QualifiedContent.Scope.PROJECT)

    override fun getName(): String = DaggerTransform::class.java.simpleName

    override fun getInputTypes(): Set<QualifiedContent.ContentType> = typeClasses

    // can handle incremental builds for improved build performance
    override fun isIncremental(): Boolean = true

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> = scopes.toMutableSet()

    override fun transform(transformInvocation: TransformInvocation) {
        // to get configuration in the future
        val appExtension = project.extensions.findByName("android")
                as? BaseExtension
            ?: throw IllegalStateException("invalid android extensions")
        val config = TransformConfig(
            invocation = transformInvocation,
            androidClasspath = appExtension.bootClasspath
        )

        ClassTraversal(config).perform()
    }

    override fun applyToVariant(variant: VariantInfo): Boolean = variant.isDebuggable
}
