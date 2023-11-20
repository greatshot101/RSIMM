package net.guwy.rsimm.content.network_packets;

import net.guwy.rsimm.config.RsImmServerConfigs;
import net.guwy.rsimm.content.items.arc_reactors.AbstractArcReactorItem;
import net.guwy.rsimm.content.items.armors.AbstractIronmanArmorItem;
import net.guwy.rsimm.index.RsImmSounds;
import net.guwy.rsimm.mechanics.capabilities.player.arc_reactor.ArcReactorSlot;
import net.guwy.rsimm.mechanics.capabilities.player.arc_reactor.ArcReactorSlotProvider;
import net.guwy.rsimm.mechanics.capabilities.player.armor_data.IronmanArmorDataProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ArmorKeyBindingHoldC2SPacket {
    /**
     * Key Binding Transmitter to the server for keybinding handling
     *
     * Should probably be reformatted to be like the weapon key binding
     */
    public ArmorKeyBindingHoldC2SPacket() {

    }

    public ArmorKeyBindingHoldC2SPacket(FriendlyByteBuf buf) {

    }

    public void toBytes(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // HERE WE ARE ON THE SERVER!

            ServerPlayer player = context.getSender();
            ServerLevel level = player.getLevel();

            player.getCapability(ArcReactorSlotProvider.PLAYER_REACTOR_SLOT).ifPresent(arcReactor -> {
                player.getCapability(IronmanArmorDataProvider.PLAYER_IRONMAN_ARMOR_DATA).ifPresent(armorData -> {

                    // The part that does the armor stuff
                    if(armorData.getHasArmor()){
                        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
                        AbstractIronmanArmorItem armor = (AbstractIronmanArmorItem) chestplate.getItem();
                        armor.armorKeyHoldAction(player);

                    }
                    // The part that does the Arc Reactor Stuff
                    else {
                        ArcReactorSlot.removeArcReactor(player);
                    }

                });
            });

        });
        return true;
    }
}
