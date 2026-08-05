package com.blockified.alarctica.item;

import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class ModToolMaterial {
	public static final ToolMaterial SwordOfExperience = new ToolMaterial() {
		@Override
		public int getDurability() {
			return 1561;
		}

		@Override
		public float getMiningSpeedMultiplier() {
			return 8f;
		}

		@Override
		public float getAttackDamage() {
			return 3f;
		}

		@Override
		public int getMiningLevel() {
			return 3;
		}

		@Override
		public int getEnchantability() {
			return 10;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.ofItems(Items.DIAMOND);
		}
	};

	public static final ToolMaterial AxeOfExperience = new ToolMaterial() {
		@Override
		public int getDurability() {
			return 1561;
		}

		@Override
		public float getMiningSpeedMultiplier() {
			return 8f;
		}

		@Override
		public float getAttackDamage() {
			return 3f;
		}

		@Override
		public int getMiningLevel() {
			return 3;
		}

		@Override
		public int getEnchantability() {
			return 10;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.ofItems(Items.DIAMOND);
		}
	};
}
