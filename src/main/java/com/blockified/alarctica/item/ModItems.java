package com.blockified.alarctica.item;

import com.blockified.Blockified;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
	public static final Item TarIngot = registerItem("tar_ingot",
			new Item(new FabricItemSettings()));

	/**//**//**//**//**/
	/*Sword/Tools/Combat*/
	public static final Item SwordOfExperience = registerItem("sword_of_experience",
			new ExperienceSwordItem(ModToolMaterial.SwordOfExperience, 11, 1f, new FabricItemSettings(), 10, 15));

	public static final Item AxeOfExperience = registerItem("axe_of_experience",
			new ExperienceAxeItem(ModToolMaterial.AxeOfExperience, 12, 1f, new FabricItemSettings(), 5, 10));

	public static final Item IceTotemOfResistance = registerItem("ice_totem_of_resistance",
			new Item(new FabricItemSettings().maxCount(1)));

	/**//**//**//**//**/
	private static Item registerItem(String name, Item item) {
		return Registry.register(Registries.ITEM, new Identifier(Blockified.MOD_ID, name), item);
	}

	public static void registerModItems() {
	}
}
