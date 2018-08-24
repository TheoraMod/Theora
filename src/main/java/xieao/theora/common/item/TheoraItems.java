package xieao.theora.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import xieao.theora.common.block.IGenericBlock;
import xieao.theora.common.block.TheoraBlocks;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TheoraItems {

    public static final Set<Item> ITEMS = new HashSet<>();

    static {
        for (Block block : TheoraBlocks.BLOCKS) {
            if (block instanceof IGenericBlock) {
                IGenericBlock gBlock = (IGenericBlock) block;
                ResourceLocation location = block.getRegistryName();
                Objects.requireNonNull(location);
                register(gBlock.getItemBlock(), location.toString());
            }
        }
    }

    public static <T extends Item> T register(T item, String name) {
        item.setRegistryName(name);
        item.setUnlocalizedName(name);
        ITEMS.add(item);
        return item;
    }
}
