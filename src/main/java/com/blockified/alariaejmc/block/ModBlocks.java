package com.blockified.alariaejmc.block;

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

	/*Placed/picked up via bucket (always LEVEL 6); also generates as
	  variable-depth (LEVEL 1-6) desert riverbank strips - see
	  OobleckStripFeature. Quicksand behavior lives in OobleckBlock.*/
	public static final Block Oobleck = registerBlock("oobleck",
			new OobleckBlock(FabricBlockSettings.copyOf(Blocks.MUD)));

	/*Soul Sand inspired bog trio; shovel is the effective tool
	  (see data/minecraft/tags/blocks/mineable/shovel.json).*/
	public static final Block ClayBog = registerBlock("clay_bog",
			new ClayBogBlock(FabricBlockSettings.copyOf(Blocks.SOUL_SAND)));

	public static final Block BogBlock = registerBlock("bog_block",
			new BogSurfaceBlock(FabricBlockSettings.copyOf(Blocks.SOUL_SAND)));

	public static final Block MudBog = registerBlock("mud_bog",
			new MudBogBlock(FabricBlockSettings.copyOf(Blocks.POWDER_SNOW)));

	/*Iron-tier ore; drops Raw Magnetite (see loot table), smeltable into
	  Magnetite Ingot. No world generation placement yet.*/
	public static final Block MagnetiteOreBlock = registerBlock("magnetite_ore_block",
			new Block(FabricBlockSettings.copyOf(Blocks.IRON_ORE)));

	/*Redstone: off/powering_up/on, toggled by any redstone signal (lever,
	  wire, etc.). See MagnetarBlock/ModMagnetarTicker for the radius
	  wireless-power behavior.*/
	public static final Block Magnetar = registerBlock("magnetar",
			new MagnetarBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_BLOCK)));

	/*Walk-through plane of a Lodestone Reach portal. No BlockItem - it's
	  created by lighting a Magnetar frame with a Magnetized Tarch, never
	  placed by hand. Unbreakable strength so it survives until the frame
	  goes.*/
	public static final Block LodestoneReachPortal = registerBlockWithoutItem("lodestone_reach_portal",
			new LodestoneReachPortalBlock(FabricBlockSettings.copyOf(Blocks.NETHER_PORTAL)
					.luminance(state -> 11).strength(-1.0f, 3600000.0f)));

	/*-----------*/
	private static Block registerBlock(String name, Block block) {
		registerBlockItem(name, block);
		return Registry.register(Registries.BLOCK, new Identifier(Blockified.MOD_ID, name), block);
	}

	private static Block registerBlockWithoutItem(String name, Block block) {
		return Registry.register(Registries.BLOCK, new Identifier(Blockified.MOD_ID, name), block);
	}

	private static void registerBlockItem(String name, Block block) {
		Registry.register(Registries.ITEM, new Identifier(Blockified.MOD_ID, name),
				new BlockItem(block, new FabricItemSettings()));
	}

	public static void registerModBlocks() {
	}
}
