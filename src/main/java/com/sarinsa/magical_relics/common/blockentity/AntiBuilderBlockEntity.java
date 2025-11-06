package com.sarinsa.magical_relics.common.blockentity;

import com.sarinsa.magical_relics.common.block.AntiBuilderBlock;
import com.sarinsa.magical_relics.common.block.CamoBlock;
import com.sarinsa.magical_relics.common.core.config.MRGeneralConfig;
import com.sarinsa.magical_relics.common.core.registry.MRBlockEntities;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import com.sarinsa.magical_relics.common.util.References;
import fathertoast.crust.api.util.BoxShape;
import fathertoast.crust.api.util.IBlockEntityDebugShapeProvider;
import fathertoast.crust.api.util.IDebugShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;


public class AntiBuilderBlockEntity extends BlockEntity implements IBlockEntityDebugShapeProvider {
    
    private Direction lastFacing;
    private int[] bbCoordinates;
    private AABB effectiveArea;
    private boolean registeredListener = false;
    
    
    public AntiBuilderBlockEntity( BlockPos pos, BlockState state ) {
        super( MRBlockEntities.ANTI_BUILDER.get(), pos, state );
        bbCoordinates = new int[] {
                -10, -10, -10, 10, 10, 10
        };
        effectiveArea = new AABB(
                getBlockPos().offset( bbCoordinates[0], bbCoordinates[1], bbCoordinates[2] ),
                getBlockPos().offset( bbCoordinates[3], bbCoordinates[4], bbCoordinates[5] )
        );
    }
    
    /** Called on both server and client. */
    public static void tick( Level level, BlockPos pos, BlockState state, AntiBuilderBlockEntity antiBuilder ) {
        Direction dir = antiBuilder.getBlockState().getValue( AntiBuilderBlock.FACING );
        
        if( dir != antiBuilder.lastFacing ) {
            antiBuilder.recalculateEffectiveArea( dir, antiBuilder.bbCoordinates );
            antiBuilder.lastFacing = antiBuilder.getBlockState().getValue( AntiBuilderBlock.FACING );
        }
    }
    
    public void recalculateEffectiveArea( int[] bbCoordinates ) {
        this.recalculateEffectiveArea( getBlockState().getValue( AntiBuilderBlock.FACING ), bbCoordinates );
    }
    
    public void recalculateEffectiveArea( Direction direction, int[] bbCoordinates ) {
        this.bbCoordinates = bbCoordinates;
        AABB aabb = effectiveArea;
        
        switch( direction ) {
            case NORTH -> {
                aabb = new AABB( bbCoordinates[0], bbCoordinates[1], bbCoordinates[2], bbCoordinates[3], bbCoordinates[4], bbCoordinates[5] );
                break;
            }
            case EAST -> {
            
            }
            case SOUTH -> {
            
            }
            case WEST -> {
            
            }
        }
        setEffectiveArea( aabb );
    }
    
    @SuppressWarnings( "ConstantConditions" )
    @Override
    public void setLevel( Level level ) {
        this.level = level;
        
        if( level != null && !registeredListener ) {
            MinecraftForge.EVENT_BUS.register( this );
            registeredListener = true;
        }
    }
    
    @Nullable
    public AABB getEffectiveArea() {
        return effectiveArea;
    }
    
    
    public void setEffectiveArea( AABB area ) {
        this.effectiveArea = area;
    }
    
    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        
        if( registeredListener )
            MinecraftForge.EVENT_BUS.unregister( this );
    }
    
    @Override
    public void setRemoved() {
        super.setRemoved();
        
        if( registeredListener )
            MinecraftForge.EVENT_BUS.unregister( this );
    }
    
    @Override
    protected void saveAdditional( CompoundTag compoundTag ) {
        super.saveAdditional( compoundTag );
        saveBoundsData( compoundTag );
    }
    
    @Override
    public void load( CompoundTag compoundTag ) {
        super.load( compoundTag );
        readBoundsData( compoundTag );
    }
    
    private void saveBoundsData( CompoundTag tag ) {
        tag.putIntArray( "effectiveAreaBounds", bbCoordinates );
    }
    
    private void readBoundsData( CompoundTag tag ) {
        if( tag.contains( "effectiveAreaBounds", Tag.TAG_INT_ARRAY ) ) {
            try {
                int[] bbCoordinates = tag.getIntArray( "effectiveAreaBounds" );
                
                if( bbCoordinates.length == 6 ) {
                    recalculateEffectiveArea( getBlockState().getValue( AntiBuilderBlock.FACING ), bbCoordinates );
                }
            }
            catch( Exception ignored ) { }
        }
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag updateTag = new CompoundTag();
        saveBoundsData( updateTag );
        return updateTag;
    }
    
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create( this );
    }
    
    @Override
    public void handleUpdateTag( CompoundTag tag ) {
        readBoundsData( tag );
    }
    
    
    //-----------------------------------------------------------------------------------------------------------------
    //
    //                                              EVENT STUFF
    //
    //-----------------------------------------------------------------------------------------------------------------
    
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onPlayerRightClickBlock( PlayerInteractEvent.RightClickBlock event ) {
        if( !MRGeneralConfig.CONFIG.enableAntiBuilderBlock.get() || event.getEntity().isCreative() || effectiveArea == null || event.getLevel() != level )
            return;
        
        Item item = event.getItemStack().getItem();
        
        if( item == Blocks.AIR.asItem() )
            return;
        
        BlockPos pos = event.getHitVec().getBlockPos();
        
        if( effectiveArea.contains( pos.getX(), pos.getY(), pos.getZ() ) ) {
            if( event.getLevel().getBlockState( event.getPos() ).getBlock() instanceof CamoBlock ) {
                event.setUseBlock( Event.Result.DENY );
            }
            event.setUseItem( Event.Result.DENY );
            event.getEntity().displayClientMessage( References.ANTI_BUILDER_BLOCK_MESSAGE, true );
        }
    }
    
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onPlayerLeftClickBlock( PlayerInteractEvent.LeftClickBlock event ) {
        if( !MRGeneralConfig.CONFIG.enableAntiBuilderBlock.get() || event.getEntity().isCreative() || effectiveArea == null || event.getLevel() != level )
            return;
        
        BlockState blockState = event.getLevel().getBlockState( event.getPos() );
        
        if( blockState.getBlock() == MRBlocks.ANTI_BUILDER.get() || blockState.isAir() )
            return;
        
        BlockPos pos = event.getPos();
        
        if( effectiveArea.contains( pos.getX(), pos.getY(), pos.getZ() ) ) {
            event.setUseItem( Event.Result.DENY );
            event.getEntity().displayClientMessage( References.ANTI_BUILDER_BLOCK_MESSAGE, true );
        }
    }
    
    @SubscribeEvent
    public void onPlayerInteractEntity( PlayerInteractEvent.EntityInteract event ) {
        if( effectiveArea == null || event.getLevel() != level )
            return;
    }
    
    @SubscribeEvent
    public void onPlayerInteractEntity( LivingAttackEvent event ) {
        if( effectiveArea == null || event.getEntity().level() != level )
            return;
    }
    
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onBlockBreak( BlockEvent.BreakEvent event ) {
        if( !MRGeneralConfig.CONFIG.enableAntiBuilderBlock.get() || effectiveArea == null || event.getLevel() != level )
            return;
        
        if( event.getState().getBlock() == MRBlocks.ANTI_BUILDER.get() )
            return;
        
        checkAndCancelPlayer( event, event.getPos(), event.getPlayer() );
    }
    
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onExplodeEvent( ExplosionEvent.Detonate event ) {
        if( !MRGeneralConfig.CONFIG.enableAntiBuilderBlock.get() || effectiveArea == null || event.getLevel() != level )
            return;
        
        Vec3 pos = event.getExplosion().getPosition();
        
        if( effectiveArea.contains( pos.x(), pos.y(), pos.z() ) ) {
            event.getAffectedBlocks().clear();
        }
    }
    
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onMobGrief( EntityMobGriefingEvent event ) {
        if( !MRGeneralConfig.CONFIG.enableAntiBuilderBlock.get() || effectiveArea == null || event.getEntity().level() != level )
            return;
        
        BlockPos pos = event.getEntity().blockPosition();
        
        if( effectiveArea.contains( pos.getX(), pos.getY(), pos.getZ() ) ) {
            event.setResult( Event.Result.DENY );
        }
    }
    
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onBlockPlace( BlockEvent.EntityPlaceEvent event ) {
        if( !MRGeneralConfig.CONFIG.enableAntiBuilderBlock.get() || effectiveArea == null || event.getLevel() != level )
            return;
        
        if( event.getState().getBlock() == MRBlocks.ANTI_BUILDER.get() )
            return;
        
        checkAndCancelPlayer( event, event.getPos(), event.getEntity() instanceof Player player ? player : null );
    }
    
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onBlockMultiPlace( BlockEvent.EntityMultiPlaceEvent event ) {
        if( !MRGeneralConfig.CONFIG.enableAntiBuilderBlock.get() || effectiveArea == null || event.getLevel() != level )
            return;
        
        checkAndCancelPlayer( event, event.getPos(), event.getEntity() instanceof Player player ? player : null );
    }
    
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onFluidPlaceBlock( BlockEvent.FluidPlaceBlockEvent event ) {
        if( !MRGeneralConfig.CONFIG.enableAntiBuilderBlock.get() || effectiveArea == null || event.getLevel() != level )
            return;
        
        checkAndCancel( event, event.getPos() );
    }
    
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onPortalSpawn( BlockEvent.PortalSpawnEvent event ) {
        if( !MRGeneralConfig.CONFIG.enableAntiBuilderBlock.get() || effectiveArea == null || event.getLevel() != level )
            return;
        
        checkAndCancel( event, event.getPos() );
    }
    
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onToolModification( BlockEvent.BlockToolModificationEvent event ) {
        if( !MRGeneralConfig.CONFIG.enableAntiBuilderBlock.get() || effectiveArea == null || event.getLevel() != level )
            return;
        
        checkAndCancelPlayer( event, event.getPos(), event.getPlayer() );
    }
    
    
    private void checkAndCancel( Event event, BlockPos pos ) {
        if( effectiveArea.contains( pos.getX(), pos.getY(), pos.getZ() ) ) {
            event.setCanceled( true );
        }
    }
    
    private void checkAndCancelPlayer( Event event, BlockPos pos, @Nullable Player player ) {
        if( player == null || player.isCreative() )
            return;
        
        if( effectiveArea.contains( pos.getX(), pos.getY(), pos.getZ() ) ) {
            event.setCanceled( true );
            player.displayClientMessage( References.ANTI_BUILDER_BLOCK_MESSAGE, true );
        }
    }
    
    @Override
    @Nullable
    public List<IDebugShape> getDebugShapes() {
        return List.of( new BoxShape( effectiveArea ) );
    }
}
