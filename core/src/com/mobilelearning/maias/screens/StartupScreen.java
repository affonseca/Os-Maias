package com.mobilelearning.maias.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mobilelearning.maias.Assets;
import com.mobilelearning.maias.MaiasGame;

/**
 * Created by AFFonseca on 16/04/2015.
 */
public class StartupScreen extends StandardScreen {
    private TextureRegionDrawable backgroundImage;
    private TextureRegionDrawable button_normal, button_clicked;

    public StartupScreen(MaiasGame game, SpriteBatch batch) {
        super(game, batch);
    }

    @Override
    public void load() {
        Assets.loadStartupScreen();
    }

    @Override
    public void prepare() {
        super.prepare();
        TextureAtlas atlas = Assets.prepareStartupScreen();
        backgroundImage = new TextureRegionDrawable(atlas.findRegion("background"));
        button_normal = new TextureRegionDrawable(atlas.findRegion("start_normal"));
        button_clicked = new TextureRegionDrawable(atlas.findRegion("start_clicked"));

    }

    @Override
    public void unload() {
        Assets.unloadStartupScreen();
    }

    @Override
    public void show() {
        super.show();

        backgroundStage.addActor(new Image(backgroundImage));

        Button startupButton = new Button(button_normal, button_clicked);
        startupButton.setPosition((mainStage.getWidth()-startupButton.getPrefWidth())/2, 198f);
        startupButton.addListener((new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                Assets.loadGlobalAssets();
                game.loadNextScreen(StartupScreen.this, MaiasGame.ScreenType.MAIN_MENU);
            }
        }));


        mainStage.addActor(startupButton);
    }
}
