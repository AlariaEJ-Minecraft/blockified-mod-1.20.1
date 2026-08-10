package com.blockified.alariaejmc.item;

import com.blockified.alariaejmc.block.MagnetarBlock;
import com.blockified.alariaejmc.world.ModPortals;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Lights a Lodestone Reach portal, flint-and-steel style: right-click any
 * Magnetar in a flat 4x4 ring whose front faces up, and the enclosed 2x2
 * fills with portal blocks.
 */
public class MagnetizedTarchItem extends Item {
	public MagnetizedTarchItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos clicked = context.getBlockPos();
		BlockState clickedState = world.getBlockState(clicked);

		if (!(clickedState.getBlock() instanceof MagnetarBlock)
				|| clickedState.get(MagnetarBlock.FACING) != Direction.UP) {
			return ActionResult.PASS;
		}

		if (world.isClient) {
			return ActionResult.SUCCESS;
		}

		BlockPos origin = ModPortals.findFrameOrigin(world, clicked);
		if (origin == null || !ModPortals.lightPortal(world, origin)) {
			return ActionResult.FAIL;
		}

		world.playSound(null, clicked, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0f, 1.2f);

		PlayerEntity player = context.getPlayer();
		if (player != null) {
			context.getStack().damage(1, player, p -> p.sendToolBreakStatus(context.getHand()));
		}

		return ActionResult.SUCCESS;
	}
}
