package chartbuddha;

import com.motivewave.platform.sdk.common.Coordinate;
import com.motivewave.platform.sdk.common.DataContext;
import com.motivewave.platform.sdk.common.Defaults;
import com.motivewave.platform.sdk.common.Enums;
import com.motivewave.platform.sdk.common.IndicatorInfo;
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
import java.awt.Font;

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

    // SMA Bands color palette
    /** Color - Text */
    private static final Color CP_00 = new Color(000, 000, 000, 255); // Black
    /** Color - 20 SMA */
    private static final Color CP_01 = new Color(120, 123, 134, 255); // Gray
    /** Color - 21 SMA */
    private static final Color CP_02 = new Color(120, 123, 134, 255); // Gray
    /** Color - 50 SMA */
    private static final Color CP_03 = new Color(000, 255, 255, 255); // Cyan
    /** Color - 55 SMA */
    private static final Color CP_04 = new Color(000, 000, 255, 255); // Blue
    /** Color - 200 SMA */
    private static final Color CP_05 = new Color(255, 255, 000, 255); // Yellow
    /** Color - 233 SMA */
    private static final Color CP_06 = new Color(255, 128, 000, 255); // Orange
    /** Color - 365 SMA */
    private static final Color CP_07 = new Color(255, 055, 255, 255); // Magenta
    /** Color - 377 SMA */
    private static final Color CP_08 = new Color(128, 000, 128, 255); // Purple
    /** Color - 20 SMA Fill */
    private static final Color CP_09 = new Color(120, 123, 134, 120); // Gray Transparent
    /** Color - 21 SMA Fill */
    private static final Color CP_10 = new Color(120, 123, 134, 120); // Gray Transparent
    /** Color - 50 SMA Fill */
    private static final Color CP_11 = new Color(000, 255, 255, 120); // Cyan Transparent
    /** Color - 55 SMA Fill */
    private static final Color CP_12 = new Color(000, 000, 255, 120); // Blue Transparent
    /** Color - 200 SMA Fill */
    private static final Color CP_13 = new Color(255, 255, 000, 120); // Yellow Transparent
    /** Color - 233 SMA Fill */
    private static final Color CP_14 = new Color(255, 128, 000, 120); // Orange Transparent
    /** Color - 365 SMA Fill */
    private static final Color CP_15 = new Color(255, 055, 255, 120); // Magenta Transparent
    /** Color - 377 SMA Fill */
    private static final Color CP_16 = new Color(128, 000, 128, 120); // Purple Transparent
    /** Color - Tag Outline */
    private static final Color CP_17 = new Color(255, 000, 000, 255); // Red
    /** Color - Up Arrow */
    private static final Color C_UP = new Color(000, 255, 000, 255); // Green
    /** Color - Down Arrow */
    private static final Color C_DOWN = new Color(255, 000, 000, 255); // Red
    /** Input - 20 SMA */
    private static final String INPUT_01 = "input_01";
    /** Input - 21 SMA */
    private static final String INPUT_02 = "input_02";
    /** Input - 50 SMA */
    private static final String INPUT_03 = "input_03";
    /** Input - 55 SMA */
    private static final String INPUT_04 = "input_04";
    /** Input - 200 SMA */
    private static final String INPUT_05 = "input_05";
    /** Input - 233 SMA */
    private static final String INPUT_06 = "input_06";
    /** Input - 365 SMA */
    private static final String INPUT_07 = "input_07";
    /** Input - 377 SMA */
    private static final String INPUT_08 = "input_08";
    /** Method - 20 SMA */
    private static final String METHOD_01 = "method_01";
    /** Method - 21 SMA */
    private static final String METHOD_02 = "method_02";
    /** Method - 50 SMA */
    private static final String METHOD_03 = "method_03";
    /** Method - 55 SMA */
    private static final String METHOD_04 = "method_04";
    /** Method - 200 SMA */
    private static final String METHOD_05 = "method_05";
    /** Method - 233 SMA */
    private static final String METHOD_06 = "method_06";
    /** Method - 365 SMA */
    private static final String METHOD_07 = "method_07";
    /** Method - 377 SMA */
    private static final String METHOD_08 = "method_08";
    /** Period - 20 SMA */
    private static final String PERIOD_01 = "period_01";
    /** Period - 21 SMA */
    private static final String PERIOD_02 = "period_02";
    /** Period - 50 SMA */
    private static final String PERIOD_03 = "period_03";
    /** Period - 55 SMA */
    private static final String PERIOD_04 = "period_04";
    /** Period - 200 SMA */
    private static final String PERIOD_05 = "period_05";
    /** Period - 233 SMA */
    private static final String PERIOD_06 = "period_06";
    /** Period - 365 SMA */
    private static final String PERIOD_07 = "period_07";
    /** Period - 377 SMA */
    private static final String PERIOD_08 = "period_08";
    /** Path - 20 SMA */
    private static final String PATH_01 = "path_01";
    /** Path - 21 SMA */
    private static final String PATH_02 = "path_02";
    /** Path - 50 SMA */
    private static final String PATH_03 = "path_03";
    /** Path - 55 SMA */
    private static final String PATH_04 = "path_04";
    /** Path - 200 SMA */
    private static final String PATH_05 = "path_05";
    /** Path - 233 SMA */
    private static final String PATH_06 = "path_06";
    /** Path - 365 SMA */
    private static final String PATH_07 = "path_07";
    /** Path - 377 SMA */
    private static final String PATH_08 = "path_08";
    /** Indicator - 20 SMA */
    private static final String IND_01 = "ind_01";
    /** Indicator - 21 SMA */
    private static final String IND_02 = "ind_02";
    /** Indicator - 50 SMA */
    private static final String IND_03 = "ind_03";
    /** Indicator - 55 SMA */
    private static final String IND_04 = "ind_04";
    /** Indicator - 200 SMA */
    private static final String IND_05 = "ind_05";
    /** Indicator - 233 SMA */
    private static final String IND_06 = "ind_06";
    /** Indicator - 365 SMA */
    private static final String IND_07 = "ind_07";
    /** Indicator - 377 SMA */
    private static final String IND_08 = "ind_08";
    /** Fill - 20 SMA */
    private static final String FILL_01 = "fill_01";
    /** Fill - 21 SMA */
    private static final String FILL_02 = "fill_02";
    /** Fill - 50 SMA */
    private static final String FILL_03 = "fill_03";
    /** Fill - 55 SMA */
    private static final String FILL_04 = "fill_04";
    /** Fill - 200 SMA */
    private static final String FILL_05 = "fill_05";
    /** Fill - 233 SMA */
    private static final String FILL_06 = "fill_06";
    /** Fill - 365 SMA */
    private static final String FILL_07 = "fill_07";
    /** Fill - 377 SMA */
    private static final String FILL_08 = "fill_08";
    // === Markers === //
    private static final String UP_MARKER_01 = "up_marker_01";
    private static final String DN_MARKER_01 = "dn_marker_01";
    private static final String UP_MARKER_02 = "up_marker_02";
    private static final String DN_MARKER_02 = "dn_marker_02";
    private static final String UP_MARKER_03 = "up_marker_03";
    private static final String DN_MARKER_03 = "dn_marker_03";
    private static final String UP_MARKER_04 = "up_marker_04";
    private static final String DN_MARKER_04 = "dn_marker_04";

    enum Values {
        /** Value - 20 SMA */
        VALUE_01,
        /** Value - 21 SMA */
        VALUE_02,
        /** Value - 50 SMA */
        VALUE_03,
        /** Value - 55 SMA */
        VALUE_04,
        /** Value - 200 SMA */
        VALUE_05,
        /** Value - 233 SMA */
        VALUE_06,
        /** Value - 365 SMA */
        VALUE_07,
        /** Value - 377 SMA */
        VALUE_08,
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

    @Override
    /* === INITIALIZE === */
    public void initialize(Defaults defaults) {
        /* === SETTING DESCRIPTOR === */
        var sd = createSD();
        var indicatorFont = new Font("Monospaced", Font.BOLD, 16);

        // Tab - A
        var tab = sd.addTab("20-21");
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
        grp.addRow(new PathDescriptor(PATH_01, "Fast", CP_01, 3.0f, null, true, false, true));
        grp.addRow(new PathDescriptor(PATH_02, "Slow", CP_02, 1.0f, null, true, false, true));
        // Group - Top and Bottom Fill
        grp = tab.addGroup("Fill");
        grp.addRow(
            new ShadeDescriptor(FILL_01, "Top Fill", PATH_01, PATH_02, Enums.ShadeType.ABOVE, CP_09, true, true)
        );
        grp.addRow(
            new ShadeDescriptor(FILL_02, "Bottom Fill", PATH_01, PATH_02, Enums.ShadeType.BELOW, CP_10, true, true)
        );
        // Group - Indicators
        grp = tab.addGroup("Indicators");
        var IndicatorInfo01 = new IndicatorInfo(
            "INDICATOR_INFO_01", // String - id
            CP_01, // Color - Label
            CP_00, // Color - Text
            CP_17, // Color - Label Outline
            false, // Bool - Outline Enabled
            indicatorFont, // Font
            false, // Bool - Show On Top
            true, // Bool - Show Label
            CP_01, // Color - Line
            1.0f, // Float - Line Width
            null, // Float - Line Dash
            true, // Bool - Show Line
            false, // Bool - Show Tag
            "20 SMA", // String - Tag
            CP_00, // Color - Tag Text
            CP_01, // Color - Tag Background
            true // Bool - Enabled
        )
            .setExtLastBar(true);
        grp.addRow(new IndicatorDescriptor(IND_01, "Fast", IndicatorInfo01, true));
        var IndicatorInfo02 = new IndicatorInfo(
            "INDICATOR_INFO_02", // String - id
            CP_02, // Color - Label
            CP_00, // Color - Text
            CP_17, // Color - Label Outline
            false, // Bool - Outline Enabled
            indicatorFont, // Font
            false, // Bool - Show On Top
            true, // Bool - Show Label
            CP_02, // Color - Line
            1.0f, // Float - Line Width
            null, // Float - Line Dash
            true, // Bool - Show Line
            false, // Bool - Show Tag
            "21 SMA", // String - Tag
            CP_00, // Color - Tag Text
            CP_02, // Color - Tag Background
            true // Bool - Enabled
        )
            .setExtLastBar(true);
        grp.addRow(new IndicatorDescriptor(IND_02, "Slow", IndicatorInfo02, true));
        // Tab - B
        tab = sd.addTab("50-55");
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
        grp.addRow(new PathDescriptor(PATH_03, "Fast", CP_03, 3.0f, null, true, false, true));
        grp.addRow(new PathDescriptor(PATH_04, "Slow", CP_04, 1.0f, null, true, false, true));
        // Group - Fill
        grp = tab.addGroup("Fill");
        grp.addRow(
            new ShadeDescriptor(FILL_03, "Top Fill", PATH_03, PATH_04, Enums.ShadeType.ABOVE, CP_11, true, true)
        );
        grp.addRow(
            new ShadeDescriptor(FILL_04, "Bottom Fill", PATH_03, PATH_04, Enums.ShadeType.BELOW, CP_12, true, true)
        );
        // Group - Indicators
        grp = tab.addGroup("Indicators");
        var IndicatorInfo03 = new IndicatorInfo(
            "INDICATOR_INFO_03", // String - id
            CP_03, // Color - Label
            CP_00, // Color - Text
            CP_17, // Color - Label Outline
            false, // Bool - Outline Enabled
            indicatorFont, // Font
            false, // Bool - Show On Top
            true, // Bool - Show Label
            CP_03, // Color - Line
            1.0f, // Float - Line Width
            null, // Float - Line Dash
            true, // Bool - Show Line
            false, // Bool - Show Tag
            "50 SMA", // String - Tag
            CP_00, // Color - Tag Text
            CP_03, // Color - Tag Background
            true // Bool - Enabled
        )
            .setExtLastBar(true);
        grp.addRow(new IndicatorDescriptor(IND_03, "Fast", IndicatorInfo03, true));
        var IndicatorInfo04 = new IndicatorInfo(
            "INDICATOR_INFO_04", // String - id
            CP_04, // Color - Label
            CP_00, // Color - Text
            CP_17, // Color - Label Outline
            false, // Bool - Outline Enabled
            indicatorFont, // Font
            false, // Bool - Show On Top
            true, // Bool - Show Label
            CP_04, // Color - Line
            1.0f, // Float - Line Width
            null, // Float - Line Dash
            true, // Bool - Show Line
            false, // Bool - Show Tag
            "55 SMA", // String - Tag
            CP_00, // Color - Tag Text
            CP_04, // Color - Tag Background
            true // Bool - Enabled
        )
            .setExtLastBar(true);
        grp.addRow(new IndicatorDescriptor(IND_04, "Slow", IndicatorInfo04, true));

        // Tab - C
        tab = sd.addTab("200-233");
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
        grp.addRow(new PathDescriptor(PATH_05, "Fast", CP_05, 3.0f, null, true, false, true));
        grp.addRow(new PathDescriptor(PATH_06, "Slow", CP_06, 1.0f, null, true, false, true));
        // Group - Fill
        grp = tab.addGroup("Fill");
        grp.addRow(
            new ShadeDescriptor(FILL_05, "Top Fill", PATH_05, PATH_06, Enums.ShadeType.ABOVE, CP_13, true, true)
        );
        grp.addRow(
            new ShadeDescriptor(FILL_06, "Bottom Fill", PATH_05, PATH_06, Enums.ShadeType.BELOW, CP_14, true, true)
        );
        // Group - Indicators
        grp = tab.addGroup("Indicators");
        var IndicatorInfo05 = new IndicatorInfo(
            "INDICATOR_INFO_05", // String - id
            CP_05, // Color - Label
            CP_00, // Color - Text
            CP_17, // Color - Label Outline
            false, // Bool - Outline Enabled
            indicatorFont, // Font
            false, // Bool - Show On Top
            true, // Bool - Show Label
            CP_05, // Color - Line
            1.0f, // Float - Line Width
            null, // Float - Line Dash
            true, // Bool - Show Line
            false, // Bool - Show Tag
            "200 SMA", // String - Tag
            CP_00, // Color - Tag Text
            CP_05, // Color - Tag Background
            true // Bool - Enabled
        )
            .setExtLastBar(true);
        grp.addRow(new IndicatorDescriptor(IND_05, "Fast", IndicatorInfo05, true));
        var IndicatorInfo06 = new IndicatorInfo(
            "INDICATOR_INFO_06", // String - id
            CP_06, // Color - Label
            CP_00, // Color - Text
            CP_17, // Color - Label Outline
            false, // Bool - Outline Enabled
            indicatorFont, // Font
            false, // Bool - Show On Top
            true, // Bool - Show Label
            CP_06, // Color - Line
            1.0f, // Float - Line Width
            null, // Float - Line Dash
            true, // Bool - Show Line
            false, // Bool - Show Tag
            "233 SMA", // String - Tag
            CP_00, // Color - Tag Text
            CP_06, // Color - Tag Background
            true // Bool - Enabled
        )
            .setExtLastBar(true);
        grp.addRow(new IndicatorDescriptor(IND_06, "Slow", IndicatorInfo06, true));

        // Tab - D
        tab = sd.addTab("365-377");
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
        grp.addRow(new PathDescriptor(PATH_07, "Fast", CP_07, 3.0f, null, true, false, true));
        grp.addRow(new PathDescriptor(PATH_08, "Slow", CP_08, 1.0f, null, true, false, true));
        // Group - Fill
        grp = tab.addGroup("Fill");
        grp.addRow(
            new ShadeDescriptor(FILL_07, "Top Fill", PATH_07, PATH_08, Enums.ShadeType.ABOVE, CP_15, true, true)
        );
        grp.addRow(
            new ShadeDescriptor(FILL_08, "Bottom Fill", PATH_07, PATH_08, Enums.ShadeType.BELOW, CP_16, true, true)
        );
        // Group - Indicators
        grp = tab.addGroup("Indicators");
        var IndicatorInfo07 = new IndicatorInfo(
            "INDICATOR_INFO_07", // String - id
            CP_07, // Color - Label
            CP_00, // Color - Text
            CP_17, // Color - Label Outline
            false, // Bool - Outline Enabled
            indicatorFont, // Font
            false, // Bool - Show On Top
            true, // Bool - Show Label
            CP_07, // Color - Line
            1.0f, // Float - Line Width
            null, // Float - Line Dash
            true, // Bool - Show Line
            false, // Bool - Show Tag
            "365 SMA", // String - Tag
            CP_00, // Color - Tag Text
            CP_07, // Color - Tag Background
            true // Bool - Enabled
        )
            .setExtLastBar(true);
        grp.addRow(new IndicatorDescriptor(IND_07, "Fast", IndicatorInfo07, true));
        var IndicatorInfo08 = new IndicatorInfo(
            "INDICATOR_INFO_08", // String - id
            CP_08, // Color - Label
            CP_00, // Color - Text
            CP_17, // Color - Label Outline
            false, // Bool - Outline Enabled
            indicatorFont, // Font
            false, // Bool - Show On Top
            true, // Bool - Show Label
            CP_08, // Color - Line
            1.0f, // Float - Line Width
            null, // Float - Line Dash
            true, // Bool - Show Line
            false, // Bool - Show Tag
            "377 SMA", // String - Tag
            CP_00, // Color - Tag Text
            CP_08, // Color - Tag Background
            true // Bool - Enabled
        )
            .setExtLastBar(true);
        grp.addRow(new IndicatorDescriptor(IND_08, "Slow", IndicatorInfo08, true));

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

        /* === RUNTIME DESCRIPTOR === */
        var desc = createRD();
        // Label Settings
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
        // Export Values for Bands
        desc.exportValue(new ValueDescriptor(Values.VALUE_01, "SMA", new String[] { INPUT_01, METHOD_01, PERIOD_01 }));
        desc.exportValue(new ValueDescriptor(Values.VALUE_02, "SMA", new String[] { INPUT_02, METHOD_02, PERIOD_02 }));
        desc.exportValue(new ValueDescriptor(Values.VALUE_03, "SMA", new String[] { INPUT_03, METHOD_03, PERIOD_03 }));
        desc.exportValue(new ValueDescriptor(Values.VALUE_04, "SMA", new String[] { INPUT_04, METHOD_04, PERIOD_04 }));
        desc.exportValue(new ValueDescriptor(Values.VALUE_05, "SMA", new String[] { INPUT_05, METHOD_05, PERIOD_05 }));
        desc.exportValue(new ValueDescriptor(Values.VALUE_06, "SMA", new String[] { INPUT_06, METHOD_06, PERIOD_06 }));
        desc.exportValue(new ValueDescriptor(Values.VALUE_07, "SMA", new String[] { INPUT_07, METHOD_07, PERIOD_07 }));
        desc.exportValue(new ValueDescriptor(Values.VALUE_08, "SMA", new String[] { INPUT_08, METHOD_08, PERIOD_08 }));
        // Export Values for Signals
        desc.exportValue(new ValueDescriptor(Signals.CROSS_ABOVE_A, Enums.ValueType.BOOLEAN, "Cross Above A"));
        desc.exportValue(new ValueDescriptor(Signals.CROSS_BELOW_A, Enums.ValueType.BOOLEAN, "Cross Below A"));
        desc.exportValue(new ValueDescriptor(Signals.CROSS_ABOVE_B, Enums.ValueType.BOOLEAN, "Cross Above B"));
        desc.exportValue(new ValueDescriptor(Signals.CROSS_BELOW_B, Enums.ValueType.BOOLEAN, "Cross Below B"));
        desc.exportValue(new ValueDescriptor(Signals.CROSS_ABOVE_C, Enums.ValueType.BOOLEAN, "Cross Above C"));
        desc.exportValue(new ValueDescriptor(Signals.CROSS_BELOW_C, Enums.ValueType.BOOLEAN, "Cross Below C"));
        desc.exportValue(new ValueDescriptor(Signals.CROSS_ABOVE_D, Enums.ValueType.BOOLEAN, "Cross Above D"));
        desc.exportValue(new ValueDescriptor(Signals.CROSS_BELOW_D, Enums.ValueType.BOOLEAN, "Cross Below D"));
        // Declare Paths
        desc.declarePath(Values.VALUE_01, PATH_01);
        desc.declarePath(Values.VALUE_02, PATH_02);
        desc.declarePath(Values.VALUE_03, PATH_03);
        desc.declarePath(Values.VALUE_04, PATH_04);
        desc.declarePath(Values.VALUE_05, PATH_05);
        desc.declarePath(Values.VALUE_06, PATH_06);
        desc.declarePath(Values.VALUE_07, PATH_07);
        desc.declarePath(Values.VALUE_08, PATH_08);
        // Declare Indicators
        desc.declareIndicator(Values.VALUE_01, IND_01);
        desc.declareIndicator(Values.VALUE_02, IND_02);
        desc.declareIndicator(Values.VALUE_03, IND_03);
        desc.declareIndicator(Values.VALUE_04, IND_04);
        desc.declareIndicator(Values.VALUE_05, IND_05);
        desc.declareIndicator(Values.VALUE_06, IND_06);
        desc.declareIndicator(Values.VALUE_07, IND_07);
        desc.declareIndicator(Values.VALUE_08, IND_08);
        // Signals
        desc.declareSignal(Signals.CROSS_ABOVE_A, "Fast A Cross Above");
        desc.declareSignal(Signals.CROSS_BELOW_A, "Fast A Cross Below");
        desc.declareSignal(Signals.CROSS_ABOVE_B, "Fast B Cross Above");
        desc.declareSignal(Signals.CROSS_BELOW_B, "Fast B Cross Below");
        desc.declareSignal(Signals.CROSS_ABOVE_C, "Fast C Cross Above");
        desc.declareSignal(Signals.CROSS_BELOW_C, "Fast C Cross Below");
        desc.declareSignal(Signals.CROSS_ABOVE_D, "Fast D Cross Above");
        desc.declareSignal(Signals.CROSS_BELOW_D, "Fast D Cross Below");
        // Range Keys
        desc.setRangeKeys(Values.VALUE_01, Values.VALUE_02);
        desc.setRangeKeys(Values.VALUE_03, Values.VALUE_04);
        desc.setRangeKeys(Values.VALUE_05, Values.VALUE_06);
        desc.setRangeKeys(Values.VALUE_07, Values.VALUE_08);
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

        series.setDouble(index, Values.VALUE_01, fastMA_A);
        series.setDouble(index, Values.VALUE_02, slowMA_A);
        series.setDouble(index, Values.VALUE_03, fastMA_B);
        series.setDouble(index, Values.VALUE_04, slowMA_B);
        series.setDouble(index, Values.VALUE_05, fastMA_C);
        series.setDouble(index, Values.VALUE_06, slowMA_C);
        series.setDouble(index, Values.VALUE_07, fastMA_D);
        series.setDouble(index, Values.VALUE_08, slowMA_D);

        if (!series.isBarComplete(index)) {
            return;
        }

        // Check to see if a cross occurred and raise signal.
        var c_A = new Coordinate(series.getStartTime(index), slowMA_A);
        if (crossedAbove(series, index, Values.VALUE_01, Values.VALUE_02)) {
            var marker = getSettings().getMarker(UP_MARKER_01);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_A, Enums.Position.BOTTOM, marker));
            }
            ctx.signal(index, Signals.CROSS_ABOVE_A, "Fast MA Crossed Above!", series.getClose(index));
        } else if (crossedBelow(series, index, Values.VALUE_01, Values.VALUE_02)) {
            var marker = getSettings().getMarker(DN_MARKER_01);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_A, Enums.Position.TOP, marker));
            }
            ctx.signal(index, Signals.CROSS_BELOW_A, "Fast MA Crossed Below!", series.getClose(index));
        }

        // Check to see if a cross occurred and raise signal.
        var c_B = new Coordinate(series.getStartTime(index), slowMA_B);
        if (crossedAbove(series, index, Values.VALUE_03, Values.VALUE_04)) {
            var marker = getSettings().getMarker(UP_MARKER_02);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_B, Enums.Position.BOTTOM, marker));
            }
            ctx.signal(index, Signals.CROSS_ABOVE_B, "Fast MA Crossed Above!", series.getClose(index));
        } else if (crossedBelow(series, index, Values.VALUE_03, Values.VALUE_04)) {
            var marker = getSettings().getMarker(DN_MARKER_02);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_B, Enums.Position.TOP, marker));
            }
            ctx.signal(index, Signals.CROSS_BELOW_B, "Fast MA Crossed Below!", series.getClose(index));
        }

        // Check to see if a cross occurred and raise signal.
        var c_C = new Coordinate(series.getStartTime(index), slowMA_C);
        if (crossedAbove(series, index, Values.VALUE_05, Values.VALUE_06)) {
            var marker = getSettings().getMarker(UP_MARKER_03);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_C, Enums.Position.BOTTOM, marker));
            }
            ctx.signal(index, Signals.CROSS_ABOVE_C, "Fast MA Crossed Above!", series.getClose(index));
        } else if (crossedBelow(series, index, Values.VALUE_05, Values.VALUE_06)) {
            var marker = getSettings().getMarker(DN_MARKER_03);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_C, Enums.Position.TOP, marker));
            }
            ctx.signal(index, Signals.CROSS_BELOW_C, "Fast MA Crossed Below!", series.getClose(index));
        }

        // Check to see if a cross occurred and raise signal.
        var c_D = new Coordinate(series.getStartTime(index), slowMA_D);
        if (crossedAbove(series, index, Values.VALUE_07, Values.VALUE_08)) {
            var marker = getSettings().getMarker(UP_MARKER_04);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_D, Enums.Position.BOTTOM, marker));
            }
            ctx.signal(index, Signals.CROSS_ABOVE_D, "Fast MA Crossed Above!", series.getClose(index));
        } else if (crossedBelow(series, index, Values.VALUE_07, Values.VALUE_08)) {
            var marker = getSettings().getMarker(DN_MARKER_04);
            if (marker.isEnabled()) {
                addFigure(new Marker(c_D, Enums.Position.TOP, marker));
            }
            ctx.signal(index, Signals.CROSS_BELOW_D, "Fast MA Crossed Below!", series.getClose(index));
        }

        series.setComplete(index);
    }
}
