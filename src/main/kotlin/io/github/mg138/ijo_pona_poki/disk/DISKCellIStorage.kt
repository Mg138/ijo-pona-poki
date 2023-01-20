package io.github.mg138.ijo_pona_poki.disk

import appeng.api.config.Actionable
import appeng.api.stacks.AEKey
import appeng.api.stacks.AEKeyType
import appeng.api.stacks.KeyCounter
import it.unimi.dsi.fastutil.longs.LongArrayList
import it.unimi.dsi.fastutil.objects.Object2LongMap
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtLongArray
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import kotlin.math.min

typealias Inventory = Object2LongMap<AEKey>

typealias AmountList = LongArrayList

fun emptyInv(): Inventory = Object2LongOpenHashMap<AEKey>().apply { this.defaultReturnValue(0) }

class DISKCellIStorage(val item: DISKCellItem, private val inventory: Inventory) {
    companion object {
        val KEY_TYPE: AEKeyType = AEKeyType.items()

        const val DISK_ID = "disk_id"
        const val KEYS_TAG = "keys"
        const val AMOUNTS_TAG = "amts"

        fun fromNbt(nbt: NbtCompound): DISKCellIStorage? {
            val item = nbt.getString(this.DISK_ID)
                          .let(Identifier::tryParse)
                          .let { Registry.ITEM.get(it) as? DISKCellItem }
                          ?: return null

            val inv = emptyInv()

            val keys = nbt.get(this.KEYS_TAG) as? NbtList ?: return DISKCellIStorage(item, inv)
            val amounts = nbt.get(this.AMOUNTS_TAG)?.let { (it as? NbtLongArray)?.longArray } ?: return DISKCellIStorage(item, inv)

            keys
                .map { it as? NbtCompound }
                .map {
                    try {
                        this.KEY_TYPE.loadKeyFromTag(it)
                    } catch (e: Exception) {
                        null
                    }
                }
                .withIndex()
                .forEach { (index, k) ->
                    k?.let { key ->
                        inv[key] = amounts[index]
                    }
                }

            return DISKCellIStorage(item, inv)
        }
    }

    fun writeNbt(nbt: NbtCompound): Boolean {
        val id = Registry.ITEM.getId(this.item.asItem())
        val keys = NbtList()
        val amounts = AmountList(this.inventory.size)

        this.inventory.object2LongEntrySet().forEach { (key, amount) ->
            if (amount > 0L) {
                keys += key.toTag()
                amounts += amount
            }
        }

        if (keys.isEmpty()) return false

        nbt.putString(Companion.DISK_ID, id.toString())
        nbt.put(Companion.KEYS_TAG, keys)
        nbt.putLongArray(Companion.AMOUNTS_TAG, amounts.toLongArray())

        return true
    }

    /**
     * One item is exactly one byte.
     */
    private var size: Long = this.inventory.values.sum()

    private val capacity = this.item.capacity()

    fun usedBytes() = this.size

    /**
     * How much space is left in the inventory.
     */
    private fun spaceLeft() = this.capacity - this.size


    fun insert(key: AEKey?, amount: Long, mode: Actionable): Long {
        if (key !in Companion.KEY_TYPE) return 0L

        val spaceLeft = this.spaceLeft()
        val inserting = min(amount, spaceLeft)

        if (mode == Actionable.MODULATE) {
            this.inventory.mergeLong(key, inserting, Long::plus)
            this.size += inserting
        }

        return inserting
    }

    fun extract(key: AEKey?, amount: Long, mode: Actionable): Long {
        if (key !in Companion.KEY_TYPE) return 0L

        val present = this.inventory.getLong(key)
        if (present <= 0) {
            return 0L
        }

        val extracting = min(amount, present)

        if (mode == Actionable.MODULATE) {
            val left = present - extracting
            this.inventory[key] = left

            this.size -= extracting
        }

        return extracting
    }

    fun getAvailableStacks(out: KeyCounter) {
        this.inventory.object2LongEntrySet().forEach { (key, amount) ->
            out.add(key, amount)
        }
    }
}