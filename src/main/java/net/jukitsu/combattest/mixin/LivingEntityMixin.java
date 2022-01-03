package net.jukitsu.combattest.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract double getAttributeValue(Attribute attr);


    @Shadow public abstract boolean isDamageSourceBlocked(DamageSource damageSource);

    @Shadow @Nullable public abstract DamageSource getLastDamageSource();

    @Shadow public abstract boolean isUsingItem();

    @Shadow protected ItemStack useItem;

    @Shadow public abstract ItemStack getItemInHand(InteractionHand interactionHand);

    @Shadow protected abstract void blockUsingShield(LivingEntity livingEntity);

    @Shadow protected abstract void actuallyHurt(DamageSource damageSource, float f);

    @Shadow protected abstract float getDamageAfterMagicAbsorb(DamageSource damageSource, float f);

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

    @Inject(method = "hurt", at = @At("HEAD"))
    public void damageThroughShield(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir)
    {
        float h = 0.0F;
        Entity entity2;
        if(f > 0.0F && this.isDamageSourceBlocked(damageSource))
        {
            h = Math.min(this.getBlockingItem().getTagElement("BlockEntityTag") != null ? 10.0F : 5.0F, f);
            if (!damageSource.isProjectile() && !damageSource.isExplosion()) {
                entity2 = damageSource.getDirectEntity();
                if (entity2 instanceof LivingEntity) {
                    this.blockUsingShield((LivingEntity)entity2);
                }
            } else {
                h = f;
            }
            f-=h;
            
            this.actuallyHurt(damageSource, f);

        }
    }

    @Overwrite
    public boolean isBlocking()
    {
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
