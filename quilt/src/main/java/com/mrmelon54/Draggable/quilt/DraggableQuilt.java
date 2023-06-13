package com.mrmelon54.Draggable.quilt;

import com.mrmelon54.Draggable.fabriclike.DraggableFabricLike;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class DraggableQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        DraggableFabricLike.init();
    }
}
