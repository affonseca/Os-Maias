package com.mobilelearning.maias.questionHandling;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.mobilelearning.maias.Assets;

/**
 * Created by AFFonseca on 03/08/2015.
 */
public class QuestionDB {
    private ObjectMap<String, Array<Array<Question>>> themesAndDifficulties;
    private String [] themeIndex;

    public QuestionDB() {
        themesAndDifficulties = new ObjectMap<>();
        themeIndex = new String[Assets.numberOfThemes];

        FileHandle file = Gdx.files.internal("questions.json");
        String jsonQuestions = file.readString();

        Json json = new Json();
        QuestionArray questionArray = json.fromJson(QuestionArray.class, jsonQuestions);

        //Creating the DB
        int counter = 0;
        for(Question question : questionArray.questions){

            //If the theme is new, create one entry.
            if(!themesAndDifficulties.containsKey(question.theme)){
                Array<Array<Question>> newEntry = new Array<>();
                for(int i=0; i< Assets.maxDifficulty; i++){
                    newEntry.add(new Array<Question>());
                }
                themesAndDifficulties.put(question.theme, newEntry);

                //Assuming themes are ordered, at least for the first set of questions
                themeIndex[counter++] = question.theme;
            }
            //Adding question to the right place
            themesAndDifficulties.get(question.theme).get(question.difficulty-1).add(question);
        }


        //Debugging code
        /*for(String key: themesAndDifficulties.keys().toArray()){
            System.out.println("Found theme named \"" +key + "\"");
        }*/
        /*for(String key: themeIndex){
            System.out.println("Found theme named \"" +key + "\"");
        }*/
    }

    public Question getLongestQuestion(){
        int max = 0;
        Question out = null;

        /*for(Question question : questionArray.questions){
            if(question.question.length() > max){
                out = question;
                max = question.question.length();
            }
        }*/
        /*for(Question question : questionArray.questions){
            for(String wrongChoice :question.wrongChoices) {
                if (wrongChoice.length() > max) {
                    max = wrongChoice.length();
                    out = question;
                }
            }
        }*/
        /*for(Question question : questionArray.questions){
            if(question.commentaryCorrect.length() > max){
                out = question;
                max = question.commentaryCorrect.length();
            }
            if(question.commentaryWrong.length() > max){
                out = question;
                max = question.commentaryWrong.length();
            }
        }*/

        return out;
    }


    public Array<Question> getChallengeQuestions(int [] difficulties, int numberOfQuestions){
        int startingNumberOfQuestionForEachDifficulty = numberOfQuestions/difficulties.length;
        int overflow = numberOfQuestions%difficulties.length;
        if(overflow != 0)
            startingNumberOfQuestionForEachDifficulty++;
        int numberOfQuestionPerDifficulty[] = new int[difficulties.length];
        for(int i=0; i<difficulties.length; i++){
            numberOfQuestionPerDifficulty[i] = startingNumberOfQuestionForEachDifficulty;
        }

        //Calculating new overflow and removing questions until correct
        overflow = startingNumberOfQuestionForEachDifficulty*difficulties.length - numberOfQuestions;
        for(int i=0; i<overflow; i++){
            numberOfQuestionPerDifficulty[MathUtils.random(0, numberOfQuestionPerDifficulty.length-1)]--;
        }

        //Creating the questions. Only one for each pair theme/difficulty
        Array<Question> out = new Array<>();
        for(int i=0; i<numberOfQuestionPerDifficulty.length; i++){
            Array<Integer> themes = new Array<>(new Integer[]{0, 1, 2, 3, 4, 5, 6});
            for(int j=0; j<numberOfQuestionPerDifficulty[i]; j++) {
                int index = MathUtils.random(0, themes.size - 1);
                out.addAll(nonRepeatedArrayOfQuestion(themes.get(index), difficulties[i], 1));
                themes.removeIndex(index);
            }
        }
        if(out.size != numberOfQuestions)
            return null;
        return out;

    }

    public Array<Question> getThemeQuestions(int themeValue){

        //Most will have 3 except 2 or in the worst case 1.
        int numberOfQuestionPerDifficulty[] = {3, 3, 3, 3};
        numberOfQuestionPerDifficulty[MathUtils.random(0, numberOfQuestionPerDifficulty.length-1)]--;
        numberOfQuestionPerDifficulty[MathUtils.random(0, numberOfQuestionPerDifficulty.length-1)]--;

        Array<Question> out = new Array<>();
        for(int i=0; i<numberOfQuestionPerDifficulty.length; i++){
            out.addAll(nonRepeatedArrayOfQuestion(themeValue, i, numberOfQuestionPerDifficulty[i]));
        }
        if(out.size != 10)
            return null;
        return out;
    }

    private Array<Question> nonRepeatedArrayOfQuestion(int themeValue, int difficultyValue, int arraySize){
        Array<Question> originalPool = themesAndDifficulties.get(themeIndex[themeValue]).get(difficultyValue);

        Array<Question> poolCopy = new Array<>(originalPool);
        Array<Question> out = new Array<>();
        for (int i=0; i<arraySize && poolCopy.size!=0; i++){
            int index = MathUtils.random(0, poolCopy.size-1);
            out.add(poolCopy.get(index));
            poolCopy.removeIndex(index);
        }
        return out;
    }

    public Question replaceQuestion(Question oldQuestion){
        Array<Question> originalPool = themesAndDifficulties.get(oldQuestion.theme).get(oldQuestion.difficulty-1);
        Array<Question> poolCopy = new Array<>(originalPool);
        poolCopy.removeValue(oldQuestion, false);

        return poolCopy.get(MathUtils.random(0, poolCopy.size-1));
    }
}
