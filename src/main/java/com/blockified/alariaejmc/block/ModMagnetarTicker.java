package com.blockified.alariaejmc.block;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.GlobalPos;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Drives Magnetar blocks without needing a BlockEntity: a short power-up
 * countdown, then upkeep of the beam projected from the block's front
 * face while it stays ON.
 *
 * The beam runs in a straight line - line of sight - and stops at the
 * first block that isn't air, so whatever it runs into is powered by the
 * final segment sitting against it. Segments are ordinary redstone
 * sources, so this works with every vanilla component; the old version
 * force-wrote RedstoneWireBlock.POWER over a radius, which only ever
 * moved wire and let that forced power travel back down the wire into
 * the Magnetar and latch it on.
 */
public class ModMagnetarTicker {
	private static final int POWER_UP_TICKS = 30;
	private static final int MAX_BEAM_LENGTH = 32;

	private static final Map<GlobalPos, Integer> poweringUp = new HashMap<>();
	private static final Set<GlobalPos> active = new HashSet<>();

	public static void startPoweringUp(net.minecraft.world.World world, BlockPos pos) {
		GlobalPos gp = toGlobalPos(world, pos);
		if (gp != null) {
			poweringUp.put(gp, POWER_UP_TICKS);
		}
	}

	public static void stop(net.minecraft.world.World world, BlockPos pos, Direction facing) {
		GlobalPos gp = toGlobalPos(world, pos);
		if (gp == null) {
			return;
		}
		poweringUp.remove(gp);
		active.remove(gp);
		if (world instanceof ServerWorld serverWorld) {
			clearBeam(serverWorld, pos, facing);
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

	/**
	 * Extends the beam forward over air, leaving anything else alone and
	 * stopping there. Segments already in place are stepped over, so this
	 * is safe to call every tick.
	 */
	private static void projectBeam(ServerWorld world, BlockPos origin, Direction facing) {
		BlockPos.Mutable cursor = origin.mutableCopy();
		for (int i = 0; i < MAX_BEAM_LENGTH; i++) {
			cursor.move(facing);
			BlockState state = world.getBlockState(cursor);

			if (state.getBlock() instanceof MagnetarBeamBlock && state.get(MagnetarBeamBlock.FACING) == facing) {
				continue;
			}
			if (state.isAir()) {
				world.setBlockState(cursor, ModBlocks.MagnetarBeam.getDefaultState()
						.with(MagnetarBeamBlock.FACING, facing), 3);
				continue;
			}
			return;
		}
	}

	private static void clearBeam(ServerWorld world, BlockPos origin, Direction facing) {
		BlockPos.Mutable cursor = origin.mutableCopy();
		for (int i = 0; i < MAX_BEAM_LENGTH; i++) {
			cursor.move(facing);
			BlockState state = world.getBlockState(cursor);
			if (!(state.getBlock() instanceof MagnetarBeamBlock) || state.get(MagnetarBeamBlock.FACING) != facing) {
				return;
			}
			world.removeBlock(cursor, false);
		}
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
					BlockState state = world.getBlockState(pos);
					world.setBlockState(pos, state.with(MagnetarBlock.STATE, MagnetarBlock.MagnetarState.ON));
					world.updateNeighborsAlways(pos, state.getBlock());
					active.add(entry.getKey());
					countdownIterator.remove();
				} else {
					entry.setValue(remaining);
				}
			}

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

				Direction facing = magnetarState.get(MagnetarBlock.FACING);

				/*Re-check the back face here as well as in neighborUpdate:
				  a source can stop powering us without ever notifying this
				  block, and going stale would leave the beam stuck on.*/
				if (!MagnetarBlock.isReceivingPowerFromBack(world, center, facing)) {
					activeIterator.remove();
					clearBeam(world, center, facing);
					world.setBlockState(center, magnetarState.with(MagnetarBlock.STATE, MagnetarBlock.MagnetarState.OFF));
					world.updateNeighborsAlways(center, magnetarState.getBlock());
					continue;
				}

				projectBeam(world, center, facing);
			}
		});
	}
}
