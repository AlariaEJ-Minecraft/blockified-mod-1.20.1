package com.blockified.alariaejmc.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

/**
 * Passive marker effect: no per-tick stat changes of its own. The actual
 * phantom-attack cancellation lives in ModEvents (ALLOW_DAMAGE), which
 * checks for this effect on the victim.
 */
public class PhantomProtectionEffect extends StatusEffect {
	public PhantomProtectionEffect(StatusEffectCategory category, int color) {
		super(category, color);
	}
}
