package com.blockified.alariaejmc.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Off/powering-up/on states driven by incoming redstone power (lever,
 * button, wire, etc. - anything World#isReceivingRedstonePower picks up).
 * Directly-adjacent redstone (wire touching the block, or a component
 * reading power off it) is handled by ordinary vanilla propagation via
 * emitsRedstonePower/getStrongRedstonePower below. The genuinely custom
 * part - powering redstone wire within a radius with no physical wire
 * connection - lives in ModMagnetarTicker, which deliberately skips the
 * immediately-adjacent cells already covered by vanilla propagation (see
 * that class for why).
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

	public MagnetarBlock(Settings settings) {
		super(settings);
		setDefaultState(getStateManager().getDefaultState().with(STATE, MagnetarState.OFF));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(STATE);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
		if (world.isClient) {
			return;
		}

		boolean powered = world.isReceivingRedstonePower(pos);
		MagnetarState current = state.get(STATE);
		if (powered && current == MagnetarState.OFF) {
			world.setBlockState(pos, state.with(STATE, MagnetarState.POWERING_UP));
			ModMagnetarTicker.startPoweringUp(world, pos);
		} else if (!powered && current != MagnetarState.OFF) {
			world.setBlockState(pos, state.with(STATE, MagnetarState.OFF));
			ModMagnetarTicker.stop(world, pos);
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			ModMagnetarTicker.stop(world, pos);
		}
		super.onStateReplaced(state, world, pos, newState, moved);
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(STATE) == MagnetarState.ON ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return getWeakRedstonePower(state, world, pos, direction);
	}
}
