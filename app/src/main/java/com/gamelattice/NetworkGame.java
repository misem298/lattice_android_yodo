/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamelattice;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import com.jme3.audio.AudioNode;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
//import static hod;
//import static GameStart.soundEffects;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class NetworkGame {
    protected int clientId, soundRepeator ;
    protected int nrNetHod;
    protected BitmapText bmText;
    protected Boolean netgameInit, soundOn, readed;
    protected String uuid, myHod, gameID, toWrite ;
    protected static String response;
    final private String url;
    private URL myurl;
    private Joint pickedJoint;
    private Checker pickedChecker;
    final private Node guiNode;
    private Context ctx;
    private Button button;
    private EditText editText;

    //private AudioNode an;
    
    protected NetworkGame(BitmapText bmText, Node guiNode, Context ctx) {
        this.gameID = "";
        this.bmText = bmText;
        this.netgameInit = false;
        this.uuid = UUID.randomUUID().toString();
        this.response = "choose new gameID (the same for you and rival): ";
        this.url = "https://gamelattice.com/gm/game_server/create_game.php";
        //this.url = "http://localhost:81/gm/gm/game_server/create_game.php";
        this.myHod = "";
        this.soundRepeator = 20;
        this.soundOn = true;
        this.readed = false;
         this.nrNetHod = 1;
        this.toWrite = "";
        this.guiNode = guiNode;
        this.ctx = ctx;
        //this.an = new AudioNode();
}
    
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void initNetGame(String key, Geometry pickGeom) {
        try {
            setNetGame(key, pickGeom);
        } catch (IOException ex) {
            Logger.getLogger(NetworkGame.class.getName()).log(Level.SEVERE, null, ex);
        }      
    }
    
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void setNetGame(String key, Geometry pickedMenu) throws IOException{
        if(key.substring(0,3).equals("key")) {
            if (key.equals("keyEnt") | key.equals("keyEntnp")) {  
                if (gameID.isEmpty()) {
                    GameStart.writeSomeThing("Wrong name, try again: ", true);
                    GameStart.playSound(GameStart.soundEffects.neon_audio, 10000);
                    response = "Wrong name, try again: ";
                    startActivity();
                    return;
                }
                if(gameID.substring(0,1).matches("[0-9]")) gameID = "x" + gameID; // name of table must be from leter
                response = getRequest("gameid=" + gameID + "&gamer=" + uuid + "&init=" + netgameInit)
                        .substring(response.lastIndexOf("@") + 1).replace("\r", "").replace("\n", "");                                   
                switch (response) {
                    case "try another gameID: ":
                        gameID = "";
                        GameStart.writeSomeThing(response + gameID, true);
                        GameStart.playSound(GameStart.soundEffects.note_audio, 10000);
                        startActivity();
                    break;
                    case "start game, you are gamer1":
                        GameStart.writeSomeThing("Your first move", true);
                        GameStart.playSound(GameStart.soundEffects.cymb_audio, 10000);
                        netgameInit = false;
                        GameStart.firstMove = "my";
                        GameStart.hod = GameStart.mycolor;  //new ColorRGBA(aigame.aicheckers.get(0).defaultcolor);
                    break;
                    case "start game, you are gamer2":
                        GameStart.writeSomeThing("Waiting rival's move", true);
                        GameStart.playSound(GameStart.soundEffects.fanf_audio, 5000);
                        netgameInit = false;
                        GameStart.firstMove = "ai";
                        GameStart.hod = GameStart.aicolor; //new ColorRGBA(aigame.mycheckers.get(0).defaultcolor);
                    break;
                    case "wait gamer2":
                        //System.out.println("intNetGame netgameInit " + netgameInit);
                        GameStart.writeSomeThing("Tell to rival the gameID, wait while he regirstered", true);
                        GameStart.playSound(GameStart.soundEffects.gong_audio, 20000);
                        GameStart.isGameGoing = true;
                    break; 
                    case "the game has been interrupted":
                        GameStart.writeSomeThing("The game has been interrupted, try load it or start new", true);
                        GameStart.playSound(GameStart.soundEffects.gong_audio, 10000);
                        gameID = "";
                        break;
                }           
                return;
            }
            if (key.equals("keyBS") & gameID.length() > 0) gameID = gameID.substring(0,gameID.length()-1);
            else gameID = gameID + key.substring(3,4);
            GameStart.writeSomeThing(response + gameID, true);
            GameStart.playSound(GameStart.soundEffects.click_audio, 5000);
        }
        if (key.equals("Select")) {
            if (pickedMenu != null && pickedMenu.getName().contains("menu") & !pickedMenu.getName().contains("net")) {
                //System.out.println("setNetGame pickedMenu " + pickedMenu.getName());
                //if (!pickedMenu.getName().contains("menumenu")) 
                netgameInit = false;
            }
        }
    }
    
    protected void writeText(String text,  Boolean show, AudioNode an) { 
        if (!show) return;
        bmText.setText(text);
        bmText.setColor(ColorRGBA.randomColor()); 
        bmText.setLocalTranslation(GameStart.screenWidth/2 - bmText.getLineWidth()/2 , 
        GameStart.screenHeight - bmText.getLineHeight()/2 ,   0);  
        guiNode.attachChild(bmText);
        if (soundRepeator == 20) {
                if (soundOn) an.playInstance();
                soundRepeator = 0;
            }
            soundRepeator++;       
    }  
    

    @RequiresApi(api = VERSION_CODES.KITKAT)
    protected String getRequest(String urlParameters) throws IOException {
        HttpURLConnection con = null;   
        myurl = new URL(url);
        
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        try {           
            con = (HttpURLConnection) myurl.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(postData);
            }
            StringBuilder content;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String line;
                content = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }
            response = content.toString();
            //System.out.println(content.toString() + " uuid " + uuid);
        } finally {
            con.disconnect();
        }
            return response;
    }
    
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected String writeHod(String hod) throws IOException{
        return getRequest("gameid=" + gameID + "&gamer=" + uuid + "&init=" + netgameInit + "&hod=" + hod + "&rw=" + "w");
    }
    
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected String readHod(int nr) throws IOException{
        return getRequest("gameid=" + gameID + "&gamer=" + uuid + "&init=" + netgameInit + "&rw=" + "r" + "&nr=" + nr)
                .replace("\r", "").replace("\n", "");
    }   
    
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void playNetGame(String rw, MyGame mygame, MyGame aigame, Lattice lattice) {
        String aimove = "";
        switch (rw.substring(0,5))  {
            case "read ": 
                while (!readed) {
                    try {                //read database
                        aimove = readHod(nrNetHod);
                        System.out.println("nrNetHod  " + nrNetHod );
                        readed = true;                     
                    } catch (IOException ex) {
                        readed = false;
                        GameStart.writeSomeThing("no connection", true);
                        GameStart.playSound(GameStart.soundEffects.chime_audio, 5000);
                        Logger.getLogger(GameStart.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                readed = false;
                if (aimove.contains("wait rival move")) {
                    GameStart.writeSomeThing("waiting rival's move", true);
                    GameStart.playSound(GameStart.soundEffects.chime_audio, 5000);
                    nrNetHod  = nrNetHod + 1;
                    return;
                }                               
                if (aimove.length() > 20) { 
                    GameStart.writeSomeThing("Waiting first rival's move", true);
                    GameStart.playSound(GameStart.soundEffects.chime_audio, 5000);
                    nrNetHod  = nrNetHod + 1;
                    return;
                }                                             
                if (aimove.contains("nomoves")) {
                    GameStart.writeSomeThing("Rival hasn't move, your move", true);
                    GameStart.playSound(GameStart.soundEffects.chime_audio, 5000);
                    nrNetHod  = nrNetHod + 1;
                }                 
                if (aimove.contains("hod") ){         
                    nrNetHod  = nrNetHod + 1;
                    setMove(aimove, lattice, aigame);                                                                                  
                }
                if(aimove.contains("new")) { 
                    pickedJoint = lattice.getJointFromArray(lattice.getOppositeVector(lattice.getVectorByString(aimove.substring(6,9)))); 
                    aigame.returnPickedCheckerToPlay(lattice, pickedJoint);
                    nrNetHod  = nrNetHod + 1;
                    return;
                }
                if(aimove.equals("") & !GameStart.answerYesNo) {
                    GameStart.writeSomeThing("waiting rival's move", true);
                    GameStart.playSound(GameStart.soundEffects.chime_audio, 5000);
                }               
                aigame.goMyGame(pickedJoint, pickedChecker);
                GameStart.picking = false;
                break;          
            case "write": 
                switch (rw.substring(6)) {
                    case "hod":
                        toWrite = "hod" + mygame.moveFrom + mygame.moveTo;                                
                        break;
                    case "new":
                        toWrite = "newche" + mygame.convertToAa1(mygame.checkerToBirth.v);    
                        break;
                    case "no moves":
                    try {    
                        if (!readHod(nrNetHod).equals("")) return;
                        toWrite = "nomoves";
                    } catch (IOException ex) {
                        Logger.getLogger(NetworkGame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                        break;
                }
                try {  // hod 
                    writeHod(toWrite);
                } catch (IOException ex) {
                    Logger.getLogger(GameStart.class.getName()).log(Level.SEVERE, null, ex);
                } 
            break;
        }
    }      
    
    private void setMove(String aimove, Lattice lattice, MyGame aigame) {
        String ch = aimove.substring(3,6);
        String jo = aimove.substring(6,9);                
        pickedJoint = lattice.getJointFromArray(lattice.getOppositeVector(lattice.getVectorByString(jo)));
        for (Checker c : aigame.mycheckers) {
            if (lattice.getOppositeVector(lattice.getVectorByString(ch)).equals(c.v)) {
                pickedChecker = c; 
                selectChecker(pickedChecker, pickedJoint, aigame);
                aigame.moveChecked = true;
                break;
            }
        } 
    }
    
    private void selectChecker(Checker pickedChecker, Joint pickedJoint, MyGame game) {
        if (pickedChecker != null) {
            pickedChecker.resetColor();
            if (pickedJoint != null) {
                pickedJoint.resetColor();
                game.moveChecked = false;
            }       
        //pickedChecker = game.getMyCheckerByGeometryName(pickedChecker.name);
            if (pickedChecker.defaultcolor.equals(GameStart.hod)) {
                GameStart.picking = true;
                GameStart.shiveringTime = System.currentTimeMillis();
                //game.premoving = true;
                //game.preMoveTime = System.currentTimeMillis();
                game.checkerChecked = true;
                game.checkerToMove = pickedChecker;
                GameStart.stop = false;
                if (game.soundOn) GameStart.playSound(GameStart.soundEffects.pick_audio, 0);
                pickedChecker.setBright(3);
                double pause = System.currentTimeMillis();
                while(System.currentTimeMillis() - pause < 1000) {}
            }  
            else {
                game.checkerChecked = false;
            }
        }
    }

    protected void switchNetGame() {
        netgameInit = true;
        GameStart.isGameGoing = true;
        gameID = "";
        nrNetHod = 1;
        response = "choose new gameID (the same for you and rival): ";
        //GameStart.writeSomeThing(response, true);
        //GameStart.playSound(GameStart.soundEffects.chime_audio, 5000);
        startActivity();
    }
    protected void startActivity(){
        GameStart.playSound(GameStart.soundEffects.chime_audio, 5000);
        final Intent intent = new Intent(ctx, NetworkNameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }
    protected Checker getPickedChecker(){
        return pickedChecker;
    }
    protected Joint getPickedJoint(){
        return pickedJoint;
    }
    //if (android.os.Build.VERSION.SDK_INT > 8) {
    //    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
    //            .permitAll().build();
    //    StrictMode.setThreadPolicy(policy);
    //your codes here
    //}
}
