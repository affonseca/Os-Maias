package com.mobilelearning.maias.uiUtils;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by AFFonseca on 21/04/2015.
 */
public class RgbColor extends Color {

    public RgbColor(float a, float b, float c, float d) {
        super(a/255f, b/255f, c/255f, d/255f);
    }
}
