package com.blockified.alariaejmc.world;

import com.blockified.alariaejmc.block.LodestoneReachPortalBlock;
import com.blockified.alariaejmc.block.MagnetarBlock;
import com.blockified.alariaejmc.block.ModBlocks;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

/**
 * Frame validation and travel for the Lodestone Reach portal: a flat 4x4
 * ring of Magnetar blocks (12 blocks, all FACING=UP so the front face
 * points at the sky) enclosing a 2x2 interior that the Magnetized Tarch
 * fills with portal blocks. The frame's redstone STATE is deliberately
 * not checked - FACING is what defines the pattern, so an unpowered
 * frame lights just the same.
 */
public class ModPortals {
	private static final int SIZE = 4;

	/**
	 * Finds the origin (lowest X/Z corner) of a valid frame the clicked
	 * block participates in, or null if it isn't part of one. The clicked
	 * block can sit at any of the 12 ring slots, so every ring offset is
	 * tried.
	 */
	public static BlockPos findFrameOrigin(World world, BlockPos clicked) {
		for (int dx = 0; dx < SIZE; dx++) {
			for (int dz = 0; dz < SIZE; dz++) {
				if (!isRingSlot(dx, dz)) {
					continue;
				}
				BlockPos origin = clicked.add(-dx, 0, -dz);
				if (isValidFrame(world, origin)) {
					return origin;
				}
			}
		}
		return null;
	}

	private static boolean isRingSlot(int x, int z) {
		return x == 0 || x == SIZE - 1 || z == 0 || z == SIZE - 1;
	}

	private static boolean isValidFrame(World world, BlockPos origin) {
		for (int x = 0; x < SIZE; x++) {
			for (int z = 0; z < SIZE; z++) {
				BlockPos pos = origin.add(x, 0, z);
				BlockState state = world.getBlockState(pos);
				if (isRingSlot(x, z)) {
					if (!(state.getBlock() instanceof MagnetarBlock)
							|| state.get(MagnetarBlock.FACING) != Direction.UP) {
						return false;
					}
				} else if (!state.isAir() && !(state.getBlock() instanceof LodestoneReachPortalBlock)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Fills the 2x2 interior of an already-validated frame with portal
	 * blocks. Returns false if it was already lit.
	 */
	public static boolean lightPortal(World world, BlockPos origin) {
		boolean litAny = false;
		for (int x = 1; x < SIZE - 1; x++) {
			for (int z = 1; z < SIZE - 1; z++) {
				BlockPos pos = origin.add(x, 0, z);
				if (world.getBlockState(pos).getBlock() instanceof LodestoneReachPortalBlock) {
					continue;
				}
				world.setBlockState(pos, ModBlocks.LodestoneReachPortal.getDefaultState(), 3);
				litAny = true;
			}
		}
		return litAny;
	}

	/**
	 * Sends an entity to the opposite side (Reach <-> Overworld), building
	 * a matching frame at the arrival site so the return trip works. The
	 * mapping is 1:1 on X/Z, so a round trip lands you back where you
	 * started.
	 */
	public static void travel(Entity entity, ServerWorld from, BlockPos portalPos) {
		MinecraftServer server = from.getServer();
		if (server == null) {
			return;
		}

		boolean inReach = from.getRegistryKey().equals(ModDimensions.LODESTONE_REACH);
		ServerWorld destination = server.getWorld(inReach ? World.OVERWORLD : ModDimensions.LODESTONE_REACH);
		if (destination == null) {
			return;
		}

		int y = MathHelper.clamp(portalPos.getY(),
				destination.getBottomY() + 8, destination.getTopY() - 8);
		BlockPos arrival = new BlockPos(portalPos.getX(), y, portalPos.getZ());
		buildArrivalFrame(destination, arrival);

		entity.resetPortalCooldown();
		FabricDimensions.teleport(entity, destination,
				new TeleportTarget(Vec3d.ofCenter(arrival), Vec3d.ZERO, entity.getYaw(), entity.getPitch()));
	}

	/**
	 * Carves out a pocket and lays a lit frame centered on the arrival
	 * spot, so the traveller always has somewhere solid to land and a way
	 * back - the Reach's floating-island terrain makes no guarantees about
	 * what's at any given coordinate.
	 */
	private static void buildArrivalFrame(ServerWorld world, BlockPos arrival) {
		BlockPos origin = arrival.add(-1, 0, -1);

		for (int x = 0; x < SIZE; x++) {
			for (int z = 0; z < SIZE; z++) {
				BlockPos pos = origin.add(x, 0, z);
				world.setBlockState(pos.down(), Blocks.OBSIDIAN.getDefaultState(), 3);

				if (isRingSlot(x, z)) {
					world.setBlockState(pos, ModBlocks.Magnetar.getDefaultState()
							.with(MagnetarBlock.FACING, Direction.UP), 3);
				} else {
					world.setBlockState(pos, ModBlocks.LodestoneReachPortal.getDefaultState(), 3);
				}

				/*Headroom so the arrival isn't inside terrain.*/
				for (int dy = 1; dy <= 3; dy++) {
					world.setBlockState(pos.up(dy), Blocks.AIR.getDefaultState(), 3);
				}
			}
		}
	}
}
