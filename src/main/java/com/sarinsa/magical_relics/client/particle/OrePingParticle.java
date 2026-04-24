package com.sarinsa.magical_relics.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sarinsa.magical_relics.client.ParticleRenderTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class OrePingParticle extends TextureSheetParticle {
    
    private final SpriteSet sprites;
    
    public OrePingParticle( ClientLevel level, double x, double y, double z, SpriteSet animatedSprite ) {
        super( level, x, y, z, 0.0D, 0.0D, 0.0D );
        sprites = animatedSprite;
        quadSize *= 2.0F;
        lifetime = 30;
        hasPhysics = false;
        
        float redGreenOffset = (float) random.nextDouble() * 0.2F + 0.3F;
        rCol = ((float) (random.nextDouble() * 0.20000000298023224D) + 0.8F) * redGreenOffset;
        gCol = ((float) (random.nextDouble() * 0.20000000298023224D) + 1.0F) * redGreenOffset;
        bCol = 1.0F;
        
        setSpriteFromAge( animatedSprite );
    }
    
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderTypes.NO_DEPTH;
    }
    
    @Override
    public float getQuadSize( float partialTick ) {
        return quadSize * Mth.clamp( ((float) age + partialTick) / (float) lifetime * 32.0F, 0.0F, 1.0F );
    }
    
    @Override
    public void tick() {
        if( age++ >= lifetime ) {
            remove();
        }
        else {
            setSpriteFromAge( sprites );
        }
    }
    
    @Override
    protected int getLightColor( float f ) {
        BlockPos pos = BlockPos.containing( x, y, z );
        // noinspection deprecation
        return level.hasChunkAt( pos ) ? 15728640 : 0;
    }
    
    @Override
    public void render( VertexConsumer vertexConsumer, Camera camera, float partialTick ) {
        Vec3 cameraPos = camera.getPosition();
        float f = (float) (Mth.lerp( partialTick, xo, x ) - cameraPos.x());
        float f1 = (float) (Mth.lerp( partialTick, yo, y ) - cameraPos.y());
        float f2 = (float) (Mth.lerp( partialTick, zo, z ) - cameraPos.z());
        Quaternionf quaternion;
        
        if( roll == 0.0F ) {
            quaternion = camera.rotation();
        }
        else {
            quaternion = new Quaternionf( camera.rotation() );
            quaternion.rotateZ( Mth.lerp( partialTick, oRoll, roll ) );
        }
        Vector3f[] vertexPos = new Vector3f[] {
                new Vector3f( -1.0F, -1.0F, 0.0F ),
                new Vector3f( -1.0F, 1.0F, 0.0F ),
                new Vector3f( 1.0F, 1.0F, 0.0F ),
                new Vector3f( 1.0F, -1.0F, 0.0F )
        };
        float quadSize = getQuadSize( partialTick );
        
        for( int i = 0; i < 4; ++i ) {
            Vector3f vPos = vertexPos[i];
            vPos.rotate( quaternion );
            vPos.mul( quadSize );
            vPos.add( f, f1, f2 );
        }
        float u0 = getU0();
        float u1 = getU1();
        float v0 = getV0();
        float v1 = getV1();
        
        int lightColor = getLightColor( partialTick );
        
        vertexConsumer.vertex( vertexPos[0].x(), vertexPos[0].y(), vertexPos[0].z() ).uv( u1, v1 ).color( rCol, gCol, bCol, alpha ).uv2( lightColor ).endVertex();
        vertexConsumer.vertex( vertexPos[1].x(), vertexPos[1].y(), vertexPos[1].z() ).uv( u1, v0 ).color( rCol, gCol, bCol, alpha ).uv2( lightColor ).endVertex();
        vertexConsumer.vertex( vertexPos[2].x(), vertexPos[2].y(), vertexPos[2].z() ).uv( u0, v0 ).color( rCol, gCol, bCol, alpha ).uv2( lightColor ).endVertex();
        vertexConsumer.vertex( vertexPos[3].x(), vertexPos[3].y(), vertexPos[3].z() ).uv( u0, v1 ).color( rCol, gCol, bCol, alpha ).uv2( lightColor ).endVertex();
    }
    
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        
        private final SpriteSet sprites;
        
        public Factory( SpriteSet animatedSprite ) {
            this.sprites = animatedSprite;
        }
        
        @Override
        public Particle createParticle( SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed ) {
            return new OrePingParticle( level, x, y, z, sprites );
        }
    }
}
