package com.mrmelon54.Draggable.duck;

import net.minecraft.client.multiplayer.ServerData;

public interface ServerListDuckProvider {
    void add(int index, ServerData serverInfo);
}
