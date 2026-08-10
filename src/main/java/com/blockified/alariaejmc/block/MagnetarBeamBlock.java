package com.blockified.alariaejmc.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
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
 * Invisible one-block node that a Magnetar parks against whatever its
 * line of sight lands on, so the link genuinely looks wireless - nothing
 * is drawn between the two.
 *
 * A node exists at all only because redstone power in Minecraft is
 * strictly local: something has to sit next to the target and emit, or
 * lamps, pistons, doors and repeaters simply never see a signal. It
 * emits weak power on every side exactly like a Block of Redstone, which
 * is what lets every vanilla component respond normally.
 *
 * FACING stores the direction the link travelled, so the node can look
 * back along that axis for its Magnetar and delete itself once that stops
 * checking out - covering chunk churn, world edits, or the block being
 * removed while the node was unloaded.
 */
public class MagnetarBeamBlock extends Block {
	public static final DirectionProperty FACING = Properties.FACING;

	private static final int MAX_LINK_LENGTH = 32;

	public MagnetarBeamBlock(Settings settings) {
		super(settings);
		setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	/*Drawn as nothing at all - the point of the rework. The blockstate and
	  model still exist so the game has a particle texture to fall back on.*/
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	/**
	 * Walks back along the link axis looking for the Magnetar that owns
	 * this node: everything in between has to be air (or another node),
	 * and the block found has to be an ON Magnetar pointing this way.
	 */
	public static boolean isSupported(BlockView world, BlockState state, BlockPos pos) {
		Direction facing = state.get(FACING);
		BlockPos.Mutable cursor = pos.mutableCopy();
		for (int i = 0; i < MAX_LINK_LENGTH; i++) {
			cursor.move(facing.getOpposite());
			BlockState behind = world.getBlockState(cursor);

			if (behind.getBlock() instanceof MagnetarBlock) {
				return behind.get(MagnetarBlock.LIT) && behind.get(MagnetarBlock.FACING) == facing;
			}
			if (behind.isAir() || behind.getBlock() instanceof MagnetarBeamBlock) {
				continue;
			}
			return false;
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
