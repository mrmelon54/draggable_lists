package com.mrmelon54.DraggableLists;

import com.mrmelon54.DraggableLists.config.ConfigStructure;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.server.packs.repository.Pack;

import java.util.function.Supplier;

public class DraggableLists {
    public static final String MOD_ID = "draggable_lists";
    private static ConfigStructure config;

    public static ConfigStructure getConfig() {
        return config;
    }

    public static void init() {
        AutoConfig.register(ConfigStructure.class, JanksonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ConfigStructure.class).getConfig();
    }

    public static Supplier<Screen> createConfigScreen(Screen screen) {
        return AutoConfig.getConfigScreen(ConfigStructure.class, screen);
    }

    public static boolean shouldNotTouch(Pack pack) {
        return pack.isFixedPosition() || pack.isRequired() || pack.getId().equals("vanilla");
    }

    public static boolean shouldNotTouch(PackSelectionModel.Entry pack) {
        return pack.isFixedPosition() || pack.isRequired() || pack.getId().equals("vanilla");
    }
}
