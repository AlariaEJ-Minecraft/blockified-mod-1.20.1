package com.blockified.alarctica.block;

import com.blockified.Blockified;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {
	public static final Block HotTar = registerBlock("hot_tar",
			new Block(FabricBlockSettings.copyOf(Blocks.MAGMA_BLOCK).sounds(BlockSoundGroup.BASALT)
					.resistance(40.5f)));

	public static final Block ResistantPackedIce = registerBlock("resistant_packed_ice",
			new Block(FabricBlockSettings.copyOf(Blocks.PACKED_ICE).resistance(1200f)));

	/*-----------*/
	private static Block registerBlock(String name, Block block) {
		registerBlockItem(name, block);
		return Registry.register(Registries.BLOCK, new Identifier(Blockified.MOD_ID, name), block);
	}

	private static void registerBlockItem(String name, Block block) {
		Registry.register(Registries.ITEM, new Identifier(Blockified.MOD_ID, name),
				new BlockItem(block, new FabricItemSettings()));
	}

	public static void registerModBlocks() {
	}
}
