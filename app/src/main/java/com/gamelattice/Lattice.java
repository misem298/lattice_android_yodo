/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamelattice;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;

/**
 *
 * @author Administrator
 */
public class Lattice {
    private AssetManager assetManager;
    protected int dimensionX;
    protected int dimensionY;
    protected int dimensionZ;
    protected Beam[][][][] beam = new Beam[10][10][10][4];
    protected ArrayList<Beam> beamList = new ArrayList<>();
    protected Joint[][][] joint = new Joint[10][10][10];
    protected Node latticeNode ;
    
    protected BitmapText[] textX = new BitmapText[8];  
    protected BitmapText[] textY = new BitmapText[8];  
    protected BitmapText[] textZ = new BitmapText[8]; 
    protected Quaternion  q = new Quaternion();
    protected ArrayList<Joint> myJoints = new ArrayList<>();
    protected ArrayList<Joint> aiJoints = new ArrayList<>();
    protected String mbsn = "ABCDEFGHIJKLMNOabcdefghijklmno0123456789";

    public Lattice(int dX, int dY, int dZ){
        this.latticeNode = new Node();
        this.dimensionX = dX;
        this.dimensionY = dY;
        this.dimensionZ = dZ;
        this.q = q.set(0,0,0,0);
    }

    final Node buildLattice(AssetManager assetManager){
        this.assetManager = assetManager;
        this.beamList.clear();
        ColorRGBA color;
        String index;
        for (int l = 0; l < 2 * dimensionZ; l++ ) {
            if (l == 0 || l == 2 * dimensionZ - 1) color = new ColorRGBA(0.7f, 0.99f, 0.7f, 1);
            else color = new ColorRGBA(0.7f, 0.8f, 0.7f, 1);
            int z = 2 * l - 2 * dimensionZ + 1;
            for (int i = 0; i < 2 * dimensionY; i++) {
                int y = 2 * i - 2 * dimensionY + 1;
                for (int j = 0; j < 2 * dimensionX; j++) {
                    index = String.valueOf(j) + String.valueOf(i) + String.valueOf(l);
                    int x = 2 * j - 2 * dimensionX + 1;
                    if (l % 2 == 0) {
                        if((i + j) % 2 == 0 ) {
                            joint[j][i][l] = new Joint(x, y, z, color, assetManager, "jo" + index);
                            if (l == 0 )  myJoints.add(joint[j][i][l]);
                            latticeNode.attachChild(joint[j][i][l].geom);
                            if (i == 0 && j == 0) {
                                beamList.add(new Beam(x, y + 1, z + 1, -0.78f, 0, assetManager, "be" + index + "0"));// y ^
                                beamList.add(new Beam(x + 1, y, z + 1, 0, 0.78f, assetManager, "be" + index + "1"));  // x >
                            }
                            if (i == 2 * dimensionY - 1 && j == 2 * dimensionX - 1) {
                                beamList.add(new Beam(x, y - 1, z + 1, 0.78f, 0, assetManager, "be" + index + "0"));
                                beamList.add(new Beam(x - 1, y, z + 1, 0, -0.78f, assetManager, "be" + index + "1"));
                            }
                            if (i == 0 && j > 0 ){
                                beamList.add(new Beam(x, y + 1, z + 1, -0.78f, 0, assetManager, "be" + index + "0"));
                                beamList.add(new Beam(x - 1, y, z + 1, 0, -0.78f, assetManager, "be" + index + "1"));
                                beamList.add(new Beam(x + 1, y, z + 1, 0, 0.78f, assetManager, "be" + index + "2"));
                                //latticeNode.attachChild(beam[j][i][l][2].geom);
                            }
                            if (j == 0 && i > 0){
                                beamList.add(new Beam(x, y + 1, z + 1, -0.78f, 0, assetManager, "be" + index + "0"));
                                beamList.add(new Beam(x , y - 1, z + 1, 0.78f, 0, assetManager, "be" + index + "1"));
                                beamList.add(new Beam(x + 1, y, z + 1, 0, 0.78f, assetManager, "be" + index + "2"));
                                //latticeNode.attachChild(beam[j][i][l][2].geom);
                            }
                            if (i == 2 * dimensionY - 1 && j < 2 * dimensionX - 1 ) {
                                beamList.add(new Beam(x + 1, y , z + 1, 0, 0.78f, assetManager, "be" + index + "0"));
                                beamList.add(new Beam(x - 1, y, z + 1, 0, -0.78f, assetManager, "be" + index + "1"));
                                beamList.add(new Beam(x, y - 1, z + 1, 0.78f, 0, assetManager, "be" + index + "2"));
                                //latticeNode.attachChild(beam[j][i][l][2].geom);
                            }
                            if (j == 2 * dimensionX - 1 && i < 2 * dimensionY - 1 ) {
                                beamList.add(new Beam(x, y + 1, z + 1, -0.78f, 0, assetManager, "be" + index + "0"));
                                beamList.add(new Beam(x - 1, y, z + 1, 0, -0.78f, assetManager, "be" + index + "1"));
                                beamList.add(new Beam(x, y - 1, z + 1, 0.78f, 0, assetManager, "be" + index + "2"));
                                //latticeNode.attachChild(beam[j][i][l][2].geom);
                            }
                            if(i > 0 && i < 2 * dimensionY - 1 && j > 0 && j < 2 * dimensionX - 1 ) {
                                beamList.add(new Beam(x, y + 1, z + 1, -0.78f, 0, assetManager, "be" + index + "0"));
                                beamList.add(new Beam(x - 1, y, z + 1, 0, -0.78f, assetManager, "be" + index + "1"));
                                beamList.add(new Beam(x + 1, y, z + 1, 0, 0.78f, assetManager, "be" + index + "2"));
                                beamList.add(new Beam(x, y - 1, z + 1, 0.78f, 0, assetManager, "be" + index + "3"));
                                //latticeNode.attachChild(beam[j][i][l][2].geom);
                                //latticeNode.attachChild(beam[j][i][l][3].geom);
                            }
                        }
                    }
                    else {
                        if ((i + j) % 2 != 0 ) {
                            joint[j][i][l] = new Joint(x, y, z, color, assetManager, "jo" + index);
                            if (l == 2 * dimensionZ  - 1)  aiJoints.add(joint[j][i][l]);
                            latticeNode.attachChild(joint[j][i][l].geom);
                            if (l < 2 * dimensionZ - 1) {
                                if (i == 0 && j == 2 * dimensionX - 1) {
                                    beamList.add(new Beam(x, y + 1, z + 1, -0.78f, 0, assetManager, "be" + index + "0"));// y ^
                                    beamList.add(new Beam(x - 1, y, z + 1, 0, -0.78f, assetManager, "be" + index + "1"));  // x >
                                }
                                if (i == 2 * dimensionY - 1 && j == 0) {
                                    beamList.add(new Beam(x, y - 1, z + 1, 0.78f, 0, assetManager, "be" + index + "0"));
                                    beamList.add(new Beam(x + 1, y, z + 1, 0, 0.78f, assetManager, "be" + index + "1"));
                                }
                                if (i == 0 && j < 2 * dimensionX - 1 ){
                                    beamList.add(new Beam(x, y + 1, z + 1, -0.78f, 0, assetManager, "be" + index + "0"));
                                    beamList.add(new Beam(x - 1, y, z + 1, 0, -0.78f, assetManager, "be" + index + "1"));
                                    beamList.add(new Beam(x + 1, y, z + 1, 0, 0.78f, assetManager, "be" + index + "2"));
                                    //latticeNode.attachChild(beam[j][i][l][2].geom);
                                }
                                if (j == 0 && i < 2 * dimensionY - 1){
                                    beamList.add(new Beam(x, y + 1, z + 1,  -0.78f, 0, assetManager, "be" + index + "0"));
                                    beamList.add(new Beam(x , y - 1, z + 1, 0.78f, 0, assetManager, "be" + index + "1"));
                                    beamList.add(new Beam(x + 1, y, z + 1, 0, 0.78f, assetManager, "be" + index + "2"));
                                    //latticeNode.attachChild(beam[j][i][l][2].geom);
                                }
                                if (i == 2 * dimensionY - 1 && j > 0 ) {
                                    beamList.add(new Beam(x + 1, y , z + 1, 0, 0.78f, assetManager, "be" + index + "0"));
                                    beamList.add(new Beam(x - 1, y, z + 1, 0, -0.78f, assetManager, "be" + index + "1"));
                                    beamList.add(new Beam(x, y - 1, z + 1, 0.78f, 0, assetManager, "be" + index + "2"));
                                    //latticeNode.attachChild(beam[j][i][l][2].geom);
                                }
                                if (j == 2 * dimensionX - 1 && i > 0 ) {
                                    beamList.add(new Beam(x, y + 1, z + 1, -0.78f, 0, assetManager, "be" + index + "0"));
                                    beamList.add(new Beam(x - 1, y, z + 1, 0, -0.78f, assetManager, "be" + index + "1"));
                                    beamList.add(new Beam(x, y - 1, z + 1, 0.78f, 0, assetManager, "be" + index + "2"));
                                    //latticeNode.attachChild(beam[j][i][l][2].geom);
                                }
                                if (i > 0 && i < 2 * dimensionY - 1 && j > 0 && j < 2 * dimensionX - 1 ) {
                                    beamList.add(new Beam(x, y + 1, z + 1, -0.78f, 0, assetManager, "be" + index + "0"));
                                    beamList.add(new Beam(x - 1, y, z + 1, 0, -0.78f, assetManager, "be" + index + "1"));
                                    beamList.add(new Beam(x + 1, y, z + 1, 0, 0.78f, assetManager, "be" + index + "2"));
                                    beamList.add(new Beam(x, y - 1, z + 1, 0.78f, 0, assetManager, "be" + index + "3"));
                                    //latticeNode.attachChild(beam[j][i][l][2].geom);
                                    //latticeNode.attachChild(beam[j][i][l][3].geom);
                                }
                                //latticeNode.attachChild(beam[j][i][l][0].geom);
                                //latticeNode.attachChild(beam[j][i][l][1].geom);
                            }
                        }
                    }
                }
            }
        }
        for (Beam b: beamList) latticeNode.attachChild(b.geom);
        latticeNode.setLocalTranslation(0,  dimensionZ * dimensionZ , 0 );
        return latticeNode;
    }

    public Node abcdLattice(AssetManager assetManager ){
        Quaternion m = new Quaternion();
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        String[] upCase = {"A","B","C","D","E","F","G","H"};
        String[] loCase = {"a","b","c","d","e","f","g","h"};
        m.set(1,0,0,1);
        float y = -2 * dimensionY + 0.2f;
        float z = -2 * dimensionZ + 0.4f;
        for(int l = 0; l < 2 * dimensionX; l++ ) {
            float x = 2 * l - 2 * dimensionX + 1;
            textX[l] = new BitmapText(font, false);
            //setText(textX[l], upCase[l], l, x, y, z, m);
            textX[l].setColor(new ColorRGBA(0.7f, 0.99f, 0.7f, 1));
            //textX[l].setAlpha(0.5f);
            textX[l].setSize(0.2f);
            // textX[l].setStyle(0, 1, 3);
            //textX[l].setStyle(0, 1, 1);
            textX[l].setText(upCase[l]); // + String.valueOf(x)
            textX[l].setLocalTranslation(x - textX[l].getLineWidth()/2, y , z + 0.5f);
            textX[l].setLocalRotation(m);
            latticeNode.attachChild(textX[l]);
        }

        float x = -2 * dimensionX + 0.4f;
        z = - 2 * dimensionZ + 0.4f;
        for (int l = 0; l < 2 * dimensionY; l++ ) {
            y = 2 * l - 2 * dimensionY + 1 + 0.1f;
            textY[l] = new BitmapText(font, false);
            textY[l].setColor(new ColorRGBA(0.7f, 0.99f, 0.7f, 1));
            //textY[l].setAlpha(0.5f);
            textY[l].setSize(0.3f);
            textY[l].setText( loCase[l]);
            //setText(textY[l], loCase[l], l, x, y, z, m);
            textY[l].setLocalTranslation(x - textY[l].getLineWidth()/2, y , z + 0.5f);
            textY[l].setLocalRotation(m);
            latticeNode.attachChild(textY[l]);
        }

        x =  -2 * dimensionX  + 0.4f;
        y =  -2 * dimensionY + 0.2f;
        for(int l = 0; l < 2 * dimensionZ; l++ ) {
            z = 2 * l - 2 * dimensionZ + 1;
            textZ[l] = new BitmapText(font, false);
            //setText(textZ[l], String.valueOf(l + 1), l, x, y, z, m);
            textZ[l].setColor(new ColorRGBA(0.7f, 0.99f, 0.7f, 1));
            // textZ[l].setAlpha(0.5f);
            textZ[l].setSize(0.2f);
            textZ[l].setText(String.valueOf(l + 1));   //+ String.valueOf(z)
            textZ[l].setLocalTranslation(x - textZ[l].getLineWidth()/2, y, z );
            textZ[l].setLocalRotation(m);
            latticeNode.attachChild(textZ[l]);
        }
        return latticeNode;
    }

    protected void addRotate(){
        //Quaternion  p = new Quaternion();
        for (int i=0; i < 2 * dimensionX; i++) textX[i].setLocalRotation(q);
        for (int i=0; i < 2 * dimensionY; i++) textY[i].setLocalRotation(q);
        for (int i=0; i < 2 * dimensionZ; i++) textZ[i].setLocalRotation(q);
    }

    protected void resetAllSpecular(){
        //for (Spatial ce: latticeNode.getChildren()) {
        //getControlElement(ce.getName()).material.clearParam("Specular");
        //getControlElement(ce..getName()).material.clearParam("Shininess");
    }

    protected Joint getJointByVector(Vector3f v) {
        for (Joint j: myJoints ) {
            //System.out.println("getJointByVector" + j.v + "  " + v);
            if (j.v.equals(v)) return j;
        }
        for (Joint j: aiJoints ) {
            //System.out.println("getJointByVector" + j.v + "  " + v);
            if (j.v.equals(v)) return j;
        }
        return null;
    }

    protected void resetMyAiJoints() {
        for (Joint j: myJoints ) {
            j.resetColor();
            j.geom.setLocalTranslation(j.x, j.y, j.z);
            j.mesh.updateGeometry(6, 6, 0.2f);
        }
        for (Joint j: aiJoints ) {
            j.resetColor();
            j.geom.setLocalTranslation(j.x, j.y, j.z);
            j.mesh.updateGeometry(6, 6, 0.2f);
        }
    }

    protected void resetJoints(ArrayList<Joint> joints) {
        for (Joint j: joints ) {
            j.resetColor();
            j.geom.setLocalTranslation(j.x, j.y, j.z);
            j.mesh.updateGeometry(6, 6, 0.2f);
        }
    }

    protected Beam getBeamByGeometryName(String gName) {
        for (Beam b: beamList) {
            if (b.geom.getName().equals(gName)) return b;
        }
        return null;
    }

    protected Joint getJointFromArray(Vector3f v) {
        for (int i=0; i < 2 * dimensionX; i++) {
            for (int j=0; j < 2 * dimensionY; j++){
                for (int k=0; k < 2 * dimensionZ; k++) {
                    if (joint[i][j][k] != null) {
                        //System.out.println(joint[i][j][k].v + "     v  "  + v);
                        if (joint[i][j][k].v.equals(v)) return joint[i][j][k];
                    }
                }
            }
        }
        return null;
    }

    protected Vector3f getVectorByString(String abc){
        return new Vector3f((int)(((int) (abc.substring(0).charAt(0)) - 65) * 2) - (2 * dimensionX -1) ,
                (int)(((int) (abc.substring(1,2).charAt(0))- 97) * 2) - (2 * dimensionY -1),
                (int)(((int) (abc.substring(2,3).charAt(0)) - 49) * 2) - (2 * dimensionZ -1));
    }

    protected Vector3f getOppositeVector(Vector3f v){
        return new Vector3f(v.x, -v.y, - v.z);
    }

    protected void showFreeJoints(ArrayList<Vector3f> freeJoints) {
        for (Vector3f v: freeJoints) {
            getJointByVector(v).shiverJoint();
        }
    }
}


   
