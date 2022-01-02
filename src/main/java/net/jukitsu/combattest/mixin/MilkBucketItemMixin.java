package net.jukitsu.combattest.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MilkBucketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MilkBucketItem.class)
public class MilkBucketItemMixin {
    @Inject(method = "getUseDuration", at = @At(value = "HEAD"), cancellable = true)
    public void changeUseDuration(ItemStack itemStack, CallbackInfoReturnable<Integer> cir)
    {

        cir.setReturnValue(20);

    }

}
