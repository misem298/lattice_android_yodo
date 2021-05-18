/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamelattice;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.TangentBinormalGenerator;

/**
 *
 * @author Administrator
 */
public class Joint {
       
    protected int x;
    protected int y;
    protected int z;
    protected float xz;
    protected float yz;
    public Geometry geo;
    public Sphere mesh;
    public Geometry geom;
    public Material material;
    public ColorRGBA color;
    public ColorRGBA defaultcolor;
    public boolean checked;
    protected Vector3f v;
    
    public Joint(int x, int y, int z, ColorRGBA color, AssetManager assetManager, String name) {
        // this.material = material;
        this.x = x;
        this.y = y;
        this.z = z;
        v = new Vector3f(x, y, z);
        //this.v.x = x;
        //this.v.y = y;
        //this.v.z = z;
        this.color = color;
        this.defaultcolor = color;
        this.checked = false;
        mesh =  new Sphere(6, 6, 0.3f);
        //TangentBinormalGenerator.generate(mesh);
        geom = new Geometry(name, mesh);
        material = new Material(assetManager,"/Common/MatDefs/Light/Lighting.j3md");
        material.setTexture("DiffuseMap",assetManager.loadTexture("Textures/color.jpg"));
        //material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geom.setMaterial(material);
        material.setBoolean("UseMaterialColors",true);
        material.setColor("Diffuse",this.defaultcolor);
        material.setColor("Specular",new ColorRGBA(0,1,0,1));//this.defaultcolor);
        material.setFloat("Shininess", 1f);  // [0,128]
        geom.setLocalTranslation(this.x, this.y, this.z);
    }
   
    
    protected ColorRGBA getColor() {
        return this.color;
    }
    
 
    public Geometry drawOne(AssetManager assetManager, String name  ) { 
        mesh =  new Sphere(6,6, 0.2f);
        TangentBinormalGenerator.generate(mesh);
        geom = new Geometry(name, mesh);
        material = new Material(assetManager,"/Common/MatDefs/Light/Lighting.j3md");
        material.setTexture("DiffuseMap",assetManager.loadTexture("Textures/white.jpg"));
        //material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geom.setMaterial(material);
        material.setBoolean("UseMaterialColors",true);
        material.setColor("Diffuse",this.defaultcolor);
        material.setColor("Specular",this.defaultcolor);
        material.setFloat("Shininess", 64f);  // [0,128]
        geom.setLocalTranslation(this.x, this.y, this.z);
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
     public Geometry setColor(ColorRGBA color){
        material.setColor("Diffuse",color);
        material.setColor("Specular",color);
        material.setFloat("Shininess", 128f);  // [0,128]
        geom.setMaterial(material);
        return geom;
    }
    
     public void resetColor(){
        material.setColor("Diffuse",this.defaultcolor);
        material.setColor("Specular",this.defaultcolor);
        material.setFloat("Shininess", 128f);  // [0,128]
        geom.setMaterial(material);
    }
    
     protected void shiverJoint() {
        
        geom.setLocalTranslation(x + (0.05f * FastMath.nextRandomFloat() - 0.1f), 
                y + (0.05f * FastMath.nextRandomFloat() - 0.1f), 
                z + (0.05f * FastMath.nextRandomFloat() - 0.1f));
        material.setColor("Diffuse", new ColorRGBA(GameStart.hod));
        material.setColor("Specular", new ColorRGBA(GameStart.hod));
        material.setFloat("Shininess", 1f);  // [0,128]
        geom.setMaterial(material);
        mesh.updateGeometry(6, 6, 0.25f);
    }
     /*protected void resetJoint() {
        resetColor();
        geom.setLocalTranslation(x,y,z);
        mesh.updateGeometry(6, 6, 0.2f); 
     }*/
            
    public boolean freeJoint(Vector3f v, Checker checker){
        return !checker.v.equals(v) ;
    }
    
     public void setSpecular(float shine){
        material.setColor("Specular",defaultcolor);
        material.setFloat("Shininess", shine);  // [0,128]
        geom.setMaterial(material);
    }
    
    public void resetSpecular(){
        material.clearParam("Specular");
        material.clearParam("Shininess");
    }
    
    /*public ArrayList<Vector3f> FreeJointUpOrDown(Checker checker, int up){
        ArrayList<Vector3f> vl = new ArrayList<>(); 
        Vector3f vect = new Vector3f();
        if (freeJoint(vect.set(checker.x + 2, checker.y, checker.z + 2 * up), checker)) vl.add(vect);
        if (freeJoint(vect.set(checker.x - 2, checker.y, checker.z + 2 * up), checker)) vl.add(vect);
        if (freeJoint(vect.set(checker.x, checker.y + 2, checker.z + 2 * up), checker)) vl.add(vect);
        if (freeJoint(vect.set(checker.x, checker.y - 2, checker.z + 2 * up), checker)) vl.add(vect);
        return vl;
    }*/
      
}
