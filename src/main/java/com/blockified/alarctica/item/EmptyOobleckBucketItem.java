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
 * Picks a placed Oobleck block back up into a filled bucket.
 */
public class EmptyOobleckBucketItem extends Item {
	public EmptyOobleckBucketItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();

		if (world.getBlockState(pos).getBlock() != ModBlocks.Oobleck) {
			return ActionResult.PASS;
		}

		if (!world.isClient) {
			world.breakBlock(pos, false);
			world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);

			PlayerEntity player = context.getPlayer();
			if (player != null && !player.getAbilities().creativeMode) {
				ItemStack stack = context.getStack();
				stack.decrement(1);
				ItemStack filledBucket = new ItemStack(ModItems.BucketOfOobleck);
				if (stack.isEmpty()) {
					player.setStackInHand(context.getHand(), filledBucket);
				} else if (!player.giveItemStack(filledBucket)) {
					player.dropItem(filledBucket, false);
				}
			}
		}

		return ActionResult.success(world.isClient);
	}
}
