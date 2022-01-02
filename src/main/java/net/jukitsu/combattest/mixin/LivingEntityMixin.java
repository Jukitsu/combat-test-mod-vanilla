package net.jukitsu.combattest.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract double getAttributeValue(Attribute attr);

    @Overwrite
    public void knockback(double d, double e, double f) {
        d *= 1.0D - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        if (!(d <= 0.0D)) {
            this.hasImpulse = true;
            Vec3 vec3 = this.getDeltaMovement();
            Vec3 vec32 = (new Vec3(e, 0.0D, f)).normalize().scale(d);
            this.setDeltaMovement(vec3.x / 2.0D - vec32.x, this.onGround ? Math.min(0.5D, d * 0.9) : vec3.y + Math.min(0.75D, d * 0.5), vec3.z / 2.0D - vec32.z);
        }
    }

    @Inject(method="actuallyHurt", at=@At("HEAD"))
    protected void cancelInvulnerabilityTimer(DamageSource damageSource, float f, CallbackInfo info) {
        if (!this.isInvulnerableTo(damageSource) && damageSource.getEntity() instanceof Player) {
            this.invulnerableTime = 0;
        }
    }

}
