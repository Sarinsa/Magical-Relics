package com.sarinsa.magical_relics.common.util;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.sarinsa.magical_relics.common.ability.base.ArtifactCategory;
import com.sarinsa.magical_relics.common.ability.base.AttributeBoost;
import com.sarinsa.magical_relics.common.ability.base.BaseArtifactAbility;
import com.sarinsa.magical_relics.common.ability.base.TriggerType;
import com.sarinsa.magical_relics.common.core.MagicalRelics;
import com.sarinsa.magical_relics.common.core.config.ability.CooldownAbilityConfig;
import com.sarinsa.magical_relics.common.core.registry.MRArtifactAbilities;
import com.sarinsa.magical_relics.common.core.registry.MRItems;
import com.sarinsa.magical_relics.common.item.IArtifactItem;
import com.sarinsa.magical_relics.common.tag.MRItemTags;
import fathertoast.crust.api.config.common.value.collection.RegistrySet;
import fathertoast.crust.api.lib.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ArtifactUtils {
    
    public static final String CURIO_BELT = "belt";
    public static final String CURIO_RING = "ring";
    public static final String CURIO_AMULET = "amulet";
    
    public static final String[] CURIO_SLOTS = {
            CURIO_BELT,
            CURIO_RING,
            CURIO_AMULET
    };
    
    public static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    };
    
    /** Very cool and awesome custom rarities. **/
    public static final Rarity RARITY_MUNDANE = Rarity.create( MagicalRelics.rl( "mundane" ).toString(), ChatFormatting.GRAY );
    public static final Rarity RARITY_GLORIOUS = Rarity.create( MagicalRelics.rl( "glorious" ).toString(), ChatFormatting.GREEN );
    
    
    /** NBT keys for mod data storage. */
    public static final String TAG_MOD_DATA = "MagicalRelicsData";
    public static final String TAG_ABILITY = "MRArtifactAbilities";
    public static final String TAG_VARIANT = "MRArtifactVariant";
    public static final String TAG_ITEM_COLOR = "MRItemColor";
    public static final String TAG_ATTRIBUTE_MODS = "MRAttributeModifiers";
    public static final String TAG_ABILITY_COOLDOWNS = "MRAbilityCooldowns";
    public static final String TAG_PREFIX = "MRNamePrefix";
    public static final String TAG_SUFFIX = "MRNameSuffix";
    
    
    /** Possible overlay colors for artifact items. */
    private static final int[] ARTIFACT_COLORS = {
            0x00B6FF, 0x1466FF, 0x6647FF,
            0xC23FFF, 0xFF00A5, 0xFF0010,
            0xFF5F0F, 0xFF9D00, 0xFFE500,
            0x2FBC00, 0x00BA6F, 0x37B7AA,
            0x915E35, 0xC4746F, 0xC170BC,
            0x84BF4E, 0x6B75BC, 0xD8D8D8
    };
    
    /** Contains all abilities that CAN be applied when a random artifact is generated. */
    private static final List<BaseArtifactAbility<?>> OBTAINABLE_ABILITIES = new ArrayList<>();
    
    
    /**
     * @param artifactItem The artifact item to use for this item stack.<br><br>
     *                     Should normally be an instance of the following:
     *                     <br>
     *                     {@link com.sarinsa.magical_relics.common.item.ArtifactArmorItem}<br>
     *                     {@link com.sarinsa.magical_relics.common.item.ArtifactItem}<br>
     *                     {@link com.sarinsa.magical_relics.common.item.ArtifactAxeItem}<br>
     *                     {@link com.sarinsa.magical_relics.common.item.DyableArtifactArmorItem}<br>
     * @param variant      An integer corresponding to a specific texture variant of the artifact item.<br>
     * @return An item stack with all the necessary NBT tags for ability data.
     */
    public static ItemStack createBlankArtifact( Item artifactItem, int variant, RandomSource randomSource ) {
        ItemStack artifactStack = new ItemStack( artifactItem );
        
        CompoundTag modData = NBTHelper.getOrCreateCompound( artifactStack.getOrCreateTag(), TAG_MOD_DATA );
        
        modData.putInt( TAG_VARIANT, variant );
        modData.putInt( TAG_ITEM_COLOR, ARTIFACT_COLORS[randomSource.nextInt( ARTIFACT_COLORS.length )] );
        modData.put( TAG_ABILITY_COOLDOWNS, new CompoundTag() );
        modData.putString( TAG_PREFIX, References.MUNDANE_ABILITY_PREFIX );
        modData.putString( TAG_SUFFIX, "" );
        
        return artifactStack;
    }
    
    /**
     * Generates an artifact item with randomized abilities, variant and overlay color. Neat!
     * <br><br>
     *
     * @return The randomly generated artifact ItemStack.
     */
    public static ItemStack generateRandomArtifact( LevelReader level, RandomSource random, boolean legendary ) {
        ArtifactCategory category = ArtifactCategory.values()[random.nextInt( ArtifactCategory.values().length )];
        List<RegistryObject<? extends Item>> artifactList = MRItems.ARTIFACTS_BY_CATEGORY.get( category );
        
        Item artifactItem = artifactList.get( random.nextInt( artifactList.size() ) ).get();
        ItemStack artifactStack = createBlankArtifact( artifactItem, random.nextInt( category.getVariations() ), random );
        
        // Apply a random trim if the artifact is an armor piece
        applyRandomArmorTrim( level, random, artifactStack );
        
        var allAbilities = new ArrayList<>( OBTAINABLE_ABILITIES );
        // Filter out abilities that are not applicable to the Artifact's category.
        allAbilities.removeIf( ( ability ) -> !ability.getCompatibleTypes().contains( ((IArtifactItem) artifactItem).getCategory() ) );
        
        BaseArtifactAbility<?>[] abilitiesToApply;
        BaseArtifactAbility<?>[] appliedAbilities = {};
        // We might get unlucky RNG here and there,
        // so try 10 times before giving up
        for( int i = 0; i < 10; i++ ) {
            Collections.shuffle( allAbilities );
            
            final int maxAbilities = legendary
                    ? Math.min( 4, allAbilities.size() )
                    : Math.min( 1 + (random.nextInt( 3 ) == 0 ? random.nextInt( 3 ) : 0), allAbilities.size() );
            abilitiesToApply = new BaseArtifactAbility[maxAbilities];
            
            for( int j = 0; j < maxAbilities; j++ )
                abilitiesToApply[j] = allAbilities.get( j );
            
            appliedAbilities = tryApplyAbilities( artifactStack, random, abilitiesToApply );
            
            if( appliedAbilities.length > 0 )
                break;
        }
        // Try to apply enchantments if this is a legendary artifact
        if( legendary ) {
            EnchantmentHelper.enchantItem( random, artifactStack, 20 + random.nextInt( 11 ), false );
        }
        // Apply some stock attribute mods for daggers and swords and whatnot
        applyMandatoryAttributeMods( artifactStack, ((IArtifactItem) artifactItem).getCategory(), random );
        
        // Create a custom display name for the ItemStack, picking random
        // prefixes and suffixes from successfully applied abilities
        if( appliedAbilities.length > 0 ) {
            CompoundTag tag = artifactStack.getOrCreateTag();
            CompoundTag modDataTag = tag.getCompound( TAG_MOD_DATA );
            
            modDataTag.putString( TAG_PREFIX, appliedAbilities[0].getPrefixes()[random.nextInt( appliedAbilities[0].getPrefixes().length )] );
            
            if( appliedAbilities.length > 1 ) {
                modDataTag.putString( TAG_SUFFIX, appliedAbilities[1].getSuffixes()[random.nextInt( appliedAbilities[1].getSuffixes().length )] );
            }
            else {
                modDataTag.putString( TAG_SUFFIX, appliedAbilities[0].getSuffixes()[random.nextInt( appliedAbilities[0].getSuffixes().length )] );
            }
        }
        return artifactStack;
    }
    
    /**
     * @return The altered display name for the given artifact item.
     */
    @Nullable
    public static Component getItemDisplayName( ItemStack itemStack ) {
        CompoundTag stackTag = itemStack.getOrCreateTag();
        
        if( stackTag.contains( TAG_MOD_DATA, Tag.TAG_COMPOUND ) ) {
            CompoundTag modDataTag = stackTag.getCompound( TAG_MOD_DATA );
            
            if( modDataTag.contains( TAG_PREFIX, Tag.TAG_STRING ) && modDataTag.contains( TAG_SUFFIX, Tag.TAG_STRING ) ) {
                return Component.literal(
                        Component.translatable( modDataTag.getString( TAG_PREFIX ) ).getString() + " "
                                + Component.translatable( itemStack.getItem().getDescriptionId( itemStack ) ).getString() + " "
                                + Component.translatable( modDataTag.getString( TAG_SUFFIX ) ).getString()
                );
            }
        }
        return null;
    }
    
    /**
     * Tries to apply a random armor trim to the given ItemStack,
     * if the item is an instance of {@link ArmorItem}.
     */
    public static void applyRandomArmorTrim( LevelReader level, RandomSource random, ItemStack itemStack ) {
        if( !(itemStack.getItem() instanceof ArmorItem) ) return;
        
        try {
            final Registry<TrimPattern> patterns = level.registryAccess().registryOrThrow( Registries.TRIM_PATTERN );
            final Registry<TrimMaterial> materials = level.registryAccess().registryOrThrow( Registries.TRIM_MATERIAL );
            
            Holder.Reference<TrimPattern> randomPattern = patterns.getRandom( random ).orElseThrow();
            Holder.Reference<TrimMaterial> randomMaterial = materials.getRandom( random ).orElseThrow();
            
            ArmorTrim trim = new ArmorTrim( materials.wrapAsHolder( randomMaterial.get() ), patterns.wrapAsHolder( randomPattern.get() ) );
            
            ArmorTrim.setTrim( level.registryAccess(), itemStack, trim );
        }
        catch( Exception e ) {
            MagicalRelics.LOG.error( "Failed to apply random armor trim to artifact armor!" );
        }
    }
    
    /**
     * Applies "mandatory" attribute modifiers to artifacts of
     * a certain artifact category, like randomized damage bonuses for
     * artifact swords and daggers.
     */
    @SuppressWarnings( "ConstantConditions" )
    public static void applyMandatoryAttributeMods( ItemStack itemStack, ArtifactCategory category, RandomSource random ) {
        CompoundTag modDataTag = itemStack.getOrCreateTag().getCompound( TAG_MOD_DATA );
        
        if( category == ArtifactCategory.SWORD || category == ArtifactCategory.DAGGER ) {
            String attackDmgId = ForgeRegistries.ATTRIBUTES.getKey( Attributes.ATTACK_DAMAGE ).toString();
            String attackSpeedId = ForgeRegistries.ATTRIBUTES.getKey( Attributes.ATTACK_SPEED ).toString();
            
            // Attack damage
            CompoundTag attackDmgMod = new CompoundTag();
            attackDmgMod.putString( "AttributeId", attackDmgId );
            attackDmgMod.put( "AttributeMod", new AttributeModifier(
                    Item.BASE_ATTACK_DAMAGE_UUID,
                    "Weapon attack dmg",
                    (double) ((TieredItem) itemStack.getItem()).getTier().getAttackDamageBonus() + 3.0D + (double) (random.nextInt( 3 )),
                    AttributeModifier.Operation.ADDITION
            ).save() );
            attackDmgMod.putString( "ActiveType", AttributeBoost.ActiveType.HELD.getName() );
            
            // Attack speed
            CompoundTag attackSpeed = new CompoundTag();
            attackSpeed.putString( "AttributeId", attackSpeedId );
            attackSpeed.put( "AttributeMod", new AttributeModifier(
                    Item.BASE_ATTACK_SPEED_UUID,
                    "Weapon attack speed",
                    category == ArtifactCategory.DAGGER ? -1.8D : -2.4D,
                    AttributeModifier.Operation.ADDITION
            ).save() );
            attackSpeed.putString( "ActiveType", AttributeBoost.ActiveType.HELD.getName() );
            
            modDataTag.getList( TAG_ATTRIBUTE_MODS, Tag.TAG_COMPOUND ).add( attackDmgMod );
            modDataTag.getList( TAG_ATTRIBUTE_MODS, Tag.TAG_COMPOUND ).add( attackSpeed );
        }
    }
    
    /**
     * @return a Multimap containing any additional attribute modifiers applied by artifact abilities.
     */
    @Nullable
    public static Multimap<Attribute, AttributeModifier> getAttributeMods( ItemStack itemStack, @Nullable AttributeBoost.ActiveType activeType ) {
        CompoundTag stackTag = itemStack.getOrCreateTag();
        
        if( stackTag.contains( TAG_MOD_DATA, Tag.TAG_COMPOUND ) && stackTag.getCompound( TAG_MOD_DATA ).contains( TAG_ATTRIBUTE_MODS, Tag.TAG_LIST ) ) {
            ListTag attributeModsTag = stackTag.getCompound( TAG_MOD_DATA ).getList( TAG_ATTRIBUTE_MODS, Tag.TAG_COMPOUND );
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            
            for( int i = 0; i < attributeModsTag.size(); i++ ) {
                CompoundTag attributeTag = attributeModsTag.getCompound( i );
                
                Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue( ResourceLocation.tryParse( attributeTag.getString( "AttributeId" ) ) );
                AttributeModifier modifier = AttributeUtils.loadUUIDSensitive( attributeTag.getCompound( "AttributeMod" ) );
                AttributeBoost.ActiveType modActiveType = AttributeBoost.ActiveType.getFromName( attributeTag.getString( "ActiveType" ) );
                
                boolean canApply = false;
                
                if( activeType == null ) {
                    canApply = true;
                }
                else if( activeType == AttributeBoost.ActiveType.HELD_OR_EQUIPPED
                        || modActiveType == AttributeBoost.ActiveType.HELD_OR_EQUIPPED ) {
                    canApply = true;
                }
                else if( activeType == modActiveType ) {
                    canApply = true;
                }
                
                if( attribute != null && modifier != null && canApply ) {
                    builder.put( attribute, modifier );
                }
            }
            return builder.build();
        }
        return null;
    }
    
    /**
     * @return An integer representing the texture variant of the given artifact item stack.
     */
    public static int getVariant( ItemStack itemStack ) {
        CompoundTag stackTag = itemStack.getOrCreateTag();
        
        if( !NBTHelper.containsCompound( stackTag, TAG_MOD_DATA )
                || !NBTHelper.containsNumber( stackTag.getCompound( TAG_MOD_DATA ), TAG_VARIANT ) )
            return 1;
        
        return stackTag.getCompound( TAG_MOD_DATA ).getInt( TAG_VARIANT );
    }
    
    
    /**
     * Attempts to apply the given artifact ability instances to the given item stack.
     * <br><br>
     *
     * @param itemStack The item stack to apply the abilities to.
     * @param toApply   The ability instances to apply to the given item stack.
     * @return An array of abilities that were successfully applied. Can be empty!
     */
    public static BaseArtifactAbility<?>[] tryApplyAbilities( ItemStack itemStack, RandomSource random, BaseArtifactAbility<?>... toApply ) {
        if( toApply.length == 0 )
            return new BaseArtifactAbility[0];
        
        final Map<BaseArtifactAbility<?>, TriggerType> currentAbilities = getAllAbilities( itemStack );
        
        // Make sure necessary NBT keys exist on the ItemStack
        final CompoundTag modData = NBTHelper.getOrCreateCompound( itemStack.getOrCreateTag(), TAG_MOD_DATA );
        NBTHelper.putCompoundList( modData, TAG_ABILITY, List.of() );
        NBTHelper.putCompoundList( modData, TAG_ATTRIBUTE_MODS, List.of() );
        
        final List<BaseArtifactAbility<?>> successfullyApplied = new ArrayList<>();
        final List<TriggerType> occupiedTriggers = new ArrayList<>();
        
        for( BaseArtifactAbility<?> nextToApply : toApply ) {
            // Skip if the item already has the ability
            if( currentAbilities.containsKey( nextToApply ) ) continue;
            
            final TriggerType randomTrigger = nextToApply.getRandomTrigger( itemStack, random, itemStack.getItem() instanceof ArmorItem, itemStack.is( MRItemTags.ARTIFACT_CURIOS ) );
            
            // No suitable trigger found, skip to next ability
            if( randomTrigger == null ) continue;
            // Continue if we already applied an ability with this trigger type, and it is not stackable
            if( occupiedTriggers.contains( randomTrigger ) && !randomTrigger.canStack() ) continue;
            
            final ResourceLocation abilityId = MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getKey( nextToApply );
            
            // Make sure the ability actually exists in the registry before applying
            if( abilityId == null ) {
                MagicalRelics.LOG.warn( "Attempted applying an ability with no ID to an artifact. Problematic ability: {}", nextToApply );
                continue;
            }
            // Success, probably
            CompoundTag abilityData = new CompoundTag();
            abilityData.putString( "AbilityId", abilityId.toString() );
            abilityData.putString( "TriggerType", randomTrigger.getName() );
            modData.getList( TAG_ABILITY, Tag.TAG_COMPOUND ).add( abilityData );
            nextToApply.onAbilityAttached( itemStack, random );
            successfullyApplied.add( nextToApply );
            occupiedTriggers.add( randomTrigger );
            
            // Save any ability attribute modifiers to NBT
            AttributeBoost boost = nextToApply.getAttributeWithBoost();
            
            if( boost != null ) {
                // noinspection ConstantConditions
                String attributeId = ForgeRegistries.ATTRIBUTES.getKey( boost.attribute().get() ).toString();
                CompoundTag attributeMod = new CompoundTag();
                
                attributeMod.putString( "AttributeId", attributeId );
                attributeMod.put( "AttributeMod", new AttributeModifier(
                        boost.name(),
                        boost.valueProvider().getRangedValue( random ),
                        boost.operation()
                ).save() );
                attributeMod.putString( "ActiveType", boost.activeType().getName() );
                modData.getList( TAG_ATTRIBUTE_MODS, Tag.TAG_COMPOUND ).add( attributeMod );
            }
        }
        return successfullyApplied.toArray( new BaseArtifactAbility[0] );
    }
    
    /**
     * Attempts to remove the specified ability from the artifact item.
     *
     * @return True if nothing went horribly wrong.
     */
    @SuppressWarnings( "ConstantConditions" )
    public static boolean removeAbility( @Nonnull ItemStack artifact, @Nonnull BaseArtifactAbility<?> ability ) {
        try {
            final CompoundTag modData = NBTHelper.getOrCreateCompound( artifact.getOrCreateTag(), TAG_MOD_DATA );
            final String abilityId = MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getKey( ability ).toString();
            
            final List<CompoundTag> abilityList = NBTHelper.getCompoundList( modData, TAG_ABILITY );
            
            // Remove the ability from the item stack's NBT.
            abilityList.removeIf( ( compoundTag ) -> compoundTag.getString( "AbilityId" ).equals( abilityId ) );
            NBTHelper.putCompoundList( modData, TAG_ABILITY, abilityList );
            
            final List<CompoundTag> attributeBoostList = NBTHelper.getCompoundList( modData, TAG_ATTRIBUTE_MODS );
            
            // Try to remove any attribute modifiers that
            // were added by the ability.
            attributeBoostList.removeIf( ( compoundTag ) -> {
                CompoundTag attributeModTag = compoundTag.getCompound( "AttributeMod" );
                return attributeModTag.getString( "Name" ).equals( ability.getAttributeWithBoost().name() );
            } );
            NBTHelper.putCompoundList( modData, TAG_ATTRIBUTE_MODS, attributeBoostList );
        }
        catch( Exception e ) {
            return false;
        }
        return true;
    }
    
    /**
     * @return The TriggerType associated with the given ability, if present in the ItemStack's NBT.
     * return null otherwise.
     */
    @Nullable
    public static TriggerType getTriggerFromStack( ItemStack artifact, BaseArtifactAbility<?> ability ) {
        Map<BaseArtifactAbility<?>, TriggerType> allAbilities = getAllAbilities( artifact );
        
        for( BaseArtifactAbility<?> abilityToCheck : allAbilities.keySet() ) {
            if( abilityToCheck == ability )
                return allAbilities.get( abilityToCheck );
        }
        return null;
    }
    
    /**
     * @return A List of all abilities on the artifact item stack with the given TriggerType.
     * Will not be null, but may be empty.
     */
    @Nonnull
    public static Collection<BaseArtifactAbility<?>> getAbilitiesWithTrigger( TriggerType type, ItemStack itemStack ) {
        List<BaseArtifactAbility<?>> list = new ArrayList<>();
        
        if( itemStack.isEmpty() ) return list;
        
        Map<BaseArtifactAbility<?>, TriggerType> abilities = getAllAbilities( itemStack );
        
        if( abilities.isEmpty() ) return list;
        
        for( BaseArtifactAbility<?> ability : abilities.keySet() ) {
            if( abilities.get( ability ) == type )
                list.add( ability );
        }
        return list;
    }
    
    /**
     * @return True if the given item stack has the specified ability attached to it.
     */
    public static boolean hasAbility( ItemStack itemStack, BaseArtifactAbility<?> ability ) {
        Map<BaseArtifactAbility<?>, TriggerType> abilities = getAllAbilities( itemStack );
        if( abilities.isEmpty() ) return false;
        return abilities.containsKey( ability );
    }
    
    /**
     * Loops through the given player's curio inventory and checks if
     * the specified ability is present on any item stacks. Note that only
     * the curio slots with the identifiers in {@link ArtifactUtils#CURIO_SLOTS}
     * are checked.
     */
    @SuppressWarnings( "ConstantConditions" )
    public static boolean hasAbilityOnCurio( Player player, BaseArtifactAbility<?> ability ) {
        ICuriosItemHandler curioInventory = CuriosApi.getCuriosInventory( player ).orElse( null );
        
        if( curioInventory != null ) {
            List<SlotResult> slotResults = curioInventory.findCurios( CURIO_SLOTS );
            
            for( SlotResult slotResult : slotResults ) {
                if( hasAbility( slotResult.stack(), ability ) )
                    return true;
            }
        }
        return false;
    }
    
    /**
     * @return A Map of all artifact abilities the given ItemStack has, with their respective TriggerType.
     * Returns an empty Map if no abilities are found.
     */
    @Nonnull
    public static Map<BaseArtifactAbility<?>, TriggerType> getAllAbilities( ItemStack itemStack ) {
        Map<BaseArtifactAbility<?>, TriggerType> abilities = new HashMap<>();
        CompoundTag stackTag = itemStack.getTag();
        
        if( stackTag == null )
            return abilities;
        
        if( !stackTag.contains( TAG_MOD_DATA ) || !stackTag.getCompound( TAG_MOD_DATA ).contains( TAG_ABILITY ) )
            return abilities;
        
        ListTag abilitiesTag = stackTag.getCompound( TAG_MOD_DATA ).getList( TAG_ABILITY, ListTag.TAG_COMPOUND );
        
        for( int i = 0; i < abilitiesTag.size(); i++ ) {
            ResourceLocation abilityId = ResourceLocation.tryParse( abilitiesTag.getCompound( i ).getString( "AbilityId" ) );
            TriggerType triggerType = TriggerType.getFromName( abilitiesTag.getCompound( i ).getString( "TriggerType" ) );
            
            if( MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().containsKey( abilityId ) ) {
                abilities.put( MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getValue( abilityId ), triggerType );
            }
        }
        return abilities;
    }
    
    /**
     * Adds the description of every ability on an artifact item stack to its tooltip.<br><br>
     * Called from:<br><br>
     * {@link com.sarinsa.magical_relics.common.item.ArtifactItem#appendHoverText(ItemStack, Level, List, TooltipFlag)}
     * <br><br>
     * {@link com.sarinsa.magical_relics.common.item.ArtifactArmorItem#appendHoverText(ItemStack, Level, List, TooltipFlag)}
     * <br><br>
     * {@link com.sarinsa.magical_relics.common.item.ArtifactAxeItem#appendHoverText(ItemStack, Level, List, TooltipFlag)}
     * <br><br>
     * {@link com.sarinsa.magical_relics.common.item.DyableArtifactArmorItem#appendHoverText(ItemStack, Level, List, TooltipFlag)}
     */
    public static void addDescriptionsToTooltip( ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag ) {
        Map<BaseArtifactAbility<?>, TriggerType> abilities = ArtifactUtils.getAllAbilities( itemStack );
        
        if( !abilities.isEmpty() ) {
            components.add( Component.literal( " " ) );
            
            for( BaseArtifactAbility<?> ability : abilities.keySet() ) {
                MutableComponent description = ability.getAbilityDescription( getTriggerFromStack( itemStack, ability ), itemStack, level, flag );
                
                if( description != null ) {
                    if( ability.showCooldownSymbol() ) {
                        MutableComponent cooldownComponent = Component.translatable( isAbilityOnCooldown( itemStack, ability ) ? "❄ " : "" ).setStyle( Style.EMPTY.withColor( 0xA3EFFF ) );
                        description.setStyle( ability.getRarity().getStyleModifier().apply( description.getStyle() ) );
                        cooldownComponent.append( description );
                        components.add( cooldownComponent );
                    }
                    else {
                        description.setStyle( ability.getRarity().getStyleModifier().apply( description.getStyle() ) );
                        components.add( description );
                    }
                }
            }
            components.add( Component.literal( " " ) );
        }
    }
    
    /**
     * Puts the specified ability on cooldown for the given artifact
     * using the ability's cooldown config.
     *
     * @param itemStack The artifact item.
     * @param ability   The ability to put on a cooldown.
     */
    public static void setAbilityOnCooldown( ItemStack itemStack, BaseArtifactAbility<? extends CooldownAbilityConfig> ability ) {
        setAbilityCooldown( itemStack, ability, ability.getConfig().COOLDOWN.cooldown.get() );
    }
    
    /**
     * Puts the specified ability on cooldown for the given artifact.
     *
     * @param itemStack The artifact item.
     * @param ability   The ability to put on a cooldown.
     * @param cooldown  The duration of the cooldown in ticks.
     */
    @SuppressWarnings( "ConstantConditions" )
    public static void setAbilityCooldown( ItemStack itemStack, BaseArtifactAbility<?> ability, int cooldown ) {
        CompoundTag modData = itemStack.getOrCreateTag().getCompound( TAG_MOD_DATA );
        
        if( modData.contains( TAG_ABILITY_COOLDOWNS, Tag.TAG_COMPOUND ) ) {
            CompoundTag cooldownsTag = modData.getCompound( TAG_ABILITY_COOLDOWNS );
            String abilityId = MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getKey( ability ).toString();
            
            if( !cooldownsTag.contains( abilityId ) ) {
                cooldownsTag.putInt( abilityId, cooldown );
            }
        }
    }
    
    /**
     * @return True if the given ability is on cooldown for the specified artifact item.
     */
    public static boolean isAbilityOnCooldown( ItemStack itemStack, BaseArtifactAbility<?> ability ) {
        CompoundTag modData = NBTHelper.getOrCreateCompound( itemStack.getOrCreateTag(), TAG_MOD_DATA );
        CompoundTag cooldownData = NBTHelper.getOrCreateCompound( modData, TAG_ABILITY_COOLDOWNS );
        
        // noinspection ConstantConditions
        String abilityId = MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getKey( ability ).toString();
        
        return cooldownData.contains( abilityId );
    }
    
    /**
     * Decrements all ability cooldowns on the ItemStack by the given number.
     * <br><br>
     * Called from {@link com.sarinsa.magical_relics.common.event.ServerEventListener#onServerTick(TickEvent.ServerTickEvent)}.
     */
    @SuppressWarnings( "ConstantConditions" )
    public static void tickAbilityCooldowns( Player player, int decrement ) {
        // Tick player inventory
        for( ItemStack itemStack : player.getInventory().items ) {
            CompoundTag tag = itemStack.getTag();
            
            if( tag == null )
                continue;
            
            if( tag.contains( TAG_MOD_DATA, Tag.TAG_COMPOUND ) && tag.getCompound( TAG_MOD_DATA ).contains( TAG_ABILITY_COOLDOWNS, Tag.TAG_COMPOUND ) ) {
                CompoundTag cooldownTag = tag.getCompound( TAG_MOD_DATA ).getCompound( TAG_ABILITY_COOLDOWNS );
                
                for( String key : cooldownTag.getAllKeys() ) {
                    cooldownTag.putInt( key, cooldownTag.getInt( key ) - decrement );
                }
                cooldownTag.getAllKeys().removeIf( key -> cooldownTag.getInt( key ) <= 0 );
            }
        }
        // Tick curio artifacts on the player
        ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory( player ).orElse( null );
        
        if( curiosInventory != null ) {
            for( SlotResult slotResult : curiosInventory.findCurios( CURIO_SLOTS ) ) {
                CompoundTag tag = slotResult.stack().getTag();
                
                if( tag == null )
                    continue;
                
                if( tag.contains( TAG_MOD_DATA, Tag.TAG_COMPOUND ) && tag.getCompound( TAG_MOD_DATA ).contains( TAG_ABILITY_COOLDOWNS, Tag.TAG_COMPOUND ) ) {
                    CompoundTag cooldownTag = tag.getCompound( TAG_MOD_DATA ).getCompound( TAG_ABILITY_COOLDOWNS );
                    
                    for( String key : cooldownTag.getAllKeys() ) {
                        cooldownTag.putInt( key, cooldownTag.getInt( key ) - decrement );
                    }
                    cooldownTag.getAllKeys().removeIf( key -> cooldownTag.getInt( key ) <= 0 );
                }
            }
        }
    }
    
    /**
     * Clears and repopulates the map of obtainable abilities.
     * <br><br>
     * This gets called when {@link com.sarinsa.magical_relics.common.core.config.MainConfig.General#unobtainableAbilities} changes.
     */
    @SuppressWarnings( "UnstableApiUsage" )
    public static void refreshObtainableAbilities( RegistrySet<BaseArtifactAbility<?>> unobtainable ) {
        OBTAINABLE_ABILITIES.clear();
        
        for( BaseArtifactAbility<?> ability : MRArtifactAbilities.ARTIFACT_ABILITY_REGISTRY.get().getValues() ) {
            if( unobtainable.contains( ability ) )
                continue;
            OBTAINABLE_ABILITIES.add( ability );
        }
    }
}
