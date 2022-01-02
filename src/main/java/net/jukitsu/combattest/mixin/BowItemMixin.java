package net.jukitsu.combattest.mixin;

import net.minecraft.world.entity.LivingEntity;
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

@Mixin(BowItem.class)
public class BowItemMixin {

    //add bow fatigue by using a method from 1.16 8c

    @Shadow public int getUseDuration(ItemStack itemStack) {
        return 72000;
    }

    ItemStack bow;
    int i;

     float getFatigueForTime(int i) {
        if (i < 60) {
            return 0.5F;
        } else {
            return i >= 200 ? 10.5F : 0.5F + 10.0F * (float)(i - 60) / 140.0F;
        }
    }

    @Redirect(method= "releaseUsing", at = @At(value="INVOKE", target="Lnet/minecraft/world/entity/projectile/AbstractArrow;setCritArrow(Z)V"))
    private void applyBowFatigue(AbstractArrow arrow, boolean bl){

        int j = this.getUseDuration(bow) - i;
        if(getFatigueForTime(j) > 0.5d)
        {
            arrow.setCritArrow(false);

        }

    }

    @Inject(method = "releaseUsing", at = @At("HEAD"))
    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i, CallbackInfo info)
    {
        bow = itemStack;
        this.i = i;
    }
}


