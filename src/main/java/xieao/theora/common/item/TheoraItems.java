package xieao.theora.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import xieao.theora.Theora;
import xieao.theora.common.block.IGenericBlock;
import xieao.theora.common.block.TheoraBlocks;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TheoraItems {

    public static final Set<Item> ITEMS = new HashSet<>();

    public static final ItemOoze OOZE;
    public static final ItemWand WAND;
    public static final ItemPigCoin PIG_COIN;
    public static final ItemPigCoinBag PIG_COIN_BAG;
    public static final ItemAcidVial ACID_VIAL;

    static {
        OOZE = register(new ItemOoze(), "ooze");
        WAND = register(new ItemWand(), "wand");
        PIG_COIN = register(new ItemPigCoin(), "pigcoin");
        PIG_COIN_BAG = register(new ItemPigCoinBag(), "pigcoinbag");
        ACID_VIAL = register(new ItemAcidVial(), "acidvial");

        for (Block block : TheoraBlocks.BLOCKS) {
            if (block instanceof IGenericBlock) {
                IGenericBlock block1 = (IGenericBlock) block;
                ResourceLocation location = block.getRegistryName();
                Objects.requireNonNull(location);
                register(block1.getItemBlock(), location.toString());
            }
        }
    }

    public static <T extends Item & IGenericItem> T register(T item, String name) {
        item.setRegistryName(name);
        item.setUnlocalizedName(name);
        item.setCreativeTab(Theora.TAB);
        ITEMS.add(item);
        return item;
    }
}
