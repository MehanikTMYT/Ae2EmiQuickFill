package com.zaixiayesheng.ae2_emi_quick_fill.mixin;

import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Makes AE2's own EMI recipe handlers (crafting terminal, wireless crafting terminal,
 * pattern encoding terminal) respond to {@link EmiCraftContext.Type#CRAFTABLE}, which is
 * the context type EMI uses for shift-click / craft-key recipe transfers.
 *
 * <h2>Why this is needed</h2>
 * <p>EMI's {@code EmiRecipeFiller.performFill} calls {@code handler.canCraft()} before
 * {@code handler.craft()}. AE2's {@code AbstractRecipeHandler.canCraft} only runs its own
 * transfer simulation for {@code FILL_BUTTON} (the plus button). For {@code CRAFTABLE} it
 * falls back to {@code StandardRecipeHandler.canCraft}, which requires every ingredient to
 * be in the player inventory — so shift-click does nothing when materials are stored in
 * the ME network.
 *
 * <h2>How it works</h2>
 * <p>We redirect the single {@code EmiCraftContext.getType()} call inside {@code canCraft}
 * so that {@code CRAFTABLE} is seen as {@code FILL_BUTTON}. AE2's own feasibility check
 * then runs normally. The {@code craft()} method does not check context type, so once
 * {@code canCraft} passes the original fill packet logic executes unchanged — no transfer
 * code is rewritten.
 *
 * <h2>Target classes</h2>
 * <p>AE2 26.1 moved its EMI integration from {@code appeng.integration.modules.emi} to
 * {@code appeng.client.integrations.emi} (it was entirely absent in 26.1.12-beta and returned by
 * 26.1.12-SNAPSHOT). {@code appeng.client.integrations.emi.AbstractRecipeHandler} is package-private,
 * so it is referenced by string in {@link Mixin#targets()} rather than by a literal class.
 *
 * <p>{@code EmiEncodePatternHandler} (the pattern encoding terminal) extends it but overrides
 * {@code canCraft} with the same {@code getType() == FILL_BUTTON} guard, so it needs the redirect
 * too — patching the parent alone leaves the encoding terminal rejecting shift-click.
 * The crafting/wireless terminals use {@code EmiUseCraftingRecipeHandler}, which inherits the
 * parent {@code canCraft} and is covered by the first target.
 *
 * @author zaixiayesheng (在下叶笙)
 */
@Mixin(targets = {
        "appeng.client.integrations.emi.AbstractRecipeHandler",
        "appeng.client.integrations.emi.EmiEncodePatternHandler"
})
public class Ae2AbstractRecipeHandlerMixin {

    /**
     * Redirects {@code context.getType()} inside {@code canCraft}.
     *
     * @param context the EMI craft context passed to canCraft
     * @return {@code FILL_BUTTON} when the original type is {@code CRAFTABLE}; the original type otherwise
     */
    @Redirect(
            method = "canCraft",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/emi/emi/api/recipe/handler/EmiCraftContext;getType()"
                            + "Ldev/emi/emi/api/recipe/handler/EmiCraftContext$Type;"
            )
    )
    private EmiCraftContext.Type emiAe2ShiftCraft$treatCraftableAsFill(EmiCraftContext<?> context) {
        EmiCraftContext.Type type = context.getType();
        // Shift-click / craft-key sends CRAFTABLE; pretend it is FILL_BUTTON so AE2 runs its own check
        if (type == EmiCraftContext.Type.CRAFTABLE) {
            return EmiCraftContext.Type.FILL_BUTTON;
        }
        return type;
    }
}
