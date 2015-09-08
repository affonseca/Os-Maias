package com.mobilelearning.maias.uiUtils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mobilelearning.maias.Assets;
import com.mobilelearning.maias.SavedData;
import com.mobilelearning.maias.screens.StandardScreen;
import com.mobilelearning.maias.serviceHandling.handlers.GetLeaderboardScoresHandler;
import com.mobilelearning.maias.serviceHandling.handlers.UpdateStatsHandler;
import com.mobilelearning.maias.serviceHandling.json.LeaderboardScoresData;
import com.mobilelearning.maias.serviceHandling.services.GetLeaderboardScoresService;
import com.mobilelearning.maias.serviceHandling.services.UpdateStatsService;

/**
 * Created by AFFonseca on 16/07/2015.
 */
public class UpdateStatsDialog implements UpdateStatsHandler, GetLeaderboardScoresHandler {
    private StandardScreen screen;
    private Stage stage;
    private ButtonType[] buttonsOnClassError;
    private ButtonType[] buttonsOnUserError;
    private ButtonType[] buttonsOnOtherErrors;
    private UpdateStatsDialogCallback callback;

    public UpdateStatsDialog(StandardScreen screen, Stage stage, ButtonType[] buttonsOnClassError,
                             ButtonType[] buttonsOnUserError, ButtonType[] buttonsOnOtherErrors,
                             UpdateStatsDialogCallback callback) {
        this.screen = screen;
        this.stage = stage;
        this.buttonsOnClassError = buttonsOnClassError;
        this.buttonsOnUserError = buttonsOnUserError;
        this.buttonsOnOtherErrors = buttonsOnOtherErrors;
        this.callback = callback;
    }

    public void updateStats(){

        screen.startLoadingAnimation(false);
        UpdateStatsService service = new UpdateStatsService(this);
        service.requestUpdateStats(
                Long.parseLong(SavedData.getCurrentClassID()),
                SavedData.getStats()
        );
    }

    public void getLeaderboardScores(){
        screen.startLoadingAnimation(false);
        GetLeaderboardScoresService service = new GetLeaderboardScoresService(this);
        service.requestLeaderboardScores(
                Long.parseLong(SavedData.getCurrentClassID()),
                SavedData.getStats().challengeScore
        );
    }

    private void showDialog(String errorMessage, ButtonType[] buttons){

        Dialog out = new Dialog("", new Window.WindowStyle(
                screen.uiSkin.getFont("default-font"),
                Color.WHITE,
                new TextureRegionDrawable(Assets.miscellaneous.findRegion("dialog_panel")))){
            @Override
            protected void result(Object object) {
                if (((String) object).contains("continue")) {
                    callback.onContinue();
                }
                else if (((String) object).contains("retry")) {
                    if(callback instanceof GetLeaderboardScoresDialogCallback)
                        getLeaderboardScores();
                    else
                        updateStats();
                }
                else { //if goBack
                    callback.onGoBack();
                }
            }
        };

        out.getCell(out.getContentTable()).height(306f);
        out.getContentTable().center().padTop(12f);

        out.getCell(out.getButtonTable()).height(303f);
        out.getButtonTable().center().padBottom(23f).defaults().padTop(11f);

        Label.LabelStyle labelStyle = new Label.LabelStyle(screen.uiSkin.getFont("default-font"), Color.WHITE);

        Label title = new Label("Erro a guardar a\npontuação", labelStyle);
        title.setAlignment(Align.center);
        out.text(title).getContentTable().row();
        out.getContentTable().getCells().peek().padBottom(40f);

        Label message = new Label(errorMessage, labelStyle);
        message.setAlignment(Align.center);
        message.setFontScale(0.8f);
        out.text(message).getContentTable().row();

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(
                new TextureRegionDrawable(Assets.miscellaneous.findRegion("dialog_button_up")),
                new TextureRegionDrawable(Assets.miscellaneous.findRegion("dialog_button_down")),
                null, screen.uiSkin.getFont("default-font")
        );
        buttonStyle.fontColor = Color.WHITE;

        for(ButtonType buttonType : buttons){
            switch (buttonType){
                case CONTINUE:
                    out.button("continuar", "continue", buttonStyle).getButtonTable().row();
                    break;
                case GO_BACK:
                    out.button("cancelar", "goBack", buttonStyle).getButtonTable().row();
                    break;
                case RETRY:
                    out.button("tentar novamente", "retry", buttonStyle).getButtonTable().row();
                    break;
            }
        }

        for(Actor actor : out.getButtonTable().getChildren()){
            TextButton button = (TextButton)actor;
            button.getLabel().setFontScale(0.8f);
        }

        out.show(stage);
    }

    private String formatMessage(String originalMessage){
        String newMessage = "";

        while (true) {
            int currentCharacter = 25;

            if (originalMessage.length() <= currentCharacter) {
                newMessage = newMessage + originalMessage;
                break;
            }

            while (originalMessage.charAt(currentCharacter - 1) != ' ' && currentCharacter > 0)
                currentCharacter--;

            if (currentCharacter == 0)
                currentCharacter = 25;

            newMessage = newMessage + originalMessage.substring(0, currentCharacter) + "\n";
            originalMessage = originalMessage.substring(currentCharacter);
        }


        return newMessage;
    }

    @Override
    public void onUpdateStatsSuccess() {
        screen.stopLoadingAnimation();
        callback.onContinue();
    }

    @Override
    public void onUpdateStatsClassError() {
        screen.stopLoadingAnimation();
        showDialog(formatMessage("A turma selecionada já não existe"), buttonsOnClassError);
    }

    @Override
    public void onUpdateStatsUserError() {
        screen.stopLoadingAnimation();
        showDialog(formatMessage("Já não pertences à turma selecionada"), buttonsOnUserError);
    }

    @Override
    public void onUpdateStatsOtherError(String error) {
        screen.stopLoadingAnimation();
        showDialog(formatMessage(error), buttonsOnOtherErrors);
    }

    @Override
    public void onGetLeaderboardScoresSuccess(LeaderboardScoresData response) {
        screen.stopLoadingAnimation();
        GetLeaderboardScoresDialogCallback castedCallback = (GetLeaderboardScoresDialogCallback)callback;
        castedCallback.onSuccess(response);
    }

    @Override
    public void onGetLeaderboardScoresClassError() {
        onUpdateStatsClassError();
    }

    @Override
    public void onGetLeaderboardScoresUserError() {
        onUpdateStatsUserError();
    }

    @Override
    public void onGetLeaderboardScoresError(String error) {
        onUpdateStatsOtherError(error);
    }

    public enum ButtonType {
        CONTINUE, GO_BACK, RETRY
    }

    public interface UpdateStatsDialogCallback {
        void onContinue();
        void onGoBack();
    }

    public interface GetLeaderboardScoresDialogCallback extends UpdateStatsDialogCallback {
        void onSuccess(LeaderboardScoresData response);
    }
}
