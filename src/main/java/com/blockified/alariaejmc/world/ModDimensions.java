package com.blockified.alariaejmc.world;

import com.blockified.Blockified;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * The Lodestone Reach is fully data-driven (see
 * data/blockified/dimension[_type]/lodestone_reach.json and the matching
 * biome), so all the code needs is a key to look the world up by.
 */
public class ModDimensions {
	public static final RegistryKey<World> LODESTONE_REACH = RegistryKey.of(RegistryKeys.WORLD,
			new Identifier(Blockified.MOD_ID, "lodestone_reach"));
}
