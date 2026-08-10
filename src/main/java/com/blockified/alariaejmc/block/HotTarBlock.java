package com.blockified.alariaejmc.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Burns whatever stands on it, the way a Magma Block does, and sets it
 * alight on top of that - this is hot tar, so it sticks and keeps
 * burning rather than just scorching once.
 *
 * Fire-immune mobs and anyone wearing Frost Walker are spared, matching
 * the vanilla magma rules players already know.
 */
public class HotTarBlock extends Block {
	private static final float STEP_DAMAGE = 1.0f;
	private static final int BURN_SECONDS = 3;

	public HotTarBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
		if (!entity.isFireImmune()
				&& entity instanceof LivingEntity living
				&& !EnchantmentHelper.hasFrostWalker(living)) {
			entity.damage(world.getDamageSources().hotFloor(), STEP_DAMAGE);
			entity.setOnFireFor(BURN_SECONDS);
		}
		super.onSteppedOn(world, pos, state, entity);
	}
}
