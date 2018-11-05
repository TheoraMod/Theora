package xieao.theora.client.helper;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import xieao.theora.Theora;
import xieao.theora.common.block.TileBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public class RendererHelper {

    //TODO rerange

    public static final Minecraft mc = Minecraft.getMinecraft();
    public static int tickCount;
    public static float partialTicks;

    @SubscribeEvent
    public static void renderWorldLastTick(RenderWorldLastEvent event) {
        if (mc.currentScreen == null || !mc.currentScreen.doesGuiPauseGame()) {
            partialTicks = event.getPartialTicks();
        }
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (mc.currentScreen == null || !mc.currentScreen.doesGuiPauseGame()) {
                tickCount++;
            }
        }
    }

    public static void setShadeModel() {
        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }
    }

    public static void renderFlatQuad(double dim) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        dim /= 2.0D;
        double mid = -dim;
        buffer.pos(dim, 0.0D, dim).tex(1, 1).endVertex();
        buffer.pos(dim, 0.0D, mid).tex(1, 0).endVertex();
        buffer.pos(mid, 0.0D, mid).tex(0, 0).endVertex();
        buffer.pos(mid, 0.0D, dim).tex(0, 1).endVertex();
        tessellator.draw();
    }

    public static void render(TileBase tile, boolean inGui) {
        tile.setGhostTile(inGui);
        TileEntityRendererDispatcher.instance.render(tile, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
    }

    public static void render(IBakedModel model, IBlockAccess world, IBlockState state, boolean smooth) {
        BlockModelRenderer renderer = mc.getBlockRendererDispatcher().getBlockModelRenderer();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        if (smooth) {
            renderer.renderModelSmooth(world, model, state, BlockPos.ORIGIN, builder, true, 0);
        } else {
            renderer.renderModelFlat(world, model, state, BlockPos.ORIGIN, builder, false, 0);
        }
        tessellator.draw();
    }

    public static IBakedModel getOBJModel(String path) {
        return getOBJModel(path, ImmutableMap.of(), TRSRTransformation.identity());
    }

    public static IBakedModel getOBJModel(String path, IModelState modelState) {
        return getOBJModel(path, ImmutableMap.of(), modelState);
    }

    public static IBakedModel getOBJModel(String path, ImmutableMap<String, String> textures) {
        return getOBJModel(path, textures, TRSRTransformation.identity());
    }

    public static IBakedModel getOBJModel(String path, ImmutableMap<String, String> textures, IModelState modelState) {
        OBJModel model = null;
        try {
            model = (OBJModel) OBJLoader.INSTANCE.loadModel(Theora.location("models/" + path)).process(ImmutableMap.of("flip-v", "true"));
            if (!textures.isEmpty()) {
                model = (OBJModel) model.retexture(textures);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(model);
        return model.bake(modelState, Attributes.DEFAULT_BAKED_FORMAT, ModelLoader.defaultTextureGetter());
    }

    public static void render(IBlockState state, IBlockAccess blockAccess, BlockPos pos) {
        BlockRendererDispatcher brd = mc.getBlockRendererDispatcher();
        BlockModelRenderer modelRenderer = brd.getBlockModelRenderer();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        IBakedModel model = brd.getModelForState(state);
        state = state.getBlock().getExtendedState(state, blockAccess, pos);
        modelRenderer.renderModel(blockAccess, model, state, BlockPos.ORIGIN, bufferBuilder, true);
        tessellator.draw();
    }

    public static void renderRotatingItems(NonNullList<ItemStack> stacks, float scale, float rotation, double y, double radius, int... excludedSlots) {
        if (!stacks.isEmpty()) {
            List<ItemStack> toRender = new ArrayList<>();
            for (int i = 0; i < stacks.size(); i++) {
                boolean flag = false;
                for (int excl : excludedSlots) {
                    if (i == excl) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    ItemStack stack = stacks.get(i);
                    if (!stack.isEmpty()) {
                        toRender.add(stack);
                    }
                }
            }
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.5D, y, 0.5D);
            GlStateManager.rotate(-rotation, 0.0F, 1.0F, 0.0F);
            int size = toRender.size();
            for (int i = 0; i < size; i++) {
                ItemStack stack = toRender.get(i);
                double slice = 2.0D * Math.PI / size;
                double angle = slice * i;
                double x = radius * Math.cos(angle);
                double z = radius * Math.sin(angle);
                GlStateManager.pushMatrix();
                GlStateManager.translate(x, stack.getItem() instanceof ItemBlock ? -0.04D : 0.0D, z);
                double center = Math.atan2(-x, -z);
                float facing = (float) Math.toDegrees(center);
                facing = (float) (facing + Math.ceil(-facing / 360.0F) * 360.0F);
                if (facing < 0.0F) {
                    facing += 360.0F;
                }
                GlStateManager.rotate(facing, 0.0F, 1.0F, 0.0F);
                renderItemStack(stack, stack.getItem() instanceof ItemBlock ? scale / 1.2F : scale);
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();
        }
    }

    public static void renderItemStack(ItemStack stack, float scale) {
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, scale);
            RenderItem renderItem = mc.getRenderItem();
            renderItem.renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
            GlStateManager.popMatrix();
        }
    }
}