package net.guwy.rsimm.mechanics.event.server_events;

import net.guwy.rsimm.mechanics.capabilities.player.arc_reactor.ArcReactorSlot;
import net.guwy.rsimm.mechanics.capabilities.player.arc_reactor.ArcReactorSlotProvider;
import net.guwy.rsimm.mechanics.capabilities.player.armor_data.IronmanArmorData;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class CapabilityCarryOverDeathHandler {

    public static void init(PlayerEvent.Clone event){
        event.getOriginal().reviveCaps();
        event.getOriginal().getCapability(ArcReactorSlotProvider.PLAYER_REACTOR_SLOT).ifPresent(oldStore -> {
            event.getEntity().getCapability(ArcReactorSlotProvider.PLAYER_REACTOR_SLOT).ifPresent(newStore -> {

                if(oldStore.getPlayerArcReactorPoisoning() > oldStore.getMaximumPoisoning() * 3/4){
                    oldStore.setPlayerArcReactorPoisoning(oldStore.getMaximumPoisoning() * 3/4);        //gives you 12 days if the poison factor is 14 (43 if its 4)
                }
                newStore.copyFrom(oldStore);
            });
        });
        event.getOriginal().invalidateCaps();

    }
}
