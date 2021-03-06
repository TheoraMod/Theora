package zeroneye.theora.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import zeroneye.theora.api.TheoraAPI;

import java.util.Objects;
import java.util.function.Supplier;

public class SyncPlayerData {
    private CompoundNBT compound;

    public SyncPlayerData(CompoundNBT compound) {
        this.compound = compound;
    }

    public static void encode(SyncPlayerData msg, PacketBuffer buffer) {
        buffer.writeCompoundTag(msg.compound);
    }

    public static SyncPlayerData decode(PacketBuffer buffer) {
        return new SyncPlayerData(Objects.requireNonNull(buffer.readCompoundTag()));
    }

    public static void handle(SyncPlayerData msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> TheoraAPI.getPlayerData(Minecraft.getInstance().player).ifPresent(playerData ->
                playerData.read(msg.compound)));
        ctx.get().setPacketHandled(true);
    }
}
