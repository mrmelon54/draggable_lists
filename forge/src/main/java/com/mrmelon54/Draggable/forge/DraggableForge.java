package com.mrmelon54.Draggable.forge;

import dev.architectury.platform.forge.EventBuses;
import com.mrmelon54.Draggable.Draggable;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Draggable.MOD_ID)
public class DraggableForge {
    public DraggableForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Draggable.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory.class, () -> new ConfigScreenFactory((mc, screen) -> Draggable.createConfigScreen(screen).get()));

        Draggable.init();
    }
}
