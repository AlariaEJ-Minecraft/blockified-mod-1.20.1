package com.blockified;

import com.blockified.alarctica.block.ModBlocks;
import com.blockified.alarctica.item.ModItemGroups;
import com.blockified.alarctica.item.ModItems;
import net.fabricmc.api.ModInitializer;

public class Blockified implements ModInitializer {
	public static final String MOD_ID = "blockified";

	@Override
	public void onInitialize() {
		ModBlocks.registerModBlocks();
		ModItems.registerModItems();
		ModItemGroups.registerModItemGroups();
	}
}
