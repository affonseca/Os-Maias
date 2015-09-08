package com.mobilelearning.maias.serviceHandling.services;

import com.badlogic.gdx.utils.JsonValue;

/**
 * Created with IntelliJ IDEA.
 * User: AFFonseca
 * Date: 26-01-2015
 * Time: 17:38
 * To change this template use File | Settings | File Templates.
 */
public interface Service {

    public void onRequestSuccess(JsonValue response);

    public void onRequestFailure(String error);

    public String getType();

    public enum ServiceType{

        //All different Services
        LOGIN("login"),
        REGISTER("register"),
        OTHER_CLASSES("otherClasses"),
        USER_CLASSES("userClasses"),
        JOIN_CLASS("requestJoinClass"),
        GET_STATS("getStats"),
        UPDATE_STATS("updateStats"),
        GET_LEADERBORADS_SCORES("getLeaderboardScores");

        private String value;

        private ServiceType(String value) {
            this.value = value;
        }

        public String getValue(){
            return value;
        }

    }

}
