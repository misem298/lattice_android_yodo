/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamelattice;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import java.util.ArrayList;

/**
 *
 * @author Administrator
 */
public class GameHistory {
    protected ArrayList<String> movesHistory;
    protected ArrayList<Ball> historyBalls;
    //protected ArrayList<ArrayList<Vector3f>> histData;
    protected ArrayList<ArrayList<Checker>> history;
    protected boolean histChanged;
    protected ArrayList<Checker> ownCheckers;
    protected String moveFrom, moveTo;
    protected int scroll, shift;
    protected int deleteFrom;
    protected Node ballNode;
    final private AssetManager assetManager;
    protected float x, y, z;
    
    protected GameHistory(AssetManager assetManager){
        this.x = 0f;
        this.y = 0;
        this.z = 0;
        this.assetManager = assetManager;
        this.movesHistory = new ArrayList<>();
        this.historyBalls = new ArrayList<>();
        this.history = new ArrayList<>(); 
        //this.histNode = new Node();
        this.ballNode = new Node();
        this.histChanged = false; 
        this.scroll = 0;
        this.shift = 15;
        this.deleteFrom = -1;
    }
    

    protected void reccordHistory(String gamer, ArrayList<Checker> ownCheckers,
                                  String moveFrom, String moveTo, String moveKind) {
        //System.out.println("reccordHistory " + gamer + moveTo + "" + ownCheckers.get(0).defaultcolor); 
        if (deleteFrom != -1) deleteHistory(deleteFrom);
        this.ownCheckers = ownCheckers;
        ArrayList<Checker> ownCheckersCopy = new ArrayList();
        for (Checker c: ownCheckers) {
            Checker cCopy = new Checker(c.x, c.y, c.z, c.color);
            cCopy.damka = c.damka;
            cCopy.mamka = c.mamka;
            ownCheckersCopy.add(cCopy);
        }
        history.add(ownCheckersCopy);
        //System.out.println("a history " + history);
        movesHistory.add(gamer + " move: " + moveFrom + "-" + moveTo);
        String[] moves = new String[]{"hi" + gamer + String.valueOf(history.size()) ,
                                      moveFrom, moveTo, moveKind};
        Ball h = new Ball(assetManager, moves, ownCheckers.get(0).defaultcolor, ColorRGBA.White);
        historyBalls.add(h);
        histChanged = true;
        attachHistory();   
    }
    
    protected void attachHistory(){
        int column;
        int line = 0;
        String mover1 = historyBalls.get(0).geom.getName().substring(2,4);
        String moverPre = mover1;
        for (int i = 0; i < historyBalls.size(); i++ ) {
            String moverNow = historyBalls.get(i).geom.getName().substring(2,4);
            if (i > 0) moverPre = historyBalls.get(i-1).geom.getName().substring(2,4);
            if (moverPre.equals(moverNow))  line++;
            else  if (moverNow.equals(mover1)) line++;
            if (moverNow.equals("my")) column = -1;
            else column = 1;
            //historyBalls.get(i).geom.setLocalTranslation(-9f + column/2f, 4, scroll/10 - 6 + 
            //       line * historyBalls.get(i).mesh.radius * 2 );
            historyBalls.get(i).geom.setLocalTranslation(x + column/2f, 0, z + line * 0.8f);
            //System.out.println("ballHist " + (-9f + column/2f) + "  " + (scroll/10 - 5 + 
            //       line * 0.8f ) );
            if (!historyBalls.get(i).geom.getName().contains("---")) ballNode.attachChild(historyBalls.get(i).geom);
        }
    }
    
    protected void deleteHistoryBalls(int i) {
        ballNode.detachAllChildren();  
        while (historyBalls.size() > i + 1) historyBalls.remove(historyBalls.size()-1);
         while (history.size() > i + 1) history.remove(history.size()-1);
        while (movesHistory.size() > i + 1) movesHistory.remove(movesHistory.size()-1);
        for (Ball b: historyBalls)  if (!b.geom.getName().contains("---")) ballNode.attachChild(b.geom);
    }
    
    protected void deleteHistory(int i) {
        deleteFrom = -1;
        while (history.size() > i + 1) history.remove(history.size()-1);
        while (movesHistory.size() > i + 1) movesHistory.remove(movesHistory.size()-1);
        histChanged = true;
    }

    protected void deleteAll(){
        historyBalls.clear();
        movesHistory.clear();
        history.clear();       
        ballNode.detachAllChildren();
     }

    // protected Ball getBallByGeomName(String )
    /*
    protected void scrollHistory( int d){
        if(historyBalls.size() == 0) return;
        histChanged = true;
        scroll = scroll + d * 10;
        attachHistory();
        histChanged = false;
    }
    protected void historyBitmapText(boolean showHist){
        histNode.attachChild(this.hText);
        if(!showHist) return ;
        if(!histChanged) return ;
        int linePos = 0;
        this.hText.setSize(20f);
        this.hText.setColor(ownCheckers.get(0).defaultcolor);
        String txt = "";
        //System.out.println(" linePos " + linePos + " aisize " + aihistList.size() + "mysize" + myhistList.size() );
        while (linePos < movesHistory.size() ) {
            txt = txt + movesHistory.get(linePos) + "\n";            
            linePos++;
        }
        this.hText.setText(txt);
        this.hText.setLocalTranslation(10, scroll - shift + Main.screenHeight, 0);
        histChanged = false;
    }
    
    
    
    protected void showHistory(boolean showHist) {
        guiNode.detachChild(hText);
        if (showHist) guiNode.attachChild(hText);
    }
    
   
    
    protected Spatial getHistoryNodeSpatial(String gn){
        for (Spatial g : ballNode.getChildren()) {
            if (g.getName().equals(gn)) return g;
        }
        return null;
    }
    protected Ball getHistoryBallByName(String gn){
        for (Ball b : historyBalls) {
            if (b.geom.getName().equals(gn)) return b;
        }
        return null;
    }
    protected void resetAllSpecular(){
        for (Ball b : historyBalls) {
            b.geom.getMaterial().clearParam("Shininess");
        }
    }
    */
}
