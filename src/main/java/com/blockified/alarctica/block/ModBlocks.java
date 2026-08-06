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

	/*Ice variants: slipperiness and standstill push both rank
	  Hard Dense Ice > Cold Ice > Condensed Ice > Black Ice*/
	public static final Block BlackIce = registerBlock("black_ice",
			new PushingIceBlock(FabricBlockSettings.copyOf(Blocks.PACKED_ICE).slipperiness(0.96f), 0.0));

	public static final Block CondensedIce = registerBlock("condensed_ice",
			new PushingIceBlock(FabricBlockSettings.copyOf(Blocks.PACKED_ICE).slipperiness(0.975f), 0.008));

	public static final Block ColdIce = registerBlock("cold_ice",
			new PushingIceBlock(FabricBlockSettings.copyOf(Blocks.PACKED_ICE).slipperiness(0.985f), 0.016));

	public static final Block HardDenseIce = registerBlock("hard_dense_ice",
			new PushingIceBlock(FabricBlockSettings.copyOf(Blocks.PACKED_ICE).slipperiness(0.994f), 0.028));

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
