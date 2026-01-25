package chartbuddha;

import com.motivewave.platform.sdk.common.BarSize;
import com.motivewave.platform.sdk.common.DataContext;
import com.motivewave.platform.sdk.common.DataSeries;
import com.motivewave.platform.sdk.common.Defaults;
import com.motivewave.platform.sdk.common.Enums;
import com.motivewave.platform.sdk.common.IndicatorInfo;
import com.motivewave.platform.sdk.common.Inputs;
import com.motivewave.platform.sdk.common.PathInfo;
import com.motivewave.platform.sdk.common.Util;
import com.motivewave.platform.sdk.common.desc.BarSizeDescriptor;
import com.motivewave.platform.sdk.common.desc.IndicatorDescriptor;
import com.motivewave.platform.sdk.common.desc.PathDescriptor;
import com.motivewave.platform.sdk.common.desc.ShadeDescriptor;
import com.motivewave.platform.sdk.common.desc.ValueDescriptor;
import com.motivewave.platform.sdk.study.Study;
import com.motivewave.platform.sdk.study.StudyHeader;
import java.awt.Color;
import java.awt.Font;

/** Bar-based Daily Cumulative Volume Delta */
@StudyHeader(
    namespace = "com.chartbuddha",
    id = "BUDDHA_DAILY_CVD",
    name = "Buddha Daily CVD",
    label = "Daily CVD",
    desc = "Daily Cumulative Volume Delta with session reset",
    menu = "Chart Buddha",
    overlay = false,
    studyOverlay = true,
    requiresVolume = true,
    barUpdatesByDefault = true
)
public class BuddhaDailyCVD extends Study {

    // === COLOR PALETTE === //
    private static final Color CP_01 = new Color(255, 255, 255, 255); // white
    private static final Color CP_02 = new Color(000, 255, 000, 255); // Light Green
    private static final Color CP_03 = new Color(255, 000, 000, 255); // Light Red
    private static final Color CP_04 = new Color(000, 255, 000, 032); // Light Green
    private static final Color CP_05 = new Color(255, 000, 000, 032); // Light Red

    // === PATH === //
    private static final String PATH_01 = "path_01";
    private static final String PATH_02 = "path_02";
    // === FILL === //
    private static final String FILL_POS = "fill_pos";
    private static final String FILL_NEG = "fill_neg";
    // === IND === //
    private static final String IND_01 = "ind_01";
    private static final String IND_02 = "ind_02";
    // === SETTINGS === //
    private static final String CALC_BARSIZE = "calcBarSize";

    enum Values {
        CVD, // Cumulative Volume Delta
        ZERO_LINE, // Zero Line
        SESSION_START, // Tracks session start time for reset detection
    }

    @Override
    /* === INITIALIZE === */
    public void initialize(Defaults defaults) {
        /* === SETTING DESCRIPTOR === */
        var sd = createSD();
        var tab = sd.addTab("General");
        var grp = tab.addGroup("Session Reset");
        grp.addRow(new BarSizeDescriptor(Inputs.BARSIZE, "Reset Period", BarSize.getBarSize(1440))); // Default to daily (1440 minutes)
        grp = tab.addGroup("Calculation");
        grp.addRow(new BarSizeDescriptor(CALC_BARSIZE, "Calc Timeframe", BarSize.getBarSize(1))); // Default to 1 minute
        grp = tab.addGroup("CVD Line");
        var cvdInfo = new PathInfo(
            CP_02, // Positive Color (green)
            CP_03, // Negative Color (red)
            Enums.ColorPolicy.POSITIVE_NEGATIVE, // Color Policy
            0.0, // Reference value for color policy
            Enums.ShadeType.NONE, // Shade Type
            1.0f, // Line Width
            null, // Point Size (null = use default)
            0, // Bar Shift
            true, // Show Path (Line)
            true, // Continuous
            true, // Show All Bars
            false, // Show Points
            0, // Number of bars to show (0 = all)
            Integer.MAX_VALUE // Max bars
        );
        grp.addRow(new PathDescriptor(PATH_01, "CVD Line", cvdInfo, true));
        grp = tab.addGroup("Fill");
        grp.addRow(
            new ShadeDescriptor(FILL_POS, "Pos Fill", PATH_01, PATH_02, Enums.ShadeType.ABOVE, CP_04, true, true)
        );
        grp.addRow(
            new ShadeDescriptor(FILL_NEG, "Neg Fill", PATH_01, PATH_02, Enums.ShadeType.BELOW, CP_05, true, true)
        );
        grp = tab.addGroup("Indicator");
        var IndicatorInfo01 = new IndicatorInfo(
            "INDICATOR_INFO_01", // String - id
            Color.WHITE, // Color - Label
            Color.BLACK, // Color - Text
            Color.RED, // Color - Label Outline
            true, // Bool - Outline Enabled
            new Font("Monospaced", Font.BOLD, 24), // Font
            true, // Bool - Show On Top
            true, // Bool - Show Label
            Color.WHITE, // Color - Line
            1.0f, // Float - Line Width
            null, // Float - Line Dash
            true, // Bool - Show Line
            true, // Bool - Show Tag
            "CVD", // String - Tag
            Color.BLACK, // Color - Tag Text
            Color.WHITE, // Color - Tag Background
            true // Bool - Enabled
        )
            .setExtLastBar(true);
        grp.addRow(new IndicatorDescriptor(IND_01, "CVD", IndicatorInfo01, true));
        var indicatorInfo02 = new IndicatorInfo(
            "INDICATOR_INFO_02", // String - id
            Color.WHITE, // Color - Label
            Color.BLACK, // Color - Text
            Color.RED, // Color - Label Outline
            true, // Bool - Outline Enabled
            new Font("Monospaced", Font.BOLD, 24), // Font
            false, // Bool - Show On Top
            true, // Bool - Show Label
            Color.WHITE, // Color - Line
            1.0f, // Float - Line Width
            null, // Float - Line Dash
            true, // Bool - Show Line
            false, // Bool - Show Tag
            "Zero Line", // String - Tag
            Color.BLACK, // Color - Tag Text
            Color.WHITE, // Color - Tag Background
            true // Bool - Enabled
        )
            .setExtLastBar(true);
        grp.addRow(new IndicatorDescriptor(IND_02, "Zero Line", indicatorInfo02, true));
        tab = sd.addTab("Zero Line");
        grp = tab.addGroup("Zero Line");
        grp.addRow(new PathDescriptor(PATH_02, "Zero Line", CP_01, 1.0f, null, true, false, true));
        /* === RUNTIME DESCRIPTOR === */
        var desc = createRD();
        // Set Insets
        desc.setTopInsetPixels(20);
        desc.setBottomInsetPixels(20);
        // Declare Paths
        desc.declarePath(Values.CVD, PATH_01);
        desc.declarePath(Values.ZERO_LINE, PATH_02);
        // Declare Indicators
        desc.declareIndicator(Values.CVD, IND_01);
        desc.declareIndicator(Values.ZERO_LINE, IND_02);
        // Export Values
        desc.exportValue(new ValueDescriptor(Values.CVD, "CVD", new String[] { Inputs.BARSIZE }));
        desc.setRangeKeys(Values.CVD);
    }

    @Override
    public void clearState() {
        super.clearState();
        barSize = getSettings().getBarSize(Inputs.BARSIZE);
        calcBarSize = getSettings().getBarSize(CALC_BARSIZE);
    }

    @Override
    protected void calculate(int index, DataContext ctx) {
        var series = ctx.getDataSeries();
        var instr = series.getInstrument();
        boolean rth = ctx.isRTH();

        // Get current bar times
        long startTime = series.getStartTime(index);
        long endTime = series.getEndTime(index);

        // Calculate the session start for this bar
        long currentSessionStart = Util.getStartOfBar(startTime, endTime, instr, barSize, rth);

        // Get the lower timeframe data series for accurate CVD calculation
        DataSeries calcSeries = ctx.getDataSeries(calcBarSize);
        if (calcSeries == null || calcSeries.size() == 0) {
            // Fallback to current series if lower timeframe not available
            calcSeries = series;
        }

        // Calculate CVD by summing all calc timeframe bars within the current session up to current bar's end time
        double cumulativeDelta = 0.0;

        // Find all calc timeframe bars that fall within the current session and up to current bar's end time
        for (int i = 0; i < calcSeries.size(); i++) {
            long calcBarStart = calcSeries.getStartTime(i);
            long calcBarEnd = calcSeries.getEndTime(i);

            // Get the session for this calc bar
            long calcSessionStart = Util.getStartOfBar(calcBarStart, calcBarEnd, instr, barSize, rth);

            // Only include bars in the current session and before/equal to current bar's end time
            if (calcSessionStart == currentSessionStart && calcBarEnd <= endTime) {
                double close = calcSeries.getClose(i);
                double open = calcSeries.getOpen(i);
                float volume = calcSeries.getVolumeAsFloat(i);

                // Volume delta: positive if close >= open (buying pressure), negative otherwise
                double volumeDelta = (close >= open) ? volume : -volume;
                cumulativeDelta += volumeDelta;
            }

            // Stop when we've passed the current bar's end time
            if (calcBarStart > endTime) {
                break;
            }
        }

        // Store values
        series.setDouble(index, Values.CVD, cumulativeDelta);
        series.setDouble(index, Values.ZERO_LINE, 0.0);
        series.setDouble(index, Values.SESSION_START, (double) currentSessionStart);

        series.setComplete(index);
    }

    // Cached settings
    private BarSize barSize;
    private BarSize calcBarSize;
}
