package net.jukitsu.combattest.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(BowItem.class)
public abstract class BowItemMixin {

    //add bow fatigue by using a method from 1.16 8c

    public ItemStack bow;
    public int i;
    public float power;


    @Shadow
    public abstract int getUseDuration(ItemStack itemStack);

    public float getFatigueForTime(int i) {
        if (i < 60) {
            return 0.5F;
        } else {
            return i >= 200 ? 10.5F : 0.5F + 10.0F * (float) (i - 60) / 140.0F;
        }
    }


    @Redirect(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;setCritArrow(Z)V"))
    private void applyBowFatigue(AbstractArrow arrow, boolean bl) {

        int j = this.getUseDuration(bow) - i;
        arrow.setCritArrow(getFatigueForTime(j) <= 0.5F && power == 1.0F);

    }

    @Redirect(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;shootFromRotation(Lnet/minecraft/world/entity/Entity;FFFFF)V"))
    private void applyFatigueMissing(AbstractArrow instance, Entity entity, float v, float v1, float v2, float v3, float v4) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            int j = this.getUseDuration(bow) - i;
            float g = getFatigueForTime(j);
            instance.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 0.25F * g);

        }


    }

    @Inject(method = "releaseUsing", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i, CallbackInfo info) {
        bow = itemStack;
        this.i = i;
        int j = this.getUseDuration(bow) - i;
        power = BowItem.getPowerForTime(j);

    }
}


