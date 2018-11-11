package xieao.theora.common.item;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xieao.theora.api.liquid.*;
import xieao.theora.api.liquid.LiquidSlot.TransferType;
import xieao.theora.client.renderer.item.IColoredItem;

import javax.annotation.Nullable;
import java.util.List;

public class ItemVial extends ItemBase implements IColoredItem {

    public ItemVial() {
        addPropertyOverride(new ResourceLocation("stored"), (stack, world, entity) -> {
            IliquidContainer.Item vial = LiquidUtil.getILiquidContainerItem(stack);
            if (vial != null) {
                LiquidSlot LiquidSlot = vial.getLiquidSlot(0);
                return LiquidSlot.getStored() / LiquidSlot.getCapacity();
            }
            return 0.0F;
        });
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            for (Liquid liquid : Liquid.REGISTRY.values()) {
                ItemStack stack = new ItemStack(this);
                IliquidContainer.Item vial = LiquidUtil.getILiquidContainerItem(stack);
                if (vial != null) {
                    LiquidSlot[] liquidSlots = vial.getLiquidSlots();
                    if (liquidSlots.length < 1) {
                        LiquidSlot liquidSlot = new LiquidSlot(Liquid.EMPTY, false, 200.0F, 0.0F, 200.0F, TransferType.ALL);
                        vial.setLiquidSlots(new LiquidSlot[]{liquidSlot});
                    }
                    LiquidSlot LiquidSlot = vial.getLiquidSlot(0);
                    LiquidSlot.setLiquid(liquid);
                    if (!liquid.isEmpty()) {
                        LiquidSlot.setStored(LiquidSlot.getCapacity());
                    }
                    vial.setLiquidSlot(0, LiquidSlot);
                    items.add(stack);
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        IliquidContainer.Item vial = LiquidUtil.getILiquidContainerItem(stack);
        if (vial != null) {
            LiquidSlot LiquidSlot = vial.getLiquidSlot(0);
            if (!LiquidSlot.getLiquid().isEmpty()) {
                tooltip.add("Liquid: " + LiquidSlot.getLiquid().getRegistryString());
                tooltip.add("Stored: " + LiquidSlot.getStored());
            }
        }
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        IliquidContainer.Item vial = LiquidUtil.getILiquidContainerItem(stack);
        if (vial != null) {
            LiquidSlot linkedTank = vial.getLiquidSlot(0);
//            if (ItemVial.tryLiquidInteract(player, world, pos, linkedTank)) {
//                return EnumActionResult.SUCCESS;
//            }
            IliquidContainer liquidContainer = LiquidUtil.getILiquidContainer(world, pos, side);
            if (liquidContainer != null) {
                LiquidSlot vialTank = vial.getLiquidSlot(0);
                if (!vialTank.isEmpty()) {
                    for (LiquidSlot liquidSlot : liquidContainer.getLiquidSlots()) {
                        TransferType transferType = liquidSlot.getTransferType();
                        if (transferType.equals(TransferType.ALL) || transferType.equals(TransferType.RECEIVE)) {
                            if (liquidSlot.liquidEquals(vialTank.getLiquid()) && !liquidSlot.isFull() || liquidSlot.isEmpty()) {
                                liquidSlot.setLiquid(vialTank.getLiquid());
                                vialTank.drain(liquidSlot, true, !player.isCreative());
                                vial.setLiquidSlot(0, vialTank);
                                return EnumActionResult.SUCCESS;
                            }
                        }
                    }
                }
                if (!vialTank.isFull()) {
                    for (LiquidSlot liquidSlot : liquidContainer.getLiquidSlots()) {
                        TransferType transferType = liquidSlot.getTransferType();
                        if (transferType.equals(TransferType.ALL) || transferType.equals(TransferType.SEND)) {
                            if (liquidSlot.liquidEquals(vialTank.getLiquid()) || vialTank.getLiquid().isEmpty() && !liquidSlot.isEmpty()) {
                                vialTank.setLiquid(liquidSlot.getLiquid());
                                vialTank.fill(liquidSlot, true);
                                vial.setLiquidSlot(0, vialTank);
                                return EnumActionResult.SUCCESS;
                            }
                        }
                    }
                }
            }
        }
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new LiquidContainer.Item(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IItemColor getItemColor() {
        return (stack, tintIndex) -> {
            IliquidContainer.Item vial = LiquidUtil.getILiquidContainerItem(stack);
            if (vial != null) {
                LiquidSlot LiquidSlot = vial.getLiquidSlot(0);
                Liquid liquid = LiquidSlot.getLiquid();
                int liquidColor = liquid.getDarkColor();
                return tintIndex == 1 || liquid.isEmpty() ? 0xFFFFFF : liquidColor;
            }
            return 0xFFFFFF;
        };
    }
}
