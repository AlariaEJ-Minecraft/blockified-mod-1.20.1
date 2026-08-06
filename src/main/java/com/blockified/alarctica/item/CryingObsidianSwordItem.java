package com.blockified.alarctica.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

/**
 * "Invisible burning" on hit: a Wither effect (fire-like damage over time)
 * with particles suppressed, so nothing visibly shows on the target.
 */
public class CryingObsidianSwordItem extends SwordItem {
	public CryingObsidianSwordItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
		super(material, attackDamage, attackSpeed, settings);
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		boolean result = super.postHit(stack, target, attacker);
		if (!(target instanceof PlayerEntity) && !target.getWorld().isClient) {
			target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 100, 1, false, false));
		}
		return result;
	}
}
