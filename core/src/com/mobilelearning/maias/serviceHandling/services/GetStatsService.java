package com.mobilelearning.maias.serviceHandling.services;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;
import com.mobilelearning.maias.serviceHandling.Errors;
import com.mobilelearning.maias.serviceHandling.RequestException;
import com.mobilelearning.maias.serviceHandling.ServiceRequester;
import com.mobilelearning.maias.serviceHandling.handlers.GetStatsHandler;
import com.mobilelearning.maias.serviceHandling.json.StatsData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AFFonseca on 16/07/2015.
 */
public class GetStatsService implements Service {

    private GetStatsHandler handler;


    public GetStatsService(GetStatsHandler handler) {
        this.handler = handler;
    }

    public void requestStats(long classID) {

        Map<String, String> parameters = new HashMap<>();

        parameters.put("classID", "" +classID);

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
        StatsData out;

        try{
            out = StatsData.load(response);
        }
        catch (SerializationException ex){
            onRequestFailure(Errors.UNKNOWN_ERROR.getValue());
            return;
        }

        handler.onGetStatsSuccess(out);

    }

    @Override
    public void onRequestFailure(String error) {
        handler.onGetStatsError(error);
    }

    @Override
    public String getType() {
        return ServiceType.GET_STATS.getValue();
    }
}
