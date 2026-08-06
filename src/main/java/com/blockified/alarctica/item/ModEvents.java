package com.blockified.alarctica.item;

import com.blockified.alarctica.block.ModBlocks;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.Block;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;

import java.util.Optional;

public class ModEvents {
	/*Lava Bucket burns for 20000 ticks.*/
	private static final int LAVA_BUCKET_BURN_TIME = 20000;

	public static void registerEvents() {
		FuelRegistry.INSTANCE.add(ModItems.TarchedCoal, 19000);
		FuelRegistry.INSTANCE.add(ModBlocks.HotTar, (int) (LAVA_BUCKET_BURN_TIME * 1.2f));

		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
			ItemStack held = player.getMainHandStack();
			if (world.isClient || !(held.getItem() instanceof SmeltingPickaxeItem)) {
				return true;
			}

			Optional<SmeltingRecipe> recipe = world.getRecipeManager()
					.getFirstMatch(RecipeType.SMELTING, new SimpleInventory(new ItemStack(state.getBlock())), world);
			if (recipe.isEmpty()) {
				return true;
			}

			ItemStack output = recipe.get().getOutput(world.getRegistryManager()).copy();
			world.breakBlock(pos, false);
			Block.dropStack(world, pos, output);
			held.damage(1, player, p -> p.sendToolBreakStatus(player.getActiveHand()));
			return false;
		});
	}
}
