/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamelattice;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;

/**
 *
 * @author Administrator
 */
public class Ball {
    final private AssetManager am;
    protected Sphere mesh;
    protected Geometry geom;
    protected Material material;
    //protected Node ballsNode;
    protected ColorRGBA color;
    protected ColorRGBA defaultcolor;
    //private BallTextures bt;

    protected Ball(AssetManager am, String[] text, ColorRGBA textColor, ColorRGBA bgColor) {
        this.am = am;
        this.color = new ColorRGBA(bgColor);
        this.defaultcolor = new ColorRGBA(bgColor);
        BallTextures bt = new BallTextures(text[1] + "%" + text[2] + "%" + text[3], textColor, bgColor);
        createMaterial(bt.texture);
        createGeometry(text[0] + "#" + text[1] + "%" + text[2] + "%" + text[3]);
    }

    private void createMaterial(Texture texture) {
        material = new Material(am,"/Common/MatDefs/Light/Lighting.j3md");
        material.setTexture("DiffuseMap", texture); 
        material.setBoolean("UseMaterialColors",true);
        material.setColor("Diffuse",ColorRGBA.White);
        material.setColor("Specular",ColorRGBA.White);
        material.setFloat("Shininess", 0f);  // [0,128]
    }
    
    private void createGeometry(String name) {
        mesh =  new Sphere(10,10, 0.4f);
        //TangentBinormalGenerator.generate(mesh);
        geom = new Geometry(name, mesh);
        geom.setMaterial(material);
        geom.setLocalTranslation(0, 0, 0);
        Quaternion q = new Quaternion();
        geom.setLocalRotation(q.set(0, 0, -1f, -0.8f)); 
    }
    
    protected void setSpecular(float shine){
        material.setColor("Specular",defaultcolor);
        material.setFloat("Shininess", shine);  // [0,128]
        geom.setMaterial(material);
    }
    
    protected void setGeomSpecular(Geometry ge){
        material.setColor("Specular",defaultcolor);
        material.setFloat("Shininess", 12);  // [0,128]
        ge.setMaterial(material);
    }
    protected void resetSpecular(){
        material.clearParam("Specular");
        material.clearParam("Shininess");
    }
     
}
