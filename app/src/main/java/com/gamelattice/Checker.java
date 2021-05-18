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
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.BufferUtils;
//import com.jme3.util.TangentBinormalGenerator;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Administrator
 */
public class Checker {
    protected AssetManager assetManager;
    protected int x, y, z, startZ;
    protected float fx, fy, fz, sx, sy, sz, smallRadius;
    protected Vector3f v;
    protected Geometry geo;
    protected Sphere mesh;
    protected Geometry geom;
    protected Material material;
    protected ColorRGBA color, defaultcolor, transcolor;
    protected String who, name;
    protected boolean checked, mayBeEated, underAttack;
    protected boolean spark, sliping, damka, mamka, smash;
    protected String Xaxis, Yaxis, Zaxis;
    private float[] oldVertexArray = new float[270];
    final float defaultradius;
    protected ArrayList<Vector3f> moveList = new ArrayList<>();
    protected ArrayList<Vector3f> eatList = new ArrayList<>();
    protected ArrayList<ArrayList<Vector3f>> eatChains = new ArrayList<>();
    
    public  Checker(int x, int y, int z, ColorRGBA color) {
        // this.material = material;
        this.defaultradius = 0.6f;
        this.smallRadius = 0.1f;
        this.x = x;
        this.y = y;
        this.z = z;
        this.startZ = z;
        this.fx = x;
        this.fy = y;
        this.fz = z;
        this.sx = x;
        this.sy = y;
        this.sz = z;
        this.v = new Vector3f();
        this.v.set(x, y, z);     
        this.Xaxis = Character.toString((char)(x + 65- z));
        this.Yaxis = Character.toString((char)(y + 97 - z));
        this.Xaxis = Character.toString((char)(z + 49 - z));
        this.color = new ColorRGBA(color);
        this.defaultcolor = new ColorRGBA(color);
        this.transcolor = new ColorRGBA(color);
        this.transcolor.a = 0;
        this.checked = false;
        this.spark = false;
        this.smash = false;
        this.sliping = false;
        this.mayBeEated = false;
        this.underAttack = false;
        this.damka = false;
        this.mamka = false;
    }
    
    protected  Geometry drawObj(AssetManager assetManager,  String name  ) {
        this.assetManager = assetManager;
       // this.soundEffects = soundEffects;
        this.name = name;
        this.mesh =  new Sphere(10,10, defaultradius);
        //TangentBinormalGenerator.generate(this.mesh);
        this.geom = new Geometry(name, this.mesh);
        mesh.setTextureMode(Sphere.TextureMode.Projected);
        this.material = new Material(assetManager,"/Common/MatDefs/Light/Lighting.j3md");
        this.material.setTexture("DiffuseMap",assetManager.loadTexture("Textures/white.jpg"));
        //this.material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        this.geom.setMaterial(this.material);
        this.material.setBoolean("UseMaterialColors",true);
        //this.material.setColor("Ambient",this.defaultcolor);
        this.material.setColor("Diffuse",this.defaultcolor);
        this.material.setColor("Specular",this.defaultcolor);
        this.material.setFloat("Shininess", 1f);  // [0,128]
        this.geom.setLocalTranslation(this.x, this.y, this.z);
        return this.geom; 
    }
    
    protected void setMove(Vector3f vect, boolean damkaAllowed) {
        this.x = Math.round(vect.x);
        this.y = Math.round(vect.y);
        this.z = Math.round(vect.z);
        this.fx = this.x;
        this.fy = this.y;
        this.fz = this.z;
        this.v.set(this.x,this.y,this.z);
        //geom.setLocalTranslation(this.x, this.y, this.z);
        //if (damkaAllowed) if (!mamka & this.z == - startZ) transformToDamka();
    }
    
     protected void moveToStart(Vector3f vect) {
        this.x = Math.round(vect.x);
        this.y = Math.round(vect.y);
        this.z = Math.round(vect.z);
        this.fx = this.x;
        this.fy = this.y;
        this.fz = this.z;
        this.sx = this.x;
        this.sy = this.y;
        this.sz = this.z;
        this.v.set(this.x,this.y,this.z);
        //if (mamka) mesh.updateGeometry(10, 10, defaultradius);//transformBackM();
        geom.setLocalTranslation(this.x, this.y, this.z);
    }
    
    protected void remove(int xx, int yy, int zz) {
        this.fx =xx;
        this.x = xx;
        this.fy = yy;
        this.y = yy;
        this.fz =zz;
        this.z = zz;
        this.v.set(x, y, z);
        geom.setLocalTranslation(x, y, z);
        mesh.updateGeometry(10, 10, defaultradius);
        this.mamka = false;
        resetColor();
    }
    
    protected void slip(boolean soundOn, SoundAndroid soundEffects, float soundVolume) {
        if (sx == fx & sy == fy & sz == fz) {
            this.x = (int)fx;
            this.y = (int)fy;
            this.z = (int)fz;
            this.sliping = false;
            resetColor();
            if (!mamka & this.z == - startZ) {
                transformToDamka(1);
                if (soundOn & startZ < 0) soundEffects.soundPool.play(soundEffects.fanf_audio,soundVolume,soundVolume,0,0,1);
            }
            GameStart.breakTime = System.currentTimeMillis();
            return;
        }
        this.sliping = true;
        if (sx != fx) this.sx = this.sx + 0.1f * Math.round(Math.abs(fx - sx)/(fx - sx));
        if (sy != fy) this.sy = this.sy + 0.1f * Math.round(Math.abs(fy - sy)/(fy - sy));
        this.sz = this.sz + 0.1f * Math.round((Math.abs(fz - sz)/(fz - sz)));//n/p;
        //System.out.println(" " + sx + " " + sy + " " + sz);
        geom.setLocalTranslation(sx, sy, sz );   
        sx = Math.round(sx*100)/100f;
        sy = Math.round(sy*100)/100f;
        sz = Math.round(sz*100)/100f;
        
    }
    
    
    
    protected void shiverChecker() {
        geom.setLocalTranslation(sx + (0.05f * FastMath.nextRandomFloat() - 0.1f), 
                sy + (0.05f * FastMath.nextRandomFloat() - 0.1f), 
                sz + (0.05f * FastMath.nextRandomFloat() - 0.1f));
    }
    
    protected void swell(float tpf){
        //color.a =  color.a - tpf/7;
        //System.out.println(" swell" + transcolor.a);
        //this.material.setColor("Diffuse",this.color);
        mesh.radius = mesh.radius * 1.01f;
        if (mesh.radius >1) smash = true;
        transformRandomly();
    }
    
    protected void birth(float tpf){
        if(transcolor.a > 1) transcolor.a = 1;
        else transcolor.a = transcolor.a + 2f * tpf;
        if(smallRadius < defaultradius) smallRadius = smallRadius + 0.005f;//tpf / 2.2f;
        //System.out.println("smallRad " + smallRadius);//mesh.radius = r;
        mesh.updateGeometry(10, 10,  smallRadius);
        this.material.setColor("Diffuse",this.transcolor);
        this.material.setColor("Specular",this.transcolor);
        geom.setMaterial(material);
        
    } 
    
    protected  void setBeEated() {
        mesh.radius = mesh.radius + 0.1f;
        mesh.updateGeometry(10, 10, mesh.radius);
        setRandomBright();
    }
    
    protected  void resetBeEated() {
        mesh.radius = 0.5f;
        this.material.setColor("Diffuse",this.defaultcolor);
        this.material.setColor("Specular",this.defaultcolor);
        material.setFloat("Shininess", 128);
        mesh.updateGeometry(10, 10, 0.5f);
        resetColor();
    }
    
     protected  void resetChecker() {
        this.color = new ColorRGBA(this.defaultcolor);
        this.color.a = 1;
        this.smallRadius = 0.3f;
        this.material.setColor("Diffuse",this.defaultcolor);
        this.material.setColor("Specular",this.defaultcolor);
        this.material.setFloat("Shininess", 128);
        if (this.mamka) transformToDamka(1);
        else this.mesh.updateGeometry(10, 10, 0.5f);
        //resetColor();
    }
     
    /*protected  void randomRadiusShine(){
        mesh.radius = 0.4f + 0.2f * FastMath.nextRandomFloat();
        material.setFloat("Shininess", 128 * FastMath.nextRandomFloat());
        mesh.updateGeometry(10,10,mesh.radius);
    }*/
    
    /*protected  void changeRadius(float r){
        mesh.radius = r;
        mesh.updateGeometry(10,10,mesh.radius);
    }*/
    protected  void pulseRadius(float r) {
        if (mesh.radius == 0.5f ) {
            mesh.updateGeometry(10, 10, r);
            if (mamka) transformToDamka(1.5f);
        }
        else {
            mesh.updateGeometry(10, 10, 0.5f);
             if (mamka) transformToDamka(1f);
        }
    }
    
    protected void changeChekerColor(){
         if(color.equals(defaultcolor)) setColor(ColorRGBA.randomColor());
         else resetColor(); 
    }
    
    protected void transformToDamka(float ray){
        VertexBuffer buffer = mesh.getBuffer(VertexBuffer.Type.Position);
        //float[] vertexArray = new float[buffer.getData().capacity()];
        float[] vertexArray = BufferUtils.getFloatArray((FloatBuffer) buffer.getData());
        //System.out.println("transformToDamka vertexArray" + vertexArray.length);
        oldVertexArray = Arrays.copyOf(vertexArray, 0);
        vertexArray[266] = - ray;//-1f ;
        vertexArray[269] = ray;//1f;
        
        vertexArray[129] = ray;//1f;
        vertexArray[131] = 0;
        vertexArray[132] = ray;//1f;
        vertexArray[134] = 0;
        
        vertexArray[99]  = ray; //1f;
        vertexArray[101] = 0;
        vertexArray[114] = -ray;//-1f;
        vertexArray[116] = 0;
        vertexArray[147] = -ray;//-1f;
        vertexArray[149] = 0;
        vertexArray[162] = ray;//1f;
        vertexArray[164] = 0;
        
        vertexArray[153] = 0; 
        vertexArray[154] = -ray;//-1f;
        vertexArray[157] = -ray;//-1f;
        vertexArray[156] = 0;   
        vertexArray[105] = 0;
        vertexArray[106] = ray;//1f;
        vertexArray[109] = ray;//1f;
        vertexArray[108] = 0;
        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertexArray));
        mamka = true;
        
    }
    
   protected void transformRandomly(){
        VertexBuffer buffer = mesh.getBuffer(VertexBuffer.Type.Position);
        float[] vertexArray = BufferUtils.getFloatArray((FloatBuffer) buffer.getData());
        //System.out.println("transformToDamka vertexArray" + vertexArray.length);
        //oldVertexArray = Arrays.copyOf(vertexArray, 0);
        for (int i = 1; i < vertexArray.length; i++) 
            vertexArray[i] = vertexArray[i] + 0.15f * (FastMath.nextRandomFloat() - 0.5f);
        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertexArray));
        //FloatBuffer data =FloatBuffer.allocate(vertexArray.length);
        //data.put(vertexArray);
        //  buffer.setUsage(VertexBuffer.Usage.Dynamic);
        //buffer.updateData(data);
        //mamka = true;
        //Main.soundEffects.fanf_audio.playInstance();
    } 
   
    protected void transformBack(){
        System.out.println("transformBack" + Arrays.toString(oldVertexArray));
        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(oldVertexArray));
        damka = false;
    }
    
    protected void transformBackM(){
        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(oldVertexArray));
        mesh.updateBound();
        mesh.updateGeometry(10, 10, mesh.radius);
        mamka = false;
    }
    
    protected Checker getCheckerByGeometry(Geometry g) {
        if (geom.equals(g)) return this;
        return null;
    }
    
     protected Checker findCheckerByXYZ(int x, int y, int z, ArrayList<Checker> checkers) {
        for (Checker c : checkers) {if (c.x == x && c.y == y && c.z == z) return c;}
        return null;
    }
    
    protected  void setRandomBright() {  
        material.setFloat("Shininess", FastMath.nextRandomFloat() * 128);
    }
    
    protected  void setBright(int r) {  
        material.setFloat("Shininess", r);
    }
    
    protected  void resetBright() {  
        material.setFloat("Shininess", 128);
    }
    
    protected  void setColor(ColorRGBA color){
        this.color = color;
        material.setColor("Diffuse",color);
        material.setColor("Specular",color);
        material.setFloat("Shininess", 128f);  // [0,128]
        geom.setMaterial(material);
    }
    
    protected  void resetColor(){
        color = this.defaultcolor;
        color.a = 1;
        material.setColor("Diffuse",this.defaultcolor);
        material.setColor("Specular",this.defaultcolor);
        material.setFloat("Shininess", 128f);  // [0,128]
        geom.setMaterial(material);
    }
    
    protected void changeColor(ColorRGBA color){
        this.defaultcolor = color;
        this.color = color;
        resetColor();
    }
    
    /*protected ColorRGBA getColor() {
        return this.color;
    }
    
    protected void convertXYZtoAa1(Vector3f v, int dim){
        this.Xaxis = Character.toString((char)((int)v.x + 65 - 2 * dim + 1));
        this.Yaxis = Character.toString((char)((int)v.y + 97 - 2 * dim + 1));
        this.Xaxis = Character.toString((char)((int)v.z + 49 - 2 * dim + 1));
    }

    protected void stayBack() {
        geom.setLocalTranslation(x, y ,z);
    }

    protected void setSpecular(float shine){
        material.setColor("Specular",defaultcolor);
        material.setFloat("Shininess", shine);  // [0,128]
        geom.setMaterial(material);
    }

    protected void resetSpecular(){
        material.clearParam("Specular");
        material.clearParam("Shininess");
    }
    protected Vector3f checkerExist(Checker[][][] checker, int dimension, float x, float y, float z) {
        Vector3f vect = new Vector3f();
        vect.x = 0;
        vect.y = 0;
        vect.z = 100;
        for(int i= 0; i < 2 * dimension; i++){
            for(int j = 0; j < 2 * dimension; j++){
                //System.out.println(this.dimension + " xxx " + j + i +  "  x" + x + "  y" + y + "   z" +z);
                if((i + j) % 2 == 0) {
                    if (checker[j][i][0].x == x && checker[j][i][0].y == y && checker[j][i][0].z == z) {
                        vect.x = j;
                        vect.y = i;
                        vect.z = 0;
                        return vect;
                    }
                } 
                if ((i + j) % 2 != 0) {
                    if (checker[j][i][1].x == x && checker[j][i][1].y == y && checker[j][i][1].z == z) {
                        vect.x = j;
                        vect.y = i;
                        vect.z = 1;
                        return vect;
                    }   
                }   
            }
        }
        return vect;
    }*/
    /*public void move(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.fx = x;
        this.fy = y;
        this.fz = z;
        this.v.set(this.x,this.y,this.z);
        geom.setLocalTranslation(this.x, this.y, this.z);
    }*/
    /*public Spatial drawObj(AssetManager  assetManager, String file, String name){
        sas = assetManager.loadModel(file);
            sas.scale(0.3f);
        TangentBinormalGenerator.generate(sas); 
        mesh =  new Sphere(6,6, 0.1f);
        TangentBinormalGenerator.generate(mesh);
        material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        material.setTexture("DiffuseMap",assetManager.loadTexture("Textures/blue1.jpg")); 
        //Material sasMat = assetManager.loadMaterial("Common/Materials/RedColor.j3m");
        material.setBoolean("UseMaterialColors",true);
        material.setColor("Diffuse",this.defaultcolor);
        material.setColor("Specular",this.defaultcolor);
        material.setFloat("Shininess", 64f);  // [0,128]
        sas.setMaterial(material);
        sas.setName(name);
        sas.setLocalTranslation(this.x, this.y, this.z);
        //rootNode.attachChild(sas);this.defaultcolor
        return sas;
    }*/
    
}
