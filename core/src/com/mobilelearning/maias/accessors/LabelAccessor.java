package com.mobilelearning.maias.accessors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * Created with IntelliJ IDEA.
 * User: AFFonseca
 * Date: 17-02-2015
 * Time: 11:20
 * To change this template use File | Settings | File Templates.
 */
public class LabelAccessor implements TweenAccessor<Label> {
    public static final int SCROLL = 1000;
    public static final int NUMBER_CHANGE = 1001;
    private static final ActorAccessor actorAccessor = new ActorAccessor();

    @Override
    public int getValues(Label target, int tweenType, float[] returnValues) {
        switch (tweenType){
            case SCROLL:
                returnValues[0] = ((float)target.getText().length())/((float)target.getName().length());
                return 1;
            case NUMBER_CHANGE:
                returnValues[0] = Float.parseFloat(target.getText().toString());
                return 1;
            default:
                return actorAccessor.getValues(target, tweenType, returnValues);
        }
    }

    @Override
    public void setValues(Label target, int tweenType, float[] newValues) {
        switch (tweenType){
            case SCROLL:
                target.setText(target.getName().substring(0, Math.round(newValues[0]*target.getName().length())));
                return;
            case NUMBER_CHANGE:
                target.setText("" +(int)newValues[0]);
                return;
            default:
                actorAccessor.setValues(target, tweenType, newValues);
        }
    }
}
