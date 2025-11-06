package com.sarinsa.magical_relics.common.event;

import com.sarinsa.magical_relics.common.ability.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.LightningAbility;
import com.sarinsa.magical_relics.common.ability.misc.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
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
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class MREventListener {
    
    private static MinecraftServer serverInstance;
    
    private static int timeNextServerTick = 0;
    private static final int serverTickDelay = 10;
    
    private static int repairTick;
    
    private static final List<Player> aggroClearingList = new ArrayList<>();
    
    
    public static int getRepairTick() {
        return repairTick;
    }
    
    
    @SubscribeEvent
    public void onServerStarted( ServerStartedEvent event ) {
        serverInstance = event.getServer();
    }
    
    @SubscribeEvent
    public void onServerStopped( ServerStoppedEvent event ) {
        serverInstance = null;
    }
    
    @SubscribeEvent
    public void onServerTick( TickEvent.ServerTickEvent event ) {
        if( event.phase == TickEvent.Phase.END ) {
            if( ++repairTick >= 1200 )
                repairTick = 0;
            
            ++timeNextServerTick;
            
            if( timeNextServerTick >= serverTickDelay ) {
                timeNextServerTick = 0;
                
                // Tick ability cooldowns
                for( ServerPlayer player : event.getServer().getPlayerList().getPlayers() ) {
                    try {
                        ArtifactUtils.tickAbilityCooldowns( player, serverTickDelay );
                    }
                    catch( Exception e ) {
                        MagicalRelics.LOG.error( "Failed to tick ability cooldowns for player {}!", player.getName() );
                        e.printStackTrace();
                    }
                }
                
                // Go through the aggro clearing list
                aggroClearingList.removeIf( ( player ) -> {
                    if( player.isAlive() ) {
                        Level level = player.level();
                        
                        if( level.isLoaded( player.blockPosition() ) ) {
                            for( PathfinderMob pathfinderMob : level.getEntitiesOfClass( PathfinderMob.class, player.getBoundingBox().inflate( 30.0D, 30.0D, 30.0D ) ) ) {
                                if( pathfinderMob.getTarget() == player || pathfinderMob.getLastHurtByMob() == player ) {
                                    pathfinderMob.setTarget( null );
                                    pathfinderMob.setLastHurtByMob( null );
                                }
                            }
                        }
                    }
                    return true;
                } );
            }
        }
    }
    
    @SubscribeEvent
    public void onPlayerDropItem( ItemTossEvent event ) {
        ItemEntity tossedItem = event.getEntity();
        Level level = event.getEntity().level();
        Player player = event.getPlayer();
        Collection<BaseArtifactAbility> abilities = ArtifactUtils.getAbilitiesWithTrigger( TriggerType.DROPPED, tossedItem.getItem() );
        
        if( !abilities.isEmpty() ) {
            for( BaseArtifactAbility ability : abilities ) {
                if( ability.onDropped( level, tossedItem, player ) ) {
                    event.setCanceled( true );
                }
            }
        }
    }
    
    @SubscribeEvent
    @SuppressWarnings( "ConstantConditions" )
    public void onPlayerTick( TickEvent.PlayerTickEvent event ) {
        Player player = event.player;
        ItemStack heldItem = player.getItemInHand( InteractionHand.MAIN_HAND );
        Level level = player.level();
        Collection<BaseArtifactAbility> abilities = ArtifactUtils.getAbilitiesWithTrigger( TriggerType.HELD, heldItem );
        
        if( !abilities.isEmpty() ) {
            for( BaseArtifactAbility ability : abilities ) {
                ability.onHeld( level, player, heldItem, EquipmentSlot.MAINHAND );
            }
        }
        ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory( player ).orElse( null );
        
        if( curiosInventory != null ) {
            for( SlotResult slotResult : curiosInventory.findCurios( ArtifactUtils.CURIO_SLOTS ) ) {
                ItemStack curioStack = slotResult.stack();
                Collection<BaseArtifactAbility> curioAbilities = ArtifactUtils.getAbilitiesWithTrigger( TriggerType.CURIO_TICK, curioStack );
                
                if( !curioAbilities.isEmpty() ) {
                    for( BaseArtifactAbility ability : curioAbilities ) {
                        ability.onCurioTick( curioStack, level, player, slotResult.slotContext() );
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onLivingDamaged( LivingDamageEvent event ) {
        if( event.getSource().getDirectEntity() instanceof Player player ) {
            for( EquipmentSlot slot : EquipmentSlot.values() ) {
                ItemStack artifact = player.getItemBySlot( slot );
                Collection<BaseArtifactAbility> abilities = ArtifactUtils.getAbilitiesWithTrigger( TriggerType.USER_ATTACKING, artifact );
                
                if( !abilities.isEmpty() ) {
                    for( BaseArtifactAbility ability : abilities ) {
                        ability.onDamageMob( artifact, player, event.getEntity() );
                    }
                }
            }
        }
        else if( event.getEntity() instanceof Player player ) {
            for( EquipmentSlot slot : EquipmentSlot.values() ) {
                ItemStack artifact = player.getItemBySlot( slot );
                Collection<BaseArtifactAbility> abilities = ArtifactUtils.getAbilitiesWithTrigger( TriggerType.USER_DAMAGED, artifact );
                
                if( !abilities.isEmpty() ) {
                    for( BaseArtifactAbility ability : abilities ) {
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
                Collection<BaseArtifactAbility> abilities = ArtifactUtils.getAbilitiesWithTrigger( TriggerType.ON_DEATH, artifact );
                
                if( !abilities.isEmpty() ) {
                    for( BaseArtifactAbility ability : abilities ) {
                        ability.onDeath( player.level(), player, slot, null, artifact, event );
                    }
                }
            }
            ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory( player ).orElse( null );
            
            if( curiosInventory != null ) {
                for( SlotResult slotResult : curiosInventory.findCurios( ArtifactUtils.CURIO_SLOTS ) ) {
                    ItemStack curioArtifact = slotResult.stack();
                    Collection<BaseArtifactAbility> abilities = ArtifactUtils.getAbilitiesWithTrigger( TriggerType.ON_DEATH, curioArtifact );
                    
                    if( !abilities.isEmpty() ) {
                        for( BaseArtifactAbility ability : abilities ) {
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
        if( event.getEntity() instanceof Player player ) {
            UUID uuid = LightningAbility.getSummonerId( event.getLightning() );
            
            if( uuid != null && uuid.equals( player.getUUID() ) ) {
                event.setCanceled( true );
            }
        }
    }
    
    @Nullable
    public static MinecraftServer getCurrentServer() {
        return serverInstance;
    }
    
    public static void queuePlayerForDeaggro( @Nonnull Player player ) {
        Objects.requireNonNull( player );
        
        if( !aggroClearingList.contains( player ) )
            aggroClearingList.add( player );
    }
}
