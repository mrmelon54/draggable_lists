package com.mrmelon54.DraggableLists.mixin.server;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrmelon54.DraggableLists.DraggableLists;
import com.mrmelon54.DraggableLists.duck.ServerEntryDuckProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public abstract class MixinOnlineServerEntry extends ObjectSelectionList.Entry<ServerSelectionList.Entry> implements ServerEntryDuckProvider {
    @Shadow
    @Final
    private ServerData serverData;

    @Shadow
    public abstract void render(GuiGraphics guiGraphics, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f);

    @Shadow
    @Final
    private ServerSelectionList field_19117;
    @Shadow
    @Final
    private JoinMultiplayerScreen screen;
    @Shadow
    private long lastClickTime;

    @Shadow
    public abstract boolean mouseClicked(double d, double e, int i);

    private boolean isBeingDragged;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(GuiGraphics guiGraphics, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f, CallbackInfo ci) {
        if (isBeingDragged) ci.cancel();
    }

    @Override
    public ServerData getUnderlyingServer() {
        return serverData;
    }

    @Override
    public void renderPoppedOut(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        if (!isBeingDragged) return;

        isBeingDragged = false;
        guiGraphics.pose().pushPose();

        float z = 191f / 255f;
        RenderSystem.setShaderColor(z, z, z, 0.5f);
        guiGraphics.fill(x - 1, y - 1, x + entryWidth - 2, y + entryHeight + 1, 0xbfbfbfff);
        render(guiGraphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        guiGraphics.pose().popPose();
        isBeingDragged = true;
    }

    @Override
    public void setBeingDragged(boolean v) {
        isBeingDragged = v;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V", ordinal = 3))
    public void removeUpOnButton(GuiGraphics instance, ResourceLocation resourceLocation, int i, int j, float f, float g, int k, int l, int m, int n) {
        if (DraggableLists.getConfig().disableServerArrows) return;
        instance.blit(resourceLocation, i, j, f, g, k, l, m, n);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V", ordinal = 4))
    public void removeUpButton(GuiGraphics instance, ResourceLocation resourceLocation, int i, int j, float f, float g, int k, int l, int m, int n) {
        if (DraggableLists.getConfig().disableServerArrows) return;
        instance.blit(resourceLocation, i, j, f, g, k, l, m, n);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V", ordinal = 5))
    public void removeDownOnButton(GuiGraphics instance, ResourceLocation resourceLocation, int i, int j, float f, float g, int k, int l, int m, int n) {
        if (DraggableLists.getConfig().disableServerArrows) return;
        instance.blit(resourceLocation, i, j, f, g, k, l, m, n);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V", ordinal = 6))
    public void removeDownButton(GuiGraphics instance, ResourceLocation resourceLocation, int i, int j, float f, float g, int k, int l, int m, int n) {
        if (DraggableLists.getConfig().disableServerArrows) return;
        instance.blit(resourceLocation, i, j, f, g, k, l, m, n);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void removeSwapEntries(double d, double e, int i, CallbackInfoReturnable<Boolean> cir) {
        double l = field_19117.getRowLeft();
        double f = d - l;

        // don't click buttons left of 16 pixels
        if (f <= 16) {
            mouseClicked(l + 32, e, i);
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
