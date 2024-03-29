package net.jukitsu.combattest.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Inject(method = "startAttack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;attack(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;)V"),
            cancellable = true)
    private void cancelAttack(CallbackInfoReturnable info) {
        if (this.player.getAttackStrengthScale(1.0f) < 0.5) {
            info.cancel();
        }
    }

    @Inject(method = "startAttack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V"),
            cancellable = true)
    private void cancelSwing(CallbackInfoReturnable info) {
        if (this.player.getAttackStrengthScale(1.0f) < 0.2) {
            //note: value here has temporarily been changed to 0.2 just to experiment.  This number will keep getting tweaked until we get proper 4 tick swing delay
            info.cancel();
        }
    }
}
