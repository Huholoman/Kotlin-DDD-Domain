package org.huho.libs.domain.identity.generator.plugin

/**
 * @property packageName Project root package
 * @property packageToScan if set it is used to scan for annotations instead of packageName
 */
open class CQRSGeneratorConfiguration(
//    var packageName: String,
    var packageToScan: String? = null,
)
