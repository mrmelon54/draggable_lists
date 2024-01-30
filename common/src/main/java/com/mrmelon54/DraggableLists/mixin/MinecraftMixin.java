package com.mrmelon54.DraggableLists.mixin;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    public abstract Window getWindow();

    @Inject(method = "setScreen", at = @At("HEAD"))
    private void injectedSetScreen(Screen screen, CallbackInfo ci) {
        // reset cursor when screen changes
        GLFW.glfwSetCursor(getWindow().getWindow(), GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR));
    }
}
