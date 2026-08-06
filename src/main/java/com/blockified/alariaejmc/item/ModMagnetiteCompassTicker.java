package com.blockified.alariaejmc.item;

import com.blockified.alariaejmc.block.ModMagnetarTicker;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Hand;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

/**
 * Keeps any held Magnetite Compass re-pointed at the nearest currently-ON
 * Magnetar in the same dimension, vanilla-lodestone style: writes the same
 * LodestonePos/LodestoneDimension/LodestoneTracked NBT a real lodestone
 * compass uses, so the client's existing needle rendering works with no
 * custom model/rendering code. Clears those tags (falls back to a plain
 * spinning compass) once nothing is active nearby.
 */
public class ModMagnetiteCompassTicker {
	private static final int UPDATE_INTERVAL_TICKS = 20;
	private static int tickCounter = 0;

	public static void registerTicking() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			tickCounter++;
			if (tickCounter < UPDATE_INTERVAL_TICKS) {
				return;
			}
			tickCounter = 0;

			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				updateIfHeld(player, player.getStackInHand(Hand.MAIN_HAND));
				updateIfHeld(player, player.getStackInHand(Hand.OFF_HAND));
			}
		});
	}

	private static void updateIfHeld(ServerPlayerEntity player, ItemStack stack) {
		if (stack.getItem() != ModItems.MagnetiteCompass) {
			return;
		}

		RegistryKey<World> dimension = player.getWorld().getRegistryKey();
		BlockPos from = player.getBlockPos();

		GlobalPos nearest = null;
		long nearestDistSq = Long.MAX_VALUE;
		for (GlobalPos candidate : ModMagnetarTicker.getActiveMagnetars()) {
			if (!candidate.getDimension().equals(dimension)) {
				continue;
			}
			BlockPos p = candidate.getPos();
			long dx = p.getX() - from.getX();
			long dy = p.getY() - from.getY();
			long dz = p.getZ() - from.getZ();
			long distSq = dx * dx + dy * dy + dz * dz;
			if (distSq < nearestDistSq) {
				nearestDistSq = distSq;
				nearest = candidate;
			}
		}

		NbtCompound nbt = stack.getOrCreateNbt();
		if (nearest == null) {
			nbt.remove("LodestonePos");
			nbt.remove("LodestoneDimension");
			nbt.remove("LodestoneTracked");
			return;
		}

		nbt.putBoolean("LodestoneTracked", true);
		nbt.putString("LodestoneDimension", nearest.getDimension().getValue().toString());
		nbt.put("LodestonePos", NbtHelper.fromBlockPos(nearest.getPos()));
	}
}
