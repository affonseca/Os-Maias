package com.mobilelearning.maias.serviceHandling.json;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;

/**
 * Created with IntelliJ IDEA.
 * User: AFFonseca
 * Date: 10-02-2015
 * Time: 15:17
 * To change this template use File | Settings | File Templates.
 */
public class ClassArray {
    public Array<ClassInfo> classes;

    public static ClassArray load (JsonValue fatherTree) {
        try {
            Json json = new Json();
            return json.readValue(ClassArray.class, fatherTree.get("classArray"));
        } catch (SerializationException ex) {
            throw new SerializationException("Error reading classArray from Json" , ex);
        }
    }
}
