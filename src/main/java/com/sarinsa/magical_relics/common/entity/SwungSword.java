package com.sarinsa.magical_relics.common.entity;

import com.sarinsa.magical_relics.common.core.registry.MRDamageTypes;
import com.sarinsa.magical_relics.common.core.registry.MREntities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;

public class SwungSword extends Entity {

    private static final EntityDataAccessor<ItemStack> SWORD_ITEM = SynchedEntityData.defineId(SwungSword.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Direction> ATTACK_DIRECTION = SynchedEntityData.defineId(SwungSword.class, EntityDataSerializers.DIRECTION);

    private int lifespan = 0;
    private boolean hasSwung = false;


    public SwungSword(EntityType<?> type, Level level) {
        super(type, level);
    }

    public SwungSword(Level level, double x, double y, double z) {
        this(MREntities.SWUNG_SWORD.get(), level);
        setPos(x, y, z);
    }


    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(SWORD_ITEM, new ItemStack(Items.IRON_SWORD));
        entityData.define(ATTACK_DIRECTION, Direction.NORTH);
    }

    public void setSwordItem(SwordItem item) {
        if (item == null) return;
        entityData.set(SWORD_ITEM, new ItemStack(item));
    }

    public ItemStack getSwordItem() {
        return entityData.get(SWORD_ITEM);
    }

    public void setAttackDirection(Direction direction) {
        if (direction == null) return;
        entityData.set(ATTACK_DIRECTION, direction);
    }

    public Direction getAttackDirection() {
        return entityData.get(ATTACK_DIRECTION);
    }

    public int getLifespan() {
        return lifespan;
    }

    @Override
    public void tick() {
        if (++lifespan >= 10) {
            discard();
        }
        if (!hasSwung) {
            performAttack();
            hasSwung = true;
        }
    }

    private void performAttack() {
        if (!level().isClientSide) {
            level().playSound(null, blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.BLOCKS, 1.0F, 1.0F);

            AABB aabb = new AABB(blockPosition()).inflate(0.2D);
            float damage = 1.0F;

            if (getSwordItem().getItem() instanceof SwordItem swordItem) {
                damage = swordItem.getDamage();
            }

            for (LivingEntity livingEntity : level().getEntitiesOfClass(LivingEntity.class, aabb)) {
                livingEntity.hurt(MRDamageTypes.of(level(), MRDamageTypes.SWUNG_SWORD), damage);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damage) {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
