package com.blockified.alarctica.block;

import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import net.minecraft.block.Block;

/**
 * Solid and walkable, but slows horizontal movement like Soul Sand and
 * cushions falls like Slime/Honey.
 */
public class ClayBogBlock extends Block {
	public ClayBogBlock(Settings settings) {
		super(settings);
	}

	@Override
	public float getVelocityMultiplier() {
		return 0.4f;
	}

	@Override
	public void onEntityLand(BlockView world, Entity entity) {
		entity.fallDistance *= 0.2f;
		super.onEntityLand(world, entity);
	}
}
