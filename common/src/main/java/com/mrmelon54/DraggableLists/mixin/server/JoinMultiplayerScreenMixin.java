package com.mrmelon54.DraggableLists.mixin.server;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JoinMultiplayerScreen.class)
public abstract class JoinMultiplayerScreenMixin extends Screen {
    @Shadow
    protected ServerSelectionList serverSelectionList;

    protected JoinMultiplayerScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "joinSelectedServer", at = @At("HEAD"))
    private void injectedConnect(CallbackInfo info) {
        if (serverSelectionList.isDragging()) serverSelectionList.mouseReleased(0, 0, 0);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (serverSelectionList.isDragging()) serverSelectionList.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        if (serverSelectionList.isDragging()) serverSelectionList.mouseReleased(0, 0, 0);
        super.onClose();
    }
}
