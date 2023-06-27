package com.mrmelon54.DraggableLists.forge;

import com.mrmelon54.DraggableLists.DraggableLists;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DraggableLists.MOD_ID)
public class DraggableListsForge {
    public DraggableListsForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(DraggableLists.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory.class, () -> new ConfigScreenFactory((mc, screen) -> DraggableLists.createConfigScreen(screen).get()));
        DraggableLists.init();
    }
}
