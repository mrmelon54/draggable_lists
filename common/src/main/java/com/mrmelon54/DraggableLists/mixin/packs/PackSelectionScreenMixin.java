package com.mrmelon54.DraggableLists.mixin.packs;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PackSelectionScreen.class)
public class PackSelectionScreenMixin extends Screen {
    @Shadow
    private TransferableSelectionList selectedPackList;

    @Shadow
    private TransferableSelectionList availablePackList;

    protected PackSelectionScreenMixin(Component component) {
        super(component);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (selectedPackList.isMouseOver(d, e)) return selectedPackList.mouseClicked(d, e, i);
        if (availablePackList.isMouseOver(d, e)) return availablePackList.mouseClicked(d, e, i);
        return super.mouseClicked(d, e, i);
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (selectedPackList.isMouseOver(d, e)) return selectedPackList.mouseDragged(d, e, i, f, g);
        if (availablePackList.isMouseOver(d, e)) return availablePackList.mouseDragged(d, e, i, f, g);
        return super.mouseDragged(d, e, i, f, g);
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        if (selectedPackList.isDragging()) selectedPackList.mouseReleased(d, e, i);
        if (availablePackList.isDragging()) availablePackList.mouseReleased(d, e, i);
        return super.mouseReleased(d, e, i);
    }

    @Override
    public void onClose() {
        if (selectedPackList.isDragging()) selectedPackList.mouseReleased(0, 0, 0);
        if (availablePackList.isDragging()) availablePackList.mouseReleased(0, 0, 0);
        super.onClose();
    }
}
