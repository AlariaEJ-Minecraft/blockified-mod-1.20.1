package com.blockified.alariaejmc.block;

import com.blockified.Blockified;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {
	/*Burns and ignites anything standing on it - see HotTarBlock.*/
	public static final Block HotTar = registerBlock("hot_tar",
			new HotTarBlock(FabricBlockSettings.copyOf(Blocks.MAGMA_BLOCK).sounds(BlockSoundGroup.BASALT)
					.resistance(40.5f)));

	/*Ice variants: slipperiness and standstill push both rank
	  Hard Dense Ice > Cold Ice > Condensed Ice > Black Ice. All four carry
	  bedrock-tier blast resistance so no explosion can break them.*/
	public static final Block BlackIce = registerBlock("black_ice",
			new PushingIceBlock(FabricBlockSettings.copyOf(Blocks.PACKED_ICE)
					.slipperiness(0.96f).resistance(3600000.0f), 0.0));

	public static final Block CondensedIce = registerBlock("condensed_ice",
			new PushingIceBlock(FabricBlockSettings.copyOf(Blocks.PACKED_ICE)
					.slipperiness(0.975f).resistance(3600000.0f), 0.008));

	public static final Block ColdIce = registerBlock("cold_ice",
			new PushingIceBlock(FabricBlockSettings.copyOf(Blocks.PACKED_ICE)
					.slipperiness(0.985f).resistance(3600000.0f), 0.016));

	public static final Block HardDenseIce = registerBlock("hard_dense_ice",
			new PushingIceBlock(FabricBlockSettings.copyOf(Blocks.PACKED_ICE)
					.slipperiness(0.994f).resistance(3600000.0f), 0.028));

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

	/*Redstone: takes signal on any of its five non-front faces and emits
	  from the front alone, which also projects the wireless link. See
	  MagnetarBlock/MagnetarBeamBlock/ModMagnetarTicker.*/
	public static final Block Magnetar = registerBlock("magnetar",
			new MagnetarBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_BLOCK)));

	/*Invisible emitter node a Magnetar parks against its target so the
	  link reads as wireless. No BlockItem - only a Magnetar creates these.
	  Walk through, replaceable so building over it just moves the link,
	  and destroyed rather than shoved when a piston hits it. No luminance
	  on purpose: light with no visible source looks like a bug.*/
	public static final Block MagnetarBeam = registerBlockWithoutItem("magnetar_beam",
			new MagnetarBeamBlock(FabricBlockSettings.create()
					.noCollision()
					.replaceable()
					.dropsNothing()
					.nonOpaque()
					.strength(-1.0f, 3600000.0f)
					.pistonBehavior(PistonBehavior.DESTROY)));


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
