package io.github.mg138.ae2shit

import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.EmiStack
import io.github.mg138.ae2shit.blocks.CrystalGrowthBlock
import io.github.mg138.ae2shit.emi.CrystalGrowthEmiRecipe
import io.github.mg138.ae2shit.recipe.CrystalGrowthRecipe
import net.minecraft.util.Identifier

object AE2ShitEmiPlugin : EmiPlugin {
    private val CRYSTAL_GROWTH: EmiStack = EmiStack.of(CrystalGrowthBlock)
    val CRYSTAL_GROWTH_CATEGORY = EmiRecipeCategory(Identifier(CrystalGrowthRecipe.crystal_growth), this.CRYSTAL_GROWTH)

    override fun register(registry: EmiRegistry) {
        registry.addCategory(this.CRYSTAL_GROWTH_CATEGORY)
        registry.addWorkstation(this.CRYSTAL_GROWTH_CATEGORY, this.CRYSTAL_GROWTH)

        val manager = registry.recipeManager

        manager.listAllOfType(CrystalGrowthRecipe.RECIPE_TYPE).forEach {
            registry.addRecipe(CrystalGrowthEmiRecipe(it))
        }
    }
}