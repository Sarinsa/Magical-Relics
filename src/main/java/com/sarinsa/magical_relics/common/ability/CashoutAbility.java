package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import fathertoast.crust.api.config.common.ConfigManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class CashoutAbility extends BaseArtifactAbility<AbilityConfig> {
    
    private static final ResourceLocation LOOT_TABLE = MagicalRelics.rl( "misc/cashout_ability" );
    
    private static final String[] PREFIXES = {
            createPrefix( "cashout", "valuable" ),
            createPrefix( "cashout", "precious" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "cashout", "wealth" ),
            createSuffix( "cashout", "riches" ),
            createSuffix( "cashout", "money" ),
            createSuffix( "cashout", "treasure" ),
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
    
    
    public CashoutAbility() { }
    
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new AbilityConfig( cfgManager, abilityId, Rarity.UNCOMMON );
    }
    
    @Override
    public boolean onDropped( Level level, ItemEntity itemEntity, Player player ) {
        if( level instanceof ServerLevel serverLevel ) {
            LootTable lootTable = serverLevel.getServer().getLootData().getLootTable( LOOT_TABLE );
            
            if( lootTable == LootTable.EMPTY )
                return false;
            
            LootParams.Builder paramsBuilder = (new LootParams.Builder( serverLevel ))
                    .withParameter( LootContextParams.ORIGIN, itemEntity.position() )
                    .withOptionalParameter( LootContextParams.THIS_ENTITY, player );
            
            ObjectArrayList<ItemStack> loot = lootTable.getRandomItems( paramsBuilder.create( LootContextParamSets.GIFT ) );
            
            for( ItemStack itemStack : loot ) {
                Block.popResource( serverLevel, itemEntity.blockPosition(), itemStack );
            }
            return true;
        }
        // Returning false for client since it gets left out
        return true;
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
        return TriggerType.DROPPED;
    }
    
    @Override
    public List<TriggerType> supportedTriggers() {
        return TRIGGERS;
    }
    
    @Override
    public List<ArtifactCategory> getCompatibleTypes() {
        return TYPES;
    }
}
