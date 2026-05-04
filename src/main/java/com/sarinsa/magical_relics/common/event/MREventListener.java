package com.sarinsa.magical_relics.common.event;

import com.sarinsa.magical_relics.common.ability.LightningAbility;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Collection;
import java.util.UUID;

public class MREventListener {
    
    @SubscribeEvent
    public void onPlayerDropItem( ItemTossEvent event ) {
        ItemEntity tossedItem = event.getEntity();
        Level level = event.getEntity().level();
        Player player = event.getPlayer();
        Collection<BaseArtifactAbility<?>> abilities = ArtifactUtils.getAbilitiesWithTrigger( TriggerType.DROPPED, tossedItem.getItem() );
        
        if( !abilities.isEmpty() ) {
            for( BaseArtifactAbility<?> ability : abilities ) {
                if( ability.onDropped( level, tossedItem, player ) ) {
                    event.setCanceled( true );
                }
            }
        }
    }
    
    @SubscribeEvent
    @SuppressWarnings( "ConstantConditions" )
    public void onPlayerTick( TickEvent.PlayerTickEvent event ) {
        if( event.phase == TickEvent.Phase.START ) return;
        
        final Player player = event.player;
        // noinspection resource
        final Level level = player.level();
        final ItemStack mainhandItem = player.getItemInHand( InteractionHand.MAIN_HAND );
        final ItemStack offhandItem = player.getItemInHand( InteractionHand.OFF_HAND );
        
        ArtifactUtils.getAbilitiesWithTrigger( TriggerType.HELD, mainhandItem )
                .forEach( ( ability ) -> ability.onHeld( level, player, mainhandItem, EquipmentSlot.MAINHAND ) );
        ArtifactUtils.getAbilitiesWithTrigger( TriggerType.HELD, offhandItem )
                .forEach( ( ability ) -> ability.onHeld( level, player, offhandItem, EquipmentSlot.OFFHAND ) );
        
        ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory( player ).orElse( null );
        
        if( curiosInventory != null ) {
            for( SlotResult slotResult : curiosInventory.findCurios( ArtifactUtils.CURIO_SLOTS ) ) {
                final ItemStack curioStack = slotResult.stack();
                ArtifactUtils.getAbilitiesWithTrigger( TriggerType.CURIO_TICK, curioStack )
                        .forEach( ability -> ability.onCurioTick( curioStack, level, player, slotResult.slotContext() ) );
            }
        }
    }
    
    @SubscribeEvent
    public void onLivingDamaged( LivingDamageEvent event ) {
        if( event.getSource().getDirectEntity() instanceof Player player ) {
            for( EquipmentSlot slot : EquipmentSlot.values() ) {
                ItemStack artifact = player.getItemBySlot( slot );
                Collection<BaseArtifactAbility<?>> abilities = ArtifactUtils.getAbilitiesWithTrigger( TriggerType.USER_ATTACKING, artifact );
                
                if( !abilities.isEmpty() ) {
                    for( BaseArtifactAbility<?> ability : abilities ) {
                        ability.onDamageMob( artifact, player, event.getEntity() );
                    }
                }
            }
        }
        else if( event.getEntity() instanceof Player player ) {
            for( EquipmentSlot slot : EquipmentSlot.values() ) {
                ItemStack artifact = player.getItemBySlot( slot );
                Collection<BaseArtifactAbility<?>> abilities = ArtifactUtils.getAbilitiesWithTrigger( TriggerType.USER_DAMAGED, artifact );
                
                if( !abilities.isEmpty() ) {
                    for( BaseArtifactAbility<?> ability : abilities ) {
                        ability.onUserDamaged( player.level(), player, event.getSource(), artifact );
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    @SuppressWarnings( "ConstantConditions" )
    public void onLivingDeath( LivingDeathEvent event ) {
        if( event.getEntity() instanceof Player player ) {
            for( EquipmentSlot slot : EquipmentSlot.values() ) {
                ItemStack artifact = player.getItemBySlot( slot );
                Collection<BaseArtifactAbility<?>> abilities = ArtifactUtils.getAbilitiesWithTrigger( TriggerType.ON_DEATH, artifact );
                
                if( !abilities.isEmpty() ) {
                    for( BaseArtifactAbility<?> ability : abilities ) {
                        ability.onDeath( player.level(), player, slot, null, artifact, event );
                    }
                }
            }
            ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory( player ).orElse( null );
            
            if( curiosInventory != null ) {
                for( SlotResult slotResult : curiosInventory.findCurios( ArtifactUtils.CURIO_SLOTS ) ) {
                    ItemStack curioArtifact = slotResult.stack();
                    Collection<BaseArtifactAbility<?>> abilities = ArtifactUtils.getAbilitiesWithTrigger( TriggerType.ON_DEATH, curioArtifact );
                    
                    if( !abilities.isEmpty() ) {
                        for( BaseArtifactAbility<?> ability : abilities ) {
                            ability.onDeath( player.level(), player, null, slotResult.slotContext(), curioArtifact, event );
                        }
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onPlayerEquipmentChange( LivingEquipmentChangeEvent event ) {
    
    }
    
    @SubscribeEvent
    public void onEntityStruckByLightning( EntityStruckByLightningEvent event ) {
        // If enabled in config, cancel lightning strike damage for players
        // if the lightning was summoned by an artifact.
        if( event.getEntity() instanceof Player player && MRArtifactAbilities.LIGHTNING.get().getConfig().LIGHTNING.immuneSummoner.get() ) {
            UUID uuid = LightningAbility.getSummonerId( event.getLightning() );
            
            if( uuid != null && uuid.equals( player.getUUID() ) ) {
                event.setCanceled( true );
            }
        }
    }
}
