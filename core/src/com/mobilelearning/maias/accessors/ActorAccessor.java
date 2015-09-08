package com.mobilelearning.maias.accessors;

import com.badlogic.gdx.scenes.scene2d.Actor;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * Created with IntelliJ IDEA.
 * User: AFFonseca
 * Date: 17-02-2015
 * Time: 11:20
 * To change this template use File | Settings | File Templates.
 */
public class ActorAccessor implements TweenAccessor<Actor> {
    public static final int MOVE = 0;
    public static final int MOVE_X = 1;
    public static final int MOVE_Y = 2;
    public static final int ALPHA = 3;
    public static final int COLOR = 4;
    public static final int ROTATE = 5;
    public static final int SCALEXY = 6;

    @Override
    public int getValues(Actor target, int tweenType, float[] returnValues) {
        switch (tweenType){
            case MOVE:
                returnValues[0] = target.getX();
                returnValues[1] = target.getY();
                return 2;
            case MOVE_X:
                returnValues[0] = target.getX();
                return 1;
            case MOVE_Y:
                returnValues[0] = target.getY();
                return 1;
            case ALPHA:
                returnValues[0] = target.getColor().a;
                return 1;
            case  COLOR:
                returnValues[0] = target.getColor().r;
                returnValues[1] = target.getColor().g;
                returnValues[2] = target.getColor().b;
                return 3;
            case ROTATE:
                returnValues[0] = target.getRotation();
                return 1;
            case SCALEXY:
                returnValues[0] = target.getScaleX();
                return 1;
            default:
                throw new IllegalArgumentException("No valid tween type!");
        }
    }

    @Override
    public void setValues(Actor target, int tweenType, float[] newValues) {
        switch (tweenType){
            case MOVE:
                target.setPosition(newValues[0], newValues[1]);
                return;
            case MOVE_X:
                target.setX(newValues[0]);
                return;
            case MOVE_Y:
                target.setY(newValues[0]);
                return;
            case ALPHA:
                target.getColor().a = newValues[0];
                return;
            case COLOR:
                target.getColor().r = newValues[0];
                target.getColor().g = newValues[1];
                target.getColor().b = newValues[2];
                return;
            case ROTATE:
                target.setRotation(newValues[0]);
                return;
            case SCALEXY:
                target.setScale(newValues[0]);
                return;
            default:
                throw new IllegalArgumentException("No valid tween type!");

        }
    }
}
