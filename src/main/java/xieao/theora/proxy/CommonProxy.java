package xieao.theora.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import xieao.theora.Theora;
import xieao.theora.api.TheoraAPI;
import xieao.theora.api.liquid.LiquidContainerCapability;
import xieao.theora.api.player.data.PlayerDataCapability;
import xieao.theora.common.ability.TheoraAbilities;
import xieao.theora.common.block.TheoraBlocks;
import xieao.theora.common.item.TheoraItems;
import xieao.theora.common.liquid.TheoraLiquids;
import xieao.theora.common.recipe.BindingStoneRecipes;
import xieao.theora.common.recipe.FermentingRecipes;
import xieao.theora.common.recipe.RecipeHandler;
import xieao.theora.common.trade.PigZomieTrades;
import xieao.theora.network.GuiHandler;
import xieao.theora.network.TheoraNetwork;

public class CommonProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        for (Item item : TheoraItems.ITEMS)
            ForgeRegistries.ITEMS.register(item);
        for (Block block : TheoraBlocks.BLOCKS)
            ForgeRegistries.BLOCKS.register(block);
        TheoraNetwork.registerPackets();
        PlayerDataCapability.register();
        LiquidContainerCapability.register();

        TheoraAPI.INSTANCE.register(new FermentingRecipes());
        TheoraAPI.INSTANCE.register(new BindingStoneRecipes());

        TheoraLiquids.register();
        TheoraAbilities.register();

        PigZomieTrades.register();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(Theora.instance, GuiHandler.INSTANCE);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        RecipeHandler.initRecipes();
    }
}
