package script_buddha;

import java.awt.Color;

import com.motivewave.platform.sdk.common.Coordinate;
import com.motivewave.platform.sdk.common.DataContext;
import com.motivewave.platform.sdk.common.Defaults;
import com.motivewave.platform.sdk.common.Enums;
import com.motivewave.platform.sdk.common.desc.IndicatorDescriptor;
import com.motivewave.platform.sdk.common.desc.InputDescriptor;
import com.motivewave.platform.sdk.common.desc.IntegerDescriptor;
import com.motivewave.platform.sdk.common.desc.MAMethodDescriptor;
import com.motivewave.platform.sdk.common.desc.MarkerDescriptor;
import com.motivewave.platform.sdk.common.desc.PathDescriptor;
import com.motivewave.platform.sdk.common.desc.ShadeDescriptor;
import com.motivewave.platform.sdk.common.desc.ValueDescriptor;
import com.motivewave.platform.sdk.draw.Marker;
import com.motivewave.platform.sdk.study.Study;
import com.motivewave.platform.sdk.study.StudyHeader;

@StudyHeader(
        namespace = "com.chartbuddha",
        id = "SMABANDS",
        name = "SMA Bands",
        label = "SMA Bands",
        desc = "Displays a signal arrow when two moving averages (fast and slow) cross.",
        menu = "Script Buddha",
        overlay = true,
        signals = true)

public class SmaBands extends Study {

    enum Values {
        FAST_A, SLOW_A,
        FAST_B, SLOW_B,
        FAST_C, SLOW_C,
        FAST_D, SLOW_D
    };

    enum Signals {
        CROSS_ABOVE_A, CROSS_BELOW_A,
        CROSS_ABOVE_B, CROSS_BELOW_B,
        CROSS_ABOVE_C, CROSS_BELOW_C,
        CROSS_ABOVE_D, CROSS_BELOW_D
    };

    // SMA Bands color palette
    private static final Color CLR_A_SLOW = new Color(120, 123, 134, 255);
    private static final Color CLR_A_FAST = new Color(120, 123, 134, 255);
    private static final Color CLR_A_TOP_FILL = new Color(120, 123, 134, 120);
    private static final Color CLR_A_BOTTOM_FILL = new Color(120, 123, 134, 120);

    private static final Color CLR_B_SLOW = new Color(000, 000, 255, 255);
    private static final Color CLR_B_FAST = new Color(000, 255, 255, 255);
    private static final Color CLR_B_TOP_FILL = new Color(000, 255, 255, 120);
    private static final Color CLR_B_BOTTOM_FILL = new Color(000, 000, 255, 120);

    private static final Color CLR_C_SLOW = new Color(255, 128, 000, 255);
    private static final Color CLR_C_FAST = new Color(255, 255, 000, 255);
    private static final Color CLR_C_TOP_FILL = new Color(255, 255, 000, 120);
    private static final Color CLR_C_BOTTOM_FILL = new Color(255, 128, 000, 120);

    private static final Color CLR_D_SLOW = new Color(128, 000, 128, 255);
    private static final Color CLR_D_FAST = new Color(255, 055, 255, 255);
    private static final Color CLR_D_TOP_FILL = new Color(255, 055, 255, 120);
    private static final Color CLR_D_BOTTOM_FILL = new Color(128, 000, 128, 120);

    private static final Color CLR_UP = new Color(000, 255, 000, 255);
    private static final Color CLR_DOWN = new Color(255, 000, 000, 255);

    private static final Color CLR_TEXT = new Color(000, 000, 000, 255);

    // Band A
    private static final String INPUT_A1 = "input_a1";
    private static final String INPUT_A2 = "input_a2";

    private static final String METHOD_A1 = "method_a1";
    private static final String METHOD_A2 = "method_a2";

    private static final String PERIOD_A1 = "period_a1";
    private static final String PERIOD_A2 = "period_a2";

    private static final String PATH_A1 = "path_a1";
    private static final String PATH_A2 = "path_a2";

    private static final String FILL_A_TOP = "fill_a_top";
    private static final String FILL_A_BOTTOM = "fill_a_bottom";

    private static final String UP_MARKER_A = "upmarker_a";
    private static final String DOWN_MARKER_A = "downmarker_a";

    private static final String IND_A1 = "ind_a1";
    private static final String IND_A2 = "ind_a2";

    // Band B
    private static final String INPUT_B1 = "input_b1";
    private static final String INPUT_B2 = "input_b2";

    private static final String METHOD_B1 = "method_b1";
    private static final String METHOD_B2 = "method_b2";

    private static final String PERIOD_B1 = "period_b1";
    private static final String PERIOD_B2 = "period_b2";

    private static final String PATH_B1 = "path_b1";
    private static final String PATH_B2 = "path_b2";

    private static final String FILL_B_TOP = "fill_b_top";
    private static final String FILL_B_BOTTOM = "fill_b_bottom";

    private static final String UP_MARKER_B = "upmarker_b";
    private static final String DOWN_MARKER_B = "downmarker_b";

    private static final String IND_B1 = "ind_b1";
    private static final String IND_B2 = "ind_b2";

    // Band C
    private static final String INPUT_C1 = "input_c1";
    private static final String INPUT_C2 = "input_c2";

    private static final String METHOD_C1 = "method_c1";
    private static final String METHOD_C2 = "method_c2";

    private static final String PERIOD_C1 = "period_c1";
    private static final String PERIOD_C2 = "period_c2";

    private static final String PATH_C1 = "path_c1";
    private static final String PATH_C2 = "path_c2";

    private static final String FILL_C_TOP = "fill_c_top";
    private static final String FILL_C_BOTTOM = "fill_c_bottom";

    private static final String UP_MARKER_C = "upmarker_c";
    private static final String DOWN_MARKER_C = "downmarker_c";

    private static final String IND_C1 = "ind_c1";
    private static final String IND_C2 = "ind_c2";

    // Band D
    private static final String INPUT_D1 = "input_d1";
    private static final String INPUT_D2 = "input_d2";

    private static final String METHOD_D1 = "method_d1";
    private static final String METHOD_D2 = "method_d2";

    private static final String PERIOD_D1 = "period_d1";
    private static final String PERIOD_D2 = "period_d2";

    private static final String PATH_D1 = "path_d1";
    private static final String PATH_D2 = "path_d2";

    private static final String FILL_D_TOP = "fill_d_top";
    private static final String FILL_D_BOTTOM = "fill_d_bottom";

    private static final String UP_MARKER_D = "upmarker_d";
    private static final String DOWN_MARKER_D = "downmarker_d";

    private static final String IND_D1 = "ind_d1";
    private static final String IND_D2 = "ind_d2";

    @Override
    public void initialize(Defaults defaults) {
        // User Settings
        var sd = createSD();

        // Tab - A
        var tab = sd.addTab("Band A");
        // Group - Fast Moving Average
        var grp = tab.addGroup("Fast Moving Average");
        grp.addRow(new InputDescriptor(INPUT_A1, "Input", Enums.BarInput.CLOSE));
        grp.addRow(new MAMethodDescriptor(METHOD_A1, "Method", Enums.MAMethod.SMA));
        grp.addRow(new IntegerDescriptor(PERIOD_A1, "Period", 20, 1, 9999, 1));
        // Group - Slow Moving Average
        grp = tab.addGroup("Slow Moving Average");
        grp.addRow(new InputDescriptor(INPUT_A2, "Input", Enums.BarInput.CLOSE));
        grp.addRow(new MAMethodDescriptor(METHOD_A2, "Method", Enums.MAMethod.SMA));
        grp.addRow(new IntegerDescriptor(PERIOD_A2, "Period", 21, 1, 9999, 1));
        // Group - Lines
        grp = tab.addGroup("Lines");
        grp.addRow(new PathDescriptor(PATH_A1, "Fast", CLR_A_FAST, 3.0f, null, true, false, true));
        grp.addRow(new PathDescriptor(PATH_A2, "Slow", CLR_A_SLOW, 1.0f, null, true, false, true));
        // Group - Top and Bottom Fill
        grp = tab.addGroup("Fill");
        grp.addRow(new ShadeDescriptor(FILL_A_TOP, "Top Fill", PATH_A1, PATH_A2, Enums.ShadeType.ABOVE, CLR_A_TOP_FILL, true, true));
        grp.addRow(new ShadeDescriptor(FILL_A_BOTTOM, "Bottom Fill", PATH_A1, PATH_A2, Enums.ShadeType.BELOW, CLR_A_BOTTOM_FILL, true, true));
        // Group - Indicators
        grp = tab.addGroup("Indicators");
        grp.addRow(new IndicatorDescriptor(IND_A1, "Fast", CLR_A_FAST, CLR_TEXT, defaults.getFont(), true, CLR_A_FAST, 1.0f, null, true, true, "20 SMA", true, true));
        grp.addRow(new IndicatorDescriptor(IND_A2, "Slow", CLR_A_SLOW, CLR_TEXT, defaults.getFont(), true, CLR_A_SLOW, 1.0f, null, true, true, "21 SMA", true, true));

        // Tab - B
        tab = sd.addTab("Band B");
        // Group - Fast Moving Average
        grp = tab.addGroup("Fast Moving Average");
        grp.addRow(new InputDescriptor(INPUT_B1, "Input", Enums.BarInput.CLOSE));
        grp.addRow(new MAMethodDescriptor(METHOD_B1, "Method", Enums.MAMethod.SMA));
        grp.addRow(new IntegerDescriptor(PERIOD_B1, "Period", 50, 1, 9999, 1));
        // Group - Slow Moving Average
        grp = tab.addGroup("Slow Moving Average");
        grp.addRow(new InputDescriptor(INPUT_B2, "Input", Enums.BarInput.CLOSE));
        grp.addRow(new MAMethodDescriptor(METHOD_B2, "Method", Enums.MAMethod.SMA));
        grp.addRow(new IntegerDescriptor(PERIOD_B2, "Period", 55, 1, 9999, 1));
        // Group - Lines
        grp = tab.addGroup("Lines");
        grp.addRow(new PathDescriptor(PATH_B1, "Fast", CLR_B_FAST, 3.0f, null, true, false, true));
        grp.addRow(new PathDescriptor(PATH_B2, "Slow", CLR_B_SLOW, 1.0f, null, true, false, true));
        // Group - Fill
        grp = tab.addGroup("Fill");
        grp.addRow(new ShadeDescriptor(FILL_B_TOP, "Top Fill", PATH_B1, PATH_B2, Enums.ShadeType.ABOVE, CLR_B_TOP_FILL, true, true));
        grp.addRow(new ShadeDescriptor(FILL_B_BOTTOM, "Bottom Fill", PATH_B1, PATH_B2, Enums.ShadeType.BELOW, CLR_B_BOTTOM_FILL, true, true));
        // Group - Indicators
        grp = tab.addGroup("Indicators");
        grp.addRow(new IndicatorDescriptor(IND_B1, "Fast", CLR_B_FAST, CLR_TEXT, defaults.getFont(), true, CLR_B_FAST, 1.0f, null, true, true, "50 SMA", true, true));
        grp.addRow(new IndicatorDescriptor(IND_B2, "Slow", CLR_B_SLOW, CLR_TEXT, defaults.getFont(), true, CLR_B_SLOW, 1.0f, null, true, true, "55 SMA", true, true));

        // Tab - C
        tab = sd.addTab("Band C");
        // Group - Fast Moving Average
        grp = tab.addGroup("Fast Moving Average");
        grp.addRow(new InputDescriptor(INPUT_C1, "Input", Enums.BarInput.CLOSE));
        grp.addRow(new MAMethodDescriptor(METHOD_C1, "Method", Enums.MAMethod.SMA));
        grp.addRow(new IntegerDescriptor(PERIOD_C1, "Period", 200, 1, 9999, 1));
        // Group - Slow Moving Average
        grp = tab.addGroup("Slow Moving Average");
        grp.addRow(new InputDescriptor(INPUT_C2, "Input", Enums.BarInput.CLOSE));
        grp.addRow(new MAMethodDescriptor(METHOD_C2, "Method", Enums.MAMethod.SMA));
        grp.addRow(new IntegerDescriptor(PERIOD_C2, "Period", 233, 1, 9999, 1));
        // Group - Lines
        grp = tab.addGroup("Lines");
        grp.addRow(new PathDescriptor(PATH_C1, "Fast", CLR_C_FAST, 3.0f, null, true, false, true));
        grp.addRow(new PathDescriptor(PATH_C2, "Slow", CLR_C_SLOW, 1.0f, null, true, false, true));
        // Group - Fill
        grp = tab.addGroup("Fill");
        grp.addRow(new ShadeDescriptor(FILL_C_TOP, "Top Fill", PATH_C1, PATH_C2, Enums.ShadeType.ABOVE, CLR_C_TOP_FILL, true, true));
        grp.addRow(new ShadeDescriptor(FILL_C_BOTTOM, "Bottom Fill", PATH_C1, PATH_C2, Enums.ShadeType.BELOW, CLR_C_BOTTOM_FILL, true, true));
        // Group - Indicators
        grp = tab.addGroup("Indicators");
        grp.addRow(new IndicatorDescriptor(IND_C1, "Fast", CLR_C_FAST, CLR_TEXT, defaults.getFont(), true, CLR_C_FAST, 1.0f, null, true, true, "200 SMA", true, true));
        grp.addRow(new IndicatorDescriptor(IND_C2, "Slow", CLR_C_SLOW, CLR_TEXT, defaults.getFont(), true, CLR_C_SLOW, 1.0f, null, true, true, "233 SMA", true, true));

        // Tab - D
        tab = sd.addTab("Band D");
        // Group - Fast Moving Average
        grp = tab.addGroup("Fast Moving Average");
        grp.addRow(new InputDescriptor(INPUT_D1, "Input", Enums.BarInput.CLOSE));
        grp.addRow(new MAMethodDescriptor(METHOD_D1, "Method", Enums.MAMethod.SMA));
        grp.addRow(new IntegerDescriptor(PERIOD_D1, "Period", 365, 1, 9999, 1));
        // Group - Slow Moving Average
        grp = tab.addGroup("Slow Moving Average");
        grp.addRow(new InputDescriptor(INPUT_D2, "Input", Enums.BarInput.CLOSE));
        grp.addRow(new MAMethodDescriptor(METHOD_D2, "Method", Enums.MAMethod.SMA));
        grp.addRow(new IntegerDescriptor(PERIOD_D2, "Period", 377, 1, 9999, 1));
        // Group - Lines
        grp = tab.addGroup("Lines");
        grp.addRow(new PathDescriptor(PATH_D1, "Fast", CLR_D_FAST, 3.0f, null, true, false, true));
        grp.addRow(new PathDescriptor(PATH_D2, "Slow", CLR_D_SLOW, 1.0f, null, true, false, true));
        // Group - Fill
        grp = tab.addGroup("Fill");
        grp.addRow(new ShadeDescriptor(FILL_D_TOP, "Top Fill", PATH_D1, PATH_D2, Enums.ShadeType.ABOVE, CLR_D_TOP_FILL, true, true));
        grp.addRow(new ShadeDescriptor(FILL_D_BOTTOM, "Bottom Fill", PATH_D1, PATH_D2, Enums.ShadeType.BELOW, CLR_D_BOTTOM_FILL, true, true));
        // Group - Indicators
        grp = tab.addGroup("Indicators");
        grp.addRow(new IndicatorDescriptor(IND_D1, "Fast", CLR_D_FAST, CLR_TEXT, defaults.getFont(), true, CLR_D_FAST, 1.0f, null, true, true, "365 SMA", true, true));
        grp.addRow(new IndicatorDescriptor(IND_D2, "Slow", CLR_D_SLOW, CLR_TEXT, defaults.getFont(), true, CLR_D_SLOW, 1.0f, null, true, true, "377 SMA", true, true));

        // Tab - Markers
        tab = sd.addTab("Markers");
        // Group - Band A
        grp = tab.addGroup("Band A");
        grp.addRow(new MarkerDescriptor(UP_MARKER_A, "Up Marker A", Enums.MarkerType.TRIANGLE, Enums.Size.SMALL, CLR_UP, defaults.getLineColor(), false, true));
        grp.addRow(new MarkerDescriptor(DOWN_MARKER_A, "Down Marker A", Enums.MarkerType.TRIANGLE, Enums.Size.SMALL, CLR_DOWN, defaults.getLineColor(), false, true));
        // Group - Band B
        grp = tab.addGroup("Band B");
        grp.addRow(new MarkerDescriptor(UP_MARKER_B, "Up Marker B", Enums.MarkerType.TRIANGLE, Enums.Size.SMALL, CLR_UP, defaults.getLineColor(), false, true));
        grp.addRow(new MarkerDescriptor(DOWN_MARKER_B, "Down Marker B", Enums.MarkerType.TRIANGLE, Enums.Size.SMALL, CLR_DOWN, defaults.getLineColor(), false, true));
        // Group - Band C
        grp = tab.addGroup("Band C");
        grp.addRow(new MarkerDescriptor(UP_MARKER_C, "Up Marker C", Enums.MarkerType.TRIANGLE, Enums.Size.SMALL, CLR_UP, defaults.getLineColor(), false, true));
        grp.addRow(new MarkerDescriptor(DOWN_MARKER_C, "Down Marker C", Enums.MarkerType.TRIANGLE, Enums.Size.SMALL, CLR_DOWN, defaults.getLineColor(), false, true));
        // Group - Band D
        grp = tab.addGroup("Band D");
        grp.addRow(new MarkerDescriptor(UP_MARKER_D, "Up Marker D", Enums.MarkerType.TRIANGLE, Enums.Size.SMALL, CLR_UP, defaults.getLineColor(), false, true));
        grp.addRow(new MarkerDescriptor(DOWN_MARKER_D, "Down Marker D", Enums.MarkerType.TRIANGLE, Enums.Size.SMALL, CLR_DOWN, defaults.getLineColor(), false, true));

        // Runtime Settings
        var desc = createRD();
        desc.setLabelSettings(
                INPUT_A1, METHOD_A1, PERIOD_A1,
                INPUT_B1, METHOD_B1, PERIOD_B1,
                INPUT_B2, METHOD_B2, PERIOD_B2,
                INPUT_C1, METHOD_C1, PERIOD_C1,
                INPUT_C2, METHOD_C2, PERIOD_C2,
                INPUT_D1, METHOD_D1, PERIOD_D1,
                INPUT_D2, METHOD_D2, PERIOD_D2);

        desc.exportValue(new ValueDescriptor(Values.FAST_A, "Fast A", new String[]{INPUT_A1, METHOD_A1, PERIOD_A1}));
        desc.exportValue(new ValueDescriptor(Values.SLOW_A, "Slow A", new String[]{INPUT_A2, METHOD_A2, PERIOD_A2}));
        desc.exportValue(new ValueDescriptor(Values.FAST_B, "Fast B", new String[]{INPUT_B1, METHOD_B1, PERIOD_B1}));
        desc.exportValue(new ValueDescriptor(Values.SLOW_B, "Slow B", new String[]{INPUT_B2, METHOD_B2, PERIOD_B2}));
        desc.exportValue(new ValueDescriptor(Values.FAST_C, "Fast C", new String[]{INPUT_C1, METHOD_C1, PERIOD_C1}));
        desc.exportValue(new ValueDescriptor(Values.SLOW_C, "Slow C", new String[]{INPUT_C2, METHOD_C2, PERIOD_C2}));
        desc.exportValue(new ValueDescriptor(Values.FAST_D, "Fast D", new String[]{INPUT_D1, METHOD_D1, PERIOD_D1}));
        desc.exportValue(new ValueDescriptor(Values.SLOW_D, "Slow D", new String[]{INPUT_D2, METHOD_D2, PERIOD_D2}));

        desc.exportValue(new ValueDescriptor(Signals.CROSS_ABOVE_A, Enums.ValueType.BOOLEAN, "Cross Above A"));
        desc.exportValue(new ValueDescriptor(Signals.CROSS_BELOW_A, Enums.ValueType.BOOLEAN, "Cross Below A"));
        desc.exportValue(new ValueDescriptor(Signals.CROSS_ABOVE_B, Enums.ValueType.BOOLEAN, "Cross Above B"));
        desc.exportValue(new ValueDescriptor(Signals.CROSS_BELOW_B, Enums.ValueType.BOOLEAN, "Cross Below B"));
        desc.exportValue(new ValueDescriptor(Signals.CROSS_ABOVE_C, Enums.ValueType.BOOLEAN, "Cross Above C"));
        desc.exportValue(new ValueDescriptor(Signals.CROSS_BELOW_C, Enums.ValueType.BOOLEAN, "Cross Below C"));
        desc.exportValue(new ValueDescriptor(Signals.CROSS_ABOVE_D, Enums.ValueType.BOOLEAN, "Cross Above D"));
        desc.exportValue(new ValueDescriptor(Signals.CROSS_BELOW_D, Enums.ValueType.BOOLEAN, "Cross Below D"));

        desc.declarePath(Values.FAST_A, PATH_A1);
        desc.declarePath(Values.SLOW_A, PATH_A2);
        desc.declarePath(Values.FAST_B, PATH_B1);
        desc.declarePath(Values.SLOW_B, PATH_B2);
        desc.declarePath(Values.FAST_C, PATH_C1);
        desc.declarePath(Values.SLOW_C, PATH_C2);
        desc.declarePath(Values.FAST_D, PATH_D1);
        desc.declarePath(Values.SLOW_D, PATH_D2);

        desc.declareIndicator(Values.FAST_A, IND_A1);
        desc.declareIndicator(Values.SLOW_A, IND_A2);
        desc.declareIndicator(Values.FAST_B, IND_B1);
        desc.declareIndicator(Values.SLOW_B, IND_B2);
        desc.declareIndicator(Values.FAST_C, IND_C1);
        desc.declareIndicator(Values.SLOW_C, IND_C2);
        desc.declareIndicator(Values.FAST_D, IND_D1);
        desc.declareIndicator(Values.SLOW_D, IND_D2);

        // Signals
        desc.declareSignal(Signals.CROSS_ABOVE_A, "Fast A Cross Above");
        desc.declareSignal(Signals.CROSS_BELOW_A, "Fast A Cross Below");
        desc.declareSignal(Signals.CROSS_ABOVE_B, "Fast B Cross Above");
        desc.declareSignal(Signals.CROSS_BELOW_B, "Fast B Cross Below");
        desc.declareSignal(Signals.CROSS_ABOVE_C, "Fast C Cross Above");
        desc.declareSignal(Signals.CROSS_BELOW_C, "Fast C Cross Below");
        desc.declareSignal(Signals.CROSS_ABOVE_D, "Fast D Cross Above");
        desc.declareSignal(Signals.CROSS_BELOW_D, "Fast D Cross Below");

        desc.setRangeKeys(Values.FAST_A, Values.SLOW_A);
        desc.setRangeKeys(Values.FAST_B, Values.SLOW_B);
        desc.setRangeKeys(Values.FAST_C, Values.SLOW_C);
        desc.setRangeKeys(Values.FAST_D, Values.SLOW_D);
    }

    @Override
    protected void calculate(int index, DataContext ctx) {
        int fastPeriod_A = getSettings().getInteger(PERIOD_A1);
        int slowPeriod_A = getSettings().getInteger(PERIOD_A2);
        int fastPeriod_B = getSettings().getInteger(PERIOD_B1);
        int slowPeriod_B = getSettings().getInteger(PERIOD_B2);
        int fastPeriod_C = getSettings().getInteger(PERIOD_C1);
        int slowPeriod_C = getSettings().getInteger(PERIOD_C2);
        int fastPeriod_D = getSettings().getInteger(PERIOD_D1);
        int slowPeriod_D = getSettings().getInteger(PERIOD_D2);

        if (index < Math.max(fastPeriod_A, slowPeriod_A)) {
            return; // not enough data
        }
        if (index < Math.max(fastPeriod_B, slowPeriod_B)) {
            return; // not enough data
        }
        if (index < Math.max(fastPeriod_C, slowPeriod_C)) {
            return; // not enough data
        }
        if (index < Math.max(fastPeriod_D, slowPeriod_D)) {
            return; // not enough data
        }

        var series = ctx.getDataSeries();

        // Calculate and store the fast and slow MAs
        Double fastMA_A = series.ma(getSettings().getMAMethod(METHOD_A1), index, fastPeriod_A, getSettings().getInput(INPUT_A1));
        Double slowMA_A = series.ma(getSettings().getMAMethod(METHOD_A2), index, slowPeriod_A, getSettings().getInput(INPUT_A2));
        Double fastMA_B = series.ma(getSettings().getMAMethod(METHOD_B1), index, fastPeriod_B, getSettings().getInput(INPUT_B1));
        Double slowMA_B = series.ma(getSettings().getMAMethod(METHOD_B2), index, slowPeriod_B, getSettings().getInput(INPUT_B2));
        Double fastMA_C = series.ma(getSettings().getMAMethod(METHOD_C1), index, fastPeriod_C, getSettings().getInput(INPUT_C1));
        Double slowMA_C = series.ma(getSettings().getMAMethod(METHOD_C2), index, slowPeriod_C, getSettings().getInput(INPUT_C2));
        Double fastMA_D = series.ma(getSettings().getMAMethod(METHOD_D1), index, fastPeriod_D, getSettings().getInput(INPUT_D1));
        Double slowMA_D = series.ma(getSettings().getMAMethod(METHOD_D2), index, slowPeriod_D, getSettings().getInput(INPUT_D2));

        if (fastMA_A == null || slowMA_A == null) {
            return;
        }
        if (fastMA_B == null || slowMA_B == null) {
            return;
        }
        if (fastMA_C == null || slowMA_C == null) {
            return;
        }
        if (fastMA_D == null || slowMA_D == null) {
            return;
        }

        series.setDouble(index, Values.FAST_A, fastMA_A);
        series.setDouble(index, Values.SLOW_A, slowMA_A);
        series.setDouble(index, Values.FAST_B, fastMA_B);
        series.setDouble(index, Values.SLOW_B, slowMA_B);
        series.setDouble(index, Values.FAST_C, fastMA_C);
        series.setDouble(index, Values.SLOW_C, slowMA_C);
        series.setDouble(index, Values.FAST_D, fastMA_D);
        series.setDouble(index, Values.SLOW_D, slowMA_D);

        if (!series.isBarComplete(index)) {
            return;
        }

        // Check to see if a cross occurred and raise signal.
        var c_A = new Coordinate(series.getStartTime(index), slowMA_A);
        if (crossedAbove(series, index, Values.FAST_A, Values.SLOW_A)) {
            var marker = getSettings().getMarker(UP_MARKER_A);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_A, Enums.Position.BOTTOM, marker));
            }
            ctx.signal(index, Signals.CROSS_ABOVE_A, "Fast MA Crossed Above!", series.getClose(index));
        } else if (crossedBelow(series, index, Values.FAST_A, Values.SLOW_A)) {
            var marker = getSettings().getMarker(DOWN_MARKER_A);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_A, Enums.Position.TOP, marker));
            }
            ctx.signal(index, Signals.CROSS_BELOW_A, "Fast MA Crossed Below!", series.getClose(index));
        }

        // Check to see if a cross occurred and raise signal.
        var c_B = new Coordinate(series.getStartTime(index), slowMA_B);
        if (crossedAbove(series, index, Values.FAST_B, Values.SLOW_B)) {
            var marker = getSettings().getMarker(UP_MARKER_B);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_B, Enums.Position.BOTTOM, marker));
            }
            ctx.signal(index, Signals.CROSS_ABOVE_B, "Fast MA Crossed Above!", series.getClose(index));
        } else if (crossedBelow(series, index, Values.FAST_B, Values.SLOW_B)) {
            var marker = getSettings().getMarker(DOWN_MARKER_B);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_B, Enums.Position.TOP, marker));
            }
            ctx.signal(index, Signals.CROSS_BELOW_B, "Fast MA Crossed Below!", series.getClose(index));
        }

        // Check to see if a cross occurred and raise signal.
        var c_C = new Coordinate(series.getStartTime(index), slowMA_C);
        if (crossedAbove(series, index, Values.FAST_C, Values.SLOW_C)) {
            var marker = getSettings().getMarker(UP_MARKER_C);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_C, Enums.Position.BOTTOM, marker));
            }
            ctx.signal(index, Signals.CROSS_ABOVE_C, "Fast MA Crossed Above!", series.getClose(index));
        } else if (crossedBelow(series, index, Values.FAST_C, Values.SLOW_C)) {
            var marker = getSettings().getMarker(DOWN_MARKER_C);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_C, Enums.Position.TOP, marker));
            }
            ctx.signal(index, Signals.CROSS_BELOW_C, "Fast MA Crossed Below!", series.getClose(index));
        }

        // Check to see if a cross occurred and raise signal.
        var c_D = new Coordinate(series.getStartTime(index), slowMA_D);
        if (crossedAbove(series, index, Values.FAST_D, Values.SLOW_D)) {
            var marker = getSettings().getMarker(UP_MARKER_D);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_D, Enums.Position.BOTTOM, marker));
            }
            ctx.signal(index, Signals.CROSS_ABOVE_D, "Fast MA Crossed Above!", series.getClose(index));
        } else if (crossedBelow(series, index, Values.FAST_D, Values.SLOW_D)) {
            var marker = getSettings().getMarker(DOWN_MARKER_D);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_D, Enums.Position.TOP, marker));
            }
            ctx.signal(index, Signals.CROSS_BELOW_D, "Fast MA Crossed Below!", series.getClose(index));
        }

        series.setComplete(index);
    }
}
