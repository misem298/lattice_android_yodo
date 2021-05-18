/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamelattice;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.util.TangentBinormalGenerator;
import java.nio.ByteBuffer;
/**
 *
 * @author Administrator
 */
public class ControlElement {
    final private AssetManager am;
    protected BallTextures bt;
    protected float x;
    protected float y;
    protected float z;
    protected String name;
    protected int value;
    public Sphere mesh;
    public Geometry geom;
    public Material material;
    public ColorRGBA color;
    public ColorRGBA defaultcolor;
    public boolean changed;
    public boolean selected;
    protected Vector3f v;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ControlElement(float x, float y, float z, String geoName, String fileName, AssetManager assetManager, int value) {
        this.am  = assetManager;
        this.x = x;
        this.y = y;
        this.z = z;
        this.value = value;
        v = new Vector3f();
        this.v.x = x;
        this.v.y = y;
        this.v.z = z;
        this.color = ColorRGBA.White;
        this.name = "contr" + geoName;
        this.defaultcolor = ColorRGBA.White;
        this.changed = false;
        this.selected = false;
        bt = new BallTextures(fileName, ColorRGBA.DarkGray, ColorRGBA.White);
        createMaterial(bt.texture);
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
    
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Texture createTexture(String text, ColorRGBA textColor, ColorRGBA bgColor){
        Bitmap b = Bitmap.createBitmap(400, 200, Bitmap.Config.ARGB_8888);
        Canvas cnv = new Canvas(b);
        Paint p = new Paint();
        Image img = new Image();
        img.setHeight(200);
        img.setWidth(400);
        img.setDepth(1);
        img.setFormat(Image.Format.RGBA8);
        int imageSize = b.getRowBytes() * b.getHeight();
        ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(imageSize);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(imageSize);

        int color = (255 & 0xff) << 24 | ((int)(255 *  textColor.r) & 0xff) << 16 | ((int)(255 * textColor.g) & 0xff) << 8 | ((int)(255 *  textColor.b) & 0xff);
        int bgcolor = (255 & 0xff) << 24 | ((int)(255 *  bgColor.r) & 0xff) << 16 | ((int)(255 * bgColor.g) & 0xff) << 8 | ((int)(255 *  bgColor.b) & 0xff);
        cnv.drawBitmap(b, 0, 0, p);
        cnv.drawColor(bgcolor);

           if (text.substring(0,1).equals("%")) {
            p.setColor(color);
            p.setStyle(Paint.Style.FILL);
            if (text.substring(0,3).equals("%ce")) {
                RectF oval = new RectF(220, 130, 180, 90);
                cnv.drawOval(oval, p);
            }
            else {
                text = text.substring(1);
                Path path = new Path();
                path.setFillType(Path.FillType.EVEN_ODD);
                path.moveTo(getArrow(text)[0].x, getArrow(text)[0].y);
                path.lineTo(getArrow(text)[0].x, getArrow(text)[0].y);
                path.lineTo(getArrow(text)[1].x, getArrow(text)[1].y);
                path.lineTo(getArrow(text)[2].x, getArrow(text)[2].y);
                if (text.equals("closer") | text.equals("farther")) {
                    path.moveTo(180, 70);
                    path.lineTo(230, 70);
                    path.lineTo(230, 50);
                    path.lineTo(180, 50);
                    path.lineTo(180, 70);
                }
                path.close();
                cnv.drawPath(path, p);
            }
            b.copyPixelsToBuffer(byteBuffer1);
            img.addData(byteBuffer1);
        }
            else { // below text on canvas will be written
            p.setColor(color);
            Typeface tf = Typeface.create("comic_san_serif", Typeface.BOLD);
            p.setTypeface(tf);
            p.setTextSize(60);
            //p.setTypeface(Typeface.DEFAULT_BOLD);
            cnv.drawText(text, 130,100, p);
            b.copyPixelsToBuffer(byteBuffer1);
            for (int j = 0; j < byteBuffer1.array().length / 1600 - 1; j++ ) {
                for (int i = 0; i < 1600; i++) {
                    byteBuffer.put(byteBuffer1.get((318400 - j * 1600) + i));
                }
            }
            img.addData(byteBuffer);
        }
        Texture texture = new Texture2D(img);
        texture.setImage(img);
        return texture;
    }

    private Point[] getArrow(String arrow) {
        Point[] points = new Point[3];
        points[0] = new Point();
        points[1] = new Point();
        points[2] = new Point();
        switch (arrow) {
            case "left":
                points[0].x =  230;
                points[1].x =  180;
                points[2].x =  230;
                points[0].y =  80;
                points[1].y =  105;
                points[2].y =  130;
        //floats = new float[]{200, 280, 280, 280};
        //floats1 = new float[]{130, 80, 180, 180};
        break;
         case "right":
             points[0].x =  180;
             points[1].x =  230;
             points[2].x =  180;
             points[0].y =  80;
             points[1].y =  105;
             points[2].y =  130;
            //floats = new float[]{200, 200, 280, 280};
            //floats1 = new float[]{80, 180, 130, 130};
        break;
         case "down" : case "closer" :
             points[0].x =  230;
             points[1].x =  205;
             points[2].x =  180;
             points[0].y =  130;
             points[1].y =  80;
             points[2].y =  130;
            //floats = new float[]{200, 240, 280, 280};
            //floats1 = new float[]{80, 180, 80, 80};
        break;
            case "up": case  "farther":
            points[0].x =  230;
            points[1].x =  205;
            points[2].x =  180;
            points[0].y =  80;
            points[1].y =  130;
            points[2].y =  80;
            //floats = new float[]{200, 240, 280, 280};
            //floats1 = new float[]{180, 80, 180, 180};
        break;
        case "centr" :

            //floats = new float[]{220, 220, 260, 260};
            //floats1 = new float[]{160, 120, 120, 160};
            break;
        }
        //ArrayList<float[]> al = new ArrayList<>();
        //al.add(floats);
        //al.add(floats1);
        return points;
    }
    private void createMaterial(Texture texture) {
        material = new Material(am,"/Common/MatDefs/Light/Lighting.j3md");
        material.setTexture("DiffuseMap", texture); 
        material.setBoolean("UseMaterialColors",true);
        material.setColor("Diffuse",ColorRGBA.White);
        //material.setColor("Specular",ColorRGBA.White);
        material.setFloat("Shininess", 128f);  // [0,128]       
    }
    
     public void setSpecular(){
        //this.color = this.defaultcolor;
        //material.setColor("Diffuse",this.defaultcolor);
        material.setColor("Specular",ColorRGBA.Cyan);
        material.setFloat("Shininess", 12f);  // [0,128]
        geom.setMaterial(material);
    }
    public void resetSpecular(){
        material.clearParam("Specular");
        material.clearParam("Shininess");
    }
}


