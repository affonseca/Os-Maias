package com.mobilelearning.maias;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.mobilelearning.maias.screens.GameScreen;
import com.mobilelearning.maias.serviceHandling.json.*;

/**
 * Created with IntelliJ IDEA.
 * User: AFFonseca
 * Date: 26-01-2015
 * Time: 18:02
 * To change this template use File | Settings | File Templates.
 */
public class SavedData {

    private enum Values{

        DATA_NAME("MobileLearning"),
        TOKEN("token"),
        USERNAME("username"),
        USER_ID("userID"),
        CURRENT_CLASS_NAME("currentClassName"),
        CURRENT_CLASS_TEACHER("currentClassTeacher"),
        CURRENT_CLASS_ID("currentClassID"),
        MONEY("money"),
        CHALLENGE_SCORE("challengeScore"),
        THEME1_SCORE("theme1Score"), THEME1_COUNTER("theme1Counter"),
        THEME2_SCORE("theme2Score"), THEME2_COUNTER("theme2Counter"),
        THEME3_SCORE("theme3Score"), THEME3_COUNTER("theme3Counter"),
        THEME4_SCORE("theme4Score"), THEME4_COUNTER("theme4Counter"),
        THEME5_SCORE("theme5Score"), THEME5_COUNTER("theme5Counter"),
        THEME6_SCORE("theme6Score"), THEME6_COUNTER("theme6Counter"),
        THEME7_SCORE("theme7Score"), THEME7_COUNTER("theme7Counter"),
        DIFFICULTY1_SCORE("difficulty1Score"), DIFFICULTY1_COUNTER("difficulty1Counter"),
        DIFFICULTY2_SCORE("difficulty2Score"), DIFFICULTY2_COUNTER("difficulty2Counter"),
        DIFFICULTY3_SCORE("difficulty3Score"), DIFFICULTY3_COUNTER("difficulty3Counter"),
        DIFFICULTY4_SCORE("difficulty4Score"), DIFFICULTY4_COUNTER("difficulty4Counter"),
        CHALLENGE_PROGRESS("challengeProgress"), COLLECTION_DATA("collectionData"),
        CHALLENGE_SAVE("challengeSave");


        private String value;

        Values(String value) {
            this.value = value;
        }

        private String getValue(){
            return value;
        }

    }

    private static Preferences userData;

    public static void loadSavedData(){
        userData = Gdx.app.getPreferences(Values.DATA_NAME.getValue());
    }

    public static String getToken(){
        return userData.getString(Values.TOKEN.getValue(), null);
    }

    public static String getUsername(){
        return userData.getString(Values.USERNAME.getValue(), null);
    }

    public static String getUserID(){
        return userData.getString(Values.USER_ID.getValue(), null);
    }

    public static String getCurrentClassName(){
        return userData.getString(Values.CURRENT_CLASS_NAME.getValue(), null);
    }

    public static String getCurrentClassTeacher(){
        return userData.getString(Values.CURRENT_CLASS_TEACHER.getValue(), null);
    }

    public static String getCurrentClassID(){
        return userData.getString(Values.CURRENT_CLASS_ID.getValue(), null);
    }

    public static void setUserData(UserData user){
        userData.putString(Values.USER_ID.getValue(), "" +user.ID);
        userData.putString(Values.USERNAME.getValue(), "" +user.username);
        userData.putString(Values.TOKEN.getValue(), "" +user.hash);
        userData.flush();
    }

    public static void setCurrentClass(ClassInfo newClass){
        userData.putString(Values.CURRENT_CLASS_ID.getValue(), "" +newClass.classID);
        userData.putString(Values.CURRENT_CLASS_NAME.getValue(), "" +newClass.className);
        userData.putString(Values.CURRENT_CLASS_TEACHER.getValue(), "" + newClass.classTeacher);
        userData.flush();
    }

    public static boolean setChallengeProgress(int challengeProgress){
        if(challengeProgress > userData.getInteger(Values.CHALLENGE_PROGRESS.getValue(), 0)){
            userData.putInteger(Values.CHALLENGE_PROGRESS.getValue(), challengeProgress);
            return true;
        }
        return false;
    }

    public static void setChallengeSave(GameScreen.ChallengeSave challengeSave){
        if(challengeSave == null){
            userData.remove(Values.CHALLENGE_SAVE.getValue());
        }

        Json json = new Json();
        String jsonChallengeSave = json.toJson(challengeSave);
        userData.putString(Values.CHALLENGE_SAVE.getValue(), jsonChallengeSave);
        userData.flush();
    }

    public static void setStats(StatsData stats){
        userData.putInteger(Values.CHALLENGE_SCORE.getValue(), stats.challengeScore);

        userData.putInteger(Values.MONEY.getValue(), stats.money);
        userData.putInteger(Values.CHALLENGE_PROGRESS.getValue(), stats.challengeProgress);
        userData.putFloat(Values.THEME1_SCORE.getValue(), stats.theme1Score);
        userData.putLong(Values.THEME1_COUNTER.getValue(), stats.theme1Counter);
        userData.putFloat(Values.THEME2_SCORE.getValue(), stats.theme2Score);
        userData.putLong(Values.THEME2_COUNTER.getValue(), stats.theme2Counter);
        userData.putFloat(Values.THEME3_SCORE.getValue(), stats.theme3Score);
        userData.putLong(Values.THEME3_COUNTER.getValue(), stats.theme3Counter);
        userData.putFloat(Values.THEME4_SCORE.getValue(), stats.theme4Score);
        userData.putLong(Values.THEME4_COUNTER.getValue(), stats.theme4Counter);
        userData.putFloat(Values.THEME5_SCORE.getValue(), stats.theme5Score);
        userData.putLong(Values.THEME5_COUNTER.getValue(), stats.theme5Counter);
        userData.putFloat(Values.THEME6_SCORE.getValue(), stats.theme6Score);
        userData.putLong(Values.THEME6_COUNTER.getValue(), stats.theme6Counter);
        userData.putFloat(Values.THEME7_SCORE.getValue(), stats.theme7Score);
        userData.putLong(Values.THEME7_COUNTER.getValue(), stats.theme7Counter);

        userData.putFloat(Values.DIFFICULTY1_SCORE.getValue(), stats.difficulty1Score);
        userData.putLong(Values.DIFFICULTY1_COUNTER.getValue(), stats.difficulty1Counter);
        userData.putFloat(Values.DIFFICULTY2_SCORE.getValue(), stats.difficulty2Score);
        userData.putLong(Values.DIFFICULTY2_COUNTER.getValue(), stats.difficulty2Counter);
        userData.putFloat(Values.DIFFICULTY3_SCORE.getValue(), stats.difficulty3Score);
        userData.putLong(Values.DIFFICULTY3_COUNTER.getValue(), stats.difficulty3Counter);
        userData.putFloat(Values.DIFFICULTY4_SCORE.getValue(), stats.difficulty4Score);
        userData.putLong(Values.DIFFICULTY4_COUNTER.getValue(), stats.difficulty4Counter);

        userData.putString(Values.COLLECTION_DATA.getValue(), new Json().toJson(stats.collectionData));

        userData.flush();
    }

    public static StatsData getStats(){
        StatsData out = new StatsData();

        out.challengeScore = userData.getInteger(Values.CHALLENGE_SCORE.getValue(), 0);

        out.money = userData.getInteger(Values.MONEY.getValue(), 0);
        out.challengeProgress = userData.getInteger(Values.CHALLENGE_PROGRESS.getValue(), 0);
        out.theme1Score = userData.getFloat(Values.THEME1_SCORE.getValue(), 0);
        out.theme1Counter = userData.getLong(Values.THEME1_COUNTER.getValue(), 0);
        out.theme2Score = userData.getFloat(Values.THEME2_SCORE.getValue(), 0);
        out.theme2Counter = userData.getLong(Values.THEME2_COUNTER.getValue(), 0);
        out.theme3Score = userData.getFloat(Values.THEME3_SCORE.getValue(), 0);
        out.theme3Counter = userData.getLong(Values.THEME3_COUNTER.getValue(), 0);
        out.theme4Score = userData.getFloat(Values.THEME4_SCORE.getValue(), 0);
        out.theme4Counter = userData.getLong(Values.THEME4_COUNTER.getValue(), 0);
        out.theme5Score = userData.getFloat(Values.THEME5_SCORE.getValue(), 0);
        out.theme5Counter = userData.getLong(Values.THEME5_COUNTER.getValue(), 0);
        out.theme6Score = userData.getFloat(Values.THEME6_SCORE.getValue(), 0);
        out.theme6Counter = userData.getLong(Values.THEME6_COUNTER.getValue(), 0);
        out.theme7Score = userData.getFloat(Values.THEME7_SCORE.getValue(), 0);
        out.theme7Counter = userData.getLong(Values.THEME7_COUNTER.getValue(), 0);

        out.difficulty1Score = userData.getFloat(Values.DIFFICULTY1_SCORE.getValue(), 0);
        out.difficulty1Counter = userData.getLong(Values.DIFFICULTY1_COUNTER.getValue(), 0);
        out.difficulty2Score = userData.getFloat(Values.DIFFICULTY2_SCORE.getValue(), 0);
        out.difficulty2Counter = userData.getLong(Values.DIFFICULTY2_COUNTER.getValue(), 0);
        out.difficulty3Score = userData.getFloat(Values.DIFFICULTY3_SCORE.getValue(), 0);
        out.difficulty3Counter = userData.getLong(Values.DIFFICULTY3_COUNTER.getValue(), 0);
        out.difficulty4Score = userData.getFloat(Values.DIFFICULTY4_SCORE.getValue(), 0);
        out.difficulty4Counter = userData.getLong(Values.DIFFICULTY4_COUNTER.getValue(), 0);

        String jsonCollectionData =userData.getString(Values.COLLECTION_DATA.getValue());
        out.collectionData = new Json().fromJson(int[].class, jsonCollectionData);
        return out;
    }

    public static int getChallengeProgress(){
        return userData.getInteger(Values.CHALLENGE_PROGRESS.getValue(), 0);
    }

    public static GameScreen.ChallengeSave getChallengeSave(){
        String jsonChallengeSave = userData.getString(Values.CHALLENGE_SAVE.getValue(), null);
        if(jsonChallengeSave == null)
            return null;

        Json json = new Json();
        return json.fromJson(GameScreen.ChallengeSave.class, jsonChallengeSave);
    }

    public static void clearUserData(){
        userData.clear();
        userData.flush();
    }

}
