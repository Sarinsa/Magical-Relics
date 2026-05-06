package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import com.sarinsa.magical_relics.common.core.config.ability.CooldownAbilityConfig;
import com.sarinsa.magical_relics.common.network.NetworkHelper;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


public class JukeboxAbility extends BaseArtifactAbility<CooldownAbilityConfig> {
    
    public static final String TAG_ABILITY_DATA = "JukeboxAbilityData";
    public static final String TAG_X_POS = "x";
    public static final String TAG_Y_POS = "y";
    public static final String TAG_Z_POS = "z";
    public static final String TAG_PLAY = "PlayMusic";
    public static final String TAG_DISC_ITEM = "MusicDiscId";
    
    private static final String[] PREFIXES = {
            createPrefix( "jukebox", "musical" ),
            createPrefix( "jukebox", "harmonious" ),
            createPrefix( "jukebox", "plonking" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "jukebox", "tunes" ),
            createSuffix( "jukebox", "songs" ),
            createSuffix( "jukebox", "notes" )
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.USE
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.AMULET,
            ArtifactCategory.TRINKET,
            ArtifactCategory.STAFF,
            ArtifactCategory.WAND,
            ArtifactCategory.FIGURINE,
            ArtifactCategory.RING
    );
    
    
    public JukeboxAbility() { }
    
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new CooldownAbilityConfig( cfgManager, abilityId, ArtifactUtils.RARITY_GLORIOUS, 40 );
    }
    
    @Override
    @SuppressWarnings( "ConstantConditions" )
    public void onAbilityAttached( ItemStack artifact, RandomSource random ) {
        CompoundTag modData = NBTHelper.getOrCreateCompound( artifact.getOrCreateTag(), ArtifactUtils.TAG_MOD_DATA );
        CompoundTag abilityData = NBTHelper.getOrCreateCompound( modData, TAG_ABILITY_DATA );
        
        abilityData.putInt( TAG_X_POS, 0 );
        abilityData.putInt( TAG_Y_POS, 0 );
        abilityData.putInt( TAG_Z_POS, 0 );
        abilityData.putBoolean( TAG_PLAY, true );
        
        List<RecordItem> records = new ArrayList<>();
        
        for( Item item : ForgeRegistries.ITEMS.getValues() ) {
            if( item instanceof RecordItem recordItem )
                records.add( recordItem );
        }
        RecordItem randomRecord = records.get( random.nextInt( records.size() ) );
        NBTHelper.putRegistryEntry( abilityData, ForgeRegistries.ITEMS, TAG_DISC_ITEM, randomRecord );
    }
    
    @Override
    public boolean onUse( Level level, Player player, ItemStack artifact, InteractionHand hand, @Nullable HitResult hitResult ) {
        if( !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
            CompoundTag modData = NBTHelper.getOrCreateCompound( artifact.getOrCreateTag(), ArtifactUtils.TAG_MOD_DATA );
            CompoundTag abilityData = NBTHelper.getOrCreateCompound( modData, TAG_ABILITY_DATA );
            
            if( !level.isClientSide ) {
                boolean playMusic = abilityData.getBoolean( TAG_PLAY );
                
                NetworkHelper.sendJukeboxAbilityUse(
                        (ServerPlayer) player,
                        playMusic ? player.blockPosition().getX() : abilityData.getInt( TAG_X_POS ),
                        playMusic ? player.blockPosition().getY() : abilityData.getInt( TAG_Y_POS ),
                        playMusic ? player.blockPosition().getZ() : abilityData.getInt( TAG_Z_POS ),
                        playMusic
                );
            }
            abilityData.putInt( TAG_X_POS, player.getBlockX() );
            abilityData.putInt( TAG_Y_POS, player.getBlockY() );
            abilityData.putInt( TAG_Z_POS, player.getBlockZ() );
            abilityData.putBoolean( TAG_PLAY, !abilityData.getBoolean( TAG_PLAY ) );
            ArtifactUtils.setAbilityOnCooldown( artifact, this );
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
    
    @Override
    @Nullable
    public TriggerType getRandomTrigger( ItemStack artifact, RandomSource random, boolean isArmor, boolean isCurio ) {
        return isArmor ? null : TriggerType.USE;
    }
    
    @Override
    public List<TriggerType> supportedTriggers() {
        return TRIGGERS;
    }
    
    @Override
    public List<ArtifactCategory> getCompatibleTypes() {
        return TYPES;
    }
    
    @Override
    @Nullable
    public MutableComponent getAbilityDescription( @Nullable TriggerType type, ItemStack artifact, @Nullable Level level, TooltipFlag flag ) {
        CompoundTag modData = NBTHelper.getOrCreateCompound( artifact.getOrCreateTag(), ArtifactUtils.TAG_MOD_DATA );
        CompoundTag abilityData = NBTHelper.getOrCreateCompound( modData, TAG_ABILITY_DATA );
        Item disc = NBTHelper.getRegistryEntry( abilityData, ForgeRegistries.ITEMS, TAG_DISC_ITEM );
        
        String recordDesc = "missingno :(";
        
        if( disc != null )
            recordDesc = disc.getDescriptionId() + ".desc";
        
        return descComponent( null, Component.translatable( recordDesc ) );
    }
}
