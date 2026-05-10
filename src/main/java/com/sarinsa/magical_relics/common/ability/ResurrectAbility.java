package com.sarinsa.magical_relics.common.ability;

import com.google.common.collect.ImmutableList;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import com.sarinsa.magical_relics.common.core.config.ability.CooldownAbilityConfig;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import fathertoast.crust.api.config.common.ConfigManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class ResurrectAbility extends BaseArtifactAbility<CooldownAbilityConfig> {
    
    
    private static final String[] PREFIXES = {
            createPrefix( "resurrect", "regenerative" ),
            createPrefix( "resurrect", "invigorating" )
    };
    
    private static final String[] SUFFIXES = {
            createSuffix( "resurrect", "resurrection" ),
            createSuffix( "resurrect", "lazarus" )
    };
    
    private static final List<TriggerType> TRIGGERS = ImmutableList.of(
            TriggerType.ON_DEATH
    );
    
    private static final List<ArtifactCategory> TYPES = ImmutableList.of(
            ArtifactCategory.HELMET,
            ArtifactCategory.CHESTPLATE,
            ArtifactCategory.BELT,
            ArtifactCategory.FIGURINE
    );
    
    
    public ResurrectAbility() { }
    
    
    @Override
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new CooldownAbilityConfig( cfgManager, abilityId, 6000 );
    }
    
    @Override
    public void onDeath( Level level, Player player, @Nullable EquipmentSlot slot, @Nullable SlotContext slotContext, ItemStack artifact, LivingDeathEvent event ) {
        // Don't do stuff if the event is already canceled
        if( event.isCanceled() || event.getSource().is( DamageTypeTags.BYPASSES_INVULNERABILITY ) ) return;
        
        if( !ArtifactUtils.isAbilityOnCooldown( artifact, this ) ) {
            ArtifactUtils.setAbilityOnCooldown( artifact, this );
            
            if( slotContext != null ) {
                artifact.hurtAndBreak( artifact.getMaxDamage() / 4, player, ( p ) -> CuriosApi.broadcastCurioBreakEvent( slotContext ) );
            }
            else if( slot != null ) {
                artifact.hurtAndBreak( artifact.getMaxDamage() / 4, player, ( p ) -> p.broadcastBreakEvent( slot ) );
            }
            event.setCanceled( true );
            player.setHealth( Math.min( 10.0F, player.getMaxHealth() ) );
            
            if( !level.isClientSide ) {
                level.playSound( null, player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F );
            }
        }
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
        return TriggerType.ON_DEATH;
    }
    
    @Override
    public List<TriggerType> supportedTriggers() {
        return TRIGGERS;
    }
    
    @Override
    public List<ArtifactCategory> getCompatibleCategories() {
        return TYPES;
    }
}
