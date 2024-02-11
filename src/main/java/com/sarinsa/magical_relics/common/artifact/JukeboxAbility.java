package com.sarinsa.magical_relics.common.artifact;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.artifact.misc.ArtifactCategory;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class JukeboxAbility extends BaseArtifactAbility {

    private static final String[] PREFIXES = {
            createPrefix("jukebox", "musical"),
            createPrefix("jukebox", "harmonious"),
            createPrefix("jukebox", "plonking")
    };

    private static final String[] SUFFIXES = {
            createSuffix("jukebox", "tunes"),
            createSuffix("jukebox", "songs"),
            createSuffix("jukebox", "notes")
    };

    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AMULET, ArtifactCategory.TRINKET, ArtifactCategory.STAFF, ArtifactCategory.WAND, ArtifactCategory.FIGURINE, ArtifactCategory.RING
    );


    public JukeboxAbility() {
    }


    @Override
    @SuppressWarnings("ConstantConditions")
    public void onAbilityAttached(ItemStack artifact, RandomSource random) {
        CompoundTag modDataTag = artifact.getOrCreateTag().getCompound(ArtifactUtils.MOD_DATA_KEY);

        List<RecordItem> records = new ArrayList<>();

        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if (item instanceof RecordItem recordItem)
                records.add(recordItem);
        }
        RecordItem randomRecord = records.get(random.nextInt(records.size()));
        String recordId = ForgeRegistries.ITEMS.getKey(randomRecord).toString();

        modDataTag.putString("JUKEBOXMusicDiscId", recordId);
    }

    @Override
    public boolean onUse(Level level, Player player, ItemStack artifact) {
        if (!ArtifactUtils.isAbilityOnCooldown(artifact, this)) {
            CompoundTag modDataTag = artifact.getOrCreateTag().getCompound(ArtifactUtils.MOD_DATA_KEY);
            RecordItem record = (RecordItem) ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(modDataTag.getString("JUKEBOXMusicDiscId")));

            if (record == null)
                return false;

            if (level.isClientSide) {
                Minecraft.getInstance().levelRenderer.playStreamingMusic(record.getSound(), player.blockPosition(), record);
            }
            ArtifactUtils.setAbilityCooldown(artifact, this, 200);
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

    @Override
    public TriggerType getRandomTrigger(RandomSource random, boolean isArmor) {
        return isArmor ? null : TriggerType.USE;
    }

    @Override
    public List<ArtifactCategory> getCompatibleTypes() {
        return TYPES;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public MutableComponent getAbilityDescription(ItemStack artifact, @Nullable Level level, TooltipFlag flag) {
        CompoundTag modDataTag = artifact.getOrCreateTag().getCompound(ArtifactUtils.MOD_DATA_KEY);
        ResourceLocation recordId = ResourceLocation.tryParse(modDataTag.getString("JUKEBOXMusicDiscId"));

        String recordDesc = "missingno :(";

        if (ForgeRegistries.ITEMS.containsKey(recordId))
            recordDesc = ForgeRegistries.ITEMS.getValue(recordId).getDescriptionId() + ".desc";

        return Component.translatable(MagicalRelics.MODID + ".artifact_ability.magical_relics.jukebox.description", Component.translatable(recordDesc));
    }
}
