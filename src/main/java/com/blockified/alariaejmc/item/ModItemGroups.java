package com.blockified.alariaejmc.item;

import com.blockified.Blockified;
import com.blockified.alariaejmc.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
	public static final ItemGroup BLOCKIFIED_GROUP = Registry.register(Registries.ITEM_GROUP,
			new Identifier(Blockified.MOD_ID, "blockified"),
			FabricItemGroup.builder()
					.icon(() -> new ItemStack(ModItems.TarIngot))
					.displayName(Text.translatable("itemgroup.blockified"))
					.entries((context, entries) -> {
						/*Ingots\/*/
						entries.add(ModItems.TarIngot);
						entries.add(ModItems.TarchedCoal);
						entries.add(ModItems.RawMagnetite);
						entries.add(ModItems.MagnetiteIngot);
						/*Blocks\/*/
						entries.add(ModBlocks.HotTar);
						entries.add(ModBlocks.MagnetiteOreBlock);
						entries.add(ModBlocks.BlackIce);
						entries.add(ModBlocks.CondensedIce);
						entries.add(ModBlocks.ColdIce);
						entries.add(ModBlocks.HardDenseIce);
						entries.add(ModBlocks.Oobleck);
						entries.add(ModItems.BucketOfOobleck);
						entries.add(ModBlocks.ClayBog);
						entries.add(ModBlocks.BogBlock);
						entries.add(ModBlocks.MudBog);
						entries.add(ModBlocks.Magnetar);
						/*Tools & Weapons \/*/
						entries.add(ModItems.SwordOfExperience);
						entries.add(ModItems.AxeOfExperience);
						entries.add(ModItems.CryingObsidianSword);
						entries.add(ModItems.TarchedPickaxe);
						entries.add(ModItems.IceTotemOfResistance);
						/*Armor\/*/
						entries.add(ModItems.TarchedHelmet);
						entries.add(ModItems.TarchedChestplate);
						entries.add(ModItems.TarchedLeggings);
						entries.add(ModItems.TarchedBoots);
						entries.add(ModItems.MagnetiteHelmet);
						entries.add(ModItems.MagnetiteCompass);
						entries.add(ModItems.MagnetizedTarch);
					})
					.build());

	public static void registerModItemGroups() {
	}
}
