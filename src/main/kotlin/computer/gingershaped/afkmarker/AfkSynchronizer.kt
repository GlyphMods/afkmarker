package computer.gingershaped.afkmarker

import com.mojang.authlib.GameProfile
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.tick.ServerTickEvent
import net.neoforged.neoforge.network.PacketDistributor
import java.util.*

@EventBusSubscriber(modid = AfkMarker.ID, bus = EventBusSubscriber.Bus.GAME)
object AfkSynchronizer {
    @SubscribeEvent
    private fun onServerTick(event: ServerTickEvent.Post) {
        if (event.server.tickCount % 20 == 0) {
            val afkTimeout = event.server.overworld().dataStorage.afkMarkerSavedData.afkTimeout
            val afkPlayers = hashSetOf<GameProfile>()
            val pausedPlayers = hashSetOf<GameProfile>()
            for (player in event.server.playerList.players) {
                val idle = Util.getMillis() - player.lastActionTime > afkTimeout * 60 * 1000
                val manualAfkPosition = player.getData(AfkMarker.Registry.MANUAL_AFK_POSITION)
                if (idle || (manualAfkPosition.isPresent && manualAfkPosition.get() == player.blockPosition())) {
                    afkPlayers += player.gameProfile
                } else if (manualAfkPosition.isPresent && manualAfkPosition.get() != player.blockPosition()) {
                    player.setData(AfkMarker.Registry.MANUAL_AFK_POSITION, Optional.empty())
                    player.sendSystemMessage(Component.translatable("afkmarker.no_longer_afk"))
                }
                if (player.getData(AfkMarker.Registry.PAUSED)) {
                    pausedPlayers += player.gameProfile
                }
            }
            PacketDistributor.sendToAllPlayers(Packets.AfkPlayers(afkPlayers, pausedPlayers))
        }
    }
}