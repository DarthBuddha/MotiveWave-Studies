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
import java.awt.Color;

/**
 * Volume Delta indicator with Money Flow Multiplier weighting.
 * Calculates volume delta by weighting volume based on where the close
 * is within the high-low range. Includes cumulative volume delta (CVD)
 * and moving average of volume delta.
 */
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
        VOLUME, // Buy/Sell volume (foreground)
        TOTAL, // Total volume (background)
        VMA,
    }

    // === COLOR PALETTE === //
    private static final Color C_LIME = new Color(0, 255, 0, 255); // Lime (strong buy)
    private static final Color C_GREEN = new Color(0, 128, 0, 255); // Green (buy)
    private static final Color C_GRAY = new Color(120, 120, 120, 255); // Gray (neutral)
    private static final Color C_RED = new Color(128, 0, 0, 255); // Red (sell)
    private static final Color C_DARK_RED = new Color(255, 0, 0, 255); // Dark Red (strong sell)

    // === INPUT === //
    private static final String VOLUME_IND = "volumeInd";
    private static final String TOTAL_IND = "totalInd";
    private static final String VMA_IND = "vmaInd";

    @Override
    /* === INITIALIZE === */
    public void initialize(Defaults defaults) {
        // === SETTING DESCRIPTOR === //
        var sd = createSD();
        var tab = sd.addTab("General");
        // === Group: Volume
        var grp = tab.addGroup("Volume");

        // Background: Total Volume with gradient
        var totalBars = new PathDescriptor(
            "totalBar",
            "Total Volume (Background)",
            C_GRAY,
            1.0f,
            null,
            true,
            false,
            true
        );
        totalBars.setShowAsBars(true);
        totalBars.setSupportsShowAsBars(true);
        totalBars.setSupportsDisable(false);
        totalBars.setColorPolicy(Enums.ColorPolicy.SOLID);
        grp.addRow(totalBars);
        grp.addRow(new IndicatorDescriptor(TOTAL_IND, "Indicator", null, null, false, false, true));

        // Foreground: Buy/Sell Volume with solid colors
        var bars = new PathDescriptor(
            Inputs.BAR,
            "Buy/Sell Volume (Foreground)",
            defaults.getBarColor(),
            1.0f,
            null,
            true,
            false,
            true
        );
        bars.setShowAsBars(true);
        bars.setSupportsShowAsBars(true);
        bars.setSupportsDisable(false);
        bars.setColorPolicy(Enums.ColorPolicy.SOLID);
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
        desc.exportValue(new ValueDescriptor(Values.TOTAL, "Total Volume", new String[] {}));
        desc.exportValue(new ValueDescriptor(Values.VOLUME, "Volume Delta", new String[] {}));
        desc.exportValue(new ValueDescriptor(Values.VMA, "VMA", new String[] { Inputs.METHOD, Inputs.PERIOD }));
        desc.declarePath(Values.TOTAL, "totalBar");
        desc.declarePath(Values.VOLUME, Inputs.BAR);
        desc.declarePath(Values.VMA, Inputs.PATH);
        desc.setRangeKeys(Values.TOTAL, Values.VOLUME, Values.VMA);

        desc.declareIndicator(Values.TOTAL, TOTAL_IND);
        desc.declareIndicator(Values.VOLUME, VOLUME_IND);
        desc.declareIndicator(Values.VMA, VMA_IND);
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
        maxVolDelta = 1.0; // Reset max volume delta
    }

    @Override
    /* === CALCULATE === */
    protected void calculate(int index, DataContext ctx) {
        var series = ctx.getDataSeries();

        double high = series.getHigh(index);
        double low = series.getLow(index);
        double close = series.getClose(index);
        double open = series.getOpen(index);
        float vol = series.getVolumeAsFloat(index);

        // Calculate Money Flow Multiplier (MFM)
        // MFM = ((close - low) - (high - close)) / (high - low)
        // This weights volume based on where close is in the range:
        // - Close near high (buying) = MFM near +1
        // - Close near low (selling) = MFM near -1
        // - Close in middle = MFM near 0
        double mfm;
        double range = high - low;

        if (range > 0.0) {
            // Standard Money Flow Multiplier calculation
            mfm = ((close - low) - (high - close)) / range;
        } else {
            // Handle doji/single-price bars based on direction
            mfm = (close >= open) ? 1.0 : -1.0;
        }

        // Calculate weighted volume delta using Money Flow Multiplier
        double volDelta = mfm * vol;

        // Split into buy and sell volume
        double buyVol = volDelta >= 0 ? volDelta : 0;
        double sellVol = volDelta < 0 ? -volDelta : 0;

        // Update max volume for gradient scaling
        if (vol > maxVolDelta) {
            maxVolDelta = vol;
        } else if (index > 100) {
            // Gradually decay max to adapt to changing market conditions
            maxVolDelta *= 0.9999;
        }

        // Background: Total volume with gradient color based on delta
        Color gradientColor = calculateGradientColor(volDelta, vol);
        series.setDouble(index, Values.TOTAL, (double) vol);
        series.setBarColor(index, Values.TOTAL, gradientColor);

        // Foreground: Buy or sell volume with solid colors
        Color solidColor;
        double deltaAmount;
        if (volDelta >= 0) {
            // Buying pressure - show buy volume in green
            deltaAmount = buyVol;
            solidColor = C_GREEN;
        } else {
            // Selling pressure - show sell volume in red
            deltaAmount = sellVol;
            solidColor = C_RED;
        }
        series.setDouble(index, Values.VOLUME, deltaAmount);
        series.setBarColor(index, Values.VOLUME, solidColor);

        // Calculate moving average of volume delta
        if (path != null && path.isEnabled() && index >= period) {
            series.setDouble(index, Values.VMA, series.ma(method, index, period, Values.VOLUME));
        }

        series.setComplete(index);
    }

    /**
     * Calculate gradient color based on volume delta strength.
     * Matches TradingView's color.from_gradient behavior
     * Positive delta: gray -> lime
     * Negative delta: red -> gray
     */
    private Color calculateGradientColor(double volDelta, double totalVol) {
        double vmax = Math.max(totalVol, 1e-10);

        if (volDelta >= 0) {
            // Positive delta: gradient from gray to lime
            float ratio = (float) Math.min(1.0, volDelta / vmax);
            return interpolateColor(C_GRAY, C_LIME, ratio);
        } else {
            // Negative delta: gradient from red to gray
            float ratio = (float) Math.min(1.0, -volDelta / vmax);
            return interpolateColor(C_GRAY, C_DARK_RED, ratio);
        }
    }

    /**
     * Interpolate between two colors.
     */
    private Color interpolateColor(Color c1, Color c2, float ratio) {
        int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * ratio);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * ratio);
        int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * ratio);
        return new Color(r, g, b);
    }

    private int period;
    private Enums.MAMethod method;
    private PathInfo path;
    private double maxVolDelta = 1.0; // Track maximum volume delta for gradient scaling
}
