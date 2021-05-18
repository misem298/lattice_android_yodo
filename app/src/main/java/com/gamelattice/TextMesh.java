/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamelattice;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author Administrator
 */
public class TextMesh {
    protected Vector3f v;
    protected Geometry specularJoint;
    protected Sphere mesh;
    protected Geometry geom;
    //protected Material material;
    protected ColorRGBA color;
    //protected ColorRGBA defaultcolor;
    protected Node textNode ;
    protected Node meshNode ;
    protected BitmapText gameText;
    final protected Quaternion q ;
    //protected String who;
    protected int dimensionX;
    protected int dimensionY;
    protected int dimensionZ;
    protected boolean hintOn = false;
    
    public TextMesh(int x, int y, int z, AssetManager assetManager, String name) {
        this.dimensionX = x;
        this.dimensionY = y;
        this.dimensionZ = z; 
        this.v = new Vector3f();
        this.v.set(x, y, z);
        this.q = new Quaternion();
        this.q.set(0,0,0,0);
        this.textNode = new Node();
        this.meshNode = new Node();   
        BitmapFont gameFont = assetManager.loadFont("Interface/Fonts/ComicSansMS.fnt");
        gameText = new BitmapText(gameFont, false);
    }
    
    protected Node getTextNode(){
    return textNode;
    }
    protected Node getMeshNode(){
    return meshNode;
    }
    
   /* protected void showBall(AssetManager assetManager, String name){
        mesh =  new Sphere(10, 10, 0.5f);
        TangentBinormalGenerator.generate(mesh);
        geom = new Geometry(name, mesh);
        material = new Material(assetManager,"/Common/MatDefs/Light/Lighting.j3md");
        material.setTexture("DiffuseMap",assetManager.loadTexture("Textures/argo.gif"));
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geom.setMaterial(material);
        material.setBoolean("UseMaterialColors",true);
        material.setColor("Diffuse", ColorRGBA.Pink);
        material.setColor("Specular",ColorRGBA.Pink);
        material.setFloat("Shininess", 64f);  // [0,128]
        //geom.setLocalTranslation(x + 4, y-3, z-4);
        meshNode.attachChild(geom);
    }*/

    protected void showJointHint(Geometry geoJoint, Joint pickedJoint ) {
        GameStart.hintTime = System.currentTimeMillis();
        if (specularJoint != null) specularJoint.getMaterial().clearParam("Shininess");
        //jointNames.add(geoJoint);
        geoJoint.getMaterial().setFloat("Shininess", 2f);
        specularJoint = geoJoint;
        showHint(pickedJoint);
        //if (jointNames.size() > 10) jointNames.remove(0);
        hintOn = true;
    }

    protected void showHint(Joint joint){
        gameText.setText(convertToAa1(joint));
        gameText.setName("tip");
        gameText.setSize(0.4f);
        gameText.setColor(ColorRGBA.White);
        //System.out.println("showHint " + node.getLocalRotation());
        textNode.setLocalTranslation(joint.v);
        gameText.setLocalTranslation(gameText.getLineWidth()/2 + 0.1f,
                gameText.getLineWidth()/2 + 0.0f, gameText.getLineHeight()/2  );
        gameText.setLocalRotation(q);
        textNode.attachChild(gameText);
    }
    
    protected void hideHint(){
        textNode.detachChildNamed("tip");
    }

    protected void hideJointHint() {
        textNode.detachChildNamed("tip");
        specularJoint.getMaterial().clearParam("Shininess");
        hintOn = false;
        //for (Geometry g: jointNames) g.getMaterial().setFloat("Shininess", 128f);
    }

    protected void addRotate(){
            gameText.setLocalRotation(q);
            gameText.setLocalRotation(q);
            gameText.setLocalRotation(q);
        
    }
    protected String convertToAa1(Joint joint){
        return Character.toString((char)((joint.x + 2 * dimensionX - 1) / 2 + 65)) +
               Character.toString((char)((joint.y + 2 * dimensionY - 1) / 2 + 97)) + 
               Character.toString((char)((joint.z + 2 * dimensionZ - 1) / 2 + 49));
    }
}
