package computer.gingershaped.afkmarker.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import computer.gingershaped.afkmarker.AfkMarker
import computer.gingershaped.afkmarker.MINECRAFT
import computer.gingershaped.afkmarker.client.AfkMarkerClient.AFK_ICON
import computer.gingershaped.afkmarker.client.AfkMarkerClient.PAUSED_ICON
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.state.PlayerRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderNameTagEvent

@EventBusSubscriber(value = [Dist.CLIENT], modid = AfkMarker.ID)
object NameplateRenderer {
    @SubscribeEvent
    private fun onNameplateRender(event: RenderNameTagEvent.DoRender) = with(MINECRAFT) {
        val renderState = event.entityRenderState

        if (renderState !is PlayerRenderState) {
            return
        }
        val level = level ?: return
        val player = player ?: return
        val entity = level.getEntity(renderState.id)
        if (entity !is AbstractClientPlayer) {
            return
        }
        if (player == entity) {
            return
        }
        if (options.hideGui) {
            return
        }

        if (entity.gameProfile in AfkMarkerClient.afkPlayers) {
            renderNameplateIcon(
                AFK_ICON,
                event.content,
                renderState,
                event.poseStack,
                event.multiBufferSource,
                event.packedLight
            )
        } else if (entity.gameProfile in AfkMarkerClient.pausedPlayers) {
            renderNameplateIcon(
                PAUSED_ICON,
                event.content,
                renderState,
                event.poseStack,
                event.multiBufferSource,
                event.packedLight
            )
        }
    }

    private fun renderNameplateIcon(
        texture: ResourceLocation,
        component: Component,
        renderState: PlayerRenderState,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        light: Int
    ) {
        val nameTagAttachment = renderState.nameTagAttachment ?: return
        val offset = MINECRAFT.font.width(component).toFloat() / 2 + 2
        val alpha = 32

        with(poseStack) {
            pushPose()
            nameTagAttachment.let { translate(it.x, it.y + 0.5, it.z) }
            mulPose(MINECRAFT.entityRenderDispatcher.cameraOrientation())
            scale(0.025F, -0.025F, 0.025F)
            translate(0.0, -1.0, 0.0)

            val builder = buffer.getBuffer(RenderType.text(texture))
            if (renderState.isDiscrete) {
                builder.square(poseStack, offset, 10F, alpha, light)
            } else {
                builder.square(poseStack, offset, 10F, 255, light)
                val transparentBuilder = buffer.getBuffer(RenderType.textSeeThrough(texture))
                transparentBuilder.square(poseStack, offset, 10F, alpha, light)
            }

            popPose()
        }
    }

    private fun VertexConsumer.square(poseStack: PoseStack, offset: Float, size: Float, alpha: Int, light: Int) {
        vertex(poseStack, x = offset,        y = size, z = 0F, u = 0F, v = 1F, alpha, light)
        vertex(poseStack, x = offset + size, y = size, z = 0F, u = 1F, v = 1F, alpha, light)
        vertex(poseStack, x = offset + size, y = 0F,   z = 0F, u = 1F, v = 0F, alpha, light)
        vertex(poseStack, x = offset,        y = 0F,   z = 0F, u = 0F, v = 0F, alpha, light)
    }

    private fun VertexConsumer.vertex(
        poseStack: PoseStack,
        x: Float,
        y: Float,
        z: Float,
        u: Float,
        v: Float,
        alpha: Int,
        light: Int
    ) {
        poseStack.last().let { pose ->
            addVertex(pose, x, y, z)
                .setColor(255, 255, 255, alpha)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0F, 0F, -1F)
        }
    }
}