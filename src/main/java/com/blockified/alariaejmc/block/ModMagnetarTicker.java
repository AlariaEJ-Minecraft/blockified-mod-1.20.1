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
import java.util.Objects;
import java.util.Set;

/**
 * Keeps each lit Magnetar's wireless link up to date, without needing a
 * BlockEntity. Switching is immediate - the block itself handles that in
 * neighborUpdate - so all this does is maintain the link.
 *
 * The link is a line of sight - it runs straight ahead through air until
 * it meets a block - but nothing is drawn along the way. All that gets
 * placed is a single invisible node in the last air cell, resting against
 * whatever was hit, because redstone power is strictly local and a real
 * emitter has to be adjacent for lamps, pistons or doors to react. Fewer
 * than two blocks apart needs no node at all: the Magnetar's own front
 * face already reaches.
 */
public class ModMagnetarTicker {
	private static final int MAX_LINK_LENGTH = 32;

	private static final Set<GlobalPos> active = new HashSet<>();
	private static final Map<GlobalPos, BlockPos> nodes = new HashMap<>();

	public static void activate(net.minecraft.world.World world, BlockPos pos) {
		GlobalPos gp = toGlobalPos(world, pos);
		if (gp != null) {
			active.add(gp);
		}
	}

	public static void stop(net.minecraft.world.World world, BlockPos pos, Direction facing) {
		GlobalPos gp = toGlobalPos(world, pos);
		if (gp == null) {
			return;
		}
		active.remove(gp);
		if (world instanceof ServerWorld serverWorld) {
			clearNode(serverWorld, gp);
		}
		nodes.remove(gp);
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
	 * Traces the line of sight and returns the cell the node belongs in -
	 * the last air block before whatever the link runs into. Returns null
	 * when there is nothing to power: either the target is already touching
	 * the front face, or the line runs out to max range without hitting
	 * anything.
	 */
	private static BlockPos findNodePos(ServerWorld world, BlockPos origin, Direction facing) {
		BlockPos.Mutable cursor = origin.mutableCopy();
		BlockPos lastPassable = null;

		for (int i = 0; i < MAX_LINK_LENGTH; i++) {
			cursor.move(facing);
			BlockState state = world.getBlockState(cursor);

			if (state.isAir() || state.getBlock() instanceof MagnetarBeamBlock) {
				lastPassable = cursor.toImmutable();
				continue;
			}
			return lastPassable;
		}
		return null;
	}

	private static void clearNode(ServerWorld world, GlobalPos owner) {
		BlockPos node = nodes.get(owner);
		if (node == null) {
			return;
		}
		if (world.getBlockState(node).getBlock() instanceof MagnetarBeamBlock) {
			world.removeBlock(node, false);
		}
	}

	private static void updateNode(ServerWorld world, GlobalPos owner, BlockPos desired, Direction facing) {
		BlockPos current = nodes.get(owner);
		if (Objects.equals(current, desired)) {
			/*Re-place it if something removed the node behind our back.*/
			if (desired != null && !(world.getBlockState(desired).getBlock() instanceof MagnetarBeamBlock)
					&& world.getBlockState(desired).isAir()) {
				world.setBlockState(desired, ModBlocks.MagnetarBeam.getDefaultState()
						.with(MagnetarBeamBlock.FACING, facing), 3);
			}
			return;
		}

		clearNode(world, owner);
		if (desired == null) {
			nodes.remove(owner);
			return;
		}

		world.setBlockState(desired, ModBlocks.MagnetarBeam.getDefaultState()
				.with(MagnetarBeamBlock.FACING, facing), 3);
		nodes.put(owner, desired);
	}

	public static void registerTicking() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
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
						|| !magnetarState.get(MagnetarBlock.LIT)) {
					activeIterator.remove();
					clearNode(world, gp);
					nodes.remove(gp);
					continue;
				}

				Direction facing = magnetarState.get(MagnetarBlock.FACING);

				/*Re-check the back face here as well as in neighborUpdate:
				  a source can stop powering us without ever notifying this
				  block, and going stale would leave the link stuck on.*/
				if (!MagnetarBlock.isReceivingPowerFromBack(world, center, facing)) {
					activeIterator.remove();
					clearNode(world, gp);
					nodes.remove(gp);
					world.setBlockState(center, magnetarState.with(MagnetarBlock.LIT, false));
					world.updateNeighborsAlways(center, magnetarState.getBlock());
					continue;
				}

				updateNode(world, gp, findNodePos(world, center, facing), facing);
			}
		});
	}
}
