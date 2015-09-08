package com.mobilelearning.maias.serviceHandling.handlers;

import com.mobilelearning.maias.serviceHandling.json.LeaderboardScoresData;

/**
 * Created by AFFonseca on 23/07/2015.
 */
public interface GetLeaderboardScoresHandler {

    void onGetLeaderboardScoresSuccess(LeaderboardScoresData response);

    void onGetLeaderboardScoresClassError();

    void onGetLeaderboardScoresUserError();

    void onGetLeaderboardScoresError(String error);
}
