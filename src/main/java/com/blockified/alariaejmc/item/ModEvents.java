package com.blockified.alariaejmc.item;

import com.blockified.alariaejmc.block.ModBlocks;
import com.blockified.alariaejmc.effect.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
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

		/*Scooping Oobleck with a plain bucket, the way water and lava work.
		  The filled bucket handles the other half itself.*/
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			ItemStack held = player.getStackInHand(hand);
			if (held.getItem() != Items.BUCKET) {
				return ActionResult.PASS;
			}

			BlockPos pos = hitResult.getBlockPos();
			if (world.getBlockState(pos).getBlock() != ModBlocks.Oobleck) {
				return ActionResult.PASS;
			}
			if (world.isClient) {
				return ActionResult.SUCCESS;
			}

			world.breakBlock(pos, false);
			world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);

			if (!player.getAbilities().creativeMode) {
				held.decrement(1);
				ItemStack filled = new ItemStack(ModItems.BucketOfOobleck);
				if (held.isEmpty()) {
					player.setStackInHand(hand, filled);
				} else if (!player.giveItemStack(filled)) {
					player.dropItem(filled, false);
				}
			}
			return ActionResult.SUCCESS;
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

		/*Magnetite Helmet: grants Phantom Protection while worn, which
		  cancels phantom attacks outright below.*/
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				boolean wearingHelmet = player.getEquippedStack(EquipmentSlot.HEAD).getItem() == ModItems.MagnetiteHelmet;
				if (wearingHelmet) {
					StatusEffectInstance current = player.getStatusEffect(ModEffects.PhantomProtection);
					if (current == null || current.getDuration() < 20) {
						player.addStatusEffect(new StatusEffectInstance(ModEffects.PhantomProtection, 220, 0, true, false));
					}
				} else if (player.hasStatusEffect(ModEffects.PhantomProtection)) {
					player.removeStatusEffect(ModEffects.PhantomProtection);
				}
			}
		});

		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
			if (entity instanceof PlayerEntity player
					&& isWearingFullTarchedSet(player)
					&& source.isIn(DamageTypeTags.IS_EXPLOSION)) {
				return false;
			}
			if (entity instanceof PlayerEntity player
					&& source.getAttacker() instanceof PhantomEntity
					&& player.hasStatusEffect(ModEffects.PhantomProtection)) {
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
