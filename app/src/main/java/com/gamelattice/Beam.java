/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamelattice;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;
import com.jme3.util.TangentBinormalGenerator;

/**
 *
 * @author Administrator
 */
public class Beam {
    protected int x;
    protected int y;
    protected int z;
    private float xz;
    private float yz;
    public Cylinder mesh;
    public Geometry geom;
    public Geometry show;
    public Geometry hide;
    public Material material;
    public ColorRGBA color;
   
    
    
    public Beam(int x, int y, int z,  float yz, float xz, AssetManager assetManager, String name) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.xz = xz;
    this.yz = yz;
    this.color = ColorRGBA.Yellow;
    mesh = new Cylinder(6, 6, 0.04f, 2.8f, false, false);
        geom = new Geometry(name, mesh);
        //TangentBinormalGenerator.generate(mesh); 
        material = new Material(assetManager,"/Common/MatDefs/Light/Lighting.j3md");
        material.setTexture("DiffuseMap",assetManager.loadTexture("Textures/color.jpg"));
        //material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        material.setBoolean("UseMaterialColors",true);
        material.setColor("Diffuse",ColorRGBA.Yellow);
        //material.setColor("Specular",ColorRGBA.Yellow);
        material.setFloat("Shininess", 64f);  // [0,128]
        geom.setMaterial(material);
        geom.setLocalTranslation(this.x, this.y, this.z);
        geom.rotate(this.yz, this.xz, 0);    
        //return geom;
    }
    
    public Geometry drawOne(AssetManager assetManager, String name){
        mesh = new Cylinder(6, 6, 0.04f, 2.8f, false, false);
        geom = new Geometry(name, mesh);
        TangentBinormalGenerator.generate(mesh); 
        material = new Material(assetManager,"/Common/MatDefs/Light/Lighting.j3md");
        material.setTexture("DiffuseMap",assetManager.loadTexture("Textures/white.jpg"));
        //material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        material.setBoolean("UseMaterialColors",true);
        material.setColor("Diffuse",ColorRGBA.Yellow);
        material.setColor("Specular",ColorRGBA.Yellow);
        material.setFloat("Shininess", 128f);  // [0,128]
        geom.setMaterial(material);
        geom.setLocalTranslation(this.x, this.y, this.z);
        geom.rotate(this.yz, this.xz, 0);    
        return geom;
    }
    
    public Geometry hideOne(){
        geom.setLocalTranslation(100,100,100);
        return geom;
    }
    
    public Geometry showOne(){
        geom.setLocalTranslation(this.x, this.y, this.z);
        return geom;
    }
    
    ColorRGBA getColor() {
    return this.color;
    }
    
    public Geometry setColor(ColorRGBA color){
        this.color = color;
        material.setColor("Diffuse",color);
        material.setColor("Specular",color);
        material.setFloat("Shininess", 128f);  // [0,128]
        geom.setMaterial(material);
        return geom;
    }
}
