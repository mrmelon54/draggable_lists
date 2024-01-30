package com.mrmelon54.DraggableLists;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class Cursor {
    private static boolean isDragging;

    public static void setDragging() {
        isDragging = true;
        GLFW.glfwSetCursor(Minecraft.getInstance().getWindow().getWindow(), GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR));
    }

    public static void reset() {
        if (!isDragging) return;
        isDragging = false;
        GLFW.glfwSetCursor(Minecraft.getInstance().getWindow().getWindow(), GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR));
    }
}
