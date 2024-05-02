package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class CashoutAbility extends BaseArtifactAbility {

    private static final ResourceLocation LOOT_TABLE = MagicalRelics.resLoc("misc/cashout_ability");

    private static final String[] PREFIXES = {
            createPrefix("cashout", "valuable"),
            createPrefix("cashout", "precious")
    };

    private static final String[] SUFFIXES = {
            createSuffix("cashout", "wealth"),
            createSuffix("cashout", "riches"),
            createSuffix("cashout", "money"),
            createSuffix("cashout", "treasure"),
    };

    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.DROPPED
    );

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AMULET,
            ArtifactCategory.TRINKET,
            ArtifactCategory.FIGURINE,
            ArtifactCategory.RING,
            ArtifactCategory.AXE
    );


    public CashoutAbility() {
    }


    @Override
    public boolean onDropped(Level level, ItemEntity itemEntity, Player player) {
        if (level instanceof ServerLevel serverLevel) {
            LootTable lootTable = serverLevel.getServer().getLootTables().get(LOOT_TABLE);

            if (lootTable == LootTable.EMPTY)
                return false;

            LootContext.Builder builder = new LootContext.Builder(serverLevel)
                    .withRandom(serverLevel.random)
                    .withParameter(LootContextParams.ORIGIN, itemEntity.position())
                    .withParameter(LootContextParams.THIS_ENTITY, player);

            ObjectArrayList<ItemStack> loot = lootTable.getRandomItems(builder.create(LootContextParamSets.GIFT));

            for (ItemStack itemStack : loot) {
                Block.popResource(serverLevel, itemEntity.blockPosition(), itemStack);
            }
            return true;
        }
        // Returning false for client since it gets left out
        return true;
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
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
    public TriggerType getRandomTrigger(RandomSource random, boolean isArmor) {
        return TriggerType.DROPPED;
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
        return Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.cashout.description");
    }
}
