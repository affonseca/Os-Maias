package com.mobilelearning.maias.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.mobilelearning.maias.Assets;
import com.mobilelearning.maias.MaiasGame;
import com.mobilelearning.maias.SavedData;
import com.mobilelearning.maias.accessors.ActorAccessor;
import com.mobilelearning.maias.accessors.CellAccessor;
import com.mobilelearning.maias.accessors.LabelAccessor;
import com.mobilelearning.maias.questionHandling.Question;
import com.mobilelearning.maias.serviceHandling.json.StatsData;
import com.mobilelearning.maias.uiUtils.TopTable;
import com.mobilelearning.maias.uiUtils.UpdateStatsDialog;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;

/**
 * Created by AFFonseca on 03/08/2015.
 */
public class GameScreen extends StandardScreen {
    private TextureAtlas atlas;
    private final boolean isTraining;
    private Table infoTable;
    private ChallengeSave data;
    private int numberOfQuestions, wrongQuestions;
    private Label timer;
    private int maxTimerTime = 0;
    private Label questionLabel, moneyLabel;
    private Button answerButtons[];
    private Label answerLabels[];
    private Label progressLabel;
    private Button.ButtonStyle buttonNormalStyle, buttonCorrectStyle;
    private TextureRegionDrawable progressCorrect, progressWrong, progressCheckpoint, progressCheckpointCorrect;
    private Image progressImages[];
    private Button helpButton, backButton;
    private final int [][] checkpointValue = {
            {-1, -1, 1, -1, -1, 3, -1, -1, -1, 5},
            {-1, -1, 1, -1, -1, -1, -1, 3, -1, -1, -1, 5, -1, -1, 8},
            {-1, -1, 1, -1, -1, -1, -1, 3, -1, -1, -1, -1, -1, 5, -1, -1, -1, 8, -1, -1, -1, 10, -1, -1, 15},
    };
    private boolean unlockedNewLevel = false;
    private final String levelNames[] = {"Especialista", "Experiente"};



    @Override
    public void load() {

    }

    @Override
    public void prepare() {
        super.prepare();
        atlas = Assets.prepareGameScreen();

    }

    @Override
    public void unload() {

    }

    public GameScreen(MaiasGame game, SpriteBatch batch, boolean isTraining, int value) {
        super(game, batch);

        this.isTraining = isTraining;
        if(isTraining) {
            data = new ChallengeSave(value-1, Assets.questionDB.getThemeQuestions(value-1));
            numberOfQuestions = 10;
        }
        else {
            int challengeValue = value - 1;
            int difficultyArray [] = null;
            switch (challengeValue){
                case 0:
                    numberOfQuestions = 10;
                    maxTimerTime = 60;
                    difficultyArray = new int[]{0, 1};
                    break;
                case 1:
                    numberOfQuestions = 15;
                    maxTimerTime = 30;
                    difficultyArray = new int[]{0, 1, 2, 3};
                    break;
                case 2:
                    numberOfQuestions = 25;
                    maxTimerTime = 15;
                    difficultyArray = new int[]{2, 3, 4, 5};
                    break;
            }

            //try reloading
            data = SavedData.getChallengeSave();
            if(data == null || data.questions.size != numberOfQuestions)
                data = new ChallengeSave(challengeValue,
                        Assets.questionDB.getChallengeQuestions(difficultyArray, numberOfQuestions));
            else
                SavedData.setChallengeSave(null); //load and delete save
        }

    }

    @Override
    public void show() {
        super.show();

        Image background = new Image(atlas.findRegion("background"));
        backgroundStage.addActor(background);

        Table mainTable = new Table();
        mainTable.setSize(mainStage.getWidth(), mainStage.getHeight() + mainTable.getPadBottom());
        mainTable.center().padTop(142f).padBottom(10f).defaults().padBottom(12f);

        //Adding question table
        Table questionTable = new Table();
        TextureRegionDrawable questionPanel = new TextureRegionDrawable(atlas.findRegion("question_panel"));
        questionTable.setSize(questionPanel.getMinWidth(), questionPanel.getMinHeight());
        questionTable.center().top();
        questionTable.setBackground(questionPanel);
        mainTable.add(questionTable).padBottom(31f).row();

        Label.LabelStyle letterStyle = new Label.LabelStyle(uiSkin.getFont("arial"), Color.BLACK);
        Table stopwatchTable = new Table();
        TextureRegionDrawable stopWatchBackground = new TextureRegionDrawable(atlas.findRegion("stopwatch"));
        stopwatchTable.setSize(stopWatchBackground.getMinWidth(), stopWatchBackground.getMinHeight());
        stopwatchTable.setBackground(stopWatchBackground);
        timer = new Label("" +maxTimerTime, new Label.LabelStyle(uiSkin.getFont("default-font"), Color.BLACK));
        stopwatchTable.top().padTop(20f).add(timer);
        if(!isTraining)
            questionTable.add(stopwatchTable).padTop(-45f).row();

        Label.LabelStyle labelStyle = new Label.LabelStyle(uiSkin.getFont("arial"), Color.WHITE);
        questionLabel = new Label("", labelStyle); questionLabel.setWrap(true); questionLabel.setAlignment(Align.center);
        questionLabel.setFontScale(0.8f);
        questionTable.add(questionLabel).width(questionTable.getWidth() - 43f).height(questionPanel.getMinHeight());


        //Adding answer buttons
        String [] letters = {"A:  ", "B:  ", "C:  ", "D:  "};
        answerButtons = new Button[letters.length];
        answerLabels = new Label[letters.length];
        buttonNormalStyle = new Button.ButtonStyle(
                new TextureRegionDrawable(atlas.findRegion("answer_button")),
                new TextureRegionDrawable(atlas.findRegion("answer_button_selected")),
                new TextureRegionDrawable(atlas.findRegion("answer_button_selected")));
        buttonCorrectStyle = new Button.ButtonStyle(
                new TextureRegionDrawable(atlas.findRegion("answer_button_correct")), null, null);
        for(int i=0; i<letters.length; i++){
            answerButtons[i] = new Button(buttonNormalStyle);
            answerButtons[i].setTransform(true);
            answerButtons[i].center().left().padLeft(50f);
            answerButtons[i].add(new Label(letters[i], letterStyle));

            answerLabels[i] = new Label("", labelStyle);
            answerLabels[i].setFontScale(0.8f); answerLabels[i].setWrap(true);
            answerButtons[i].add(answerLabels[i]).width(425f);
            final AtomicInteger aux = new AtomicInteger(i);
            answerButtons[i].addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    Assets.clickFX.play();
                    onAnswer(answerLabels[aux.get()].getText().toString());
                }
            });

            mainTable.add(answerButtons[i]).row();
        }
        mainTable.getCells().peek().padBottom(31f);

        Label.LabelStyle progressLabelStyle = new Label.LabelStyle(uiSkin.getFont("default-font"), Color.GRAY);
        Table progressTable = new Table();

        //Adding the progress bar
        progressTable.center().left();
        TextureRegionDrawable progressBar = new TextureRegionDrawable(atlas.findRegion("progress_bar"));
        progressTable.setSize(progressBar.getMinWidth(), progressBar.getMinHeight());
        progressTable.setBackground(progressBar);
        mainTable.add(progressTable);

        progressLabel = new Label("" +(data.questionNumber+1), progressLabelStyle);
        progressLabel.setAlignment(Align.center);
        progressTable.add(progressLabel).center().width(54f).padRight(3f);

        progressCorrect = new TextureRegionDrawable(atlas.findRegion("progress_correct"));
        progressWrong = new TextureRegionDrawable(atlas.findRegion("progress_wrong"));
        progressCheckpoint = new TextureRegionDrawable(atlas.findRegion("progress_checkpoint"));
        progressCheckpointCorrect = new TextureRegionDrawable(atlas.findRegion("progress_checkpoint_correct"));


        progressImages = new Image[numberOfQuestions];
        for(int i= 0; i<numberOfQuestions; i++){

            TextureRegionDrawable correctDrawable;
            if(!isTraining && checkpointValue[data.value][i] != -1){
                if(data.questionNumber <= i)
                    correctDrawable = progressCheckpoint;
                else
                    correctDrawable = progressCheckpointCorrect;
            }
            else {
                if(data.questionNumber <= i)
                    correctDrawable = new TextureRegionDrawable(atlas.findRegion("progress_incomplete"));
                else
                    correctDrawable = progressCorrect;
            }
            progressImages[i] = new Image(correctDrawable);

            //Dividing remaining space by all progress images
            progressTable.add(progressImages[i]).padTop(4f).padRight(-20f / (float) numberOfQuestions)
                    .width((progressTable.getWidth() - 37f) / numberOfQuestions)
                    .height(progressImages[i].getPrefHeight()+2f);
        }

        mainStage.addActor(mainTable);

        final Table exitTable = new Table();
        exitTable.setBackground(new TextureRegionDrawable(atlas.findRegion("exit_panel")));
        exitTable.setBounds((mainStage.getWidth() - exitTable.getBackground().getMinWidth()) / 2,
                (mainStage.getHeight() - exitTable.getBackground().getMinHeight()) / 2,
                exitTable.getBackground().getMinWidth(), exitTable.getBackground().getMinHeight());
        exitTable.center().defaults().padBottom(13f);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(
                new TextureRegionDrawable(Assets.miscellaneous.findRegion("dialog_button_up")),
                new TextureRegionDrawable(Assets.miscellaneous.findRegion("dialog_button_down")),
                null,
                uiSkin.getFont("default-font")
        );
        textButtonStyle.fontColor = textButtonStyle.downFontColor = Color.WHITE;
        Button continueButton = new TextButton("continuar", textButtonStyle);
        continueButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();
                exitTable.setVisible(false);
                setButtonsTouchable(Touchable.enabled);
            }
        });
        Button saveAndQuitButton = new TextButton("guardar e sair", textButtonStyle);
        saveAndQuitButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();
                SavedData.setChallengeSave(data);
                game.loadNextScreen(GameScreen.this, MaiasGame.ScreenType.MAIN_MENU);
            }
        });
        Button reallyExitButton = new TextButton("terminar a partida", textButtonStyle);
        reallyExitButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();
                exitTable.setVisible(false);
                tweenManager.killTarget(timer);
                endGame(true);
            }
        });
        exitTable.add(continueButton).row();
        if(!isTraining)
            exitTable.add(saveAndQuitButton).row();
        exitTable.add(reallyExitButton).row();
        exitTable.setVisible(false);

        String topTableTitle = (isTraining) ? "TREINO": "DESAFIO";
        TopTable topTable = new TopTable(topTableTitle, true, uiSkin,
                new TopTable.BackButtonCallback() {
                    @Override
                    public void onClicked() {
                        exitTable.setVisible(true);
                        setButtonsTouchable(Touchable.disabled);
                    }
                });
        topTable.setPosition((mainStage.getWidth() - topTable.getWidth()) / 2,
                mainStage.getHeight() + mainStage.getPadBottom() - topTable.getHeight());
        backButton = topTable.getBackButton(); moneyLabel = topTable.getMoneyLabel();
        mainStage.addActor(topTable);

        helpButton = new Button(
                new TextureRegionDrawable(atlas.findRegion("help_button_up")),
                new TextureRegionDrawable(atlas.findRegion("help_button_down")),
                new TextureRegionDrawable(atlas.findRegion("help_button_down")));
        helpButton.setPosition(393f, 888f + mainStage.getPadBottom());
        helpButton.setVisible(!isTraining); mainStage.addActor(helpButton);

        final Table helpTable = new Table();
        helpTable.padTop(25f).defaults().padBottom(15f);
        helpTable.setPosition(300f, 594f + mainStage.getPadBottom());
        TextureRegionDrawable helpBackground = new TextureRegionDrawable(atlas.findRegion("help_panel"));
        helpTable.setSize(helpBackground.getMinWidth(), helpBackground.getMinHeight());
        helpTable.setBackground(helpBackground);

        final Button publicHelp = new Button(
                new TextureRegionDrawable(atlas.findRegion("public_up")),
                new TextureRegionDrawable(atlas.findRegion("public_down")));
        helpTable.add(publicHelp).row();
        if(data.help1Used){
            publicHelp.getColor().a = 0.5f;
            publicHelp.setTouchable(Touchable.disabled);
        }
        publicHelp.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();
                data.help1Used = true;
                publicHelp.getColor().a = 0.5f;
                publicHelp.setTouchable(Touchable.disabled);
                helpButton.setChecked(false);
                helpTable.setVisible(false);

                showPublicHelp();
            }
        });

        final Button collectionHelp = new Button(
                new TextureRegionDrawable(atlas.findRegion("collection_up")),
                new TextureRegionDrawable(atlas.findRegion("collection_down")));
        helpTable.add(collectionHelp).row();
        if(data.help2Used){
            collectionHelp.getColor().a = 0.5f;
            collectionHelp.setTouchable(Touchable.disabled);
        }
        collectionHelp.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();
                data.help2Used = true;
                collectionHelp.getColor().a = 0.5f;
                collectionHelp.setTouchable(Touchable.disabled);
                helpButton.setChecked(false);
                helpTable.setVisible(false);

                showCollectionHelp();
            }
        });

        final Button switchQuestionHelp = new Button(
                new TextureRegionDrawable(atlas.findRegion("switch_question_up")),
                new TextureRegionDrawable(atlas.findRegion("switch_question_down")));
        helpTable.add(switchQuestionHelp).row();
        if(data.help3Used){
            switchQuestionHelp.getColor().a = 0.5f;
            switchQuestionHelp.setTouchable(Touchable.disabled);
        }
        switchQuestionHelp.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();
                data.help3Used = true;
                switchQuestionHelp.getColor().a = 0.5f;
                switchQuestionHelp.setTouchable(Touchable.disabled);
                helpButton.setChecked(false);
                helpTable.setVisible(false);

                Question newQuestion = Assets.questionDB.replaceQuestion(data.questions.get(data.questionNumber));
                data.questions.set(data.questionNumber, newQuestion);
                setButtonsTouchable(Touchable.disabled);
                tweenManager.killTarget(timer);
                swapQuestionAnimation();
            }
        });

        final Button fiftyFiftyHelp = new Button(
                new TextureRegionDrawable(atlas.findRegion("50_50_up")),
                new TextureRegionDrawable(atlas.findRegion("50_50_down")));
        helpTable.add(fiftyFiftyHelp).row();
        helpTable.setVisible(false); mainStage.addActor(helpTable);
        if(data.help4Used){
            fiftyFiftyHelp.getColor().a = 0.5f;
            fiftyFiftyHelp.setTouchable(Touchable.disabled);
        }
        fiftyFiftyHelp.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();
                data.help4Used = true;
                fiftyFiftyHelp.getColor().a = 0.5f;
                fiftyFiftyHelp.setTouchable(Touchable.disabled);
                helpButton.setChecked(false);
                helpTable.setVisible(false);

                Array<Integer> indexes = new Array<>(new Integer[]{0, 1, 2, 3});
                for (int i = 0; i < indexes.size; i++) {
                    if (data.questions.get(data.questionNumber).rightChoice
                            .equals(answerLabels[i].getText().toString())) {
                        indexes.removeIndex(i);
                        break;
                    }
                }
                indexes.removeIndex(MathUtils.random(0, 2));
                setButtonsTouchable(Touchable.disabled);
                Timeline.createParallel()
                        .beginSequence()
                        .pushPause(0.5f)
                        .end()
                        .push(Tween.to(answerButtons[indexes.get(0)], ActorAccessor.SCALEXY, 0.5f).target(0f))
                        .push(Tween.to(answerButtons[indexes.get(1)], ActorAccessor.SCALEXY, 0.5f).target(0f))
                        .setCallback(new TweenCallback() {
                            @Override
                            public void onEvent(int type, BaseTween<?> source) {
                                setButtonsTouchable(Touchable.enabled);
                            }
                        }).start(tweenManager);
            }
        });

        helpTable.setTouchable(Touchable.enabled);
        helpTable.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                event.stop();
            }
        });

        helpButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();
                helpTable.setVisible(!helpTable.isVisible());
                event.stop();
            }
        });

        mainStage.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (!event.isStopped() && helpButton.isChecked()) {
                    helpButton.setChecked(false);
                    helpTable.setVisible(false);
                }
            }
        });

        TextureRegionDrawable tableDrawable = new TextureRegionDrawable(atlas.findRegion("info_panel"));
        infoTable = new Table();
        infoTable.center();
        infoTable.setBounds((mainStage.getWidth() - tableDrawable.getMinWidth()) / 2,
                (mainStage.getHeight() - tableDrawable.getMinHeight()) / 2,
                tableDrawable.getMinWidth(), tableDrawable.getMinHeight());
        infoTable.setBackground(tableDrawable);

        mainStage.addActor(exitTable);

        startGame();
    }


    private void startGame(){
        for(Button button:answerButtons) {
            button.setScale(0f);
        }
        setButtonsTouchable(Touchable.disabled);
        questionLabel.getColor().a = 0.0f;

        swapQuestion();
        Timeline.createParallel()
                .push(Tween.to(answerButtons[0], ActorAccessor.SCALEXY, 0.5f).target(1f))
                .push(Tween.to(answerButtons[1], ActorAccessor.SCALEXY, 0.5f).target(1f))
                .push(Tween.to(answerButtons[2], ActorAccessor.SCALEXY, 0.5f).target(1f))
                .push(Tween.to(answerButtons[3], ActorAccessor.SCALEXY, 0.5f).target(1f))
                .push(Tween.to(questionLabel, ActorAccessor.ALPHA, 0.5f).target(1f))
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        setButtonsTouchable(Touchable.enabled);
                        if(!isTraining)
                            startTimer(maxTimerTime);
                    }
                }).start(tweenManager);
    }

    private void startTimer(int value){
        Timeline.createSequence()
                .push(Tween.to(timer, LabelAccessor.NUMBER_CHANGE, value).target(1f).ease(Linear.INOUT))
                .push(Tween.set(timer, LabelAccessor.NUMBER_CHANGE).target(0f))
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        onAnswer("");
                    }
                }).start(tweenManager);
    }

    private void onAnswer(String text){

        setButtonsTouchable(Touchable.disabled);
        tweenManager.killTarget(timer);

        final AtomicBoolean isCorrect = new AtomicBoolean(false);
        final Question currentQuestion = data.questions.get(data.questionNumber);
        if(currentQuestion.rightChoice.equals(text)){
            isCorrect.set(true);
        }

        Button auxButton=backButton;
        for(int i=0; i<answerLabels.length; i++) {
            if(answerLabels[i].getText().toString().equals(currentQuestion.rightChoice)){
                auxButton = answerButtons[i];
                break;
            }
        }
        final Button correctButton = auxButton;
        Timeline.createSequence()
                .pushPause(0.5f)
                .push(Tween.mark().setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        if(isCorrect.get())
                            Assets.successFX.play();
                        else
                            Assets.failFX.play();
                    }
                }))
                .push(
                        Timeline.createSequence()
                                .push(Tween.mark()
                                        .setCallback(new TweenCallback() {
                                            @Override
                                            public void onEvent(int type, BaseTween<?> source) {
                                                correctButton.setStyle(buttonNormalStyle);
                                            }
                                        }))
                                .pushPause(0.125f)
                                .push(Tween.mark()
                                        .setCallback(new TweenCallback() {
                                            @Override
                                            public void onEvent(int type, BaseTween<?> source) {
                                                correctButton.setStyle(buttonCorrectStyle);
                                            }
                                        }))
                                .repeatYoyo(4, 0f)
                )
                .push(Tween.mark().setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        if (isCorrect.get()) {
                            if (progressImages[data.questionNumber].getDrawable().equals(progressCheckpoint))
                                progressImages[data.questionNumber].setDrawable(progressCheckpointCorrect);
                            else
                                progressImages[data.questionNumber].setDrawable(progressCorrect);
                        } else {
                            progressImages[data.questionNumber].setDrawable(progressWrong);
                        }
                    }
                }))
                .pushPause(0.75f)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        Assets.successFX.stop(); //too big if playing

                        String text = (isCorrect.get())? currentQuestion.commentaryCorrect : currentQuestion.commentaryWrong;
                        createComment(text)
                                .addListener(new ClickListener() {
                                    @Override
                                    public void clicked(InputEvent event, float x, float y) {
                                        infoTable.remove();

                                        data.questionNumber++;

                                        if (!isCorrect.get()) {
                                            wrongQuestions++;
                                            if (!isTraining || wrongQuestions == 3) {
                                                endGame(false);
                                                return;
                                            }
                                        }

                                        swapQuestionAnimation();
                                    }
                                });
                    }
                }).start(tweenManager);
    }

    public Button createComment(String comment){
        infoTable.clear();

        Label.LabelStyle labelStyle = new Label.LabelStyle(uiSkin.getFont("arial"), Color.WHITE);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(
                new TextureRegionDrawable(Assets.miscellaneous.findRegion("dialog_button_up")),
                new TextureRegionDrawable(Assets.miscellaneous.findRegion("dialog_button_down")),
                null,
                uiSkin.getFont("default-font")
        );
        textButtonStyle.fontColor = textButtonStyle.downFontColor = Color.WHITE;

        Label commentaryLabel = new Label(comment, labelStyle); commentaryLabel.setAlignment(Align.center);
        commentaryLabel.setFontScale(scaleLabel(comment, 120)); commentaryLabel.setWrap(true);
        infoTable.add(commentaryLabel).center().padTop(50f).width(360f).padBottom(28f).expandY().row();

        TextButton button = new TextButton("continuar", textButtonStyle);
        infoTable.add(button).padBottom(52f);
        mainStage.addActor(infoTable);

        return button;

    }

    private void swapQuestionAnimation(){
        Timeline.createSequence()
                .beginParallel()
                .push(Tween.to(answerButtons[0], ActorAccessor.SCALEXY, 0.5f).target(0f))
                .push(Tween.to(answerButtons[1], ActorAccessor.SCALEXY, 0.5f).target(0f))
                .push(Tween.to(answerButtons[2], ActorAccessor.SCALEXY, 0.5f).target(0f))
                .push(Tween.to(answerButtons[3], ActorAccessor.SCALEXY, 0.5f).target(0f))
                .push(Tween.to(questionLabel, ActorAccessor.ALPHA, 0.5f).target(0f))
                .end()
                .push(Tween.mark().setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        swapQuestion();
                    }
                }))
                .beginParallel()
                .push(Tween.to(answerButtons[0], ActorAccessor.SCALEXY, 0.5f).target(1f))
                .push(Tween.to(answerButtons[1], ActorAccessor.SCALEXY, 0.5f).target(1f))
                .push(Tween.to(answerButtons[2], ActorAccessor.SCALEXY, 0.5f).target(1f))
                .push(Tween.to(answerButtons[3], ActorAccessor.SCALEXY, 0.5f).target(1f))
                .push(Tween.to(questionLabel, ActorAccessor.ALPHA, 0.5f).target(1f))
                .end()
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        setButtonsTouchable(Touchable.enabled);
                        if(!isTraining)
                            startTimer(maxTimerTime);
                    }
                }).start(tweenManager);
    }

    private void swapQuestion(){
        if(data.questionNumber == numberOfQuestions) {
            unlockedNewLevel = SavedData.setChallengeProgress(data.value+1);
            tweenManager.killAll();
            endGame(false);
            return;
        }

        for(Button button: answerButtons){
            button.setChecked(false);
            button.setStyle(buttonNormalStyle);
        }


        timer.setColor(Color.BLACK);
        timer.setText("" + maxTimerTime);
        progressLabel.setText("" + (data.questionNumber + 1));
        Question currentQuestion = data.questions.get(data.questionNumber);
        questionLabel.setText(currentQuestion.question);

        //mix the answer position by switching indexes in place
        int indexArray[] = {0, 1, 2, 3};
        for(int i=0; i<indexArray.length-1; i++){
            int switchIndex = MathUtils.random(i, indexArray.length-1);
            int auxValue = indexArray[switchIndex];
            indexArray[switchIndex] = indexArray[i];
            indexArray[i] = auxValue;
        }

        for(int i=0; i<currentQuestion.wrongChoices.size; i++){
            answerLabels[indexArray[i]].setText(currentQuestion.wrongChoices.get(i));
        }
        answerLabels[indexArray[3]].setText(currentQuestion.rightChoice);
        scaleEverything();

        for(Button button: answerButtons){
            button.setChecked(false);
            button.setStyle(buttonNormalStyle);
            button.layout();
            button.setOrigin(button.getWidth()/2, button.getHeight()/2);
        }

    }

    private void showPublicHelp(){
        setButtonsTouchable(Touchable.disabled);

        //Calculating the percentages
        Question currentQuestion = data.questions.get(data.questionNumber);
        int correctAnswerPosition = -1;
        for(int i=0; i<answerLabels.length; i++){
            if(answerLabels[i].getText().toString().equals(currentQuestion.rightChoice)){
                correctAnswerPosition = i;
            }
        }
        int correctAnswerPercentage = (int)MathUtils.random(87.5f-12.5f*currentQuestion.difficulty,
                112.5f-12.5f*currentQuestion.difficulty);
        int [] wrongAnswerPercentages = new int[3];

        int percentageLeft = 100-correctAnswerPercentage;
        for (int i=0; i<2; i++){
            wrongAnswerPercentages[i] = MathUtils.random(0, percentageLeft);
            percentageLeft -= wrongAnswerPercentages[i];
        }
        wrongAnswerPercentages[wrongAnswerPercentages.length-1] = percentageLeft;

        Array<Integer> wrongAnswerPositions = new Array<>(new Integer[]{0, 1, 2, 3});
        wrongAnswerPositions.removeValue(correctAnswerPosition, false);
        wrongAnswerPositions.shuffle();

        int finalPercentages [] = new int[4];
        for(int i=0; i<wrongAnswerPositions.size; i++){
            finalPercentages[wrongAnswerPositions.get(i)] = wrongAnswerPercentages[i];
        }
        finalPercentages[correctAnswerPosition] = correctAnswerPercentage;


        //Creating the table
        final Table publicHelpTable = new Table();
        publicHelpTable.setBackground(new TextureRegionDrawable(atlas.findRegion("public_help_panel")));
        publicHelpTable.setBounds((mainStage.getWidth() - publicHelpTable.getBackground().getMinWidth()) / 2,
                (mainStage.getHeight() - publicHelpTable.getBackground().getMinHeight()) / 2,
                publicHelpTable.getBackground().getMinWidth(), publicHelpTable.getBackground().getMinHeight());
        publicHelpTable.top().left().padTop(167f).padLeft(167f).defaults().padBottom(43f);

        Label.LabelStyle percentageStyle = new Label.LabelStyle(uiSkin.getFont("default-font"), Assets.prettyRed);

        Timeline publicSequence = Timeline.createParallel().beginSequence().pushPause(0.25f).end();
        for(int percentage : finalPercentages){
            Table innerTable = new Table();

            Image bar = new Image(atlas.findRegion("public_help_bar"));
            Cell<?> barCell = innerTable.add(bar).width(0f);
            publicSequence.push(Tween.to(barCell, CellAccessor.WIDTH, 0.5f)
                    .target(((float) percentage / 100f) * bar.getPrefWidth()));

            Label percentageLabel1 = new Label("" +0, percentageStyle);
            percentageLabel1.setFontScale(0.5f);
            Label percentageLabel2 = new Label("%", percentageStyle);
            percentageLabel2.setFontScale(0.5f);

            innerTable.add(percentageLabel1).padLeft(11f);
            innerTable.add(percentageLabel2);
            publicSequence.push(Tween.to(percentageLabel1, LabelAccessor.NUMBER_CHANGE, 0.5f).target(percentage));

            publicHelpTable.add(innerTable).left().row();
        }

        Button exitButton = new Button(
                new TextureRegionDrawable(atlas.findRegion("public_help_close_up")),
                new TextureRegionDrawable(atlas.findRegion("public_help_close_down")));
        exitButton.setPosition(354f, 398f);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                publicHelpTable.remove();
                setButtonsTouchable(Touchable.enabled);
            }
        });
        publicHelpTable.addActor(exitButton);

        mainStage.addActor(publicHelpTable);
        publicSequence.start(tweenManager);
    }

    public void showCollectionHelp(){
        setButtonsTouchable(Touchable.disabled);
        tweenManager.killTarget(timer);

        final Table collectionTable = new Table();
        collectionTable.setSize(mainStage.getWidth(), mainStage.getHeight());
        collectionTable.setX(-mainStage.getWidth() - mainStage.getPadLeft());

        Assets.collection.resetPage(null);
        collectionTable.add(Assets.collection);

        final Button exitButton = new Button(
                new TextureRegionDrawable(atlas.findRegion("public_help_close_up")),
                new TextureRegionDrawable(atlas.findRegion("public_help_close_down")));
        exitButton.setPosition(552f, 894f);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                exitButton.setTouchable(Touchable.disabled);
                Tween.to(collectionTable, ActorAccessor.MOVE_X, 0.5f)
                        .target(-mainStage.getWidth() - mainStage.getPadLeft())
                        .setCallback(new TweenCallback() {
                            @Override
                            public void onEvent(int type, BaseTween<?> source) {
                                exitButton.remove();
                                collectionTable.remove();
                                setButtonsTouchable(Touchable.enabled);
                                startTimer(Integer.parseInt(timer.getText().toString()));
                            }
                        }).start(tweenManager);
            }
        });
        exitButton.setTouchable(Touchable.disabled);
        Assets.collection.addActor(exitButton);

        mainStage.addActor(collectionTable);
        Tween.to(collectionTable, ActorAccessor.MOVE_X, 0.5f).target(0f)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        exitButton.setTouchable(Touchable.enabled);
                    }
                }).start(tweenManager);

    }

    private void endGame(boolean gaveUp){
        if(isTraining)
            endTrainingGame();
        else
            endChallengeGame(gaveUp);
    }

    private void endTrainingGame(){
        int moneyReceived = 0;
        int questionsDoneInDifficulty[] = {0, 0, 0, 0};
        int questionsCorrectInDifficulty[] = {0, 0, 0, 0};
        int sumCorrect = 0, sumDone = 0;
        for(int i=0; i<data.questionNumber; i++){
            int questionDifficulty = data.questions.get(i).difficulty;
            questionsDoneInDifficulty[questionDifficulty-1]++;
            sumDone++;
            if(progressImages[i].getDrawable().equals(progressCorrect)){
                moneyReceived += questionDifficulty*10;
                questionsCorrectInDifficulty[questionDifficulty-1]++;
                sumCorrect++;
            }
        }

        //Calculating new stats
        final StatsData statsToSave = SavedData.getStats();
        switch (data.value){
            case 0:
                statsToSave.theme1Score =
                        calculateNewStat(statsToSave.theme1Score, statsToSave.theme1Counter,
                                sumCorrect/(float)sumDone, sumDone);
                statsToSave.theme1Counter += sumDone;
                break;
            case 1:
                statsToSave.theme2Score =
                        calculateNewStat(statsToSave.theme2Score, statsToSave.theme2Counter,
                                sumCorrect/(float)sumDone, sumDone);
                statsToSave.theme2Counter += sumDone;
                break;
            case 2:
                statsToSave.theme3Score =
                        calculateNewStat(statsToSave.theme3Score, statsToSave.theme3Counter,
                                sumCorrect/(float)sumDone, sumDone);
                statsToSave.theme3Counter += sumDone;
                break;
            case 3:
                statsToSave.theme4Score =
                        calculateNewStat(statsToSave.theme4Score, statsToSave.theme4Counter,
                                sumCorrect/(float)sumDone, sumDone);
                statsToSave.theme4Counter += sumDone;
                break;
            case 4:
                statsToSave.theme5Score =
                        calculateNewStat(statsToSave.theme5Score, statsToSave.theme5Counter,
                                sumCorrect/(float)sumDone, sumDone);
                statsToSave.theme5Counter += sumDone;
                break;
            case 5:
                statsToSave.theme6Score =
                        calculateNewStat(statsToSave.theme6Score, statsToSave.theme6Counter,
                                sumCorrect/(float)sumDone, sumDone);
                statsToSave.theme6Counter += sumDone;
                break;
            case 6:
                statsToSave.theme7Score =
                        calculateNewStat(statsToSave.theme7Score, statsToSave.theme7Counter,
                                sumCorrect/(float)sumDone, sumDone);
                statsToSave.theme7Counter += sumDone;
                break;
        }

        statsToSave.difficulty1Score =
                calculateNewStat(statsToSave.difficulty1Score, statsToSave.difficulty1Counter,
                        questionsCorrectInDifficulty[0]/(float)questionsDoneInDifficulty[0],
                        questionsDoneInDifficulty[0]);
        statsToSave.difficulty1Counter += questionsDoneInDifficulty[0];

        statsToSave.difficulty2Score =
                calculateNewStat(statsToSave.difficulty2Score, statsToSave.difficulty2Counter,
                        questionsCorrectInDifficulty[1]/(float)questionsDoneInDifficulty[1],
                        questionsDoneInDifficulty[1]);
        statsToSave.difficulty2Counter += questionsDoneInDifficulty[1];

        statsToSave.difficulty3Score =
                calculateNewStat(statsToSave.difficulty3Score, statsToSave.difficulty3Counter,
                        questionsCorrectInDifficulty[2]/(float)questionsDoneInDifficulty[2],
                        questionsDoneInDifficulty[2]);
        statsToSave.difficulty3Counter += questionsDoneInDifficulty[2];

        statsToSave.difficulty4Score =
                calculateNewStat(statsToSave.difficulty4Score, statsToSave.difficulty4Counter,
                        questionsCorrectInDifficulty[3]/(float)questionsDoneInDifficulty[3],
                        questionsDoneInDifficulty[3]);
        statsToSave.difficulty4Counter += questionsDoneInDifficulty[3];
        statsToSave.money += moneyReceived;
        SavedData.setStats(statsToSave);

        //Showing the table
        infoTable.clear();
        Label.LabelStyle labelStyle = new Label.LabelStyle(uiSkin.getFont("default-font"), Color.WHITE);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(
                new TextureRegionDrawable(Assets.miscellaneous.findRegion("dialog_button_up")),
                new TextureRegionDrawable(Assets.miscellaneous.findRegion("dialog_button_down")),
                null,
                uiSkin.getFont("default-font")
        );
        textButtonStyle.fontColor = textButtonStyle.downFontColor = Color.WHITE;

        final Label moneyValue = new Label("" +moneyReceived, labelStyle);
        moneyValue.setFontScale(0.8f);
        infoTable.add(moneyValue).right().padBottom(34f).padRight(10f).right();
        infoTable.add(new Image(Assets.miscellaneous.findRegion("money_icon")))
                .padBottom(34f).padLeft(10f).left().row();

        for(int i=0; i<questionsDoneInDifficulty.length; i++){
            Label label = new Label("dificuldade " +(i+1) +"   |   " +questionsCorrectInDifficulty[i]
                    +"/" +questionsDoneInDifficulty[i], labelStyle);
            label.setFontScale(0.8f); label.setAlignment(Align.left);
            infoTable.add(label).width(320f).colspan(2).row();
        }
        final TextButton button = new TextButton("continuar", textButtonStyle);
        button.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                button.removeListener(this);
                tweenManager.killTarget(moneyLabel);
                tweenManager.killTarget(moneyValue);
                Assets.moneyFX.stop();

                UpdateStatsDialog dialog = new UpdateStatsDialog(GameScreen.this, mainStage,
                        new UpdateStatsDialog.ButtonType[]{UpdateStatsDialog.ButtonType.CONTINUE},
                        new UpdateStatsDialog.ButtonType[]{UpdateStatsDialog.ButtonType.CONTINUE},
                        new UpdateStatsDialog.ButtonType[]{UpdateStatsDialog.ButtonType.RETRY,
                                UpdateStatsDialog.ButtonType.CONTINUE},
                        new UpdateStatsDialog.UpdateStatsDialogCallback() {
                            @Override
                            public void onContinue() {
                                game.loadNextScreen(GameScreen.this, MaiasGame.ScreenType.TRAINING_MENU_STATS);
                            }

                            @Override
                            public void onGoBack() {
                                //there is no going back
                            }
                        });
                dialog.updateStats();
            }
        });
        infoTable.add(button).padTop(35f).colspan(2);
        mainStage.addActor(infoTable);

        if(moneyReceived != 0) {
            Timeline.createSequence()
                    .pushPause(1f)
                    .push(Tween.mark().setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            Assets.moneyFX.play();
                        }
                    }))
                    .beginParallel()
                    .push(Tween.to(moneyValue, LabelAccessor.NUMBER_CHANGE, 5f).target(0f).ease(Cubic.OUT))
                    .push(Tween.to(moneyLabel, LabelAccessor.NUMBER_CHANGE, 5f).target(statsToSave.money).ease(Cubic.OUT))
                    .end()
                    .start(tweenManager);
        }
    }

    private void endChallengeGame(boolean gaveUp){

        int lastCheckpoint=-1, pointsPerAnswer = 0, counter = data.questionNumber-1;

        if(data.questionNumber != 0 && progressImages[data.questionNumber-1].getDrawable().equals(progressWrong))
            counter--;

        for (int i = counter; i >= 0; i--) {
            if (checkpointValue[data.value][i] != -1) {
                lastCheckpoint = i;
                break;
            }
        }

        switch (data.value){
            case 0:
                pointsPerAnswer = 20;
                break;
            case 1:
                pointsPerAnswer = 40;
                break;
            case 2:
                pointsPerAnswer = 80;
                break;
        }

        int moneyReceived;
        if(!gaveUp)
            moneyReceived = (lastCheckpoint+1)*pointsPerAnswer;
        else{
            moneyReceived = (counter+1)*pointsPerAnswer;
        }

        //Getting the new stickers
        int [] stickersReceived;
        if(lastCheckpoint == -1)
            stickersReceived = new int[0];
        else
            stickersReceived = new int[checkpointValue[data.value][lastCheckpoint]];

        Array<Integer> stickerPool = new Array<>();
        for(int i=0; i<Assets.numberOfStickers; i++){
            stickerPool.add(i);
        }

        int rareStickers [] = {20, 22, 23, 24};
        int uncommonStickers [] = {45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60};
        //removing random stickers from pool
        for(int i=0; i<stickersReceived.length; i++){
            int randomIndex = MathUtils.random(0, stickerPool.size-1);

            int randomSticker = stickerPool.get(randomIndex);
            boolean foundRare = false, reRoll = false;
            //check if rare and get change of re-roll
            for(int rareSticker : rareStickers){
                if(rareSticker == randomSticker){
                    foundRare = true;
                    if(MathUtils.random(1f) < 0.65f) { //65% probability of re-roll
                        reRoll = true;
                    }
                    break;
                }
            }
            //check if uncommon and get change of re-roll
            if(!foundRare) {
                for (int uncommonSticker : uncommonStickers) {
                    if (uncommonSticker == randomSticker) {
                        if (MathUtils.random(1f) < 0.35f) { //35% probability of re-roll
                            reRoll = true;
                        }
                        break;
                    }
                }
            }

            if(reRoll){
                i--;
                continue;
            }

            stickersReceived[i] = stickerPool.get(randomIndex);
            stickerPool.removeIndex(randomIndex);
        }

        //Calculating new stats
        final StatsData statsToSave = SavedData.getStats();
        statsToSave.money += moneyReceived;
        statsToSave.challengeScore += moneyReceived;
        for(int stickerNumber : stickersReceived){
            statsToSave.collectionData[stickerNumber]++;
        }
        SavedData.setStats(statsToSave);

        //Showing the table
        infoTable.clear();
        Label.LabelStyle labelStyle = new Label.LabelStyle(uiSkin.getFont("default-font"), Color.WHITE);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(
                new TextureRegionDrawable(Assets.miscellaneous.findRegion("dialog_button_up")),
                new TextureRegionDrawable(Assets.miscellaneous.findRegion("dialog_button_down")),
                null,
                uiSkin.getFont("default-font")
        );
        textButtonStyle.fontColor = textButtonStyle.downFontColor = Color.WHITE;

        final Label moneyValue = new Label("" +moneyReceived, labelStyle);
        moneyValue.setFontScale(0.8f);
        infoTable.add(moneyValue).right().padBottom(10f).padRight(10f).right();
        infoTable.add(new Image(Assets.miscellaneous.findRegion("money_icon")))
                .padBottom(10f).padLeft(10f).left().row();

        Table stickerTable = new Table();
        stickerTable.setSize(385f, 280f);

        int numberOfColumns;
        TextureAtlas stickerAtlas = Assets.prepareStickers();
        TextureRegionDrawable stickerExample = new TextureRegionDrawable(stickerAtlas.findRegion("1"));
        switch (stickersReceived.length){
            case 1:
                numberOfColumns = 1;
                stickerTable.defaults()
                        .size(stickerExample.getMinWidth()*0.53f, stickerExample.getMinHeight() * 0.53f);
                break;
            case 3:
                numberOfColumns = 3;
                stickerTable.defaults()
                        .size(stickerExample.getMinWidth()*0.31f, stickerExample.getMinHeight() * 0.31f);
                break;
            case 5:
                numberOfColumns = 3;
                stickerTable.defaults()
                        .size(stickerExample.getMinWidth()*0.27f, stickerExample.getMinHeight()*0.27f);
                break;
            case 8:
                numberOfColumns = 4;
                stickerTable.defaults()
                        .size(stickerExample.getMinWidth()*0.23f, stickerExample.getMinHeight()*0.23f);
                break;
            case 10:
                numberOfColumns = 5;
                stickerTable.defaults()
                        .size(stickerExample.getMinWidth()*0.18f, stickerExample.getMinHeight()*0.18f);
                break;
            case 15:
                numberOfColumns = 5;
                stickerTable.defaults()
                        .size(stickerExample.getMinWidth()*0.175f, stickerExample.getMinHeight()*0.175f);
                break;
            default:
                numberOfColumns = 0;
        }

        for(int i=0; i<stickersReceived.length; i++){
            Image sticker = new Image(stickerAtlas.findRegion("" +(stickersReceived[i]+1)));

            //padLeft when stickers Received = 5
            if(stickersReceived.length == 5 && i==3){
                Table otherStickerTable = new Table();
                otherStickerTable.defaults()
                        .size(stickerTable.defaults().getPrefWidth(), stickerTable.defaults().getPrefHeight());

                //Adding last two stickers
                otherStickerTable.add(sticker);
                i++;
                otherStickerTable.add(new Image(stickerAtlas.findRegion("" +(stickersReceived[i]+1))));

                stickerTable.add(otherStickerTable).colspan(numberOfColumns);
            }
            else if(i%(numberOfColumns) == numberOfColumns-1){
                stickerTable.add(sticker).row();
            }
            else
                stickerTable.add(sticker);
        }
        infoTable.add(stickerTable).size(385f, 280f).colspan(2).row();

        final UpdateStatsDialog dialog = new UpdateStatsDialog(GameScreen.this, mainStage,
                new UpdateStatsDialog.ButtonType[]{UpdateStatsDialog.ButtonType.CONTINUE},
                new UpdateStatsDialog.ButtonType[]{UpdateStatsDialog.ButtonType.CONTINUE},
                new UpdateStatsDialog.ButtonType[]{UpdateStatsDialog.ButtonType.RETRY,
                        UpdateStatsDialog.ButtonType.CONTINUE},
                new UpdateStatsDialog.UpdateStatsDialogCallback() {
                    @Override
                    public void onContinue() {
                        game.loadNextScreen(GameScreen.this, MaiasGame.ScreenType.CHALLENGE_MENU);
                    }

                    @Override
                    public void onGoBack() {
                        //there is no going back
                    }
                });

        final TextButton button = new TextButton("continuar", textButtonStyle);
        button.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                button.removeListener(this);
                tweenManager.killTarget(moneyLabel);
                tweenManager.killTarget(moneyValue);
                Assets.moneyFX.stop();
                infoTable.remove();

                if(unlockedNewLevel && data.value != 2){
                    Assets.fanfareFX.play();
                    Button out = createComment("Parabéns!\n\nDesbloqueaste a dificuldade " +levelNames[data.value]);
                    out.addListener(new ClickListener(){
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            Assets.fanfareFX.stop();
                            dialog.updateStats();
                        }
                    });
                }
                else {
                    dialog.updateStats();
                }
            }
        });
        infoTable.add(button).padTop(15f).colspan(2);
        mainStage.addActor(infoTable);

        if(moneyReceived != 0) {
            Timeline.createSequence()
                    .pushPause(1f)
                    .push(Tween.mark().setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            Assets.moneyFX.play();
                        }
                    }))
                    .beginParallel()
                    .push(Tween.to(moneyValue, LabelAccessor.NUMBER_CHANGE, 5f).target(0f).ease(Cubic.OUT))
                    .push(Tween.to(moneyLabel, LabelAccessor.NUMBER_CHANGE, 5f).target(statsToSave.money).ease(Cubic.OUT))
                    .end()
                    .start(tweenManager);
        }
    }

    private float calculateNewStat(float oldStat, long oldCounter, float newValuesStat,  int newValuesCounter){
        if(newValuesCounter == 0){
            if(oldCounter == 0)
                return 0f;
            return oldStat;
        }
        if(oldCounter == 0)
            return newValuesStat;

        float oldStatImportance = (float)(oldCounter/(double)(oldCounter+newValuesCounter));
        float newValuesImportance = (float)(newValuesCounter/(double)(oldCounter+newValuesCounter));

        return oldStatImportance * oldStat + newValuesImportance* newValuesStat;
    }

    private void setButtonsTouchable(Touchable touchable){
        for(Button button:answerButtons){
            button.setTouchable(touchable);
        }
        backButton.setTouchable(touchable);
        helpButton.setTouchable(touchable);
    }

    private void scaleEverything(){
        float minScale = 0.8f;
        for(Label label : answerLabels){
            float newScale = scaleLabel(label.getText().toString(), 30);
            if(newScale < minScale)
                minScale = newScale;
        }
        for(Label label : answerLabels)
            label.setFontScale(minScale);

        questionLabel.setFontScale(scaleLabel(questionLabel.getText().toString(), 100));
    }

    private float scaleLabel(String string, int maxSize){
        float shrink = 0f;
        if(string.length() > maxSize){
            shrink = (string.length()-maxSize)/(float)maxSize;
        }
        shrink = shrink/2;

        if(shrink > 1f)
            shrink = 1f;

        return 0.8f - 0.3f * shrink;
    }


    public static class ChallengeSave { //JSON Requires the static class!
        public int value;
        public int questionNumber;
        public Array<Question> questions;
        public boolean help1Used, help2Used, help3Used, help4Used;

        public ChallengeSave(int value, Array<Question> questions){
            this.value = value;
            questionNumber = 0;
            this.questions = questions;
            help1Used = false; help2Used = false; help3Used = false;help4Used = false;
        }

        @SuppressWarnings("unused") //JSON Requires it!
        public ChallengeSave() {
        }
    }
}
