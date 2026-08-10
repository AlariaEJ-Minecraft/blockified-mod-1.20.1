package com.blockified.alariaejmc.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Quicksand: standing still sinks you, moving climbs you back out, and
 * rising faster than it sinks means keeping up the pace carries you
 * across a patch. Sideways movement is always heavily dampened (sticky),
 * and past half depth an extra Slowness stacks on top. LEVEL (world-gen
 * depth, 1-6) caps how far under a given spot you can go.
 *
 * The sink/rise rules themselves live in QuicksandMotion, shared with Bog
 * Block; only the rates below are Oobleck's own.
 *
 * Collision uses a shallow shape capped at 0.3 (like MudBogBlock) so the
 * entity's bounding box reliably overlaps it regardless of LEVEL, while
 * onEntityCollision fires every tick to drive the actual sink/rise.
 */
public class OobleckBlock extends Block {
	public static final IntProperty LEVEL = IntProperty.of("level", 1, 6);

	/*Rises faster than it sinks - keep moving and Oobleck lets you go.*/
	private static final QuicksandMotion.Tuning TUNING =
			new QuicksandMotion.Tuning(0.05, 0.08, 0.2f, 0.08f, 3);

	public OobleckBlock(Settings settings) {
		super(settings);
		setDefaultState(getStateManager().getDefaultState().with(LEVEL, 6));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LEVEL);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.cuboid(0, 0, 0, 1, state.get(LEVEL) / 6.0, 1);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.cuboid(0, 0, 0, 1, Math.min(0.3, state.get(LEVEL) / 6.0), 1);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		super.onEntityCollision(state, world, pos, entity);
		QuicksandMotion.apply(world, entity, pos.getY() + 1.0, state.get(LEVEL) / 6.0, TUNING);
	}
}
