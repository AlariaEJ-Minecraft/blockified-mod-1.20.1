package com.blockified.alariaejmc.effect;

import com.blockified.Blockified;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEffects {
	public static final StatusEffect PhantomProtection = registerEffect("phantom_protection",
			new PhantomProtectionEffect(StatusEffectCategory.BENEFICIAL, 0x4A4A6E));

	private static StatusEffect registerEffect(String name, StatusEffect effect) {
		return Registry.register(Registries.STATUS_EFFECT, new Identifier(Blockified.MOD_ID, name), effect);
	}

	public static void registerModEffects() {
	}
}
