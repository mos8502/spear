package hu.nemi.spear.plugin

import com.android.build.gradle.BaseExtension
import hu.nemi.spear.transformer.DaggerTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

class Plugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.findByName("android")
        if (ext is BaseExtension) {
            ext.registerTransform(DaggerTransform(project))
            project.extensions.create("openObjects", Extension::class.java)
        } else {
            throw IllegalStateException("Can only be applied to android modules")
        }
    }
}