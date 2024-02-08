package com.sarinsa.magical_relics.common.artifact;

import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class NightVisionAbility extends BaseArtifactAbility {

    private static final int EFFECT_DURATION = 1800;


    private static final String[] PREFIXES = {
            createPrefix("night_vision", "sensing"),
            createPrefix("night_vision", "sighted")
    };

    private static final String[] SUFFIXES = {
            createSuffix("night_vision", "seeing"),
            createSuffix("night_vision", "night_vision")
    };

    public NightVisionAbility() {
        super("night_vision");
    }

    @Override
    public boolean onUse(Level level, Player player, ItemStack itemStack) {
        if (!ArtifactUtils.isAbilityOnCooldown(itemStack, this)) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, EFFECT_DURATION));

            ArtifactUtils.setAbilityCooldown(itemStack, this, EFFECT_DURATION);
            return true;
        }
        return false;
    }

    @Override
    public Rarity getRarity() {
        return Rarity.RARE;
    }

    @Override
    public String[] getPrefixes() {
        return PREFIXES;
    }

    @Override
    public String[] getSuffixes() {
        return SUFFIXES;
    }

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.USE;
    }
}
