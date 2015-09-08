package com.mobilelearning.maias.accessors;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * Created by AFFonseca on 06/08/2015.
 */
public class CellAccessor implements TweenAccessor<Cell<?>> {
public static final int WIDTH = 2000;
public static final int HEIGHT = 2001;

@Override
public int getValues(Cell<?> target, int tweenType, float[] returnValues) {
        switch (tweenType){
            case WIDTH:
                returnValues[0] = target.getPrefWidth();
                return 1;
            case HEIGHT:
                returnValues[0] = target.getPrefHeight();
                return 1;
            default:
                throw new IllegalArgumentException("No valid tween type!");
        }
}

    @Override
    public void setValues(Cell<?> target, int tweenType, float[] newValues) {
        switch (tweenType){
            case WIDTH:
                target.width(newValues[0]);
                return;
            case HEIGHT:
                target.height(newValues[0]);
                return;
            default:
                throw new IllegalArgumentException("No valid tween type!");
        }
    }
}
