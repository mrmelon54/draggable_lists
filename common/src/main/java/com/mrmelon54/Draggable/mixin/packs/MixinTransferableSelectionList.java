package com.mrmelon54.Draggable.mixin.packs;

import com.mrmelon54.Draggable.Draggable;
import com.mrmelon54.Draggable.duck.AbstractPackDuckProvider;
import com.mrmelon54.Draggable.duck.ResourcePackEntryDuckProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TransferableSelectionList.class)
@Environment(EnvType.CLIENT)
public abstract class MixinTransferableSelectionList extends ObjectSelectionList<TransferableSelectionList.PackEntry> {
    private TransferableSelectionList.PackEntry draggingObject = null;
    private double draggingStartX = 0;
    private double draggingStartY = 0;
    private double draggingOffsetX = 0;
    private double draggingOffsetY = 0;
    private long softScrollingTimer = 0;
    private double softScrollingOrigin = 0;

    public MixinTransferableSelectionList(Minecraft minecraft, int i, int j, int k, int l, int m) {
        super(minecraft, i, j, k, l, m);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || draggingObject != null || !isCapMouseY((int) mouseY)) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        draggingObject = this.getEntryAtPosition(mouseX, mouseY);
        if ((draggingObject != null) && (draggingObject instanceof ResourcePackEntryDuckProvider duckProvider) && isValidForDragging(duckProvider)) {
            // Save the mouse origin position and the offset for the top left corner of the widget
            draggingStartX = mouseX;
            draggingStartY = mouseY;
            draggingOffsetX = getRowLeft() - draggingStartX;
            draggingOffsetY = getRowTop(this.children().indexOf(draggingObject)) - draggingStartY;

            // Don't grab if inside the pack icon
            if (draggingOffsetX > -32f) {
                draggingObject = null;
                return super.mouseClicked(mouseX, mouseY, button);
            }

            this.setDragging(true);
            this.setFocused(draggingObject);
            duckProvider.setBeingDragged(true);
            softScrollingTimer = 0;
            GLFW.glfwSetCursor(minecraft.getWindow().getWindow(), GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR));
            super.mouseClicked(mouseX, mouseY, button);
            this.setSelected(null);
            return true;
        }
        draggingObject = null;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.setDragging(false);
        if (draggingObject != null) {
            GLFW.glfwSetCursor(minecraft.getWindow().getWindow(), GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR));
            if (draggingObject instanceof ResourcePackEntryDuckProvider duckProvider)
                duckProvider.setBeingDragged(false);
        }
        draggingObject = null;
        softScrollingTimer = 0;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && updateDragEvent(mouseX, mouseY)) return true;
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (draggingObject != null) return true;
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    boolean updateDragEvent(double mouseX, double mouseY) {
        if (mouseX < x0) {
            draggingStartX = mouseX;
        }
        double y = capYCoordinate((int) mouseY, true);

        TransferableSelectionList.PackEntry hoveredEntry = this.getEntryAtPosition(mouseX, y);

        PackSelectionModel.Entry draggingPack = draggingObject instanceof ResourcePackEntryDuckProvider duckProvider ? duckProvider.getUnderlyingPack() : null;
        PackSelectionModel.Entry hoveredPack = hoveredEntry instanceof ResourcePackEntryDuckProvider duckProvider ? duckProvider.getUnderlyingPack() : null;

        if (draggingPack != null && hoveredPack != null && draggingPack != hoveredPack && draggingObject instanceof ResourcePackEntryDuckProvider underlyingPackProvider) {
            if (draggingPack instanceof AbstractPackDuckProvider abstractPack && dragResourcePack(underlyingPackProvider, draggingStartY, y)) {
                draggingStartY = mouseY;
                int z = abstractPack.getIndexInSelectedList();
                draggingObject = z == -1 ? null : getEntry(z);
                if (draggingObject instanceof ResourcePackEntryDuckProvider duckProvider)
                    duckProvider.setBeingDragged(true);
                this.setSelected(null);
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        if (this.draggingObject instanceof ResourcePackEntryDuckProvider duckProvider) {
            int z = Mth.floor(mouseY + draggingOffsetY);
            int x = Mth.floor(draggingStartX + draggingOffsetX);
            int y = capYCoordinate(z);
            int entryHeight = this.itemHeight - 4;
            int entryWidth = this.getRowWidth();
            duckProvider.renderPoppedOut(guiGraphics, 0, y, x, entryWidth, entryHeight, mouseX, mouseY, false, delta);

            if (y < z) {
                if (softScrollingTimer == 0) {
                    softScrollingTimer = Util.getMillis();
                    softScrollingOrigin = getScrollAmount();
                }
                float f = (float) (Util.getMillis() - softScrollingTimer) / 5f;
                setScrollAmount(softScrollingOrigin + f);
            } else if (y > z) {
                if (softScrollingTimer == 0) {
                    softScrollingTimer = Util.getMillis();
                    softScrollingOrigin = getScrollAmount();
                }
                float f = (float) (Util.getMillis() - softScrollingTimer) / 5f;
                setScrollAmount(softScrollingOrigin - f);
            } else {
                softScrollingTimer = 0;
            }

            updateDragEvent(mouseX, mouseY);
        }
    }

    int capYCoordinate(int y, boolean useScreenSpace) {
        int scrollableTop = y0 + (useScreenSpace ? 0 : (int) Math.max(headerHeight - getScrollAmount() + 2, 0)) + 2;
        int scrollableHeight = y1 - y0 - (useScreenSpace ? 0 : itemHeight + (int) Math.max(headerHeight - getScrollAmount() + 2, 0));
        if (y < scrollableTop) y = scrollableTop;
        if (y > scrollableTop + scrollableHeight) y = scrollableTop + scrollableHeight;
        return y;
    }

    int capYCoordinate(int y) {
        return capYCoordinate(y, false);
    }

    boolean isCapMouseY(int y) {
        return capYCoordinate(y, true) == y;
    }

    boolean dragResourcePack(ResourcePackEntryDuckProvider underlyingPackProvider, double draggingStartY, double mouseY) {
        int m = Mth.floor(mouseY - (double) y0) - this.headerHeight + (int) this.getScrollAmount() - 4;
        int n = m / this.itemHeight;

        PackSelectionModel.Entry pack = underlyingPackProvider.getUnderlyingPack();
        if (pack instanceof AbstractPackDuckProvider duckProvider)
            duckProvider.moveTo(n);

        return true;
    }

    boolean isValidForDragging(ResourcePackEntryDuckProvider resourcePackEntryDuckProvider) {
        return !Draggable.shouldNotTouch(resourcePackEntryDuckProvider.getUnderlyingPack()) && resourcePackEntryDuckProvider.getUnderlyingPack().isSelected();
    }
}