package bitub.solibri.model;

import com.solibri.smc.api.checking.DoubleParameter;
import com.solibri.smc.api.checking.Severity;

public class ParameterThreshold {

    final public Severity severity;

    final public DoubleParameter[] parameters;

    public ParameterThreshold(Severity severity, DoubleParameter ... parameters) {
        this.severity = severity;
        this.parameters = parameters;
    }

    public boolean isHoldingThresholds(double ... values) {
        for (int k = 0; k < Math.min(values.length, parameters.length); ++k) {
            if (parameters[k].getValue() < values[k])
                return false;
        }
        return true;
    }
}
