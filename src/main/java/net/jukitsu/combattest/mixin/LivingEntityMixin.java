package net.jukitsu.combattest.mixin;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    protected ItemStack useItem;
    @Shadow
    @Nullable
    private DamageSource lastDamageSource;


    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract double getAttributeValue(Attribute attr);

    @Shadow
    public abstract boolean isDamageSourceBlocked(DamageSource damageSource);


    @Shadow
    public abstract boolean isUsingItem();


    @Shadow
    protected abstract void actuallyHurt(DamageSource damageSource, float f);


    @Shadow public abstract boolean isDeadOrDying();

    @Shadow protected abstract boolean checkTotemDeathProtection(DamageSource damageSource);

    @Shadow @Nullable protected abstract SoundEvent getDeathSound();

    @Shadow protected abstract float getSoundVolume();

    @Shadow public abstract float getVoicePitch();

    @Shadow public abstract void die(DamageSource damageSource);

    @Shadow protected abstract void playHurtSound(DamageSource damageSource);

    @Shadow @Nullable public abstract DamageSource getLastDamageSource();

    @Shadow @Final private CombatTracker combatTracker;

    @Shadow public abstract float getHealth();

    @Shadow protected abstract void hurtCurrentlyUsedShield(float f);

    @Shadow public abstract ItemStack getUseItem();

    @Shadow public abstract void stopUsingItem();

    @Shadow public float hurtDir;

    @Shadow public float yBodyRot;


    @Inject(at = @At("HEAD"), method = "hurt")
    public void interruptEating(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir)
    {
        if(f <= 0.5 || damageSource.isFire()) return;

        if (this.isUsingItem() && (this.getUseItem().getUseAnimation() == UseAnim.EAT || this.getUseItem().getUseAnimation() == UseAnim.DRINK)) {
            this.stopUsingItem();
        }
    }

    @Overwrite
    public void knockback(double d, double e, double f) {

        d *= 1.0D - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);


        ItemStack itemStack = this.getBlockingItem();
        if (!itemStack.isEmpty()) {
            double g = itemStack.getTagElement("BlockEntityTag") != null ? 0.8 : 0.5;
            d = Math.min(1.0D, d + g);
        }


        if (!(d <= 0.0D)) {
            this.hasImpulse = true;
            Vec3 vec3 = this.getDeltaMovement();
            Vec3 vec32 = (new Vec3(e, 0.0D, f)).normalize().scale(d);
            this.setDeltaMovement(-vec32.x, Math.min(1.0D, Math.max(0.0D, vec3.y) / 2.0D + d * 0.6D), -vec32.z);
        }
    }

    @Inject(method = "actuallyHurt", at = @At("HEAD"))
    protected void cancelInvulnerabilityTimer(DamageSource damageSource, float f, CallbackInfo info) {
        if (!this.isInvulnerableTo(damageSource) && damageSource.getEntity() instanceof Player) {

                this.invulnerableTime = 5;

        }
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void damageThroughShield(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        float h;
        Entity entity2 = damageSource.getDirectEntity();
        boolean bl = true;
        float g = f;
        if (this.isUsingItem() && (this.getUseItem().getUseAnimation() == UseAnim.EAT || this.getUseItem().getUseAnimation() == UseAnim.DRINK)) {
            this.stopUsingItem();
        }

        if (f > 0.0F && this.isDamageSourceBlocked(damageSource)) {

            h = Math.min(this.getBlockingItem().getTagElement("BlockEntityTag") != null ? 10.0F : 5.0F, f);
            if (!damageSource.isProjectile() && !damageSource.isExplosion()) {
                entity2 = damageSource.getDirectEntity();
                if (entity2 instanceof LivingEntity) {
                    this.hurtCurrentlyUsedShield(f);
                    this.blockUsingShield((LivingEntity) entity2);
                }
            } else {
                h = f;
            }
            f-=h;
            this.combatTracker.recordDamage(damageSource, this.getHealth(), f);
            this.actuallyHurt(damageSource,f);
            this.level.broadcastEntityEvent(this, (byte)29);
            boolean bl2 = true;
            if (this.isDeadOrDying()) {
                if (!this.checkTotemDeathProtection(damageSource)) {
                    SoundEvent soundEvent = this.getDeathSound();
                    if (bl2 && soundEvent != null) {
                        this.playSound(soundEvent, this.getSoundVolume(), this.getVoicePitch());
                    }

                    this.die(damageSource);
                }
            } else if (bl2) {
                this.playHurtSound(damageSource);
            }

            if ((Object) this instanceof ServerPlayer) {
                CriteriaTriggers.ENTITY_HURT_PLAYER.trigger( (ServerPlayer) (Object) this, damageSource, g, f, bl);
                if (h > 0.0F && h < 3.4028235E37F) {
                    ((ServerPlayer) (Object) this).awardStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(h * 10.0F));
                }
            }

            if (entity2 instanceof ServerPlayer) {
                CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((ServerPlayer)entity2, this, damageSource, g, f, bl);
            }


            cir.cancel();
        }
    }

    @Overwrite
    public void blockUsingShield(LivingEntity livingEntity) {
        // Fix Shield knockback
        this.blockedByShield(livingEntity);
    }
    @Overwrite
    public void blockedByShield(LivingEntity livingEntity) {
        // To do: remove shield knockback
        livingEntity.knockback(0.5D, this.getX() - livingEntity.getX(), this.getZ() - livingEntity.getZ());
    }

    @Overwrite
    public boolean isBlocking() {
        return !this.getBlockingItem().isEmpty();
    }

    public ItemStack getBlockingItem() {
        if (this.isUsingItem() && !this.useItem.isEmpty()) {
            Item item = this.useItem.getItem();
            if (item.getUseAnimation(this.useItem) == UseAnim.BLOCK) {
                return this.useItem;
            }
        } //else if ((this.isOnGround() && this.isCrouching() || this.isPassenger())) {
        //this should only be for if shield crouching is enabled, a setting for this should be added later, for now it will be on by default
//            ItemStack itemStack = this.getItemInHand(InteractionHand.OFF_HAND);
//            if (!itemStack.isEmpty() && itemStack.getItem().getUseAnimation(itemStack) == UseAnim.BLOCK) {
//                return itemStack;
//            }
//        }

        return ItemStack.EMPTY;
    }

}
