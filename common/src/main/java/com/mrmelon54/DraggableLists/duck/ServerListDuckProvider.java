package com.mrmelon54.DraggableLists.duck;

import net.minecraft.client.multiplayer.ServerData;

public interface ServerListDuckProvider {
    void add(int index, ServerData serverInfo);
}
