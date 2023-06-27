package com.mrmelon54.DraggableLists.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "draggable_lists")
@Config.Gui.Background("minecraft:textures/block/brain_coral_block.png")
public class ConfigStructure implements ConfigData {
    public boolean disableResourcePackArrows = true;
    public boolean disableServerArrows = true;
}
