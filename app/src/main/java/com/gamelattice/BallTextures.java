package com.gamelattice;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;

import java.nio.ByteBuffer;

public class BallTextures {
    protected Texture texture = new Texture2D();


    public BallTextures(String text, ColorRGBA textColor, ColorRGBA bgColor){
            int w = 500;
            int h = 200;
            Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas cnv = new Canvas(b);
            Paint p = new Paint();
            Image img = new Image();
            img.setHeight(h);
            img.setWidth(w);
            img.setDepth(1);
            img.setFormat(Image.Format.RGBA8);
            int imageSize = b.getRowBytes() * b.getHeight();
            ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(imageSize);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(imageSize);
            if(textColor.equals(ColorRGBA.White)) textColor = ColorRGBA.Black;
            if(textColor.equals(ColorRGBA.Yellow)) textColor = new ColorRGBA(0.8f,0.7f,0.01f,1);
            int color = (255 & 0xff) << 24 | ((int)(255 *  textColor.r) & 0xff) << 16 | ((int)(255 * textColor.g) & 0xff) << 8 | ((int)(255 *  textColor.b) & 0xff);
            int bgcolor = (255 & 0xff) << 24 | ((int)(255 *  bgColor.r) & 0xff) << 16 | ((int)(255 * bgColor.g) & 0xff) << 8 | ((int)(255 *  bgColor.b) & 0xff);
            cnv.drawBitmap(b, 0, 0, p);
            cnv.drawColor(bgcolor);

            if (text.substring(0,1).equals("%")) {
                p.setColor(color);
                p.setStyle(Paint.Style.FILL);
                if (text.substring(0,3).equals("%ce")) {
                    RectF oval = new RectF(w/2+20, h/2+35, w/2-25, h/2-10);
                    cnv.drawRect(oval, p);
                }
                else {
                    text = text.substring(1);
                    Path path = new Path();
                    path.setFillType(Path.FillType.EVEN_ODD);
                    path.moveTo(getArrow(text,w,h)[0].x, getArrow(text,w,h)[0].y);
                    path.lineTo(getArrow(text,w,h)[0].x, getArrow(text,w,h)[0].y);
                    path.lineTo(getArrow(text,w,h)[1].x, getArrow(text,w,h)[1].y);
                    path.lineTo(getArrow(text,w,h)[2].x, getArrow(text,w,h)[2].y);
                    if (text.equals("closer") | text.equals("farther")) {
                        path.moveTo(w/2-25, h/2 - 30);
                        path.lineTo(w/2+25, h/2 - 30);
                        path.lineTo(w/2+25, h/2 - 50);
                        path.lineTo(w/2-25, h/2 - 50);
                        path.lineTo(w/2-25, h/2 - 30);
                    }
                    path.close();
                    cnv.drawPath(path, p);
                }
                b.copyPixelsToBuffer(byteBuffer1);
                img.addData(byteBuffer1);
            }
            else { // below text on canvas will be written
                p.setColor(color);
                Context ctx = GlobalApplication.getAppContext();
                Typeface tf = Typeface.createFromAsset(ctx.getAssets(), "Interface/Fonts/ComicSansMS3.ttf");
                //Typeface.Builder builder = new Typeface.Builder("Interface/Fonts/ComicSansMS3.ttf");
                //Typeface tf = builder.build();
                p.setTypeface(tf);
                p.setTextSize(50);
                p.setTextAlign(Paint.Align.CENTER);
                p.setTypeface(Typeface.DEFAULT_BOLD);
                if (text.contains("%")) {
                    //p.setTextSize(50);
                    p.setTextAlign(Paint.Align.CENTER);
                    String text1 = text.substring(0, text.indexOf("%"));
                    String text2 = text.substring(text.indexOf("%") + 1);
                    if (text2.contains("%")) {
                        String text21 = text2.substring(0, text2.indexOf("%"));
                        String text22 = text2.substring(text2.indexOf("%") + 1);
                        cnv.drawText(text1, w/2+10,h/2 - p.getTextSize() * 0.8f, p);
                        cnv.drawText(text21, w/2+10,h/2, p);
                        cnv.drawText(text22, w/2+10,h/2 + p.getTextSize() * 0.8f , p);
                    }
                    else {
                        if(text2.contains("vol")) {
                            cnv.drawText(text1, w / 2 - 20, h / 2 , p);
                            p.setTextSize(70);
                            cnv.drawText("><", w / 2 - 20, h / 2 + p.getTextSize() * 0.6f, p);
                        }
                        else {
                            cnv.drawText(text1, w / 2 - 20, h / 2 - p.getTextSize() / 2, p);
                            cnv.drawText(text2, w / 2 - 20, h / 2 + p.getTextSize() / 2, p);
                        }
                    }
                }
                else cnv.drawText(text, w/2 - 20,h/2, p);
                b.copyPixelsToBuffer(byteBuffer1);
                for (int j = 0; j < byteBuffer1.array().length / (w * 4) - 1; j++ ) {
                    for (int i = 0; i < w * 4; i++) {
                        byteBuffer.put(byteBuffer1.get((w * 4 * (h - 1) - j * w * 4) + i));
                    }
                }
                img.addData(byteBuffer);
            }
            texture = new Texture2D(img);
            texture.setImage(img);
            //return texture;
        }

        private Point[] getArrow(String arrow, int w, int h) {
            Point[] points = new Point[3];
            points[0] = new Point();
            points[1] = new Point();
            points[2] = new Point();
            switch (arrow) {
                case "left":
                    points[0].x =  w/2+25;
                    points[1].x =  w/2-25;
                    points[2].x =  w/2+25;
                    points[0].y =  h/2-25;
                    points[1].y =  h/2;
                    points[2].y =  h/2+25;
                    //floats = new float[]{200, 280, 280, 280};
                    //floats1 = new float[]{130, 80, 180, 180};
                    break;
                case "right":
                    points[0].x =  w/2-25;
                    points[1].x =  w/2+25;
                    points[2].x =  w/2-25;
                    points[0].y =  h/2-25;
                    points[1].y =  h/2;
                    points[2].y =  h/2+25;
                    //floats = new float[]{200, 200, 280, 280};
                    //floats1 = new float[]{80, 180, 130, 130};
                    break;
                case "down" : case "closer" :
                    points[0].x =  w/2+25;
                    points[1].x =  w/2;
                    points[2].x =  w/2-25;
                    points[0].y =  h/2+30;
                    points[1].y =  h/2-20;
                    points[2].y =  h/2+30;
                    //floats = new float[]{200, 240, 280, 280};
                    //floats1 = new float[]{80, 180, 80, 80};
                    break;
                case "up": case  "farther":
                    points[0].x =  w/2+25;
                    points[1].x =  w/2;
                    points[2].x =  w/2-25;
                    points[0].y =  h/2-20;
                    points[1].y =  h/2+30;
                    points[2].y =  h/2-20;
                    break;
                case "centr" :
                    break;
            }
            return points;
        }

}
