package com.sarinsa.magical_relics.common.ability.base;

import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.config.Config;
import com.sarinsa.magical_relics.common.core.config.ability.AbilityConfig;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.util.ArtifactUtils;
import com.sarinsa.magical_relics.common.util.TranslationUtil;
import fathertoast.crust.api.config.common.ConfigManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public abstract class BaseArtifactAbility<T extends AbilityConfig> {
    
    /** This ability's Crust config. Assigned through reflection. */
    @SuppressWarnings( "unused" )
    private T config;
    
    public BaseArtifactAbility() { }
    
    
    /** Helper method for creating artifact prefixes. */
    protected static String createPrefix( String abilityName, String prefix ) {
        return MagicalRelics.MODID + ".artifact_ability." + MagicalRelics.MODID + "." + abilityName + ".prefix." + prefix;
    }
    
    /** Helper method for creating artifact suffixes. */
    protected static String createSuffix( String abilityName, String suffix ) {
        return MagicalRelics.MODID + ".artifact_ability." + MagicalRelics.MODID + "." + abilityName + ".suffix." + suffix;
    }
    
    /** Helper method for creating ability description tooltip components. */
    public MutableComponent getDescComponent( @Nullable TriggerType triggerType, Object... args ) {
        final ResourceLocation id = Objects.requireNonNull( MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getKey( this ) );
        final String triggerKey = triggerType == null ? "" : "." + triggerType.getName();
        final String s = MagicalRelics.MODID + ".artifact_ability." + id.getNamespace() + "." + id.getPath() + ".description" + triggerKey;
        
        return Component.translatable( s, args );
    }
    
    /**
     * Helper method for creating ability description tooltip components
     * containing an effect duration that should be formatted as HMS.
     */
    public MutableComponent getDescComponent( @Nullable TriggerType triggerType, long effectDuration ) {
        return getDescComponent( triggerType, TranslationUtil.ticksToHMS( effectDuration ).getString() );
    }
    
    /**
     * Helper method for creating ability description tooltip components
     * containing an effect duration that should be formatted as HMS
     * and an effect multiplier as a translated potion level string.
     */
    public MutableComponent getDescComponent( @Nullable TriggerType triggerType, long effectDuration, int effectMult ) {
        return getDescComponent( triggerType, TranslationUtil.potionLevel( effectMult ), TranslationUtil.ticksToHMS( effectDuration ).getString() );
    }
    
    /**
     * Called from {@link Config#initAbilityConfigs()} after
     * the artifact ability registry has been populated.
     *
     * @param cfgManager Magical Relic's config manager.
     * @param abilityId  The registry ID of the ability.
     */
    @SuppressWarnings( "JavadocReference" )
    public AbilityConfig createConfig( ConfigManager cfgManager, ResourceLocation abilityId ) {
        return new AbilityConfig( cfgManager, abilityId );
    }
    
    /**
     * Note that ability configs are constructed after
     * the ability Forge registry has been populated.
     *
     * @return This ability's config.
     */
    public final T getConfig() {
        // Shouldn't really happen unless this is called before
        // Forge registries are populated, so hopefully never!
        if( config == null ) {
            ResourceLocation id = MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getKey( this );
            String s = id == null ? "null" : id.toString();
            throw new IllegalStateException( "Encountered null config for ability with id \"" + s + "\"" );
        }
        return config;
    }
    
    /**
     * @return An array of possible translatable prefixes for this ability.
     */
    public abstract String[] getPrefixes();
    
    /**
     * @return An array of possible translatable suffixes for this ability.
     */
    public abstract String[] getSuffixes();
    
    /**
     * @return A random TriggerType that should be used for this ability when attached to an
     * artifact item stack.
     */
    @Nullable
    public abstract TriggerType getRandomTrigger( ItemStack artifact, RandomSource random, boolean isArmor, boolean isCurio );
    
    /**
     * @return A List of trigger types supported by this ability. This is not super
     * important; primarily utilized in the "apply ability" command.
     */
    @Nonnull
    public abstract List<TriggerType> supportedTriggers();
    
    /**
     * @return A List of artifact categories this ability is compatible with.
     */
    public abstract List<ArtifactCategory> getCompatibleTypes();
    
    /**
     * @return True if a "snowflake" symbol should be prepended to this ability's description
     * when it is on cooldown.
     */
    public boolean showCooldownSymbol() {
        return true;
    }
    
    /**
     * @return A description of this ability that will be added to the artifact item stack's tooltip.
     */
    @Nullable
    public MutableComponent getAbilityDescription( @Nullable TriggerType type, ItemStack artifact, @Nullable Level level, TooltipFlag flag ) {
        return getDescComponent( null );
    }
    
    /**
     * Called from {@link ArtifactUtils#generateRandomArtifact(LevelReader, RandomSource, boolean)} when the ability
     * is applied to an artifact item.
     * <br><br>
     * Can be overridden to write additional data to the ItemStack's NBT and whatnot.
     */
    public void onAbilityAttached( ItemStack artifact, RandomSource random ) { }
    
    /**
     * Only relevant for when an ability is attached to an artifact that is an instance of {@link com.sarinsa.magical_relics.common.item.ArtifactItem}.<br><br>
     * This is called whenever an artifact item that can be equipped in a Curio slot is unequipped.
     */
    public void onUnequipped( SlotContext slotContext, ItemStack artifact ) { }
    
    /**
     * Called when the player right-clicks while holding the artifact.
     *
     * @param hitResult An optional HitResult object. If the player interacted with a block or an entity this will be present.
     *                  If not, this is usually null.
     * @return True if the ability successfully did what it was supposed to.
     */
    public boolean onUse( Level level, Player player, ItemStack artifact, InteractionHand hand, @Nullable HitResult hitResult ) {
        return false;
    }
    
    /**
     * Called each tick while the artifact is held by the player.
     */
    public void onHeld( Level level, Player player, ItemStack artifact, EquipmentSlot slot ) { }
    
    /**
     * Called when an artifact is dropped and becomes an ItemEntity.
     *
     * @return True if the item entity should be consumed/despawned.
     */
    public boolean onDropped( Level level, ItemEntity itemEntity, Player player ) {
        return false;
    }
    
    /**
     * Called when the player attacks an entity with a held artifact.
     */
    public void onDamageMob( ItemStack artifact, Player player, LivingEntity attackedMob ) { }
    
    /**
     * Called when the player is hurt, regardless of damage source.
     */
    public void onUserDamaged( Level level, Player player, DamageSource damageSource, ItemStack artifact ) { }
    
    /**
     * Called when the player dies (only for held artifact items, armor and curio artifact items)<br><br>
     *
     * @param slot        The equipment slot of the artifact item. This will be null if the artifact item is equipped in a curio slot.
     * @param slotContext The Curios slot context of the artifact item. This will be null if the artifact item is equipped in any vanilla slots.
     */
    public void onDeath( Level level, Player player, @Nullable EquipmentSlot slot, @Nullable SlotContext slotContext, ItemStack artifact, LivingDeathEvent event ) { }
    
    /**
     * Called every tick for artifacts that exist in either the player inventory or hotbar (armor and curio slots are excluded).
     */
    public void onInventoryTick( ItemStack artifact, Level level, Entity entity, int slot, boolean isSelectedItem ) { }
    
    /**
     * Called every tick for artifacts that are equipped in armor slots.
     */
    public void onArmorTick( ItemStack artifact, Level level, Player player, EquipmentSlot slot ) { }
    
    /**
     * Called every tick for artifacts that are equipped in curio slots.
     */
    public void onCurioTick( ItemStack artifact, Level level, Player player, SlotContext slotContext ) { }
    
    /**
     * Primarily used for the ability's description text color when
     * rendering it in item tooltip.
     *
     * @return The rarity from the config associated with this ability.
     * returns {@link Rarity#COMMON} if the config does not exist yet.
     */
    public Rarity getRarity() {
        return getConfig() == null
                ? Rarity.COMMON
                : getConfig().GENERAL.rarity.get();
    }
    
    /**
     * Used by abilities that are effectively just attribute modifiers.<br><br>
     *
     * @return An AttributeBoost instance to be granted by this ability.
     */
    @Nullable
    public AttributeBoost getAttributeWithBoost() {
        return null;
    }
    
    @Override
    public String toString() {
        // noinspection ConstantConditions
        String regName = MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().containsValue( this )
                ? MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getKey( this ).toString()
                : "null";
        return "Registry name: " + regName + ", Instance: " + super.toString();
    }
}
