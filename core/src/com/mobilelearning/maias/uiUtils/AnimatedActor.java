package com.mobilelearning.maias.uiUtils;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created with IntelliJ IDEA.
 * User: AFFonseca
 * Date: 16-02-2015
 * Time: 17:03
 * To change this template use File | Settings | File Templates.
 */

public class AnimatedActor extends Image
{
    private final AnimationDrawable drawable;

    public AnimatedActor(AnimationDrawable drawable)
    {
        super(drawable);
        this.drawable = drawable;
    }

    @Override
    public void act(float delta)
    {
        drawable.act(delta);
        super.act(delta);
    }

    public AnimationDrawable getDrawable(){
        return this.drawable;
    }

}
