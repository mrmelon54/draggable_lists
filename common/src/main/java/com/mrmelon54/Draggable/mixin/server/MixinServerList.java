package com.mrmelon54.Draggable.mixin.server;

import com.mrmelon54.Draggable.duck.ServerListDuckProvider;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ServerList.class)
public class MixinServerList implements ServerListDuckProvider {
    @Shadow
    @Final
    private List<ServerData> serverList;

    @Override
    public void add(int index, ServerData serverData) {
        serverList.add(index, serverData);
    }
}
