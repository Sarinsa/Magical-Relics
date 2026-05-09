package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.StringField;
import fathertoast.crust.api.util.ResourceLocationUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class CashoutAbility extends BaseArtifactAbility<CashoutAbility.CashoutAbilityConfig> {
    
    private static final String LOOT_TABLE = MagicalRelics.rl( "misc/cashout_ability" ).toString();
    
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
    
    
    public static class CashoutAbilityConfig extends AbilityConfig {
        
        public final Cashout CASHOUT;
        
        public CashoutAbilityConfig( ConfigManager cfgManager, ResourceLocation abilityId,
                                     String lootTableId ) {
            super( cfgManager, abilityId );
            
            CASHOUT = new Cashout( this, lootTableId );
        }
        
        public static class Cashout extends AbstractConfigCategory<CashoutAbilityConfig> {
            
            public final StringField lootTableId;
            
            public Cashout( CashoutAbilityConfig parent, String lootTable ) {
                super( parent, "cashout", "Options for the loot dropped by this ability." );
                
                lootTableId = SPEC.define( new StringField( "loot_table_id", lootTable, ResourceLocationUtils::strictIsValid,
                        "The ID of the loot table to drop when this ability converts its artifact into treasure." ) );
            }
        }
    }
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new CashoutAbilityConfig( cfgManager, abilityId, LOOT_TABLE );
    }
    
    @Override
    public boolean onDropped( Level level, ItemEntity itemEntity, Player player ) {
        if( level instanceof ServerLevel serverLevel ) {
            final ResourceLocation id = ResourceLocation.parse( getConfig().CASHOUT.lootTableId.get() );
            final LootTable lootTable = serverLevel.getServer().getLootData().getLootTable( id );
            
            if( lootTable == LootTable.EMPTY )
                return false;
            
            final LootParams.Builder paramsBuilder = (new LootParams.Builder( serverLevel ))
                    .withParameter( LootContextParams.ORIGIN, itemEntity.position() )
                    .withOptionalParameter( LootContextParams.THIS_ENTITY, player );
            
            final ObjectArrayList<ItemStack> loot = lootTable.getRandomItems( paramsBuilder.create( LootContextParamSets.GIFT ) );
            
            for( ItemStack itemStack : loot ) {
                Block.popResource( serverLevel, itemEntity.blockPosition(), itemStack );
            }
            return true;
        }
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
