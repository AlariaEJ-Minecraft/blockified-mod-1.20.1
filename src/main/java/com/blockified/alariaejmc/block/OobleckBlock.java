package com.blockified.alariaejmc.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Quicksand: standing still sinks you (SINK_RATE), while moving pulls you
 * back up (RISE_RATE), so crossing a patch means keeping going. Struggling
 * upward counts too - jumping and swimming climb out just like walking
 * does. Sideways movement is always heavily dampened (sticky), and once
 * past half of a spot's max depth an extra Slowness stacks on top, so
 * anything properly sunk has to fight both. LEVEL (world-gen depth, 1-6)
 * caps how far under a given spot an idle entity can go.
 *
 * Collision uses a shallow shape capped at 0.3 (like MudBogBlock) so the
 * entity's bounding box reliably overlaps it regardless of LEVEL, while
 * onEntityCollision fires every tick to drive the actual sink/rise.
 */
public class OobleckBlock extends Block {
	public static final IntProperty LEVEL = IntProperty.of("level", 1, 6);

	private static final double SINK_RATE = 0.05;
	private static final double RISE_RATE = 0.08;
	private static final double MOVING_THRESHOLD = 0.02;
	private static final float STICKY_MULTIPLIER = 0.2f;
	private static final float SUNK_MULTIPLIER = 0.08f;

	private static final int FORGET_AFTER_TICKS = 200;
	private static final int PRUNE_WHEN_LARGER_THAN = 128;

	private static final class Sinking {
		double depth;
		long lastTick;
	}

	private static final Map<UUID, Sinking> sinking = new HashMap<>();

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
		if (world.isClient || !(entity instanceof LivingEntity living)) {
			return;
		}

		long now = world.getTime();
		UUID id = living.getUuid();
		Sinking sink = sinking.get(id);

		/*This fires once per Oobleck block the entity overlaps, so standing
		  across a boundary would otherwise sink and dampen two to four times
		  in a single tick. Only the first block each tick gets to act.*/
		if (sink != null && sink.lastTick == now) {
			return;
		}
		if (sink == null) {
			sink = new Sinking();
			sinking.put(id, sink);
			prune(now);
		}
		sink.lastTick = now;

		Vec3d velocity = living.getVelocity();
		/*Upward motion counts as struggling free, not just horizontal. A
		  jump is nearly all vertical, so measuring width alone scored every
		  airborne tick as standing still and drove the player deeper.*/
		boolean climbing = velocity.horizontalLength() > MOVING_THRESHOLD
				|| velocity.y > MOVING_THRESHOLD;

		double maxDepth = state.get(LEVEL) / 6.0;
		sink.depth = climbing
				? Math.max(0.0, sink.depth - RISE_RATE)
				: Math.min(maxDepth, sink.depth + SINK_RATE);

		/*Deliberately kept at depth 0 rather than dropped: removing it here
		  would let another overlapping block re-process this same tick.
		  prune() clears it once the entity has been gone a while.*/
		double top = pos.getY() + 1.0 - sink.depth;
		if (living.getY() < top) {
			living.setPosition(living.getX(), top, living.getZ());
		}

		boolean sunk = sink.depth > maxDepth * 0.5;
		float multiplier = sunk ? SUNK_MULTIPLIER : STICKY_MULTIPLIER;
		living.setVelocity(velocity.multiply(multiplier, velocity.y < 0 ? 0.7 : 1.0, multiplier));

		if (sunk) {
			living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 3, true, false));
		}
	}

	/*Entities that wander off while still sunk would otherwise sit in the
	  map forever.*/
	private static void prune(long now) {
		if (sinking.size() <= PRUNE_WHEN_LARGER_THAN) {
			return;
		}
		Iterator<Map.Entry<UUID, Sinking>> it = sinking.entrySet().iterator();
		while (it.hasNext()) {
			if (now - it.next().getValue().lastTick > FORGET_AFTER_TICKS) {
				it.remove();
			}
		}
	}
}
