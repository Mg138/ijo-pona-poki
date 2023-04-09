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
import appeng.blockentity.misc.InscriberRecipes
import appeng.core.definitions.AEItems
import appeng.core.settings.TickRates
import appeng.me.helpers.MachineSource
import appeng.recipes.handlers.InscriberProcessType
import appeng.recipes.handlers.InscriberRecipe
import appeng.util.inv.AppEngInternalInventory
import appeng.util.inv.CombinedInternalInventory
import appeng.util.inv.FilteredInternalInventory
import appeng.util.inv.filter.IAEItemFilter
import net.minecraft.block.BlockState
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import java.util.EnumSet.*


/**
 * Largely copied from AE2's implementation.
 */
class AdvancedInscriberBlockEntity(
    pos: BlockPos,
    blockState: BlockState,
) : AENetworkPowerBlockEntity(AdvancedInscriber.BLOCK_ENTITY, pos, blockState),
    IGridTickable,
    IUpgradeableObject {
    private var processingTime = 0

    fun getMaxProcessingTime() = 100
    fun getProcessingTime() = this.processingTime

    var clientStart: Long = 0
        private set

    var isSmash = false
        set(smash) {
            if (smash && !this.isSmash) {
                this.clientStart = System.currentTimeMillis()
            }
            field = smash
        }

    private val upgrades: IUpgradeInventory
    override fun getUpgrades(): IUpgradeInventory = this.upgrades



    private val topItemHandler = AppEngInternalInventory(this, 1)
    private val bottomItemHandler = AppEngInternalInventory(this, 1)
    private val sideItemHandler = AppEngInternalInventory(this, 2)

    private val inv = CombinedInternalInventory(
        this.topItemHandler, this.bottomItemHandler, this.sideItemHandler
    )

    override fun getInternalInventory() = this.inv


    private val topItemHandlerExtern: InternalInventory
    private val bottomItemHandlerExtern: InternalInventory
    private val sideItemHandlerExtern: InternalInventory

    init {
        this.mainNode
            .setExposedOnSides(noneOf(Direction::class.java))
            .setIdlePowerUsage(0.0)
            .addService(IGridTickable::class.java, this)
        this.internalMaxPower = 3200.0

        this.upgrades = UpgradeInventories.forMachine(AdvancedInscriberBlock, 5) { this.saveChanges() }

        val filter = object : IAEItemFilter {
            private val be = this@AdvancedInscriberBlockEntity

            override fun allowExtract(inv: InternalInventory, slot: Int, amount: Int): Boolean {
                return if (this.be.isSmash) {
                    false
                } else {
                    inv === this.be.topItemHandler || inv === this.be.bottomItemHandler || slot == 1
                }
            }

            override fun allowInsert(inv: InternalInventory, slot: Int, stack: ItemStack): Boolean {
                return when {
                    slot == 1 -> {
                        false
                    }
                    this.be.isSmash -> {
                        false
                    }
                    inv !== this.be.topItemHandler && inv !== this.be.bottomItemHandler -> {
                        true
                    }
                    AEItems.NAME_PRESS.isSameAs(stack) -> {
                        true
                    }
                    else -> {
                        InscriberRecipes.isValidOptionalIngredient(this.be.getWorld(), stack)
                    }
                }
            }
        }

        this.topItemHandlerExtern = FilteredInternalInventory(this.topItemHandler, filter)
        this.bottomItemHandlerExtern = FilteredInternalInventory(this.bottomItemHandler, filter)
        this.sideItemHandlerExtern = FilteredInternalInventory(this.sideItemHandler, filter)
    }

    override fun getSubInventory(id: Identifier): InternalInventory? {
        return if (id == STORAGE) {
            this.internalInventory
        } else {
            if (id == UPGRADES) {
                this.upgrades
            } else {
                super.getSubInventory(id)
            }
        }
    }

    override fun getExposedInventoryForSide(facing: Direction): InternalInventory {
        return if (facing == this.up) {
            this.topItemHandlerExtern
        } else {
            if (facing == this.up.opposite) {
                this.bottomItemHandlerExtern
            } else {
                this.sideItemHandlerExtern
            }
        }
    }

    override fun onReady() {
        this.mainNode.setExposedOnSides(complementOf(of(this.forward)))
        super.onReady()
    }

    override fun setOrientation(inForward: Direction?, inUp: Direction?) {
        super.setOrientation(inForward, inUp)
        this.mainNode.setExposedOnSides(complementOf(of(this.forward)))
        this.powerSides = complementOf(of(this.forward))
    }


    override fun writeNbt(data: NbtCompound) {
        super.writeNbt(data)
        this.upgrades.writeToNBT(data, "upgrades")
    }

    override fun loadTag(data: NbtCompound) {
        super.loadTag(data)
        this.upgrades.readFromNBT(data, "upgrades")
    }

    override fun readFromStream(data: PacketByteBuf): Boolean {
        val c = super.readFromStream(data)

        if (data.readBoolean()) {
            this.isSmash = true
        }

        for (i in 0 until this.inv.size()) {
            this.inv.setItemDirect(i, data.readItemStack())
        }
        this.cachedTask = null
        return c
    }

    override fun writeToStream(data: PacketByteBuf) {
        super.writeToStream(data)
        data.writeBoolean(this.isSmash)

        for (i in 0 until this.inv.size()) {
            data.writeItemStack(this.inv.getStackInSlot(i))
        }
    }

    override fun saveVisualState(data: NbtCompound) {
        super.saveVisualState(data)
        data.putBoolean("smash", this.isSmash)
    }

    override fun loadVisualState(data: NbtCompound) {
        super.loadVisualState(data)
        this.isSmash = data.getBoolean("smash")
    }

    override fun getCableConnectionType(dir: Direction): AECableType {
        return AECableType.COVERED
    }

    override fun addAdditionalDrops(level: World, pos: BlockPos, drops: MutableList<ItemStack>) {
        super.addAdditionalDrops(level, pos, drops)

        drops.addAll(this.upgrades)
    }

    override fun getTickingRequest(node: IGridNode)
        = TickingRequest(TickRates.Inscriber, this.inv.isEmpty || !this.hasWork(), false)

    private fun hasWork(): Boolean {
        if (this.getTask() != null) {
            return true
        }
        this.processingTime = 0

        return this.isSmash
    }

    private var cachedTask: InscriberRecipe? = null

    override fun onChangeInventory(inv: InternalInventory, slot: Int) {
        if (this.internalInventory.isEmpty) {
            this.processingTime = 0
        }
        if (!this.isSmash) {
            this.markForUpdate()
        }
        this.cachedTask = null
        this.mainNode.ifPresent { grid, node ->
            grid.tickManager.wakeDevice(node)
        }
    }

    fun getTask(): InscriberRecipe? {
        if (this.cachedTask == null && this.world != null) {
            val input = this.sideItemHandler.getStackInSlot(0)
            val plateA = this.topItemHandler.getStackInSlot(0)
            val plateB = this.bottomItemHandler.getStackInSlot(0)

            if (input.isEmpty) {
                return null
            }

            this.cachedTask = InscriberRecipes.findRecipe(this.world, input, plateA, plateB, true)
        }
        return this.cachedTask
    }

    override fun tickingRequest(node: IGridNode, ticksSinceLastCall: Int): TickRateModulation {
        this.isSmash = false

        if (this.hasWork()) {
            this.mainNode.ifPresent { grid ->
                val eg = grid.energyService
                var src: IEnergySource = this

                val speedFactor = 1 + this.upgrades.getInstalledUpgrades(AEItems.SPEED_CARD) * 3
                val powerConsumption = 20 * speedFactor
                val powerThreshold = powerConsumption - 0.01
                var powerReq = this.extractAEPower(powerConsumption.toDouble(), Actionable.SIMULATE, PowerMultiplier.CONFIG)

                if (powerReq <= powerThreshold) {
                    src = eg
                    powerReq = eg.extractAEPower(powerConsumption.toDouble(), Actionable.SIMULATE, PowerMultiplier.CONFIG)
                }

                if (powerReq > powerThreshold) {
                    src.extractAEPower(powerConsumption.toDouble(), Actionable.MODULATE, PowerMultiplier.CONFIG)
                    if (this.getProcessingTime() == 0) {
                        this.processingTime += speedFactor
                    } else {
                        this.processingTime += ticksSinceLastCall * speedFactor
                    }
                }
            }

            if (this.getProcessingTime() > this.getMaxProcessingTime()) {
                this.processingTime = this.getMaxProcessingTime()

                this.getTask()?.let { task ->
                    val output = task.output.copy()

                    if (this.sideItemHandler.insertItem(1, output, true).isEmpty) {
                        this.isSmash = true
                        this.processingTime = 0

                        this.sideItemHandler.insertItem(1, output, false)

                        if (task.processType == InscriberProcessType.PRESS) {
                            this.topItemHandler.extractItem(0, 1, false)
                            this.bottomItemHandler.extractItem(0, 1, false)
                        }
                        this.sideItemHandler.extractItem(0, 1, false)
                    }
                }
            }
        }

        if (this.sideItemHandler.getStackInSlot(1).item != Items.AIR) {
            val output = this.sideItemHandler.getStackInSlot(1)
            val itemKey = AEItemKey.of(output)

            this.mainNode.grid?.storageService?.inventory?.insert(
                itemKey,
                output.count.toLong(),
                Actionable.MODULATE,
                MachineSource(this)
            )?.let { inserted ->
                this.sideItemHandler.extractItem(1, inserted.toInt(), false)
                this.saveChanges()
            }
        }

        this.markForUpdate()

        return if (this.hasWork()) {
            TickRateModulation.URGENT
        } else if (!this.inv.isEmpty) {
            TickRateModulation.SLOWER
        } else {
            TickRateModulation.SLEEP
        }
    }
}