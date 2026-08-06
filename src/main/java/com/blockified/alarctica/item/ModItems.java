package com.blockified.alarctica.item;

import com.blockified.Blockified;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.PickaxeItem;
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

	public static final Item TarchedPickaxe = registerItem("tarched_pickaxe",
			new PickaxeItem(ModToolMaterial.Tarched, 1, -2.8f, new FabricItemSettings()));

	public static final Item IceTotemOfResistance = registerItem("ice_totem_of_resistance",
			new Item(new FabricItemSettings().maxCount(1)));

	public static final Item TarchedCoal = registerItem("tarched_coal",
			new Item(new FabricItemSettings()));

	/*Tarched Armor Set*/
	public static final Item TarchedHelmet = registerItem("tarched_helmet",
			new ArmorItem(ModArmorMaterial.Tarched, ArmorItem.Type.HELMET, new FabricItemSettings()));

	public static final Item TarchedChestplate = registerItem("tarched_chestplate",
			new ArmorItem(ModArmorMaterial.Tarched, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));

	public static final Item TarchedLeggings = registerItem("tarched_leggings",
			new ArmorItem(ModArmorMaterial.Tarched, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));

	public static final Item TarchedBoots = registerItem("tarched_boots",
			new ArmorItem(ModArmorMaterial.Tarched, ArmorItem.Type.BOOTS, new FabricItemSettings()));

	/**//**//**//**//**/
	private static Item registerItem(String name, Item item) {
		return Registry.register(Registries.ITEM, new Identifier(Blockified.MOD_ID, name), item);
	}

	public static void registerModItems() {
	}
}
