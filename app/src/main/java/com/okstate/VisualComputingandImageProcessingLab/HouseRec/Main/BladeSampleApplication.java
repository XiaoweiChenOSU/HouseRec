package com.okstate.VisualComputingandImageProcessingLab.HouseRec.Main;

import com.vuzix.hud.resources.DynamicThemeApplication;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.R;

public class BladeSampleApplication extends DynamicThemeApplication {

    @Override
    protected int getNormalThemeResId() {
        return R.style.AppTheme;
    }

    @Override
    protected int getLightThemeResId() {
        return R.style.AppTheme_Light;
    }
}
