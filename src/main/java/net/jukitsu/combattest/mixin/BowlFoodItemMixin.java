package net.jukitsu.combattest.mixin;

import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class BowlFoodItemMixin {
    @Inject(method = "getUseDuration", at = @At(value = "HEAD"), cancellable = true)
    public void changeUseDuration(ItemStack itemStack, CallbackInfoReturnable<Integer> cir)
    {

      if((Object) this instanceof BowlFoodItem)  cir.setReturnValue(20);

    }

}
