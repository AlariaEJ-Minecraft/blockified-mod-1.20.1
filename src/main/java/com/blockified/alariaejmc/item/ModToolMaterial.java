package com.blockified.alariaejmc.item;

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

	/*Positioned between Diamond (1561 dur / +3 dmg / level 3 / ench 10) and
	  Netherite (2031 / +4 / level 4 / ench 15), except mining speed, which
	  is held at Diamond's 8.0 deliberately.*/
	public static final ToolMaterial Tarched = new ToolMaterial() {
		@Override
		public int getDurability() {
			return 1800;
		}

		@Override
		public float getMiningSpeedMultiplier() {
			return 8.0f;
		}

		@Override
		public float getAttackDamage() {
			return 3.5f;
		}

		@Override
		public int getMiningLevel() {
			return 4;
		}

		@Override
		public int getEnchantability() {
			return 13;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.ofItems(ModItems.TarIngot);
		}
	};
}
