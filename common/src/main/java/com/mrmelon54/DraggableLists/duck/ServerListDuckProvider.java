package com.mrmelon54.DraggableLists.duck;

import com.mrmelon54.DraggableLists.DragItem;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;

public interface ServerListDuckProvider {
    void draggable_lists$moveItem(DragItem<ServerData, ServerSelectionList.OnlineServerEntry> item, int position);
}
