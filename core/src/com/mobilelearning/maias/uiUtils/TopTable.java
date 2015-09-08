package com.mobilelearning.maias.uiUtils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mobilelearning.maias.Assets;
import com.mobilelearning.maias.SavedData;

/**
 * Created by AFFonseca on 04/08/2015.
 */
public class TopTable extends Table {
    private Label moneyValue;
    private Image moneyImage;
    private ImageButton backButton;

    public TopTable(String titleString, boolean showMoney, Skin uiSkin, final BackButtonCallback callback) {
        super();

        TextureRegionDrawable topRegion = new TextureRegionDrawable(Assets.miscellaneous.findRegion("top_panel"));
        setBackground(topRegion);
        setSize(topRegion.getMinWidth(), topRegion.getMinHeight());

        center().left();

        Label.LabelStyle labelStyle = new Label.LabelStyle(uiSkin.getFont("default-font"), Color.WHITE);
        Label title = new Label(titleString, labelStyle);
        title.setFontScale(0.8f);
        add(title).padLeft(31f);

        Image separator = new Image(Assets.miscellaneous.findRegion("separator"));
        add(separator).padLeft(21f);

        moneyValue = new Label("" + SavedData.getStats().money,
                new Label.LabelStyle(uiSkin.getFont("default-font"), Color.WHITE));
        moneyValue.setFontScale(0.8f);
        add(moneyValue).padLeft(27f);

        moneyImage = new Image(Assets.miscellaneous.findRegion("money_icon"));
        add(moneyImage).padLeft(16f);

        moneyValue.setVisible(showMoney); moneyImage.setVisible(showMoney);

        backButton = new ImageButton(
                new TextureRegionDrawable(Assets.miscellaneous.findRegion("backarrow_up")),
                new TextureRegionDrawable(Assets.miscellaneous.findRegion("backarrow_down"))
        );
        backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if(callback != null)
                    callback.onClicked();
            }
        });
        backButton.setPosition(590f, 18f);
        addActor(backButton);
    }

    public void setShowMoney(boolean showMoney){
        moneyValue.setVisible(showMoney); moneyImage.setVisible(showMoney);
    }

    public Label getMoneyLabel(){
        return moneyValue;
    }

    public ImageButton getBackButton(){
        return backButton;
    }

    public interface BackButtonCallback{
        void onClicked();
    }
}
