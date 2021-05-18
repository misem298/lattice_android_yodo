/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamelattice;

import android.content.Context;
import android.content.Intent;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static com.gamelattice.GameStart.demoMode;
import static com.gamelattice.GameStart.localMode;
import static com.gamelattice.GameStart.netMode;
import static com.gamelattice.GameStart.menuSelectedColor;

/**
 *
 * @author Administrator
 */
public class MenuControl {
    protected MenuElement menu, settings, save, load, demo, netgame, aigame, locgame;
    protected MenuElement dims, colors, level, moveF, hist, sound, back;
    protected MenuElement dim222, dim322, dim223, dim323, dim333;
    protected MenuElement lev16, lev2, lev32, moveMe, moveAi;
    protected MenuElement redAi, redMy, oraAi, oraMy, yelAi, yelMy, greAi, greMy,
                          cyaAi, cyaMy, bluAi, bluMy, vioAi, vioMy, whiAi, whiMy;
    protected ArrayList<MenuElement> savedGames;

    protected HashMap<MenuElement,ArrayList<MenuElement>> menuMap;
    protected ArrayList<MenuElement> menuOption, dimOption, levelOption, moveOption, colorOption;
    protected ArrayList<MenuElement> setOption, mOption;
    protected Node menuNode ;
    protected Quaternion q = new Quaternion();
    protected GameData gd ;
    protected float x, y, z;
    protected String currentMenuElementName;
    private AssetManager am;
    private Context ctx;

    protected MenuControl(AssetManager am, GameData gd, Context ctx) {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.gd = gd;
        this.am = am;
        this.menuNode = new Node();
        this.ctx = ctx;
        this.q = q.set(0f, 0, -0.78f, -0.78f);
        this.back = new MenuElement(x, y, z-4, "backback ", ColorRGBA.White,am,  "back");
        this.back.mesh.radius = 0.41f;
        this.back.mesh.updateGeometry(10, 10, back.mesh.radius);
        this.menu = new MenuElement(x, y, z-4, "menumenu  ", ColorRGBA.White,am,  "menu");
        this.dims = new MenuElement(x, y, z-3, "dimsdims", ColorRGBA.White,am,  "dim");
        this.colors = new MenuElement(x, y, z-2, "colocolor", ColorRGBA.White,am,  "");
        this.level = new MenuElement(x, y, z-1, "levelevels", ColorRGBA.White,am,  "1");
        this.moveF = new MenuElement(x, y, z+0, "movefirst %move ", ColorRGBA.White,am,  "movem");
        this.sound = new MenuElement(x, y, z+1, "soun sound %vol", ColorRGBA.White, am,  "1");
        this.settings = new MenuElement(x, y, z-3, "settsettings", ColorRGBA.White, am,  "set");
        this.load = new MenuElement(x, y, z-1, "loadload ", ColorRGBA.White,am,  "load");
        this.save = new MenuElement(x, y, z-2, "savesave ", ColorRGBA.White,am,  "save");
        this.demo = new MenuElement(x, y, z+0, "demodemo ", ColorRGBA.White, am,  "off");
        this.netgame = new MenuElement(x, y, z+3, "net  net%game", ColorRGBA.White, am,  "off");//network
        this.locgame = new MenuElement(x, y, z+2, "loc  loc%game", ColorRGBA.White, am,  "off");//network
        this.aigame = new MenuElement(x, y, z+1, "ai   ai%game ", ColorRGBA.White, am,  "off");
        this.currentMenuElementName = "demodemo ";
        this.savedGames = new ArrayList<>();
            //this.mOption = new ArrayList<>();
        this.setOption = new ArrayList<>();
        this.menuOption = new ArrayList<>();
        this.dimOption = new ArrayList<>();
        this.levelOption = new ArrayList<>();
        this.moveOption = new ArrayList<>();
        this.colorOption = new ArrayList<>();
        this.menuMap = new HashMap<>();

        this.menuOption.add(settings);
        this.menuOption.add(demo);
        this.menuOption.add(netgame);
        this.menuOption.add(locgame);
        this.menuOption.add(aigame);
        this.menuOption.add(save);
        this.menuOption.add(load);
        this.menuOption.add(back);
        this.menuOption.add(menu);
        this.menuMap.put(menu, menuOption);

        this.setOption.add(dims);
        this.setOption.add(colors);
        this.setOption.add(level);
        this.setOption.add(moveF);
        this.setOption.add(sound);
        //this.setOption.add(back);
        this.menuMap.put(settings, setOption);

        this.dim222 = new MenuElement(x, y, z-3, "dim 222", ColorRGBA.White,am,  "222");
        this.dim322 = new MenuElement(x, y, z-2, "dim 322", ColorRGBA.White,am,  "322");
        this.dim223 = new MenuElement(x, y, z-1, "dim 223", ColorRGBA.White,am,  "223");
        this.dim323 = new MenuElement(x, y, z+0, "dim 323", ColorRGBA.White,am,  "323");
        this.dim333 = new MenuElement(x, y, z+1, "dim 333", ColorRGBA.White,am,  "333");
        this.dimOption.add(dim222);
        this.dimOption.add(dim322);
        this.dimOption.add(dim223);
        this.dimOption.add(dim323);
        this.dimOption.add(dim333);
        this.menuMap.put(dims, dimOption);

        this.lev2 = new MenuElement(x, y, z-3, "lev level1", ColorRGBA.White,am,  "level2");
        this.lev16 = new MenuElement(x, y, z-2, "lev level2", ColorRGBA.White,am,  "level16");
        this.lev32 = new MenuElement(x, y, z-1, "lev level3", ColorRGBA.White,am,  "level32");
        this.levelOption.add(lev16);
        this.levelOption.add(lev2);
        this.levelOption.add(lev32);
        this.menuMap.put(level, levelOption);

        this.moveMe = new MenuElement(x, y, z-3, "mov my%move", ColorRGBA.White,am, "my");
        this.moveAi = new MenuElement(x, y, z-2, "mov ai%move", ColorRGBA.White,am, "ai");
        this.moveOption.add(moveMe);
        this.moveOption.add(moveAi);
        this.menuMap.put(moveF, moveOption);

        this.redAi = new MenuElement(x, y, z-3, "col a1",ColorRGBA.Red, am, "a1");
        this.redMy = new MenuElement(x - 1, y, z-3, "col m1", ColorRGBA.Red, am, "m1");
        this.oraAi = new MenuElement(x, y, z-2, "col a2", new ColorRGBA(1, 0.6f, 0, 1), am, "a2Orange");
        this.oraMy = new MenuElement(x - 1, y, z-2, "col m2", new ColorRGBA(1, 0.6f, 0, 1), am, "m2Orange");
        this.yelAi = new MenuElement(x, y, z-1, "col a3", ColorRGBA.Yellow,am, "a3Yellow");
        this.yelMy = new MenuElement(x - 1, y, z-1, "col m3", ColorRGBA.Yellow, am, "m3Yellow");
        this.greAi = new MenuElement(x, y, z+0, "col a4", ColorRGBA.Green,am, "a4Green");
        this.greMy = new MenuElement(x - 1, y, z+0, "col m4", ColorRGBA.Green, am, "m4Green");
        this.cyaAi = new MenuElement(x, y, z+1, "col a5", ColorRGBA.Cyan,am, "a5Cyan");
        this.cyaMy = new MenuElement(x - 1, y, z+1, "col m5", ColorRGBA.Cyan, am, "m5Cyan");
        this.bluAi = new MenuElement(x, y, z+2, "col a6", ColorRGBA.Blue,am, "a6Blue");
        this.bluMy = new MenuElement(x - 1, y, z+2, "col m6", ColorRGBA.Blue, am, "m6Blue");
        this.vioAi = new MenuElement(x, y, z+3, "col a7", ColorRGBA.Magenta,am, "a7Magenta");
        this.vioMy = new MenuElement(x - 1, y, z+3, "col m7", ColorRGBA.Magenta, am, "m7Magenta");
        this.whiAi = new MenuElement(x, y, z+4, "col a8", ColorRGBA.White, am, "a8White");
        this.whiMy = new MenuElement(x - 1, y, z+4, "col m8", ColorRGBA.White, am, "m8White");
        //this.whiMy.selected = true;
        //this.redAi.selected = true;
        this.colorOption.add(redAi);
        this.colorOption.add(redMy);
        this.colorOption.add(oraAi);
        this.colorOption.add(oraMy);
        this.colorOption.add(yelAi);
        this.colorOption.add(yelMy);
        this.colorOption.add(greAi);
        this.colorOption.add(greMy);
        this.colorOption.add(cyaAi);
        this.colorOption.add(cyaMy);
        this.colorOption.add(bluAi);
        this.colorOption.add(bluMy);
        this.colorOption.add(vioAi);
        this.colorOption.add(vioMy);
        this.colorOption.add(whiAi);
        this.colorOption.add(whiMy);
        this.menuMap.put(colors, colorOption);
    }

    public void switchMenu1(String geomName) {
        //System.out.println("switchMenu1" + geomName.substring(4,8));
        switch (geomName.substring(4,8)) {
            case "menu":
                for (MenuElement me: menuOption) menuNode.attachChild(me.geom);
                break;
            case "sett":
                menuNode.detachAllChildren();
                for (MenuElement me: setOption) menuNode.attachChild(me.geom);
                menuNode.attachChild(back.geom);
                break;
            case "dims":
                menuNode.detachAllChildren();
                for (MenuElement me: dimOption) menuNode.attachChild(me.geom);
                menuNode.attachChild(back.geom);
                break;
            case "dim ":
                if(getMenuElementByName(geomName).selected == true) return; // if pressed the same level
                if (answerOverGame(geomName)) return; // game will restart if yes
                resetElements(dimOption);
                getMenuElementByName(geomName).geom.getMaterial().setFloat("Shininess", 8f);
                getMenuElementByName(geomName).material.setColor("Diffuse", menuSelectedColor);
                getMenuElementByName(geomName).selected = true;
                dims.value = "dim" + geomName.substring(8,11);
                dims.changed = true;
                break;
            case "move":
                if(demoMode | netMode) return;
                menuNode.detachAllChildren();
                for (MenuElement me: moveOption) menuNode.attachChild(me.geom);
                menuNode.attachChild(back.geom);
                break;
            case "mov ":
                if(getMenuElementByName(geomName).selected == true) return; // if pressed the same move
                if (answerOverGame(geomName)) return; // game will restart if yes
                moveMe.geom.getMaterial().setFloat("Shininess", 128f);
                moveMe.geom.getMaterial().setColor("Diffuse", ColorRGBA.White);
                moveMe.selected = false;
                moveAi.geom.getMaterial().setFloat("Shininess", 128f);
                moveAi.geom.getMaterial().setColor("Diffuse", ColorRGBA.White);
                moveAi.selected = false;
                resetMenuOptionsColor(moveOption);
                getMenuElementByName(geomName).setSpecular();
                getMenuElementByName(geomName).material.setColor("Diffuse", menuSelectedColor);
                getMenuElementByName(geomName).selected = true;
                moveF.value = "move" + geomName.substring(8,9);
                moveF.changed = true;
                break;
            case "leve":
                if(localMode | netMode) return;
                menuNode.detachAllChildren();
                menuNode.attachChild(back.geom);
                for (MenuElement me: levelOption) menuNode.attachChild(me.geom);
                break;
            case "lev ":
                if(getMenuElementByName(geomName).selected == true) return; // if pressed the same level
                if (answerOverGame(geomName)) return; // game will restart if yes
                lev16.geom.getMaterial().setFloat("Shininess", 128f);
                lev2.geom.getMaterial().setFloat("Shininess", 128f);
                lev32.geom.getMaterial().setFloat("Shininess", 128f);
                lev16.geom.getMaterial().setColor("Diffuse", ColorRGBA.White);
                lev2.geom.getMaterial().setColor("Diffuse", ColorRGBA.White);
                lev32.geom.getMaterial().setColor("Diffuse", ColorRGBA.White);
                lev16.selected = false;
                lev2.selected = false;
                lev32.selected = false;
                getMenuElementByName(geomName).geom.getMaterial().setFloat("Shininess", 8f);
                getMenuElementByName(geomName).material.setColor("Diffuse", menuSelectedColor);
                getMenuElementByName(geomName).selected = true;
                level.value = getMenuElementByName(geomName).value;
                level.changed = true;
                break;
            case "colo":
                menuNode.detachAllChildren();
                for (MenuElement me: colorOption) menuNode.attachChild(me.geom);
                for (MenuElement me: colorOption) if (me.selected) removeOpositeColor(me.name);
                menuNode.attachChild(back.geom);
                break;
            case "col ":
                if (geomName.substring(8,9).equals("m")) {
                    resetMyColorElements();
                    getMenuElementByName(geomName).geom.getMaterial().setFloat("Shininess", 8f);
                    getMenuElementByName(geomName).selected = true;
                    colors.value = getMenuElementByName(geomName).value;
                    colors.changed = true;
                    attachAiColor();
                    removeOposite(geomName);
                }
                else {
                    resetAiColorElements();
                    getMenuElementByName(geomName).geom.getMaterial().setFloat("Shininess", 8f);
                    getMenuElementByName(geomName).selected = true;
                    colors.value = getMenuElementByName(geomName).value;
                    colors.changed = true;
                    attachMyColor();
                    removeOposite(geomName);
                }
                break;
            case "back":
                menuNode.detachAllChildren();
                menuNode.attachChild(menu.geom);
                load.material.setColor("Diffuse", ColorRGBA.White);
                save.material.setColor("Diffuse", ColorRGBA.White);
                back.changed = true;
                break;
            case "soun":
                if (sound.value.equals("off")) {
                    sound.setSpecular();
                    //sound.value = "on";
                    sound.changed = true;
                }
                else {
                    sound.resetSpecular();
                    sound.value = "off";
                }
                sound.changed = true;
                break;
            case "hist":
                if (hist.value.equals("off")) {
                    hist.setSpecular();
                    hist.value = "on";
                }
                else {
                    hist.resetSpecular();
                    hist.value = "off";
                }
                hist.changed = true;
                break;
            case "demo":
                //if(demo.selected == true) return;
                if (answerOverGame(geomName)) return;
                resetElements(menuOption);
                demo.material.setColor("Diffuse", menuSelectedColor);
                demo.setSpecular();
                unselectMenuGameModes();
                demo.selected = true;
                aigame.value = "off";
                netgame.value = "off";
                locgame.value = "off";
                demo.changed = true;
                break;
            case "ai  ":
                //if(aigame.selected == true) return;
                if (answerOverGame(geomName)) return;
                resetElements(menuOption);
                aigame.material.setColor("Diffuse", menuSelectedColor);
                aigame.setSpecular();
                unselectMenuGameModes();
                aigame.selected = true;
                aigame.value = "on";
                demo.value = "off";
                netgame.value = "off";
                locgame.value = "off";
                aigame.changed = true;
                break;
            case "net ":
                //if(netgame.selected == true) return;
                if (answerOverGame(geomName)) return; //if (answerYesNo == false) return
                resetElements(menuOption);
                netgame.material.setColor("Diffuse", menuSelectedColor);
                netgame.setSpecular();
                unselectMenuGameModes();
                netgame.selected = true;
                demo.value = "off";
                aigame.value = "off";
                locgame.value = "off";
                netgame.changed = true;
                break;
            case "loc ":
                //if(locgame.selected == true) return;
                if (answerOverGame(geomName)) return;
                resetElements(menuOption);
                locgame.material.setColor("Diffuse", menuSelectedColor);
                locgame.setSpecular();
                unselectMenuGameModes();
                locgame.selected = true;
                demo.value = "off";
                aigame.value = "off";
                netgame.value = "off";
                locgame.changed = true;
                break;
            case "save":
                save.changed = true;
                save.setSpecular();
                break;
            case "load":
                //createLoadElements();
                //menuNode.detachAllChildren();
                //for (MenuElement me: savedGames) menuNode.attachChild(me.geom);
                //menuNode.attachChild(back.geom);
                load.changed = true;
                load.setSpecular();
                break;
            case "loaf":
                unselectMenuOption(savedGames);
                getMenuElementByName(geomName).geom.getMaterial().setFloat("Shininess", 8f);
                getMenuElementByName(geomName).material.setColor("Diffuse", menuSelectedColor);
                getMenuElementByName(geomName).selected = true;
                level.changed = true;
                break;
        }
    }

    protected Boolean answerOverGame(String geomName){
        if (GameStart.answerYesNo == false & !GameStart.demoMode & GameStart.isGameGoing) {
            GameStart.printMessage = false;
            //GameStart.writeSomeThing("Current game will over, Are you sure? (Y)", true);
            currentMenuElementName = geomName;
            GameStart.answerYesNo = true;
            final Intent intent = new Intent(ctx, TranslucentActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
            //String reply = intent.getStringExtra(TranslucentActivity.EXTRA_REPLY);
            //System.out.println("answerOverGame   TranslucentActivity " + reply);
            return true;
        }
        return false;
    }

    protected void removeOposite(String name){
        if (colors.value.substring(0,1).equals("m"))
            menuNode.detachChild(getMenuElementByName(name.replaceAll(" m" ," a")).geom);
        else menuNode.detachChild(getMenuElementByName(name.replaceAll(" a", " m")).geom);
    }
    protected void removeOpositeColor(String name){
        if (name.substring(8,9).equals("m"))
            menuNode.detachChild(getMenuElementByName(name.replace(" m" ," a")).geom);
        else menuNode.detachChild(getMenuElementByName(name.replace(" a", " m")).geom);
    }

    protected MenuElement getMenuElementByName(String name){
        for (MenuElement mn : menuMap.keySet())
            for (MenuElement mo: menuMap.get(mn)) {
                if (mo.name.equals(name)) return mo;
            }
        return null;
    }

    protected MenuElement getMenuElementByName1(String name){
        for (Map.Entry<MenuElement,ArrayList<MenuElement>> entry: menuMap.entrySet()) {
            for (MenuElement mm: entry.getValue()) {
                if(mm.name.equals(name)) return mm;
            }
        }
        return null;
    }
    protected MenuElement getMenuElementByGeometryName(String name){
        for (Map.Entry<MenuElement,ArrayList<MenuElement>> entry: menuMap.entrySet()) {
            for (MenuElement mm: entry.getValue()) {
                //System.out.println("MenuElementByGeometryName " + mm.selected);
                if(mm.geom.getName().equals(name)) return mm;
            }
        }
        return null;
    }

    protected Vector3f getSelectedDimension() {
        for (MenuElement md:dimOption) {
            if (md.selected) return new Vector3f(Float.valueOf(md.value.substring(0,1)),
                    Float.valueOf(md.value.substring(1,2)),
                    Float.valueOf(md.value.substring(2)));
        }
        return null;
    }

    protected MenuElement getMenuElementByDimension(String dim) {
        for (MenuElement me:dimOption) {
            if (me.value.equals(dim)) return me;
        }
        return null;
    }

    protected MenuElement getMenuElementByLevel(int level) {
        for (MenuElement me:levelOption) if (me.value.equals("level" + String.valueOf(level))) return me;
        return null;
    }

    protected MenuElement getSelectedElementByColor(ColorRGBA color, String who) {
        for (MenuElement me:colorOption) {
            if (color.equals(getColorRGBA(me.value)) & who.substring(0,1).equals(me.value.substring(0,1))) return me;
        }
        return null;
    }
    protected MenuElement getSelectedColorElement(String who) {
        for (MenuElement me:colorOption) {
            if (me.selected & who.substring(0,1).equals(me.value.substring(0,1))) return me;
        }
        return null;
    }
    protected MenuElement getMenuElementByValue(String val){
        for (Map.Entry<MenuElement,ArrayList<MenuElement>> entry: menuMap.entrySet()) {
            for (MenuElement me: entry.getValue()) {
                if(me.value.equals(val)) return me;
            }
        }
        return null;
    }

    void attachMyColor(){
        if (!redMy.selected) menuNode.attachChild(redMy.geom);
        if (!oraMy.selected) menuNode.attachChild(oraMy.geom);
        if (!yelMy.selected) menuNode.attachChild(yelMy.geom);
        if (!greMy.selected) menuNode.attachChild(greMy.geom);
        if (!cyaMy.selected) menuNode.attachChild(cyaMy.geom);
        if (!bluMy.selected) menuNode.attachChild(bluMy.geom);
        if (!vioMy.selected) menuNode.attachChild(vioMy.geom);
        if (!whiMy.selected) menuNode.attachChild(whiMy.geom);
    }
    void attachAiColor(){
        if (!redAi.selected) menuNode.attachChild(redAi.geom);
        if (!oraAi.selected) menuNode.attachChild(oraAi.geom);
        if (!yelAi.selected) menuNode.attachChild(yelAi.geom);
        if (!greAi.selected) menuNode.attachChild(greAi.geom);
        if (!cyaAi.selected) menuNode.attachChild(cyaAi.geom);
        if (!bluAi.selected) menuNode.attachChild(bluAi.geom);
        if (!vioAi.selected) menuNode.attachChild(vioAi.geom);
        if (!whiAi.selected) menuNode.attachChild(whiAi.geom);
    }
    public ColorRGBA getColorRGBA(String col) {
        if (col.substring(0,2).equals("a1")) return ColorRGBA.Red;
        if (col.substring(0,2).equals("m1")) return ColorRGBA.Red;
        if (col.substring(0,2).equals("a2")) return new ColorRGBA(1, 0.6f, 0, 1);
        if (col.substring(0,2).equals("m2")) return new ColorRGBA(1, 0.6f, 0, 1);
        if (col.substring(0,2).equals("a3")) return ColorRGBA.Yellow;
        if (col.substring(0,2).equals("m3")) return ColorRGBA.Yellow;
        if (col.substring(0,2).equals("a4")) return ColorRGBA.Green;
        if (col.substring(0,2).equals("m4")) return ColorRGBA.Green;
        if (col.substring(0,2).equals("a5")) return ColorRGBA.Cyan;
        if (col.substring(0,2).equals("m5")) return ColorRGBA.Cyan;
        if (col.substring(0,2).equals("a6")) return ColorRGBA.Blue;
        if (col.substring(0,2).equals("m6")) return ColorRGBA.Blue;
        if (col.substring(0,2).equals("a7")) return ColorRGBA.Magenta;
        if (col.substring(0,2).equals("m7")) return ColorRGBA.Magenta;
        if (col.substring(0,2).equals("a8")) return ColorRGBA.White;
        if (col.substring(0,2).equals("m9")) return ColorRGBA.White;
        return ColorRGBA.White;
    }

    private void resetMyColorElements(){
        redMy.geom.getMaterial().setFloat("Shininess", 128f);
        redMy.selected = false;
        oraMy.geom.getMaterial().setFloat("Shininess", 128f);
        oraMy.selected = false;
        yelMy.geom.getMaterial().setFloat("Shininess", 128f);
        yelMy.selected = false;
        greMy.geom.getMaterial().setFloat("Shininess", 128f);
        greMy.selected = false;
        cyaMy.geom.getMaterial().setFloat("Shininess", 128f);
        cyaMy.selected = false;
        bluMy.geom.getMaterial().setFloat("Shininess", 128f);
        bluMy.selected = false;
        vioMy.geom.getMaterial().setFloat("Shininess", 128f);
        vioMy.selected = false;
        whiMy.geom.getMaterial().setFloat("Shininess", 128f);
        whiMy.selected = false;
    }
    private void resetAiColorElements(){
        redAi.geom.getMaterial().setFloat("Shininess", 128f);
        redAi.selected = false;
        oraAi.geom.getMaterial().setFloat("Shininess", 128f);
        oraAi.selected = false;
        yelAi.geom.getMaterial().setFloat("Shininess", 128f);
        yelAi.selected = false;
        greAi.geom.getMaterial().setFloat("Shininess", 128f);
        greAi.selected = false;
        cyaAi.geom.getMaterial().setFloat("Shininess", 128f);
        cyaAi.selected = false;
        bluAi.geom.getMaterial().setFloat("Shininess", 128f);
        bluAi.selected = false;
        vioAi.geom.getMaterial().setFloat("Shininess", 128f);
        vioAi.selected = false;
        whiAi.geom.getMaterial().setFloat("Shininess", 128f);
        whiAi.selected = false;
    }
    private void resetElements(ArrayList<MenuElement> option) {
        for (MenuElement me: option ) {
            me.material.setColor("Diffuse", ColorRGBA.White);
            me.geom.getMaterial().setFloat("Shininess", 128f);
            me.selected = false;
        }
    }

    protected void resetAllSpecular(){
        for (Map.Entry<MenuElement,ArrayList<MenuElement>> entry: menuMap.entrySet()) {
            for (MenuElement me: entry.getValue()) {
                me.geom.getMaterial().clearParam("Shininess");
            }
        }
    }

    protected void unselectMenuItems() {
        for (Map.Entry<MenuElement,ArrayList<MenuElement>> entry: menuMap.entrySet()) {
            for (MenuElement me: entry.getValue()) {
                me.selected = false;
            }
        }
    }

    protected void  unselectMenuGameModes() {
        netgame.selected = false;
        locgame.selected = false;
        aigame.selected = false;
        demo.selected = false;
    }
    protected void  unselectMenuOption(ArrayList<MenuElement> mO) {
        for(MenuElement me: mO) me.selected = false;
    }
    protected void resetBackLigthing(){
        for (Map.Entry<MenuElement,ArrayList<MenuElement>> entry: menuMap.entrySet()) {
            for (MenuElement me: entry.getValue()) {
                me.geom.getMaterial().setColor("Diffuse", ColorRGBA.White);
            }
        }
    }
    protected void resetMenuOptionsColor(ArrayList<MenuElement> menuOption){
        for (MenuElement me: menuOption) {
            me.material.setColor("Diffuse", ColorRGBA.White);
        }
    }
    protected MenuElement createMenuElement(String name, AssetManager am, int a){
        return new MenuElement(this.x, this.y, this.z + a, "loaf" + name, ColorRGBA.White, am, "f");
    }
    private void createLoadElements() { // balls with fileNames to load saved game.
        int start=-4;                       // start position +
        for (String name: gd.getModeFileNames(gd.ctx)) {
            savedGames.add(createMenuElement(name, am, start));
            start++;
        }
    }
}
