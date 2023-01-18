package io.github.mg138.ijo_pona_poki.recipe

import com.google.gson.JsonObject
import io.github.mg138.ijo_pona_poki.IjoPonaPoki
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.*
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World

class CrystalGrowthRecipe(
    private val id: Identifier,
    private val input: Ingredient,
    private val output: ItemStack,
    val water: Long
) : Recipe<SimpleInventory> {
    companion object {
        const val crystal_growth = "${IjoPonaPoki.MOD_ID}:crystal_growth"

        val RECIPE_TYPE: RecipeType<CrystalGrowthRecipe>
            = RecipeType.register(this.crystal_growth)

        val SERIALIZER: RecipeSerializer<CrystalGrowthRecipe> =
            RecipeSerializer.register(
                this.crystal_growth, object : RecipeSerializer<CrystalGrowthRecipe> {
                    override fun read(id: Identifier, json: JsonObject): CrystalGrowthRecipe {
                        val input = Ingredient.fromJson(json["input"])
                        val output = ShapedRecipe.outputFromJson(json.getAsJsonObject("output"))
                        val water = json["water"].asLong

                        return CrystalGrowthRecipe(id, input, output, water)
                    }

                    override fun read(id: Identifier, buf: PacketByteBuf): CrystalGrowthRecipe {
                        val input = Ingredient.fromPacket(buf)
                        val output = buf.readItemStack()
                        val water = buf.readLong()

                        return CrystalGrowthRecipe(id, input, output, water)
                    }

                    override fun write(buf: PacketByteBuf, recipe: CrystalGrowthRecipe) {
                        recipe.input.write(buf)
                        buf.writeItemStack(recipe.output)
                        buf.writeLong(recipe.water)
                    }
                }
            )
    }

    override fun matches(inventory: SimpleInventory, world: World?)
        = inventory.anyMatch(this.input::test)

    override fun getIngredients(): DefaultedList<Ingredient> = DefaultedList.of<Ingredient>().also { it.add(this.input) }

    override fun craft(inventory: SimpleInventory?): ItemStack
        = this.getOutput().copy()

    override fun fits(width: Int, height: Int) = true

    override fun getOutput() = this.output

    override fun getId() = this.id
    override fun getSerializer() = Companion.SERIALIZER
    override fun getType() = Companion.RECIPE_TYPE
}