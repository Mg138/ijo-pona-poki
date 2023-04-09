package io.github.mg138.ae2shit.blocks

import appeng.api.config.Actionable
import appeng.api.config.PowerMultiplier
import appeng.api.inventories.InternalInventory
import appeng.api.networking.IGridNode
import appeng.api.networking.energy.IEnergySource
import appeng.api.networking.ticking.IGridTickable
import appeng.api.networking.ticking.TickRateModulation
import appeng.api.networking.ticking.TickingRequest
import appeng.api.stacks.AEItemKey
import appeng.api.upgrades.IUpgradeInventory
import appeng.api.upgrades.IUpgradeableObject
import appeng.api.upgrades.UpgradeInventories
import appeng.api.util.AECableType
import appeng.blockentity.grid.AENetworkPowerBlockEntity
import appeng.core.definitions.AEItems
import appeng.core.settings.TickRates
import appeng.util.inv.AppEngInternalInventory
import io.github.mg138.ae2shit.recipe.CrystalGrowthRecipe
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.block.BlockState
import net.minecraft.fluid.Fluids
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.math.min


@Suppress("UnstableApiUsage")
class CrystalGrowthBlockEntity(
    pos: BlockPos,
    blockState: BlockState,
) : AENetworkPowerBlockEntity(CrystalGrowth.BLOCK_ENTITY, pos, blockState),
    IGridTickable,
    IUpgradeableObject,
    SingleSlotStorage<FluidVariant> {
    companion object {
        private val WATER_VARIANT = FluidVariant.of(Fluids.WATER)
        const val INVENTORY_SIZE = 9 * 3

        class CrystalGrowthInventory(private val be: CrystalGrowthBlockEntity) : AppEngInternalInventory(be, Companion.INVENTORY_SIZE, 1) {
            override fun isItemValid(slot: Int, stack: ItemStack?): Boolean {
                if (slot % 2 != 0) return false
                val world = this.be.world ?: return false

                val manager = world.recipeManager

                val result = manager.getFirstMatch(CrystalGrowthRecipe.RECIPE_TYPE, SimpleInventory(stack), world)

                return result.isPresent
            }
        }
    }

    private var processingTime = 0

    fun getMaxProcessingTime() = 1000
    fun getProcessingTime() = this.processingTime

    override fun onReady() {
        this.mainNode.setExposedOnSides(EnumSet.complementOf(EnumSet.of(Direction.UP, Direction.DOWN)))
        super.onReady()
        this.update()
    }

    override fun writeNbt(data: NbtCompound) {
        super.writeNbt(data)
        this.upgrades.writeToNBT(data, "upgrades")
        data.putLong("water", this.storedWater)
    }

    override fun loadTag(data: NbtCompound) {
        super.loadTag(data)
        this.upgrades.readFromNBT(data, "upgrades")
        this.storedWater = data.getLong("water")
    }

    override fun getCableConnectionType(dir: Direction): AECableType {
        return AECableType.COVERED
    }

    private val upgrades: IUpgradeInventory
    override fun getUpgrades() = this.upgrades

    override fun addAdditionalDrops(level: World, pos: BlockPos, drops: MutableList<ItemStack>) {
        super.addAdditionalDrops(level, pos, drops)

        drops.addAll(this.upgrades)
    }



    private val growthInventory = CrystalGrowthInventory(this)
    override fun getInternalInventory() = this.growthInventory

    private var storedWater = 0L

    override fun readFromStream(data: PacketByteBuf): Boolean {
        val c = super.readFromStream(data)

        for (i in 0 until this.internalInventory.size()) {
            this.internalInventory.setItemDirect(i, data.readItemStack())
        }

        return c
    }

    override fun writeToStream(data: PacketByteBuf) {
        super.writeToStream(data)

        for (i in 0 until this.internalInventory.size()) {
            data.writeItemStack(this.internalInventory.getStackInSlot(i))
        }
    }

    override fun insert(resource: FluidVariant?, maxAmount: Long, transaction: TransactionContext): Long {
        if (resource != Companion.WATER_VARIANT) return 0

        val spaceLeft = this.capacity - this.amount
        val insertable = min(spaceLeft, maxAmount)

        transaction.addOuterCloseCallback { result ->
            if (result.wasCommitted()) {
                this.storedWater += insertable

                this.update()
                this.saveChanges()
            }
        }

        return insertable
    }

    override fun extract(resource: FluidVariant?, maxAmount: Long, transaction: TransactionContext?): Long {
        return 0
    }

    override fun isResourceBlank() = this.resource.isBlank

    override fun getResource(): FluidVariant = Companion.WATER_VARIANT

    override fun getAmount() = this.storedWater

    override fun getCapacity() = 4 * FluidConstants.BUCKET



    private val recipeCache: MutableList<CrystalGrowthRecipe> = mutableListOf()
    private fun updateRecipeCache() {
        val manager = this.world?.recipeManager ?: return

        this.recipeCache.clear()

        this.internalInventory
            .mapNotNull { manager.getFirstMatch(CrystalGrowthRecipe.RECIPE_TYPE, SimpleInventory(it), this.world).getOrNull() }
            .forEach(this.recipeCache::add)

    }



    private var waterConsumption: Long = 0

    private fun growing()
        = this.recipeCache.size

    private fun canGrow()
        = this.recipeCache.isNotEmpty()

    private fun canWork() = this.canGrow() && this.amount > 0

    private fun update() {
        this.updateRecipeCache()

        this.waterConsumption = this.recipeCache.sumOf { it.water }

        if (!this.canWork()) {
            this.processingTime = 0
        }

        this.markForUpdate()
    }




    override fun onChangeInventory(inventory: InternalInventory, slot: Int) {
        this.update()

        this.mainNode.ifPresent { grid, node ->
            grid.tickManager.wakeDevice(node)
        }
    }

    override fun getExposedInventoryForSide(side: Direction?): InternalInventory {
        return InternalInventory.empty()
    }

    init {
        this.mainNode
            .setExposedOnSides(EnumSet.noneOf(Direction::class.java))
            .setIdlePowerUsage(0.0)
            .addService(IGridTickable::class.java, this)
        this.internalMaxPower = 4800.0

        this.upgrades = UpgradeInventories.forMachine(CrystalGrowthBlock, 3) { this.saveChanges() }
    }



    private val outputBuffer: Object2IntMap<AEItemKey> = Object2IntOpenHashMap()

    private fun findSingle(dir: Direction): Storage<ItemVariant>? {
        val result = ItemStorage.SIDED.find(this.world, this.pos.add(dir.vector), dir.opposite)
        if (result?.supportsInsertion() != true) return null

        return result
    }

    private fun getOutputTarget(): Storage<ItemVariant>? {
        return this.findSingle(Direction.UP) ?: this.findSingle(Direction.DOWN)
    }

    override fun getTickingRequest(node: IGridNode?): TickingRequest
        = TickingRequest(TickRates.Inscriber, false, false)

    override fun tickingRequest(node: IGridNode, ticksSinceLastCall: Int): TickRateModulation {
        if (this.outputBuffer.isNotEmpty()) {
            this.getOutputTarget()?.let { storage ->
                val transaction = Transaction.openOuter()

                this.outputBuffer.forEach { (key, stored) ->
                    val nested = transaction.openNested()

                    val inserted = storage.insert(ItemVariant.of(key.toStack()), stored.toLong(), nested).toInt()

                    nested.addOuterCloseCallback { result ->
                        if (result.wasCommitted()) {
                            this.outputBuffer.mergeInt(key, inserted, Int::minus)

                            if (this.outputBuffer.getInt(key) <= 0) {
                                this.outputBuffer.removeInt(key)
                            }
                        }
                    }

                    nested.commit()
                }

                transaction.commit()

                if (this.outputBuffer.isEmpty()) {
                    this.processingTime = 0
                }
            }
            return TickRateModulation.SLOWER
        }

        if (this.canWork()) {
            if (this.amount < this.waterConsumption) {
                return TickRateModulation.SLOWER
            }

            this.mainNode.ifPresent { grid ->
                val eg = grid.energyService
                var src: IEnergySource = this

                val growing = this.growing()

                val speedFactor = 1 + this.upgrades.getInstalledUpgrades(AEItems.SPEED_CARD)

                val powerConsumption = 20 * speedFactor * growing
                val powerThreshold = powerConsumption - 0.01

                var powerReq = this.extractAEPower(powerConsumption.toDouble(), Actionable.SIMULATE, PowerMultiplier.CONFIG)

                if (powerReq <= powerThreshold) {
                    src = eg
                    powerReq = eg.extractAEPower(powerConsumption.toDouble(), Actionable.SIMULATE, PowerMultiplier.CONFIG)
                }

                if (powerReq > powerThreshold) {
                    src.extractAEPower(
                        powerConsumption.toDouble(),
                        Actionable.MODULATE,
                        PowerMultiplier.CONFIG
                    )
                    if (this.getProcessingTime() == 0) {
                        this.processingTime += speedFactor
                    } else {
                        this.processingTime += ticksSinceLastCall * speedFactor
                    }
                    this.storedWater -= this.waterConsumption

                    this.saveChanges()
                }
            }

            if (this.getProcessingTime() > this.getMaxProcessingTime()) {
                this.processingTime = this.getMaxProcessingTime()

                this.recipeCache.map { it.output }.forEach {
                    this.outputBuffer.mergeInt(AEItemKey.of(it), it.count, Int::plus)
                }
            }
        }

        this.markForUpdate()

        return if (this.canWork()) {
            TickRateModulation.URGENT
        } else {
            TickRateModulation.IDLE
        }
    }
}