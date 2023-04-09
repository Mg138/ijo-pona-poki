package io.github.mg138.ae2shit.emi

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.render.EmiTexture
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import io.github.mg138.ae2shit.AE2ShitEmiPlugin.CRYSTAL_GROWTH_CATEGORY
import io.github.mg138.ae2shit.recipe.CrystalGrowthRecipe
import net.minecraft.fluid.Fluids

class CrystalGrowthEmiRecipe(recipe: CrystalGrowthRecipe) : EmiRecipe {
    private val id = recipe.id
    private val input = listOf(EmiIngredient.of(recipe.ingredients[0]))
    private val output = listOf(EmiStack.of(recipe.output))
    private val water = recipe.water

    override fun getCategory() = CRYSTAL_GROWTH_CATEGORY

    override fun getId() = this.id

    override fun getInputs() = this.input

    override fun getOutputs() = this.output

    override fun getDisplayWidth(): Int {
        return 80
    }

    override fun getDisplayHeight(): Int {
        return 18
    }

    override fun addWidgets(widgets: WidgetHolder) {
        // Add an arrow texture to indicate processing
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 36, 1)

        // Adds an input slot on the left
        widgets.addSlot(this.input[0], 0, 0)
        widgets.addSlot(EmiStack.of(Fluids.WATER, this.water), 18, 0)

        // Adds an output slot on the right
        // Note that output slots need to call `recipeContext` to inform EMI about their recipe context
        // This includes being able to resolve recipe trees, favorite stacks with recipe context, and more
        widgets.addSlot(this.output[0], 60, 0).recipeContext(this)
    }
}