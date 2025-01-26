package computer.gingershaped.afkmarker

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedData.Factory
import net.minecraft.world.level.storage.DimensionDataStorage
import kotlin.properties.Delegates

class AfkMarkerSavedData(afkTimeout: Float = 5F) : SavedData() {
    constructor(tag: CompoundTag, lookupProvider: HolderLookup.Provider) : this(tag.getFloat("afkTimeout"))

    /** The number of minutes a player must be inactive for before
     * being marked as AFK.
     */
    var afkTimeout: Float by Delegates.observable(afkTimeout) { _, _, _ -> setDirty() }

    override fun save(tag: CompoundTag, registries: HolderLookup.Provider) = tag.apply {
        putFloat("afkTimeout", afkTimeout)
    }
}

val DimensionDataStorage.afkMarkerSavedData: AfkMarkerSavedData
    get() = computeIfAbsent(Factory(::AfkMarkerSavedData, ::AfkMarkerSavedData), "afkmarkersaveddata")