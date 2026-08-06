package com.blockified.alariaejmc.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * An ice variant that, in addition to its block slipperiness, nudges a
 * standing-still entity with a fixed horizontal push each tick. pushForce
 * of 0 disables the standstill push entirely (e.g. Black Ice).
 */
public class PushingIceBlock extends Block {
	private static final double STANDSTILL_THRESHOLD = 1.0E-4;

	private final double pushForce;

	public PushingIceBlock(Settings settings, double pushForce) {
		super(settings);
		this.pushForce = pushForce;
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
		super.onSteppedOn(world, pos, state, entity);
		if (pushForce <= 0 || world.isClient || !(entity instanceof LivingEntity)) {
			return;
		}

		Vec3d velocity = entity.getVelocity();
		double horizontalSpeedSq = velocity.x * velocity.x + velocity.z * velocity.z;
		if (horizontalSpeedSq < STANDSTILL_THRESHOLD) {
			entity.setVelocity(velocity.add(pushForce, 0, 0));
			entity.velocityModified = true;
		}
	}
}
