package com.mrmelon54.DraggableLists.forge;

import com.mrmelon54.DraggableLists.DraggableLists;
import com.mrmelon54.DraggableLists.config.ConfigStructure;
import me.shedaniel.autoconfig.AutoConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.neoforged.fml.common.Mod;

@Mod(DraggableLists.MOD_ID)
public class DraggableListsForge extends DraggableLists {
    public DraggableListsForge(ModContainer container) {
        container.registerExtensionPoint(ConfigScreenFactory.class,
                () -> new ConfigScreenFactory((mc, screen) -> AutoConfig.getConfigScreen(ConfigStructure.class, screen).get())
        );

        onInitializeClient();
    }
}
