package bitub.solibri;

import bitub.solibri.model.ParameterThreshold;
import com.solibri.geometry.linearalgebra.MVector3d;
import com.solibri.geometry.linearalgebra.Vector3d;
import com.solibri.geometry.mesh.TriangleMesh;
import com.solibri.geometry.primitive3d.Polygon3d;
import com.solibri.smc.api.checking.*;
import com.solibri.smc.api.model.Component;
import com.solibri.smc.api.model.PropertyType;
import com.solibri.smc.api.ui.UIContainer;
import com.solibri.smc.api.visualization.ARGBColor;
import com.solibri.smc.api.visualization.Mesh;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * GA1 - Geometry Acceptance Checks - Slope Acceptance Check UI.
 */
public class SlopeAcceptanceRule extends OneByOneRule {

    static final String PARAM_SLOPE_THRESHOLD = "bitub.slopeThreshold";
    static final String PARAM_SLOPE_CUTOFF = "bitub.slopeCutoff";
    static final String PARAM_AREA_RATIO_PATTERN = "bitub.areaRatio.%s";

    private final RuleParameters params = RuleParameters.of(this);

    final RuleResources resources = RuleResources.of(this);

    final FilterParameter paramComponentFilter = this.getDefaultFilterParameter();
    final DoubleParameter paramSlopeThreshold = params.createDouble(PARAM_SLOPE_THRESHOLD, PropertyType.PERCENTAGE);
    final DoubleParameter paramSlopeCutoff = params.createDouble(PARAM_SLOPE_CUTOFF, PropertyType.PERCENTAGE);

    final ParameterThreshold lowSeverityThreshold = new ParameterThreshold(
            Severity.LOW, params.createDouble(String.format(PARAM_AREA_RATIO_PATTERN, Severity.LOW.name()), PropertyType.PERCENTAGE));
    final ParameterThreshold moderateSeverityThreshold = new ParameterThreshold(
            Severity.MODERATE, params.createDouble(String.format(PARAM_AREA_RATIO_PATTERN, Severity.MODERATE.name()), PropertyType.PERCENTAGE));
    final ParameterThreshold criticalSeverityThreshold = new ParameterThreshold(
            Severity.CRITICAL, params.createDouble(String.format(PARAM_AREA_RATIO_PATTERN, Severity.CRITICAL.name()), PropertyType.PERCENTAGE));

    final ParameterThreshold[] paramThresholdStages = new ParameterThreshold[] {
            criticalSeverityThreshold, moderateSeverityThreshold, lowSeverityThreshold
    };

    private final ARGBColor markColor = ARGBColor.create(255, 0, 127, 255);
    private final Logger log;
    private final SlopeAcceptanceRuleUI uiDefinition;
    private final String messagePattern;

    // Per run variables
    private double thresholdAngle;
    private double cutoffAngle;

    public SlopeAcceptanceRule() {
        log = LoggerFactory.getILoggerFactory().getLogger(getClass().getCanonicalName());
        uiDefinition = new SlopeAcceptanceRuleUI(this);
        messagePattern = resources.getString("bitub.rule.GA1.MESSAGE");
    }

    @Override
    public UIContainer getParametersUIDefinition() {
        return uiDefinition.getUIContainer();
    }

    @Override
    public PreCheckResult preCheck() {
        // 45 deg = 100%
        this.thresholdAngle = Math.PI / 4 * paramSlopeThreshold.getValue();
        this.cutoffAngle = Math.PI / 4 * paramSlopeCutoff.getValue();
        log.info("Start check with threshold rad = {}, slope = {}", thresholdAngle, paramSlopeThreshold.getValue());

        return super.preCheck();
    }

    @Override
    public Collection<Result> check(Component component, ResultFactory resultFactory) {
        TriangleMesh candidateMesh = component.getTriangleMesh().filter(t -> {
            final double angle = getAngle(t.getNormal());
            return angle < cutoffAngle;
        });

        double totalArea = candidateMesh.getArea();
        TriangleMesh resultMesh = candidateMesh.filter(t -> {
            final double angle = getAngle(t.getNormal());
            return angle < thresholdAngle;
        });

        Optional<Double> optRatio = resultMesh.toTriangleCollection().stream()
                .map(Polygon3d::getArea)
                .reduce(Double::sum)
                .map(area -> area / totalArea);

        List<Result> results = new ArrayList<>();
        optRatio.ifPresent(ratio -> {
            Arrays.stream(paramThresholdStages)
                    .filter(s -> !s.isHoldingThresholds(ratio))
                    .findFirst()
                    .ifPresent(stage -> {
                        results.add(resultFactory.create(String.format(messagePattern,
                                        component.getName(), paramSlopeThreshold.getValue() * 100, ratio * 100),
                                        resources.getString("bitub.rule.GA1.TITLE"))
                                .withSeverity(stage.severity)
                                .withVisualization(v -> {
                                    v.addVisualizationItem(Mesh.create(markColor, resultMesh.toTriangleCollection()));
                                }));
                    });
        });

        return results;
    }

    @Override
    public void postCheck() {
        log.info("Completed check.");
    }

    private double getAngle(MVector3d normal) {
        return normal.angle(Vector3d.UNIT_Z);
    }
}
