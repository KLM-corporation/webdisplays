package net.montoyo.wd.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.HitResult;
import net.montoyo.wd.registry.ItemRegistry;
import net.montoyo.wd.item.ItemLaserPointer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(at = @At("HEAD"), method = "onPress", cancellable = true)
    public void prePress(long window, int button, int action, int mods, CallbackInfo ci) {
        boolean flag = action == 1;

        Minecraft mc = Minecraft.getInstance();
        if (mc.screen == null) {
            if (
                    mc.player != null && mc.level != null &&
                            mc.player.getItemInHand(InteractionHand.MAIN_HAND).getItem().equals(ItemRegistry.LASER_POINTER.get()) &&
                            (mc.hitResult == null || mc.hitResult.getType() == HitResult.Type.BLOCK || mc.hitResult.getType() == HitResult.Type.MISS)
            ) {
                ItemLaserPointer.press(flag, button);
                ci.cancel();
            }
        }
    }
}
