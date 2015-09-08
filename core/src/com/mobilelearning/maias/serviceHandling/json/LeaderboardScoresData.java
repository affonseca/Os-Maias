package com.mobilelearning.maias.serviceHandling.json;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;

/**
 * Created by AFFonseca on 23/07/2015.
 */
public class LeaderboardScoresData {
    public Array<LeaderboardScore> leaderboardScores;
    public LeaderboardPosition leaderboardPosition;

    public static LeaderboardScoresData load (JsonValue fatherTree) {
        try {
            Json json = new Json();
            return json.readValue(LeaderboardScoresData.class, fatherTree.get("leaderboardScoresData"));
        } catch (SerializationException ex) {
            throw new SerializationException("Error reading leaderboardScoresArray from Json" , ex);
        }
    }
}
