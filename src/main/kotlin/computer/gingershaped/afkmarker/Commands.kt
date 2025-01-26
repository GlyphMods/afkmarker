package computer.gingershaped.afkmarker

import com.mojang.brigadier.arguments.FloatArgumentType
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.RegisterCommandsEvent
import java.util.*

@EventBusSubscriber(modid = AfkMarker.ID, bus = EventBusSubscriber.Bus.GAME)
object Commands {
    private fun setAfkTimeout(source: CommandSourceStack, timeout: Float): Int {
        source.server.overworld().dataStorage.afkMarkerSavedData.afkTimeout = timeout
        source.sendSuccess(
            { Component.translatable("commands.afkmarker.set_afk_timeout.success", timeout) }, true
        )
        return 1
    }

    private fun markAsAfk(source: CommandSourceStack): Int {
        source.playerOrException.let { player ->
            player.setData(AfkMarker.Registry.MANUAL_AFK_POSITION, Optional.of(player.blockPosition()))
        }
        source.sendSuccess(
            { Component.translatable("commands.afkmarker.mark_afk.success") }, true
        )
        return 1
    }

    @SubscribeEvent
    private fun register(event: RegisterCommandsEvent) {
        event.dispatcher.register(Commands.literal("afk")
            .executes { markAsAfk(it.source) }
            .then(Commands
                .literal("settimeout")
                .then(
                    Commands.argument("timeout", FloatArgumentType.floatArg())
                        .requires { it.hasPermission(Commands.LEVEL_ADMINS) }
                        .executes {
                            setAfkTimeout(it.source, FloatArgumentType.getFloat(it, "timeout"))
                        }
                )
            )
        )
    }
}