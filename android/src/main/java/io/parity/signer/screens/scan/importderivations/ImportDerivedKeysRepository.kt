package io.parity.signer.screens.scan.importderivations

import io.parity.signer.domain.storage.RepoResult
import io.parity.signer.domain.storage.SeedRepository
import io.parity.signer.domain.storage.mapError
import io.parity.signer.uniffi.DerivedKeyStatus
import io.parity.signer.uniffi.SeedKeysPreview
import io.parity.signer.uniffi.importDerivations
import io.parity.signer.uniffi.populateDerivationsHasPwd


class ImportDerivedKeysRepository(
	private val seedRepository: SeedRepository,
) {

	fun importDerivedKeys(seedKeysPreview: List<SeedKeysPreview>): RepoResult<Unit> {
		val newSeeds = seedKeysPreview.map {
			it.copy(derivedKeys = it.derivedKeys
				.filter { key -> key.status == DerivedKeyStatus.Importable })
		}
		return try {
			importDerivations(newSeeds)
			RepoResult.Success(Unit)
		} catch (e: java.lang.Exception) {
			RepoResult.Failure(e)
		}
	}

	suspend fun updateWithSeed(seedPreviews: List<SeedKeysPreview>): RepoResult<List<SeedKeysPreview>> {
		val seeds: Map<String, String> =
			seedRepository.getAllSeeds().mapError() ?: return RepoResult.Failure()
		return try {
			val filledSeedKeys = populateDerivationsHasPwd(seeds, seedPreviews)
			RepoResult.Success(filledSeedKeys)
		} catch (e: java.lang.Exception) {
			RepoResult.Failure(e)
		}
	}
}
