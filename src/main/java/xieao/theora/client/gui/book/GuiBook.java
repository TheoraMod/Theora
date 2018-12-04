package xieao.theora.client.gui.book;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xieao.theora.Theora;
import xieao.theora.client.gui.button.Button;
import xieao.theora.common.lib.TheoraSounds;
import xieao.theora.common.lib.book.Entry;
import xieao.theora.common.lib.book.TheoraBook;
import xieao.theora.common.lib.book.page.Page;

import javax.annotation.Nullable;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiBook extends GuiScreen {

    public static final ResourceLocation BOOK_TEXTURE = Theora.location("textures/gui/book/background.png");

    public static GuiBook instance = new GuiBook();

    public int x, y, w = 196, h = 230;
    private final Entry entry;
    private final Page page;
    private int curPage;

    @Nullable
    private Button home;
    @Nullable
    private Button back;
    @Nullable
    private Button next;
    @Nullable
    private Button prev;

    @Nullable
    public GuiBook prevGui;

    public GuiBook(Entry entry, int index) {
        this.entry = entry;
        this.page = entry.getPage(index);
        this.curPage = index;
    }

    public GuiBook() {
        this(TheoraBook.HOME, 0);
    }

    @Override
    public void initGui() {
        this.x = (this.width - this.w) / 2;
        this.y = (this.height - this.h) / 2;
        this.home = new Button(0, this.x - 20, this.y + 32).setIcon(BOOK_TEXTURE, 15, 15, 196, 81, true, 0xffffff).setSound(TheoraSounds.PAGE_FLIP);
        this.back = new Button(1, this.x - 20, this.y + 32 + 18).setIcon(BOOK_TEXTURE, 14, 13, 196, 96, true, 0xffffff).setSound(TheoraSounds.PAGE_FLIP);
        this.next = new Button(2, this.x + this.w, this.y + this.h - 32).setIcon(BOOK_TEXTURE, 12, 23, 208, 58, true, 0xffffff).setSound(TheoraSounds.PAGE_FLIP);
        this.prev = new Button(3, this.x - 20, this.y + this.h - 32).setIcon(BOOK_TEXTURE, 12, 23, 196, 58, true, 0xffffff).setSound(TheoraSounds.PAGE_FLIP);
        this.buttonList.add(this.home);
        this.buttonList.add(this.back);
        this.buttonList.add(this.next);
        this.buttonList.add(this.prev);
        this.page.initGui(this);
        instance = this;
        refreshButtons();
    }

    public void refreshButtons() {
        int i = this.entry.pages.length;
        this.home.visible = !this.entry.getPage(0).title.equals("home");
        this.back.visible = !this.entry.getPage(0).title.equals("home") && this.prevGui != null;
        this.next.visible = i > 1 && this.curPage < i - 1;
        this.prev.visible = i > 1 && this.curPage > 0;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(new GuiBook());
                break;
            case 1:
                if (this.prevGui != null)
                    this.mc.displayGuiScreen(this.prevGui);
                break;
            case 2:
                this.curPage++;
                this.mc.displayGuiScreen(new GuiBook(this.entry, this.curPage).setPrevGui(prevGui));
                refreshButtons();
                break;
            case 3:
                this.curPage--;
                this.mc.displayGuiScreen(new GuiBook(this.entry, this.curPage).setPrevGui(prevGui));
                refreshButtons();
                break;
            default:
                this.page.actionPerformed(this, button);
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translate(this.x, this.y, 0.0D);
        this.mc.getTextureManager().bindTexture(BOOK_TEXTURE);
        drawTexturedModalRect(0, 0, 0, 0, this.w, this.h);
        this.page.draw(this, mouseX, mouseY, partialTicks);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)) {
            this.mc.player.closeScreen();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void open() {
        Minecraft.getMinecraft().displayGuiScreen(instance);
    }

    public void open(Entry entry, int i) {
        Minecraft.getMinecraft().displayGuiScreen(
                new GuiBook(entry, i).setPrevGui(this));
    }

    public GuiBook setPrevGui(@Nullable GuiBook prevGui) {
        this.prevGui = prevGui;
        return this;
    }

    public List<GuiButton> getButtonList() {
        return buttonList;
    }
}