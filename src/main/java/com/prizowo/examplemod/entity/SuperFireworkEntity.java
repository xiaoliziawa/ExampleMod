package com.prizowo.examplemod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;

public class SuperFireworkEntity extends FireworkRocketEntity {
    private int life;
    private int lifetime;
    private boolean hasExploded = false;
    
    public SuperFireworkEntity(EntityType<? extends FireworkRocketEntity> type, Level level) {
        super(type, level);
        this.life = 0;
    }

    public SuperFireworkEntity(Level level, double x, double y, double z, ItemStack stack) {
        super(level, x, y, z, stack);
        this.life = 0;
        this.lifetime = 40 + this.random.nextInt(6) + this.random.nextInt(7);
    }

    @Override
    public void tick() {
        super.tick();
        
        ++this.life;
        
        if (this.life >= this.lifetime && !hasExploded) {
            hasExploded = true;
            if (!this.level().isClientSide) {
                this.level().broadcastEntityEvent(this, (byte)17);
                this.gameEvent(GameEvent.EXPLODE, this.getOwner());
            }
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
    }

}