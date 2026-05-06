package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.compat.crust.MRCrustPlugin;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import fathertoast.crust.api.entity.IPlayerVelocityWatcher;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpiderAbility extends BaseArtifactAbility<AbilityConfig> {
    
    private static final String[] PREFIXES = {
            createPrefix( "spider", "arachnid" ),
            createPrefix( "spider", "climber" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "spider", "bouldering" ),
            createSuffix( "spider", "scrabbling" )
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ARMOR_TICK
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.LEGGINGS,
            ArtifactCategory.BOOTS
    );
    
    
    public SpiderAbility() { }
    
    
    @Override
    public void onArmorTick( ItemStack artifact, Level level, Player player, EquipmentSlot slot ) {
        if( level.isClientSide || player.isCreative() ) return;
        if( !checkHorizontalCollision( player ) ) return;
        
        final IPlayerVelocityWatcher.Entry velocityEntry = MRCrustPlugin.getPlayerVelocityWatcher().get( player );
        
        if( Math.abs( velocityEntry.dX() ) > 0.001 || Math.abs( velocityEntry.dZ() ) > 0.001 || velocityEntry.dY() > 0.001 ) {
            if( player.tickCount % 30 == 0 ) {
                artifact.hurtAndBreak( 1, player, entity -> entity.broadcastBreakEvent( slot ) );
            }
        }
    }
    
    /** @return If the given entity has horizontal collision with a block. */
    public static boolean checkHorizontalCollision( Entity entity ) {
        // noinspection resource
        return entity.level().getBlockCollisions( entity, entity.getBoundingBox()
                // Expand box slightly towards X and Z to check for horizontal collisions.
                .inflate( 0.01, 0.0, 0.01 ) ).iterator().hasNext();
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
        return isArmor ? TriggerType.ARMOR_TICK : null;
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
