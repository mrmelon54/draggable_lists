package com.mrmelon54.DraggableLists.config;

import net.minecraft.client.gui.screens.Screen;

public enum EnabledMode {
    DISABLED,
    REQUIRES_MODIFIER,
    ENABLED;

    public boolean isEnabled() {
        return switch (this) {
            case DISABLED -> false;
            case REQUIRES_MODIFIER -> Screen.hasShiftDown();
            case ENABLED -> true;
        };
    }
}
