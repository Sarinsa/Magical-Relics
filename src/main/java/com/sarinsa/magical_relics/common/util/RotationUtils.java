package com.sarinsa.magical_relics.common.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Helper class containing various convenience methods
 * related to rotating stuff.
 */
public class RotationUtils {
    
    /**
     * @param original The bounding box to transform.
     * @param rotation The rotation to rotate the box relative to.
     *                 If this is {@link Rotation#NONE} or null, the original box is returned.
     * @return A new AABB with the same dimensions as the original box,
     * then rotated using the given rotation enum.
     */
    public static AABB rotate( AABB original, @Nullable Rotation rotation ) {
        Objects.requireNonNull( original );
        if( rotation == null || rotation == Rotation.NONE ) return original;
        
        final double minX = original.minX;
        final double minY = original.minY;
        final double minZ = original.minZ;
        
        final double maxX = original.maxX;
        final double maxY = original.maxY;
        final double maxZ = original.maxZ;
        
        final Vec3 corner1;
        final Vec3 corner2;
        
        switch( rotation ) {
            case CLOCKWISE_90 -> {
                corner1 = new Vec3( 1.0 - minZ, minY, minX );
                corner2 = new Vec3( 1.0 - maxZ, maxY, maxX );
            }
            case CLOCKWISE_180 -> {
                corner1 = new Vec3( 1.0 - minX, minY, 1.0 - minZ );
                corner2 = new Vec3( 1.0 - maxX, maxY, 1.0 - maxZ );
            }
            default -> {
                corner1 = new Vec3( minZ, minY, 1.0 - minX );
                corner2 = new Vec3( maxZ, maxY, 1.0 - maxX );
            }
        }
        return new AABB(
                Math.min( corner1.x, corner2.x ), Math.min( corner1.y, corner2.y ), Math.min( corner1.z, corner2.z ),
                Math.max( corner1.x, corner2.x ), Math.max( corner1.y, corner2.y ), Math.max( corner1.z, corner2.z )
        );
    }
    
    /**
     * Checks the difference between two horizontal directions
     * and returns the {@link Rotation} that would be needed to
     * "rotate" the first direction to become the second one.
     * <br><br>
     * {@link Direction#UP} and {@link Direction#DOWN} are not supported
     * argument types, and this method will return {@link Rotation#NONE} if either are
     * used as a parameter.
     *
     * @param dir1 The first direction.
     * @param dir2 The second direction.
     */
    public static Rotation rotationFromDirectionDiff( Direction dir1, Direction dir2 ) {
        Objects.requireNonNull( dir1 );
        Objects.requireNonNull( dir2 );
        
        int diff = (dir1.get2DDataValue() - dir2.get2DDataValue()) & 3;
        
        return switch( diff ) {
            case 1 -> Rotation.CLOCKWISE_90;
            case 2 -> Rotation.CLOCKWISE_180;
            case 3 -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }
}
