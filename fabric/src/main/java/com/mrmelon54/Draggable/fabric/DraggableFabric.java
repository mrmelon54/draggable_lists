package com.mrmelon54.Draggable.fabric;

import com.mrmelon54.Draggable.fabriclike.DraggableFabricLike;
import net.fabricmc.api.ModInitializer;

public class DraggableFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        DraggableFabricLike.init();
    }
}
