package com.mobilelearning.maias.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mobilelearning.maias.Assets;
import com.mobilelearning.maias.MaiasGame;
import com.mobilelearning.maias.SavedData;
import com.mobilelearning.maias.accessors.ActorAccessor;
import com.mobilelearning.maias.serviceHandling.json.StatsData;
import com.mobilelearning.maias.uiUtils.TopTable;

import java.util.concurrent.atomic.AtomicInteger;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

/**
 * Created by AFFonseca on 03/08/2015.
 */
public class TrainingMenu extends StandardScreen {
    private final boolean loadInStats;
    private TextureAtlas atlas;
    private ImageButton statsButton;
    private ImageButton [] themeButtons;
    private Table mainTable, statsTable, currentTable, lastTable;
    private final MaiasGame.ScreenType [] themeScreens = {
            MaiasGame.ScreenType.TRAINING_1,
            MaiasGame.ScreenType.TRAINING_2,
            MaiasGame.ScreenType.TRAINING_3,
            MaiasGame.ScreenType.TRAINING_4,
            MaiasGame.ScreenType.TRAINING_5,
            MaiasGame.ScreenType.TRAINING_6,
            MaiasGame.ScreenType.TRAINING_7,
    };

    public TrainingMenu(MaiasGame game, SpriteBatch batch, boolean loadInStats) {
        super(game, batch);
        this.loadInStats = loadInStats;
    }

    @Override
    public void load() {

    }

    @Override
    public void prepare() {
        super.prepare();

        atlas = Assets.prepareTrainingMenu();

        themeButtons = new ImageButton[7];
        for (int i=0; i<themeButtons.length; i++){
            themeButtons[i] = new ImageButton(
                    new TextureRegionDrawable(atlas.findRegion("episode" +(i+1) +"_up")),
                    new TextureRegionDrawable(atlas.findRegion("episode" +(i+1) +"_down"))
            );

            final AtomicInteger aux = new AtomicInteger(i);
            themeButtons[i].addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    Assets.clickFX.play();
                    game.loadNextScreen(TrainingMenu.this, themeScreens[aux.get()]);
                }
            }) ;
        }

        statsButton = new ImageButton(
                new TextureRegionDrawable(atlas.findRegion("stats_button_up")),
                new TextureRegionDrawable(atlas.findRegion("stats_button_down"))

        );
    }

    @Override
    public void unload() {

    }

    @Override
    public void show() {
        Image background = new Image(atlas.findRegion("background"));
        backgroundStage.addActor(background);

        //Adding the elements to the main table
        mainTable = new Table();
        mainTable.setSize(mainStage.getWidth(), mainStage.getHeight() + mainStage.getPadBottom());
        mainTable.center().top().padTop(112f + mainStage.getPadBottom()).defaults().padBottom(6f);

        for(ImageButton button : themeButtons){
            mainTable.add(button).row();
        }
        mainTable.getCells().peek().padBottom(76f);
        mainTable.add(statsButton);
        statsButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();
                changeTable(statsTable, true);
            }
        });

        mainStage.addActor(mainTable);

        //Adding the top bar
        TopTable topTable = new TopTable("TREINO", true, uiSkin,
                new TopTable.BackButtonCallback() {
                    @Override
                    public void onClicked() {
                        game.loadNextScreen(TrainingMenu.this, MaiasGame.ScreenType.MAIN_MENU);
                    }
                });
        topTable.setPosition((mainStage.getWidth() - topTable.getWidth()) / 2,
                mainStage.getHeight() + mainStage.getPadBottom() - topTable.getHeight());
        mainTable.addActor(topTable);



        //Stats table
        statsTable = new Table();
        statsTable.setSize(mainStage.getWidth(), mainStage.getHeight() + mainStage.getPadBottom());
        statsTable.center().top().padTop(112f).defaults().padBottom(6f);

        statsTable.center().padTop(mainStage.getPadBottom());

        Table innerStatsTable = new Table();
        innerStatsTable.setBackground(new TextureRegionDrawable(atlas.findRegion("stats_panel")));
        innerStatsTable.setSize(innerStatsTable.getBackground().getMinWidth(),
                innerStatsTable.getBackground().getMinHeight());
        innerStatsTable.center().top().padTop(45f).padBottom(25f);

        StatsData statsData = SavedData.getStats();
        Label.LabelStyle labelStyle = new Label.LabelStyle(uiSkin.getFont("default-font"), Color.WHITE);

        Label themeTitle = new Label("EPISÓDIOS", labelStyle);
        themeTitle.setFontScale(1.6f);
        innerStatsTable.add(themeTitle).row();
        Label theme1Label = new Label("episódio 1  |  " + (int)(statsData.theme1Score * 100) + "%", labelStyle);
        theme1Label.setFontScale(0.8f); innerStatsTable.add(theme1Label).row();
        Label theme2Label = new Label("episódio 2  |  " + (int)(statsData.theme2Score * 100) + "%", labelStyle);
        theme2Label.setFontScale(0.8f); innerStatsTable.add(theme2Label).row();
        Label theme3Label = new Label("episódio 3  |  " + (int)(statsData.theme3Score * 100) + "%", labelStyle);
        theme3Label.setFontScale(0.8f); innerStatsTable.add(theme3Label).row();
        Label theme4Label = new Label("episódio 4  |  " + (int)(statsData.theme4Score * 100) + "%", labelStyle);
        theme4Label.setFontScale(0.8f); innerStatsTable.add(theme4Label).row();
        Label theme5Label = new Label("episódio 5  |  " + (int)(statsData.theme5Score * 100) + "%", labelStyle);
        theme5Label.setFontScale(0.8f); innerStatsTable.add(theme5Label).row();
        Label theme6Label = new Label("episódio 6  |  " + (int)(statsData.theme6Score * 100) + "%", labelStyle);
        theme6Label.setFontScale(0.8f); innerStatsTable.add(theme6Label).row();
        Label theme7Label = new Label("episódio 7  |  " + (int)(statsData.theme7Score * 100) + "%", labelStyle);
        theme7Label.setFontScale(0.8f); innerStatsTable.add(theme7Label).padBottom(40f).row();


        Label difficultyTitle = new Label("DIFICULDADE", labelStyle);
        difficultyTitle.setFontScale(1.6f);
        innerStatsTable.add(difficultyTitle).row();
        Label difficulty1Label = new Label("dificuldade 1  |  " + (int)(statsData.difficulty1Score * 100) + "%", labelStyle);
        difficulty1Label.setFontScale(0.8f); innerStatsTable.add(difficulty1Label).row();
        Label difficulty2Label = new Label("dificuldade 2  |  " + (int)(statsData.difficulty2Score * 100) + "%", labelStyle);
        difficulty2Label.setFontScale(0.8f); innerStatsTable.add(difficulty2Label).row();
        Label difficulty3Label = new Label("dificuldade 3  |  " + (int)(statsData.difficulty3Score * 100) + "%", labelStyle);
        difficulty3Label.setFontScale(0.8f); innerStatsTable.add(difficulty3Label).row();
        Label difficulty4Label = new Label("dificuldade 4  |  " + (int)(statsData.difficulty4Score * 100) + "%", labelStyle);
        difficulty4Label.setFontScale(0.8f); innerStatsTable.add(difficulty4Label).row();

        statsTable.add(innerStatsTable);

        //Adding the top bar
        TopTable statsTopTable = new TopTable("ESTATÍSTICAS", true, uiSkin,
                new TopTable.BackButtonCallback() {
                    @Override
                    public void onClicked() {
                        changeTable(mainTable, false);
                    }
                });
        statsTopTable.setPosition((mainStage.getWidth() - statsTopTable.getWidth()) / 2,
                mainStage.getHeight() + mainStage.getPadBottom() - statsTopTable.getHeight());
        statsTable.addActor(statsTopTable);

        mainStage.addActor(statsTable);

        if(loadInStats) {
            mainTable.setVisible(false);
            statsTable.setVisible(true);
            currentTable = statsTable;
        }
        else {
            mainTable.setVisible(true);
            statsTable.setVisible(false);
            currentTable = mainTable;
        }
        
    }

    private void changeTable(Table to, boolean goFront){
        to.setTouchable(Touchable.disabled);
        currentTable.setTouchable(Touchable.disabled);

        float newTableStart = mainStage.getWidth(), oldTableEnd = - mainStage.getWidth();

        if(!goFront){
            newTableStart *= -1;
            oldTableEnd *= -1;
        }

        to.setX(newTableStart);
        to.setVisible(true);
        Tween.to(to, ActorAccessor.MOVE_X, 1.0f).target(0.0f).start(tweenManager);
        Tween.to(currentTable, ActorAccessor.MOVE_X, 1.0f).target(oldTableEnd)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        if(type != TweenCallback.COMPLETE)
                            return;
                        lastTable.setVisible(false);
                        currentTable.setTouchable(Touchable.enabled);
                        lastTable.setTouchable(Touchable.enabled);
                    }
                })
                .start(tweenManager);
        lastTable = currentTable;
        currentTable = to;
    }
}
