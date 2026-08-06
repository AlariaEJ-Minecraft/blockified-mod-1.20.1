package com.blockified.alarctica.world;

import com.blockified.Blockified;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

/**
 * Hot Tar ore vein: overworld stone biomes, Y 15-20, matching the
 * configured/placed feature JSON under data/blockified/worldgen/.
 */
public class ModWorldGen {
	public static void registerWorldGen() {
		RegistryKey<PlacedFeature> hotTarOre = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
				new Identifier(Blockified.MOD_ID, "hot_tar_ore"));

		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
				GenerationStep.Feature.UNDERGROUND_ORES, hotTarOre);
	}
}
