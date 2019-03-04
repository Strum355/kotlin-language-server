package org.javacs.kt.symbols

import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.name.FqName
//import org.javacs.kt.util.computeAsync
import org.javacs.kt.util.LRUCache

class GlobalSymbolsManager(
	private val module: ModuleDescriptor
) {
	private val excludedNames = setOf("META-INF")
	private val topLevelPackages = module.getSubPackagesOf(FqName.ROOT, { it.toString() !in excludedNames })
    val cache = LRUCache<String, DeclarationDescriptor>(200)

	fun deepQueryAllSymbols(): Collection<DeclarationDescriptor> =
			topLevelPackages.flatMap { deepQuerySymbolsIn(module.getPackage(it)) }
            .onEach { cache.put(it.name.toString(), it) }

	private fun deepQuerySymbolsIn(packageDescriptor: PackageViewDescriptor): Collection<DeclarationDescriptor> =
			packageDescriptor.memberScope
				.getContributedDescriptors()
				.filter { it.name.toString() != "package-info" }
				.flatMap {
					when (it) {
						is PackageViewDescriptor -> deepQuerySymbolsIn(it)
						else -> listOf(it)
					}
				}
}
