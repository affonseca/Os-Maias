package com.mobilelearning.maias.serviceHandling.json;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.SerializationException;
import com.mobilelearning.maias.Assets;

/**
 * Created by AFFonseca on 16/07/2015.
 */
public class StatsData {
    public int money, challengeProgress, challengeScore;
    public long difficulty1Counter;
    public float difficulty1Score;
    public long difficulty2Counter;
    public float difficulty2Score;
    public long difficulty3Counter;
    public float difficulty3Score;
    public long difficulty4Counter;
    public float difficulty4Score;
    public long theme1Counter;
    public float theme1Score;
    public long theme2Counter;
    public float theme2Score;
    public long theme3Counter;
    public float theme3Score;
    public long theme4Counter;
    public float theme4Score;
    public long theme5Counter;
    public float theme5Score;
    public long theme6Counter;
    public float theme6Score;
    public long theme7Counter;
    public float theme7Score;
    public int collectionData [];

    public static StatsData load (JsonValue fatherTree) {
        try {
            Json json = new Json();
            String jsonStatsData = fatherTree.get("statsData")
                    .prettyPrint(JsonWriter.OutputType.minimal, 0).replace("\"", "");
            StatsData statsData =  json.fromJson(StatsData.class, jsonStatsData);

            if(statsData.collectionData == null){
                statsData.collectionData = new int[Assets.numberOfStickers];
                for(int i=0; i<statsData.collectionData.length; i++)
                    statsData.collectionData[i] = 0;
            }
            return statsData;
        } catch (Exception ex) {
            throw new SerializationException("Error reading scoreData from Json" , ex);
        }
    }
}
