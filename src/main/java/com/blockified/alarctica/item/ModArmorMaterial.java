package com.blockified.alarctica.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class ModArmorMaterial {
	/*Positioned between Diamond (helmet 363 / chest 528 / legs 495 / boots 429
	  durability, toughness 2.0, kb resist 0.0, ench 10) and Netherite (407 /
	  592 / 555 / 481, toughness 3.0, kb resist 0.1, ench 15). Protection is
	  identical between those two tiers (3/8/6/3), so it stays there.*/
	public static final ArmorMaterial Tarched = new ArmorMaterial() {
		@Override
		public int getDurability(ArmorItem.Type type) {
			switch (type) {
				case HELMET:
					return 385;
				case CHESTPLATE:
					return 560;
				case LEGGINGS:
					return 525;
				case BOOTS:
					return 455;
				default:
					return 385;
			}
		}

		@Override
		public int getProtection(ArmorItem.Type type) {
			switch (type) {
				case HELMET:
					return 3;
				case CHESTPLATE:
					return 8;
				case LEGGINGS:
					return 6;
				case BOOTS:
					return 3;
				default:
					return 0;
			}
		}

		@Override
		public int getEnchantability() {
			return 13;
		}

		@Override
		public SoundEvent getEquipSound() {
			return SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.ofItems(ModItems.TarIngot);
		}

		@Override
		public String getName() {
			return "blockified:tarched";
		}

		@Override
		public float getToughness() {
			return 2.5f;
		}

		@Override
		public float getKnockbackResistance() {
			return 0.05f;
		}
	};
}
