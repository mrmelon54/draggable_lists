package com.mrmelon54.DraggableLists.fabric;

import com.mrmelon54.DraggableLists.fabriclike.DraggableListsFabricLike;
import net.fabricmc.api.ModInitializer;

public class DraggableListsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        DraggableListsFabricLike.init();
    }
}
