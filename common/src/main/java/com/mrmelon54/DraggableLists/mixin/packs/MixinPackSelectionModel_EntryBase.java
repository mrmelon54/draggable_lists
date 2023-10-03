package com.mrmelon54.DraggableLists.mixin.packs;

import com.mrmelon54.DraggableLists.DraggableLists;
import com.mrmelon54.DraggableLists.duck.AbstractPackDuckProvider;
import com.mrmelon54.DraggableLists.duck.ResourcePackOrganizerDuckProvider;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.stream.Stream;

@Mixin(PackSelectionModel.EntryBase.class)
public abstract class MixinPackSelectionModel_EntryBase implements AbstractPackDuckProvider {
    @Shadow
    protected abstract List<Pack> getSelfList();

    @Shadow
    @Final
    private Pack pack;

    @Shadow
    @Dynamic("field_25460 is provided by PackSelectionModel.EntryBase but has no mapping")
    @Final
    private PackSelectionModel field_25460;

    @Override
    public void draggable_lists$moveTo(int j) {
        if (DraggableLists.shouldNotTouch(pack)) return;

        List<Pack> list = getSelfList();
        list.remove(pack);
        list.add(j, pack);

        // get all fixed position resource packs, remove them and add them all at the end
        Stream<Pack> packStream = list.stream().filter(DraggableLists::shouldNotTouch);
        List<Pack> list1 = packStream.toList();
        list.removeAll(list1);
        list.addAll(list1);

        if (field_25460 instanceof ResourcePackOrganizerDuckProvider duckProvider)
            duckProvider.draggable_lists$updateSelectedList();
    }

    @Override
    public int draggable_lists$getIndexInSelectedList() {
        return getSelfList().indexOf(pack);
    }
}
