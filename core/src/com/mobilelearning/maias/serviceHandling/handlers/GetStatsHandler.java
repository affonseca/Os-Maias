package com.mobilelearning.maias.serviceHandling.handlers;

import com.mobilelearning.maias.serviceHandling.json.StatsData;

/**
 * Created by AFFonseca on 16/07/2015.
 */
public interface GetStatsHandler {

    void onGetStatsSuccess(StatsData response);
    void onGetStatsError(String error);

}
