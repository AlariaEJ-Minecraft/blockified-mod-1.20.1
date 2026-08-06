package com.blockified.alariaejmc.world.feature;

import com.blockified.Blockified;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class ModFeatures {
	public static final Feature<DefaultFeatureConfig> OOBLECK_STRIP = registerFeature("oobleck_strip",
			new OobleckStripFeature(DefaultFeatureConfig.CODEC));

	private static Feature<DefaultFeatureConfig> registerFeature(String name, Feature<DefaultFeatureConfig> feature) {
		return Registry.register(Registries.FEATURE, new Identifier(Blockified.MOD_ID, name), feature);
	}

	public static void registerModFeatures() {
	}
}
