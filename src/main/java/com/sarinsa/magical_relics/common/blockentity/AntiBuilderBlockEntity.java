package com.sarinsa.magical_relics.common.blockentity;

import com.sarinsa.magical_relics.common.block.AntiBuilderBlock;
import com.sarinsa.magical_relics.common.core.config.Config;
import com.sarinsa.magical_relics.common.core.registry.MRBlockEntities;
import com.sarinsa.magical_relics.common.core.registry.MRBlocks;
import com.sarinsa.magical_relics.common.util.RotationUtil;
import com.sarinsa.magical_relics.common.util.TranslationUtil;
import fathertoast.crust.api.config.common.value.collection.RegistryValueList;
import fathertoast.crust.api.config.common.value.collection.value.MobEffectStats;
import fathertoast.crust.api.lib.NBTHelper;
import fathertoast.crust.api.util.BoxShape;
import fathertoast.crust.api.util.IDebugShape;
import fathertoast.crust.api.util.IDebugShapeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class AntiBuilderBlockEntity extends BlockEntity implements IDebugShapeProvider {
    
    public static final String KEY_EFFECTIVE_AREA = "EffectiveAreaBounds";
    public static final String KEY_LAST_FACING = "LastFacing";
    
    private static final List<MobEffectInstance> MALADIES = new ArrayList<>();
    
    private Direction lastFacing;
    
    private AABB effectiveArea;
    private boolean registeredListener = false;
    
    
    public AntiBuilderBlockEntity( BlockPos pos, BlockState state ) {
        super( MRBlockEntities.ANTI_BUILDER.get(), pos, state );
    }
    
    @Override
    public void onLoad() {
        // noinspection ConstantConditions
        if( !level.isClientSide ) {
            if( effectiveArea == null ) {
                // Create default effective area box.
                setEffectiveArea( new AABB(
                        -10, -10, -10,
                        11, 11, 11
                ) );
            }
            if( lastFacing == null ) {
                lastFacing = getBlockState().getValue( AntiBuilderBlock.FACING );
            }
        }
    }
    
    /** Convenience method for requesting a block update at this block entity's position. */
    private void sendBlockUpdate() {
        if( level == null ) return;
        level.sendBlockUpdated( getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS );
    }
    
    /**
     * The server ticker used for this block entity type.
     *
     * @see AntiBuilderBlock#getTicker(Level, BlockState, BlockEntityType)
     */
    public static void tick( AntiBuilderBlockEntity antiBuilder ) {
        final Direction dir = antiBuilder.getBlockState().getValue( AntiBuilderBlock.FACING );
        
        if( dir != antiBuilder.lastFacing && antiBuilder.effectiveArea != null ) {
            final BlockPos pos = antiBuilder.getBlockPos();
            final Rotation rotation = RotationUtil.rotationFromDirectionDiff( dir, antiBuilder.lastFacing );
            antiBuilder.recalculateEffectiveArea( rotation, antiBuilder.effectiveArea.move( -pos.getX(), -pos.getY(), -pos.getZ() ) );
            antiBuilder.sendBlockUpdate();
            antiBuilder.lastFacing = dir;
        }
    }
    
    /**
     * Recalculates the effective area bounding box for this anti-builder,
     * with no specified rotation.
     *
     * @param boxDimensions An array containing the bounds of the box to create.
     *                      This array must contain only the following, in this specific order:<br>
     *                      <strong>{min-X, min-Y, min-Z, max-X, max-Y, max-Z}</strong>.<br>
     *                      Note that the box is moved to this block entity's position during this operation.
     */
    public void recalculateEffectiveArea( int[] boxDimensions ) {
        recalculateEffectiveArea( null, boxDimensions );
    }
    
    /**
     * Recalculates the effective area bounding box for this anti-builder.
     *
     * @param rotation      The rotation to use when rotating the box. If this is null, no transformations are made.
     * @param boxDimensions An array containing the bounds of the box to create.
     *                      This array must contain only the following, in this specific order:<br>
     *                      <strong>{min-X, min-Y, min-Z, max-X, max-Y, max-Z}</strong>.<br>
     *                      Note that the box is moved to this block entity's position during this operation.
     */
    public void recalculateEffectiveArea( @Nullable Rotation rotation, int[] boxDimensions ) {
        if( boxDimensions.length != 6 )
            throw new IllegalArgumentException( "Attempted to recalculate effective area with bounds array with invalid length: "
                    + boxDimensions.length );
        recalculateEffectiveArea( rotation, new AABB(
                boxDimensions[0], boxDimensions[1], boxDimensions[2],
                boxDimensions[3], boxDimensions[4], boxDimensions[5]
        ) );
    }
    
    /**
     * Recalculates the effective area bounding box for this anti-builder,
     * using no rotation.
     *
     * @param boundingBox The bounding box to use. Note that the box is moved to this block entity's position during this operation.
     */
    public void recalculateEffectiveArea( AABB boundingBox ) {
        recalculateEffectiveArea( null, boundingBox );
    }
    
    /**
     * Recalculates the effective area bounding box for this anti-builder.
     *
     * @param rotation    The rotation to use when rotating the box. If this is null, no transformations are made.
     * @param boundingBox The bounding box to use. Note that the box is moved to this block entity's position during this operation.
     */
    public void recalculateEffectiveArea( @Nullable Rotation rotation, AABB boundingBox ) {
        setEffectiveArea( RotationUtil.rotate( boundingBox, rotation ) );
    }
    
    /**
     * @param box      The bounding box to write to an int array.
     * @param relative If true, this block entity's coordinates gets subtracted from the box coordinates.
     *                 Do this if the box to write was already moved to this block entity's position earlier,
     *                 and you wish to go back to "zero".
     * @return The given {@link AABB} as an int array with the following format:<br>
     * <strong>{min-X, min-Y, min-Z, max-X, max-Y, max-Z}</strong>
     */
    public int[] aabbToIntArray( AABB box, boolean relative ) {
        Objects.requireNonNull( box );
        if( relative ) {
            final BlockPos pos = getBlockPos();
            return new int[] {
                    (int) box.minX - pos.getX(), (int) box.minY - pos.getY(), (int) box.minZ - pos.getZ(),
                    (int) box.maxX - pos.getX(), (int) box.maxY - pos.getY(), (int) box.maxZ - pos.getZ(),
            };
        }
        else {
            return new int[] {
                    (int) box.minX, (int) box.minY, (int) box.minZ,
                    (int) box.maxX, (int) box.maxY, (int) box.maxZ
            };
        }
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
    
    /** @return This anti-builder's effective area bounding box. */
    @Nullable
    public AABB getEffectiveArea() {
        return effectiveArea;
    }
    
    /**
     * Sets this anti-builder's effective area bounding box.
     * Note that the provided box is moved to this block entity's position.
     */
    public void setEffectiveArea( AABB aabb ) {
        effectiveArea = aabb.move( getBlockPos() );
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
    protected void saveAdditional( CompoundTag saveTag ) {
        super.saveAdditional( saveTag );
        
        saveTag.putInt( KEY_LAST_FACING, lastFacing.ordinal() );
        saveEffectiveArea( saveTag );
    }
    
    @Override
    public void load( CompoundTag saveTag ) {
        super.load( saveTag );
        
        if( NBTHelper.containsNumber( saveTag, KEY_LAST_FACING ) ) {
            // If the number ends up exceeding enum length, do nothing
            int ordinal = saveTag.getInt( KEY_LAST_FACING );
            if( ordinal > Direction.values().length )
                return;
            lastFacing = Direction.values()[ordinal];
        }
        readEffectiveArea( saveTag );
    }
    
    /** Saves the anti builder's effective area box to NBT. */
    private void saveEffectiveArea( CompoundTag saveTag ) {
        if( effectiveArea == null ) return;
        final int[] boxDimensions = aabbToIntArray( effectiveArea, true );
        saveTag.putIntArray( KEY_EFFECTIVE_AREA, boxDimensions );
    }
    
    /** Reads the anti builder's effective area box from NBT. */
    private void readEffectiveArea( CompoundTag saveTag ) {
        if( NBTHelper.containsIntArray( saveTag, KEY_EFFECTIVE_AREA ) ) {
            try {
                final int[] boxDimensions = saveTag.getIntArray( KEY_EFFECTIVE_AREA );
                // Array length should be 6, if not the data is bad
                if( boxDimensions.length == 6 ) {
                    recalculateEffectiveArea( null, boxDimensions );
                }
            }
            catch( Exception ignored ) { }
        }
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag updateTag = new CompoundTag();
        saveEffectiveArea( updateTag );
        return updateTag;
    }
    
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create( this );
    }
    
    @Override
    public void handleUpdateTag( CompoundTag tag ) {
        readEffectiveArea( tag );
    }
    
    
    //-----------------------------------------------------------------------------------------------------------------
    //
    //                                              EVENT STUFF
    //
    //-----------------------------------------------------------------------------------------------------------------
    
    
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onPlayerLeftClickBlock( PlayerInteractEvent.LeftClickBlock event ) {
        final var level = event.getLevel();
        final var player = event.getEntity();
        
        if( player.isCreative() || !canIntervene( event.getLevel() ) ) return;
        
        if( !Config.MAIN.ANTI_BUILDER.antiBuilderBlocksBuilding.get() ) return;
        
        final BlockState blockState = level.getBlockState( event.getPos() );
        
        if( blockState.getBlock() == MRBlocks.ANTI_BUILDER.get() || blockState.isAir() )
            return;
        
        final BlockPos pos = event.getPos();
        
        if( isWithinBounds( pos ) ) {
            event.setUseItem( Event.Result.DENY );
            player.displayClientMessage( TranslationUtil.ANTI_BUILDER_BLOCK_MESSAGE, true );
        }
    }
    
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onBlockBreak( BlockEvent.BreakEvent event ) {
        final var level = event.getLevel();
        final var player = event.getPlayer();
        final var blockPos = event.getPos();
        
        if( !canIntervene( level ) || event.getState().getBlock() == MRBlocks.ANTI_BUILDER.get() )
            return;
        
        if( Config.MAIN.ANTI_BUILDER.antiBuilderBlocksBuilding.get() ) {
            checkAndCancel( event, blockPos, player );
        }
        else {
            inflictWithMalady( player, blockPos );
        }
    }
    
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onExplodeEvent( ExplosionEvent.Detonate event ) {
        if( !Config.MAIN.ANTI_BUILDER.antiBuilderBlocksBuilding.get() || !canIntervene( event.getLevel() ) )
            return;
        
        final Vec3 pos = event.getExplosion().getPosition();
        
        if( isWithinBounds( pos ) ) {
            event.getAffectedBlocks().clear();
        }
    }
    
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onMobGrief( EntityMobGriefingEvent event ) {
        if( !Config.MAIN.ANTI_BUILDER.antiBuilderBlocksBuilding.get() || !canIntervene( event.getEntity().level() ) )
            return;
        
        final BlockPos pos = event.getEntity().blockPosition();
        
        if( isWithinBounds( pos ) ) {
            event.setResult( Event.Result.DENY );
        }
    }
    
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onBlockPlace( BlockEvent.EntityPlaceEvent event ) {
        if( !canIntervene( event.getLevel() ) )
            return;
        
        if( event.getState().getBlock() == MRBlocks.ANTI_BUILDER.get() )
            return;
        
        final Entity entity = event.getEntity();
        final BlockPos pos = event.getPos();
        
        if( Config.MAIN.ANTI_BUILDER.antiBuilderBlocksBuilding.get() ) {
            checkAndCancel( event, pos, event.getEntity() instanceof Player player ? player : null );
        }
        else if( entity instanceof LivingEntity livingEntity ) {
            inflictWithMalady( livingEntity, pos );
        }
    }
    
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onBlockMultiPlace( BlockEvent.EntityMultiPlaceEvent event ) {
        if( !Config.MAIN.ANTI_BUILDER.antiBuilderBlocksBuilding.get() || !canIntervene( event.getLevel() ) )
            return;
        
        checkAndCancel( event, event.getPos(), event.getEntity() instanceof Player player ? player : null );
    }
    
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onToolModification( BlockEvent.BlockToolModificationEvent event ) {
        if( !Config.MAIN.ANTI_BUILDER.antiBuilderBlocksBuilding.get() || !canIntervene( event.getLevel() ) )
            return;
        checkAndCancel( event, event.getPos(), event.getPlayer() );
    }
    
    /**
     * Convenience method for canceling world interaction events.
     * <br><br>
     * If the given block pos is within this anti-builder's effective area,
     * the event is canceled.
     */
    private void checkAndCancel( Event event, BlockPos pos ) {
        if( isWithinBounds( pos ) ) event.setCanceled( true );
    }
    
    /**
     * Convenience method for canceling world interaction events that may involve players.
     * <br><br>
     * If the given block pos is within this anti-builder's effective area,
     * the event is canceled. If a player is involved and said player is in creative mode,
     * we return early without canceling the event.
     */
    private void checkAndCancel( Event event, BlockPos pos, @Nullable Player player ) {
        if( player == null || player.isCreative() )
            return;
        
        if( isWithinBounds( pos ) ) {
            event.setCanceled( true );
            player.displayClientMessage( TranslationUtil.ANTI_BUILDER_BLOCK_MESSAGE, true );
        }
    }
    
    /**
     * @return True if this anti builder's effective area box is not null
     * and the given level and this anti builder's level are the same.
     */
    @SuppressWarnings( "BooleanMethodIsAlwaysInverted" )
    private boolean canIntervene( LevelAccessor level ) {
        return this.level == level && effectiveArea != null;
    }
    
    /** @return True if the given entity is inside this anti-builder's effective area. */
    private boolean isWithinBounds( Entity entity ) {
        return isWithinBounds( entity.blockPosition() );
    }
    
    /** @return True if the given position is inside this anti-builder's effective area. */
    private boolean isWithinBounds( Vec3 position ) {
        return effectiveArea.contains( position.x, position.y, position.z );
    }
    
    /** @return True if the given block position is within this-anti builder's effective area. */
    private boolean isWithinBounds( BlockPos pos ) {
        return effectiveArea.contains( pos.getX(), pos.getY(), pos.getZ() );
    }
    
    /**
     * Grabs a random mob effect instance from the "plague" config list
     * and applies it to the given entity if it is inside the anti-builder's
     * effective area. Players in creative mode are immune.
     */
    private void inflictWithMalady( LivingEntity entity, BlockPos pos ) {
        // No effects to apply, abort.
        if( MALADIES.isEmpty() ) return;
        if( entity instanceof Player player && player.isCreative() ) return;
        
        if( isWithinBounds( pos ) ) {
            // noinspection resource
            MobEffectInstance malady = new MobEffectInstance( MALADIES.get( entity.level().random.nextInt( MALADIES.size() ) ) );
            entity.addEffect( malady );
        }
    }
    
    /**
     * @return A List of debug shapes that should be rendered in the world.
     * The list may be null, but do NOT include any null entries in the list.
     */
    @Override // IDebugShapeProvider
    @Nullable
    public List<IDebugShape> getDebugShapes() {
        return effectiveArea == null ? IDebugShapeProvider.NO_SHAPES : List.of( new BoxShape( effectiveArea ).withColor( 0x00FF00 ) );
    }
    
    /**
     * The effective area box should already be moved relative to
     * the anti-builder's in-world position, so we don't
     * want the shape renderer to move the box again.
     */
    @Override // IDebugShapeProvider
    public boolean useWorldPosition() {
        return false;
    }
    
    /**
     * Called when the {@link com.sarinsa.magical_relics.common.core.config.MainConfig.AntiBuilder#maladies}
     * config field is loaded/reloaded.
     */
    @SuppressWarnings( "UnstableApiUsage" )
    public static void refreshMaladiesList( RegistryValueList<MobEffect, MobEffectStats> list ) {
        MALADIES.clear();
        list.entries().forEach( ( entry )
                -> MALADIES.add( entry.value().create( entry.key() ) ) );
    }
}
