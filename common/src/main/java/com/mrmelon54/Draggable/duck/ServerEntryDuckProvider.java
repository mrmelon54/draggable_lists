package com.mrmelon54.Draggable.duck;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ServerData;

public interface ServerEntryDuckProvider {
    ServerData getUnderlyingServer();

    void renderPoppedOut(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta);

    void setBeingDragged(boolean v);
}
