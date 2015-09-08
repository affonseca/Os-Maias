package com.mobilelearning.maias.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.mobilelearning.maias.Assets;
import com.mobilelearning.maias.MaiasGame;
import com.mobilelearning.maias.SavedData;
import com.mobilelearning.maias.accessors.ActorAccessor;
import com.mobilelearning.maias.serviceHandling.handlers.GetStatsHandler;
import com.mobilelearning.maias.serviceHandling.handlers.JoinClassHandler;
import com.mobilelearning.maias.serviceHandling.handlers.LoginHandler;
import com.mobilelearning.maias.serviceHandling.handlers.OtherClassesHandler;
import com.mobilelearning.maias.serviceHandling.handlers.RegisterHandler;
import com.mobilelearning.maias.serviceHandling.handlers.UserClassesHandler;
import com.mobilelearning.maias.serviceHandling.json.*;
import com.mobilelearning.maias.serviceHandling.json.ClassInfo;
import com.mobilelearning.maias.serviceHandling.services.GetStatsService;
import com.mobilelearning.maias.serviceHandling.services.JoinClassService;
import com.mobilelearning.maias.serviceHandling.services.LoginService;
import com.mobilelearning.maias.serviceHandling.services.OtherClassesService;
import com.mobilelearning.maias.serviceHandling.services.RegisterService;
import com.mobilelearning.maias.serviceHandling.services.UserClassesService;
import com.mobilelearning.maias.uiUtils.TopTable;
import com.mobilelearning.maias.uiUtils.UpdateStatsDialog;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

/**
 * Created with IntelliJ IDEA.
 * User: AFFonseca
 * Date: 12-01-2015
 * Time: 15:57
 * To change this template use File | Settings | File Templates.
*/
public class MainMenu extends StandardScreen implements LoginHandler, RegisterHandler, OtherClassesHandler,
        JoinClassHandler, UserClassesHandler, GetStatsHandler {
    private static final String firstTimePlayingMessage = "Para te tornares um especialista na obra " +
            "\"Os Maias\", de Eça de Queirós, deves treinar os teus conhecimentos primeiro, com o dinheiro" +
            " que angariares podes depois apostar na área de Desafio. No leaderboard apenas serão contados" +
            " os pontos que conseguires nos Desafios. Prova que és um expert!";

    private TextureAtlas mainMenuAtlas;
    private TextButton.TextButtonStyle textButtonStyle;
    private Button.ButtonStyle continueButtonStyle;
    private Button.ButtonStyle backButtonStyle;
    private TextField.TextFieldStyle textFieldStyle;

    private Table loginTable;
    private TextField usernameText;
    private TextField passwordText;

    private Table userClassesTable;
    private ClassArray userClasses;
    private TextButton userClassesJoinButton;
    private List<String> userClassesList;

    private Table otherClassesTable;
    private int joinedClassIndex;
    private ClassArray classesToJoin;
    private List<String> otherClassesList;

    private Table mainMenuTable;
    private Table collectionTable;
    private Label collectionMoney;
    private Table leaderboardTable, leaderboardEntries, creditsTable;


    private Table optionsTable;

    private Dialog messageDialog;
    private Label messageLabel;

    private Table lastTable;
    private Table currentTable;


    public MainMenu(MaiasGame game, SpriteBatch batch){
        super(game, batch);
    }

    @Override
    public void show() {
        super.show();

        backgroundStage.addActor(new Image(mainMenuAtlas.findRegion("background")));
        textButtonStyle = new TextButton.TextButtonStyle(
                new TextureRegionDrawable(mainMenuAtlas.findRegion("text_button_up")),
                new TextureRegionDrawable(mainMenuAtlas.findRegion("text_button_down")),
                null,
                uiSkin.getFont("default-font")
        );
        textButtonStyle.fontColor = textButtonStyle.downFontColor = Color.WHITE;

        TextButton.TextButtonStyle checkedButtonStyle = new TextButton.TextButtonStyle(
                new TextureRegionDrawable(mainMenuAtlas.findRegion("text_button_up")),
                new TextureRegionDrawable(mainMenuAtlas.findRegion("text_button_down")),
                new TextureRegionDrawable(mainMenuAtlas.findRegion("text_button_down")),
                uiSkin.getFont("default-font")
        );
        checkedButtonStyle.fontColor = textButtonStyle.downFontColor  = Color.WHITE;

        textFieldStyle = new TextField.TextFieldStyle(uiSkin.get(TextField.TextFieldStyle.class));
        textFieldStyle.font = uiSkin.getFont("default-font");
        textFieldStyle.messageFont = uiSkin.getFont("default-font");
        textFieldStyle.messageFontColor = textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.background = new TextureRegionDrawable(mainMenuAtlas.findRegion("text_field"));

        continueButtonStyle = new Button.ButtonStyle(
                new TextureRegionDrawable(mainMenuAtlas.findRegion("continue_button_up")),
                new TextureRegionDrawable(mainMenuAtlas.findRegion("continue_button_down")),
                null
        );

        backButtonStyle = new Button.ButtonStyle(
                new TextureRegionDrawable(Assets.miscellaneous.findRegion("back_button_up")),
                new TextureRegionDrawable(Assets.miscellaneous.findRegion("back_button_down")),
                null
        );

        createLoginTable();
        createUserClassesTable();
        createOtherClassesTable();
        createMainMenuTable();
        createLeaderboardTable();
        createCreditsTable();
        createCollectionTable();
        createOptionsTable();
        createDialogWindow();

        //Show no table!
        loginTable.setVisible(false);
        otherClassesTable.setVisible(false);
        userClassesTable.setVisible(false);
        mainMenuTable.setVisible(false);
        leaderboardTable.setVisible(false);
        creditsTable.setVisible(false);
        collectionTable.setVisible(false);
        optionsTable.setVisible(false);

        if(SavedData.getCurrentClassID() == null){
            //login required
            loginTable.setVisible(true);
            currentTable = loginTable;

            if(SavedData.getUserID() != null){
                //login not required. Going to step 2
                startLoadingAnimation(false);

                UserClassesService getClasses = new UserClassesService(MainMenu.this);
                getClasses.requestUserClasses();
            }
        }
        //Class not required. Going to main menu!
        else{
            mainMenuTable.setVisible(true);
            currentTable = mainMenuTable;
        }


    }

    @Override
    public void load() {
    }

    @Override
    public void prepare() {
        super.prepare();
        mainMenuAtlas = Assets.prepareMainMenu();
        Assets.prepareGlobalAssets();
        uiSkin = Assets.uiSkin;

    }

    @Override
    public void unload() {
    }

    private void createLoginTable(){

        //Creating the login Table and it's components
        loginTable = new Table();
        loginTable.setBounds(0, 0, mainStage.getWidth(), mainStage.getHeight());

        Image background = new Image(new TextureRegionDrawable(mainMenuAtlas.findRegion("login_table")));
        background.setPosition((mainStage.getWidth()-background.getPrefWidth())/2, 436f);
        loginTable.addActor(background);
        loginTable.center().top().padTop(195f);

        TextButton loginButton = new TextButton("ENTRAR", textButtonStyle);
        loginButton.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                Assets.clickFX.play();
                usernameText.getOnscreenKeyboard().show(false);
                startLoadingAnimation(false);

                LoginService login = new LoginService(MainMenu.this);
                login.requestLogin(usernameText.getText(), passwordText.getText());

            }
        }) ;

        TextButton registerButton = new TextButton("REGISTAR", textButtonStyle);
        registerButton.addListener(new ClickListener(){
            public void clicked (InputEvent event, float x, float y) {
                Assets.clickFX.play();
                usernameText.getOnscreenKeyboard().show(false);
                startLoadingAnimation(false);

                RegisterService register = new RegisterService(MainMenu.this);
                register.requestRegistration(usernameText.getText(), passwordText.getText());

            }
        }) ;

        Label.LabelStyle labelStyle = new Label.LabelStyle(uiSkin.getFont("default-font"), Color.WHITE);

        Label usernameLabel = new Label("LOGIN", labelStyle);
        usernameLabel.setAlignment(Align.center);

        usernameText = new TextField("", textFieldStyle);
        usernameText.setSize(
                usernameText.getStyle().background.getMinWidth(),
                usernameText.getStyle().background.getMinHeight());

        Label passwordLabel = new Label("PASSWORD", labelStyle);
        passwordLabel.setAlignment(Align.center);

        passwordText = new TextField("", textFieldStyle);
        passwordText.setPasswordCharacter('*');
        passwordText.setPasswordMode(true);
        passwordText.setSize(
                passwordText.getStyle().background.getMinWidth(),
                passwordText.getStyle().background.getMinHeight());

        loginTable.add(usernameLabel).row();
        loginTable.add(usernameText).width(usernameText.getStyle().background.getMinWidth()); loginTable.row();
        loginTable.add(passwordLabel).padTop(37f).row();
        loginTable.add(passwordText).width(passwordText.getStyle().background.getMinWidth()); loginTable.row();
        loginTable.add(loginButton).padTop(62f); loginTable.row();
        loginTable.add(registerButton).padTop(23f); loginTable.row();
        //loginTable.debug();

        mainStage.addActor(loginTable);

    }

    private void createUserClassesTable(){
        //Now creating the User Classes Table
        userClassesTable = new Table();
        userClassesTable.setBounds(0, 0, mainStage.getWidth(), mainStage.getHeight());

        Image background = new Image(new TextureRegionDrawable(mainMenuAtlas.findRegion("classes_table")));
        background.setPosition((mainStage.getWidth() - background.getPrefWidth()) / 2, 296f);
        userClassesTable.addActor(background);
        userClassesTable.center().top().padTop(276f);

        userClassesList = new List<>(uiSkin);
        userClassesList.getStyle().font = uiSkin.getFont("default-font");
        userClassesList.getStyle().selection = new TextureRegionDrawable(mainMenuAtlas.findRegion("list_color"));
        ScrollPane classesPane = new ScrollPane(userClassesList,
                new ScrollPane.ScrollPaneStyle(textFieldStyle.background, null, null, null, null));
        classesPane.setScrollBarPositions(false, true);
        classesPane.setOverscroll(false, true);

        Button backButton2 = new Button(backButtonStyle);
        backButton2.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (lastTable == loginTable || lastTable == otherClassesTable) {
                    changeTable(loginTable, false);

                    SavedData.clearUserData();
                    usernameText.setText("");
                    passwordText.setText("");

                } else
                    changeTable(optionsTable, false);

            }
        }) ;

        Button continueButton2 = new Button(continueButtonStyle);
        continueButton2.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();
                if (userClasses.classes.size == 0)
                    return;

                startLoadingAnimation(false);

                ClassInfo chosenClass = userClasses.classes.get(userClassesList.getSelectedIndex());
                GetStatsService getScoreService = new GetStatsService(MainMenu.this);
                getScoreService.requestStats(chosenClass.classID);

            }
        }) ;


        userClassesJoinButton = new TextButton("ENTRAR NUMA TURMA", textButtonStyle);
        userClassesJoinButton.getLabel().setFontScale(0.65f);
        userClassesJoinButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();
                startLoadingAnimation(false);

                OtherClassesService getClasses = new OtherClassesService(MainMenu.this);
                getClasses.requestOtherClasses();

            }
        }) ;

        userClassesTable.add(classesPane).height(373f).width(327f);
        userClassesTable.row();

        userClassesTable.add(continueButton2).padTop(-22f); userClassesTable.row();
        userClassesTable.add(userClassesJoinButton).padTop(23f); userClassesTable.row();

        backButton2.setPosition(
                mainStage.getWidth() - backButton2.getPrefWidth() - 25f,
                mainStage.getHeight() - backButton2.getPrefHeight() - 25f);
        userClassesTable.addActor(backButton2);

        mainStage.addActor(userClassesTable);

    }

    private void createOtherClassesTable(){
        //Now creating the other Classes Table

        otherClassesTable = new Table();
        otherClassesTable.setBounds(0, 0, mainStage.getWidth(), mainStage.getHeight());

        Image background = new Image(new TextureRegionDrawable(mainMenuAtlas.findRegion("classes_table")));
        background.setPosition((mainStage.getWidth() - background.getPrefWidth()) / 2, 296f);
        otherClassesTable.addActor(background);
        otherClassesTable.center().top().padTop(276f);

        otherClassesList = new List<>(uiSkin);
        otherClassesList.getStyle().font = uiSkin.getFont("default-font");
        userClassesList.getStyle().selection = new TextureRegionDrawable(mainMenuAtlas.findRegion("list_color"));
        ScrollPane classesPane = new ScrollPane(otherClassesList,
                new ScrollPane.ScrollPaneStyle(textFieldStyle.background, null, null, null, null));
        classesPane.setScrollBarPositions(false, true);
        classesPane.setOverscroll(false, true);

        Button joinButton = new Button(continueButtonStyle);
        joinButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();
                startLoadingAnimation(false);

                joinedClassIndex = otherClassesList.getSelectedIndex();
                long classToJoinID = classesToJoin.classes.get(joinedClassIndex).classID;

                JoinClassService join = new JoinClassService(MainMenu.this);
                join.requestJoinClass("" + classToJoinID);

            }
        }) ;

        Button backButton = new Button(backButtonStyle);
        backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (lastTable == userClassesTable) {

                    startLoadingAnimation(false);

                    UserClassesService myClassesService = new UserClassesService(MainMenu.this);
                    myClassesService.requestUserClasses();

                } else {
                    changeTable(optionsTable, false);
                }

            }
        });

        otherClassesTable.add(classesPane).height(373f).width(327f);
        otherClassesTable.row();

        otherClassesTable.add(joinButton).padTop(-22f); otherClassesTable.row();

        backButton.setPosition(
                mainStage.getWidth() - backButton.getPrefWidth() - 25f,
                mainStage.getHeight() - backButton.getPrefHeight() - 25f);
        otherClassesTable.addActor(backButton);

        mainStage.addActor(otherClassesTable);

    }


    private void createMainMenuTable(){
        mainMenuTable = new Table();
        mainMenuTable.setBounds(0, 0, mainStage.getWidth(), mainStage.getHeight());
        mainMenuTable.center().padTop(100f).padLeft(47f).defaults().padBottom(30f).padRight(47f);

        TextButton training = new TextButton("TREINO", textButtonStyle);
        training.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();
                game.loadNextScreen(MainMenu.this, MaiasGame.ScreenType.TRAINING_MENU);
            }
        }) ;

        TextButton challenge = new TextButton("DESAFIO", textButtonStyle);
        challenge.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();
                game.loadNextScreen(MainMenu.this, MaiasGame.ScreenType.CHALLENGE_MENU);
            }
        }) ;

        TextButton collection = new TextButton("CADERNETA", textButtonStyle);
        collection.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();
                Assets.collection.resetPage(collectionMoney);
                changeTable(collectionTable, true);
            }
        });

        TextButton options = new TextButton("OPÇÕES", textButtonStyle);
        options.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();
                changeTable(optionsTable, true);
            }
        }) ;

        ImageButton leaderboardButton = new ImageButton(
                new TextureRegionDrawable(mainMenuAtlas.findRegion("leaderboard_button_up")),
                new TextureRegionDrawable(mainMenuAtlas.findRegion("leaderboard_button_down"))
        );
        leaderboardButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();

                mainMenuTable.setTouchable(Touchable.disabled);
                UpdateStatsDialog scoreDialog = new UpdateStatsDialog(MainMenu.this, mainStage,
                    new UpdateStatsDialog.ButtonType[]{
                            UpdateStatsDialog.ButtonType.GO_BACK},
                    new UpdateStatsDialog.ButtonType[]{
                            UpdateStatsDialog.ButtonType.GO_BACK},
                    new UpdateStatsDialog.ButtonType[]{
                            UpdateStatsDialog.ButtonType.RETRY,
                            UpdateStatsDialog.ButtonType.GO_BACK
                            },
                    new UpdateStatsDialog.GetLeaderboardScoresDialogCallback() {
                        @Override
                        public void onSuccess(LeaderboardScoresData response) {
                            fillLeaderboardsTable(response);
                            changeTable(leaderboardTable, true);
                        }
                        @Override
                        public void onContinue() {
                            //Continue is not an option xD
                        }
                        @Override
                        public void onGoBack() {
                            mainMenuTable.setTouchable(Touchable.childrenOnly);
                        }
                    });
                scoreDialog.getLeaderboardScores();
            }
        });

        ImageButton creditsButton = new ImageButton(
                new TextureRegionDrawable(mainMenuAtlas.findRegion("credits_button_up")),
                new TextureRegionDrawable(mainMenuAtlas.findRegion("credits_button_down"))
        );
        creditsButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();
                changeTable(creditsTable, true);
            }
        });

        ImageButton logoutButton = new ImageButton(
                new TextureRegionDrawable(mainMenuAtlas.findRegion("logout_button_up")),
                new TextureRegionDrawable(mainMenuAtlas.findRegion("logout_button_down"))
        );
        logoutButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();
                UpdateStatsDialog scoreDialog = new UpdateStatsDialog(MainMenu.this, mainStage,
                        new UpdateStatsDialog.ButtonType[]{
                                UpdateStatsDialog.ButtonType.CONTINUE,
                                UpdateStatsDialog.ButtonType.GO_BACK},
                        new UpdateStatsDialog.ButtonType[]{
                                UpdateStatsDialog.ButtonType.CONTINUE,
                                UpdateStatsDialog.ButtonType.GO_BACK},
                        new UpdateStatsDialog.ButtonType[]{
                                UpdateStatsDialog.ButtonType.CONTINUE,
                                UpdateStatsDialog.ButtonType.RETRY,
                                UpdateStatsDialog.ButtonType.GO_BACK},
                        new UpdateStatsDialog.UpdateStatsDialogCallback() {
                            @Override
                            public void onContinue() {
                                changeTable(loginTable, false);

                                SavedData.clearUserData();
                                usernameText.setText("");
                                passwordText.setText("");
                            }

                            @Override
                            public void onGoBack() {
                                //DO absolutely nothing :)
                            }
                        });
                scoreDialog.updateStats();
            }
        }) ;

        Label.LabelStyle versionStyle = new Label.LabelStyle(uiSkin.getFont("arial"), Color.BLACK);
        versionStyle.background = new TextureRegionDrawable(new TextureRegion(Assets.loading.getKeyFrame(0),
                        0, Assets.loading.getKeyFrame(0).getRegionHeight()/10, 1, 1));
        Label version = new Label(" Versão: " +Assets.currentVersion +"  ", versionStyle);
        version.setAlignment(Align.center); version.setFontScale(0.6f);

        mainMenuTable.add(training).colspan(3);mainMenuTable.row();
        mainMenuTable.add(challenge).colspan(3);mainMenuTable.row();
        mainMenuTable.add(collection).colspan(3);mainMenuTable.row();
        mainMenuTable.add(options).colspan(3).padBottom(70f);  mainMenuTable.row();
        mainMenuTable.add(leaderboardButton); mainMenuTable.add(creditsButton); mainMenuTable.add(logoutButton).row();
        mainMenuTable.add(version).colspan(3).padTop(70f);

        mainStage.addActor(mainMenuTable);

    }

    private void createLeaderboardTable(){
        leaderboardTable = new Table();
        leaderboardTable.setBounds(0, 0, mainStage.getWidth(), mainStage.getHeight()+mainStage.getPadBottom());
        leaderboardTable.top().padTop(318f+mainStage.getPadBottom()).padLeft(66f).padRight(66f);

        leaderboardTable.addActor(new Image(mainMenuAtlas.findRegion("leaderboard")));

        //Adding the top bar
        TopTable topTable = new TopTable("LEADERBOARD", false, uiSkin,
                new TopTable.BackButtonCallback() {
                    @Override
                    public void onClicked() {
                        changeTable(mainMenuTable, false);
                    }
                });
        topTable.setPosition((mainStage.getWidth() - topTable.getWidth()) / 2,
                mainStage.getHeight() + mainStage.getPadBottom() - topTable.getHeight());
        leaderboardTable.addActor(topTable);

        leaderboardEntries = new Table();
        leaderboardEntries.center().top().defaults().height(67f);
        leaderboardTable.add(leaderboardEntries).width(508f);

        mainStage.addActor(leaderboardTable);

    }

    private void createCreditsTable(){
        creditsTable = new Table();
        creditsTable.setBounds(0, 0, mainStage.getWidth(), mainStage.getHeight()+mainStage.getPadBottom());

        creditsTable.setBackground(new TextureRegionDrawable(mainMenuAtlas.findRegion("credits_background")));

        creditsTable.addActor(new Image(mainMenuAtlas.findRegion("credits")));

        //Adding the top bar
        TopTable topTable = new TopTable("CRÉDITOS", false, uiSkin,
                new TopTable.BackButtonCallback() {
                    @Override
                    public void onClicked() {
                        changeTable(mainMenuTable, false);
                    }
                });
        topTable.setPosition((mainStage.getWidth() - topTable.getWidth()) / 2,
                mainStage.getHeight() + mainStage.getPadBottom() - topTable.getHeight());
        creditsTable.addActor(topTable);

        mainStage.addActor(creditsTable);

    }

    private void createCollectionTable(){
        collectionTable = new Table();
        collectionTable.setBounds(0, 0, mainStage.getWidth(), mainStage.getHeight() + mainStage.getPadBottom());

        collectionTable.addActor(Assets.collection);

        //Adding the top bar
        TopTable topTable = new TopTable("CADERNETA", true, uiSkin,
                new TopTable.BackButtonCallback() {
                    @Override
                    public void onClicked() {
                        changeTable(mainMenuTable, false);
                    }
                });
        topTable.setPosition((mainStage.getWidth() - topTable.getWidth()) / 2,
                mainStage.getHeight() + mainStage.getPadBottom() - topTable.getHeight());
        collectionTable.addActor(topTable);
        collectionMoney = topTable.getMoneyLabel();

        mainStage.addActor(collectionTable);

    }

    private void createOptionsTable(){
        optionsTable = new Table();
        optionsTable.setBounds(0, 0, mainStage.getWidth(), mainStage.getHeight());
        optionsTable.center().defaults().padBottom(33f);


        TextButton changeClass = new TextButton("MUDAR DE TURMA", textButtonStyle);
        changeClass.getLabel().setFontScale(0.9f);
        changeClass.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();
                startLoadingAnimation(false);

                UserClassesService getClasses = new UserClassesService(MainMenu.this);
                getClasses.requestUserClasses();

            }

        }) ;

        TextButton joinClass = new TextButton("INSCREVER EM TURMA", textButtonStyle);
        joinClass.getLabel().setFontScale(0.9f);
        joinClass.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Assets.clickFX.play();
                startLoadingAnimation(false);

                OtherClassesService getClasses = new OtherClassesService(MainMenu.this);
                getClasses.requestOtherClasses();

            }
        }) ;


        Button backButton = new Button(backButtonStyle);
        backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                changeTable(mainMenuTable, false);
            }
        }) ;

        optionsTable.add(changeClass).center();  optionsTable.row();
        optionsTable.add(joinClass).center();  optionsTable.row();
        backButton.setPosition(
                mainStage.getWidth() - backButton.getPrefWidth() - 25f,
                mainStage.getHeight() - backButton.getPrefHeight() - 25f);
        optionsTable.addActor(backButton);

        mainStage.addActor(optionsTable);
    }

    private void createDialogWindow(){
        messageDialog = new Dialog("", new Window.WindowStyle(
                uiSkin.getFont("default-font"),
                Color.WHITE,
                new TextureRegionDrawable(Assets.miscellaneous.findRegion("dialog_panel"))
        ));
        messageDialog.getCell(messageDialog.getContentTable()).expandY();
        messageDialog.getContentTable().center().padTop(12f);

        //messageDialog.getCell(messageDialog.getButtonTable()).height(303f);
        messageDialog.getButtonTable().center().padBottom(23f).defaults().padTop(11f);

        Label.LabelStyle labelStyle = new Label.LabelStyle(uiSkin.getFont("default-font"), Color.WHITE);

        messageLabel = new Label("", labelStyle);
        messageLabel.setFontScale(0.8f);
        messageLabel.setAlignment(Align.center); messageDialog.text(messageLabel);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(
                new TextureRegionDrawable(Assets.miscellaneous.findRegion("dialog_button_up")),
                new TextureRegionDrawable(Assets.miscellaneous.findRegion("dialog_button_down")),
                null, uiSkin.getFont("default-font")
        );
        buttonStyle.fontColor = Color.WHITE;
        TextButton okButton = new TextButton("OK", buttonStyle);
        okButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                currentTable.setTouchable(Touchable.enabled);
            }
        }) ;
        messageDialog.button(okButton);

    }

    private void showMessage(String message){
        //Since dialog is buggy and I cannot make it \n if the message is bigger than the mainStage:
        currentTable.setTouchable(Touchable.disabled);

        String newMessage = "";

        while (true){
            int currentCharacter = 26;

            if(message.length() <= currentCharacter){
                newMessage = newMessage + message;
                break;
            }

            while(message.charAt(currentCharacter-1) != ' ' && currentCharacter >0)
                currentCharacter--;

            if(currentCharacter == 0)
                currentCharacter = 26;

            newMessage = newMessage + message.substring(0, currentCharacter) + "\n";
            message = message.substring(currentCharacter);

        }
        messageLabel.setText(newMessage);
        messageDialog.show(mainStage);
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

                        if(lastTable == userClassesTable && currentTable == mainMenuTable){
                            StatsData statsData = SavedData.getStats();
                            if(statsData.difficulty1Counter == 0){ //didn't answer any question in training yet!
                                showMessage(firstTimePlayingMessage);
                            }
                        }
                    }
                })
                .start(tweenManager);
        lastTable = currentTable;
        currentTable = to;
    }

    @Override
    public void onLoginSuccess(UserData response) {
        //Login was successful. Now it's necessary to load the classes.
        SavedData.setUserData(response);

        UserClassesService getClasses = new UserClassesService(MainMenu.this);
        getClasses.requestUserClasses();

    }

    @Override
    public void onLoginError(String error) {
        stopLoadingAnimation();
        showMessage(error);
    }

    @Override
    public void onRegistrationSuccess(UserData response) {
        onLoginSuccess(response);
    }

    @Override
    public void onRegistrationError(String error) {
        stopLoadingAnimation();
        showMessage(error);
    }

    @Override
    public void onGettingUserClassesSuccess(ClassArray response) {

        stopLoadingAnimation();
        if(currentTable == optionsTable)
            userClassesJoinButton.setVisible(false);
        else
            userClassesJoinButton.setVisible(true);
        if(currentTable == otherClassesTable)
            changeTable(userClassesTable, false);
        else
            changeTable(userClassesTable, true);

        Array<String> classesArray = new Array<>(response.classes.size);
        for(ClassInfo it:response.classes){
            classesArray.add(it.className);
        }

        userClasses = response;
        userClassesList.setItems(classesArray);
    }

    @Override
    public void onGettingUserClassesError(String error) {
        stopLoadingAnimation();
        showMessage(error);
    }

    @Override
    public void onGettingOtherClassesSuccess(ClassArray response) {
        stopLoadingAnimation();

        changeTable(otherClassesTable, true);

        Array<String> classesArray = new Array<>(response.classes.size);
        for(ClassInfo it:response.classes){
            classesArray.add(it.className);
        }

        classesToJoin = response;
        otherClassesList.setItems(classesArray);
    }

    @Override
    public void onGettingOtherClassesError(String error) {
        stopLoadingAnimation();
        showMessage(error);
    }

    @Override
    public void onJoinClassSuccess() {
        stopLoadingAnimation();
        showMessage("Pedido de entrada adicionado com sucesso!");

        otherClassesList.getItems().removeIndex(joinedClassIndex);
        classesToJoin.classes.removeIndex(joinedClassIndex);
    }

    @Override
    public void onJoinClassError(String error) {
        stopLoadingAnimation();
        showMessage(error);
    }

    @Override
    public void onGetStatsSuccess(StatsData response) {
        stopLoadingAnimation();
        SavedData.setStats(response);

        ClassInfo chosenClass = userClasses.classes.get(userClassesList.getSelectedIndex());
        SavedData.setCurrentClass(chosenClass);

        if(lastTable == loginTable || lastTable == otherClassesTable)
            changeTable(mainMenuTable, true);
        else
            changeTable(optionsTable, false);
    }

    @Override
    public void onGetStatsError(String error) {
        stopLoadingAnimation();
        showMessage(error);
    }

    private void fillLeaderboardsTable(LeaderboardScoresData scoresData){
        leaderboardEntries.clear();

        Label.LabelStyle entryStyle = new Label.LabelStyle(uiSkin.getFont("default-font"), Color.WHITE);

        int counter = scoresData.leaderboardScores.size;
        if(counter == 5)
            counter++; //if list complete, add the possibility of outside list

        for(int i=0; i<counter; i++){
            String position, name, score;

            if(i==5){
                if(scoresData.leaderboardPosition.position < 6)
                    continue;

                position = "" +scoresData.leaderboardPosition.position;
                name = SavedData.getUsername();
                score = "" +scoresData.leaderboardPosition.challengeScore;
            }
            else {
                position = "" +(i+1);
                name = scoresData.leaderboardScores.get(i).username;
                score = "" +scoresData.leaderboardScores.get(i).score;
            }

            Label positionLabel = new Label(position, entryStyle);
            positionLabel.setAlignment(Align.right); positionLabel.setFontScale(0.9f);
            leaderboardEntries.add(positionLabel).padRight(20f).width(73f);

            Label nameLabel = new Label(name, entryStyle);
            leaderboardEntries.add(nameLabel).left().expandX();

            float shrink = 0f;
            if(name.length() > 10){
                shrink = (name.length()-10)/10f;
            }
            nameLabel.setFontScale(0.9f - 0.4f * shrink);

            Label scoreLabel = new Label(score, entryStyle);
            scoreLabel.setAlignment(Align.right); scoreLabel.setFontScale(0.9f);
            leaderboardEntries.add(scoreLabel).left().padLeft(20f).padRight(10f).row();

            if(i==5){
                leaderboardEntries.getCell(positionLabel).padTop(75f);
                leaderboardEntries.getCell(nameLabel).padTop(75f);
                leaderboardEntries.getCell(scoreLabel).padTop(75f);
            }
        }

    }
}
