package com.blockified.alariaejmc.block;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Tracks active Magnetar blocks without needing a BlockEntity: a short
 * countdown (power-up animation) then, once ON, a periodic radius scan
 * that force-sets nearby redstone wire to full power - the "wireless"
 * part, since it ignores whether the wire is physically connected back
 * to the block. Deliberately skips the immediately-adjacent cells: wire
 * touching the Magnetar is already powered by ordinary vanilla
 * propagation (MagnetarBlock emits redstone power directly), and forcing
 * it here too would let that wire's forced power keep the Magnetar
 * "receiving power" forever, even after the original lever is flipped
 * off.
 */
public class ModMagnetarTicker {
	private static final int POWER_UP_TICKS = 30;
	private static final int RADIUS = 8;
	private static final int SCAN_INTERVAL_TICKS = 5;

	private static final Map<GlobalPos, Integer> poweringUp = new HashMap<>();
	private static final Set<GlobalPos> active = new HashSet<>();
	private static int scanCounter = 0;

	public static void startPoweringUp(net.minecraft.world.World world, BlockPos pos) {
		GlobalPos gp = toGlobalPos(world, pos);
		if (gp != null) {
			poweringUp.put(gp, POWER_UP_TICKS);
		}
	}

	public static void stop(net.minecraft.world.World world, BlockPos pos) {
		GlobalPos gp = toGlobalPos(world, pos);
		if (gp != null) {
			poweringUp.remove(gp);
			active.remove(gp);
		}
	}

	/*Read-only view for ModMagnetiteCompassTicker, which needs to find the
	  nearest ON Magnetar to point a held compass at.*/
	public static Set<GlobalPos> getActiveMagnetars() {
		return Collections.unmodifiableSet(active);
	}

	private static GlobalPos toGlobalPos(net.minecraft.world.World world, BlockPos pos) {
		if (!(world instanceof ServerWorld serverWorld)) {
			return null;
		}
		return GlobalPos.create(serverWorld.getRegistryKey(), pos.toImmutable());
	}

	public static void registerTicking() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			Iterator<Map.Entry<GlobalPos, Integer>> countdownIterator = poweringUp.entrySet().iterator();
			while (countdownIterator.hasNext()) {
				Map.Entry<GlobalPos, Integer> entry = countdownIterator.next();
				ServerWorld world = server.getWorld(entry.getKey().getDimension());
				BlockPos pos = entry.getKey().getPos();
				if (world == null || !(world.getBlockState(pos).getBlock() instanceof MagnetarBlock)) {
					countdownIterator.remove();
					continue;
				}

				int remaining = entry.getValue() - 1;
				if (remaining <= 0) {
					world.setBlockState(pos, world.getBlockState(pos).with(MagnetarBlock.STATE, MagnetarBlock.MagnetarState.ON));
					active.add(entry.getKey());
					countdownIterator.remove();
				} else {
					entry.setValue(remaining);
				}
			}

			scanCounter++;
			if (scanCounter < SCAN_INTERVAL_TICKS) {
				return;
			}
			scanCounter = 0;

			Iterator<GlobalPos> activeIterator = active.iterator();
			while (activeIterator.hasNext()) {
				GlobalPos gp = activeIterator.next();
				ServerWorld world = server.getWorld(gp.getDimension());
				BlockPos center = gp.getPos();
				if (world == null) {
					activeIterator.remove();
					continue;
				}

				BlockState magnetarState = world.getBlockState(center);
				if (!(magnetarState.getBlock() instanceof MagnetarBlock)
						|| magnetarState.get(MagnetarBlock.STATE) != MagnetarBlock.MagnetarState.ON) {
					activeIterator.remove();
					continue;
				}

				for (int dx = -RADIUS; dx <= RADIUS; dx++) {
					for (int dy = -RADIUS; dy <= RADIUS; dy++) {
						for (int dz = -RADIUS; dz <= RADIUS; dz++) {
							if (Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz))) <= 1) {
								continue;
							}
							BlockPos target = center.add(dx, dy, dz);
							BlockState targetState = world.getBlockState(target);
							if (targetState.getBlock() instanceof RedstoneWireBlock
									&& targetState.get(RedstoneWireBlock.POWER) < 15) {
								world.setBlockState(target, targetState.with(RedstoneWireBlock.POWER, 15), 3);
							}
						}
					}
				}
			}
		});
	}
}
