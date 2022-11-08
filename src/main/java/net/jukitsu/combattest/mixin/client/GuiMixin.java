package net.jukitsu.combattest.mixin.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin extends GuiComponent {

    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract boolean canRenderCrosshairForSpectator(HitResult hitResult);

    @Shadow private int screenWidth;

    @Shadow private int screenHeight;

    @Inject(method = "renderCrosshair", at = @At("HEAD"))
    public void preventIndicatorRender(PoseStack poseStack, CallbackInfo ci)
{

}

@Overwrite
private void renderCrosshair(PoseStack poseStack) {
    Options options = this.minecraft.options;
    if (options.getCameraType().isFirstPerson()) {
        if (this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || this.canRenderCrosshairForSpectator(this.minecraft.hitResult)) {
            if (options.renderDebug && !options.hideGui && !this.minecraft.player.isReducedDebugInfo() && !options.reducedDebugInfo) {
                Camera camera = this.minecraft.gameRenderer.getMainCamera();
                PoseStack poseStack2 = RenderSystem.getModelViewStack();
                poseStack2.pushPose();
                poseStack2.translate((double) (this.screenWidth / 2), (double) (this.screenHeight / 2), (double) this.getBlitOffset());
                poseStack2.mulPose(Vector3f.XN.rotationDegrees(camera.getXRot()));
                poseStack2.mulPose(Vector3f.YP.rotationDegrees(camera.getYRot()));
                poseStack2.scale(-1.0F, -1.0F, -1.0F);
                RenderSystem.applyModelViewMatrix();
                RenderSystem.renderCrosshair(10);
                poseStack2.popPose();
                RenderSystem.applyModelViewMatrix();
            } else {
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                //int camera = true;
                this.blit(poseStack, (this.screenWidth - 15) / 2, (this.screenHeight - 15) / 2, 0, 0, 15, 15);
                if (this.minecraft.options.attackIndicator == AttackIndicatorStatus.CROSSHAIR) {
                    if ((double) this.minecraft.player.getAttackStrengthScale(1.0F) >= 0.7D) {
                        float poseStack2 = this.minecraft.player.getAttackStrengthScale(0.0F);
                        boolean bl = false;
                        if (this.minecraft.crosshairPickEntity != null && this.minecraft.crosshairPickEntity instanceof LivingEntity && poseStack2 >= 1.0F) {
                            bl = this.minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0F;
                            bl &= this.minecraft.crosshairPickEntity.isAlive();
                        }

                        int i = this.screenHeight / 2 - 7 + 16;
                        int j = this.screenWidth / 2 - 8;
                        if (bl) {
                            this.blit(poseStack, j, i, 68, 94, 16, 16);
                        } else if (poseStack2 < 1.0F) {
                            int k = (int) (poseStack2 * 17.0F);
                            this.blit(poseStack, j, i, 36, 94, 16, 4);
                            this.blit(poseStack, j, i, 52, 94, k, 4);
                        }
                    }
                }

            }
        }
    }
}
}
