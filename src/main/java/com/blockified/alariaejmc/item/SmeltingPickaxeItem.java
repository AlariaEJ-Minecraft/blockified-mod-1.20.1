package com.blockified.alariaejmc.item;

import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;

/**
 * Marker subclass: blocks mined with this pickaxe drop their furnace-smelted
 * result instead of the raw block, when a smelting recipe exists. See
 * ModEvents for the actual block-break interception.
 */
public class SmeltingPickaxeItem extends PickaxeItem {
	public SmeltingPickaxeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
		super(material, attackDamage, attackSpeed, settings);
	}
}
