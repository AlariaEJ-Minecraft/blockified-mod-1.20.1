package com.blockified.alarctica.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Powder-Snow-style sinking: no collision for entities without boots (any
 * boots, not just leather), full collision for entities wearing boots.
 * A single layer resting on solid ground stops the entity at about half
 * depth; three or more stacked layers let the entity sink through and,
 * once their head is submerged, slowly drown.
 */
public class MudBogBlock extends Block {
	public MudBogBlock(Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return hasBoots(context.getEntity()) ? VoxelShapes.fullCube() : VoxelShapes.empty();
	}

	private static boolean hasBoots(Entity entity) {
		if (!(entity instanceof LivingEntity living)) {
			return false;
		}
		ItemStack boots = living.getEquippedStack(EquipmentSlot.FEET);
		return !boots.isEmpty() && boots.getItem() instanceof ArmorItem armor && armor.getType() == ArmorItem.Type.BOOTS;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		super.onEntityCollision(state, world, pos, entity);
		if (world.isClient || !(entity instanceof LivingEntity living) || hasBoots(entity)) {
			return;
		}

		Vec3d velocity = entity.getVelocity();
		entity.setVelocity(velocity.multiply(0.4, velocity.y < 0 ? 0.7 : 1.0, 0.4));

		boolean deepPatch = world.getBlockState(pos.down()).getBlock() == this
				&& world.getBlockState(pos.down(2)).getBlock() == this;

		if (!deepPatch) {
			double halfway = pos.getY() + 0.5;
			if (entity.getY() < halfway) {
				entity.setPosition(entity.getX(), halfway, entity.getZ());
				Vec3d clamped = entity.getVelocity();
				entity.setVelocity(clamped.x, Math.max(clamped.y, 0), clamped.z);
			}
			return;
		}

		boolean submerged = world.getBlockState(BlockPos.ofFloored(entity.getX(), entity.getEyeY(), entity.getZ()))
				.getBlock() == this;
		if (submerged) {
			living.setAirSupply(living.getAirSupply() - 1);
			if (living.getAirSupply() < -20) {
				living.setAirSupply(0);
				living.damage(world.getDamageSources().create(ModDamageTypes.MUD_BOG_DROWN), 2.0f);
			}
		} else {
			living.setAirSupply(Math.min(living.getAirSupply() + 4, living.getMaxAirSupply()));
		}
	}
}
