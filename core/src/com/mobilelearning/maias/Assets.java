package com.mobilelearning.maias;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.mobilelearning.maias.questionHandling.QuestionDB;
import com.mobilelearning.maias.uiUtils.Collection;
import com.mobilelearning.maias.uiUtils.RgbColor;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created with IntelliJ IDEA.
 * User: AFFonseca
 * Date: 12-01-2015
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
public class Assets {

    public static String currentVersion = "1.0";
    public static RgbColor prettyRed = new RgbColor(199, 35, 34, 255);
    public static RgbColor beige = new RgbColor(255,168,124, 255);

    public static QuestionDB questionDB;
    public static int numberOfThemes= 7;
    public static int maxDifficulty = 6;

    public static Collection collection;
    public static int numberOfStickers = 61;
    public static Animation lisbonAnimation;

    //Global Assets
    public static Skin uiSkin;
    public static TextureAtlas miscellaneous;
    public static Animation loading;

    //Sound Assets
    public static Sound clickFX;
    public static Sound successFX;
    public static Sound failFX;
    public static Sound moneyFX;
    public static Sound fanfareFX;

    private static GameAssetManager manager;

    public static void  loadStartupAssets(){
        manager = new GameAssetManager();

        manager.gLoad("ui/startup.atlas", TextureAtlas.class);
        manager.gLoad("ui/uiskin.json", Skin.class);
        manager.gLoad("animations/loadingAnimation.atlas", TextureAtlas.class);
        manager.gLoad("sounds/clickFX.ogg", Sound.class);

        manager.finishLoading();

        final TextureAtlas loadingAtlas = manager.get("animations/loadingAnimation.atlas", TextureAtlas.class);
        loadingAtlas.getRegions().sort(new Comparator<TextureAtlas.AtlasRegion>() {
            @Override
            public int compare(TextureAtlas.AtlasRegion o1, TextureAtlas.AtlasRegion o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        loading = new Animation(1 / 15f, loadingAtlas.getRegions());
        loading.setPlayMode(Animation.PlayMode.LOOP);
        uiSkin = manager.get("ui/uiskin.json", Skin.class);
        clickFX = manager.get("sounds/clickFX.ogg", Sound.class);

    }

    public static void loadGlobalAssets() {
        //Questions Database
        questionDB = new QuestionDB();

        //basic elements
        manager.gLoad("ui/miscellaneous.atlas", TextureAtlas.class);

        //animations
        manager.gLoad("animations/lisbonAnimation.atlas", TextureAtlas.class);

        //UI
        manager.gLoad("ui/mainMenu.atlas", TextureAtlas.class);
        manager.gLoad("ui/trainingMenu.atlas", TextureAtlas.class);
        manager.gLoad("ui/challengeMenu.atlas", TextureAtlas.class);
        manager.gLoad("ui/gameScreen.atlas", TextureAtlas.class);
        manager.gLoad("ui/collection.atlas", TextureAtlas.class);
        manager.gLoad("ui/stickers/stickers.atlas", TextureAtlas.class);

        //Sound
        manager.gLoad("sounds/moneyFX.ogg", Sound.class);
        manager.gLoad("sounds/fanfareFX.ogg", Sound.class);
        manager.gLoad("sounds/successFX.ogg", Sound.class);
        manager.gLoad("sounds/failFX.ogg", Sound.class);
    }

    public static void prepareGlobalAssets(){
        uiSkin = manager.get("ui/uiskin.json", Skin.class);
        miscellaneous = manager.get("ui/miscellaneous.atlas", TextureAtlas.class);

        //Sounds
        moneyFX = manager.get("sounds/moneyFX.ogg", Sound.class);
        fanfareFX = manager.get("sounds/fanfareFX.ogg", Sound.class);
        successFX = manager.get("sounds/successFX.ogg", Sound.class);
        failFX = manager.get("sounds/failFX.ogg", Sound.class);

        final TextureAtlas lisbonAtlas = manager.get("animations/lisbonAnimation.atlas", TextureAtlas.class);
        lisbonAtlas.getRegions().sort(new Comparator<TextureAtlas.AtlasRegion>() {
            @Override
            public int compare(TextureAtlas.AtlasRegion o1, TextureAtlas.AtlasRegion o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        lisbonAnimation = new Animation(1 / 10f, lisbonAtlas.getRegions());
        lisbonAnimation.setPlayMode(Animation.PlayMode.LOOP);

        collection = new Collection();

    }

    public static void loadStartupScreen(){
        manager.gLoad("ui/startup.atlas", TextureAtlas.class);
    }

    public static TextureAtlas prepareStartupScreen(){
        return manager.get("ui/startup.atlas", TextureAtlas.class);
    }

    public static void unloadStartupScreen(){
        manager.gUnload("ui/startup.atlas");
    }

    public static TextureAtlas prepareMainMenu(){
        return manager.get("ui/mainMenu.atlas", TextureAtlas.class);
    }

    public static TextureAtlas prepareTrainingMenu(){
        return manager.get("ui/trainingMenu.atlas", TextureAtlas.class);
    }

    public static TextureAtlas prepareChallengeMenu(){
        return manager.get("ui/challengeMenu.atlas", TextureAtlas.class);
    }

    public static TextureAtlas prepareGameScreen(){
        return manager.get("ui/gameScreen.atlas", TextureAtlas.class);
    }

    public static TextureAtlas prepareCollection(){
        return manager.get("ui/collection.atlas", TextureAtlas.class);
    }

    public static TextureAtlas prepareStickers(){
        return manager.get("ui/stickers/stickers.atlas", TextureAtlas.class);
    }

    public static boolean update(){
        return manager.update();
    }

    public static void dispose(){
        if(manager != null)
            manager.dispose();
    }

    public static class GameAssetManager extends AssetManager {
        ObjectMap<String, AtomicInteger> usages;

        public GameAssetManager() {
            super();
            usages = new ObjectMap<>();
        }

        public synchronized <T> void gLoad(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
            if(!usages.containsKey(fileName)){
                usages.put(fileName, new AtomicInteger(0));
                super.load(fileName, type, parameter);
            }
            usages.get(fileName).addAndGet(1);
        }

        public synchronized <T> void gLoad(String fileName, Class<T> type) {
            gLoad(fileName, type, null);
        }

        public synchronized void gUnload(String fileName) {
            if(usages.containsKey(fileName)){
                if(usages.get(fileName).addAndGet(-1) == 0){
                    usages.remove(fileName);
                    super.unload(fileName);
                }
            }
        }
    }

}
