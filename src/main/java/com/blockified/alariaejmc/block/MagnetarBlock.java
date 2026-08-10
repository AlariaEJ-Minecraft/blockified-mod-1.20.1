package com.blockified.alariaejmc.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * A plain lit/unlit redstone component, switching the moment its input
 * changes the way a Redstone Lamp does - no wind-up state, no cooldown.
 *
 * Output leaves the front face, where ModMagnetarTicker also projects an
 * invisible wireless link out to whatever the block is aimed at. The back
 * face is the input and never emits: it is the one face that must stay
 * silent, since powering it would feed this block's own signal straight
 * back into its trigger and latch it on for good.
 */
public class MagnetarBlock extends Block {
	public static final BooleanProperty LIT = Properties.LIT;
	public static final DirectionProperty FACING = Properties.FACING;

	public MagnetarBlock(Settings settings) {
		super(settings);
		setDefaultState(getStateManager().getDefaultState().with(LIT, false).with(FACING, Direction.NORTH));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LIT, FACING);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
	}

	/**
	 * Reads the back face only - the face opposite the front. Uses the
	 * same neighbour/direction pairing as World#isReceivingRedstonePower:
	 * for the neighbour at pos.offset(d), query it with d.
	 */
	public static boolean isReceivingPowerFromBack(World world, BlockPos pos, Direction front) {
		Direction back = front.getOpposite();
		return world.getEmittedRedstonePower(pos.offset(back), back) > 0;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
		if (world.isClient) {
			return;
		}

		Direction facing = state.get(FACING);
		boolean powered = isReceivingPowerFromBack(world, pos, facing);
		if (powered == state.get(LIT)) {
			return;
		}

		if (powered) {
			world.setBlockState(pos, state.with(LIT, true));
			ModMagnetarTicker.activate(world, pos);
		} else {
			ModMagnetarTicker.stop(world, pos, facing);
			world.setBlockState(pos, state.with(LIT, false));
		}
		world.updateNeighborsAlways(pos, this);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			ModMagnetarTicker.stop(world, pos, state.get(FACING));
			world.updateNeighborsAlways(pos, this);
		}
		super.onStateReplaced(state, world, pos, newState, moved);
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	/*"direction" points from the block being powered back towards this
	  one, so the neighbour sitting at our FACING side queries us with
	  FACING.getOpposite() - the same convention vanilla repeaters use.*/
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (!state.get(LIT)) {
			return 0;
		}
		return direction == state.get(FACING).getOpposite() ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return getWeakRedstonePower(state, world, pos, direction);
	}
}
