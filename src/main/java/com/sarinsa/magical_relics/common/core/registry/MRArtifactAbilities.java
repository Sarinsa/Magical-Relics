package com.sarinsa.magical_relics.common.core.registry;

import com.sarinsa.magical_relics.common.ability.*;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class MRArtifactAbilities {
    
    public static final ResourceLocation REGISTRY_KEY = MagicalRelics.rl( "artifact_abilities" );
    public static final DeferredRegister<BaseArtifactAbility<?>> ARTIFACT_ABILITIES = DeferredRegister.create( REGISTRY_KEY, MagicalRelics.MODID );
    public static final Supplier<IForgeRegistry<BaseArtifactAbility<?>>> ARTIFACT_ABILITY_REGISTRY = ARTIFACT_ABILITIES.makeRegistry( RegistryBuilder::new );
    
    
    public static final RegistryObject<BakerAbility> BAKER = register( "baker", BakerAbility::new );
    public static final RegistryObject<CashoutAbility> CASHOUT = register( "cashout", CashoutAbility::new );
    public static final RegistryObject<HealthBoostAbility> HEALTH_BOOST = register( "health_boost", HealthBoostAbility::new );
    public static final RegistryObject<SpeedBoostAbility> SPEED_BOOST = register( "speed_boost", SpeedBoostAbility::new );
    public static final RegistryObject<AirSneakAbility> AIR_SNEAK = register( "air_sneak", AirSneakAbility::new );
    public static final RegistryObject<NightVisionAbility> NIGHT_VISION = register( "night_vision", NightVisionAbility::new );
    public static final RegistryObject<AdrenalineAbility> ADRENALINE = register( "adrenaline", AdrenalineAbility::new );
    public static final RegistryObject<JukeboxAbility> JUKEBOX = register( "jukebox", JukeboxAbility::new );
    public static final RegistryObject<WaterBreathingAbility> WATER_BREATHING = register( "water_breathing", WaterBreathingAbility::new );
    public static final RegistryObject<ObscurityAbility> OBSCURITY = register( "obscurity", ObscurityAbility::new );
    public static final RegistryObject<SailorAbility> SAILOR = register( "sailor", SailorAbility::new );
    public static final RegistryObject<StunAbility> STUN = register( "stun", StunAbility::new );
    public static final RegistryObject<FireballAbility> FIREBALL = register( "fireball", FireballAbility::new );
    public static final RegistryObject<MassExcavateAbility> MASS_EXCAVATE = register( "mass_excavate", MassExcavateAbility::new );
    public static final RegistryObject<IlluminationAbility> ILLUMINATION = register( "illumination", IlluminationAbility::new );
    public static final RegistryObject<GlowVisionAbility> GLOW_VISION = register( "glow_vision", GlowVisionAbility::new );
    public static final RegistryObject<JumpBoostAbility> JUMP_BOOST = register( "jump_boost", JumpBoostAbility::new );
    public static final RegistryObject<ResurrectAbility> RESURRECT = register( "resurrect", ResurrectAbility::new );
    public static final RegistryObject<SelfRepairAbility> SELF_REPAIR = register( "self_repair", SelfRepairAbility::new );
    public static final RegistryObject<RepairOthersAbility> REPAIR_OTHERS = register( "repair_others", RepairOthersAbility::new );
    public static final RegistryObject<TntAbility> TNT = register( "tnt", TntAbility::new );
    public static final RegistryObject<ReachBoostAbility> REACH_BOOST = register( "reach_boost", ReachBoostAbility::new );
    public static final RegistryObject<OreRadarAbility> ORE_RADAR = register( "ore_radar", OreRadarAbility::new );
    public static final RegistryObject<TerrainWalkerAbility> TERRAIN_WALKER = register( "terrain_walker", TerrainWalkerAbility::new );
    public static final RegistryObject<SlowFallingAbility> SLOW_FALLING = register( "slow_falling", SlowFallingAbility::new );
    public static final RegistryObject<SpiderAbility> SPIDER = register( "spider", SpiderAbility::new );
    public static final RegistryObject<LightningAbility> LIGHTNING = register( "lightning", LightningAbility::new );
    public static final RegistryObject<FoodieAbility> FOODIE = register( "foodie", FoodieAbility::new );
    public static final RegistryObject<YeetAbility> YEET = register( "yeet", YeetAbility::new );
    public static final RegistryObject<DeflectProjectileAbility> DEFLECT_PROJECTILE = register( "deflect_projectile", DeflectProjectileAbility::new );
    
    
    private static <CFG extends AbilityConfig, T extends BaseArtifactAbility<CFG>> RegistryObject<T> register( String name, Supplier<T> supplier ) {
        return ARTIFACT_ABILITIES.register( name, supplier );
    }
    
    @Nullable
    public static BaseArtifactAbility<?> fromId( ResourceLocation id ) {
        if( ARTIFACT_ABILITY_REGISTRY.get().containsKey( id ) )
            return ARTIFACT_ABILITY_REGISTRY.get().getValue( id );
        return null;
    }
}
