package com.blockified.alariaejmc.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

/**
 * Solid and walkable, slower than Soul Sand, with a shallow (a couple
 * pixels) sink on top instead of a full-height collision box.
 */
public class BogSurfaceBlock extends Block {
	private static final VoxelShape SHAPE = VoxelShapes.cuboid(0, 0, 0, 1, 0.875, 1);

	public BogSurfaceBlock(Settings settings) {
		super(settings);
	}

	@Override
	public float getVelocityMultiplier() {
		return 0.3f;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}
}
