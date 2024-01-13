package com.mrmelon54.DraggableLists;

import com.mrmelon54.DraggableLists.config.ConfigStructure;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;

public class DraggableLists {
    public static final String MOD_ID = "draggable_lists";
    public static final ConfigStructure CONFIG = AutoConfig.register(ConfigStructure.class, Toml4jConfigSerializer::new).get();

    public void onInitializeClient() {
        // no-op
    }
}
