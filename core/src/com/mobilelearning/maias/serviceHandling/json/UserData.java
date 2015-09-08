package com.mobilelearning.maias.serviceHandling.json;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;

/**
 * Created with IntelliJ IDEA.
 * User: AFFonseca
 * Date: 26-01-2015
 * Time: 12:33
 * To change this template use File | Settings | File Templates.
 */
public class UserData{
    public long ID;
    public String username;
    public String hash;


    public static UserData load (JsonValue fatherTree) {
        try {
            Json json = new Json();
            return json.readValue(UserData.class, fatherTree.get("userData"));
        } catch (SerializationException ex) {
            throw new SerializationException("Error reading userData from Json" , ex);
        }
    }
}
