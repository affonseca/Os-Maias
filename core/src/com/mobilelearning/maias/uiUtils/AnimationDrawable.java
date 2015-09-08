package com.mobilelearning.maias.uiUtils;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

/**
 * Created with IntelliJ IDEA.
 * User: AFFonseca
 * Date: 16-02-2015
 * Time: 17:04
 * To change this template use File | Settings | File Templates.
 */
public class AnimationDrawable extends BaseDrawable
{
    public final Animation animation;

    private float stateTime = 0;
    public boolean isRunning = true;

    public AnimationDrawable(Animation anim)
    {
        this.animation = anim;
        setMinWidth(anim.getKeyFrames()[0].getRegionWidth());
        setMinHeight(anim.getKeyFrames()[0].getRegionHeight());
    }

    public void act(float delta)
    {
        if(isRunning)
            stateTime += delta;
    }

    public void reset()
    {
        stateTime = 0;
    }

    @Override
    public float getMinWidth() {
        return animation.getKeyFrames()[0].getRegionWidth();
    }

    @Override
    public float getMinHeight() {
        return animation.getKeyFrames()[0].getRegionHeight();
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        batch.draw(animation.getKeyFrame(stateTime), x, y, width, height);
    }

}
