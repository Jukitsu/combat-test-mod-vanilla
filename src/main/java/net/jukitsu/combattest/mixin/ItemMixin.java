package net.jukitsu.combattest.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Shadow public abstract Item asItem();

    @Inject(method = "getUseDuration", at = @At(value = "HEAD"), cancellable = true)
    public void changeUseDuration(ItemStack itemStack, CallbackInfoReturnable<Integer> cir)
    {

      if((Object) this instanceof BowlFoodItem)  cir.setReturnValue(20);

    }

}
