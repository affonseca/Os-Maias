package com.mobilelearning.maias.serviceHandling.services;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;
import com.mobilelearning.maias.serviceHandling.Errors;
import com.mobilelearning.maias.serviceHandling.RequestException;
import com.mobilelearning.maias.serviceHandling.ServiceRequester;
import com.mobilelearning.maias.serviceHandling.handlers.GetLeaderboardScoresHandler;
import com.mobilelearning.maias.serviceHandling.json.LeaderboardScoresData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AFFonseca on 16/07/2015.
 */
public class GetLeaderboardScoresService implements Service {

    private GetLeaderboardScoresHandler handler;


    public GetLeaderboardScoresService(GetLeaderboardScoresHandler handler) {
        this.handler = handler;
    }

    public void requestLeaderboardScores(long classID, int challengeScore) {

        Map<String, String> parameters = new HashMap<>();

        parameters.put("classID", "" +classID);
        parameters.put("challengeScore", "" +challengeScore);

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
        LeaderboardScoresData out;

        try{
            out = LeaderboardScoresData.load(response);
        }
        catch (SerializationException ex){
            onRequestFailure(Errors.UNKNOWN_ERROR.getValue());
            return;
        }

        handler.onGetLeaderboardScoresSuccess(out);
    }

    @Override
    public void onRequestFailure(String error) {
        switch (error) {
            case "A turma selecionada já não existe":
                handler.onGetLeaderboardScoresClassError();
                break;
            case "Já não pertences à turma selecionada":
                handler.onGetLeaderboardScoresUserError();
                break;
            default:
                handler.onGetLeaderboardScoresError(error);
        }
    }

    @Override
    public String getType() {
        return ServiceType.GET_LEADERBORADS_SCORES.getValue();
    }
}
