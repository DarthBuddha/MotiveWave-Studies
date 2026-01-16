package chartbuddha;

import com.motivewave.platform.sdk.common.Coordinate;
import com.motivewave.platform.sdk.common.DataContext;
import com.motivewave.platform.sdk.common.DataSeries;
import com.motivewave.platform.sdk.common.Defaults;
import com.motivewave.platform.sdk.common.Enums;
import com.motivewave.platform.sdk.common.desc.ColorDescriptor;
import com.motivewave.platform.sdk.common.desc.DoubleDescriptor;
import com.motivewave.platform.sdk.common.desc.IndicatorDescriptor;
import com.motivewave.platform.sdk.common.desc.InputDescriptor;
import com.motivewave.platform.sdk.common.desc.IntegerDescriptor;
import com.motivewave.platform.sdk.common.desc.MarkerDescriptor;
import com.motivewave.platform.sdk.common.desc.PathDescriptor;
import com.motivewave.platform.sdk.common.desc.ValueDescriptor;
import com.motivewave.platform.sdk.draw.Marker;
import com.motivewave.platform.sdk.study.Study;
import com.motivewave.platform.sdk.study.StudyHeader;
import java.awt.Color;

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
        OSC,
        LINREG,
        SQUEEZE_COUNT,
        SQUEEZE_MARKER,
        ATR_EMA,
        ATR_VALUE,
    }

    // Color palette
    private static final Color C_POS_INC = new Color(000, 128, 000, 255); // Dark Green
    private static final Color C_POS_DEC = new Color(255, 000, 000, 255); // Light Red
    private static final Color C_NEG_INC = new Color(128, 000, 000, 255); // Dark Red
    private static final Color C_NEG_DEC = new Color(000, 255, 000, 255); // Light Green
    private static final Color C_SQUEEZE_ON = new Color(255, 215, 000, 192); // Gold
    private static final Color C_NO_SQUEEZE = new Color(000, 000, 000, 000); // Transparent
    private static final Color C_TEXT = new Color(000, 000, 000, 255);
    // Color keys
    private static final String K_COLOR_POS_INC = "color_pos_inc";
    private static final String K_COLOR_POS_DEC = "color_pos_dec";
    private static final String K_COLOR_NEG_INC = "color_neg_inc";
    private static final String K_COLOR_NEG_DEC = "color_neg_dec";
    private static final String K_COLOR_SQUEEZE_ON = "color_squeeze_on";
    private static final String K_COLOR_NO_SQUEEZE = "color_no_squeeze";
    // Settings keys
    private static final String K_INPUT = "input";
    private static final String K_PATH = "path";
    private static final String K_PERIOD = "period";
    private static final String K_BB_STD = "bb_std";
    private static final String K_KC_STD = "kc_std";
    private static final String K_IND = "k_ind";

    private static final String SQUEEZE_MARKER = "squeeze_marker";

    @Override
    /* === INITIALIZE === */
    public void initialize(Defaults defaults) {
        // === SETTING DESCRIPTOR ===
        var sd = createSD();
        var tab = sd.addTab("General");

        var grp = tab.addGroup("Inputs");
        grp.addRow(new InputDescriptor(K_INPUT, "Source", Enums.BarInput.CLOSE));
        grp.addRow(new IntegerDescriptor(K_PERIOD, "Period", 21, 1, 999, 1));
        grp.addRow(new DoubleDescriptor(K_BB_STD, "BB StdDev", 2.1, 0.1, 999, 0.1));
        grp.addRow(new DoubleDescriptor(K_KC_STD, "KC StdDev", 1.4, 0.1, 999, 0.1));

        tab = sd.addTab("Histogram");
        grp = tab.addGroup("Colors");
        grp.addRow(new ColorDescriptor(K_COLOR_POS_INC, "Positive Trend Increasing", C_POS_INC));
        grp.addRow(new ColorDescriptor(K_COLOR_POS_DEC, "Positive Trend Decreasing", C_POS_DEC));
        grp.addRow(new ColorDescriptor(K_COLOR_NEG_INC, "Negative Trend Increasing", C_NEG_INC));
        grp.addRow(new ColorDescriptor(K_COLOR_NEG_DEC, "Negative Trend Decreasing", C_NEG_DEC));
        grp = tab.addGroup("Indicators");
        grp.addRow(
            new IndicatorDescriptor(
                K_IND,
                "Osc",
                defaults.getBarColor(),
                C_TEXT,
                defaults.getFont(),
                true,
                defaults.getBarColor(),
                2.0f,
                null,
                true,
                true,
                "Osc",
                true,
                true
            )
        );

        tab = sd.addTab("Squeeze");
        grp = tab.addGroup("Squeeze Dots");
        grp.addRow(
            new MarkerDescriptor(
                SQUEEZE_MARKER,
                "Squeeze Dot",
                Enums.MarkerType.CIRCLE,
                Enums.Size.MEDIUM,
                C_SQUEEZE_ON,
                null,
                false,
                true
            )
        );

        grp = tab.addGroup("Squeeze Line");
        grp.addRow(new ColorDescriptor(K_COLOR_SQUEEZE_ON, "Squeeze On", C_SQUEEZE_ON));
        grp.addRow(new ColorDescriptor(K_COLOR_NO_SQUEEZE, "No Squeeze", C_NO_SQUEEZE));
        grp.addRow(new PathDescriptor(K_PATH, "Squeeze Path", C_NO_SQUEEZE, 5.0f, null, true, false, true));

        // === QUICK SETTINGS === //
        sd.addQuickSettings(K_INPUT, K_PERIOD, K_BB_STD, K_KC_STD);
        // === RUNTIME DESCRIPTOR ===
        var desc = createRD();
        desc.declareBars(Values.OSC, null);
        desc.declareIndicator(Values.OSC, K_IND);
        desc.declarePath(0, K_PATH);
        desc.exportValue(
            new ValueDescriptor(Values.OSC, "osc", new String[] { K_INPUT, K_PERIOD, K_BB_STD, K_KC_STD })
        );
        desc.setLabelSettings(K_INPUT, K_PERIOD, K_BB_STD, K_KC_STD);
        desc.setRangeKeys(Values.OSC);
        desc.setTopInsetPixels(20);
        desc.setBottomInsetPixels(20);
    }

    @Override
    public void onLoad(Defaults defaults) {
        int p1 = getSettings().getInteger(K_PERIOD);
        setMinBars(p1);
    }

    @Override
    protected void calculate(int index, DataContext ctx) {
        int period = getSettings().getInteger(K_PERIOD);
        Object source = getSettings().getInput(K_INPUT, Enums.BarInput.CLOSE);
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
            stdBB = getSettings().getDouble(K_BB_STD);
            stdKC = getSettings().getDouble(K_KC_STD);
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
        Color color_pos_Inc = getSettings().getColor(K_COLOR_POS_INC);
        Color color_pos_Dec = getSettings().getColor(K_COLOR_POS_DEC);
        Color color_neg_Inc = getSettings().getColor(K_COLOR_NEG_INC);
        Color color_neg_Dec = getSettings().getColor(K_COLOR_NEG_DEC);
        Color color_squeeze = getSettings().getColor(K_COLOR_SQUEEZE_ON);
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

            // Set path color based on squeeze state (use path index 0, not PATH string)
            Color guideColor = getSettings().getColor(K_COLOR_NO_SQUEEZE);
            series.setPathColor(index, 0, squeezeSignal ? color_squeeze : guideColor);
        }

        // Draw the zero line path
        var path = getSettings().getPath(K_PATH);
        if (path != null && path.isEnabled()) {
            series.setDouble(index, 0, 0.0); // Set path value to 0 for the zero line
        }

        series.setComplete(index);
    }

    // ========================================
    // CUSTOM FUNCTIONS
    // ========================================

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

        double sumX = 0,
            sumY = 0,
            sumXY = 0,
            sumX2 = 0;
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
}
