package computer.gingershaped.afkmarker.client

import com.mojang.authlib.GameProfile
import computer.gingershaped.afkmarker.AfkMarker
import computer.gingershaped.afkmarker.Packets
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.client.gui.ConfigurationScreen
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import net.neoforged.neoforge.network.handling.IPayloadContext
import thedarkcolour.kotlinforforge.neoforge.forge.LOADING_CONTEXT

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

    init {
        with(LOADING_CONTEXT.activeContainer) {
            registerConfig(ModConfig.Type.CLIENT, Configuration.spec)
            registerExtensionPoint(
                IConfigScreenFactory::class.java,
                IConfigScreenFactory(::ConfigurationScreen)
            )
        }
    }

    fun handleAfkPlayersPacket(payload: Packets.AfkPlayers, context: IPayloadContext) {
        afkPlayers = payload.afkPlayers
        pausedPlayers = payload.pausedPlayers
    }
}