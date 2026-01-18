package chartbuddha;

import com.motivewave.platform.sdk.common.DataContext;
import com.motivewave.platform.sdk.common.Defaults;
import com.motivewave.platform.sdk.common.Enums;
import com.motivewave.platform.sdk.common.Inputs;
import com.motivewave.platform.sdk.common.LineInfo;
import com.motivewave.platform.sdk.common.PathInfo;
import com.motivewave.platform.sdk.common.Util;
import com.motivewave.platform.sdk.common.desc.IndicatorDescriptor;
import com.motivewave.platform.sdk.common.desc.IntegerDescriptor;
import com.motivewave.platform.sdk.common.desc.MAMethodDescriptor;
import com.motivewave.platform.sdk.common.desc.PathDescriptor;
import com.motivewave.platform.sdk.common.desc.ValueDescriptor;
import com.motivewave.platform.sdk.study.Study;
import com.motivewave.platform.sdk.study.StudyHeader;

/** Displays volume as bars */
@StudyHeader(
    namespace = "com.chartbuddha",
    id = "BUDDHA_VOL_DELTA",
    name = "Buddha Volume Delta",
    label = "Vol Delta",
    desc = "Volume Delta",
    menu = "Chart Buddha",
    overlay = false,
    requiresVolume = true,
    requiresBarUpdates = true
)
public class BuddhaVolDelta extends Study {

    enum Values {
        VOLUME,
        VMA,
    }

    // === COLOR PALETTE === //
    // private static final Color C_01 = new Color(000, 128, 000, 255); // Dark Green
    // private static final Color C_02 = new Color(128, 000, 000, 255); // Dark Red
    // private static final Color C_03 = new Color(120, 120, 120, 255); // Gray

    // === INPUT === //
    private static final String VOLUME_IND = "volumeInd";
    private static final String VMA_IND = "vmaInd";

    @Override
    /* === INITIALIZE === */
    public void initialize(Defaults defaults) {
        // === SETTING DESCRIPTOR === //
        var sd = createSD();
        var tab = sd.addTab("General");
        // === Group: Volume
        var grp = tab.addGroup("Volume");
        var bars = new PathDescriptor(Inputs.BAR, "Volume Bars", defaults.getBarColor(), 1.0f, null, true, false, true);
        bars.setShowAsBars(true);
        bars.setSupportsShowAsBars(true);
        bars.setSupportsDisable(false);
        bars.setColorPolicies(
            new Enums.ColorPolicy[] {
                Enums.ColorPolicy.PRICE_BAR,
                Enums.ColorPolicy.SOLID,
                Enums.ColorPolicy.HIGHER_LOWER,
                Enums.ColorPolicy.GRADIENT,
            }
        );
        bars.setColorPolicy(Enums.ColorPolicy.PRICE_BAR);
        grp.addRow(bars);
        grp.addRow(new IndicatorDescriptor(VOLUME_IND, "Indicator", null, null, false, true, true));
        // === Group: Moving Average
        grp = tab.addGroup("Moving Average");
        grp.addRow(new MAMethodDescriptor(Inputs.METHOD, "Method", Enums.MAMethod.SMA));
        grp.addRow(new IntegerDescriptor(Inputs.PERIOD, "Period", 21, 1, 9999, 1));
        var pdesc = new PathDescriptor(Inputs.PATH, "Line", Util.awtColor(225, 102, 0), 1.0f, null, false, false, true);
        pdesc.setShadeType(Enums.ShadeType.BELOW);
        grp.addRow(pdesc);
        grp.addRow(new IndicatorDescriptor(VMA_IND, "Indicator", null, null, false, false, true));

        // === QUICK SETTINGS === //
        // sd.addQuickSettings(Inputs.BAR, Inputs.METHOD);
        // sd.addQuickSettings(
        //     new SliderDescriptor(Inputs.PERIOD, "LBL_PERIOD", 20, 1, 9999, true, () -> Enums.Icon.SINE_WAVE.get())
        // );
        // sd.addQuickSettings(Inputs.PATH);

        // === RUNTIME DESCRIPTOR === //
        var desc = createRD();
        desc.exportValue(new ValueDescriptor(Values.VOLUME, "Volume", new String[] {}));
        desc.exportValue(new ValueDescriptor(Values.VMA, "VMA", new String[] { Inputs.METHOD, Inputs.PERIOD }));
        desc.declarePath(Values.VOLUME, Inputs.BAR);
        desc.declarePath(Values.VMA, Inputs.PATH);
        desc.setRangeKeys(Values.VOLUME, Values.VMA);

        desc.declareIndicator(Values.VOLUME, VOLUME_IND);
        desc.declareIndicator(Values.VMA, VMA_IND);
        desc.setMinTopValue(0);
        desc.setFixedBottomValue(0);
        desc.setBottomInsetPixels(0);
        desc.setMinTick(1.0);
        desc.setTopInsetPixels(20);
        desc.setBottomInsetPixels(20);

        // Add zero line
        LineInfo zeroLine = new LineInfo(0.0, Util.awtColor(150, 150, 150), 1.0f, null, true);
        desc.addHorizontalLine(zeroLine);
    }

    @Override
    /* === CLEAR STATE === */
    public void clearState() {
        super.clearState();
        path = getSettings().getPath(Inputs.PATH);
        period = getSettings().getInteger(Inputs.PERIOD, 21);
        method = getSettings().getMAMethod(Inputs.METHOD, Enums.MAMethod.SMA);
    }

    @Override
    /* === CALCULATE === */
    protected void calculate(int index, DataContext ctx) {
        var series = ctx.getDataSeries();
        float vol = series.getVolumeAsFloat(index);

        // Calculate volume delta: positive when close >= open (buying), negative when close < open (selling)
        // Display as absolute value but color based on bar direction (PRICE_BAR color policy)
        double close = series.getClose(index);
        double open = series.getOpen(index);
        float volDelta = (close >= open) ? vol : -vol;

        // Store absolute value so all bars display upward, but PRICE_BAR coloring maintains green/red/gray
        series.setFloat(index, Values.VOLUME, Math.abs(volDelta));
        if (path != null && path.isEnabled() && index >= period) {
            series.setDouble(index, Values.VMA, series.ma(method, index, period, Values.VOLUME));
        }
        series.setComplete(index);
    }

    private int period;
    private Enums.MAMethod method;
    private PathInfo path;
}
