package com.blockified.alarctica.item;

import com.blockified.alarctica.block.ModBlocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Places a solid Oobleck block, Powder-Snow-bucket style (no fluid physics).
 */
public class BucketOfOobleckItem extends Item {
	public BucketOfOobleckItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos hitPos = context.getBlockPos();
		BlockPos placePos = world.getBlockState(hitPos).isReplaceable() ? hitPos : hitPos.offset(context.getSide());

		if (!world.getBlockState(placePos).isReplaceable()) {
			return ActionResult.FAIL;
		}

		if (!world.isClient) {
			world.setBlockState(placePos, ModBlocks.Oobleck.getDefaultState());
			world.playSound(null, placePos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);

			PlayerEntity player = context.getPlayer();
			if (player != null && !player.getAbilities().creativeMode) {
				ItemStack stack = context.getStack();
				stack.decrement(1);
				ItemStack emptyBucket = new ItemStack(ModItems.EmptyOobleckBucket);
				if (stack.isEmpty()) {
					player.setStackInHand(context.getHand(), emptyBucket);
				} else if (!player.giveItemStack(emptyBucket)) {
					player.dropItem(emptyBucket, false);
				}
			}
		}

		return ActionResult.success(world.isClient);
	}
}
