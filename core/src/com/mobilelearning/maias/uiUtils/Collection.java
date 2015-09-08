package com.mobilelearning.maias.uiUtils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mobilelearning.maias.Assets;
import com.mobilelearning.maias.SavedData;
import com.mobilelearning.maias.accessors.LabelAccessor;
import com.mobilelearning.maias.screens.StandardScreen;
import com.mobilelearning.maias.serviceHandling.json.StatsData;

import java.util.concurrent.atomic.AtomicInteger;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Cubic;

/**
 * Created by AFFonseca on 21/08/2015.
 */
public class Collection extends Table {
    private final static float portugalPositions [][] = {{110f, 453f}, {177f, 591f}};
    private final static float lisbonPositions [][] = {{256f, 113f}, {196f, 113f}, {204f, 70f},
            {228f, 113f}, {162f, 124f}, {242f, 74f}, {225f, 168f}, {115f, 195f}, {147f, 76f},
            {50f, 209f}, {189f, 168f}, {256f, 210f}, {255f, 161f}, {153f, 168f}};

    private int [] collectionData;
    private TextureAtlas stickerAtlas;
    private static final String [] pageLabelTexts = {"A-Bo", "Br-Cr", "Cr-D", "E-Ge", "Go-Gu", "Ma", "Ma-Me",
            "Mo-N", "P-R", "Sa-So", "St-T", "V", "", "MAPA"};
    private static final int [] stickersPerPage = {4, 4, 4, 4, 4, 4, 2, 4, 4, 4, 4, 3};

    private Label.LabelStyle normalStyle, selectedStyle;
    private Label [] pageLabels;
    private int currentPage = 12;
    private Table [] pages;

    private Image [] stickers;
    private Label [] stickersQuantity;

    private Table lisbonTable;

    private Table stickerInfoTable;
    private Image infoTableSticker;
    private Button tradeButton;
    private Label infoTableStickerQuantity, infoTableLabel, moneyLabel;
    int currentStickerNumber;

    public Collection() {
        TextureAtlas atlas = Assets.prepareCollection();
        stickerAtlas = Assets.prepareStickers();

        setBackground(new TextureRegionDrawable(atlas.findRegion("page")));
        setSize(getBackground().getMinWidth(), getBackground().getMinHeight());
        top().left().padTop(82f).padLeft(536f).defaults().height(60.25f).width(68f);

        Image separator = new Image(atlas.findRegion("vertical_line"));
        separator.setX(530f); addActor(separator);

        normalStyle = new Label.LabelStyle(Assets.uiSkin.getFont("arial"), Color.BLACK);
        selectedStyle = new Label.LabelStyle(Assets.uiSkin.getFont("arial"), Assets.prettyRed);

        pageLabels = new Label[pageLabelTexts.length];
        pages = new Table[pageLabelTexts.length];
        int stickerCounter = 0;
        stickers = new Image[Assets.numberOfStickers];
        stickersQuantity = new Label[Assets.numberOfStickers];
        for (int i=0; i< pageLabelTexts.length; i++) {
            final AtomicInteger tempInt = new AtomicInteger(i);
            pageLabels[i] = new Label(pageLabelTexts[i], normalStyle);
            pageLabels[i].setFontScale(0.55f); pageLabels[i].setAlignment(Align.center);
            pageLabels[i].addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    changePage(tempInt.get());
                }
            });
            add(pageLabels[i]).row();

            if(i>11) //continue if cover or map
                continue;

            pages[i] = new Table();
            pages[i].setSize(532f, getHeight());
            pages[i].center().top();

            pages[i].add(new Label(pageLabelTexts[i], normalStyle)).padTop(45f).padBottom(8f).colspan(2).row();
            pages[i].add(new Image(atlas.findRegion("underline"))).padBottom(30f).colspan(2).row();

            for(int j=0; j< stickersPerPage[i]; j++){
                Table stickerTable = new Table();
                Image sticker = new Image(stickerAtlas.findRegion("" +(++stickerCounter)));
                stickerTable.add(sticker).size(sticker.getPrefWidth()*0.6f, sticker.getPrefHeight()*0.6f).row();
                final AtomicInteger aux = new AtomicInteger(stickerCounter);
                sticker.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        showStickerInfo(aux.get());
                    }
                });
                Label numberOfSameSticker = new Label("", normalStyle);
                numberOfSameSticker.setFontScale(0.8f);
                stickerTable.add(numberOfSameSticker).right().padTop(-10f).padRight(18f);

                Cell<?> stickerCell = pages[i].add(stickerTable).padBottom(30f);

                if(j%2 == 0)
                    stickerCell.padLeft(12f);
                else
                    stickerCell.row();

                stickers[stickerCounter-1] = sticker;
                stickersQuantity[stickerCounter-1] = numberOfSameSticker;
            }
        }

        //Cover
        pageLabels[12].getListeners().clear();
        pages[12] = new Table();
        pages[12].setBackground(new TextureRegionDrawable(atlas.findRegion("cover")));
        pages[12].setSize(pages[12].getBackground().getMinWidth(), pages[12].getBackground().getMinHeight());
        addActor(pages[12]);

        //Map
        pages[13] = new Table();
        pages[13].setSize(532f, getHeight());
        pages[13].center().top();

        pages[13].add(new Label(pageLabelTexts[13], normalStyle)).padTop(45f).padBottom(8f).colspan(2).row();
        pages[13].add(new Image(atlas.findRegion("underline"))).padBottom(30f).colspan(2).row();

        Table mapTable = new Table();
        mapTable.setBackground(new TextureRegionDrawable(atlas.findRegion("portugal_map")));
        mapTable.setSize(mapTable.getBackground().getMinWidth(), mapTable.getBackground().getMinHeight()-1);
        mapTable.setPosition(91f, 24f);

        for(float [] position : portugalPositions){
            Image sticker = new Image(stickerAtlas.findRegion("" +(++stickerCounter)));
            sticker.setPosition(position[0], position[1]);
            sticker.setSize(sticker.getPrefWidth() * 0.085f, sticker.getPrefHeight() * 0.085f);
            final AtomicInteger aux = new AtomicInteger(stickerCounter);
            sticker.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showStickerInfo(aux.get());
                }
            });
            stickers[stickerCounter-1] = sticker;
            mapTable.addActor(sticker);
        }

        AnimatedActor lisbonImage = new AnimatedActor(new AnimationDrawable(Assets.lisbonAnimation));
        lisbonImage.setPosition(0f, 254f); mapTable.addActor(lisbonImage);

        lisbonTable = new Table();
        lisbonTable.setBackground(new TextureRegionDrawable(atlas.findRegion("lisbon_map")));
        lisbonTable.setSize(lisbonTable.getBackground().getMinWidth(), lisbonTable.getBackground().getMinHeight());
        lisbonTable.setPosition(13f, 24f);

        for(float [] position : lisbonPositions){
            Image sticker = new Image(stickerAtlas.findRegion("" +(++stickerCounter)));
            sticker.setPosition(position[0], position[1]);
            sticker.setSize(sticker.getPrefWidth() * 0.085f, sticker.getPrefHeight() * 0.085f);
            final AtomicInteger aux = new AtomicInteger(stickerCounter);
            sticker.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showStickerInfo(aux.get());
                }
            });
            stickers[stickerCounter-1] = sticker;
            lisbonTable.addActor(sticker);
        }

        lisbonImage.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                lisbonTable.setVisible(true);
            }
        });

        Button lisbonExitButton = new Button(
                new TextureRegionDrawable(atlas.findRegion("button_close_up")),
                new TextureRegionDrawable(atlas.findRegion("button_close_down"))
        );
        lisbonExitButton.setColor(Color.DARK_GRAY);

        lisbonExitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                lisbonTable.setVisible(false);
            }
        });
        lisbonExitButton.setSize(lisbonExitButton.getPrefWidth(), lisbonExitButton.getPrefHeight());
        lisbonExitButton.setPosition(
                lisbonTable.getWidth() - lisbonExitButton.getPrefWidth() - 35f,
                lisbonTable.getHeight() - lisbonExitButton.getPrefHeight() - 35f
        );
        lisbonTable.addActor(lisbonExitButton);

        pages[13].addActor(mapTable); pages[13].addActor(lisbonTable);

        //Sticker Info
        stickerInfoTable = new Table();

        Image mask = new Image(atlas.findRegion("black_pixel")); mask.setFillParent(true);
        mask.getColor().a = 0.8f; stickerInfoTable.addActor(mask);

        Table innerStickerInfoTable = new Table(); innerStickerInfoTable.left();

        infoTableStickerQuantity = new Label("",
                new Label.LabelStyle(Assets.uiSkin.getFont("arial"), Color.WHITE));
        infoTableStickerQuantity.setFontScale(0.9f);
        innerStickerInfoTable.add(infoTableStickerQuantity).padLeft(26f).padRight(15f);

        TextButton.TextButtonStyle tradeButtonStyle = new TextButton.TextButtonStyle(
                new TextureRegionDrawable(atlas.findRegion("button_trade_up")),
                new TextureRegionDrawable(atlas.findRegion("button_trade_down")),
                null, null);
        tradeButtonStyle.font = Assets.uiSkin.getFont("arial");
        tradeButtonStyle.fontColor = Color.WHITE;
        tradeButton = new TextButton("trocar", tradeButtonStyle);
        tradeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                tradeSticker();
            }
        });
        tradeButton.setSize(tradeButton.getPrefWidth(), tradeButton.getPrefHeight());
        innerStickerInfoTable.add(tradeButton).left();

        Button exitButton = new Button(
                new TextureRegionDrawable(atlas.findRegion("button_close_up")),
                new TextureRegionDrawable(atlas.findRegion("button_close_down"))
        );
        innerStickerInfoTable.add(exitButton).right().expandX().padRight(20f).row();

        infoTableSticker = new Image(stickerAtlas.findRegion("1"));

        infoTableLabel = new Label("", new Label.LabelStyle(Assets.uiSkin.getFont("arial"), Color.WHITE));
        infoTableLabel.setFontScale(0.7f); infoTableLabel.setWrap(true);


        Table container = new Table(); container.left();
        container.add(infoTableLabel).width(345f);

        final ScrollPane pane = new ScrollPane(container);
        pane.getStyle().vScroll = new TextureRegionDrawable(atlas.findRegion("scroll_bar"));
        pane.getStyle().vScrollKnob = new TextureRegionDrawable(atlas.findRegion("scroll_knob"));
        pane.setFadeScrollBars(false);

        stickerInfoTable.add(innerStickerInfoTable).width(infoTableSticker.getPrefWidth()).row();
        stickerInfoTable.add(infoTableSticker).padBottom(10f).row();
        stickerInfoTable.add(pane).size(371f, 247f);

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pane.setScrollY(0f);
                closeStickerInfo();
            }
        });

    }

    private void changePage(int newPage){
        if(newPage == currentPage)
            return;

        pageLabels[currentPage].setStyle(normalStyle);
        pageLabels[newPage].setStyle(selectedStyle);

        pages[currentPage].remove();
        addActor(pages[newPage]);

        currentPage = newPage;
    }

    private void showStickerInfo(int stickerNumber){
        currentStickerNumber = stickerNumber-1;

        for(Table page : pages) {
                page.setTouchable(Touchable.disabled);
        }
        for (Label label :pageLabels)
            label.setTouchable(Touchable.disabled);

        Stage currentStage = getStage();

        if(currentStage instanceof StandardScreen.StandardStage) {
            StandardScreen.StandardStage standardStage = (StandardScreen.StandardStage) currentStage;
            stickerInfoTable.setBounds(-standardStage.getPadLeft(), -standardStage.getPadBottom(),
                    standardStage.getRealWidth(), standardStage.getRealHeight());
        }
        else
            stickerInfoTable.setBounds(0, 0, currentStage.getWidth(), currentStage.getHeight());

        infoTableStickerQuantity.setText("x" + collectionData[currentStickerNumber]);
        tradeButton.setVisible(collectionData[currentStickerNumber] > 1 && moneyLabel != null);
        infoTableSticker.setDrawable(new TextureRegionDrawable(stickerAtlas.findRegion("" + stickerNumber)));
        infoTableLabel.setText(CollectionInfo.stickerInfos[currentStickerNumber]);

        addActor(stickerInfoTable);
    }

    private void tradeSticker(){
        Assets.moneyFX.stop();

        StatsData statsData = SavedData.getStats();
        moneyLabel.setText("" +statsData.money);

        statsData.money += 50;
        statsData.collectionData[currentStickerNumber] -= 1;
        collectionData = statsData.collectionData;
        SavedData.setStats(statsData);

        if(collectionData[currentStickerNumber] == 1)
            tradeButton.setVisible(false);

        if(stickersQuantity[currentStickerNumber] != null)
            stickersQuantity[currentStickerNumber].setText("x" + collectionData[currentStickerNumber]);
        infoTableStickerQuantity.setText("x" + collectionData[currentStickerNumber]);


        if(getStage() instanceof StandardScreen.StandardStage){
            TweenManager tweenManager = ((StandardScreen.StandardStage)getStage()).getTweenManager();

            Assets.moneyFX.play();
            tweenManager.killTarget(moneyLabel);
            Tween.to(moneyLabel, LabelAccessor.NUMBER_CHANGE, 5f).target(statsData.money).ease(Cubic.OUT)
                    .start(tweenManager);
        }
        else {
            moneyLabel.setText("" +statsData.money);
        }
    }

    private void closeStickerInfo(){
        stickerInfoTable.remove();

        for(Table page : pages) {
                page.setTouchable(Touchable.childrenOnly);
        }
        for (Label label :pageLabels)
            label.setTouchable(Touchable.enabled);

    }

    public void resetPage(Label moneyLabel){
        lisbonTable.setVisible(false);

        collectionData = SavedData.getStats().collectionData;
        for(int i=0; i<collectionData.length; i++){

            if(collectionData[i] != 0){
                stickers[i].setColor(Color.WHITE);
                stickers[i].setTouchable(Touchable.enabled);
                if(stickersQuantity[i] != null) {
                    stickersQuantity[i].setColor(Color.BLACK);
                    stickersQuantity[i].setText("x" + collectionData[i]);
                }
            }
            else {
                stickers[i].setColor(Color.GRAY);
                stickers[i].getColor().a = 0.5f;
                stickers[i].setTouchable(Touchable.disabled);
                if(stickersQuantity[i] != null) {
                    stickersQuantity[i].setColor(Color.GRAY);
                    stickersQuantity[i].getColor().a = 0.5f;
                    stickersQuantity[i].setText("x" +collectionData[i]);
                }
                else
                    stickers[i].getColor().a = 0.25f;
            }
        }

        this.moneyLabel = moneyLabel;

        changePage(12);
    }
}
