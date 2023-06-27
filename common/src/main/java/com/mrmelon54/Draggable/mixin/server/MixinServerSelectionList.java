package com.mrmelon54.Draggable.mixin.server;

import com.mrmelon54.Draggable.duck.MultiplayerScreenDuckProvider;
import com.mrmelon54.Draggable.duck.ServerEntryDuckProvider;
import com.mrmelon54.Draggable.duck.ServerListDuckProvider;
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

    private ServerSelectionList.OnlineServerEntry draggingObject = null;
    private double draggingStartX = 0;
    private double draggingStartY = 0;
    private double draggingOffsetX = 0;
    private double draggingOffsetY = 0;
    private long softScrollingTimer = 0;
    private double softScrollingOrigin = 0;

    public MixinServerSelectionList(Minecraft minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l, m);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && draggingObject == null && isCapMouseY((int) mouseY)) {
            ServerSelectionList.Entry a = this.getEntryAtPosition(mouseX, mouseY);
            draggingObject = a instanceof ServerSelectionList.OnlineServerEntry b ? b : null;
            if (draggingObject != null && draggingObject instanceof ServerEntryDuckProvider duckProvider) {
                // Save the mouse origin position and the offset for the top left corner of the widget
                draggingStartX = mouseX;
                draggingStartY = mouseY;
                draggingOffsetX = getRowLeft() - draggingStartX;
                draggingOffsetY = getRowTop(this.children().indexOf(draggingObject)) - draggingStartY;

                // Don't grab if inside the server icon
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
                return true;
            } else {
                draggingObject = null;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.setDragging(false);
        if (draggingObject != null) {
            GLFW.glfwSetCursor(minecraft.getWindow().getWindow(), GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR));
            if (draggingObject instanceof ServerEntryDuckProvider duckProvider)
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
        double y = capYCoordinate((int) mouseY, true);

        ServerSelectionList.Entry hoveredEntry = this.getEntryAtPosition(mouseX, y);

        ServerData draggingPack = draggingObject instanceof ServerEntryDuckProvider duckProvider ? duckProvider.getUnderlyingServer() : null;
        ServerData hoveredPack = hoveredEntry instanceof ServerEntryDuckProvider duckProvider ? duckProvider.getUnderlyingServer() : null;

        if (draggingPack != null && hoveredPack != null && draggingPack != hoveredPack && draggingObject instanceof ServerEntryDuckProvider serverEntryDuckProvider) {
            if (dragServerItem(serverEntryDuckProvider, draggingStartY, y)) {
                draggingStartY = mouseY;
                if (screen instanceof MultiplayerScreenDuckProvider multiplayerScreenDuckProvider) {
                    int z = multiplayerScreenDuckProvider.getIndexOfServerInfo(serverEntryDuckProvider.getUnderlyingServer());
                    draggingObject = z == -1 ? null : (getEntry(z) instanceof ServerSelectionList.OnlineServerEntry b ? b : null);
                    if (draggingObject instanceof ServerEntryDuckProvider duckProvider)
                        duckProvider.setBeingDragged(true);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        if (this.draggingObject instanceof ServerEntryDuckProvider duckProvider) {
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
        int scrollableTop = y0 + 4;
        int scrollableHeight = y1 - y0 - (useScreenSpace ? 2 : itemHeight + 2);
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

    boolean dragServerItem(ServerEntryDuckProvider underlyingServerProvider, double draggingStartY, double mouseY) {
        if (screen instanceof MultiplayerScreenDuckProvider multiplayerScreenDuckProvider) {
            int i = multiplayerScreenDuckProvider.getIndexOfServerInfo(underlyingServerProvider.getUnderlyingServer());
            if (i == -1) return false;

            int m = Mth.floor(mouseY - (double) this.y0) - this.headerHeight + (int) this.getScrollAmount() - 4;
            int n = m / this.itemHeight;

            if (n >= 0 && n < onlineServers.size()) {
                moveServerEntry(i, n);
                return true;
            }
        }
        return false;
    }

    void moveServerEntry(int a, int b) {
        ServerList servers = this.screen.getServers();
        if (servers instanceof ServerListDuckProvider duckProvider) {
            ServerData serverData = servers.get(a);
            servers.remove(serverData);
            duckProvider.add(b, serverData);
            servers.save();
            updateOnlineServers(servers);
        }
    }
}