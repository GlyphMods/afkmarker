package computer.gingershaped.afkmarker.client

import computer.gingershaped.afkmarker.AfkMarker
import computer.gingershaped.afkmarker.Packets
import net.minecraft.client.gui.screens.PauseScreen
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ScreenEvent
import net.neoforged.neoforge.network.PacketDistributor

@EventBusSubscriber(modid = AfkMarker.ID, value = [Dist.CLIENT], bus = EventBusSubscriber.Bus.GAME)
object PauseListener {
    @SubscribeEvent
    private fun onScreenOpen(event: ScreenEvent.Opening) {
        if (event.screen is PauseScreen && Configuration.config.syncPaused.get()) {
            PacketDistributor.sendToServer(Packets.SetPaused(true))
        }
    }
    @SubscribeEvent
    private fun onScreenClose(event: ScreenEvent.Closing) {
        if (event.screen is PauseScreen && Configuration.config.syncPaused.get()) {
            PacketDistributor.sendToServer(Packets.SetPaused(false))
        }
    }
}