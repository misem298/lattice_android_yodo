/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamelattice;

import android.os.Build;

import androidx.annotation.RequiresApi;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Administrator
 */
public final class GameControl {
   // protected ControlElement up;
   // protected ControlElement down;
   // protected ControlElement left;
   // protected ControlElement right;
   // protected ControlElement closer;
   // protected ControlElement farther;
    private ControlElement exit;
    private ControlElement restart;
    //protected ControlElement soundLess;
    //protected ControlElement soundMore;
    private ControlElement centr;
    private ControlElement pause;
    protected Node controlNode ;
    protected float x, y, z;
    
    
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GameControl(AssetManager assetManager) {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.controlNode = new Node();
        //this.up = new ControlElement(-0.9f, 3f, -4.5f, "up", "%up",assetManager,  100);
        //this.down = new ControlElement(0.9f, 3f, -4.5f, "down", "%down",assetManager,  100);
        //this.left = new ControlElement(-1.8f, 3f, -4.5f, "left", "%left",assetManager,  100);
        //this.right = new ControlElement(1.8f, 3f, -4.5f, "right", "%right",assetManager, 100);
        //this.closer = new ControlElement(-2.7f, 3f, -4.5f, "farther", "%farther",assetManager,  60);
        //this.farther = new ControlElement(-3.6f, 3f, -4.5f, "closer", "%closer",assetManager, 60);
        this.centr = new ControlElement(x+1.8f, y+0.5f, z, "centr", "%centr",assetManager, 100);
        //this.exit = new ControlElement(x+4.5f, y+0.5f, z, "exit", "exit ",assetManager, 60);
        //this.restart = new ControlElement(x+2.7f, y+0.5f, z, "rest", "start ",assetManager, 60);
        //this.pause = new ControlElement(x+3.6f, y+0.5f, z, "pause", "pause",assetManager, 60);
        //this.soundLess = new ControlElement(-4.5f, 3f, -4.5f, "soundLess", "  vol-",assetManager, 60);
        //this.soundMore = new ControlElement(-5.4f, 3f, -4.5f, "soundMore", "  vol+",assetManager, 60);
        switchOn();
    }
    
    protected void switchOff() {
        this.controlNode.detachAllChildren();      
    }
     protected void switchOn() {
//        this.controlNode.attachChild(up.geom);
//        this.controlNode.attachChild(down.geom);
//        this.controlNode.attachChild(left.geom);
//        this.controlNode.attachChild(right.geom);
        //this.controlNode.attachChild(exit.geom);
        //this.controlNode.attachChild(restart.geom);
        //this.controlNode.attachChild(soundLess.geom);
        //this.controlNode.attachChild(soundMore.geom);
        this.controlNode.attachChild(centr.geom);
        //this.controlNode.attachChild(closer.geom);
        //this.controlNode.attachChild(farther.geom);
        //this.controlNode.attachChild(pause.geom);
    }
     protected ControlElement getControlElement(String name) {
        // if (up.name.equals(name)) return up;
        // if (down.name.equals(name)) return down;
        // if (left.name.equals(name)) return left;
       //  if (right.name.equals(name)) return right;
         if (exit.name.equals(name)) return exit;
         if (restart.name.equals(name)) return restart;
       //  if (soundLess.name.equals(name)) return soundLess;
      //   if (soundMore.name.equals(name)) return soundMore;
         if (centr.name.equals(name)) return centr;
         return null;
     }
    protected void resetAllSpecular(){
        for (Spatial ce: controlNode.getChildren()) {
            //getControlElement(ce.getName()).material.clearParam("Specular");
            getControlElement(ce.getName()).material.clearParam("Shininess");
        }
    }
}
