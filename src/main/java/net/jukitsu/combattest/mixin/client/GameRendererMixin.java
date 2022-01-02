package net.jukitsu.combattest.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow @Final
    private Minecraft minecraft;

    @Inject(method="pick", at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/BlockHitResult;miss(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/BlockHitResult;"), locals= LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    public void checkReachBeforeMissing(float f, CallbackInfo info, Entity entity, double d, Vec3 vec3, boolean bl, int i, double e, Vec3 vec32, Vec3 vec33, float g, AABB aabb, EntityHitResult entityHitResult, Entity entity2, Vec3 vec34, double h) {
        if ((double)this.minecraft.player.getAttackStrengthScale(1.0F) >= 0.95D) {
            if (h > 16.0D) {
                this.minecraft.hitResult = BlockHitResult.miss(vec34, Direction.getNearest(vec32.x, vec32.y, vec32.z), new BlockPos(vec34));
            } else if (h < e || this.minecraft.hitResult == null) {
                this.minecraft.hitResult = entityHitResult;
                if (entity2 instanceof LivingEntity || entity2 instanceof ItemFrame) {
                    this.minecraft.crosshairPickEntity = entity2;
                }
                this.minecraft.getProfiler().pop();
                info.cancel();
            }
        }
    }
}
