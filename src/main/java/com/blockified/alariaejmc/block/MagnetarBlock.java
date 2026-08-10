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
 * changes the way a Redstone Lamp does - no wind-up, no cooldown.
 *
 * Five faces in, one out. Back, left, right, top and bottom all take
 * signal, and any one of them is enough on its own - they are OR'd, so
 * nothing depends on a particular face being wired. The front is the sole
 * output: it emits, it switches off a redstone torch attached to it, and
 * it projects the invisible wireless link that ModMagnetarTicker
 * maintains.
 *
 * No face is ever both, and that is what keeps the block switchable - see
 * isInputFace. The link's node sits against the front for the same
 * reason.
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
	 * Every face except the front takes input; the front alone emits.
	 *
	 * The split has to be clean like this. A face that both emitted and
	 * read would drive 15 into whatever is attached, read that straight
	 * back as its own input, and stay lit forever with the real signal cut
	 * - the latch that made this block impossible to switch off before.
	 */
	public static boolean isInputFace(Direction facing, Direction face) {
		return face != facing;
	}

	/**
	 * True when any single input face carries a signal - they are OR'd, so
	 * no one face is required. Uses the same neighbour/direction pairing as
	 * World#isReceivingRedstonePower: for the neighbour at pos.offset(d),
	 * query it with d.
	 */
	public static boolean isReceivingPowerFromInputs(World world, BlockPos pos, Direction facing) {
		for (Direction direction : Direction.values()) {
			if (!isInputFace(facing, direction)) {
				continue;
			}
			if (world.getEmittedRedstonePower(pos.offset(direction), direction) > 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
		if (world.isClient) {
			return;
		}

		Direction facing = state.get(FACING);
		boolean powered = isReceivingPowerFromInputs(world, pos, facing);
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
	  one, so the face of ours touching it is direction.getOpposite() -
	  the same convention vanilla repeaters use.*/
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (!state.get(LIT)) {
			return 0;
		}
		return isInputFace(state.get(FACING), direction.getOpposite()) ? 0 : 15;
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return getWeakRedstonePower(state, world, pos, direction);
	}
}
