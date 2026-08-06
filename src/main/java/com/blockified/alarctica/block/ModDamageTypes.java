package com.blockified.alarctica.block;

import com.blockified.Blockified;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModDamageTypes {
	public static final RegistryKey<DamageType> MUD_BOG_DROWN = RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
			new Identifier(Blockified.MOD_ID, "mud_bog_drown"));
}
