package com.gamelattice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResults;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.font.BitmapText;
import com.jme3.input.controls.TouchListener;
import com.jme3.input.controls.TouchTrigger;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.light.SpotLight;
import com.jme3.math.FastMath;
import com.jme3.system.AppSettings;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.yodo1.mas.Yodo1Mas;
import com.yodo1.mas.error.Yodo1MasError;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import static android.content.Context.MODE_PRIVATE;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class GameStart extends SimpleApplication {

    private static final java.util.UUID UUID = null;
    private AppSettings settings;
    private Lattice lattice;
    protected static Context ctx;
    private NetworkGame networkGame;
    private MyGame aigame;
    private MyGame mygame;
    private ArrayList<JmeCursor> cursors = new ArrayList<>();
    private Checkers checkers;
    private GameData  gd;
    protected static BitmapText bmText, infoText;
    protected static ColorRGBA hod, menuSelectedColor;
    static protected boolean picking, stop, soundOn, answerYesNo, printMessage;
    protected static int screenHeight, screenWidth;
    static protected SoundAndroid soundEffects;
    //static protected
    protected static ColorRGBA mycolor, aicolor;
    private Joint pickedJoint;//, overJoint;
    protected Ball pickedBall ;
    private Checker pickedChecker;//, overChecker;
    private TextMesh tm ;
    private MenuControl menuControl;
    private GameControl gameControl;
    private GameHistory gameHistory;
    private VisualEffects ve;
    private Node explosionEffect = new Node("explosionFX");
    final private ArrayList<String> beamNames= new ArrayList<>();
    final private ArrayList<Geometry> jointNames= new ArrayList<>();
    private ArrayList<Checker> mycheckers, aicheckers;
    private Quaternion qs;
    private Geometry closestCollisionGeometry;
    private Geometry specularBall, specularJoint;
    private double updateTime, startTime, showControl, showMenu, showHistory;
    static protected double  beamTime, tTime, hintTime, timeFrame;
    static protected double breakTime, shiveringTime, birthTime, soundPlayTime, soundPausedTime;
    protected static float soundVolume = 0.9f, deltaVolume = -0.1f;
    static private int dimensionX, dimensionY, dimensionZ, soundRepeator, smLevel, gameMode ;
    static String[] gameModeName;  // for saveGame in gameData
    private float beamRadius = 1;
    protected static String firstMove;
    private boolean showHist, actions, latticeToStart, savingFile;
    protected static boolean netMode, localMode, aiMode, demoMode, loadMode; // // added in version 2
    protected static boolean isGameGoing;

    public GameStart()  {
    }
    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void simpleInitApp() {
        inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_MEMORY);
        stateManager.detach( stateManager.getState(FlyCamAppState.class)); //remove standart mouse control
        //stateManager.detach( stateManager.getState(DebugKeysAppState.class)); //remove standart mouse control
        inputManager.deleteMapping( SimpleApplication.INPUT_MAPPING_MEMORY );
        inputManager.addMapping("Touch", new TouchTrigger(0));
        inputManager.addListener(touchListener, new String[]{"Touch"});
        Geometry specularBall = new Geometry();
        setDisplayStatView(false);
        setDisplayFps(false);

        settings = new AppSettings(true);
        screenHeight=this.context.getSettings().getHeight();
        screenWidth=this.context.getSettings().getWidth();
        settings.setTitle("lattice");
        this.setSettings(settings);
        ve = new VisualEffects(assetManager);
        ve.createFlame();
        ve.createDebris();
        closestCollisionGeometry = new Geometry();
        isGameGoing = false;
        stop = false;
        latticeToStart = false;
        savingFile=false;
        loadMode = false;
        demoMode = false;// added in version  2
        aiMode = false;
        localMode = false;
        netMode = false;// added in version 2
        gameMode = 2;
        gameModeName = new String[]{"net","loc","ai","demo"};
        showHist = true;
        speed = 0.8f;
        updateTime = 400;
        startTime = System.currentTimeMillis();
        soundPlayTime = 5000;
        soundPausedTime = System.currentTimeMillis();
        timeFrame =  System.currentTimeMillis();
        soundOn = true;
        printMessage = true;
        tTime = startTime;
        cursors = new ArrayList<>();
        mycolor = new ColorRGBA();
        aicolor = new ColorRGBA();
        menuSelectedColor = new ColorRGBA(1, 1, 5, 1);
        hod = ColorRGBA.White;
        gameControl = new GameControl(assetManager);
        gameHistory = new GameHistory(assetManager);
        // Initialize the globals access so that the defualt
        // components can find what they need.
        ctx = ((GlobalApplication) ctx).getAppContext();
        String paknam = ctx.getApplicationContext().getPackageName();
        try {
            soundEffects = new SoundAndroid(ctx.getAssets());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //rootNode.attachChild(soundEffects.audioNode);
        guiFont = assetManager.loadFont("Interface/Fonts/ComicSansMS.fnt");
        bmText = new BitmapText(guiFont, false);
        infoText = new BitmapText(guiFont, false);
        setGameText(); //sets font for bmText
        guiNode.attachChild(bmText);
        networkGame = new NetworkGame(bmText, guiNode, ctx);
        gd = new GameData(ctx);
        menuControl = new MenuControl(assetManager, gd, ctx);
        menuControl.menuNode.attachChild(menuControl.menu.geom);
        //readDeafaulSettings();
            try {
                readSettings();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        menuControl.getMenuElementByValue(String.valueOf(dimensionX)
               + String.valueOf(dimensionY) + String.valueOf(dimensionZ)).selected = true;
        menuControl.getSelectedElementByColor(mycolor, "my").selected = true;
        menuControl.getSelectedElementByColor(aicolor, "ai").selected = true;
        menuControl.getMenuElementByValue("level" + String.valueOf(smLevel)).selected = true;
        lattice = new Lattice(dimensionX, dimensionY, dimensionZ);
        lattice.buildLattice(assetManager);
        lattice.abcdLattice(assetManager);
        checkers = new Checkers(assetManager, dimensionX, dimensionY, dimensionZ);
        //tempCheckers = new ArrayList();
        mycheckers = new ArrayList();
        aicheckers = new ArrayList();

        rootNode.attachChild(menuControl.menuNode);
        checkers.builtCheckers(mycolor, aicolor, mycheckers, aicheckers);
        rootNode.attachChild(checkers.checkersNode);
        //rootNode.attachChild(lattice.buildLattice(assetManager));
        //rootNode.attachChild(lattice.abcdLattice(assetManager));
        rootNode.attachChild(checkers.benchNode);
        //lattice.latticeNode.attachChild(explosionEffect);
        lattice.latticeNode.attachChild(ve.explosionEffect);
        tm = new TextMesh(dimensionX, dimensionY, dimensionZ, assetManager, "tm");
        lattice.latticeNode.attachChild(tm.getTextNode());
        rootNode.attachChild(lattice.latticeNode);
        rootNode.attachChild(gameControl.controlNode);
        rootNode.attachChild(gameHistory.ballNode);
        rootNode.rotate(- 1.57f, 0, 0);
        qs = new Quaternion(lattice.latticeNode.getLocalRotation());

        aigame = new MyGame(mycheckers, aicheckers, dimensionX, dimensionY, dimensionZ,
                -1, assetManager, soundEffects,"ai", guiNode, guiFont);
        if (aiMode | netMode) aigame.auto = true;
        mygame = new MyGame(aicheckers, mycheckers, dimensionX, dimensionY, dimensionZ,
                1, assetManager, soundEffects,"my", guiNode, guiFont);
        mygame.smartLevel = smLevel;
        aigame.smartLevel = smLevel;
        //setGameData();
        setNodesPositions();
        turnLight();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void simpleUpdate(float tpf) {
        if(answerYesNo) {
            try {controlAnswerYesNo();}
            catch (FileNotFoundException e) {e.printStackTrace();}
        }
        if (networkGame.netgameInit ) {
            initNetGame();
            return;
        }
        if(SaveFileActivity.readyToSave) try {saveGame();} catch (FileNotFoundException e) {e.printStackTrace();}
        if(LoadFileActivity.readyToLoad)try {loadGame();} catch (FileNotFoundException e) {e.printStackTrace();}
        if (System.currentTimeMillis() - timeFrame < 10) return;
        //timeFrame = System.currentTimeMillis();
        actions = aigame.moveating | mygame.moveating | aigame.eating | aigame.eating | mygame.pickReturnJoint
                                                                                    |  aigame.pickReturnJoint;
        if (mygame.pickReturnJoint) {
            mygame.returnPickedCheckerToPlay(lattice, pickedJoint);
        }
        if (aigame.pickReturnJoint & !netMode) {  //added at version 2
            aigame.returnPickedCheckerToPlay(lattice, pickedJoint);
        }
        if (aigame.premoving & aigame.auto) {
            showCheckerToMove(aigame);
            return;
        }
        if (mygame.premoving & mygame.auto) {
            showCheckerToMove(mygame);
            return;
        }
        if (picking) {
            showPickedChecker();
            return;
        }
        if (aigame.moveating) {
            aigame.checkerToMove.slip(aigame.soundOn, soundEffects, soundVolume);
            aigame.moveating = aigame.checkerToMove.sliping;
            if(!aigame.moveating) {
                reccordHistory(aigame);
                return;
            }
        }
        if (mygame.moveating) {
            mygame.checkerToMove.slip(mygame.soundOn, soundEffects, soundVolume);
            mygame.moveating = mygame.checkerToMove.sliping;
            if(!mygame.moveating) {
                reccordHistory(mygame);  // line bellow added in version 2
                if (netMode) networkGame.playNetGame("write hod", mygame, aigame, lattice); //added at version 2
                return;
            }
        }

        if (aigame.eating) {
            aigame.checkerToBeEated.swell(tpf);
            if (aigame.checkerToBeEated.smash) makeDebris(aigame);
        }
        if (mygame.eating) {
            mygame.checkerToBeEated.swell(tpf);
            if (mygame.checkerToBeEated.smash) makeDebris(mygame);
        }

        if (mygame.birthing) {
            showBirth(mygame, tpf);
            if(!mygame.birthing & netMode) if (netMode) networkGame.playNetGame("write new", mygame, aigame, lattice); //added at version 2
            return;
        }
        if (aigame.birthing) {
            showBirth(aigame, tpf);
            return;
        }
        if (mygame.moving | aigame.moving | mygame.moveating | aigame.moveating |
                mygame.eating | aigame.eating) return;

        if (System.currentTimeMillis() - breakTime < 1000 ) return;//pause after move
        //overJoint();
        if (System.currentTimeMillis() - hintTime > 3000 & tm.hintOn) tm.hideJointHint();

        if (!aigame.movesAre & !mygame.movesAre & mygame.auto |
                !aigame.movesAre & !mygame.movesAre & !mygame.pickReturnJoint ) {// getCheckersInField(aicheckers) == 0 | getCheckersInField(mycheckers) == 0
            if (!answerYesNo) printGameResult();
            menuControl.unselectMenuGameModes();
            stop = true;
        }
        if (stop  & ! mygame.testing ) return;

        if (System.currentTimeMillis() - startTime > updateTime ) {//repeating of autogame
            startTime = System.currentTimeMillis();
            //System.out.println("netMode  " + netMode  );
            mygame.auto = !aigame.movesAre | demoMode;
            if(hod.equals(aicolor)) {  //hod ai
                if (netMode) networkGame.playNetGame("read ", mygame, aigame, lattice); // added at version 2
                else {
                    if(!aigame.auto) {
                        aigame.goMyGame(pickedJoint, pickedChecker);
                        printMessage(aigame);
                    }
                    else  {
                        aigame.autoGame();
                        printMessage(aigame);
                    }
                }
            }
            else {                     // hod my
                if (!mygame.auto)  {
                    mygame.goMyGame(pickedJoint, pickedChecker);
                    printMessage(mygame);
                }
                else {
                    mygame.autoGame();
                    printMessage(mygame);
                }  // finishing game
                if (!netMode) {
                }
                if (!mygame.movesAre & netMode & !mygame.pickReturnJoint) {  // added at version 2
                    networkGame.playNetGame("write no moves", mygame, aigame, lattice);
                }
            }
        }
    }


    private void selectChecker(String pickedGeomName, MyGame game) {
        if (pickedChecker !=null) {
            pickedChecker.resetColor();
            if (pickedJoint != null) {
                pickedJoint.resetColor();
                game.moveChecked = false;
            }
        }
        pickedChecker = game.getMyCheckerByGeometryName(pickedGeomName);
        if (pickedChecker.defaultcolor.equals(hod)) {
            picking = true;
            shiveringTime = System.currentTimeMillis();
            game.checkerChecked = true;
            game.checkerToMove = pickedChecker;
            //stop = false;
            if (game.soundOn) soundEffects.soundPool.play(soundEffects.pick_audio,soundVolume,soundVolume,0,0,1);
        }
        else {
            game.checkerChecked = false;
        }
    }

    private Geometry pickGeom(){
        CollisionResults results = new CollisionResults(); // Reset results list.
        Geometry target = null;
        // Convert screen click to 3d position
        Vector2f click2d = inputManager.getCursorPosition();
        //if (click2d.x < 100) comeBack(click2d);
        Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(click3d, dir);
        rootNode.collideWith(ray, results);
        if (results.size() > 0) target = results.getClosestCollision().getGeometry();
        return target;
    }

    private void overJoint() throws IllegalArgumentException{
        screenHeight=this.context.getSettings().getHeight();
        screenWidth=this.context.getSettings().getWidth();
        Vector2f cursor = inputManager.getCursorPosition();
        /*if (cursor.x < screenWidth * 0.12f) {
            showHistory = System.currentTimeMillis();
            rootNode.attachChild(gameHistory.ballNode);
           // inputManager.setMouseCursor(cursors.get(1));
        }
        //else inputManager.setMouseCursor(cursors.get(0));
        if (cursor.x < screenWidth * 0.7f  & cursor.x > screenWidth * 0.3f & cursor.y < screenHeight * 0.1f )  {
            rootNode.attachChild(gameControl.controlNode);
            showControl = System.currentTimeMillis();
        }
        if(cursor.x > screenWidth * 0.8f & cursor.y < screenHeight * 0.5f) {
            rootNode.attachChild(menuControl.menuNode);
            showMenu = System.currentTimeMillis();
        }*/
        Vector3f vect = cam.getWorldCoordinates(new Vector2f(cursor.x, cursor.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(cursor, 1f).subtractLocal(vect).normalizeLocal();
        CollisionResults results = new CollisionResults(); // Reset results list.
        String cl;

        rootNode.collideWith(new Ray(vect, dir), results); // Collect intersections between ray and all nodes in results list.
        if (results.size() > 0) {
            closestCollisionGeometry = results.getClosestCollision().getGeometry();
            cl = closestCollisionGeometry.getName();
            switch (cl.substring(0, 2)) {
                case "ch": case "me" : case "hi": case "co":
                    jointNames.add(closestCollisionGeometry);
                    closestCollisionGeometry.getMaterial().setFloat("Shininess", 8f);
                    if (jointNames.size() > 10) jointNames.remove(0);
                    break;
                case "jo":
                    jointNames.add(closestCollisionGeometry);
                    closestCollisionGeometry.getMaterial().setFloat("Shininess", 8f);
                    tm.showHint(lattice.joint
                            [Integer.valueOf(cl.substring(2, 3))]
                            [Integer.valueOf(cl.substring(3, 4))]
                            [Integer.valueOf(cl.substring(4))]);
                    if (jointNames.size() > 10) jointNames.remove(0);
                    break;
                case "be":
                    beamRadius = 0.02f;
                    beamNames.add(cl);
                    beamTime = System.currentTimeMillis();
                    lattice.getBeamByGeometryName(beamNames.get(beamNames.size() - 1))
                            .mesh.updateGeometry(6, 6, beamRadius, beamRadius, 2.8f, false, false);
                    if (beamNames.size() > 50) beamNames.remove(0);
                    break;
            }
        }
        else {
            try {
                if (beamRadius == 0.02f) {
                    for (String nm: beamNames) {
                        lattice.getBeamByGeometryName(nm).mesh.updateGeometry(6, 6, 0.04f, 0.04f, 2.8f, false, false);
                    }
                }
                for (Geometry g: jointNames) {
                    g.getMaterial().setFloat("Shininess", 128f);
                }
                tm.hideHint();
            }
            catch (IllegalArgumentException | NullPointerException e) {
                //System.out.println("No over object" + e);
            }
        }
    }

    private void setNodesPositions(){
        float x0 = cam.getWorldCoordinates(new Vector2f(1,1), 0.9009f).x;
        float z0 = cam.getWorldCoordinates(new Vector2f(1,1), 0.9009f).y;
        menuControl.menuNode.setLocalTranslation(-x0 - 1.5f, 0, z0 + 6);
        gameHistory.ballNode.setLocalTranslation(x0 + 1,0, z0   );
        gameControl.controlNode.setLocalTranslation(-x0 - 5, 0, z0 + 0.5f);
    }

    protected void restartGameFrom(String ge, Boolean restart){
        int i = Integer.valueOf(ge.substring(4, ge.indexOf("#")))-1;
        if(i < 2) return;
        stop = true;
        //checkers.killCheckers(mycheckers);
        //checkers.killCheckers(aicheckers);
        mycheckers.clear();
        aicheckers.clear();
        checkers.checkersNode.detachAllChildren();
        if (restart) gameHistory.deleteHistoryBalls(i);
        if (ge.substring(2,4).equals("my")) {
            mycheckers.addAll(checkers.copyCheckers(gameHistory.history.get(i)));
            while(i > 0) if (gameHistory.movesHistory.get(i--).substring(0,2).equals("ai")) break;
            aicheckers.addAll(checkers.copyCheckers(gameHistory.history.get(i+1)));
            //if (firstMove.equals("my")) hod = new ColorRGBA(mycheckers.get(0).defaultcolor);
            hod = new ColorRGBA(aicheckers.get(0).defaultcolor);
        }
        else {
            aicheckers.addAll(checkers.copyCheckers(gameHistory.history.get(i)));
            while(i > 0) if (gameHistory.movesHistory.get(i--).substring(0,2).equals("my")) break;
            mycheckers.addAll(checkers.copyCheckers(gameHistory.history.get(i+1)));
            if (firstMove.equals("my")) hod = new ColorRGBA(aicheckers.get(0).defaultcolor);
            hod = new ColorRGBA(mycheckers.get(0).defaultcolor);
        }
        mycolor=new ColorRGBA(mycheckers.get(0).defaultcolor);
        aicolor=new ColorRGBA(aicheckers.get(0).defaultcolor);

        checkers.bornCheckers(mycheckers, aicheckers);
        //gameHistory.attachHistory();
        aigame.movesAre = true;
        mygame.movesAre = true;
        if (restart) stop = false;
        checkers.benchNode.detachAllChildren();
        for (Checker c: mycheckers) if (!mygame.isJointIn(c.v))  checkers.benchNode.attachChild(c.geom);
        for (Checker c: aicheckers) if (!aigame.isJointIn(c.v))  checkers.benchNode.attachChild(c.geom);
        //setNodesPositions();
    }

    private void turnLight(){
        SpotLight spot = new SpotLight();
        spot.setSpotRange(250);
        spot.setSpotOuterAngle(89* FastMath.DEG_TO_RAD);
        spot.setSpotInnerAngle(89 * FastMath.DEG_TO_RAD);
        spot.setDirection(cam.getDirection());
        spot.setPosition(new Vector3f(0,0, 60));
        rootNode.addLight(spot);
        SpotLight spot1 = new SpotLight();
        spot1.setSpotRange(250);
        spot1.setSpotOuterAngle(89* FastMath.DEG_TO_RAD);
        spot1.setSpotInnerAngle(89 * FastMath.DEG_TO_RAD);
        spot1.setDirection(cam.getDirection());
        spot1.setPosition(new Vector3f(0,0, 80));
        rootNode.addLight(spot1);
    }
    //********************** settings *************************************************
    //********************** settings *************************************************
    //********************** settings *************************************************
    protected void changeColors(){
        menuControl.colors.changed = false;
        if (menuControl.colors.value.substring(0,1).equals("m")) {
            if(hod.equals(mycolor)) hod = new ColorRGBA(menuControl.getColorRGBA(menuControl.colors.value));
            mycolor = menuControl.getColorRGBA(menuControl.colors.value);
            for (Checker c: mycheckers) c.changeColor(mycolor);
        }
        else    {
            if(hod.equals(aicolor)) hod = new ColorRGBA(menuControl.getColorRGBA(menuControl.colors.value));
            aicolor = menuControl.getColorRGBA(menuControl.colors.value);
            for (Checker c: aicheckers) c.changeColor(aicolor);
        }
    }

    protected void changeDimension(){
        demoMode = false;
        menuControl.dims.changed = false;
        demoMode = false;
        dimensionX = Integer.valueOf(menuControl.dims.value.substring(3,4));
        dimensionY = Integer.valueOf(menuControl.dims.value.substring(4,5));
        dimensionZ = Integer.valueOf(menuControl.dims.value.substring(5));
        aigame.dimensionX = dimensionX;
        aigame.dimensionY = dimensionY;
        aigame.dimensionZ = dimensionZ;
        mygame.dimensionX = dimensionX;
        mygame.dimensionY = dimensionY;
        mygame.dimensionZ = dimensionZ;
        checkers.dimensionX = dimensionX;
        checkers.dimensionY = dimensionY;
        checkers.dimensionZ = dimensionZ;
        lattice.dimensionX = dimensionX;
        lattice.dimensionY = dimensionY;
        lattice.dimensionZ = dimensionZ;
        tm.dimensionX = dimensionX;
        tm.dimensionY = dimensionY;
        tm.dimensionZ = dimensionZ;
        restartGame();
        aigame.auto = true;
        if (demoMode) mygame.auto = true;
    }
    protected void changeLevel(){
        menuControl.level.changed = false;
        if (smLevel == Integer.valueOf(menuControl.level.value.substring(5))) return;
        smLevel = Integer.valueOf(menuControl.level.value.substring(5));
        //System.out.println(smLevel);
        aigame.smartLevel = smLevel;
        mygame.smartLevel = smLevel;
        restartGame();
        aigame.auto = true;
        if (demoMode) mygame.auto = true;
    }

    protected void changeFirstMove(){
        menuControl.moveF.changed = false;
        if (firstMove.equals("my") & menuControl.moveF.value.equals("movem") |
                firstMove.equals("ai") & menuControl.moveF.value.equals("movea")) return;
        if (menuControl.moveF.value.equals("movem")) {
            firstMove = "my";
            hod = mycolor;
        }
        else {
            firstMove = "ai";
            hod = aicolor;
        }
        restartGame();
        if (aiMode) aigame.auto = true;
    }

    protected void restartGame(){
        mygame = null;
        aigame = null;
        soundPlayTime =  5000;
        gameHistory.deleteAll();
        gameHistory.ballNode.detachAllChildren();
        gameHistory = null;
        aigame = new MyGame(mycheckers, aicheckers, dimensionX, dimensionY, dimensionZ,
                -1, assetManager, soundEffects, "ai", guiNode, guiFont);
        //aigame.auto = true;
        mygame = new MyGame(aicheckers, mycheckers, dimensionX, dimensionY, dimensionZ,
                1, assetManager, soundEffects, "my", guiNode, guiFont);
        gameHistory = new GameHistory(assetManager);
        lattice.latticeNode.detachAllChildren();
        checkers.benchNode.detachAllChildren();
        lattice.myJoints.clear();
        lattice.aiJoints.clear();
        checkers.checkersNode.detachAllChildren();
        mycheckers.clear();
        aicheckers.clear();
        //checkers.killCheckers(mycheckers);
        //checkers.killCheckers(aicheckers);
        checkers.builtCheckers( mycolor, aicolor, mycheckers, aicheckers);
        lattice.buildLattice(assetManager);
        lattice.abcdLattice(assetManager);
        lattice.latticeNode.attachChild(explosionEffect);
        lattice.latticeNode.attachChild(ve.explosionEffect);
        lattice.latticeNode.attachChild(tm.getTextNode());
        rootNode.attachChild(gameHistory.ballNode);
        mygame.makeBench();
        aigame.makeBench();
        if (firstMove.equals("my")) hod = mycolor;
        else hod = aicolor;
        aigame.resetGame();
        mygame.resetGame();
        stop = false;
        mygame.eated = false;
        aigame.eated = false;
        pickedChecker = null;
        pickedJoint = null;
        setNodesPositions();
    }

    private void printGameResult() {
        int myBalls = 0;
        int aiBalls = 0;
        for (Checker c : mycheckers)  if (c.z == - c.startZ) myBalls++;
        for (Checker c : aicheckers)  if (c.z == - c.startZ & Math.abs(c.x) < 2 * dimensionX) aiBalls++;
        soundPlayTime = 20000;
        if (myBalls >= aiBalls)  {
            writeSomeThing("Game over, my " + myBalls + " - " + "  rival's " +  aiBalls, true);
           // soundEffects.soundPool.play(soundEffects.fanf_audio,soundVolume,soundVolume,0,0,1);
            playSound(soundEffects.fanf_audio, 20000);
        }
        else  {
            writeSomeThing("Game over, my " + myBalls + " - " + "  rival's " +  aiBalls, true);
            //soundEffects.soundPool.play(soundEffects.coin_audio,soundVolume,soundVolume,0,0,1);
            playSound(soundEffects.coin_audio, 20000);
        }
    }
    private void printMessage(MyGame game) {
        if (game.haveEat & !game.pickReturnJoint) {
            writeSomeThing(game.nick + "take!", printMessage);
            //soundEffects.soundPool.play(soundEffects.trol_audio,soundVolume,soundVolume,0,0,1);
            playSound(soundEffects.trol_audio, 5000);
        }
        if (game.haveMove & !game.pickReturnJoint) {
            writeSomeThing(game.nick + "move now!", printMessage);
            //soundEffects.soundPool.play(soundEffects.pod_audio,soundVolume,soundVolume,0,0,1);
            playSound(soundEffects.pod_audio, 5000);
        }
        if (game.pickReturnJoint) {
            writeSomeThing(game.nick + "pick a return joint!",printMessage);
            //soundEffects.soundPool.play(soundEffects.fanf_audio,soundVolume,soundVolume,0,0,1);
            playSound(soundEffects.chime_audio, 5000);
        }
        if (game.auto) {
            writeSomeThing(game.nick +"move now!", printMessage);
            playSound(soundEffects.trol_audio, 5000);
        }
    }
    protected static void playSound(int an, float soundRepeator){
        if (soundOn & System.currentTimeMillis() - soundPausedTime > soundRepeator ) {
            try {soundEffects.soundPool.play(an, soundVolume, soundVolume,0,0,1);} //an.playInstance();}
            catch (NullPointerException e) {System.out.println(e + "an");}
            soundPausedTime = System.currentTimeMillis();
        }
    }

    protected static void writeSomeThing(String text, Boolean show) {
        if (!show) return;
        bmText.setText(text);
        bmText.setSize(50f);
        ColorRGBA clr = ColorRGBA.randomColor();
        bmText.setColor(new ColorRGBA(0.5f + 0.5f* clr.r, 0.5f+0.5f*clr.g, 0.5f + 0.5f*clr.b, 1));
        bmText.setLocalTranslation(screenWidth/2 - bmText.getLineWidth()/2, screenHeight - bmText.getLineHeight()/2,   0);
    }
    /*protected static void playSound(AudioNode an, double soundRepeator){
        if (soundOn & System.currentTimeMillis() - soundPausedTime > soundRepeator) {
            try {an.playInstance();}
            catch (NullPointerException e) {System.out.println(e + "an");}
            soundPausedTime = System.currentTimeMillis();
        }
    }*/
    private void setGameText(){
        guiFont = assetManager.loadFont("Interface/Fonts/ComicSansMS.fnt");
        bmText.setName("gametext");
        bmText.setSize((int)screenWidth/17);
        infoText = new  BitmapText(guiFont, false);
        infoText.setSize(40f);
        infoText.setText("");
        infoText.setLocalTranslation(screenWidth/2 - infoText.getLineWidth()/2 ,
                infoText.getLineHeight()  ,   -4);
        guiNode.attachChild(infoText);
    }
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++ switch Games witch Games switch Games switch Games +++++++
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    protected void switchDemo(){ // demo Mode
        menuControl.demo.changed = false;
        networkGame.netgameInit = false;
        isGameGoing = false;
        demoMode = true;//!demoMode;
        localMode = false;//!localMode;
        netMode = false;//!netMode;
        aiMode = false;//!aiMode;
        gameMode = 1;
        restartGame();
        mygame.smartLevel = 32;
        aigame.smartLevel = 32;
        mygame.auto = true;//!mygame.auto;//!mygame.auto;
        aigame.auto = true;
        answerYesNo = false;
        printMessage = true;
        isGameGoing = false;
    }

    protected void switchAiGame(){  // ai Mode
        menuControl.aigame.changed = false;
        networkGame.netgameInit = false;
        isGameGoing = false;
        demoMode = false;
        netMode = false;
        localMode = false;
        aiMode = true;
        gameMode = 2;
        restartGame();
        mygame.smartLevel = 2;
        aigame.smartLevel = smLevel;
        mygame.auto = false;
        aigame.auto = true;
        answerYesNo = false;
        printMessage = true;
        isGameGoing = false;
    }

    private void switchLocGame()  {
        menuControl.locgame.changed = false;
        networkGame.netgameInit = false;
        isGameGoing = false;
        netMode = false;
        localMode = true;
        aiMode = false;
        demoMode = false;
        gameMode = 3;
        restartGame();
        mygame.smartLevel = 2;
        aigame.smartLevel = 2;
        mygame.auto = false;
        aigame.auto = false;
        answerYesNo = false;
        printMessage = true;
        isGameGoing = false;
    }

    private void switchNetGame()   {
        menuControl.netgame.changed = false;
        isGameGoing = false;
        netMode = true;
        localMode = false;
        aiMode = false;
        demoMode = false;
        gameMode = 4;
        restartGame();
        //System.out.println(" switchNetGame() ");
        mygame.smartLevel = 2;
        aigame.smartLevel = 2;
        networkGame.switchNetGame();
        answerYesNo = false;
        printMessage = true;
        isGameGoing = false;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initNetGame() { // added at version 2

        if (NetworkNameActivity.reply != null) {
            if (NetworkNameActivity.reply.equals("-1")) {
                networkGame.gameID = NetworkNameActivity.gameName;
                //networkGame.uuid = NetworkNameActivity.uuid;
                networkGame.initNetGame("keyEnt", null);
            }
            else {
                networkGame.netgameInit = false;
                //menuControl.getMenuElementByName(menuControl.aigame.name);
                //System.out.println("initNetGame " + "menu" + menuControl.aigame.name);
                menuControl.switchMenu1(menuControl.aigame.name);
                switchAiGame();
            }
            NetworkNameActivity.reply = null;
        }
        if (networkGame.response.equals("wait gamer2") ) {
            networkGame.initNetGame("keyEnt", null);
        }
        return;
    }

    private void switchSound() {
        menuControl.sound.changed = false;
        if (soundVolume > 0.9f | soundVolume <= 0.05f) deltaVolume = -deltaVolume;
        soundVolume = soundVolume + deltaVolume;
        if(soundVolume < 0) soundVolume = 0;
        mygame.soundVolume = soundVolume;
        aigame.soundVolume = soundVolume;
    }


    private void reccordHistory(MyGame game){
        isGameGoing = true;
        if( gameHistory.movesHistory.size() > 1 &&
                game.gamer.equals((gameHistory.movesHistory.get(gameHistory.movesHistory.size()-1).substring(0,2)))) {
            if (game.gamer.equals("ai")) gameHistory.reccordHistory(mygame.gamer, mygame.mycheckers, mygame.moveFrom, mygame.moveTo, "---");
            if (game.gamer.equals("my")) gameHistory.reccordHistory(aigame.gamer, aigame.mycheckers, aigame.moveFrom, aigame.moveTo, "---");
        }
        gameHistory.reccordHistory(game.gamer, game.mycheckers, game.moveFrom, game.moveTo, game.moveKind);
    }
// ********************************************** effects ***************************************
// ********************************************** effects ***************************************
// ********************************************** effects ***************************************
    private void makeDebris(MyGame game) {
        ve.debris.setStartColor(game.checkerToBeEated.defaultcolor);
        ve.debris.setEndColor(game.checkerToBeEated.defaultcolor);
        ve.debris.setLocalTranslation(game.debrisVector);
        ve.debris.emitAllParticles();
        //System.out.println(" makeDebris " + game.checkerToBeEated.defaultcolor + " v " + game.convertToAa1(game.eatVector[0]));
        if (game.soundOn) soundEffects.soundPool.play(soundEffects.burst_audio,soundVolume,soundVolume,0,0,1);
        game.checkerToBeEated.smash = false;
        game.checkerToBeEated.remove(game.checkerToBeEated.x, game.checkerToBeEated.y, game.checkerToBeEated.z);
        checkers.checkersNode.detachChild(game.checkerToBeEated.geom);
        checkers.benchNode.attachChild(game.checkerToBeEated.geom);
        game.eating = false;
        game.eated = true;
        game.moveChecked = false;
        game.checkerToBeEated = null;
        breakTime = System.currentTimeMillis();
    }

    private void makeFlame(MyGame game) {
        ve.flame.setLocalTranslation(game.flameVector);
        ve.explosionEffect.attachChild(ve.flame);
        ve.flame.emitAllParticles();
        game.flame = false;
    }

    private void showBirth(MyGame game, float tpf){
        if (game.flame) {
            makeFlame(game);
            if (game.soundOn & game.gamer.equals("ai")) soundEffects.soundPool.play(soundEffects.expls_audio,soundVolume,soundVolume,0,0,1);
            if (game.soundOn & game.gamer.equals("my")) soundEffects.soundPool.play(soundEffects.coin_audio,soundVolume,soundVolume,0,0,1);
            game.checkerToBirth.moveToStart(game.flameVector);
            checkers.benchNode.detachChild(game.checkerToBirth.geom);
            checkers.checkersNode.attachChild(game.checkerToBirth.geom);
            game.flameVector.set(100, 100, 100);
            birthTime = System.currentTimeMillis();
            game.haveMove =false;
        }
        if (System.currentTimeMillis() - birthTime < 1000) {
            game.checkerToBirth.birth(tpf);
        }
        else {
            game.birthing = false;
            game.checkerToBirth.resetChecker();
            breakTime = System.currentTimeMillis();
        }
    }

    private void showCheckerToMove(MyGame game) {
        try {
            if (System.currentTimeMillis() - game.preMoveTime < 1000) {
                    game.checkerToMove.setBright(2);
                    game.checkerToMove.shiverChecker();
                return;
            }
            game.premoving = false;
            if (game.soundOn ) playSound(soundEffects.pick_audio, 0);
            game.checkerToMove.setBright(128);
        }
        catch (NullPointerException e) {
            System.out.println("NullPointer" + e);
        }
    }

    private void  showPickedChecker() {
        if (System.currentTimeMillis() - shiveringTime < 1000 ) {
            try {
                pickedChecker.shiverChecker();
                pickedChecker.setBright(3);
            }
            catch (NullPointerException e) {
                picking = false;
                System.out.println(e);
            }
        }
        else picking = false;
    }

    private void returnCheckerToPlay() {
        showFreeJoints();
        if (!lattice.myJoints.contains(pickedJoint)) return;
        if (pickedJoint.z == mycheckers.get(0).startZ) {
            mygame.flameVector = new Vector3f(pickedJoint.v);
            mygame.flame = true;
            mygame.birthing = true;
            lattice.resetMyAiJoints();
            mygame.pickReturnJoint = false;
        }
    }
    private void showFreeJoints() {
        for (Vector3f v: mygame.freeJoints) {
            //System.out.println("showFreeJoints" + v);
            if (mygame.soundOn ) soundEffects.soundPool.play(soundEffects.pick_audio,soundVolume,soundVolume,0,0,1);
            lattice.getJointByVector(v).shiverJoint();
        }
    }
//*************************************************************************************************************
//************************************** save load ************************************************************
//*************************************************************************************************************

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void readSettings() throws FileNotFoundException {
        //gd.readSettings("checker.txt",ctx);
        for (String f: ctx.fileList()) if (f.equals("checker.txt")) gd.readSettings("checker.txt", ctx);
        //System.out.println("readSettings  " + gd.foundDataFile + "  " + gameMode);
        if (!gd.foundDataFile) {//default values
            mycolor = new ColorRGBA(0, 0, 1, 1);
            aicolor = new ColorRGBA(1, 0, 0, 1);
            firstMove = "my";
            hod = mycolor;
            dimensionX = 2;
            dimensionY = 2;
            dimensionZ = 2;
            smLevel= 2 ;
            aiMode = true;
            gameMode = 2;
            menuControl.aigame.selectMenuElement(menuSelectedColor);
        }
        else {
            mycolor = new ColorRGBA(gd.myclr);
            aicolor = new ColorRGBA(gd.aiclr);
            firstMove = gd.move;
            if (firstMove.equals("my"))hod = mycolor;
            else hod = aicolor;
            dimensionX = gd.dimensionX;
            dimensionY = gd.dimensionY;
            dimensionZ = gd.dimensionZ;
            smLevel = gd.smartLevel;
            gameMode = gd.mode;
            menuControl.resetMenuOptionsColor(menuControl.menuOption);
            menuControl.unselectMenuGameModes();
            switch (gameMode) {
                case 2:
                    aiMode = true;
                    smLevel = gd.smartLevel;
                    menuControl.aigame.selectMenuElement(menuSelectedColor);
                    break;
                case 3:
                    localMode = true;
                    smLevel = gd.smartLevel;
                    menuControl.locgame.selectMenuElement(menuSelectedColor);
                    break;
                case 4:
                    netMode = true;
                    smLevel = gd.smartLevel;
                    networkGame.netgameInit = true;
                    menuControl.netgame.selectMenuElement(menuSelectedColor);
                    networkGame.response = "choose new gameID (the same for you and rival): ";
                    writeSomeThing(networkGame.response, printMessage);
                    playSound(soundEffects.chime_audio, 5000);
                    break;
            }
        }
        menuControl.getMenuElementByDimension(String.valueOf(dimensionX)
                + String.valueOf(dimensionY) + String.valueOf(dimensionZ)).selected = true;
        menuControl.getMenuElementByDimension(String.valueOf(dimensionX)
                + String.valueOf(dimensionY) + String.valueOf(dimensionZ)).material.setColor("Diffuse", menuSelectedColor);
        menuControl.getSelectedElementByColor(mycolor, "my").selected = true;
        menuControl.getSelectedElementByColor(aicolor, "ai").selected = true;
        menuControl.getMenuElementByLevel(smLevel).material.setColor("Diffuse", menuSelectedColor);
        menuControl.getMenuElementByLevel(smLevel).selected = true;
        if (firstMove.equals("my"))  menuControl.moveMe.material.setColor("Diffuse", menuSelectedColor);
        if (firstMove.equals("ai"))  menuControl.moveAi.material.setColor("Diffuse", menuSelectedColor);
        menuControl.sound.material.setColor("Diffuse", menuSelectedColor);
    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT & Build.VERSION_CODES.O)
    protected void loadGame() throws FileNotFoundException {
        menuControl.load.changed = false;
        if(!LoadFileActivity.readyToLoad) {
            GameStart.playSound(GameStart.soundEffects.chime_audio, 5000);
            final Intent intent = new Intent(ctx, LoadFileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
            //System.out.println("saveGame()  " + SaveFileActivity.readyToSave);
            return;
        }
        LoadFileActivity.readyToLoad = false;
        menuControl.load.resetMenuElement();
        startTime = System.currentTimeMillis();
        if(LoadFileActivity.canceled) return;
        gd.loadGame(ctx,LoadFileActivity.fName);
        if (!gd.foundDataFile) {
            demoMode = false;
            writeSomeThing("Game file is bad or absent", true);
            playSound(soundEffects.bell_audio, 5000);
        }
        else {
            writeSomeThing("", true);
            mycolor = new ColorRGBA(gd.myclr);
            aicolor = new ColorRGBA(gd.aiclr);
            firstMove = gd.move;
            smLevel = gd.smartLevel;
            if (firstMove.equals("my")) hod = mycolor;
            else hod = aicolor;
            dimensionX = gd.dimensionX;
            dimensionY = gd.dimensionY;
            dimensionZ = gd.dimensionZ;
            mycheckers.clear();
            aicheckers.clear();
            checkers.checkersNode.detachAllChildren();
            checkers.benchNode.detachAllChildren();
            mycheckers.addAll(gd.mychks);
            aicheckers.addAll(gd.aichks);
            checkers.bornCheckers(mycheckers, aicheckers);
            for (Checker c : mycheckers)
                if (!mygame.isJointIn(c.v)) checkers.benchNode.attachChild(c.geom);
            for (Checker c : aicheckers)
                if (!aigame.isJointIn(c.v)) checkers.benchNode.attachChild(c.geom);
            aigame.resetGame();
            mygame.resetGame();
            stop = false;
            if (gd.gameMode.equals("3")) {
                netMode = false;
                localMode = true;
                aiMode = false;
                demoMode = false;
                gameMode = 3;
                aigame.auto = false;
                mygame.auto = false;
                menuControl.resetMenuOptionsColor(menuControl.menuOption);
                menuControl.unselectMenuGameModes();
                menuControl.locgame.selectMenuElement(menuSelectedColor);
                return;
            }
            if (gd.gameMode.equals("2")) {
                netMode = false;
                localMode = false;
                aiMode = true;
                demoMode = false;
                gameMode = 2;
                aigame.auto = true;
                mygame.auto = false;
                menuControl.resetMenuOptionsColor(menuControl.menuOption);
                menuControl.unselectMenuGameModes();
                menuControl.aigame.selectMenuElement(menuSelectedColor);
                return;
            }
            if (gd.gameMode.equals("4")) {
                netMode = true;
                localMode = false;
                aiMode = false;
                demoMode = false;
                gameMode = 4;
                networkGame.uuid = gd.gamerId;
                networkGame.gameID = gd.netgameId;
                networkGame.nrNetHod = 1;
                try {
                    networkGame.response = networkGame.getRequest("gameid=" + networkGame.gameID + "&gamer=" + networkGame.uuid + "&init=load&nr=1");
                } catch (IOException ex) {
                    System.out.println("GameStart.loadGame " + ex);
                }
                try {
                    networkGame.response = networkGame.getRequest("gameid=" + networkGame.gameID + "&gamer=" + networkGame.uuid + "&init=reset&nr=1");
                } catch (IOException ex) {
                    System.out.println("GameStart.loadGame " + ex);
                }
                aigame.auto = false;
                mygame.auto = false;
                menuControl.resetMenuOptionsColor(menuControl.menuOption);
                menuControl.unselectMenuGameModes();
                menuControl.netgame.selectMenuElement(menuSelectedColor);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void saveGame() throws FileNotFoundException {
        menuControl.save.changed = false;
        if (demoMode) {
            writeSomeThing("Not used in demo mode", true);
            playSound(soundEffects.bell_audio, 5000);
            menuControl.save.resetMenuElement();
            return;
        }
        if(!SaveFileActivity.readyToSave) {
            GameStart.playSound(GameStart.soundEffects.chime_audio, 5000);
            final Intent intent = new Intent(ctx, SaveFileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
            //System.out.println("saveGame()  " + SaveFileActivity.readyToSave);
            return;
        }
        SaveFileActivity.readyToSave = false;
        menuControl.save.resetMenuElement();
        if (SaveFileActivity.canceled) return;
        gd.saveGame(mygame.mycheckers, mygame.aicheckers, firstMove, dimensionX, dimensionY,
                    dimensionZ, smLevel, soundVolume, deltaVolume, ctx, gameMode);
        if (gd.gameSaved) {
            if (netMode) gd.addNetData(networkGame.uuid, networkGame.gameID, SaveFileActivity.fName);
            if (gd.gameSaved) {
                writeSomeThing("Game saved", true);
                playSound(soundEffects.cymb_audio, 0);
                startTime = System.currentTimeMillis();
                return;
            }
        }
        writeSomeThing("error, game not saved", true);
        playSound(soundEffects.bell_audio, 0);
        startTime = System.currentTimeMillis();


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void saveSettingsToFile() throws FileNotFoundException {
        if(netMode | demoMode) return;
        menuControl.back.changed = false;
        gd.settingsToFile(mygame.mycheckers.get(0).defaultcolor, mygame.aicheckers.get(0).defaultcolor, firstMove ,
                dimensionX, dimensionY, dimensionZ, smLevel, soundVolume, deltaVolume, ctx, gameMode);
    }

//***************************** game control *******************************************************************
    private final TouchListener touchListener = new TouchListener(){
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onTouch(String name, TouchEvent evt, float tpf) {
            screenHeight=context.getSettings().getHeight();
            screenWidth=context.getSettings().getWidth();
            switch(evt.getType()) {
                case MOVE:
                    latticeToStart = true;
                    if (evt.getY() > screenHeight / 3 & evt.getY() < screenHeight * 2 / 3 &
                            evt.getX()> screenWidth / 3 & evt.getX() < screenWidth * 2 / 3) {
                        turnLatticeLR(evt.getDeltaX() / 150);
                        turnLatticeUD(evt.getDeltaY() / 150);
                    }
                    if ((evt.getY() > screenHeight / 6 & evt.getY() < screenHeight * 5 / 6 &
                            evt.getX() > screenWidth / 7 & evt.getX() < screenWidth  / 4) ||
                            (evt.getY()> screenHeight / 6 & evt.getY() < screenHeight * 5 / 6 &
                            evt.getX() > screenWidth * 3 / 4 & evt.getX() < screenWidth * 6 / 7)){
                        lattice.latticeNode.setLocalTranslation(lattice.latticeNode.getLocalTranslation().x ,
                                lattice.latticeNode.getLocalTranslation().y, lattice.latticeNode.getLocalTranslation().z + evt.getDeltaY()/70 );
                        checkers.checkersNode.setLocalTranslation(checkers.checkersNode.getLocalTranslation().x ,
                                checkers.checkersNode.getLocalTranslation().y, checkers.checkersNode.getLocalTranslation().z + evt.getDeltaY()/70);
                    }
                    if (evt.getY()> screenHeight * 7 / 8 & evt.getX()> screenWidth / 6 & evt.getX() < screenWidth  * 5/ 6 ){
                        lattice.latticeNode.setLocalTranslation(lattice.latticeNode.getLocalTranslation().x + evt.getDeltaX()/70,
                                lattice.latticeNode.getLocalTranslation().y, lattice.latticeNode.getLocalTranslation().z);
                        checkers.checkersNode.setLocalTranslation(checkers.checkersNode.getLocalTranslation().x + evt.getDeltaX()/70,
                                checkers.checkersNode.getLocalTranslation().y, checkers.checkersNode.getLocalTranslation().z);
                    }

                    if ( evt.getX() < screenWidth / 8) {
                        gameHistory.ballNode.setLocalTranslation(gameHistory.ballNode.getLocalTranslation().x,
                                gameHistory.ballNode.getLocalTranslation().y , gameHistory.ballNode.getLocalTranslation().z + evt.getDeltaY()/70);
                    }
                    if ( evt.getY() > screenHeight / 5 & evt.getX() >  screenWidth * 7 / 8) {
                        menuControl.menuNode.setLocalTranslation(menuControl.menuNode.getLocalTranslation().x,
                                menuControl.menuNode.getLocalTranslation().y , menuControl.menuNode.getLocalTranslation().z + evt.getDeltaY()/70);
                    }
                    if (evt.getY() < screenHeight / 8 & evt.getX() > screenWidth  / 5) {
                        gameControl.controlNode.setLocalTranslation(gameControl.controlNode.getLocalTranslation().x  + evt.getDeltaX()/70,
                                gameControl.controlNode.getLocalTranslation().y , gameControl.controlNode.getLocalTranslation().z);
                    }
                   //TouchEvent.Type.
                    break;
                case TAP:
                    try {
                        tappedPoint();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case SCALE_MOVE:
                    latticeToStart = true;
                    if (evt.getX() < screenWidth / 8) { // history scale
                        //gameHistory.scrollHistory((int)evt.getDeltaX() / 20);
                        //if (gameHistory.ballNode.getLocalTranslation().y < -3 | gameHistory.ballNode.getLocalTranslation().y > 3) return;
                        if (evt.getScaleFactor() < 1) {
                            if (gameHistory.ballNode.getLocalTranslation().y > 3) return;
                            gameHistory.ballNode.setLocalTranslation(gameHistory.ballNode.getLocalTranslation().x - 1/evt.getScaleFactor()/7.05f,
                                    gameHistory.ballNode.getLocalTranslation().y + 1/evt.getScaleFactor()/5, gameHistory.ballNode.getLocalTranslation().z);
                        }
                        else  {
                            if (gameHistory.ballNode.getLocalTranslation().y < -3) return;
                            gameHistory.ballNode.setLocalTranslation(gameHistory.ballNode.getLocalTranslation().x + evt.getScaleFactor()/7.05f,
                                    gameHistory.ballNode.getLocalTranslation().y - evt.getScaleFactor()/5, gameHistory.ballNode.getLocalTranslation().z);
                        }
                    }

                    if ( evt.getY() > screenHeight / 5 & evt.getX() >  screenWidth * 7 / 8) { // menu scale
                        if (evt.getScaleFactor() < 1) {
                            if (menuControl.menuNode.getLocalTranslation().y > 3) return;
                            menuControl.menuNode.setLocalTranslation(menuControl.menuNode.getLocalTranslation().x + 1/evt.getScaleFactor()/7.05f,
                                    menuControl.menuNode.getLocalTranslation().y + 1/evt.getScaleFactor()/5, menuControl.menuNode.getLocalTranslation().z);
                        }
                        else  {
                            if (menuControl.menuNode.getLocalTranslation().y <-3) return;
                            menuControl.menuNode.setLocalTranslation(menuControl.menuNode.getLocalTranslation().x - evt.getScaleFactor()/7.05f,
                                    menuControl.menuNode.getLocalTranslation().y - evt.getScaleFactor()/5, menuControl.menuNode.getLocalTranslation().z);
                        }
                    }

                    if ( evt.getY() < screenHeight / 8 & evt.getX() >  screenWidth / 6 & evt.getX() <  screenWidth * 5 / 6) { // control scale
                        if (evt.getScaleFactor() < 1) {
                            if (gameControl.controlNode.getLocalTranslation().y > 3) return;
                            gameControl.controlNode.setLocalTranslation(gameControl.controlNode.getLocalTranslation().x,
                                    gameControl.controlNode.getLocalTranslation().y + 1/evt.getScaleFactor()/5,
                                    gameControl.controlNode.getLocalTranslation().z  - 1/evt.getScaleFactor()/12f);
                        }
                        else  {
                            if (gameControl.controlNode.getLocalTranslation().y <-3) return;
                            gameControl.controlNode.setLocalTranslation(gameControl.controlNode.getLocalTranslation().x,
                                    gameControl.controlNode.getLocalTranslation().y - evt.getScaleFactor()/5,
                                    gameControl.controlNode.getLocalTranslation().z + evt.getScaleFactor()/12f);
                        }
                    }

                    if (evt.getY() > screenHeight / 5 & evt.getY() < screenHeight * 4 / 5 &
                            evt.getX()> screenWidth / 5 & evt.getX() < screenWidth * 4 / 5) { // lattice scale
                        if (evt.getScaleFactor() < 1) {
                            if (lattice.latticeNode.getLocalTranslation().y > 2) return;
                            lattice.latticeNode.setLocalTranslation(lattice.latticeNode.getLocalTranslation().x ,
                                    lattice.latticeNode.getLocalTranslation().y + 1/evt.getScaleFactor()/5, lattice.latticeNode.getLocalTranslation().z);
                            checkers.checkersNode.setLocalTranslation(checkers.checkersNode.getLocalTranslation().x ,
                                    checkers.checkersNode.getLocalTranslation().y + 1/evt.getScaleFactor()/5, checkers.checkersNode.getLocalTranslation().z);
                        }
                        else  {
                            if (lattice.latticeNode.getLocalTranslation().y < - 2) return;
                            lattice.latticeNode.setLocalTranslation(lattice.latticeNode.getLocalTranslation().x ,
                                    lattice.latticeNode.getLocalTranslation().y - evt.getScaleFactor()/5, lattice.latticeNode.getLocalTranslation().z);
                            checkers.checkersNode.setLocalTranslation(checkers.checkersNode.getLocalTranslation().x ,
                                    checkers.checkersNode.getLocalTranslation().y - evt.getScaleFactor()/5, checkers.checkersNode.getLocalTranslation().z);
                        }
                    }
                    break;
                case LONGPRESSED:
                   pressedLong();
                case DOWN:
                    //pressedLong();
                    break;
                case HOVER_START:
                    overJoint();
                    break;
            }
        }
    };
    //=================================================================controlAnswerYesNo() ======
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void controlAnswerYesNo() throws FileNotFoundException {
        if(TranslucentActivity.reply == null) return; //System.out.println("tappedPoint " + TranslucentActivity.reply);
        if (TranslucentActivity.reply.equals("-1")) {
            if(netMode) networkGame.netgameInit = false;
            menuControl.switchMenu1(menuControl.currentMenuElementName);
            try {  selectByChangedMenuItem();}
            catch (FileNotFoundException e) {System.out.println(e);}
        }
        if(!networkGame.netgameInit) printMessage = true;
        answerYesNo = false;
        TranslucentActivity.reply = null;
    }
    //=================================================================================================
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void selectByChangedMenuItem() throws FileNotFoundException {
        if (menuControl.colors.changed) changeColors();
        if (menuControl.dims.changed) changeDimension();
        if (menuControl.level.changed) changeLevel();
        if (menuControl.moveF.changed) changeFirstMove();
        if (menuControl.demo.changed) switchDemo();
        if (menuControl.sound.changed) switchSound();
        if (menuControl.save.changed) saveGame();
        if (menuControl.load.changed) loadGame();
        if (menuControl.back.changed) saveSettingsToFile();
        if (menuControl.demo.changed) switchDemo();//demoMode
        if (menuControl.aigame.changed) switchAiGame(); //aiMode
        if (menuControl.netgame.changed) switchNetGame(); // netMode
        if (menuControl.locgame.changed) switchLocGame();  // Local Mode
    }
//===============================================================================tappedPoint()======
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void tappedPoint() throws FileNotFoundException {
        Geometry pickedGeom = pickGeom();
        if (pickedGeom == null) return;
        String pickedGeomName = pickedGeom.getName();
        if(networkGame.netgameInit) if (!pickedGeomName.substring(0, 2).equals("me")) return;
        try {

            switch (pickedGeomName.substring(0, 2)) {
                case "ch":
                    if (hod.equals(mycolor) & pickedGeom.getMaterial().getParam("Diffuse")
                            .getValue().equals(mycolor) & !actions) {
                        selectChecker(pickedGeomName, mygame);
                    }
                    if (hod.equals(aicolor) & pickedGeom.getMaterial().getParam("Diffuse")
                            .getValue().equals(aicolor) & !actions) {
                        selectChecker(pickedGeomName, aigame);
                    }
                    break;
                case "jo":
                    pickedJoint = lattice.joint
                            [Integer.valueOf(pickedGeomName.substring(2, 3))]
                            [Integer.valueOf(pickedGeomName.substring(3, 4))]
                            [Integer.valueOf(pickedGeomName.substring(4))];
                    if (mygame.checkerChecked | mygame.pickReturnJoint) {
                        if (mygame.pickReturnJoint & lattice.myJoints.contains(pickedJoint)) break;
                        mygame.moveChecked = true;
                    }
                    if (aigame.checkerChecked | aigame.pickReturnJoint) {
                        if (aigame.pickReturnJoint & lattice.aiJoints.contains(pickedJoint)) break;
                        aigame.moveChecked = true;
                    }
                    tm.showJointHint(pickedGeom, pickedJoint);
                    break;
                case "me":
                    if (mygame.soundOn)
                        soundEffects.soundPool.play(soundEffects.click_audio, soundVolume, soundVolume, 0, 0, 1);
                    menuControl.switchMenu1(pickedGeomName);
                    selectByChangedMenuItem();
                    break;
                case "hi":
                    if (mygame.soundOn)
                        soundEffects.soundPool.play(soundEffects.click_audio, soundVolume, soundVolume, 0, 0, 1);
                    if (specularBall != null) specularBall.getMaterial().setFloat("Shininess", 128f);
                    pickedGeom.getMaterial().setFloat("Shininess", 12f);
                    specularBall = pickedGeom;
                    if (!netMode) restartGameFrom(pickedGeomName, false);
                    break;
                case "co":
                    if (pickedGeomName.equals("contrcentr")) resetLattice();
                    break;
            }
        }
        catch (NullPointerException | FileNotFoundException e) {
            //System.out.println(" No picked object" + e);
        }
    }
//==================================================================================================
    private void resetLattice(){
        soundEffects.soundPool.play(soundEffects.click_audio,soundVolume,soundVolume,0,0,1);
        if (latticeToStart) {
            latticeToStart = false;
            lattice.latticeNode.setLocalTranslation(0, dimensionX * dimensionY, 0);
            checkers.checkersNode.setLocalTranslation(0, dimensionX * dimensionY, 0);
            lattice.latticeNode.setLocalRotation(new Quaternion(0,0,0,1));
            checkers.checkersNode.setLocalRotation(new Quaternion(0,0,0,1));
        }
    }

    private void turnLatticeLR(float tpf){
        lattice.latticeNode.rotate(0, 0, tpf);
        checkers.checkersNode.rotate(0, 0, tpf);
        lattice.addRotate();
        tm.textNode.rotate(0, 0, tpf);
        latticeToStart = true;
    }
    private void turnLatticeUD(float tpf){
        lattice.latticeNode.rotate(-tpf, 0, 0);
        checkers.checkersNode.rotate(-tpf, 0, 0);
        lattice.addRotate();
        tm.textNode.rotate(tpf, 0, 0);
        latticeToStart = true;
    }

    private void pressedLong() {
        Geometry pickedGeom = pickGeom();
        if (pickedGeom == null) return;
        String pickedGeomName = pickedGeom.getName();
        if(pickedGeomName.substring(0,2).equals("hi"))  restartGameFrom(pickedGeomName, true);
    }

    private void moveCloserFarther(float tpf) {
        if (lattice.latticeNode.getWorldTranslation().y > 0 & lattice.latticeNode.getWorldTranslation().y < 20) {
            lattice.latticeNode.move(0, tpf * 20, 0);
            checkers.checkersNode.move(0, tpf * 20, 0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setGameData() throws IOException {
        String sning = java.util.UUID.randomUUID().toString();
        String cod = null;
        //File file = new File("package.cfg");
        //file.getCanonicalFile().delete();
        //OutputStreamWriter ooo = new OutputStreamWriter(ctx.openFileOutput("package.cfg", MODE_PRIVATE), StandardCharsets.UTF_8);
        //ooo.write("xxx");
        //ooo.close();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(ctx.openFileInput("package.cfg"), StandardCharsets.UTF_8))){
            cod = reader.readLine();
        } catch (IOException e) {
            OutputStreamWriter ous = new OutputStreamWriter(ctx.openFileOutput("package.cfg", MODE_PRIVATE), StandardCharsets.UTF_8);
            ous.write(sning);
            ous.close();
            e.printStackTrace();
        }
        //BufferedReader reader = new BufferedReader(new InputStreamReader(ctx.getAssets().open("Sounds/package.cfg")));
        //cod = reader.readLine();
        //FileOutputStream fOut = ctx.openFileOutput("package.cfg", MODE_PRIVATE);
        if (cod.equals(sning)) return;
        else System.exit(0); //stop
    }
    /* @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }*/

    private boolean waitGame() {
        try {
            return !mygame.checkerToMove.sliping & !aigame.checkerToMove.sliping &
                    !mygame.checkerToMove.sliping & !aigame.checkerToMove.sliping;
            //&    !mygame.eating & !aigame.eating;
        }
        catch (NullPointerException e) {
            //System.out.println("pauseGame " + e);
            return true;
        }
    }

    private void repeateSound(int an) {
        if (soundRepeator > 20) {
            if (mygame.soundOn) soundEffects.soundPool.play(soundEffects.pick_audio,soundVolume,soundVolume,0,0,1);
            soundRepeator = 0;
        }
        soundRepeator++;
    }
    private void printInfo(){
        int gt = 300 - (int)(System.currentTimeMillis()-tTime)/1000;
        String gameTime = "trial game " + String.valueOf(gt);
        if (gt < 0)  {
            gameTime = gameTime + " time over ";
            stop = true;
        }
        infoText.setText(gameTime);
        infoText.setColor(ColorRGBA.randomColor());
    }

/*    protected static void writeFileList(ArrayList<String> fileName, Node guiNode, BitmapFont font ) {
        System.out.println("static void writeFileList  " + fileName.toString());
        BitmapText[] fileArray = new BitmapText[fileName.size()];
        ColorRGBA clr = ColorRGBA.randomColor();
        for (int i=0; i < fileName.size(); i++) {
            fileArray[i] = new BitmapText(font, false);
            fileArray[i].setText(fileName.get(i));
            fileArray[i].setSize(50f);
            fileArray[i].setColor(new ColorRGBA(0.5f + 0.5f* clr.r, 0.5f+0.5f*clr.g, 0.5f + 0.5f*clr.b, 1));
            fileArray[i].setLocalTranslation(screenWidth/2 - fileArray[i].getLineWidth()/2, screenHeight - fileArray[i].getLineHeight()/2 - i*50 ,   0);
            System.out.println("static void writeFileList  " + fileName.get(i));
            guiNode.attachChild(fileArray[i]);
        }

    }*/

    //protected void switchHist(){
    //    showHist = !showHist;
    //    menuControl.hist.changed = false;
    // }
/*  private int getCheckersInFinish(ArrayList<Checker> checkers){
        int i = 0;
        for (Checker c: checkers) {
            if (dimensionZ == 3) {
                if ((c.z == -c.startZ | c.z == - (c.startZ - Math.signum(c.startZ) * 2))
                        & FastMath.abs(c.y) < 2 * dimensionY
                        & FastMath.abs(c.x) < 2 * dimensionX ) i++;
            }
            if (dimensionZ == 2) {
                if (c.z == -c.startZ
                        & FastMath.abs(c.y) < 2 * dimensionY
                        & FastMath.abs(c.x) < 2 * dimensionX ) i++;
            }
        }
        return i;
    }*/
/* @RequiresApi(api = Build.VERSION_CODES.KITKAT)
 private void setGameData() throws FileNotFoundException {
       // MyIron si = new MyIron();
     String active = "";
     //   String android_id = Settings.Secure.getString(ctx.getContentResolver(),
     //           Settings.Secure.ANDROID_ID);
        gd.readFile("package.cfg", ctx);
        String tok = gd.lines.get(0);
        if (tok.equals("0")) {
            tok = String.valueOf(System.currentTimeMillis());
            gd.saveToFile(tok, "package.cfg", ctx);
        }
        else
        if (!tok.contains("#")) {
            //String active = new ConnectionMysql(tok.substring(0, 10),//tok.substring(0, 10)).getActive(tok);
            HttpPostRequest hpt = new HttpPostRequest();
            try {
                active = hpt.getRequest(tok);
            } catch (IOException ex) {
                //Logger.getLogger(GameStart.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (active.equals("not")) {
                si.replaceString(os, sning, tok);
            }
            else {
                System.exit(0);//stop
            }
        }
        else if (!tok.contains(sning)) System.exit(0);//stop;
    }*/
    /*switch(pickedGeomName) {
                                case "contrpause":
                                    stop = !stop;
                                    break;
                                case "contrexit":
                                    soundRepeator=21;
                                    repeateSound(soundEffects.click_audio);
                                    System.exit(1);
                                    break;
                                case "contrrest":
                                    soundRepeator=21;
                                    repeateSound(soundEffects.click_audio);
                                    restartGame();
                                    break;
                                case "contrcentr":
                                    soundRepeator=21;
                                    repeateSound(soundEffects.click_audio);
                                    if (latticeToStart) {
                                        latticeToStart = false;
                                        lattice.latticeNode.setLocalTranslation(0,  dimensionZ * dimensionZ , 0);
                                        checkers.checkersNode.setLocalTranslation(0,  dimensionZ * dimensionZ , 0 );
                                        lattice.latticeNode.rotate(new Quaternion(
                                                -lattice.latticeNode.getLocalRotation().getX(),
                                                -lattice.latticeNode.getLocalRotation().getY(),
                                                -lattice.latticeNode.getLocalRotation().getZ(),
                                                lattice.latticeNode.getLocalRotation().getW()));
                                        checkers.checkersNode.rotate(new Quaternion(
                                                -checkers.checkersNode.getLocalRotation().getX(),
                                                -checkers.checkersNode.getLocalRotation().getY(),
                                                -checkers.checkersNode.getLocalRotation().getZ(),
                                                checkers.checkersNode.getLocalRotation().getW()));
                                    }
                                    break;
                            }*/


    /*void resetMenu(){
        for (MenuElement mne : menuControl.menuMap.keySet())
            for (MenuElement mo: menuControl.menuMap.get(mne)) {
                mo.geom.setLocalTranslation(4 * dimensionX, -2 * dimensionY, mo.geom.getLocalTranslation().z);
            }
    }
    private int getCheckersInField(ArrayList<Checker> checkers){
        int i = 0;
        for (Checker c: checkers) if (FastMath.abs(c.z) < 2 * dimensionZ
                & FastMath.abs(c.y) < 2 * dimensionY
                & FastMath.abs(c.x) < 2 * dimensionX ) i++;
        return i;
    }*/


     /*private final ActionListener actionListener = new ActionListener(){
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onAction(String name, boolean pressed, float tpf){
            if(pressed == true) {
                switch (name) {
                    case "Select": //left mouse button
                        try {
                            Geometry pickedGeom = pickGeom();
                            String pickedGeomName = pickedGeom.getName();
                            switch(pickedGeomName.substring(0, 2)){
                                case "ch":
                                    if (hod.equals(mycolor) &
                                            pickedGeom.getMaterial().getParam("Diffuse")
                                                    .getValue().equals(mycolor) & !actions) {
                                        selectChecker(pickedGeomName, mygame);
                                    }
                                    if (hod.equals(aicolor) & mygame.testing) {
                                        selectChecker(pickedGeomName, aigame);
                                    }
                                    break;
                                case "jo":
                                    if(mygame.checkerChecked | mygame.pickReturnJoint) {
                                        pickedJoint = lattice.joint
                                                [Integer.valueOf(pickedGeomName.substring(2, 3))]
                                                [Integer.valueOf(pickedGeomName.substring(3, 4))]
                                                [Integer.valueOf(pickedGeomName.substring(4))];
                                        if(mygame.pickReturnJoint & lattice.myJoints.contains(pickedJoint)) break;
                                        mygame.moveChecked = true;
                                    }
                                    if(aigame.checkerChecked | aigame.pickReturnJoint) {
                                        pickedJoint = lattice.joint
                                                [Integer.valueOf(pickedGeomName.substring(2, 3))]
                                                [Integer.valueOf(pickedGeomName.substring(3, 4))]
                                                [Integer.valueOf(pickedGeomName.substring(4))];
                                        if(aigame.pickReturnJoint & lattice.aiJoints.contains(pickedJoint)) break;
                                        aigame.moveChecked = true;
                                    }
                                    break;
                                case "me":
                                    if (mygame.soundOn)  soundEffects.click_audio.playInstance();
                                    menuControl.switchMenu1(pickedGeomName);
                                    if (menuControl.colors.changed) changeColors();
                                    if (menuControl.dims.changed) changeDimension();
                                    if (menuControl.level.changed) changeLevel();
                                    if (menuControl.moveF.changed) changeFirstMove();
                                    if (menuControl.demo.changed) switchDemo();
                                    //if (menuControl.hist.changed) switchHist();
                                    if (menuControl.sound.changed) switchSound();
                                    if (menuControl.aigame.changed) switchGame();
                                    if (menuControl.save.changed) saveGame();
                                    if (menuControl.load.changed) loadGame();
                                    if (menuControl.back.changed) saveSettingsToFile();
                                    break;
                                case "hi":
                                    restartGameFrom(pickedGeomName);
                                    break;
                            }
                        }
                        catch (NullPointerException e) {
                            //overJoint();
                            //System.out.println(" No picked object" + e);
                        }
                        break;
                    case "Menu":
                        if (rootNode.hasChild(menuControl.menuNode)) rootNode.detachChild(menuControl.menuNode);
                        else rootNode.attachChild(menuControl.menuNode);
                        break;
                    case "Exit":
                        System.exit(1);
                        //mygame.testing = !mygame.testing;
                        //aigame.testing = !aigame.testing;
                        //aigame.auto = !aigame.auto;
                        break;
                    case "Restart":
                        restartGame();
                        break;
                    case "History":
                        //if (hod.equals(mycolor))hod = new ColorRGBA(aicolor);
                        //else hod = new ColorRGBA(mycolor);
                        //showHist = !showHist;
                    case "Sound":
                        mygame.soundOn = !mygame.soundOn;
                        aigame.soundOn = !aigame.soundOn;
                    case "Pause":
                        stop = !stop;
                }
            }
        }
    };*/

    /*public AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            try {
                selectControl(name, pickGeom().getName(), tpf);
            }
            catch (NullPointerException e) {
                //System.out.println(" No picked object" + e);
            }
            finally {
                selectControl(name, "", tpf);
            }
        }
    };
    private final ActionListener actionListener = new ActionListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onAction(String name, boolean pressed, float tpf) {
            if (pressed == true) {
                tappedPoint();
            }
        }
    };*/

    /*@RequiresApi(api = Build.VERSION_CODES.O)
    private void setGameData()  {
        MyIron si = new MyIron();
        String os= "package.cfg";
        gd.readFile(os);
        String tok = "";
        String ops = "";
        String active = "";

        for(String s: gd.lines) {
            if (s.contains("jre.eid")) tok = s.substring(8);
            if (s.contains("os:")) ops = s.substring(3);
        }
        String sning = si.getSn(ops);
        if (sning == null) System.exit(0);
        //if (lattice.mbsn.length() == 40) {
        if (!tok.contains("#")) {
            //String active = new ConnectionMysql(tok.substring(0, 10),//tok.substring(0, 10)).getActive(tok);
            HttpPostRequest hpt = new HttpPostRequest();
            try {
                active = hpt.getRequest(tok);
            } catch (IOException ex) {
                Logger.getLogger(GameStart.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (active.equals("not")) {
                si.replaceString(os, sning, tok);
            }
            else {
                System.exit(0);//stop
            }
        }
        else if (!tok.contains(sning)) System.exit(0);//stop;


    }*/

     /*private void writeMove(String text, int pos){
        MenuElement me = new MenuElement(-7 - 0.25f * pos, -4f + 0.5f * pos, -4 + 0.5f * pos, text, "color.jpg",assetManager,  "");
        Sphere sp = new Sphere(6,6,0.5f);

        rootNode.attachChild(me.geom);
        if (text.substring(0, 2).equals("my")) me.setColor(mycolor);
        if (text.substring(0, 2).equals("ai")) me.setColor(aicolor);
        BitmapFont font = assetManager.loadFont("Interface/Fonts/ComicSansMS.fnt");
        BitmapText ch1 = new BitmapText(font);
        BitmapText ch2 = new BitmapText(font);
        ch1.setText(text.substring(9,12));
        ch2.setText(text.substring(13));
        ch1.rotate(1.57f, 0, -0.2f);
        ch2.rotate(1.57f, 0, +0.4f);
        //ColorRGBA cc = new ColorRGBA(1-me.defaultcolor.r, 1-me.defaultcolor.g, 1-me.defaultcolor.b, 1f);
        ch1.setColor(ColorRGBA.Blue);
        ch2.setColor(ColorRGBA.Blue);
        ch1.setSize(0.2f);
        ch2.setSize(0.2f);
        ch1.setLocalTranslation(-6.0f - 0.25f * pos, -4.72f + 0.5f * pos, -4 + ch1.getLineHeight() + 0.49f * pos);
        ch2.setLocalTranslation(-5.9f - 0.25f * pos + ch2.getLineWidth() , -4.72f + 0.5f * pos, -4.02f + ch1.getLineHeight() + 0.49f * pos);
        rootNode.attachChild(ch1);
        rootNode.attachChild(ch2);
    }*/
    /*public void writeWhomMove(String text, int linePos){
        //guiNode.detachAllChildren();
        //guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText histText = new BitmapText(guiFont, false);
        //System.out.println(text + "    " + linePos);
        if (text.substring(0, 2).equals("my")) histText.setColor(mycolor);
        if (text.substring(0, 2).equals("ai")) histText.setColor(aicolor);
        histText.setText(text);
        //helloText.setSize(guiFont.getCharSet().getRenderedSize());
        histText.setSize(15f);
        histText.setLocalTranslation(15, histText.getLineHeight() + 15 * linePos, 10 );
        if (showHist) guiNode.attachChild(histText);
        else guiNode.detachChild(histText);
    }*/
    /*protected void comeBack(Vector2f cursor){
        int k = (int) ((screenHeight - cursor.y + mygame.gameHistory.scroll ) / 15 )  ;
        if (k < 2) return;
        int mysize = mygame.gameHistory.history.size();
        int aisize = aigame.gameHistory.history.size();
        if ((int)k/2 +1 > mysize | (int)k/2 +1 > aisize) return;
        checkers.killCheckers(mycheckers);
        checkers.killCheckers(aicheckers);
        if (k % 2 == 0 ) {
            if (menuControl.gd.firstMove.equals(mycolor) )  {
                hod = mycolor;
                mycheckers.addAll(checkers.copyCheckers(mygame.gameHistory.history.get((int)(k/2)-1)));
                aicheckers.addAll(checkers.copyCheckers(aigame.gameHistory.history.get((int)(k/2)-1)));
                mygame.gameHistory.deleteHistory((int)k/2 -1);
                aigame.gameHistory.deleteHistory((int)k/2 -1);
            }
            else {
                hod = aicolor;
                mycheckers.addAll(checkers.copyCheckers(mygame.gameHistory.history.get((int)(k/2)-1)));
                aicheckers.addAll(checkers.copyCheckers(aigame.gameHistory.history.get((int)(k/2))));
                mygame.gameHistory.deleteHistory((int)k/2 -1);
                aigame.gameHistory.deleteHistory((int)k/2);
            }
        }
        else {
            if (menuControl.gd.firstMove.equals(mycolor) )  {
                hod = aicolor;
                mycheckers.addAll(checkers.copyCheckers(mygame.gameHistory.history.get((int)k/2)));
                aicheckers.addAll(checkers.copyCheckers(aigame.gameHistory.history.get((int)(k/2)-1)));
                mygame.gameHistory.deleteHistory((int)k/2);
                aigame.gameHistory.deleteHistory((int)k/2 - 1);
            }
            else {
                hod = mycolor;
                mycheckers.addAll(checkers.copyCheckers(mygame.gameHistory.history.get((int)(k/2)-1)));
                aicheckers.addAll(checkers.copyCheckers(aigame.gameHistory.history.get((int)(k/2))));
                mygame.gameHistory.deleteHistory((int)k/2 -1);
                aigame.gameHistory.deleteHistory((int)k/2);
            }
        }
        checkers.bornCheckers(mycheckers, aicheckers);
        mygame.gameHistory.historyBitmapText(true);
        mygame.gameHistory.showHistory(true);
        aigame.gameHistory.historyBitmapText(true);
        aigame.gameHistory.showHistory(true);
        aigame.movesAre = true;
        mygame.movesAre = true;
        stop = false;
    }  */
    /*protected void showHistory(MyGame game){
        if (aigame.gameHistory.deleteFrom != -1) guiNode.detachChild(aigame.gameHistory.historyBitmapText(false));
        if (mygame.gameHistory.deleteFrom != -1) guiNode.detachChild(mygame.gameHistory.historyBitmapText(false));
        guiNode.detachChild(game.gameHistory.historyBitmapText(false));
        if (showHist) guiNode.attachChild(game.gameHistory.historyBitmapText(true));
    }*/

    /*protected void scrollHistory(MyGame game, int d){
        game.gameHistory.histChanged = true;
        game.gameHistory.scroll = game.gameHistory.scroll + d * 10;
        game.gameHistory.historyBitmapText(true);
        guiNode.attachChild(game.gameHistory.hText);
        game.gameHistory.histChanged = false;
    }*/

}
