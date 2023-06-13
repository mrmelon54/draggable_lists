package com.mrmelon54.Draggable.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "draggable-resource-packs")
@Config.Gui.Background("minecraft:textures/block/brain_coral_block.png")
public class ConfigStructure implements ConfigData {
    public boolean disableResourcePackArrows = true;
    public boolean disableServerArrows = true;
}
