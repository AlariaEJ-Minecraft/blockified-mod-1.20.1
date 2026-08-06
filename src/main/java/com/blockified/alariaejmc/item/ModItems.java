package com.blockified.alariaejmc.item;

import com.blockified.Blockified;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.CompassItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
	public static final Item TarIngot = registerItem("tar_ingot",
			new Item(new FabricItemSettings()));

	/**//**//**//**//**/
	/*Sword/Tools/Combat*/
	public static final Item SwordOfExperience = registerItem("sword_of_experience",
			new ExperienceSwordItem(ModToolMaterial.SwordOfExperience, 13, 1f, new FabricItemSettings(), 10, 15));

	public static final Item AxeOfExperience = registerItem("axe_of_experience",
			new ExperienceAxeItem(ModToolMaterial.AxeOfExperience, 14, 1f, new FabricItemSettings(), 5, 10));

	/*Damage 17; swing speed -2.0 (heavier/slower than a vanilla diamond
	  sword's -2.4, fitting obsidian's weight); Netherite-tier material
	  since no durability/mining stats were specified.*/
	public static final Item CryingObsidianSword = registerItem("crying_obsidian_sword",
			new CryingObsidianSwordItem(ToolMaterials.NETHERITE, 17, -2.0f, new FabricItemSettings()));

	public static final Item TarchedPickaxe = registerItem("tarched_pickaxe",
			new SmeltingPickaxeItem(ModToolMaterial.Tarched, 1, -2.8f, new FabricItemSettings()));

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

	/*Oobleck*/
	public static final Item BucketOfOobleck = registerItem("bucket_of_oobleck",
			new BucketOfOobleckItem(new FabricItemSettings().maxCount(1)));

	public static final Item EmptyOobleckBucket = registerItem("empty_bucket_of_oobleck",
			new EmptyOobleckBucketItem(new FabricItemSettings().maxCount(16)));

	/*Magnetite: iron-tier armor stats. Helmet grants Phantom Protection
	  while worn - see ModEvents for the effect upkeep/removal and the
	  phantom-attack cancellation.*/
	public static final Item RawMagnetite = registerItem("raw_magnetite",
			new Item(new FabricItemSettings()));

	public static final Item MagnetiteIngot = registerItem("magnetite_ingot",
			new Item(new FabricItemSettings()));

	public static final Item MagnetiteHelmet = registerItem("magnetite_helmet",
			new ArmorItem(ArmorMaterials.IRON, ArmorItem.Type.HELMET, new FabricItemSettings()));

	/*Plain vanilla CompassItem so the client's existing lodestone-style
	  needle rendering/NBT contract works with zero custom code - see
	  ModMagnetiteCompassTicker, which keeps re-pointing it at the
	  nearest currently-ON Magnetar.*/
	public static final Item MagnetiteCompass = registerItem("magnetite_compass",
			new CompassItem(new FabricItemSettings()));

	/**//**//**//**//**/
	private static Item registerItem(String name, Item item) {
		return Registry.register(Registries.ITEM, new Identifier(Blockified.MOD_ID, name), item);
	}

	public static void registerModItems() {
	}
}
