package net.ramgames.ecliptrix;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public interface ModTags {

    TagKey<Item> LENSES = registerItemTag("ecliptrix:spyglass_lenses");

    private static TagKey<Item> registerItemTag(String id) {
        return TagKey.of(Registries.ITEM.getKey(),new Identifier(id));
    }

}
