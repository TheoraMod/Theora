package xieao.theora.item;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xieao.theora.api.TheoraAPI;
import xieao.theora.api.player.GateData;
import xieao.theora.block.gate.TileGate;
import xieao.theora.client.renderer.item.IItemColorHolder;
import xieao.theora.lib.util.PlayerUtil;

public class ItemStone extends ItemBase implements IItemColorHolder {
    public ItemStone(Properties properties) {
        super(properties);
    }

    @Override
    public EnumActionResult onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        if (!world.isRemote) {
            EntityPlayer player = context.getPlayer();
            if (player != null && !PlayerUtil.isFake(player)) {
                TheoraAPI.getPlayerData(player).ifPresent(playerData -> {
                    GateData gateData = playerData.gate;
                    TileEntity tileEntity = gateData.getTileEntity(world);
                    if (tileEntity instanceof TileGate) {
                        TileGate gate = (TileGate) tileEntity;


                    }
                });
            }
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IItemColor getItemColor() {
        return (stack, i) -> 0xffffff;
    }
}