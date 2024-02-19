package com.sarinsa.magical_relics.common.artifact;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.artifact.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.entity.VolatileFireball;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FireballAbility extends BaseArtifactAbility {

    private static final String[] PREFIXES = {
            createPrefix("fireball", "flaming"),
            createPrefix("fireball", "burning")
    };

    private static final String[] SUFFIXES = {
            createSuffix("fireball", "fire"),
            createSuffix("fireball", "heat"),
            createSuffix("fireball", "incineration")
    };

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.RING, ArtifactCategory.WAND, ArtifactCategory.STAFF, ArtifactCategory.DAGGER
    );


    public FireballAbility() {}


    @Override
    public boolean onUse(Level level, Player player, ItemStack itemStack) {
        if (!ArtifactUtils.isAbilityOnCooldown(itemStack, this)) {
            Vec3 viewVec = player.getViewVector(1.0F);
            VolatileFireball fireball = new VolatileFireball(level, player, 0.0D, 0.0D, 0.0D, 1);
            fireball.setPos(player.getX() + viewVec.x * 2.0D, player.getY(0.5D) + 0.25D, fireball.getZ() + viewVec.z * 2.0D);
            fireball.shootFromRotation(player, player.getXRot(), player.getYRot(), 2.5F, 2.5F, 2.5F);
            level.addFreshEntity(fireball);

            itemStack.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(player.getUsedItemHand()));

            ArtifactUtils.setAbilityCooldown(itemStack, this, 20);
            return true;
        }
        return false;
    }

    @Override
    public String[] getPrefixes() {
        return PREFIXES;
    }

    @Override
    public String[] getSuffixes() {
        return SUFFIXES;
    }

    @Nullable
    @Override
    public TriggerType getRandomTrigger(RandomSource random, boolean isArmor) {
        return isArmor ? null : TriggerType.USE;
    }

    @Override
    public List<ArtifactCategory> getCompatibleTypes() {
        return TYPES;
    }

    @Override
    public MutableComponent getAbilityDescription(ItemStack artifact, @Nullable Level level, TooltipFlag flag) {
        return Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.fireball.description");
    }
}
