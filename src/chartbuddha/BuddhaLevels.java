package chartbuddha;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.motivewave.platform.sdk.common.DataContext;
import com.motivewave.platform.sdk.common.DataSeries;
import com.motivewave.platform.sdk.common.Defaults;
import com.motivewave.platform.sdk.common.desc.ColorDescriptor;
import com.motivewave.platform.sdk.common.desc.DoubleDescriptor;
import com.motivewave.platform.sdk.common.desc.PathDescriptor;
import com.motivewave.platform.sdk.draw.Figure;
import com.motivewave.platform.sdk.draw.Line;
import com.motivewave.platform.sdk.study.Study;
import com.motivewave.platform.sdk.study.StudyHeader;

@StudyHeader(
    namespace = "com.chartbuddha",
    id = "BUDDHA_LEVELS",
    name = "Buddha Levels",
    desc = "Plots grid lines with pre-defined interval",
    menu = "Chart Buddha",
    overlay = true,
    underlayByDefault = true,
    requiresVolume = false,
    supportsBarUpdates = false
)
public class BuddhaLevels extends Study {

    // === Color Palette - ChartBuddha === //
    /** Color - Transparent */
    private static final Color CLR_01 = new Color(051, 051, 051, 255); // Opaque White
    // === Setting Keys === //
    /** Color Key - Grid */
    private static final String COLOR_GRID = "color_grid";
    /** Input - Step */
    private static final String STEP = "step";
    /** Input - Grid */
    private static final String GRID = "grid";
    /** Input - Start */
    private static final String START = "start";
    /** Input - End */
    private static final String END = "end";

    @Override
    /* === INITIALIZE === */
    public void initialize(Defaults defaults) {
        /* === SETTING DESCRIPTOR === */
        var sd = createSD();
        // Tab - General
        var tab = sd.addTab("General");
        // Group - Inputs
        var grp = tab.addGroup("Inputs");
        grp.addRow(new DoubleDescriptor(START, "Start", 0.0D, 0.0D, 100000.0D, 0.01D));
        grp.addRow(new DoubleDescriptor(END, "End", 0.0D, 0.0D, 100000.0D, 0.01D));
        grp.addRow(new DoubleDescriptor(STEP, "Step", 0.0D, 0.0D, 1000.0D, 0.01D));
        // Group - Colors
        grp = tab.addGroup("Colors");
        grp.addRow(new ColorDescriptor(COLOR_GRID, "Grid", CLR_01, true, true));
        grp.addRow(
            new PathDescriptor(
                GRID, // name of the setting (key)
                "Grid", // label displayed to the user
                CLR_01, // default color of the path line
                3.0f, // default width of the path line
                null, // default dash value of the path line
                true, // true if the path is enabled by default
                true, // true if the max points setting is allowed
                false // true if the user can disable this path
            )
        );

        /* === RUNTIME DESCRIPTOR === */
        var desc = createRD();
        desc.setLabelSettings(new String[] { STEP, GRID });
        setRuntimeDescriptor(desc);
    }

    @Override
    public void clearState() {
        clearFigures();
        this.gridLines.clear();
    }

    @Override
    protected void calculateValues(DataContext ctx) {
        DataSeries series = ctx.getDataSeries();
        if (series.size() < 2) return;
        double start = getSettings().getDouble(START);
        double end = getSettings().getDouble(END);
        double step = getSettings().getDouble(STEP);
        boolean draw = true;
        while (draw) {
            Line line = new Line(series.getStartTime(1), start, series.getEndTime(series.size() - 1), start);
            line.setColor(getSettings().getColor(COLOR_GRID));
            line.setExtendRightBounds(true);
            if (!this.gridLines.containsKey(start)) {
                addFigure((Figure) line);
                this.gridLines.put(start, "dummy");
            }
            start += step;
            if (start > end) draw = false;
        }
    }

    protected Map<Double, String> gridLines = Collections.synchronizedMap(new HashMap<>());
}
