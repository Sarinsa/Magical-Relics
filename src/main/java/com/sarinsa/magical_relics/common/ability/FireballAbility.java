package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.entity.VolatileFireball;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import com.sarinsa.magical_relics.common.util.annotations.AbilityConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
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

    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.USE
    );

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.RING,
            ArtifactCategory.WAND,
            ArtifactCategory.STAFF,
            ArtifactCategory.DAGGER,
            ArtifactCategory.AXE
    );

    private static ForgeConfigSpec.IntValue explosionPower;
    private static ForgeConfigSpec.IntValue cooldown;


    public FireballAbility() {}


    @AbilityConfig(abilityId = "magical_relics:fireball")
    public static void buildEntries(ForgeConfigSpec.Builder configBuilder) {
        explosionPower = configBuilder.comment("The explosion power of the fireballs summoned by this ability. Be a bit careful with larger numbers, since the fireballs explode automatically after having traveled a good distance")
                .defineInRange("explosionPower", 1, 1, 20);

        cooldown = configBuilder.comment("How many ticks of cooldown to put this ability on when it has been used")
                .defineInRange("cooldown", 20, 5, 100000);
    }

    @Override
    public boolean onUse(Level level, Player player, ItemStack itemStack) {
        if (!ArtifactUtils.isAbilityOnCooldown(itemStack, this)) {
            Vec3 viewVec = player.getViewVector(1.0F);
            VolatileFireball fireball = new VolatileFireball(level, player, 0.0D, 0.0D, 0.0D, explosionPower.get());
            fireball.setPos(player.getX() + viewVec.x * 2.0D, player.getY(0.5D) + 0.25D, fireball.getZ() + viewVec.z * 2.0D);
            fireball.shootFromRotation(player, player.getXRot(), player.getYRot(), 2.5F, 2.5F, 2.5F);
            level.addFreshEntity(fireball);

            if (!level.isClientSide) {
                RandomSource random = level.random;
                level.playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0F, (random.nextFloat() -random.nextFloat()) * 0.2F + 1.0F);
            }
            itemStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));

            ArtifactUtils.setAbilityCooldown(itemStack, this, cooldown.get());
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
    public TriggerType getRandomTrigger(RandomSource random, boolean isArmor, boolean isCurio) {
        return isArmor ? null : TriggerType.USE;
    }

    @NotNull
    @Override
    public List<TriggerType> supportedTriggers() {
        return TRIGGERS;
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
