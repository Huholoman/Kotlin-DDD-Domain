package org.huho.libs.domain.identity.generator

import io.github.classgraph.ClassGraph
import org.gradle.api.file.FileCollection
import org.huho.libs.messenger.cqrs.generator.processor.ClassProcessor
import java.net.URL
import java.net.URLClassLoader

class ClassScanner(
    runtimeClasspath: FileCollection,
    val packageToScan: String,
    val processor: ClassProcessor,
) {
    val classLoader: URLClassLoader
    val classGraph: ClassGraph

    init {
        val classpathUrls: Array<URL> = runtimeClasspath.files.map { it.toURI().toURL() }.toTypedArray()
        classLoader = URLClassLoader(classpathUrls, this.javaClass.classLoader)

        classGraph =
            ClassGraph()
                .overrideClassLoaders(classLoader)
                .acceptPackages(packageToScan)
                .enableClassInfo()
    }

    fun scan() {
        classGraph
            .scan()
            .allClasses
            .filter { classInfo ->
                !classInfo.simpleName.endsWith("KoinModuleKt", ignoreCase = true)
            }.forEach { classInfo ->
                try {
                    val cls = Class.forName(classInfo.name, true, classLoader).kotlin
                    if (classInfo.name.startsWith(packageToScan)) {
                        processor.handle(cls)
                    }
                } catch (e: ClassNotFoundException) {
                    println("Class not found during initial load: ${classInfo.name}, skipping. Error: ${e.message}")
                } catch (_: NoSuchMethodError) {
                    // skip, no idea what is the issue O_o
                } catch (e: Throwable) {
                    println("Error loading class during initial load: ${classInfo.simpleName}, error: ${e.message}")
                    e.printStackTrace(
                        System.out,
                    )
                }
            }
    }
}
