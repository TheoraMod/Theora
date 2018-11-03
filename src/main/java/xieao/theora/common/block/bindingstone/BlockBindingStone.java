package xieao.theora.common.block.bindingstone;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xieao.theora.common.block.BlockBase;

import javax.annotation.Nullable;

public class BlockBindingStone extends BlockBase implements ITileEntityProvider {

    public BlockBindingStone() {
        super(Material.ROCK);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileBindingStone();
    }

    @Override
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
}