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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mobilelearning.maias.Assets;
import com.mobilelearning.maias.MaiasGame;
import com.mobilelearning.maias.SavedData;
import com.mobilelearning.maias.accessors.LabelAccessor;
import com.mobilelearning.maias.serviceHandling.json.StatsData;
import com.mobilelearning.maias.uiUtils.TopTable;
import com.mobilelearning.maias.uiUtils.UpdateStatsDialog;

import java.util.concurrent.atomic.AtomicInteger;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;

/**
 * Created by AFFonseca on 03/08/2015.
 */
public class ChallengeMenu extends StandardScreen{
    private static final String [] infoTexts = {
            "Tens 60 segundos para responder a cada uma das 10 questões que te serão apresentadas. " +
                    "Por cada resposta correta receberás 20 pontos para a tua classificação no leaderboard. " +
                    "Se perderes apenas recebes a pontuação relativa ao último patamar desbloqueado. " +
                    "No entanto se parares recebes a pontuação da última questão.\n\nEstás preparado/a? Bom jogo!",
            "Já sabes como funciona, no entanto aqui passas a ter 15 questões e apenas 45 segundos para responder. No " +
                    "entanto cada questão correta vale 40 pontos para melhorar a tua prestação no leaderboard. " +
                    "\n\nEu sei que consegues! Bom jogo!",
            "Como já és um especialista apenas tens que saber que te esperam 25 questões e por cada questão que" +
                    " acertares terás 80 pontos para acrescentar aos tens no leaderboard!\n\nForça!"
    };
    private static final String specialMessage = "Sem dinheiro suficiente?\n\n" +
            "Podes usar o modo de treino para recuperar algum dinheiro.";
    private static final String reloadLevelMessage = "Tens um jogo gravado por terminar.\n\nQueres continuar?\n\n" +
            "Atenção que se começares um novo jogo e gravares perdes o progresso atual.";

    private static final int [] entryCost = {100, 300, 1000};

    private TextureAtlas atlas;
    private ImageButton [] challengeButtons;
    private TopTable topTable;
    private Table mainTable, infoTable;
    //private Label infoLabel, moneyLabel, valuesLabel;
    //private Button infoButtonContinue, infoButtonBack;
    private final MaiasGame.ScreenType [] challengeScreens = {
            MaiasGame.ScreenType.CHALLENGE_1,
            MaiasGame.ScreenType.CHALLENGE_2,
            MaiasGame.ScreenType.CHALLENGE_3
    };

    public ChallengeMenu(MaiasGame game, SpriteBatch batch) {
        super(game, batch);
    }

    @Override
    public void load() {

    }

    @Override
    public void prepare() {
        super.prepare();

        atlas = Assets.prepareChallengeMenu();

        challengeButtons = new ImageButton[3];
        for (int i=0; i< challengeButtons.length; i++){
            challengeButtons[i] = new ImageButton(
                    new TextureRegionDrawable(atlas.findRegion("difficulty" +(i+1) +"_up")),
                    new TextureRegionDrawable(atlas.findRegion("difficulty" +(i+1) +"_down"))
            );

            final AtomicInteger aux = new AtomicInteger(i);
            challengeButtons[i].addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    Assets.clickFX.play();
                    createInfoTable(aux.get());
                }
            }) ;
        }
    }

    @Override
    public void unload() {
    }

    @Override
    public void show() {
        Image background = new Image(atlas.findRegion("background"));
        backgroundStage.addActor(background);

        //Adding the elements
        mainTable = new Table();
        mainTable.setSize(mainStage.getWidth(), mainStage.getHeight());
        mainTable.center().top().padTop(290f).defaults().padBottom(33f);

        int currentChallengeProgress = SavedData.getChallengeProgress();
        for(int i=0; i<challengeButtons.length; i++){
            mainTable.add(challengeButtons[i]).row();
            if(i>currentChallengeProgress){
                challengeButtons[i].setTouchable(Touchable.disabled);
                challengeButtons[i].getColor().a = 0.5f;
            }

        }

        mainStage.addActor(mainTable);

        //Adding the top bar
        topTable = new TopTable("DESAFIO", true, uiSkin,
                new TopTable.BackButtonCallback() {
                    @Override
                    public void onClicked() {
                        game.loadNextScreen(ChallengeMenu.this, MaiasGame.ScreenType.MAIN_MENU);
                    }
                });
        topTable.setPosition((mainStage.getWidth() - topTable.getWidth()) / 2,
                mainStage.getHeight() + mainStage.getPadBottom() - topTable.getHeight());
        mainStage.addActor(topTable);

        mainStage.addActor(topTable);

        infoTable = new Table();
        infoTable.setBackground(new TextureRegionDrawable(atlas.findRegion("info_panel")));
        infoTable.setBounds(
                (mainStage.getWidth() - infoTable.getBackground().getMinWidth()) / 2,
                (mainStage.getHeight() - infoTable.getBackground().getMinHeight()) / 2,
                infoTable.getBackground().getMinWidth(), infoTable.getBackground().getMinHeight());
        infoTable.center().padTop(70f).padBottom(64f);

        GameScreen.ChallengeSave save = SavedData.getChallengeSave();
        if(save != null){
            createInfoTable(save.value);
        }
    }

    private void createInfoTable(final int value){
        boolean isLoadingGame = false;
        GameScreen.ChallengeSave save = SavedData.getChallengeSave();
        if(save != null && save.value == value){
            isLoadingGame = true;
        }
        final boolean loadedGame = isLoadingGame;

        mainTable.setTouchable(Touchable.disabled);
        topTable.setTouchable(Touchable.disabled);

        infoTable.clear();

        Label.LabelStyle labelStyle = new Label.LabelStyle(uiSkin.getFont("default-font"), Color.WHITE);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(
                new TextureRegionDrawable(Assets.miscellaneous.findRegion("dialog_button_up")),
                new TextureRegionDrawable(Assets.miscellaneous.findRegion("dialog_button_down")),
                null,
                uiSkin.getFont("default-font")
        );
        textButtonStyle.fontColor = textButtonStyle.downFontColor = Color.WHITE;

        String text = (loadedGame) ? reloadLevelMessage : infoTexts[value];
        final Label messageLabel = new Label(text, labelStyle);
        if(value == 0 && !loadedGame){
            messageLabel.setFontScale(0.7f);
        }
        else
            messageLabel.setFontScale(0.8f);
        messageLabel.setAlignment(Align.center); messageLabel.setWrap(true);
        infoTable.add(messageLabel).width(446f).padBottom(24f).expandY().row();

        Table costTable = new Table();
        costTable.add(new Label("custo de entrada: ", labelStyle));
        final Label moneyLabel = new Label("" +entryCost[value], labelStyle);
        moneyLabel.setAlignment(Align.right);
        costTable.add(moneyLabel).width(100f).padRight(10f);
        costTable.add(new Image(Assets.miscellaneous.findRegion("money_icon"))).padLeft(10f);
        infoTable.add(costTable).padBottom(24f).expandY().row();

        final StatsData statsData = SavedData.getStats();
        final TextButton continueButton = new TextButton("continuar", textButtonStyle);

        final TextButton backButton = new TextButton("voltar", textButtonStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();
                infoTable.remove();
                mainTable.setTouchable(Touchable.childrenOnly);
                topTable.setTouchable(Touchable.childrenOnly);
            }
        });

        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();

                if(loadedGame){
                    game.loadNextScreen(ChallengeMenu.this, challengeScreens[value]);
                }
                else if(statsData.money < entryCost[value]){
                    messageLabel.setText(specialMessage);
                    messageLabel.setFontScale(0.8f);
                    continueButton.getColor().a = 0.25f;
                    continueButton.setTouchable(Touchable.disabled);
                }
                else {
                    continueButton.setTouchable(Touchable.disabled);
                    backButton.setTouchable(Touchable.disabled);

                    statsData.money -= entryCost[value];
                    SavedData.setStats(statsData);

                    Assets.moneyFX.play();
                    Timeline.createParallel()
                            .push(Tween.to(topTable.getMoneyLabel(), LabelAccessor.NUMBER_CHANGE, 5f)
                                    .target(statsData.money).ease(Cubic.OUT))
                            .push(Tween.to(moneyLabel, LabelAccessor.NUMBER_CHANGE, 5f).target(0f).ease(Cubic.OUT))
                            .setCallback(new TweenCallback() {
                                @Override
                                public void onEvent(int type, BaseTween<?> source) {
                                    UpdateStatsDialog dialog = new UpdateStatsDialog(ChallengeMenu.this, mainStage,
                                            new UpdateStatsDialog.ButtonType[]{UpdateStatsDialog.ButtonType.GO_BACK},
                                            new UpdateStatsDialog.ButtonType[]{UpdateStatsDialog.ButtonType.GO_BACK},
                                            new UpdateStatsDialog.ButtonType[] {
                                                    UpdateStatsDialog.ButtonType.GO_BACK,
                                                    UpdateStatsDialog.ButtonType.RETRY,
                                                    UpdateStatsDialog.ButtonType.CONTINUE},
                                            new UpdateStatsDialog.UpdateStatsDialogCallback() {
                                                @Override
                                                public void onContinue() {
                                                    game.loadNextScreen(ChallengeMenu.this, challengeScreens[value]);
                                                }

                                                @Override
                                                public void onGoBack() {
                                                    infoTable.remove();
                                                    mainTable.setTouchable(Touchable.childrenOnly);
                                                    topTable.setTouchable(Touchable.childrenOnly);
                                                }
                                            });
                                    dialog.updateStats();
                                }
                            })
                            .start(tweenManager);
                }
            }
        });
        infoTable.add(continueButton).padBottom(17f).row();
        infoTable.add(backButton);

        mainStage.addActor(infoTable);

    }
}
