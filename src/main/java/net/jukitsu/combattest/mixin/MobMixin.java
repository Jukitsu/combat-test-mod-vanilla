package net.jukitsu.combattest.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin {

@Inject(method = "doHurtTarget", at = @At(value = "HEAD"))
public void interruptEating(Entity entity, CallbackInfoReturnable<Boolean> cir)
{
    if(entity instanceof Player)
    {
        Player player = (Player) entity;

        if(player.isUsingItem() && player.getUseItem().isEdible()) player.stopUsingItem();
    }
}


}
