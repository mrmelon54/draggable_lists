package com.mrmelon54.DraggableLists.mixin.packs;

import com.mrmelon54.DraggableLists.Cursor;
import com.mrmelon54.DraggableLists.duck.AbstractPackDuckProvider;
import com.mrmelon54.DraggableLists.duck.ResourcePackEntryDuckProvider;
import com.mrmelon54.DraggableLists.mixin.accessor.AbstractScrollAreaAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TransferableSelectionList.class)
@Environment(EnvType.CLIENT)
public abstract class TransferableSelectionListMixin extends ObjectSelectionList<TransferableSelectionList.PackEntry> {
    @Unique
    private TransferableSelectionList.PackEntry draggable_lists$draggingObject = null;
    @Unique
    private double draggable_lists$draggingStartX = 0;
    @Unique
    private double draggable_lists$draggingStartY = 0;
    @Unique
    private double draggable_lists$draggingOffsetX = 0;
    @Unique
    private double draggable_lists$draggingOffsetY = 0;
    @Unique
    private long draggable_lists$softScrollingTimer = 0;
    @Unique
    private double draggable_lists$softScrollingOrigin = 0;

    public TransferableSelectionListMixin(Minecraft minecraft, int i, int j, int k, int l, int m) {
        super(minecraft, i, j, k, l);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.updateScrolling(mouseX, mouseY, button);
        if (((AbstractScrollAreaAccessor)this).isScrolling() || button != 0 || draggable_lists$draggingObject != null || !draggable_lists$isCapMouseY((int) mouseY)) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        draggable_lists$draggingObject = this.getEntryAtPosition(mouseX, mouseY);
        if ((draggable_lists$draggingObject != null) && (draggable_lists$draggingObject instanceof ResourcePackEntryDuckProvider duckProvider) && draggable_lists$isValidForDragging(duckProvider)) {
            // Save the mouse origin position and the offset for the top left corner of the widget
            draggable_lists$draggingStartX = mouseX;
            draggable_lists$draggingStartY = mouseY;
            draggable_lists$draggingOffsetX = getRowLeft() - draggable_lists$draggingStartX;
            draggable_lists$draggingOffsetY = getRowTop(this.children().indexOf(draggable_lists$draggingObject)) - draggable_lists$draggingStartY;

            // Don't grab if inside the pack icon
            if (draggable_lists$draggingOffsetX > -32f) {
                draggable_lists$draggingObject = null;
                return super.mouseClicked(mouseX, mouseY, button);
            }

            this.setDragging(true);
            this.setFocused(draggable_lists$draggingObject);
            duckProvider.draggable_lists$setBeingDragged(true);
            draggable_lists$softScrollingTimer = 0;
            Cursor.setDragging();
            super.mouseClicked(mouseX, mouseY, button);
            this.setSelected(null);
            return true;
        }
        draggable_lists$draggingObject = null;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.setDragging(false);
        if (draggable_lists$draggingObject != null) {
            Cursor.reset();
            if (draggable_lists$draggingObject instanceof ResourcePackEntryDuckProvider duckProvider)
                duckProvider.draggable_lists$setBeingDragged(false);
        }
        draggable_lists$draggingObject = null;
        draggable_lists$softScrollingTimer = 0;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && draggable_lists$updateDragEvent(mouseX, mouseY)) return true;
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (draggable_lists$draggingObject != null) return true;
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Unique
    boolean draggable_lists$updateDragEvent(double mouseX, double mouseY) {
        if (mouseX < getX()) {
            draggable_lists$draggingStartX = mouseX;
        }
        double y = draggable_lists$capYCoordinate((int) mouseY, true);

        TransferableSelectionList.PackEntry hoveredEntry = this.getEntryAtPosition(mouseX, y);

        PackSelectionModel.Entry draggingPack = draggable_lists$draggingObject instanceof ResourcePackEntryDuckProvider duckProvider ? duckProvider.draggable_lists$getUnderlyingPack() : null;
        PackSelectionModel.Entry hoveredPack = hoveredEntry instanceof ResourcePackEntryDuckProvider duckProvider ? duckProvider.draggable_lists$getUnderlyingPack() : null;

        if (draggingPack != null && hoveredPack != null && draggingPack != hoveredPack && draggable_lists$draggingObject instanceof ResourcePackEntryDuckProvider underlyingPackProvider) {
            if (draggingPack instanceof AbstractPackDuckProvider abstractPack && draggable_lists$dragResourcePack(underlyingPackProvider, y)) {
                draggable_lists$draggingStartY = mouseY;
                int z = abstractPack.draggable_lists$getIndexInSelectedList();
                draggable_lists$draggingObject = z == -1 ? null : getEntry(z);
                if (draggable_lists$draggingObject instanceof ResourcePackEntryDuckProvider duckProvider)
                    duckProvider.draggable_lists$setBeingDragged(true);
                this.setSelected(null);
                return true;
            }
        }
        return false;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.renderWidget(guiGraphics, mouseX, mouseY, delta);

        if (this.draggable_lists$draggingObject instanceof ResourcePackEntryDuckProvider duckProvider) {
            int z = Mth.floor(mouseY + draggable_lists$draggingOffsetY);
            int x = Mth.floor(draggable_lists$draggingStartX + draggable_lists$draggingOffsetX);
            int y = draggable_lists$capYCoordinate(z);
            int entryHeight = this.itemHeight - 4;
            int entryWidth = this.getRowWidth();
            duckProvider.draggable_lists$renderPoppedOut(guiGraphics, 0, y, x, entryWidth, entryHeight, mouseX, mouseY, false, delta);

            if (y < z) {
                if (draggable_lists$softScrollingTimer == 0) {
                    draggable_lists$softScrollingTimer = Util.getMillis();
                    draggable_lists$softScrollingOrigin = scrollAmount();
                }
                float f = (float) (Util.getMillis() - draggable_lists$softScrollingTimer) / 5f;
                setScrollAmount(draggable_lists$softScrollingOrigin + f);
            } else if (y > z) {
                if (draggable_lists$softScrollingTimer == 0) {
                    draggable_lists$softScrollingTimer = Util.getMillis();
                    draggable_lists$softScrollingOrigin = scrollAmount();
                }
                float f = (float) (Util.getMillis() - draggable_lists$softScrollingTimer) / 5f;
                setScrollAmount(draggable_lists$softScrollingOrigin - f);
            } else {
                draggable_lists$softScrollingTimer = 0;
            }

            draggable_lists$updateDragEvent(mouseX, mouseY);
        }
    }

    @Unique
    int draggable_lists$capYCoordinate(int y, boolean useScreenSpace) {
        int scrollableTop = getY() + (useScreenSpace ? 0 : (int) Math.max(headerHeight - scrollAmount() + 2, 0)) + 2;
        int scrollableHeight = getBottom() - getY() - (useScreenSpace ? 0 : itemHeight + (int) Math.max(headerHeight - scrollAmount() + 2, 0));
        if (y < scrollableTop) y = scrollableTop;
        if (y > scrollableTop + scrollableHeight) y = scrollableTop + scrollableHeight;
        return y;
    }

    @Unique
    int draggable_lists$capYCoordinate(int y) {
        return draggable_lists$capYCoordinate(y, false);
    }

    @Unique
    boolean draggable_lists$isCapMouseY(int y) {
        return draggable_lists$capYCoordinate(y, true) == y;
    }

    @Unique
    boolean draggable_lists$dragResourcePack(ResourcePackEntryDuckProvider underlyingPackProvider, double mouseY) {
        int m = Mth.floor(mouseY - (double) getY()) - this.headerHeight + (int) this.scrollAmount() - 4;
        int n = m / this.itemHeight;

        PackSelectionModel.Entry pack = underlyingPackProvider.draggable_lists$getUnderlyingPack();
        if (pack instanceof AbstractPackDuckProvider duckProvider)
            duckProvider.draggable_lists$moveTo(n);

        return true;
    }

    @Unique
    boolean draggable_lists$isValidForDragging(ResourcePackEntryDuckProvider resourcePackEntryDuckProvider) {
        PackSelectionModel.Entry entry = resourcePackEntryDuckProvider.draggable_lists$getUnderlyingPack();
        return !entry.isFixedPosition() && entry.isSelected();
    }
}