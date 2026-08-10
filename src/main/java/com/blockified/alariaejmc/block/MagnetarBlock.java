package com.blockified.alariaejmc.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Off/powering-up/on, toggled by any redstone signal arriving on one of
 * its five non-front faces. Once ON it behaves as an ordinary redstone
 * source out of its front face only, and ModMagnetarTicker projects a
 * beam of MagnetarBeamBlock straight ahead until the beam hits something.
 * Those beam blocks are themselves real redstone sources, which is what
 * makes the whole thing work with every vanilla component rather than
 * just wire.
 *
 * The front face is excluded from the input check on purpose: the first
 * beam block sits directly against it, so counting it as input would
 * latch the block on forever and it could never be switched off.
 */
public class MagnetarBlock extends Block {
	public enum MagnetarState implements StringIdentifiable {
		OFF("off"),
		POWERING_UP("powering_up"),
		ON("on");

		private final String name;

		MagnetarState(String name) {
			this.name = name;
		}

		@Override
		public String asString() {
			return name;
		}
	}

	public static final EnumProperty<MagnetarState> STATE = EnumProperty.of("magnetar_state", MagnetarState.class);
	public static final DirectionProperty FACING = Properties.FACING;

	public MagnetarBlock(Settings settings) {
		super(settings);
		setDefaultState(getStateManager().getDefaultState().with(STATE, MagnetarState.OFF).with(FACING, Direction.NORTH));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(STATE, FACING);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
	}

	/**
	 * Mirrors World#isReceivingRedstonePower but skips the front face.
	 * Same neighbour/direction pairing vanilla uses: for the neighbour at
	 * pos.offset(d), query it with d.
	 */
	public static boolean isReceivingPowerIgnoringFront(World world, BlockPos pos, Direction front) {
		for (Direction direction : Direction.values()) {
			if (direction == front) {
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
		boolean powered = isReceivingPowerIgnoringFront(world, pos, facing);
		MagnetarState current = state.get(STATE);
		if (powered && current == MagnetarState.OFF) {
			world.setBlockState(pos, state.with(STATE, MagnetarState.POWERING_UP));
			ModMagnetarTicker.startPoweringUp(world, pos);
		} else if (!powered && current != MagnetarState.OFF) {
			ModMagnetarTicker.stop(world, pos, facing);
			world.setBlockState(pos, state.with(STATE, MagnetarState.OFF));
			world.updateNeighborsAlways(pos, this);
		}
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

	/*Front face only. "direction" points from the block being powered
	  back towards this one, so the neighbour sitting at our FACING side
	  queries us with FACING.getOpposite() - same convention vanilla
	  repeaters use.*/
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (state.get(STATE) != MagnetarState.ON) {
			return 0;
		}
		return direction == state.get(FACING).getOpposite() ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return getWeakRedstonePower(state, world, pos, direction);
	}
}
