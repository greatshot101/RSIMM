package net.guwy.rsimm.content.network_packets;

import net.guwy.rsimm.client.ArmorClientData;
import net.guwy.rsimm.mechanics.capabilities.custom.player.armor_data.FlyMode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FlightDataS2CPacket {
    /**
     * Sends data from server to client for flight handling
     *
     * These data used by the client to check if the player is flying at ar which mode
     * which then translates to motion as speed is non-existent at the server side
     */

    private final double playerRotation;
    private final boolean isFlying;
    private final FlyMode flyMode;

    public FlightDataS2CPacket(double playerRotation, boolean isFlying, FlyMode flyMode) {
        this.playerRotation = playerRotation;
        this.isFlying = isFlying;
        this.flyMode = flyMode;
    }

    public FlightDataS2CPacket(FriendlyByteBuf buf) {
        this.playerRotation = buf.readDouble();
        this.isFlying = buf.readBoolean();
        this.flyMode = buf.readEnum(FlyMode.class);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeDouble(playerRotation);
        buf.writeBoolean(isFlying);
        buf.writeEnum(flyMode);

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // HERE WE ARE ON THE CLIENT!

            ArmorClientData.setPlayerRotation(playerRotation);
            ArmorClientData.setIsFlying(isFlying);
            ArmorClientData.setFlyMode(flyMode);
        });
        return true;
    }
}
