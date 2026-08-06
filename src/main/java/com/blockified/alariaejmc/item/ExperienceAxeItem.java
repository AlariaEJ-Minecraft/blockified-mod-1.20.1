package com.blockified.alariaejmc.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;

import java.util.Random;

/**
 * An axe that drains experience from the mob it hits and grants it to the attacking player.
 */
public class ExperienceAxeItem extends AxeItem {
	private static final Random RANDOM = new Random();

	private final int minXp;
	private final int maxXp;

	public ExperienceAxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings, int minXp, int maxXp) {
		super(material, attackDamage, attackSpeed, settings);
		this.minXp = minXp;
		this.maxXp = maxXp;
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		boolean result = super.postHit(stack, target, attacker);
		if (attacker instanceof PlayerEntity player && !(target instanceof PlayerEntity) && !player.getWorld().isClient) {
			player.addExperience(minXp + RANDOM.nextInt(maxXp - minXp + 1));
		}
		return result;
	}
}
