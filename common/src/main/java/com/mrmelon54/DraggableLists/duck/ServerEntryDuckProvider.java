package com.mrmelon54.DraggableLists.duck;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ServerData;

public interface ServerEntryDuckProvider {
    ServerData draggable_lists$getUnderlyingServer();

    void draggable_lists$renderPoppedOut(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta);

    void draggable_lists$setBeingDragged(boolean v);
}
