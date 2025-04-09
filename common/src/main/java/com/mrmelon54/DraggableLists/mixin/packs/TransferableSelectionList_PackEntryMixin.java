package com.mrmelon54.DraggableLists.mixin.packs;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mrmelon54.DraggableLists.DragItem;
import com.mrmelon54.DraggableLists.DraggableLists;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(TransferableSelectionList.PackEntry.class)
public abstract class TransferableSelectionList_PackEntryMixin extends ObjectSelectionList.Entry<TransferableSelectionList.PackEntry> implements DragItem<PackSelectionModel.Entry, TransferableSelectionList.PackEntry> {
    @Shadow
    @Final
    private TransferableSelectionList parent;

    @Shadow
    @Final
    private PackSelectionModel.Entry pack;

    @Unique
    private boolean draggable_lists$isBeingDragged = false;

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 4))
    public boolean removeOnUpArrowButtons(GuiGraphics instance, Function<ResourceLocation, RenderType> function, ResourceLocation resourceLocation, int i, int j, int k, int l) {
        return !DraggableLists.CONFIG.disableResourcePackArrows;
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 5))
    public boolean removeOffUpArrowButtons(GuiGraphics instance, Function<ResourceLocation, RenderType> function, ResourceLocation resourceLocation, int i, int j, int k, int l) {
        return !DraggableLists.CONFIG.disableResourcePackArrows;
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 6))
    public boolean removeOnDownArrowButtons(GuiGraphics instance, Function<ResourceLocation, RenderType> function, ResourceLocation resourceLocation, int i, int j, int k, int l) {
        return !DraggableLists.CONFIG.disableResourcePackArrows;
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 7))
    public boolean removeOffDownArrowButtons(GuiGraphics instance, Function<ResourceLocation, RenderType> function, ResourceLocation resourceLocation, int i, int j, int k, int l) {
        return !DraggableLists.CONFIG.disableResourcePackArrows;
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void removeMoveTowardEnd(double d, double e, int i, CallbackInfoReturnable<Boolean> cir) {
        if (!DraggableLists.CONFIG.disableResourcePackArrows) return;

        // Allow using the whole pack selection button
        if (pack.canSelect()) return;

        double f = d - (double) parent.getRowLeft();
        if (f < 16 || f > 32) return;

        cir.setReturnValue(true);
        cir.cancel();
    }

    @Override
    public PackSelectionModel.Entry draggable_lists$getUnderlyingData() {
        return pack;
    }

    @Override
    public TransferableSelectionList.PackEntry draggable_lists$getUnderlyingEntry() {
        return (TransferableSelectionList.PackEntry) (Object) this;
    }

    @Override
    public void draggable_lists$render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        if (!draggable_lists$isBeingDragged) return;
        render(guiGraphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
    }

    @Override
    public void draggable_lists$setBeingDragged(boolean v) {
        draggable_lists$isBeingDragged = v;
    }
}
