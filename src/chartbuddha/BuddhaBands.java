package chartbuddha;

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
import java.awt.Color;

@StudyHeader(
    namespace = "com.chartbuddha",
    id = "BUDDHA_BANDS",
    name = "Buddha Bands",
    label = "Buddha Bands",
    desc = "Displays a signal arrow when two moving averages (fast and slow) cross.",
    menu = "Chart Buddha",
    overlay = true,
    signals = true
)
public class BuddhaBands extends Study {

    enum Values {
        FAST_A,
        SLOW_A,
        FAST_B,
        SLOW_B,
        FAST_C,
        SLOW_C,
        FAST_D,
        SLOW_D,
    }

    enum Signals {
        CROSS_ABOVE_A,
        CROSS_BELOW_A,
        CROSS_ABOVE_B,
        CROSS_BELOW_B,
        CROSS_ABOVE_C,
        CROSS_BELOW_C,
        CROSS_ABOVE_D,
        CROSS_BELOW_D,
    }

    // SMA Bands color palette
    private static final Color C_01 = new Color(120, 123, 134, 255); // SMA 20
    private static final Color C_02 = new Color(120, 123, 134, 255); // SMA 21
    private static final Color C_03 = new Color(000, 255, 255, 255); // SMA 50
    private static final Color C_04 = new Color(000, 000, 255, 255); // SMA 55
    private static final Color C_05 = new Color(255, 255, 000, 255); // SMA 200
    private static final Color C_06 = new Color(255, 128, 000, 255); // SMA 233
    private static final Color C_07 = new Color(255, 055, 255, 255); // SMA 365
    private static final Color C_08 = new Color(128, 000, 128, 255); // SMA 377
    // SMA Bands Fill Colors
    private static final Color C_T_FILL_01 = new Color(120, 123, 134, 120);
    private static final Color C_B_FILL_01 = new Color(120, 123, 134, 120);
    private static final Color C_T_FILL_02 = new Color(000, 255, 255, 120);
    private static final Color C_B_FILL_02 = new Color(000, 000, 255, 120);
    private static final Color C_T_FILL_03 = new Color(255, 255, 000, 120);
    private static final Color C_B_FILL_03 = new Color(255, 128, 000, 120);
    private static final Color C_T_FILL_04 = new Color(255, 055, 255, 120);
    private static final Color C_B_FILL_04 = new Color(128, 000, 128, 120);
    // Markers Colors
    private static final Color C_UP = new Color(000, 255, 000, 255);
    private static final Color C_DOWN = new Color(255, 000, 000, 255);
    // Text Color
    private static final Color C_TEXT = new Color(000, 000, 000, 255);

    // === INPUT === //
    private static final String INPUT_01 = "input_01"; // SMA 20
    private static final String INPUT_02 = "input_02"; // SMA 21
    private static final String INPUT_03 = "input_03"; // SMA 50
    private static final String INPUT_04 = "input_04"; // SMA 55
    private static final String INPUT_05 = "input_05"; // SMA 200
    private static final String INPUT_06 = "input_06"; // SMA 233
    private static final String INPUT_07 = "input_07"; // SMA 365
    private static final String INPUT_08 = "input_08"; // SMA 377
    // === METHOD === //
    private static final String METHOD_01 = "method_01"; // SMA 20
    private static final String METHOD_02 = "method_02"; // SMA 21
    private static final String METHOD_03 = "method_03"; // SMA 50
    private static final String METHOD_04 = "method_04"; // SMA 55
    private static final String METHOD_05 = "method_05"; // SMA 200
    private static final String METHOD_06 = "method_06"; // SMA 233
    private static final String METHOD_07 = "method_07"; // SMA 365
    private static final String METHOD_08 = "method_08"; // SMA 377
    // === PERIOD === //
    private static final String PERIOD_01 = "period_01"; // SMA 20
    private static final String PERIOD_02 = "period_02"; // SMA 21
    private static final String PERIOD_03 = "period_03"; // SMA 50
    private static final String PERIOD_04 = "period_04"; // SMA 55
    private static final String PERIOD_05 = "period_05"; // SMA 200
    private static final String PERIOD_06 = "period_06"; // SMA 233
    private static final String PERIOD_07 = "period_07"; // SMA 365
    private static final String PERIOD_08 = "period_08"; // SMA 377
    // === PATH === //
    private static final String PATH_01 = "path_01"; // SMA 20
    private static final String PATH_02 = "path_02"; // SMA 21
    private static final String PATH_03 = "path_03"; // SMA 50
    private static final String PATH_04 = "path_04"; // SMA 55
    private static final String PATH_05 = "path_05"; // SMA 200
    private static final String PATH_06 = "path_06"; // SMA 233
    private static final String PATH_07 = "path_07"; // SMA 365
    private static final String PATH_08 = "path_08"; // SMA 377
    // === IND === //
    private static final String IND_01 = "ind_01"; // SMA 20
    private static final String IND_02 = "ind_02"; // SMA 21
    private static final String IND_03 = "ind_03"; // SMA 50
    private static final String IND_04 = "ind_04"; // SMA 55
    private static final String IND_05 = "ind_05"; // SMA 200
    private static final String IND_06 = "ind_06"; // SMA 233
    private static final String IND_07 = "ind_07"; // SMA 365
    private static final String IND_08 = "ind_08"; // SMA 377
    // === Band Fill Color === //
    private static final String T_FILL_01 = "t_fill_01";
    private static final String B_FILL_01 = "b_fill_01";
    private static final String T_FILL_02 = "t_fill_02";
    private static final String B_FILL_02 = "b_fill_02";
    private static final String T_FILL_03 = "t_fill_03";
    private static final String B_FILL_03 = "b_fill_03";
    private static final String T_FILL_04 = "t_fill_04";
    private static final String B_FILL_04 = "b_fill_04";
    // === Markers === //
    private static final String UP_MARKER_01 = "up_marker_01";
    private static final String DN_MARKER_01 = "dn_marker_01";
    private static final String UP_MARKER_02 = "up_marker_02";
    private static final String DN_MARKER_02 = "dn_marker_02";
    private static final String UP_MARKER_03 = "up_marker_03";
    private static final String DN_MARKER_03 = "dn_marker_03";
    private static final String UP_MARKER_04 = "up_marker_04";
    private static final String DN_MARKER_04 = "dn_marker_04";

    @Override
    /* === INITIALIZE === */
    public void initialize(Defaults defaults) {
        // === SETTING DESCRIPTOR ===
        var sd = createSD();

        // Tab - A
        var tab = sd.addTab("Band A");
        // Group - Fast Moving Average
        var grp = tab.addGroup("Fast Moving Average");
        grp.addRow(new InputDescriptor(INPUT_01, "Input", Enums.BarInput.CLOSE));
        grp.addRow(new MAMethodDescriptor(METHOD_01, "Method", Enums.MAMethod.SMA));
        grp.addRow(new IntegerDescriptor(PERIOD_01, "Period", 20, 1, 9999, 1));
        // Group - Slow Moving Average
        grp = tab.addGroup("Slow Moving Average");
        grp.addRow(new InputDescriptor(INPUT_02, "Input", Enums.BarInput.CLOSE));
        grp.addRow(new MAMethodDescriptor(METHOD_02, "Method", Enums.MAMethod.SMA));
        grp.addRow(new IntegerDescriptor(PERIOD_02, "Period", 21, 1, 9999, 1));
        // Group - Lines
        grp = tab.addGroup("Lines");
        grp.addRow(new PathDescriptor(PATH_01, "Fast", C_01, 3.0f, null, true, false, true));
        grp.addRow(new PathDescriptor(PATH_02, "Slow", C_02, 1.0f, null, true, false, true));
        // Group - Top and Bottom Fill
        grp = tab.addGroup("Fill");
        grp.addRow(
            new ShadeDescriptor(T_FILL_01, "Top Fill", PATH_01, PATH_02, Enums.ShadeType.ABOVE, C_T_FILL_01, true, true)
        );
        grp.addRow(
            new ShadeDescriptor(
                B_FILL_01,
                "Bottom Fill",
                PATH_01,
                PATH_02,
                Enums.ShadeType.BELOW,
                C_B_FILL_01,
                true,
                true
            )
        );
        // Group - Indicators
        grp = tab.addGroup("Indicators");
        grp.addRow(
            new IndicatorDescriptor(
                IND_01,
                "Fast",
                C_01,
                C_TEXT,
                defaults.getFont(),
                true,
                C_01,
                1.0f,
                null,
                true,
                true,
                "20 SMA",
                true,
                true
            )
        );
        grp.addRow(
            new IndicatorDescriptor(
                IND_02,
                "Slow",
                C_02,
                C_TEXT,
                defaults.getFont(),
                true,
                C_02,
                1.0f,
                null,
                true,
                true,
                "21 SMA",
                true,
                true
            )
        );

        // Tab - B
        tab = sd.addTab("Band B");
        // Group - Fast Moving Average
        grp = tab.addGroup("Fast Moving Average");
        grp.addRow(new InputDescriptor(INPUT_03, "Input", Enums.BarInput.CLOSE));
        grp.addRow(new MAMethodDescriptor(METHOD_03, "Method", Enums.MAMethod.SMA));
        grp.addRow(new IntegerDescriptor(PERIOD_03, "Period", 50, 1, 9999, 1));
        // Group - Slow Moving Average
        grp = tab.addGroup("Slow Moving Average");
        grp.addRow(new InputDescriptor(INPUT_04, "Input", Enums.BarInput.CLOSE));
        grp.addRow(new MAMethodDescriptor(METHOD_04, "Method", Enums.MAMethod.SMA));
        grp.addRow(new IntegerDescriptor(PERIOD_04, "Period", 55, 1, 9999, 1));
        // Group - Lines
        grp = tab.addGroup("Lines");
        grp.addRow(new PathDescriptor(PATH_03, "Fast", C_03, 3.0f, null, true, false, true));
        grp.addRow(new PathDescriptor(PATH_04, "Slow", C_04, 1.0f, null, true, false, true));
        // Group - Fill
        grp = tab.addGroup("Fill");
        grp.addRow(
            new ShadeDescriptor(T_FILL_02, "Top Fill", PATH_03, PATH_04, Enums.ShadeType.ABOVE, C_T_FILL_02, true, true)
        );
        grp.addRow(
            new ShadeDescriptor(
                B_FILL_02,
                "Bottom Fill",
                PATH_03,
                PATH_04,
                Enums.ShadeType.BELOW,
                C_B_FILL_02,
                true,
                true
            )
        );
        // Group - Indicators
        grp = tab.addGroup("Indicators");
        grp.addRow(
            new IndicatorDescriptor(
                IND_03,
                "Fast",
                C_03,
                C_TEXT,
                defaults.getFont(),
                true,
                C_03,
                1.0f,
                null,
                true,
                true,
                "50 SMA",
                true,
                true
            )
        );
        grp.addRow(
            new IndicatorDescriptor(
                IND_04,
                "Slow",
                C_04,
                C_TEXT,
                defaults.getFont(),
                true,
                C_04,
                1.0f,
                null,
                true,
                true,
                "55 SMA",
                true,
                true
            )
        );

        // Tab - C
        tab = sd.addTab("Band C");
        // Group - Fast Moving Average
        grp = tab.addGroup("Fast Moving Average");
        grp.addRow(new InputDescriptor(INPUT_05, "Input", Enums.BarInput.CLOSE));
        grp.addRow(new MAMethodDescriptor(METHOD_05, "Method", Enums.MAMethod.SMA));
        grp.addRow(new IntegerDescriptor(PERIOD_05, "Period", 200, 1, 9999, 1));
        // Group - Slow Moving Average
        grp = tab.addGroup("Slow Moving Average");
        grp.addRow(new InputDescriptor(INPUT_06, "Input", Enums.BarInput.CLOSE));
        grp.addRow(new MAMethodDescriptor(METHOD_06, "Method", Enums.MAMethod.SMA));
        grp.addRow(new IntegerDescriptor(PERIOD_06, "Period", 233, 1, 9999, 1));
        // Group - Lines
        grp = tab.addGroup("Lines");
        grp.addRow(new PathDescriptor(PATH_05, "Fast", C_05, 3.0f, null, true, false, true));
        grp.addRow(new PathDescriptor(PATH_06, "Slow", C_06, 1.0f, null, true, false, true));
        // Group - Fill
        grp = tab.addGroup("Fill");
        grp.addRow(
            new ShadeDescriptor(T_FILL_03, "Top Fill", PATH_05, PATH_06, Enums.ShadeType.ABOVE, C_T_FILL_03, true, true)
        );
        grp.addRow(
            new ShadeDescriptor(
                B_FILL_03,
                "Bottom Fill",
                PATH_05,
                PATH_06,
                Enums.ShadeType.BELOW,
                C_B_FILL_03,
                true,
                true
            )
        );
        // Group - Indicators
        grp = tab.addGroup("Indicators");
        grp.addRow(
            new IndicatorDescriptor(
                IND_05,
                "Fast",
                C_05,
                C_TEXT,
                defaults.getFont(),
                true,
                C_05,
                1.0f,
                null,
                true,
                true,
                "200 SMA",
                true,
                true
            )
        );
        grp.addRow(
            new IndicatorDescriptor(
                IND_06,
                "Slow",
                C_06,
                C_TEXT,
                defaults.getFont(),
                true,
                C_06,
                1.0f,
                null,
                true,
                true,
                "233 SMA",
                true,
                true
            )
        );

        // Tab - D
        tab = sd.addTab("Band D");
        // Group - Fast Moving Average
        grp = tab.addGroup("Fast Moving Average");
        grp.addRow(new InputDescriptor(INPUT_07, "Input", Enums.BarInput.CLOSE));
        grp.addRow(new MAMethodDescriptor(METHOD_07, "Method", Enums.MAMethod.SMA));
        grp.addRow(new IntegerDescriptor(PERIOD_07, "Period", 365, 1, 9999, 1));
        // Group - Slow Moving Average
        grp = tab.addGroup("Slow Moving Average");
        grp.addRow(new InputDescriptor(INPUT_08, "Input", Enums.BarInput.CLOSE));
        grp.addRow(new MAMethodDescriptor(METHOD_08, "Method", Enums.MAMethod.SMA));
        grp.addRow(new IntegerDescriptor(PERIOD_08, "Period", 377, 1, 9999, 1));
        // Group - Lines
        grp = tab.addGroup("Lines");
        grp.addRow(new PathDescriptor(PATH_07, "Fast", C_07, 3.0f, null, true, false, true));
        grp.addRow(new PathDescriptor(PATH_08, "Slow", C_08, 1.0f, null, true, false, true));
        // Group - Fill
        grp = tab.addGroup("Fill");
        grp.addRow(
            new ShadeDescriptor(T_FILL_04, "Top Fill", PATH_07, PATH_08, Enums.ShadeType.ABOVE, C_T_FILL_04, true, true)
        );
        grp.addRow(
            new ShadeDescriptor(
                B_FILL_04,
                "Bottom Fill",
                PATH_07,
                PATH_08,
                Enums.ShadeType.BELOW,
                C_B_FILL_04,
                true,
                true
            )
        );
        // Group - Indicators
        grp = tab.addGroup("Indicators");
        grp.addRow(
            new IndicatorDescriptor(
                IND_07,
                "Fast",
                C_07,
                C_TEXT,
                defaults.getFont(),
                true,
                C_07,
                1.0f,
                null,
                true,
                true,
                "365 SMA",
                true,
                true
            )
        );
        grp.addRow(
            new IndicatorDescriptor(
                IND_08,
                "Slow",
                C_08,
                C_TEXT,
                defaults.getFont(),
                true,
                C_08,
                1.0f,
                null,
                true,
                true,
                "377 SMA",
                true,
                true
            )
        );

        // Tab - Markers
        tab = sd.addTab("Markers");
        // Group - Band A
        grp = tab.addGroup("Band A");
        grp.addRow(
            new MarkerDescriptor(
                UP_MARKER_01,
                "Up Marker 01",
                Enums.MarkerType.TRIANGLE,
                Enums.Size.SMALL,
                C_UP,
                defaults.getLineColor(),
                false,
                true
            )
        );
        grp.addRow(
            new MarkerDescriptor(
                DN_MARKER_01,
                "Down Marker 01",
                Enums.MarkerType.TRIANGLE,
                Enums.Size.SMALL,
                C_DOWN,
                defaults.getLineColor(),
                false,
                true
            )
        );
        // Group - Band B
        grp = tab.addGroup("Band B");
        grp.addRow(
            new MarkerDescriptor(
                UP_MARKER_02,
                "Up Marker 02",
                Enums.MarkerType.TRIANGLE,
                Enums.Size.SMALL,
                C_UP,
                defaults.getLineColor(),
                false,
                true
            )
        );
        grp.addRow(
            new MarkerDescriptor(
                DN_MARKER_02,
                "Down Marker 02",
                Enums.MarkerType.TRIANGLE,
                Enums.Size.SMALL,
                C_DOWN,
                defaults.getLineColor(),
                false,
                true
            )
        );
        // Group - Band C
        grp = tab.addGroup("Band C");
        grp.addRow(
            new MarkerDescriptor(
                UP_MARKER_03,
                "Up Marker 03",
                Enums.MarkerType.TRIANGLE,
                Enums.Size.SMALL,
                C_UP,
                defaults.getLineColor(),
                false,
                true
            )
        );
        grp.addRow(
            new MarkerDescriptor(
                DN_MARKER_03,
                "Down Marker 03",
                Enums.MarkerType.TRIANGLE,
                Enums.Size.SMALL,
                C_DOWN,
                defaults.getLineColor(),
                false,
                true
            )
        );
        // Group - Band D
        grp = tab.addGroup("Band D");
        grp.addRow(
            new MarkerDescriptor(
                UP_MARKER_04,
                "Up Marker 04",
                Enums.MarkerType.TRIANGLE,
                Enums.Size.SMALL,
                C_UP,
                defaults.getLineColor(),
                false,
                true
            )
        );
        grp.addRow(
            new MarkerDescriptor(
                DN_MARKER_04,
                "Down Marker 04",
                Enums.MarkerType.TRIANGLE,
                Enums.Size.SMALL,
                C_DOWN,
                defaults.getLineColor(),
                false,
                true
            )
        );

        // === RUNTIME DESCRIPTOR ===
        var desc = createRD();
        desc.setLabelSettings(
            INPUT_01,
            METHOD_01,
            PERIOD_01,
            INPUT_03,
            METHOD_03,
            PERIOD_03,
            INPUT_04,
            METHOD_04,
            PERIOD_04,
            INPUT_05,
            METHOD_05,
            PERIOD_05,
            INPUT_06,
            METHOD_06,
            PERIOD_06,
            INPUT_07,
            METHOD_07,
            PERIOD_07,
            INPUT_08,
            METHOD_08,
            PERIOD_08
        );

        desc.exportValue(new ValueDescriptor(Values.FAST_A, "Fast A", new String[] { INPUT_01, METHOD_01, PERIOD_01 }));
        desc.exportValue(new ValueDescriptor(Values.SLOW_A, "Slow A", new String[] { INPUT_02, METHOD_02, PERIOD_02 }));
        desc.exportValue(new ValueDescriptor(Values.FAST_B, "Fast B", new String[] { INPUT_03, METHOD_03, PERIOD_03 }));
        desc.exportValue(new ValueDescriptor(Values.SLOW_B, "Slow B", new String[] { INPUT_04, METHOD_04, PERIOD_04 }));
        desc.exportValue(new ValueDescriptor(Values.FAST_C, "Fast C", new String[] { INPUT_05, METHOD_05, PERIOD_05 }));
        desc.exportValue(new ValueDescriptor(Values.SLOW_C, "Slow C", new String[] { INPUT_06, METHOD_06, PERIOD_06 }));
        desc.exportValue(new ValueDescriptor(Values.FAST_D, "Fast D", new String[] { INPUT_07, METHOD_07, PERIOD_07 }));
        desc.exportValue(new ValueDescriptor(Values.SLOW_D, "Slow D", new String[] { INPUT_08, METHOD_08, PERIOD_08 }));

        desc.exportValue(new ValueDescriptor(Signals.CROSS_ABOVE_A, Enums.ValueType.BOOLEAN, "Cross Above A"));
        desc.exportValue(new ValueDescriptor(Signals.CROSS_BELOW_A, Enums.ValueType.BOOLEAN, "Cross Below A"));
        desc.exportValue(new ValueDescriptor(Signals.CROSS_ABOVE_B, Enums.ValueType.BOOLEAN, "Cross Above B"));
        desc.exportValue(new ValueDescriptor(Signals.CROSS_BELOW_B, Enums.ValueType.BOOLEAN, "Cross Below B"));
        desc.exportValue(new ValueDescriptor(Signals.CROSS_ABOVE_C, Enums.ValueType.BOOLEAN, "Cross Above C"));
        desc.exportValue(new ValueDescriptor(Signals.CROSS_BELOW_C, Enums.ValueType.BOOLEAN, "Cross Below C"));
        desc.exportValue(new ValueDescriptor(Signals.CROSS_ABOVE_D, Enums.ValueType.BOOLEAN, "Cross Above D"));
        desc.exportValue(new ValueDescriptor(Signals.CROSS_BELOW_D, Enums.ValueType.BOOLEAN, "Cross Below D"));

        desc.declarePath(Values.FAST_A, PATH_01);
        desc.declarePath(Values.SLOW_A, PATH_02);
        desc.declarePath(Values.FAST_B, PATH_03);
        desc.declarePath(Values.SLOW_B, PATH_04);
        desc.declarePath(Values.FAST_C, PATH_05);
        desc.declarePath(Values.SLOW_C, PATH_06);
        desc.declarePath(Values.FAST_D, PATH_07);
        desc.declarePath(Values.SLOW_D, PATH_08);

        desc.declareIndicator(Values.FAST_A, IND_01);
        desc.declareIndicator(Values.SLOW_A, IND_02);
        desc.declareIndicator(Values.FAST_B, IND_03);
        desc.declareIndicator(Values.SLOW_B, IND_04);
        desc.declareIndicator(Values.FAST_C, IND_05);
        desc.declareIndicator(Values.SLOW_C, IND_06);
        desc.declareIndicator(Values.FAST_D, IND_07);
        desc.declareIndicator(Values.SLOW_D, IND_08);
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
        int fastPeriod_A = getSettings().getInteger(PERIOD_01);
        int slowPeriod_A = getSettings().getInteger(PERIOD_02);
        int fastPeriod_B = getSettings().getInteger(PERIOD_03);
        int slowPeriod_B = getSettings().getInteger(PERIOD_04);
        int fastPeriod_C = getSettings().getInteger(PERIOD_05);
        int slowPeriod_C = getSettings().getInteger(PERIOD_06);
        int fastPeriod_D = getSettings().getInteger(PERIOD_07);
        int slowPeriod_D = getSettings().getInteger(PERIOD_08);

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
        Double fastMA_A = series.ma(
            getSettings().getMAMethod(METHOD_01),
            index,
            fastPeriod_A,
            getSettings().getInput(INPUT_01)
        );
        Double slowMA_A = series.ma(
            getSettings().getMAMethod(METHOD_02),
            index,
            slowPeriod_A,
            getSettings().getInput(INPUT_02)
        );
        Double fastMA_B = series.ma(
            getSettings().getMAMethod(METHOD_03),
            index,
            fastPeriod_B,
            getSettings().getInput(INPUT_03)
        );
        Double slowMA_B = series.ma(
            getSettings().getMAMethod(METHOD_04),
            index,
            slowPeriod_B,
            getSettings().getInput(INPUT_04)
        );
        Double fastMA_C = series.ma(
            getSettings().getMAMethod(METHOD_05),
            index,
            fastPeriod_C,
            getSettings().getInput(INPUT_05)
        );
        Double slowMA_C = series.ma(
            getSettings().getMAMethod(METHOD_06),
            index,
            slowPeriod_C,
            getSettings().getInput(INPUT_06)
        );
        Double fastMA_D = series.ma(
            getSettings().getMAMethod(METHOD_07),
            index,
            fastPeriod_D,
            getSettings().getInput(INPUT_07)
        );
        Double slowMA_D = series.ma(
            getSettings().getMAMethod(METHOD_08),
            index,
            slowPeriod_D,
            getSettings().getInput(INPUT_08)
        );

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
            var marker = getSettings().getMarker(UP_MARKER_01);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_A, Enums.Position.BOTTOM, marker));
            }
            ctx.signal(index, Signals.CROSS_ABOVE_A, "Fast MA Crossed Above!", series.getClose(index));
        } else if (crossedBelow(series, index, Values.FAST_A, Values.SLOW_A)) {
            var marker = getSettings().getMarker(DN_MARKER_01);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_A, Enums.Position.TOP, marker));
            }
            ctx.signal(index, Signals.CROSS_BELOW_A, "Fast MA Crossed Below!", series.getClose(index));
        }

        // Check to see if a cross occurred and raise signal.
        var c_B = new Coordinate(series.getStartTime(index), slowMA_B);
        if (crossedAbove(series, index, Values.FAST_B, Values.SLOW_B)) {
            var marker = getSettings().getMarker(UP_MARKER_02);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_B, Enums.Position.BOTTOM, marker));
            }
            ctx.signal(index, Signals.CROSS_ABOVE_B, "Fast MA Crossed Above!", series.getClose(index));
        } else if (crossedBelow(series, index, Values.FAST_B, Values.SLOW_B)) {
            var marker = getSettings().getMarker(DN_MARKER_02);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_B, Enums.Position.TOP, marker));
            }
            ctx.signal(index, Signals.CROSS_BELOW_B, "Fast MA Crossed Below!", series.getClose(index));
        }

        // Check to see if a cross occurred and raise signal.
        var c_C = new Coordinate(series.getStartTime(index), slowMA_C);
        if (crossedAbove(series, index, Values.FAST_C, Values.SLOW_C)) {
            var marker = getSettings().getMarker(UP_MARKER_03);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_C, Enums.Position.BOTTOM, marker));
            }
            ctx.signal(index, Signals.CROSS_ABOVE_C, "Fast MA Crossed Above!", series.getClose(index));
        } else if (crossedBelow(series, index, Values.FAST_C, Values.SLOW_C)) {
            var marker = getSettings().getMarker(DN_MARKER_03);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_C, Enums.Position.TOP, marker));
            }
            ctx.signal(index, Signals.CROSS_BELOW_C, "Fast MA Crossed Below!", series.getClose(index));
        }

        // Check to see if a cross occurred and raise signal.
        var c_D = new Coordinate(series.getStartTime(index), slowMA_D);
        if (crossedAbove(series, index, Values.FAST_D, Values.SLOW_D)) {
            var marker = getSettings().getMarker(UP_MARKER_04);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_D, Enums.Position.BOTTOM, marker));
            }
            ctx.signal(index, Signals.CROSS_ABOVE_D, "Fast MA Crossed Above!", series.getClose(index));
        } else if (crossedBelow(series, index, Values.FAST_D, Values.SLOW_D)) {
            var marker = getSettings().getMarker(DN_MARKER_04);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_D, Enums.Position.TOP, marker));
            }
            ctx.signal(index, Signals.CROSS_BELOW_D, "Fast MA Crossed Below!", series.getClose(index));
        }

        series.setComplete(index);
    }
}
