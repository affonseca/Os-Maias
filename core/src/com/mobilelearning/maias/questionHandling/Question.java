package com.mobilelearning.maias.questionHandling;

import com.badlogic.gdx.utils.Array;
import com.mobilelearning.maias.serviceHandling.json.ClassInfo;

/**
 * Created by AFFonseca on 03/08/2015.
 */
public class Question {
    public String  question;
    public int difficulty;
    public String theme;
    public String rightChoice;
    public Array<String> wrongChoices;
    public String commentaryCorrect;
    public String commentaryWrong;

}