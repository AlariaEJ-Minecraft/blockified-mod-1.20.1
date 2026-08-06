package com.blockified.alariaejmc.world.feature;

import com.blockified.alariaejmc.block.ModBlocks;
import com.blockified.alariaejmc.block.OobleckBlock;
import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

/**
 * Short strip of variable-depth Oobleck hugging a desert riverbank.
 * Aborts entirely unless the origin sits on sand/red sand with open water
 * somewhere in a short radius (the "near rivers" requirement), then lays
 * a random-length line along a random horizontal direction, re-finding the
 * surface at each step and assigning it a random LEVEL (1-6).
 */
public class OobleckStripFeature extends Feature<DefaultFeatureConfig> {
	private static final int WATER_SEARCH_RADIUS = 8;
	private static final int MIN_LENGTH = 4;
	private static final int MAX_LENGTH = 10;

	public OobleckStripFeature(Codec<DefaultFeatureConfig> configCodec) {
		super(configCodec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		StructureWorldAccess world = context.getWorld();
		BlockPos origin = context.getOrigin();
		Random random = context.getRandom();

		BlockPos surface = findSurface(world, origin);
		if (surface == null || !isDesertSurface(world, surface) || !hasNearbyWater(world, surface)) {
			return false;
		}

		Direction direction = Direction.Type.HORIZONTAL.random(random);
		int length = MIN_LENGTH + random.nextInt(MAX_LENGTH - MIN_LENGTH + 1);

		boolean placedAny = false;
		BlockPos.Mutable cursor = surface.mutableCopy();
		for (int i = 0; i < length; i++) {
			if (random.nextFloat() < 0.25f) {
				cursor.move(direction.rotateYClockwise());
			}
			cursor.move(direction);

			BlockPos stepSurface = findSurface(world, cursor.toImmutable());
			if (stepSurface == null || !isDesertSurface(world, stepSurface)) {
				continue;
			}

			int level = 1 + random.nextInt(6);
			world.setBlockState(stepSurface.up(),
					ModBlocks.Oobleck.getDefaultState().with(OobleckBlock.LEVEL, level), 3);
			placedAny = true;
		}

		return placedAny;
	}

	private static BlockPos findSurface(StructureWorldAccess world, BlockPos near) {
		BlockPos.Mutable pos = near.mutableCopy().move(0, 2, 0);
		int minY = world.getBottomY();
		while (pos.getY() > minY) {
			if (!world.getBlockState(pos).isAir() && world.getBlockState(pos).getFluidState().isEmpty()) {
				return pos.toImmutable();
			}
			pos.move(0, -1, 0);
		}
		return null;
	}

	private static boolean isDesertSurface(StructureWorldAccess world, BlockPos surface) {
		return world.getBlockState(surface).isOf(Blocks.SAND) || world.getBlockState(surface).isOf(Blocks.RED_SAND);
	}

	private static boolean hasNearbyWater(StructureWorldAccess world, BlockPos surface) {
		BlockPos.Mutable pos = new BlockPos.Mutable();
		for (int dx = -WATER_SEARCH_RADIUS; dx <= WATER_SEARCH_RADIUS; dx++) {
			for (int dz = -WATER_SEARCH_RADIUS; dz <= WATER_SEARCH_RADIUS; dz++) {
				for (int dy = -2; dy <= 2; dy++) {
					pos.set(surface.getX() + dx, surface.getY() + dy, surface.getZ() + dz);
					if (world.getBlockState(pos).getFluidState().isIn(FluidTags.WATER)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
