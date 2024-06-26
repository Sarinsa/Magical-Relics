package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TntAbility extends BaseArtifactAbility {


    private static final String[] PREFIXES = {
            createPrefix("tnt", "demolishing"),
            createPrefix("tnt", "explosive")
    };

    private static final String[] SUFFIXES = {
            createSuffix("tnt", "booms"),
            createSuffix("tnt", "combusting"),
            // Yes this is spanish, but it is funny
            createSuffix("tnt", "explotando")
    };

    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.RIGHT_CLICK_BLOCK
    );

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.TRINKET,
            ArtifactCategory.WAND,
            ArtifactCategory.STAFF
    );


    public TntAbility() {

    }


    @Override
    public boolean onClickBlock(Level level, ItemStack artifact, BlockPos pos, BlockState state, Direction face, Player player) {
        if (!ArtifactUtils.isAbilityOnCooldown(artifact, this)) {
            BlockPos relativePos = pos.relative(face);
            BlockState relativeState = level.getBlockState(relativePos);

            if (relativeState.getCollisionShape(level, relativePos).isEmpty()) {
                PrimedTnt tnt = new PrimedTnt(level, relativePos.getX() + 0.5D, relativePos.getY(), relativePos.getZ() + 0.5D, player);
                level.addFreshEntity(tnt);

                if (!level.isClientSide) {
                    level.playSound(null, relativePos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                artifact.hurtAndBreak(2, player, (p) -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                ArtifactUtils.setAbilityCooldown(artifact, this, 400);
                return true;
            }
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
        return isArmor ? null : TriggerType.RIGHT_CLICK_BLOCK;
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
        return Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.tnt.description");
    }
}
