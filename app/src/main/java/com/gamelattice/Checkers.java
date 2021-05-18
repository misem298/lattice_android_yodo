/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamelattice;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;

/**
 *
 * @author Administrator
 */
public class Checkers {
    protected AssetManager assetManager;
    protected Node checkersNode;
    protected Node benchNode ;
    protected int dimensionX;
    protected int dimensionY;
    protected int dimensionZ;
    //protected ColorRGBA color;
    protected Checker[][][] cheker = new Checker[10][10][10];
    //public Cheker[][][] chekerWhite = new Cheker[10][10][10];
    
    protected Checkers(AssetManager assetManager,int dX, int dY, int dZ){
        checkersNode = new Node();
        benchNode = new Node();
        benchNode.setLocalTranslation(0, 10, 0);
        this.dimensionX = dX; 
        this.dimensionY = dY;
        this.dimensionZ = dZ;
        this.assetManager = assetManager;
        
        //this.color = ColorRGBA.Black;
    }
    protected Node builtCheckers(ColorRGBA mycolor,
                ColorRGBA aicolor, ArrayList mycheckers, ArrayList aicheckers) {
        for(int k = 0; k < dimensionZ ; k = k + 2){
        for(int i= 0; i < 2 * dimensionY; i++){
            int y = 2 * i - 2 * dimensionY + 1;
            for(int j = 0; j < 2 * dimensionX; j++){
                int x = 2 * j - 2 * dimensionX + 1;
                if((i + j + k/2) % 2 == 0 ) { 
                    cheker[j][i][k] = new Checker(x, y, -2 * dimensionZ + 1 + k, mycolor);
                    cheker[j][i][k].startZ = -2 * dimensionZ + 1;
                    mycheckers.add(cheker[j][i][k]);
                    checkersNode.attachChild(cheker[j][i][k].drawObj(assetManager,  
                            "ch" + String.valueOf(j) + String.valueOf(i) + String.valueOf(k)));
                }
                if((i + j + k/2) % 2 != 0 ) {
                    cheker[j][i][k + 1] = new Checker(x, y, + 2 * dimensionZ - 1 - k, aicolor);
                    cheker[j][i][k + 1].startZ = 2 * dimensionZ - 1;
                    aicheckers.add(cheker[j][i][k + 1]);
                    checkersNode.attachChild(cheker[j][i][k + 1].drawObj(assetManager, 
                            "ch" + String.valueOf(j) + String.valueOf(i) + String.valueOf(k + 1)));
                }
            }
        }
        }
        //checkersNode.rotate(- 1.57f, 0, 0);
        checkersNode.setLocalTranslation(0,  dimensionZ * dimensionZ , 0 );
        
    return checkersNode;
    }
    
    protected void bornCheckers(ArrayList<Checker> mycheckers, ArrayList<Checker> aicheckers){
        int ki = 0;
        int kj = 0;
        for(int k = 0; k < dimensionZ ; k = k + 2){
        for(int i= 0; i < 2 * dimensionY; i++){
            for(int j = 0; j < 2 * dimensionX; j++){
                if((i + j + k/2) % 2 == 0 ) { 
                    cheker[j][i][k] = mycheckers.get(ki);
                    cheker[j][i][k].startZ = -2 * dimensionZ + 1;
                    cheker[j][i][k].drawObj(assetManager, 
                            "ch" + String.valueOf(j) + String.valueOf(i) + String.valueOf(k));
                    if (isVectorIn(cheker[j][i][k].v)) checkersNode.attachChild(cheker[j][i][k].geom);
                    if (cheker[j][i][k].mamka) cheker[j][i][k].transformToDamka(1);
                    ki++;
                }
                if((i + j + k/2) % 2 != 0 ) {
                    cheker[j][i][k + 1] = aicheckers.get(kj);
                    cheker[j][i][k + 1].startZ = 2 * dimensionZ - 1;
                    cheker[j][i][k + 1].drawObj(assetManager, 
                            "ch" + String.valueOf(j) + String.valueOf(i) + String.valueOf(k + 1));
                    if (isVectorIn(cheker[j][i][k + 1].v)) checkersNode.attachChild(cheker[j][i][k + 1].geom);
                    if (cheker[j][i][k + 1].mamka) cheker[j][i][k + 1].transformToDamka(1);
                    kj++;
                }    
            }
        }
        }
    }
    
    protected void killCheckers(ArrayList<Checker> checkers){
        for (Checker c: checkers) {
            try{
                checkersNode.detachChild(c.geom);
            }
            catch (NullPointerException e) {
                 // System.out.println(" No picked object" + e);
            } 
            c = null;
        }
        checkers.clear();
    }
    private boolean isVectorIn(Vector3f v){
        return (Math.abs(v.x) < 2 * dimensionX & Math.abs(v.y) < 2 * dimensionY & Math.abs(v.z) < 2 * dimensionZ );
    }
    protected ArrayList<Checker> copyCheckers(ArrayList<Checker> checkers) {
        ArrayList<Checker> checkersCopy = new ArrayList<>();
        for (Checker c: checkers) {
        Checker cCopy = new Checker(c.x, c.y, c.z, c.color);
        cCopy.damka = c.damka;
        cCopy.mamka = c.mamka;
        checkersCopy.add(cCopy);
        }
        return checkersCopy;
    }
    
    public Vector3f checkerExist(int x, int y, int z) {
        Vector3f vect = new Vector3f();
        vect.x = 0;
        vect.y = 0;
        vect.z = 100;
        for(int i= 0; i < 2 * this.dimensionY; i++){
            for(int j = 0; j < 2 * this.dimensionX; j++){
                //System.out.println(this.dimension + " xxx " + j + i +  "  x" + x + "  y" + y + "   z" +z);
                if((i + j) % 2 == 0) {
                    if (cheker[j][i][0].x == x && cheker[j][i][0].y == y && cheker[j][i][0].z == z) {
                        vect.x = j;
                        vect.y = i;
                        vect.z = 0;
                        return vect;
                    }
                } 
                if ((i + j) % 2 != 0) {
                    if (cheker[j][i][1].x == x && cheker[j][i][1].y == y && cheker[j][i][1].z == z) {
                        vect.x = j;
                        vect.y = i;
                        vect.z = 1;
                        return vect;
                    }   
                }   
            }
        }
        return vect;
    }
   
   // public void deleteCheker(Vector3f vect) {
   //     cheker[(int)vect.x][(int)vect.y][(int)vect.z].move(100, 100, 100); 
   // }
}
