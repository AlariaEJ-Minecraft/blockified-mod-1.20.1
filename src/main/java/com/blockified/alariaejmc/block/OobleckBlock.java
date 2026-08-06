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
import java.util.Map;
import java.util.UUID;

/**
 * Quicksand: staying still sinks fast (SINK_RATE); continuous horizontal
 * motion instead pulls the entity back up (RISE_RATE), so the only way to
 * cross a patch safely is to keep walking/sprinting through it. Sideways
 * movement is always heavily dampened (sticky), and once past half of the
 * spot's max depth an extra Slowness stacks on top, so a sunk entity has to
 * fight through both to climb back out. LEVEL (world-gen depth, 1-6) caps
 * how far under a given spot a fully idle entity can go.
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

	private static final Map<UUID, Double> sinkDepth = new HashMap<>();

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

		UUID id = living.getUuid();
		double maxDepth = state.get(LEVEL) / 6.0;
		boolean moving = living.getVelocity().horizontalLength() > MOVING_THRESHOLD;

		double depth = sinkDepth.getOrDefault(id, 0.0);
		depth = moving ? Math.max(0.0, depth - RISE_RATE) : Math.min(maxDepth, depth + SINK_RATE);
		if (depth <= 0.0) {
			sinkDepth.remove(id);
		} else {
			sinkDepth.put(id, depth);
		}

		double top = pos.getY() + 1.0 - depth;
		if (living.getY() < top) {
			living.setPosition(living.getX(), top, living.getZ());
		}

		boolean sunk = depth > maxDepth * 0.5;
		float multiplier = sunk ? SUNK_MULTIPLIER : STICKY_MULTIPLIER;
		Vec3d velocity = living.getVelocity();
		living.setVelocity(velocity.multiply(multiplier, velocity.y < 0 ? 0.7 : 1.0, multiplier));

		if (sunk) {
			living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 3, true, false));
		}
	}
}
