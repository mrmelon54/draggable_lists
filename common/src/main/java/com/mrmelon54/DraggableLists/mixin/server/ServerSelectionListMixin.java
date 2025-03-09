package com.mrmelon54.DraggableLists.mixin.server;

import com.mrmelon54.DraggableLists.DragItem;
import com.mrmelon54.DraggableLists.DragList;
import com.mrmelon54.DraggableLists.DragManager;
import com.mrmelon54.DraggableLists.duck.ServerListDuckProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerSelectionList.class)
@Environment(EnvType.CLIENT)
public abstract class ServerSelectionListMixin extends ObjectSelectionList<ServerSelectionList.Entry> implements DragList<ServerData, ServerSelectionList.OnlineServerEntry> {
    @Shadow
    @Final
    private JoinMultiplayerScreen screen;

    @Shadow
    public abstract void setSelected(@Nullable ServerSelectionList.Entry entry);

    @Shadow
    public abstract void updateOnlineServers(ServerList serverList);

    @Shadow
    public abstract int getRowWidth();

    @Unique
    private final DragManager<ServerData, ServerSelectionList.OnlineServerEntry> draggable_lists$dragManager = new DragManager<>(this);

    public ServerSelectionListMixin(Minecraft minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l);
    }

    @Override
    protected void renderListItems(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
        draggable_lists$dragManager.renderListItems(guiGraphics, mouseX, mouseY, tickDelta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (draggable_lists$dragManager.mouseClicked(mouseX, mouseY, button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggable_lists$dragManager.mouseReleased(mouseX, mouseY, button)) return true;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggable_lists$dragManager.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true;
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (draggable_lists$dragManager.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) return true;
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.renderWidget(guiGraphics, mouseX, mouseY, delta);
        draggable_lists$dragManager.renderWidget(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public DragItem<ServerData, ServerSelectionList.OnlineServerEntry> draggable_lists$getEntryAtPosition(double mouseX, double mouseY) {
        ServerSelectionList.Entry entryAtPosition = getEntryAtPosition(mouseX, mouseY);
        if (entryAtPosition instanceof ServerSelectionList.OnlineServerEntry onlineServerEntry) {
            return (DragItem<ServerData, ServerSelectionList.OnlineServerEntry>) onlineServerEntry;
        }
        return null;
    }

    public int draggable_lists$getIndexOfEntry(DragItem<ServerData, ServerSelectionList.OnlineServerEntry> selectedItem) {
        return children().indexOf(selectedItem.draggable_lists$getUnderlyingEntry());
    }

    public void draggable_lists$setDragging(boolean b) {
        super.setDragging(b);
    }

    public int draggable_lists$getHeaderHeight() {
        return headerHeight;
    }

    public int draggable_lists$getY() {
        return getY();
    }

    public int draggable_lists$getBottom() {
        return getBottom();
    }

    public int draggable_lists$getItemHeight() {
        return itemHeight;
    }

    public int draggable_lists$getRowTop(int i) {
        return getRowTop(i);
    }

    public int draggable_lists$getRowBottom(int i) {
        return getRowBottom(i);
    }

    public double draggable_lists$getRowLeft() {
        return getRowLeft();
    }

    public int draggable_lists$getRowWidth() {
        return getRowWidth();
    }

    public double draggable_lists$getScrollAmount() {
        return getScrollAmount();
    }

    public void draggable_lists$setScrollAmount(double v) {
        setScrollAmount(v);
    }

    public void draggable_lists$moveEntry(DragItem<ServerData, ServerSelectionList.OnlineServerEntry> item, int n) {
        ServerList servers = screen.getServers();
        if (servers instanceof ServerListDuckProvider duckProvider) {
            duckProvider.draggable_lists$moveItem(item, n);
            servers.save();
            updateOnlineServers(servers);
        }
    }

    public int draggable_lists$getItemCount() {
        return getItemCount();
    }

    public void draggable_lists$renderItem(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta, int i, int rowLeft, int rowTop, int rowWidth, int rowHeight) {
        renderItem(guiGraphics, mouseX, mouseY, tickDelta, i, rowLeft, rowTop, rowWidth, rowHeight);
    }
}