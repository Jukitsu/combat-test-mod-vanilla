package net.jukitsu.combattest.mixin;


import com.mojang.authlib.GameProfile;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    public PlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(EntityType.PLAYER, level);
    }

    @Shadow
    public abstract float getAttackStrengthScale(float f);

    @Shadow
    public abstract void resetAttackStrengthTicker();

    @Shadow
    public abstract void attack(Entity e);


    private boolean isAttackAvailable(float f) {
        return this.getAttackStrengthScale(f) > 0.5;
    }

    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 2)
    private float cancelAttackStrengthScale(float x) {
        return 1.0F;
    }

    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 3)
    private boolean doSweepAttack(boolean x) {
        return x && getAttackStrengthScale(1.0F) > 0.95F && EnchantmentHelper.getSweepingDamageRatio(this) > 0.0F;
    }


    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;resetAttackStrengthTicker()V"))
    public void cancelAttackReset(Player player) {

    }

    @Redirect(method="attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;distanceToSqr(Lnet/minecraft/world/entity/Entity;)D"))
    public double modifyHitReg(Player player, Entity entity) {
        return 0.0D; // Temporary cancelling hitreg check
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    public void cancelAttack(Entity entity, CallbackInfo info) {
        if (!this.isAttackAvailable(1.0F)) {
            info.cancel();
        }
    }

    @Inject(method = "attack", at = @At("TAIL"))
    public void resetAttackTime(Entity entity, CallbackInfo info) {
        this.resetAttackStrengthTicker();
    }

    @Inject(method="actuallyHurt", at=@At("HEAD"))
    private void modifyPlayerInvulnerability(DamageSource damageSource, float f, CallbackInfo ci) {
        if (!this.isInvulnerableTo(damageSource)) {
            this.invulnerableTime = 5;
        }
    }

}
