package computer.gingershaped.afkmarker

import com.mojang.authlib.GameProfile
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec2
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import org.apache.logging.log4j.LogManager
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import java.util.Optional

@Mod(AfkMarker.ID)
object AfkMarker {
    const val ID = "afkmarker"
    val LOGGER = LogManager.getLogger()

    init {
        Registry.register()
    }

    object Registry {
        private val ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ID)
        val PAUSED: AttachmentType<Boolean> by ATTACHMENT_TYPES.register("paused") { ->
            AttachmentType.builder { -> false }.build()
        }
        val MANUAL_AFK_POSITION: AttachmentType<Optional<BlockPos>> by ATTACHMENT_TYPES.register("manualafk") { ->
            AttachmentType.builder { -> Optional.empty<BlockPos>() }.build()
        }

        fun register() {
            ATTACHMENT_TYPES.register(MOD_BUS)
        }
    }

    fun handlePlayerPausedPacket(payload: Packets.SetPaused, context: IPayloadContext) {
        context.player().setData(Registry.PAUSED, payload.paused)
    }
}

val MINECRAFT get() = Minecraft.getInstance()