package io.github.mg138.ijo_pona_poki.items

import appeng.api.client.StorageCellModels
import appeng.core.definitions.AEItems
import appeng.menu.me.common.MEStorageMenu
import io.github.mg138.ijo_pona_poki.IjoPonaPoki.DEFAULT_SETTINGS
import io.github.mg138.ijo_pona_poki.IjoPonaPoki.id
import net.minecraft.item.Item
import net.minecraft.util.registry.Registry



@Suppress("unused")
object DISKDrives {
    private fun String.model() = id("model/drive/cells/$this")

    private fun item(name: String, item: Item): Item {
        return Registry.register(Registry.ITEM, id(name), item)
    }
    private fun diskCell(name: String, item: DISKDrive): Item {
        StorageCellModels.registerModel(item, name.model())

        return this.item(name, item)
    }

    val DISK_HOUSING = this.item("disk_housing", Item(DEFAULT_SETTINGS))

    private fun diskId(kilo: String) = "disk_drive_$kilo"
    private fun portableDiskId(kilo: String) = "portable_disk_$kilo"

    private fun diskCapacity(kilo: Long) = kilo * 1000
    private fun portableDiskCapacity(kilo: Long) = this.diskCapacity(kilo) / 3

    val DISK_DRIVE_1K  = this.diskCell(
        this.diskId("1k" ),
        DISKDrive(
            AEItems.CELL_COMPONENT_1K,
            this.DISK_HOUSING,
            this.diskCapacity(1 ),
            0.5
        )
    )
    val DISK_DRIVE_4K  = this.diskCell(
        this.diskId("4k" ),
        DISKDrive(
            AEItems.CELL_COMPONENT_4K,
            this.DISK_HOUSING,
            this.diskCapacity(4 ),
            1.0
        )
    )
    val DISK_DRIVE_16K = this.diskCell(
        this.diskId("16k"),
        DISKDrive(
            AEItems.CELL_COMPONENT_16K,
            this.DISK_HOUSING,
            this.diskCapacity(16),
            1.5
        )
    )
    val DISK_DRIVE_64K = this.diskCell(
        this.diskId("64k"),
        DISKDrive(
            AEItems.CELL_COMPONENT_64K,
            this.DISK_HOUSING,
            this.diskCapacity(64),
            2.0
        )
    )

    val PORTABLE_DISK_1K  = this.item(
        this.portableDiskId("1k" ),
        PortableDISK(
            this.portableDiskCapacity(1 ),
            0.5,
            id(this.portableDiskId("1k" )),
            MEStorageMenu.PORTABLE_ITEM_CELL_TYPE
        )
    )
    val PORTABLE_DISK_4K  = this.item(
        this.portableDiskId("4k" ),
        PortableDISK(
            this.portableDiskCapacity(4 ),
            1.0,
            id(this.portableDiskId("4k" )),
            MEStorageMenu.PORTABLE_ITEM_CELL_TYPE
        )
    )
    val PORTABLE_DISK_16K = this.item(
        this.portableDiskId("16k"),
        PortableDISK(
            this.portableDiskCapacity(16),
            1.5,
            id(this.portableDiskId("16k")),
            MEStorageMenu.PORTABLE_ITEM_CELL_TYPE
        )
    )
    val PORTABLE_DISK_64K = this.item(
        this.portableDiskId("64k"),
        PortableDISK(
            this.portableDiskCapacity(64),
            2.0,
            id(this.portableDiskId("64k")),
            MEStorageMenu.PORTABLE_ITEM_CELL_TYPE
        )
    )
}