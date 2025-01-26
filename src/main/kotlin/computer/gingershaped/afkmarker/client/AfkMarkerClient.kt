package computer.gingershaped.afkmarker.client

import com.mojang.authlib.GameProfile
import computer.gingershaped.afkmarker.AfkMarker
import computer.gingershaped.afkmarker.Packets
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.network.handling.IPayloadContext

@Mod(value = AfkMarker.ID, dist = [Dist.CLIENT])
object AfkMarkerClient {
    @JvmStatic val AFK_ICON: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(AfkMarker.ID, "textures/afk.png")
    @JvmStatic val PAUSED_ICON: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(AfkMarker.ID, "textures/paused.png")

    var afkPlayers = setOf<GameProfile>()
        private set
    var pausedPlayers = setOf<GameProfile>()
        private set

    fun handleAfkPlayersPacket(payload: Packets.AfkPlayers, context: IPayloadContext) {
        afkPlayers = payload.afkPlayers
        pausedPlayers = payload.pausedPlayers
    }
}