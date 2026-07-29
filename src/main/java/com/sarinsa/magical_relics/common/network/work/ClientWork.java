package com.sarinsa.magical_relics.common.network.work;

import com.sarinsa.magical_relics.client.screen.AntiBuilderScreen;
import com.sarinsa.magical_relics.common.ability.JukeboxAbility;
import com.sarinsa.magical_relics.common.blockentity.AntiBuilderBlockEntity;
import com.sarinsa.magical_relics.common.blockentity.CamoBlockEntity;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.config.sync.SyncedProperties;
import com.sarinsa.magical_relics.common.core.config.sync.SyncedProperty;
import com.sarinsa.magical_relics.common.network.message.S2CCamoBlockUpdate;
import com.sarinsa.magical_relics.common.network.message.S2CJukeboxAbility;
import com.sarinsa.magical_relics.common.network.message.S2COpenAntiBuilderScreen;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings( "resource" )
public class ClientWork {
    
    public static void handleJukeboxAbilityUse( S2CJukeboxAbility message ) {
        final LocalPlayer player = Minecraft.getInstance().player;
        
        if( player == null ) return;
        
        final LevelRenderer levelRenderer = Minecraft.getInstance().levelRenderer;
        final BlockPos pos = new BlockPos( message.x(), message.y(), message.z() );
        
        final CompoundTag modData = NBTHelper.getOrCreateCompound( player.getMainHandItem().getOrCreateTag(), ArtifactUtils.TAG_MOD_DATA );
        final CompoundTag abilityData = NBTHelper.getOrCreateCompound( modData, JukeboxAbility.TAG_ABILITY_DATA );
        final Item item = NBTHelper.getRegistryEntry( abilityData, ForgeRegistries.ITEMS, JukeboxAbility.TAG_DISC_ITEM );
        
        if( !message.play() ) {
            levelRenderer.playStreamingMusic( null, pos, null );
        }
        else {
            if( item instanceof RecordItem record ) {
                final RandomSource random = player.level().getRandom();
                levelRenderer.playStreamingMusic( record.getSound(), pos, record );
                
                for( int i = 0; i < 10; i++ ) {
                    player.level().addParticle(
                            ParticleTypes.NOTE,
                            (player.getX()) + (random.nextGaussian() / 2),
                            (player.getY() + 1.2D) + (random.nextGaussian() / 4),
                            (player.getZ()) + (random.nextGaussian() / 2),
                            random.nextInt( 25 ) / 24.0D, 0.0D, 0.0D );
                }
            }
        }
    }
    
    public static void handleOpenBEScreen( S2COpenAntiBuilderScreen message ) {
        int screenType = message.screenType();
        LocalPlayer player = Minecraft.getInstance().player;
        
        if( player == null ) return;
        
        if( screenType == 0 ) {
            BlockPos blockPos = new BlockPos( message.x(), message.y(), message.z() );
            BlockEntity blockEntity = player.level().getExistingBlockEntity( blockPos );
            
            if( blockEntity instanceof AntiBuilderBlockEntity antiBuilder ) {
                Minecraft.getInstance().setScreen( new AntiBuilderScreen( blockPos, antiBuilder ) );
            }
        }
    }
    
    @SuppressWarnings( { "unchecked", "ConstantConditions" } )
    public static void handleCfgValueSync( Object value, byte valueId ) {
        final SyncedProperty<?, ?> property = SyncedProperties.getFromId( valueId );
        
        try {
            if( value instanceof Integer i ) {
                ((SyncedProperty<Integer, ?>) property).setValue( i );
            }
            else if( value instanceof Double d ) {
                ((SyncedProperty<Double, ?>) property).setValue( d );
            }
            else if( value instanceof Boolean b ) {
                ((SyncedProperty<Boolean, ?>) property).setValue( b );
            }
            else {
                MagicalRelics.LOG.warn( "Received config field sync packet from server with unsupported value type! Property ID: '{}'", value );
            }
        }
        catch( Exception e ) {
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
    
    public static void handleCamoStateUpdate( S2CCamoBlockUpdate message ) {
        final ClientLevel level = Minecraft.getInstance().level;
        
        if( message.camoState() == null ) return;
        if( level == null ) return;
        if( !level.isLoaded( message.pos() ) ) return;
        
        if( level.getExistingBlockEntity( message.pos() ) instanceof CamoBlockEntity camoBlockEntity ) {
            final BlockState oldCamoState = camoBlockEntity.getCamoState();
            
            camoBlockEntity.setCamoState( message.camoState() );
            
            if( oldCamoState == null || message.camoState().getLightEmission( level, message.pos() ) != oldCamoState.getLightEmission( level, message.pos() ) ) {
                level.getLightEngine().checkBlock( message.pos() );
            }
        }
    }
}
