package chartbuddha;

import com.motivewave.platform.sdk.common.BarSize;
import com.motivewave.platform.sdk.common.DataContext;
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
import com.motivewave.platform.sdk.study.Study;
import com.motivewave.platform.sdk.study.StudyHeader;
import java.awt.Color;
import java.awt.Font;

/** Displays volume as bars */
@StudyHeader(
    namespace = "com.chartbuddha",
    id = "BUDDHA_DAILY_CVD",
    name = "Buddha Daily CVD",
    label = "Daily CVD",
    desc = "Daily Cumulative Volume Delta",
    menu = "Chart Buddha",
    overlay = false,
    requiresVolume = true,
    requiresBarUpdates = true
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

    enum Values {
        VALUE_01, // Cumulative Volume Delta
        VALUE_02, // Zero Line
        VALUE_03, // Store session start time for comparison
    }

    @Override
    /* === INITIALIZE === */
    public void initialize(Defaults defaults) {
        /* === SETTING DESCRIPTOR === */
        var sd = createSD();
        var tab = sd.addTab("General");
        var grp = tab.addGroup("Session Reset");
        grp.addRow(new BarSizeDescriptor(Inputs.BARSIZE, "Reset Period", BarSize.getBarSize(1440))); // Default to daily (1440 minutes)
        grp = tab.addGroup("CVD Line");
        var cvdInfo = new PathInfo(
            CP_02, // Positive Color (green)
            CP_03, // Negative Color (red)
            Enums.ColorPolicy.POSITIVE_NEGATIVE, // Color Policy
            0.0, // Reference value for color policy
            Enums.ShadeType.NONE, // Shade Type
            5.0f, // Line Width
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
            5.0f, // Float - Line Width
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
        // Set Range Keys
        desc.setRangeKeys(Values.VALUE_01, Values.VALUE_02);
        // Declare Paths
        desc.declarePath(Values.VALUE_01, PATH_01);
        desc.declarePath(Values.VALUE_02, PATH_02);
        // Declare Indicators
        desc.declareIndicator(Values.VALUE_01, IND_01);
        desc.declareIndicator(Values.VALUE_02, IND_02);
    }

    @Override
    protected void calculate(int index, DataContext ctx) {
        var series = ctx.getDataSeries();
        var barSize = getSettings().getBarSize(Inputs.BARSIZE);
        var instr = series.getInstrument();
        boolean rth = ctx.isRTH(); // Regular Trading Hours

        // Get current bar's session start using MotiveWave's utility
        long currentTime = series.getStartTime(index);
        long currentEnd = series.getEndTime(index);
        long sessionStart = Util.getStartOfBar(currentTime, currentEnd, instr, barSize, rth);

        // Calculate volume delta for this bar
        double close = series.getClose(index);
        double open = series.getOpen(index);
        double volume = series.getVolume(index);

        // If close > open, it's buying volume (positive), otherwise selling volume (negative)
        double volumeDelta = 0.0;
        if (close > open) {
            volumeDelta = volume;
        } else if (close < open) {
            volumeDelta = -volume;
        }
        // If close == open, volumeDelta remains 0

        // Check if this is a new session by comparing with previous bar's session start
        double cvd = volumeDelta; // Default: start fresh with current bar's delta

        if (index > 0) {
            Double prevSessionStart = series.getDouble(index - 1, Values.VALUE_03);
            if (prevSessionStart != null && prevSessionStart == sessionStart) {
                // Same session, add to previous CVD
                Double prevCVD = series.getDouble(index - 1, Values.VALUE_01);
                if (prevCVD != null) {
                    cvd = prevCVD + volumeDelta;
                }
            }
            // If different session or no previous data, cvd stays as volumeDelta (fresh start)
        }

        // Store the values
        series.setDouble(index, Values.VALUE_01, cvd);
        series.setDouble(index, Values.VALUE_03, (double) sessionStart);
        series.setDouble(index, Values.VALUE_02, 0.0);

        series.setComplete(index);
    }

    @Override
    public void clearState() {
        super.clearState();
    }
}
