package com.blockified;

import com.blockified.alariaejmc.block.ModBlocks;
import com.blockified.alariaejmc.block.ModMagnetarTicker;
import com.blockified.alariaejmc.effect.ModEffects;
import com.blockified.alariaejmc.item.ModEvents;
import com.blockified.alariaejmc.item.ModItemGroups;
import com.blockified.alariaejmc.item.ModItems;
import com.blockified.alariaejmc.world.ModWorldGen;
import net.fabricmc.api.ModInitializer;

public class Blockified implements ModInitializer {
	public static final String MOD_ID = "blockified";

	@Override
	public void onInitialize() {
		ModBlocks.registerModBlocks();
		ModEffects.registerModEffects();
		ModItems.registerModItems();
		ModItemGroups.registerModItemGroups();
		ModEvents.registerEvents();
		ModWorldGen.registerWorldGen();
		ModMagnetarTicker.registerTicking();
	}
}
