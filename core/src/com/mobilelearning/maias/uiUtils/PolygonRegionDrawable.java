package com.mobilelearning.maias.uiUtils;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable;

/**
 * Created with IntelliJ IDEA.
 * User: AFFonseca
 * Date: 09-03-2015
 * Time: 15:56
 * To change this template use File | Settings | File Templates.
 */
public class PolygonRegionDrawable extends BaseDrawable implements TransformDrawable {

    private PolygonRegion region;
    private float startingX, startingY;

    public PolygonRegionDrawable() {
    }

    /** copies the region
     *  @see #PolygonRegionDrawable(com.badlogic.gdx.graphics.g2d.PolygonRegion, boolean) */
    public PolygonRegionDrawable(PolygonRegion region) {
        this(region, true);
    }

    public PolygonRegionDrawable(PolygonRegion region, boolean copyRegion) {
        setRegion(region, copyRegion);
    }

    /** copies the region
     *  @see #PolygonRegionDrawable(com.mobilelearning.maias.uiUtils.PolygonRegionDrawable, boolean) */
    public PolygonRegionDrawable(PolygonRegionDrawable drawable) {
        this(drawable, true);
    }

    public PolygonRegionDrawable(PolygonRegionDrawable drawable, boolean copyRegion) {
        super(drawable);
        setRegion(drawable.region, copyRegion);
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        x-= startingX; y-=startingY;
        if(batch instanceof PolygonSpriteBatch)
            ((PolygonSpriteBatch) batch).draw(region, x, y, width, height);
        else
            batch.draw(region.getRegion(), x, y, width, height);
    }

    @Override
    public void draw(Batch batch, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        x-= startingX; y-=startingY;
        if(batch instanceof PolygonSpriteBatch)
            ((PolygonSpriteBatch) batch).draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
        else
            batch.draw(region.getRegion(), x, y, originX, originY, width, height, scaleX, scaleY, rotation);
    }

    // SUSPICIOUS METHOD
    public void setRegion(PolygonRegion region, boolean copy) {
        PolygonRegion use = copy ? new PolygonRegion(new TextureRegion(region.getRegion()), region.getVertices(), region.getTriangles()) : region;
        this.region = use;
        float polyWidth = getPolygonWidth(use.getVertices()), polyHeight = getPolygonHeight(use.getVertices());
        use.getRegion().setRegionWidth((int) polyWidth);
        use.getRegion().setRegionHeight((int) polyHeight);
        setMinWidth(polyWidth);
        setMinHeight(polyHeight);
    }

    private float getPolygonWidth(float vertices[]){
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;

        for(int i=0; i<vertices.length; i+=2){
            if(vertices[i] < min)
                min = vertices[i];
            if(vertices[i] > max)
                max = vertices[i];
        }

        startingX = min;
        return max-min;
    }

    private float getPolygonHeight(float vertices[]){
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;

        for(int i=1; i<vertices.length; i+=2){
            if(vertices[i] < min)
                min = vertices[i];
            if(vertices[i] > max)
                max = vertices[i];
        }

        startingY = min;
        return max-min;
    }

    public PolygonRegion getRegion() {
        return region;
    }

    public float getStartingX(){
        return startingX;
    }

    public float getStartingY(){
        return startingY;
    }

}
