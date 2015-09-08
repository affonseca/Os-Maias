package com.mobilelearning.maias;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mobilelearning.maias.screens.ChallengeMenu;
import com.mobilelearning.maias.screens.GameScreen;
import com.mobilelearning.maias.screens.MainMenu;
import com.mobilelearning.maias.screens.StandardScreen;
import com.mobilelearning.maias.screens.StartupScreen;
import com.mobilelearning.maias.screens.TrainingMenu;

public class MaiasGame extends Game {
    private StandardScreen newScreen;
    private SpriteBatch batch;

    @Override
    public void create () {
        batch = new SpriteBatch();

        //Loading assets and saved data
        Assets.loadStartupAssets();
        SavedData.loadSavedData();

        //Starting with main menu
        StandardScreen startup = new StartupScreen(this, batch);
        startup.prepare();
        setScreen(startup);
    }

    @Override
    public void dispose() {
        super.dispose();
        Assets.dispose();
        batch.dispose();
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        super.render();
    }

    public void loadNextScreen(final StandardScreen oldScreen, ScreenType newScreenType){

        switch (newScreenType){
            case STARTUP:
                newScreen = new StartupScreen(this, batch);
                break;
            case MAIN_MENU:
                newScreen = new MainMenu(this, batch);
                break;
            case TRAINING_MENU:
                newScreen = new TrainingMenu(this, batch, false);
                break;
            case TRAINING_MENU_STATS:
                newScreen = new TrainingMenu(this, batch, true);
                break;
            case CHALLENGE_MENU:
                newScreen = new ChallengeMenu(this, batch);
                break;
            case TRAINING_1:
                newScreen = new GameScreen(this, batch, true, 1);
                break;
            case TRAINING_2:
                newScreen = new GameScreen(this, batch, true, 2);
                break;
            case TRAINING_3:
                newScreen = new GameScreen(this, batch, true, 3);
                break;
            case TRAINING_4:
                newScreen = new GameScreen(this, batch, true, 4);
                break;
            case TRAINING_5:
                newScreen = new GameScreen(this, batch, true, 5);
                break;
            case TRAINING_6:
                newScreen = new GameScreen(this, batch, true, 6);
                break;
            case TRAINING_7:
                newScreen = new GameScreen(this, batch, true, 7);
                break;
            case CHALLENGE_1:
                newScreen = new GameScreen(this, batch, false, 1);
                break;
            case CHALLENGE_2:
                newScreen = new GameScreen(this, batch, false, 2);
                break;
            case CHALLENGE_3:
                newScreen = new GameScreen(this, batch, false, 3);
                break;
            default:
                throw  new IllegalArgumentException("No valid screen type!");
        }

        newScreen.load();
        oldScreen.startLoadingAnimation(true);
    }

    public void changeScreen(final StandardScreen oldScreen){
        oldScreen.stopLoadingAnimation();
        newScreen.prepare();
        setScreen(newScreen);
        newScreen = null;
        oldScreen.unload();
        oldScreen.dispose();
    }

    public enum ScreenType{
        STARTUP, MAIN_MENU, TRAINING_MENU, TRAINING_MENU_STATS, CHALLENGE_MENU, TRAINING_1, TRAINING_2, TRAINING_3,
        TRAINING_4, TRAINING_5, TRAINING_6, TRAINING_7, CHALLENGE_1, CHALLENGE_2,CHALLENGE_3
    }
}
