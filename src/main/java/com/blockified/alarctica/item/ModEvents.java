package com.blockified.alarctica.item;

import com.blockified.alarctica.block.ModBlocks;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;

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

		/*Full Tarched Armor Set bonus: fire resistance + explosion immunity.*/
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				if (!isWearingFullTarchedSet(player)) {
					continue;
				}
				StatusEffectInstance current = player.getStatusEffect(StatusEffects.FIRE_RESISTANCE);
				if (current == null || current.getDuration() < 20) {
					player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 220, 0, true, false));
				}
			}
		});

		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
			if (entity instanceof PlayerEntity player
					&& isWearingFullTarchedSet(player)
					&& source.isIn(DamageTypeTags.IS_EXPLOSION)) {
				return false;
			}
			return true;
		});
	}

	private static boolean isWearingFullTarchedSet(LivingEntity entity) {
		return entity.getEquippedStack(EquipmentSlot.HEAD).getItem() == ModItems.TarchedHelmet
				&& entity.getEquippedStack(EquipmentSlot.CHEST).getItem() == ModItems.TarchedChestplate
				&& entity.getEquippedStack(EquipmentSlot.LEGS).getItem() == ModItems.TarchedLeggings
				&& entity.getEquippedStack(EquipmentSlot.FEET).getItem() == ModItems.TarchedBoots;
	}
}
