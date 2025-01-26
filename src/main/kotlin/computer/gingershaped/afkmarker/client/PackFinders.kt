package computer.gingershaped.afkmarker.client

import computer.gingershaped.afkmarker.AfkMarker
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.repository.Pack
import net.minecraft.server.packs.repository.PackSource
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.AddPackFindersEvent

@EventBusSubscriber(modid = AfkMarker.ID, value = [Dist.CLIENT], bus = EventBusSubscriber.Bus.MOD)
object PackFinders {
    @SubscribeEvent
    private fun onAddPackFinders(event: AddPackFindersEvent) {
        event.addPackFinders(
            ResourceLocation.fromNamespaceAndPath(AfkMarker.ID, "packs/vanilla"),
            PackType.CLIENT_RESOURCES,
            Component.translatable("resourcepack.afkmarker.vanilla.name"),
            PackSource.BUILT_IN,
            false,
            Pack.Position.TOP,
        )
    }
}