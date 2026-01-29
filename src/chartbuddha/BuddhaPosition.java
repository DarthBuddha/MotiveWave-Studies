package chartbuddha;

import com.motivewave.platform.sdk.common.DataContext;
import com.motivewave.platform.sdk.common.DataSeries;
import com.motivewave.platform.sdk.common.Defaults;
import com.motivewave.platform.sdk.common.desc.DoubleDescriptor;
import com.motivewave.platform.sdk.common.desc.PathDescriptor;
import com.motivewave.platform.sdk.draw.Figure;
import com.motivewave.platform.sdk.draw.Line;
import com.motivewave.platform.sdk.study.Study;
import com.motivewave.platform.sdk.study.StudyHeader;
import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@StudyHeader(
    namespace = "com.chartbuddha",
    id = "BUDDHA_POSITION",
    name = "Buddha Position",
    desc = "Helps visualize your position.",
    menu = "Chart Buddha",
    overlay = true,
    underlayByDefault = true,
    requiresVolume = false,
    supportsBarUpdates = false
)
public class BuddhaPosition extends Study {

    // === Color Palette - ChartBuddha === //
    /** Color - Major Levels */
    private static final Color CLR_01 = new Color(200, 200, 200, 255); // Brighter for major levels
    /** Color - Minor Levels */
    private static final Color CLR_02 = new Color(100, 100, 100, 128); // Dimmer for minor levels
    // === Setting Keys === //
    /** Input - Major Step */
    private static final String MAJOR_STEP = "major_step";
    /** Input - Minor Step */
    private static final String MINOR_STEP = "minor_step";
    /** Path - Major Line */
    private static final String MAJOR_LINE = "major_line";
    /** Path - Minor Line */
    private static final String MINOR_LINE = "minor_line";
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
        grp.addRow(new DoubleDescriptor(MAJOR_STEP, "Major Step", 100.0D, 0.0D, 10000.0D, 0.01D));
        grp.addRow(new DoubleDescriptor(MINOR_STEP, "Minor Step", 10.0D, 0.0D, 1000.0D, 0.01D));
        // Group - Lines
        grp = tab.addGroup("Lines");
        grp.addRow(
            new PathDescriptor(
                MAJOR_LINE,
                "Major Line",
                CLR_01, // Brighter for major levels
                2.0f, // Thicker line
                null,
                true,
                false,
                false
            )
        );
        grp.addRow(
            new PathDescriptor(
                MINOR_LINE,
                "Minor Line",
                CLR_02, // Dimmer for minor levels
                1.0f, // Thinner line
                new float[] { 5.0f, 5.0f }, // Dashed line
                true,
                false,
                false
            )
        );

        /* === RUNTIME DESCRIPTOR === */
        var desc = createRD();
        desc.setLabelSettings(new String[] { MAJOR_STEP, MINOR_STEP });
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
        double majorStep = getSettings().getDouble(MAJOR_STEP);
        double minorStep = getSettings().getDouble(MINOR_STEP);

        // Validate steps
        if (minorStep <= 0) return;
        if (majorStep < minorStep) majorStep = minorStep;

        long startTime = series.getStartTime(1);
        long endTime = series.getEndTime(series.size() - 1);

        // Draw lines from start to end
        double currentLevel = start;
        while (currentLevel <= end) {
            // Determine if this is a major or minor line
            boolean isMajor = (majorStep > 0) && (Math.abs(currentLevel % majorStep) < 0.0001);

            // Create the line
            Line line = new Line(startTime, currentLevel, endTime, currentLevel);
            line.setExtendRightBounds(true);

            // Apply the appropriate PathDescriptor settings
            if (isMajor) {
                var pathInfo = getSettings().getPath(MAJOR_LINE);
                if (pathInfo != null && pathInfo.isEnabled()) {
                    line.setColor(pathInfo.getColor());
                }
            } else {
                var pathInfo = getSettings().getPath(MINOR_LINE);
                if (pathInfo != null && pathInfo.isEnabled()) {
                    line.setColor(pathInfo.getColor());
                }
            }

            // Add line if not already drawn at this level
            if (!this.gridLines.containsKey(currentLevel)) {
                addFigure((Figure) line);
                this.gridLines.put(currentLevel, "dummy");
            }

            currentLevel += minorStep;
        }
    }

    protected Map<Double, String> gridLines = Collections.synchronizedMap(new HashMap<>());
}
