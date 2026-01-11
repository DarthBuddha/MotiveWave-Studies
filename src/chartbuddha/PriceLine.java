package chartbuddha;

import com.motivewave.platform.sdk.common.Defaults;
import com.motivewave.platform.sdk.common.desc.IndicatorDescriptor;
import com.motivewave.platform.sdk.common.desc.ValueDescriptor;
import com.motivewave.platform.sdk.study.Study;
import com.motivewave.platform.sdk.study.StudyHeader;
import java.awt.Color;

@StudyHeader(
    namespace = "com.chartbuddha",
    id = "PRICE_LINE",
    name = "Price Line",
    label = "Price Line",
    desc = "Price Line Study",
    menu = "Chart Buddha",
    overlay = true,
    signals = true
)
public class PriceLine extends Study {

    enum Values {
        PRICE,
    }

    // SMA Bands color palette
    private static final Color CLR_PRICE = new Color(120, 123, 134, 255);
    private static final Color CLR_UP = new Color(000, 255, 000, 255);
    private static final Color CLR_DN = new Color(255, 000, 000, 255);

    private static final Color CLR_TEXT = new Color(000, 000, 000, 255);

    //
    private static final String PRICE_LINE = "price_line";

    @Override
    /* === INITIALIZE === */
    public void initialize(Defaults defaults) {
        // === SETTING DESCRIPTOR ===
        var sd = createSD();

        // Tab - A
        var tab = sd.addTab("Band A");
        // Group - Indicators
        var grp = tab.addGroup("Indicators");
        grp.addRow(
            new IndicatorDescriptor(
                PRICE_LINE,
                "Fast",
                CLR_PRICE,
                CLR_TEXT,
                defaults.getFont(),
                true,
                CLR_PRICE,
                1.0f,
                null,
                true,
                true,
                "PRICE",
                true,
                true
            )
        );

        // === RUNTIME DESCRIPTOR ===
        var desc = createRD();

        desc.exportValue(new ValueDescriptor(Values.PRICE, "Price", new String[] { PRICE_LINE }));

        desc.declareIndicator(Values.PRICE, PRICE_LINE);
    }
}
