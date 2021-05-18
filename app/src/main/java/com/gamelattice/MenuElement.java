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
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;

/**
 *
 * @author Administrator
 */
public class MenuElement {
    AssetManager assetManager;
    protected float x;
    protected float y;
    protected float z;
    protected String name;
    protected String value, text;
    protected Quaternion q;
    //public Geometry geo;
    public Sphere mesh;
    public Geometry geom;
    public Material material;
    public ColorRGBA color;
    public ColorRGBA bgcolor;
    public ColorRGBA defaultcolor;
    public boolean changed;
    public boolean selected;
    protected Vector3f v;
    protected BallTextures bt;

    public MenuElement(float x, float y, float z, String geoName, 
                       ColorRGBA bgColor, AssetManager am, String value) {
        this.assetManager = am;
        this.x = x - 0.2f + 1;
        this.y = y;
        this.z = z * 0.9f - 1f;
        this.value = value;
        this.v = new Vector3f();
        this.v.x = x - 0.2f;
        this.v.y = y;
        this.v.z = z * 0.9f - 1f;
        this.q = new Quaternion();
        this.color = bgColor;
        this.color = ColorRGBA.LightGray;
        this.name = "menu" + geoName;
        this.defaultcolor = ColorRGBA.LightGray;
        this.changed = false;
        this.selected = false;
        this.text = geoName.substring(4);
        bt = new BallTextures(text, ColorRGBA.Black, bgColor);
        createMaterial(bt.texture, bgColor);
        createGeometry(name);
    }
    
    private void createGeometry(String name) {
        mesh =  new Sphere(10,10, 0.4f);
        //TangentBinormalGenerator.generate(mesh);
        geom = new Geometry(name, mesh);
        geom.setMaterial(material);
        geom.setLocalTranslation(this.x, this.y, this.z);
        geom.setLocalRotation(new Quaternion(0, 0, -1 , -1)); 
    }
    
    private void createMaterial(Texture texture, ColorRGBA color) {
        material = new Material(assetManager,"/Common/MatDefs/Light/Lighting.j3md");
        material.setTexture("DiffuseMap", texture); 
        material.setBoolean("UseMaterialColors",true);
        material.setColor("Diffuse", color);
        //material.setColor("Specular",ColorRGBA.White);
        material.setFloat("Shininess", 128f);  // [0,128]       
    }

    public Geometry setColor(ColorRGBA color){
        this.color = color;
        material.setColor("Diffuse",color);
        //material.setColor("Specular",color);
        //material.setFloat("Shininess", 128f);  // [0,128]
        geom.setMaterial(material);
        return geom;
    }
    public void resetColor(){
        this.color = this.defaultcolor;
        material.setColor("Diffuse",this.defaultcolor);
        //material.clearParam("Specular");
        //material.setFloat("Shininess", 128f);  // [0,128]
        geom.setMaterial(material);
    }
    public void hideOne(){
        geom.setLocalTranslation(100,100,100);
    }
    public void showOne(){
        geom.setLocalTranslation(this.x, this.y, this.z);
    }

    protected void setSpecular(){
        //this.color = this.defaultcolor;
        //material.setColor("Diffuse",this.defaultcolor);
        material.setColor("Specular",ColorRGBA.Cyan);
        material.setFloat("Shininess", 12f);  // [0,128]
        geom.setMaterial(material);
    }
    protected void resetMenuElement(){
        material.setColor("Specular",ColorRGBA.White);
        material.setFloat("Shininess", 128f);  // [0,128]]
        selected = false;
    }
    protected void selectMenuElement(ColorRGBA color){
        material.setColor("Specular", color);
        material.setFloat("Shininess", 8f);  // [0,128]]
        selected = true;
    }
    protected void resetSpecular(){
        material.clearParam("Specular");
        material.clearParam("Shininess");
    }
    private ColorRGBA getColorByName(String colorName){
        ColorRGBA colorRGBA = new ColorRGBA(1,1,1,1);
        return colorRGBA;
    }
}
