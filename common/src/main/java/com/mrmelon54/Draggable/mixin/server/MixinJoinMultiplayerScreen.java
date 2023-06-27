package com.mrmelon54.Draggable.mixin.server;

import com.mrmelon54.Draggable.duck.MultiplayerScreenDuckProvider;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JoinMultiplayerScreen.class)
public abstract class MixinJoinMultiplayerScreen extends Screen implements MultiplayerScreenDuckProvider {
    @Shadow
    protected ServerSelectionList serverSelectionList;

    @Shadow
    public abstract ServerList getServers();

    protected MixinJoinMultiplayerScreen(Component component) {
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

    @Override
    public int getIndexOfServerInfo(ServerData serverData) {
        ServerList servers = getServers();
        for (int i = 0; i < servers.size(); i++)
            if (servers.get(i) == serverData) return i;
        return -1;
    }
}
