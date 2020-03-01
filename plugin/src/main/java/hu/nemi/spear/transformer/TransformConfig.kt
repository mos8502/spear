package hu.nemi.spear.transformer

import com.android.build.api.transform.TransformInvocation
import java.io.File

data class TransformConfig(
    val invocation: TransformInvocation,
    val androidClasspath: List<File>
)