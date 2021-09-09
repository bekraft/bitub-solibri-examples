package bitub.solibri;

import bitub.solibri.model.ParameterThreshold;
import com.solibri.smc.api.ui.*;

import java.util.Arrays;

/**
 * GA1 - Geometry Acceptance Checks - Slope Acceptance Check UI
 */
class SlopeAcceptanceRuleUI {
    private final SlopeAcceptanceRule ruleInstance;
    private final UIContainer uiDefinition;

    public SlopeAcceptanceRuleUI(SlopeAcceptanceRule ruleInstance) {
        this.ruleInstance = ruleInstance;
        this.uiDefinition = createUI();
    }

    public UIContainer getUIContainer() {
        return uiDefinition;
    }

    private UIContainer createUI() {
        UIContainer uiContainer = UIContainerVertical.create(
                ruleInstance.resources.getString("bitub.rule.GA1.TITLE"),
                BorderType.LINE);

        uiContainer.addComponent(UILabel.create(
                ruleInstance.resources.getString("bitub.rule.GA1.DESCRIPTION")));

        // Component filter
        UIContainer componentFilter = UIContainerVertical.create();
        componentFilter.addComponent(UIRuleParameter.create(ruleInstance.paramComponentFilter));

        // Filter container
        UIContainer filterContainer = UIContainerHorizontal.create(
                ruleInstance.resources.getString("bitub.filterContainer.TITLE"), BorderType.LINE );

        filterContainer.addComponent(componentFilter);
        uiContainer.addComponent(filterContainer);

        UIContainer paramContainer = UIContainerVertical.create(
                ruleInstance.resources.getString("bitub.paramContainer.TITLE"), BorderType.LINE );

        paramContainer.addComponent(UIRuleParameter.create(ruleInstance.paramSlopeThreshold));
        paramContainer.addComponent(UIRuleParameter.create(ruleInstance.paramSlopeCutoff));

        Arrays.stream(ruleInstance.paramThresholdStages)
                .flatMap(t -> Arrays.stream(t.parameters))
                .forEach(p -> paramContainer.addComponent(UIRuleParameter.create(p)));

        uiContainer.addComponent(paramContainer);
        return uiContainer;
    }
}
