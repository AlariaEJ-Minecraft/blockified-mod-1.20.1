package com.blockified.alariaejmc.block;

import com.blockified.alariaejmc.world.ModPortals;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * The walk-through plane of a Lodestone Reach portal. Pass-through (no
 * collision) and indestructible by hand, like a vanilla portal - breaking
 * the Magnetar frame is what takes it down. Entities entering it get sent
 * across by ModPortals, guarded by the vanilla portal cooldown so they
 * don't bounce straight back.
 */
public class LodestoneReachPortalBlock extends Block {
	public LodestoneReachPortalBlock(Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		return false;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		super.onEntityCollision(state, world, pos, entity);
		if (!(world instanceof ServerWorld serverWorld) || entity.hasPortalCooldown()) {
			return;
		}
		ModPortals.travel(entity, serverWorld, pos);
	}
}
