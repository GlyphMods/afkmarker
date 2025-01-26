package computer.gingershaped.afkmarker

import com.mojang.authlib.GameProfile
import computer.gingershaped.afkmarker.client.AfkMarkerClient
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent

@EventBusSubscriber(modid = AfkMarker.ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object Packets {
    data class AfkPlayers(val afkPlayers: HashSet<GameProfile>, val pausedPlayers: HashSet<GameProfile>) : CustomPacketPayload {
        override fun type() = TYPE

        companion object {
            val TYPE = CustomPacketPayload.Type<AfkPlayers>(
                ResourceLocation.fromNamespaceAndPath(AfkMarker.ID, "afk_players")
            )
            val CODEC = StreamCodec.composite(
                ByteBufCodecs.collection(::HashSet, ByteBufCodecs.GAME_PROFILE),
                AfkPlayers::afkPlayers,
                ByteBufCodecs.collection(::HashSet, ByteBufCodecs.GAME_PROFILE),
                AfkPlayers::pausedPlayers,
                ::AfkPlayers
            )
        }
    }

    data class SetPaused(val paused: Boolean) : CustomPacketPayload {
        override fun type() = TYPE

        companion object {
            val TYPE = CustomPacketPayload.Type<SetPaused>(
                ResourceLocation.fromNamespaceAndPath(AfkMarker.ID, "set_paused")
            )
            val CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL,
                SetPaused::paused,
                ::SetPaused
            )
        }
    }

    @SubscribeEvent
    private fun register(event: RegisterPayloadHandlersEvent) {
        with(event.registrar("1")) {
            playToClient(
                AfkPlayers.TYPE,
                AfkPlayers.CODEC,
                AfkMarkerClient::handleAfkPlayersPacket,
            )
            playToServer(
                SetPaused.TYPE,
                SetPaused.CODEC,
                AfkMarker::handlePlayerPausedPacket
            )
        }
    }
}