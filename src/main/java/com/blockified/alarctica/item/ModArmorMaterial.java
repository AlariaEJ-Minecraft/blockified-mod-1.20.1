package com.blockified.alarctica.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class ModArmorMaterial {
	public static final ArmorMaterial Tarched = new ArmorMaterial() {
		@Override
		public int getDurability(ArmorItem.Type type) {
			switch (type) {
				case HELMET:
					return 220;
				case CHESTPLATE:
					return 320;
				case LEGGINGS:
					return 300;
				case BOOTS:
					return 260;
				default:
					return 220;
			}
		}

		@Override
		public int getProtection(ArmorItem.Type type) {
			switch (type) {
				case HELMET:
					return 2;
				case CHESTPLATE:
					return 7;
				case LEGGINGS:
					return 5;
				case BOOTS:
					return 2;
				default:
					return 0;
			}
		}

		@Override
		public int getEnchantability() {
			return 12;
		}

		@Override
		public SoundEvent getEquipSound() {
			return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
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
			return 1.0f;
		}

		@Override
		public float getKnockbackResistance() {
			return 0.0f;
		}
	};
}
