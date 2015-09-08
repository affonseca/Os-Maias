package com.mobilelearning.maias.serviceHandling.services;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.mobilelearning.maias.serviceHandling.Errors;
import com.mobilelearning.maias.serviceHandling.RequestException;
import com.mobilelearning.maias.serviceHandling.ServiceRequester;
import com.mobilelearning.maias.serviceHandling.handlers.UpdateStatsHandler;
import com.mobilelearning.maias.serviceHandling.json.StatsData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AFFonseca on 16/07/2015.
 */
public class UpdateStatsService implements Service{
    private UpdateStatsHandler handler;


    public UpdateStatsService(UpdateStatsHandler handler) {
        this.handler = handler;
    }

    public void requestUpdateStats(long classID, StatsData data) {

        Map<String, String> parameters = new HashMap<>();

        parameters.put("classID", "" +classID);
        parameters.put("money", "" +data.money);
        parameters.put("challengeProgress", "" +data.challengeProgress);
        parameters.put("challengeScore", "" +data.challengeScore);

        parameters.put("theme1Score", "" +data.theme1Score);
        parameters.put("theme1Counter", "" +data.theme1Counter);
        parameters.put("theme2Score", "" +data.theme2Score);
        parameters.put("theme2Counter", "" +data.theme2Counter);
        parameters.put("theme3Score", "" +data.theme3Score);
        parameters.put("theme3Counter", "" +data.theme3Counter);
        parameters.put("theme4Score", "" +data.theme4Score);
        parameters.put("theme4Counter", "" +data.theme4Counter);
        parameters.put("theme5Score", "" +data.theme5Score);
        parameters.put("theme5Counter", "" +data.theme5Counter);
        parameters.put("theme6Score", "" +data.theme6Score);
        parameters.put("theme6Counter", "" +data.theme6Counter);
        parameters.put("theme7Score", "" +data.theme7Score);
        parameters.put("theme7Counter", "" +data.theme7Counter);


        parameters.put("difficulty1Score", "" +data.difficulty1Score);
        parameters.put("difficulty1Counter", "" +data.difficulty1Counter);
        parameters.put("difficulty2Score", "" +data.difficulty2Score);
        parameters.put("difficulty2Counter", "" +data.difficulty2Counter);
        parameters.put("difficulty3Score", "" +data.difficulty3Score);
        parameters.put("difficulty3Counter", "" +data.difficulty3Counter);
        parameters.put("difficulty4Score", "" +data.difficulty4Score);
        parameters.put("difficulty4Counter", "" +data.difficulty4Counter);

        Json json = new Json();
        parameters.put("collectionData", json.toJson(data.collectionData));

        try{
            ServiceRequester serviceRequester = new ServiceRequester(this, parameters);
            serviceRequester.postRequest();
        }
        catch (RequestException e){
            onRequestFailure(Errors.MAX_CONCURRENT_REQUESTS_REACHED.getValue());
        }
    }


    @Override
    public void onRequestSuccess(JsonValue response) {
        handler.onUpdateStatsSuccess();
    }

    @Override
    public void onRequestFailure(String error) {
        switch (error) {
            case "A turma selecionada já não existe":
                handler.onUpdateStatsClassError();
                break;
            case "Já não pertences à turma selecionada":
                handler.onUpdateStatsUserError();
                break;
            default:
                handler.onUpdateStatsOtherError(error);
        }
    }

    @Override
    public String getType() {
        return ServiceType.UPDATE_STATS.getValue();
    }
}
