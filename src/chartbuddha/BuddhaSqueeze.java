package chartbuddha;

import java.awt.Color;

import com.motivewave.platform.sdk.common.Coordinate;
import com.motivewave.platform.sdk.common.DataContext;
import com.motivewave.platform.sdk.common.DataSeries;
import com.motivewave.platform.sdk.common.Defaults;
import com.motivewave.platform.sdk.common.Enums;
import com.motivewave.platform.sdk.common.Inputs;
import com.motivewave.platform.sdk.common.desc.ColorDescriptor;
import com.motivewave.platform.sdk.common.desc.DoubleDescriptor;
import com.motivewave.platform.sdk.common.desc.InputDescriptor;
import com.motivewave.platform.sdk.common.desc.IntegerDescriptor;
import com.motivewave.platform.sdk.common.desc.MarkerDescriptor;
import com.motivewave.platform.sdk.common.desc.ValueDescriptor;
import com.motivewave.platform.sdk.draw.Marker;
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

    // Standard deviation values for Bollinger Bands and Keltner Channels
    enum Values {
        OSC, LINREG, SQUEEZE_COUNT, SQUEEZE_MARKER, ATR_EMA, ATR_VALUE
    }

    // Buddha Bands color palette
    private static final Color CLR_POS_INC = new Color(000, 128, 000, 255); // Dark Green
    private static final Color CLR_POS_DEC = new Color(255, 000, 000, 255); // Light Red
    private static final Color CLR_NEG_INC = new Color(128, 000, 000, 255); // Dark Red
    private static final Color CLR_NEG_DEC = new Color(000, 255, 000, 255); // Light Green
    private static final Color CLR_SQUEEZE = new Color(255, 255, 000, 255); // Yellow
    private static final Color CLR_GUIDE = new Color(000, 000, 000, 255); // White
    // Color settings keys
    private static final String COLOR_POS_INC = "color_pos_inc";
    private static final String COLOR_POS_DEC = "color_pos_dec";
    private static final String COLOR_NEG_INC = "color_neg_inc";
    private static final String COLOR_NEG_DEC = "color_neg_dec";
    private static final String COLOR_SQUEEZE = "color_squeeze";
    private static final String COLOR_GUIDE = "color_guide";
    // Input settings keys
    private static final String INPUT = "input";
    private static final String PERIOD = "period";
    private static final String BB_STD = "bb_std";
    private static final String KC_STD = "kc_std";
    // Indicator settings keys
    // private static final String IND_GUIDE = "ind_guide";
    // private static final String IND_SQUEEZE = "ind_squeeze";
    private static final String SQUEEZE_MARKER = "squeeze_marker";

    /* 
    Linear Regression Curve
    
    Linear regression curve. A line that best fits the prices specified over a user-defined time period. 
    It is calculated using the least squares method. The result of this function is calculated using the 
    formula: 
    linear regression curve = intercept + slope * (length - 1 - offset), 
    where intercept and slope are the values calculated with the least squares method on source series 

    ta.linreg(source, length, offset) → series float
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

            // X-axis: oldest bar (i=period-1) has x=0, newest bar (i=0) has x=period-1
            double x = period - 1 - i;
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double slope = (period * sumXY - sumX * sumY) / (period * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / period;
        return intercept + slope * (period - 1 - offset);
    }

    // === AVERAGE TRUE RANGE (ATR) FUNCTION ===
    private Double atr(DataSeries series, int index, int period) {
        if (index < period) {
            return null;
        }

        double sum = 0;
        for (int i = 0; i < period; i++) {
            int barIndex = index - i;

            Float high = series.getHigh(barIndex);
            Float low = series.getLow(barIndex);

            // For first bar, use high-low as true range (no previous close available)
            if (barIndex == 0) {
                sum += (high - low);
                continue;
            }

            Float prevClose = series.getClose(barIndex - 1);

            double tr1 = high - low;
            double tr2 = Math.abs(high - prevClose);
            double tr3 = Math.abs(low - prevClose);
            double trueRange = Math.max(tr1, Math.max(tr2, tr3));

            sum += trueRange;
        }

        return sum / period;
    }

    // === STANDARD DEVIATION FUNCTION ===
    private Double stdev(DataSeries series, int index, Object source, int period) {
        if (index < period - 1) {
            return null;
        }

        // Calculate mean
        double sum = 0;
        for (int i = 0; i < period; i++) {
            double value = series.getDouble(index - i, source);
            if (Double.isNaN(value)) {
                return null;
            }
            sum += value;
        }
        double mean = sum / period;

        // Calculate variance
        double variance = 0;
        for (int i = 0; i < period; i++) {
            double value = series.getDouble(index - i, source);
            double diff = value - mean;
            variance += diff * diff;
        }
        variance = variance / period;

        return Math.sqrt(variance);
    }

    // === SQUEEZE DETECTION FUNCTION ===
    private Boolean buddhaSqueeze(DataSeries series, int index, Object source, int length, double stdKC, double stdBB) {
        Double atrValue = atr(series, index, length);
        Double stdevValue = stdev(series, index, source, length);

        if (atrValue == null || stdevValue == null) {
            return null;
        }

        double kcWidth = stdKC * atrValue;
        double bbWidth = stdBB * stdevValue;

        // Squeeze occurs when BB width is less than or very close to KC width
        // Using 1.08 threshold for fine-tuned sensitivity (8% tolerance)
        boolean squeeze = kcWidth != 0 ? (bbWidth / kcWidth) < 1.08 : false;
        return squeeze;
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

        var squeeze = tab.addGroup("Squeeze");
        squeeze.addRow(new MarkerDescriptor(SQUEEZE_MARKER, "Squeeze Dot",
                Enums.MarkerType.CIRCLE, Enums.Size.MEDIUM, CLR_SQUEEZE, null, true, true));
        squeeze.addRow(new ColorDescriptor(COLOR_SQUEEZE, "Squeeze Color", CLR_SQUEEZE));
        squeeze.addRow(new ColorDescriptor(COLOR_GUIDE, "No Squeeze Color", CLR_GUIDE));

        sd.addQuickSettings(INPUT, PERIOD, BB_STD, KC_STD);
        sd.addQuickSettings(COLOR_SQUEEZE, COLOR_POS_INC, COLOR_POS_DEC, COLOR_NEG_INC, COLOR_NEG_DEC);

        // === RUNTIME DESCRIPTOR ===
        var desc = createRD();
        desc.setLabelSettings(INPUT, PERIOD, BB_STD, KC_STD);
        desc.exportValue(new ValueDescriptor(Values.OSC, "osc", new String[]{INPUT, PERIOD, BB_STD, KC_STD}));
        desc.declareBars(Values.OSC, null);
        desc.declareIndicator(Values.OSC, Inputs.IND);
        desc.declareIndicator(Values.SQUEEZE_MARKER, SQUEEZE_MARKER);
        desc.setRangeKeys(Values.OSC);
        desc.setTopInsetPixels(50);
        desc.setBottomInsetPixels(50);
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

        // Regime-based volatility adjustment
        double stdBB;
        double stdKC;

        // Calculate ATR and its 50-period EMA for volatility regime detection
        Double currentAtr = atr(series, index, period);
        if (currentAtr != null) {
            // Store ATR value in series for EMA calculation
            series.setDouble(index, Values.ATR_VALUE, currentAtr);

            // Calculate EMA of ATR using stored values
            Double atrEma = series.ma(Enums.MAMethod.EMA, index, 50, Values.ATR_VALUE);

            // Determine volatility regime and set multipliers
            if (atrEma != null && currentAtr > atrEma) {
                // High volatility regime
                stdBB = 2.2;
                stdKC = 1.7;
            } else {
                // Low volatility regime
                stdBB = 1.8;
                stdKC = 1.3;
            }
        } else {
            // Fallback to user settings if ATR calculation fails
            stdBB = getSettings().getDouble(BB_STD);
            stdKC = getSettings().getDouble(KC_STD);
        }

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
            Double currentOsc = series.getDouble(index, Values.OSC);
            boolean increasing = currentOsc > prevOsc;
            if (increasing) {
                series.setBarColor(index, Values.OSC, currentOsc > 0 ? color_pos_Inc : color_neg_Dec);
            } else {
                series.setBarColor(index, Values.OSC, currentOsc > 0 ? color_pos_Dec : color_neg_Inc);
            }
        }

        // Calculate squeeze signal
        Boolean squeezeSignal = buddhaSqueeze(series, index, source, period, stdKC, stdBB);

        if (squeezeSignal != null) {
            // Get previous squeeze count (0 if null or first bar)
            Double prevCount = index > 0 ? series.getDouble(index - 1, Values.SQUEEZE_COUNT) : null;
            int prevCountInt = (prevCount != null) ? prevCount.intValue() : 0;

            // Increment if squeeze is on, reset to 0 if off
            int squeezeCount = squeezeSignal ? (prevCountInt + 1) : 0;

            // Store the count
            series.setDouble(index, Values.SQUEEZE_COUNT, (double) squeezeCount);

            // Show marker at zero line when squeeze is on
            if (squeezeSignal) {
                var markerDesc = getSettings().getMarker(SQUEEZE_MARKER);
                if (markerDesc.isEnabled()) {
                    var coord = new Coordinate(series.getStartTime(index), 0.0);
                    addFigure(new Marker(coord, Enums.Position.CENTER, markerDesc));
                }
            }
        }

        series.setComplete(index);
    }

}
