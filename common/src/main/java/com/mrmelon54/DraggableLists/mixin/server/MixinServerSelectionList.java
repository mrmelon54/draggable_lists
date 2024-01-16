package com.mrmelon54.DraggableLists.mixin.server;

import com.mrmelon54.DraggableLists.duck.MultiplayerScreenDuckProvider;
import com.mrmelon54.DraggableLists.duck.ServerEntryDuckProvider;
import com.mrmelon54.DraggableLists.duck.ServerListDuckProvider;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(ServerSelectionList.class)
public abstract class MixinServerSelectionList extends ObjectSelectionList<ServerSelectionList.Entry> {
    @Shadow
    @Final
    private JoinMultiplayerScreen screen;

    @Shadow
    public abstract void setSelected(@Nullable ServerSelectionList.Entry entry);

    @Shadow
    @Final
    private List<ServerSelectionList.OnlineServerEntry> onlineServers;

    @Shadow
    public abstract void updateOnlineServers(ServerList serverList);

    @Unique
    private ServerSelectionList.OnlineServerEntry draggable_lists$draggingObject = null;
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

    public MixinServerSelectionList(Minecraft minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && draggable_lists$draggingObject == null && draggable_lists$isCapMouseY((int) mouseY)) {
            ServerSelectionList.Entry a = this.getEntryAtPosition(mouseX, mouseY);
            draggable_lists$draggingObject = a instanceof ServerSelectionList.OnlineServerEntry b ? b : null;
            if (draggable_lists$draggingObject != null && draggable_lists$draggingObject instanceof ServerEntryDuckProvider duckProvider) {
                // Save the mouse origin position and the offset for the top left corner of the widget
                draggable_lists$draggingStartX = mouseX;
                draggable_lists$draggingStartY = mouseY;
                draggable_lists$draggingOffsetX = getRowLeft() - draggable_lists$draggingStartX;
                draggable_lists$draggingOffsetY = getRowTop(this.children().indexOf(draggable_lists$draggingObject)) - draggable_lists$draggingStartY;

                // Don't grab if inside the server icon
                if (draggable_lists$draggingOffsetX > -32f) {
                    draggable_lists$draggingObject = null;
                    return super.mouseClicked(mouseX, mouseY, button);
                }

                this.setDragging(true);
                this.setFocused(draggable_lists$draggingObject);
                duckProvider.draggable_lists$setBeingDragged(true);
                draggable_lists$softScrollingTimer = 0;
                GLFW.glfwSetCursor(minecraft.getWindow().getWindow(), GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR));
                super.mouseClicked(mouseX, mouseY, button);
                return true;
            } else {
                draggable_lists$draggingObject = null;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.setDragging(false);
        if (draggable_lists$draggingObject != null) {
            GLFW.glfwSetCursor(minecraft.getWindow().getWindow(), GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR));
            if (draggable_lists$draggingObject instanceof ServerEntryDuckProvider duckProvider)
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
        double y = draggable_lists$capYCoordinate((int) mouseY, true);

        ServerSelectionList.Entry hoveredEntry = this.getEntryAtPosition(mouseX, y);

        ServerData draggingPack = draggable_lists$draggingObject instanceof ServerEntryDuckProvider duckProvider ? duckProvider.draggable_lists$getUnderlyingServer() : null;
        ServerData hoveredPack = hoveredEntry instanceof ServerEntryDuckProvider duckProvider ? duckProvider.draggable_lists$getUnderlyingServer() : null;

        if (draggingPack != null && hoveredPack != null && draggingPack != hoveredPack && draggable_lists$draggingObject instanceof ServerEntryDuckProvider serverEntryDuckProvider) {
            if (draggable_lists$dragServerItem(serverEntryDuckProvider, y)) {
                draggable_lists$draggingStartY = mouseY;
                if (screen instanceof MultiplayerScreenDuckProvider multiplayerScreenDuckProvider) {
                    int z = multiplayerScreenDuckProvider.draggable_lists$getIndexOfServerInfo(serverEntryDuckProvider.draggable_lists$getUnderlyingServer());
                    draggable_lists$draggingObject = z == -1 ? null : (getEntry(z) instanceof ServerSelectionList.OnlineServerEntry b ? b : null);
                    if (draggable_lists$draggingObject instanceof ServerEntryDuckProvider duckProvider)
                        duckProvider.draggable_lists$setBeingDragged(true);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.renderWidget(guiGraphics, mouseX, mouseY, delta);

        if (this.draggable_lists$draggingObject instanceof ServerEntryDuckProvider duckProvider) {
            int z = Mth.floor(mouseY + draggable_lists$draggingOffsetY);
            int x = Mth.floor(draggable_lists$draggingStartX + draggable_lists$draggingOffsetX);
            int y = draggable_lists$capYCoordinate(z);
            int entryHeight = this.itemHeight - 4;
            int entryWidth = this.getRowWidth();
            duckProvider.draggable_lists$renderPoppedOut(guiGraphics, 0, y, x, entryWidth, entryHeight, mouseX, mouseY, false, delta);

            if (y < z) {
                if (draggable_lists$softScrollingTimer == 0) {
                    draggable_lists$softScrollingTimer = Util.getMillis();
                    draggable_lists$softScrollingOrigin = getScrollAmount();
                }
                float f = (float) (Util.getMillis() - draggable_lists$softScrollingTimer) / 5f;
                setScrollAmount(draggable_lists$softScrollingOrigin + f);
            } else if (y > z) {
                if (draggable_lists$softScrollingTimer == 0) {
                    draggable_lists$softScrollingTimer = Util.getMillis();
                    draggable_lists$softScrollingOrigin = getScrollAmount();
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
        int scrollableTop = getY() + 4;
        int scrollableHeight = getBottom() - getY() - (useScreenSpace ? 2 : itemHeight + 2);
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
    boolean draggable_lists$dragServerItem(ServerEntryDuckProvider underlyingServerProvider, double mouseY) {
        if (screen instanceof MultiplayerScreenDuckProvider multiplayerScreenDuckProvider) {
            int i = multiplayerScreenDuckProvider.draggable_lists$getIndexOfServerInfo(underlyingServerProvider.draggable_lists$getUnderlyingServer());
            if (i == -1) return false;

            int m = Mth.floor(mouseY - (double) this.getY()) - this.headerHeight + (int) this.getScrollAmount() - 4;
            int n = m / this.itemHeight;

            if (n >= 0 && n < onlineServers.size()) {
                draggable_lists$moveServerEntry(i, n);
                return true;
            }
        }
        return false;
    }

    @Unique
    void draggable_lists$moveServerEntry(int a, int b) {
        ServerList servers = this.screen.getServers();
        if (servers instanceof ServerListDuckProvider duckProvider) {
            ServerData serverData = servers.get(a);
            servers.remove(serverData);
            duckProvider.draggable_lists$add(b, serverData);
            servers.save();
            updateOnlineServers(servers);
        }
    }
}