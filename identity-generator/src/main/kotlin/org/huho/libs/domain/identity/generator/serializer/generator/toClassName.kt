package org.huho.libs.domain.identity.generator.serializer.generator

import com.squareup.kotlinpoet.ClassName
import org.huho.libs.domain.identity.generator.serializer.ClassInfo

fun ClassInfo.toClassName(): ClassName = ClassName(packagePath, className)
