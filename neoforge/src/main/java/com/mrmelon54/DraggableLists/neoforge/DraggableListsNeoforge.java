package com.mrmelon54.DraggableLists.neoforge;

import com.mrmelon54.DraggableLists.DraggableLists;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(DraggableLists.MOD_ID)
public class DraggableListsNeoforge {
    public DraggableListsNeoforge() {
        if (!FMLEnvironment.dist.isClient())
            return;

        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (client, parent) -> DraggableLists.createConfigScreen(parent).get());
        DraggableLists.init();
    }
}
