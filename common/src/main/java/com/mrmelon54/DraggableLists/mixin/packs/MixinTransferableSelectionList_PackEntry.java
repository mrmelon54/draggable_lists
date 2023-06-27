package com.mrmelon54.DraggableLists.mixin.packs;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrmelon54.DraggableLists.DraggableLists;
import com.mrmelon54.DraggableLists.duck.ResourcePackEntryDuckProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TransferableSelectionList.PackEntry.class)
public abstract class MixinTransferableSelectionList_PackEntry extends ObjectSelectionList.Entry<TransferableSelectionList.PackEntry> implements ResourcePackEntryDuckProvider {
    @Shadow
    @Final
    private PackSelectionModel.Entry pack;
    @Shadow
    @Final
    private TransferableSelectionList parent;
    private boolean isBeingDragged = false;

    @Override
    public PackSelectionModel.Entry getUnderlyingPack() {
        return pack;
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(GuiGraphics guiGraphics, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f, CallbackInfo ci) {
        if (isBeingDragged)
            ci.cancel();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V", ordinal = 5))
    public void removeOnUpArrowButtons(GuiGraphics instance, ResourceLocation resourceLocation, int i, int j, float f, float g, int k, int l, int m, int n) {
        if (DraggableLists.getConfig().disableResourcePackArrows) return;
        instance.blit(resourceLocation, i, j, f, g, k, l, m, n);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V", ordinal = 6))
    public void removeOffUpArrowButtons(GuiGraphics instance, ResourceLocation resourceLocation, int i, int j, float f, float g, int k, int l, int m, int n) {
        if (DraggableLists.getConfig().disableResourcePackArrows) return;
        instance.blit(resourceLocation, i, j, f, g, k, l, m, n);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V", ordinal = 7))
    public void removeOnDownArrowButtons(GuiGraphics instance, ResourceLocation resourceLocation, int i, int j, float f, float g, int k, int l, int m, int n) {
        if (DraggableLists.getConfig().disableResourcePackArrows) return;
        instance.blit(resourceLocation, i, j, f, g, k, l, m, n);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V", ordinal = 8))
    public void removeOffDownArrowButtons(GuiGraphics instance, ResourceLocation resourceLocation, int i, int j, float f, float g, int k, int l, int m, int n) {
        if (DraggableLists.getConfig().disableResourcePackArrows) return;
        instance.blit(resourceLocation, i, j, f, g, k, l, m, n);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void removeMoveTowardEnd(double d, double e, int i, CallbackInfoReturnable<Boolean> cir) {
        if (!DraggableLists.getConfig().disableResourcePackArrows) return;
        if (d > getRectangle().width() / 2f) return;

        double f = d - (double) parent.getRowLeft();
        if (f < 16 || f > 32) return;

        cir.setReturnValue(true);
        cir.cancel();
    }

    @Override
    public void renderPoppedOut(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        if (!isBeingDragged) return;

        isBeingDragged = false;
        guiGraphics.pose().pushPose();

        float z = 191f / 255f;
        RenderSystem.setShaderColor(z, z, z, 0.5f);
        guiGraphics.fill(x - 1, y - 1, x + entryWidth - 9, y + entryHeight + 1, 0xbfbfbfff);
        render(guiGraphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        guiGraphics.pose().popPose();
        isBeingDragged = true;
    }

    @Override
    public void setBeingDragged(boolean v) {
        isBeingDragged = v;
    }
}
