package xieao.theora.common.item;

import net.minecraft.item.Item;
import xieao.theora.client.render.item.ItemRenderer;

public class ItemBase extends Item {

    public ItemBase() {
        setTileEntityItemStackRenderer(
                new ItemRenderer()
        );
    }
}
