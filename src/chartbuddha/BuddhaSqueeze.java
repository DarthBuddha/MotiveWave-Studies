package chartbuddha;

import com.motivewave.platform.sdk.common.Coordinate;
import com.motivewave.platform.sdk.common.DataContext;
import com.motivewave.platform.sdk.common.DataSeries;
import com.motivewave.platform.sdk.common.Defaults;
import com.motivewave.platform.sdk.common.Enums;
import com.motivewave.platform.sdk.common.IndicatorInfo;
import com.motivewave.platform.sdk.common.MarkerInfo;
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
import java.awt.Font;

@StudyHeader(
    namespace = "com.chartbuddha",
    id = "BuddhaSqueeze",
    name = "Buddha Squeeze",
    label = "Squeeze",
    desc = "The Buddha Squeeze highlights volatility compression and signals when the market is poised to break out with directional momentum.",
    menu = "Chart Buddha",
    signals = false,
    overlay = false,
    studyOverlay = true
)
public class BuddhaSqueeze extends Study {

    // === Color Palette - ChartBuddha === //
    /** Color - Transparent */
    private static final Color CLR_TRANSP = new Color(000, 000, 000, 000); // Transparent
    /** Color - Black */
    private static final Color CLR_BLACK = new Color(000, 000, 000, 255); // Black
    /** Color - White */
    private static final Color CLR_WHITE = new Color(255, 255, 255, 255); // White
    /** Color - Red */
    private static final Color CLR_RED = new Color(255, 000, 000, 255); // Red
    // === Color Palette - Study === //
    /** Color - Histogram Positive Increasing */
    private static final Color CP_01 = new Color(000, 128, 000, 255); // Dark Green
    /** Color - Histogram Positive Decreasing */
    private static final Color CP_02 = new Color(255, 000, 000, 255); // Light Red
    /** Color - Histogram Negative Increasing */
    private static final Color CP_03 = new Color(128, 000, 000, 255); // Dark Red
    /** Color - Histogram Negative Decreasing */
    private static final Color CP_04 = new Color(000, 255, 000, 255); // Light Green
    /** Color - Squeeze On */
    private static final Color CLR_SQUEEZE = new Color(255, 215, 000, 192); // Gold
    // === Setting Keys === //
    /** Color Key - Positive Trend Increasing */
    private static final String COLOR_POS_INC = "color_pos_inc";
    /** Color Key - Positive Trend Decreasing */
    private static final String COLOR_POS_DEC = "color_pos_dec";
    /** Color Key - Negative Trend Increasing */
    private static final String COLOR_NEG_INC = "color_neg_inc";
    /** Color Key - Negative Trend Decreasing */
    private static final String COLOR_NEG_DEC = "color_neg_dec";
    /** Color Key - Squeeze On */
    private static final String COLOR_SQUEEZE_ON = "color_squeeze_on";
    /** Color Key - No Squeeze */
    private static final String COLOR_NO_SQUEEZE = "color_no_squeeze";
    /** Input - Source */
    private static final String INPUT = "input";
    /** Period - Histogram */
    private static final String PERIOD_01 = "period_01";
    /** Period - ATR */
    private static final String PERIOD_02 = "period_02";
    /** Standard Deviation - Bollinger Bands */
    private static final String STD_BB = "std_bb";
    /** Standard Deviation - Keltner Channels */
    private static final String STD_KC = "std_kc";
    /** Path - Histogram */
    private static final String PATH_01 = "path_01";
    /** Squeeze - Threshold */
    private static final String SQUEEZE_THRESHOLD = "squeeze_threshold";
    /** Squeeze - Marker */
    private static final String SQUEEZE_MARKER = "squeeze_marker";
    /** Indicator - Zero Line */
    private static final String IND_00 = "ind_00";
    /** Indicator - Oscillator */
    private static final String IND_01 = "ind_01";

    // Cached settings for performance
    private int period;
    private Object source;
    private double bbStd;
    private double kcStd;
    private int atrPeriod;
    private double squeezeThreshold;
    private Color colorPosInc;
    private Color colorPosDec;
    private Color colorNegInc;
    private Color colorNegDec;
    private Color colorSqueeze;

    enum Values {
        /** Value - Zero Line */
        VALUE_00,
        /** Value - Oscillator */
        VALUE_01,
        /** Value - Linear Regression */
        VALUE_02,
        /** Value - Squeeze Count */
        VALUE_03,
        /** Value - ATR for internal use */
        VALUE_04,
    }

    @Override
    /* === INITIALIZE === */
    public void initialize(Defaults defaults) {
        /* === SETTING DESCRIPTOR === */
        var sd = createSD();
        var indicatorFont = new Font("Monospaced", Font.BOLD, 16);

        // === Inputs TAB === //
        var tab = sd.addTab("Inputs");
        var grp = tab.addGroup("Histogram");
        grp.addRow(new InputDescriptor(INPUT, "Source", Enums.BarInput.CLOSE));
        grp.addRow(new IntegerDescriptor(PERIOD_01, "Period", 21, 1, 999, 1));
        grp = tab.addGroup("Standard Deviations");
        grp.addRow(new DoubleDescriptor(STD_BB, "BB StdDev", 2.1, 0.1, 999, 0.1));
        grp.addRow(new DoubleDescriptor(STD_KC, "KC StdDev", 1.4, 0.1, 999, 0.1));
        grp = tab.addGroup("Squeeze Sensitivity");
        grp.addRow(new IntegerDescriptor(PERIOD_02, "ATR EMA Period", 21, 1, 999, 1));
        grp.addRow(new DoubleDescriptor(SQUEEZE_THRESHOLD, "Squeeze Threshold", 1.03, 0.1, 2.0, 0.01));

        // === Histogram TAB === //
        tab = sd.addTab("Histogram");
        // Group - Colors
        grp = tab.addGroup("Colors");
        grp.addRow(new ColorDescriptor(COLOR_POS_INC, "Positive Trend Increasing", CP_01));
        grp.addRow(new ColorDescriptor(COLOR_POS_DEC, "Positive Trend Decreasing", CP_02));
        grp.addRow(new ColorDescriptor(COLOR_NEG_INC, "Negative Trend Increasing", CP_03));
        grp.addRow(new ColorDescriptor(COLOR_NEG_DEC, "Negative Trend Decreasing", CP_04));
        // Group - Indicators
        // Indicator - Oscillator
        grp = tab.addGroup("Indicators");
        var IndicatorInfo01 = new IndicatorInfo(
            "INDICATOR_INFO_01", // String - id
            CLR_WHITE, // Color - Label
            CLR_BLACK, // Color - Text
            CLR_RED, // Color - Label Outline
            true, // Bool - Outline Enabled
            indicatorFont, // Font
            true, // Bool - Show On Top
            true, // Bool - Show Label
            CLR_WHITE, // Color - Line
            1.0f, // Float - Line Width
            null, // Float - Line Dash
            true, // Bool - Show Line
            true, // Bool - Show Tag
            "Osc", // String - Tag
            CLR_BLACK, // Color - Tag Text
            CLR_WHITE, // Color - Tag Background
            true // Bool - Enabled
        )
            .setExtLastBar(true);
        grp.addRow(new IndicatorDescriptor(IND_01, "Oscillator", IndicatorInfo01, true));
        // Indicator - Zero Line
        var IndicatorInfo00 = new IndicatorInfo(
            "INDICATOR_INFO_00", // String - id
            CLR_WHITE, // Color - Label
            CLR_BLACK, // Color - Text
            CLR_RED, // Color - Label Outline
            true, // Bool - Outline Enabled
            indicatorFont, // Font
            false, // Bool - Show On Top
            true, // Bool - Show Label
            CLR_WHITE, // Color - Line
            0.25f, // Float - Line Width
            null, // Float - Line Dash
            true, // Bool - Show Line
            false, // Bool - Show Tag
            "", // String - Tag
            CLR_BLACK, // Color - Tag Text
            CLR_WHITE, // Color - Tag Background
            true // Bool - Enabled
        )
            .setExtLastBar(false);
        grp.addRow(new IndicatorDescriptor(IND_00, "Zero Line", IndicatorInfo00, true));

        // === Squeeze TAB === //
        tab = sd.addTab("Squeeze");
        // Group - Squeeze Dots
        grp = tab.addGroup("Squeeze Dots");
        var markerInfo01 = new MarkerInfo(
            Enums.MarkerType.LINE_ARROW, // Enums - Marker Type
            Enums.Size.MEDIUM, // Enums - Size
            CLR_SQUEEZE, // Color - Fill
            CLR_BLACK, // Color - Text
            CLR_SQUEEZE, // Color - Outline
            true // Bool - Enabled
        );
        grp.addRow(new MarkerDescriptor(SQUEEZE_MARKER, "Squeeze Marker", markerInfo01, true));

        // Group - Squeeze Line
        grp = tab.addGroup("Squeeze Line");
        grp.addRow(new ColorDescriptor(COLOR_SQUEEZE_ON, "Squeeze On", CLR_SQUEEZE));
        grp.addRow(new ColorDescriptor(COLOR_NO_SQUEEZE, "No Squeeze", CLR_TRANSP));

        grp.addRow(
            new PathDescriptor(
                PATH_01, // String - Path Key
                "Squeeze Path", // String - Label
                CLR_TRANSP, // Color - Path Color
                1.5f, // Float - Line Width
                null, // Float - Dash (null for solid)
                true, // Bool - Enabled
                false, // Bool - Supports Max Points
                true // Bool - Supports Disabled
            )
        );

        //  var pathInfo01 = new PathInfo(
        //      CLR_TRANSP, // Color - Path Color
        //      5.0f, // Float - Line Width
        //      null, // Float - Dash (null for solid)
        //      true, // Bool - Enabled
        //      false, // Bool - Continuous
        //      true, // Bool - Show All Bars
        //      0, // Int - Bar Center (0 = all)
        //      Integer.MAX_VALUE // Int - Max bars
        //  );
        //  grp.addRow(
        //      new PathDescriptor(
        //          PATH_01, // String - Path Key
        //          "Squeeze Line", // String - Label
        //          pathInfo01, // PathInfo - Path Info
        //          true, // Bool - Enabled
        //          false, // Bool - Show Tag
        //          true // Bool - Supports Disabled
        //      )
        //  );

        // === QUICK SETTINGS === //
        sd.addQuickSettings(PATH_01, SQUEEZE_MARKER);

        /* === RUNTIME DESCRIPTOR === */
        var desc = createRD();
        // Set Insets
        desc.setTopInsetPixels(20);
        desc.setBottomInsetPixels(20);
        // Set Range Keys
        desc.setRangeKeys(Values.VALUE_01);
        // Declare Bars
        desc.declareBars(Values.VALUE_01, null);
        // Declare Paths
        desc.declarePath(0, PATH_01);
        // Declare Indicators
        desc.declareIndicator(Values.VALUE_00, IND_00);
        desc.declareIndicator(Values.VALUE_01, IND_01);
        // Export Values
        desc.exportValue(
            new ValueDescriptor(Values.VALUE_01, "osc", new String[] { INPUT, PERIOD_01, STD_BB, STD_KC })
        );
        // Set Label Settings
        desc.setLabelSettings(INPUT, PERIOD_01, STD_BB, STD_KC);
    }

    @Override
    public void onLoad(Defaults defaults) {
        int p1 = getSettings().getInteger(PERIOD_01, 21);
        setMinBars(p1);
    }

    @Override
    public void clearState() {
        super.clearState();
        // Cache settings for performance
        var settings = getSettings();
        period = settings.getInteger(PERIOD_01, 21);
        source = settings.getInput(INPUT, Enums.BarInput.CLOSE);
        bbStd = settings.getDouble(STD_BB, 2.1);
        kcStd = settings.getDouble(STD_KC, 1.4);
        atrPeriod = settings.getInteger(PERIOD_02, 50);
        squeezeThreshold = settings.getDouble(SQUEEZE_THRESHOLD, 1.08);
        colorPosInc = settings.getColor(COLOR_POS_INC);
        colorPosDec = settings.getColor(COLOR_POS_DEC);
        colorNegInc = settings.getColor(COLOR_NEG_INC);
        colorNegDec = settings.getColor(COLOR_NEG_DEC);
        colorSqueeze = settings.getColor(COLOR_SQUEEZE_ON);
    }

    @Override
    protected void calculate(int index, DataContext ctx) {
        var series = ctx.getDataSeries();

        // Regime-based volatility adjustment
        double stdBB;
        double stdKC;

        // Calculate ATR once and cache it for both regime detection and squeeze calculation
        Double currentAtr = atr(series, index, period);
        if (currentAtr != null) {
            // Store ATR value in series for EMA calculation
            series.setDouble(index, Values.VALUE_04, currentAtr);

            // Calculate EMA of ATR using stored values
            Double atrEma = series.ma(Enums.MAMethod.EMA, index, atrPeriod, Values.VALUE_04);

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
            stdBB = this.bbStd;
            stdKC = this.kcStd;
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
            series.setDouble(index, Values.VALUE_02, inputToLinReg);
        }

        // Only calculate oscillator once we have enough data for linreg
        if (index < (period * 2) - 1) {
            return;
        }

        // Calculate linear regression on the stored LINREG values
        Double osc = linreg(series, index, Values.VALUE_02, period, 0);
        if (osc == null) {
            return;
        }

        // Store oscillator value (scaled by 100 like Pine Script)
        series.setDouble(index, Values.VALUE_01, osc * 100);

        // Color Logic - use cached colors
        Double prevOsc = series.getDouble(index - 1, Values.VALUE_01);
        Double currentOsc = series.getDouble(index, Values.VALUE_01);
        if (prevOsc != null && currentOsc != null) {
            boolean increasing = currentOsc > prevOsc;
            if (increasing) {
                series.setBarColor(index, Values.VALUE_01, currentOsc > 0 ? colorPosInc : colorNegDec);
            } else {
                series.setBarColor(index, Values.VALUE_01, currentOsc > 0 ? colorPosDec : colorNegInc);
            }
        } else if (currentOsc != null) {
            // First bar or when prevOsc is null - default to positive/negative color based on sign
            series.setBarColor(index, Values.VALUE_01, currentOsc > 0 ? colorPosInc : colorNegInc);
        }

        // Calculate squeeze signal - pass cached ATR to avoid recalculation
        Boolean squeezeSignal = buddhaSqueeze(series, index, currentAtr, stdKC, stdBB);

        if (squeezeSignal != null) {
            // Get previous squeeze count (0 if null or first bar)
            Double prevCount = index > 0 ? series.getDouble(index - 1, Values.VALUE_03) : null;
            int prevCountInt = (prevCount != null) ? prevCount.intValue() : 0;

            // Increment if squeeze is on, reset to 0 if off
            int squeezeCount = squeezeSignal ? (prevCountInt + 1) : 0;

            // Store the count
            series.setDouble(index, Values.VALUE_03, (double) squeezeCount);

            // Show marker on opposite side of histogram when squeeze is on
            if (squeezeSignal && currentOsc != null) {
                var markerDesc = getSettings().getMarker(SQUEEZE_MARKER);
                if (markerDesc.isEnabled()) {
                    // Place marker on opposite side of histogram
                    // If histogram is positive, place marker above it (pointing down)
                    // If histogram is negative, place marker below it (pointing up)
                    double markerY = currentOsc > 0 ? currentOsc : currentOsc;
                    Enums.Position markerPos = currentOsc > 0 ? Enums.Position.TOP : Enums.Position.BOTTOM;

                    var coord = new Coordinate(series.getStartTime(index), markerY);
                    addFigure(new Marker(coord, markerPos, markerDesc));
                }
            }

            // Set path color based on squeeze state (use path index 0, not PATH string)
            Color guideColor = getSettings().getColor(COLOR_NO_SQUEEZE);
            series.setPathColor(index, 0, squeezeSignal ? colorSqueeze : guideColor);
        }

        // Draw the zero line path
        var path = getSettings().getPath(PATH_01);
        if (path != null && path.isEnabled()) {
            series.setDouble(index, 0, 0.0); // Set path value to 0 for the zero line
        }

        // Store the values
        series.setDouble(index, Values.VALUE_00, 0.0);

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
            Double y = series.getDouble(barIndex, source);
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
    private Boolean buddhaSqueeze(DataSeries series, int index, Double atrValue, double stdKC, double stdBB) {
        Double stdevValue = stdev(series, index, source, period);

        if (atrValue == null || stdevValue == null) {
            return null;
        }

        double kcWidth = stdKC * atrValue;
        double bbWidth = stdBB * stdevValue;

        // Squeeze occurs when BB width is less than or very close to KC width
        if (kcWidth == 0) {
            return false;
        }

        boolean squeeze = (bbWidth / kcWidth) < squeezeThreshold;
        return squeeze;
    }
}
