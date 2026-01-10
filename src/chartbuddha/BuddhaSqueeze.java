package chartbuddha;

import java.awt.Color;

import com.motivewave.platform.sdk.common.DataContext;
import com.motivewave.platform.sdk.common.DataSeries;
import com.motivewave.platform.sdk.common.Defaults;
import com.motivewave.platform.sdk.common.Enums;
import com.motivewave.platform.sdk.common.Inputs;
import com.motivewave.platform.sdk.common.desc.ColorDescriptor;
import com.motivewave.platform.sdk.common.desc.DoubleDescriptor;
import com.motivewave.platform.sdk.common.desc.GuideDescriptor;
import com.motivewave.platform.sdk.common.desc.IndicatorDescriptor;
import com.motivewave.platform.sdk.common.desc.InputDescriptor;
import com.motivewave.platform.sdk.common.desc.IntegerDescriptor;
import com.motivewave.platform.sdk.common.desc.ValueDescriptor;
import com.motivewave.platform.sdk.study.Study;
import com.motivewave.platform.sdk.study.StudyHeader;

@StudyHeader(
        namespace = "com.chartbuddha",
        id = "BuddhaSqueeze",
        name = "Buddha Squeeze",
        desc = "The Buddha Squeeze highlights volatility compression and signals when the market is poised to break out with directional momentum.",
        menu = "Chart Buddha",
        signals = false,
        overlay = false,
        studyOverlay = true
)

public class BuddhaSqueeze extends Study {

    enum Values {
        OSC, LINREG
    }

    // Buddha Bands color palette
    private static final Color CLR_POS_INC = new Color(000, 128, 000, 255); // Dark Green
    private static final Color CLR_POS_DEC = new Color(255, 000, 000, 255); // Light Red
    private static final Color CLR_NEG_INC = new Color(128, 000, 000, 255); // Dark Red
    private static final Color CLR_NEG_DEC = new Color(000, 255, 000, 255); // Light Green

    private static final String INPUT = "input";
    private static final String PERIOD = "period";
    private static final String BB_STD = "bb_std";
    private static final String KC_STD = "kc_std";
    private static final String COLOR_POS_INC = "color_pos_inc";
    private static final String COLOR_POS_DEC = "color_pos_dec";
    private static final String COLOR_NEG_INC = "color_neg_inc";
    private static final String COLOR_NEG_DEC = "color_neg_dec";

    /* 
    ** Linear Regression Curve
    ** 
    ** Linear regression curve. A line that best fits the prices specified over a user-defined time period. 
    ** It is calculated using the least squares method. The result of this function is calculated using the 
    ** formula: 
    ** linear regression curve = intercept + slope * (length - 1 - offset), 
    ** where intercept and slope are the values calculated with the least squares method on source series 
    **
    ** ta.linreg(source, length, offset) → series float
     */
    // === LINEAR REGRESSION CURVE FUNCTION ===
    private Double linreg(DataSeries series, int index, Object source, int period, int offset) {
        if (index < period - 1) {
            return null;
        }

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < period; i++) {
            int barIndex = index - i;
            double y = series.getDouble(barIndex, source);
            if (Double.isNaN(y)) {
                return null;
            }

            double x = i;
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double slope = (period * sumXY - sumX * sumY) / (period * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / period;
        return intercept + slope * (period - 1 - offset);
    }

    @Override
    /* === INITIALIZE === */
    public void initialize(Defaults defaults) {
        // === SETTING DESCRIPTOR ===
        var sd = createSD();
        var tab = sd.addTab("General");

        var General = tab.addGroup("Inputs");
        General.addRow(new InputDescriptor(INPUT, "Source", Enums.BarInput.CLOSE));
        General.addRow(new IntegerDescriptor(PERIOD, "Period", 21, 1, 999, 1));
        General.addRow(new DoubleDescriptor(BB_STD, "BB StdDev", 2.1, 0.1, 999, 0.1));
        General.addRow(new DoubleDescriptor(KC_STD, "KC StdDev", 1.4, 0.1, 999, 0.1));
        tab = sd.addTab("Display");

        var display = tab.addGroup("Histogram");
        display.addRow(new ColorDescriptor(COLOR_POS_INC, "Positive Increasing", CLR_POS_INC));
        display.addRow(new ColorDescriptor(COLOR_POS_DEC, "Positive Decreasing", CLR_POS_DEC));
        display.addRow(new ColorDescriptor(COLOR_NEG_INC, "Negative Increasing", CLR_NEG_INC));
        display.addRow(new ColorDescriptor(COLOR_NEG_DEC, "Negative Decreasing", CLR_NEG_DEC));

        var guides = tab.addGroup("Guide");
        var mg = new GuideDescriptor(Inputs.MIDDLE_GUIDE, "Middle Guide", 0, -999.1, 999.1, .1, true);
        mg.setDash(null);
        guides.addRow(mg);

        var indicator = tab.addGroup("Indicator");
        indicator.addRow(new IndicatorDescriptor(Inputs.IND, "Indicator", defaults.getLineColor(), null, false, true, true));

        // Quick Settings (Tool Bar and Popup Editor)
        sd.addQuickSettings(INPUT, PERIOD, BB_STD, KC_STD);
        sd.addQuickSettings(COLOR_POS_INC, COLOR_POS_DEC, COLOR_NEG_INC, COLOR_NEG_DEC);

        // === RUNTIME DESCRIPTOR ===
        var desc = createRD();
        desc.setLabelSettings(INPUT, PERIOD, BB_STD, KC_STD);
        desc.exportValue(new ValueDescriptor(Values.OSC, "osc", new String[]{INPUT, PERIOD, BB_STD, KC_STD}));
        desc.declareBars(Values.OSC, null);
        desc.declareIndicator(Values.OSC, Inputs.IND);
        desc.setRangeKeys(Values.OSC);
    }

    @Override
    public void onLoad(Defaults defaults) {
        int p1 = getSettings().getInteger(PERIOD);
        setMinBars(p1);
    }

    @Override
    protected void calculate(int index, DataContext ctx) {
        int period = getSettings().getInteger(PERIOD);
        Object source = getSettings().getInput(INPUT, Enums.BarInput.CLOSE);
        var series = ctx.getDataSeries();

        // Always calculate and store the linreg input (close - oscBase/2) for every bar
        // This is needed so linreg can look back at historical values
        Double highest = series.highest(index, period, Enums.BarInput.HIGH);
        Double lowest = series.lowest(index, period, Enums.BarInput.LOW);
        Double ema = series.ma(Enums.MAMethod.EMA, index, period, source);

        if (highest != null && lowest != null && ema != null) {
            Double oscBase = (highest + lowest) / 2.0 + ema;
            Float closeVal = series.getClose(index);
            Double inputToLinReg = closeVal - (oscBase / 2.0);
            series.setDouble(index, Values.LINREG, inputToLinReg);
        }

        // Only calculate oscillator once we have enough data for linreg
        if (index < (period * 2) - 1) {
            return;
        }

        // Calculate linear regression on the stored LINREG values
        Double osc = linreg(series, index, Values.LINREG, period, 0);
        if (osc == null) {
            return;
        }

        // Store oscillator value (scaled by 100 like Pine Script)
        series.setDouble(index, Values.OSC, osc * 100);

        // Color Logic
        Color color_pos_Inc = getSettings().getColor(COLOR_POS_INC);
        Color color_pos_Dec = getSettings().getColor(COLOR_POS_DEC);
        Color color_neg_Inc = getSettings().getColor(COLOR_NEG_INC);
        Color color_neg_Dec = getSettings().getColor(COLOR_NEG_DEC);

        Double prevOsc = series.getDouble(index - 1, Values.OSC);
        if (prevOsc != null) {
            boolean increasing = (osc * 100) > prevOsc;
            if (increasing) {
                series.setBarColor(index, Values.OSC, osc > 0 ? color_pos_Inc : color_neg_Inc);
            } else {
                series.setBarColor(index, Values.OSC, osc > 0 ? color_pos_Dec : color_neg_Dec);
            }
        }

        series.setComplete(index);
    }

}
