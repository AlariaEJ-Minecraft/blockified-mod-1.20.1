package com.blockified.alariaejmc.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

/**
 * One segment of a Magnetar's beam. Emits weak redstone power on every
 * side exactly like a Block of Redstone, so anything vanilla can be
 * driven by it - lamps, pistons, doors, repeaters, comparators, wire,
 * dispensers - rather than only redstone wire the way the old radius
 * scan managed.
 *
 * Segments verify the block behind them is either the emitting Magnetar
 * or another segment pointing the same way, and delete themselves when
 * that stops being true. That makes the beam shorten by itself when
 * something is placed in its path and clean up if its Magnetar vanishes
 * without warning (chunk churn, world edits, a piston shoving it).
 */
public class MagnetarBeamBlock extends Block {
	public static final DirectionProperty FACING = Properties.FACING;

	public MagnetarBeamBlock(Settings settings) {
		super(settings);
		setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	public static boolean isSupported(BlockView world, BlockState state, BlockPos pos) {
		Direction facing = state.get(FACING);
		BlockState behind = world.getBlockState(pos.offset(facing.getOpposite()));
		if (behind.getBlock() instanceof MagnetarBlock) {
			return behind.get(MagnetarBlock.STATE) == MagnetarBlock.MagnetarState.ON
					&& behind.get(MagnetarBlock.FACING) == facing;
		}
		if (behind.getBlock() instanceof MagnetarBeamBlock) {
			return behind.get(FACING) == facing;
		}
		return false;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
			WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		world.scheduleBlockTick(pos, this, 1);
		return state;
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!isSupported(world, state, pos)) {
			world.removeBlock(pos, false);
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			world.updateNeighborsAlways(pos, this);
		}
		super.onStateReplaced(state, world, pos, newState, moved);
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	/*Weak power on all sides, matching Block of Redstone. Weak is what
	  drives lamps/doors/pistons/wire directly; leaving strong power at 0
	  keeps it from pushing a signal through solid blocks, which is the
	  behaviour players already expect from a redstone block.*/
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return 15;
	}
}
