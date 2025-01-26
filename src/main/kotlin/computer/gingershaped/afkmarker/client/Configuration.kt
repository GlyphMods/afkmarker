package computer.gingershaped.afkmarker.client

import net.neoforged.neoforge.common.ModConfigSpec

class Configuration private constructor(val builder: ModConfigSpec.Builder) {
    val syncPaused = builder
        .comment("Whether to show a pause icon to other players")
        .comment("when you have the pause screen open.")
        .define("sync_paused", true)

    companion object {
        @JvmStatic val config: Configuration
        @JvmStatic val spec: ModConfigSpec
        init {
            ModConfigSpec.Builder().configure(::Configuration).let { (config, spec) ->
                this.config = config
                this.spec = spec
            }
        }
    }
}