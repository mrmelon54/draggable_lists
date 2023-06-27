package com.mrmelon54.DraggableLists.quilt;

import com.mrmelon54.DraggableLists.fabriclike.DraggableListsFabricLike;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class DraggableListsQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        DraggableListsFabricLike.init();
    }
}
