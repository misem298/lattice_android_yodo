/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamelattice;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;


import androidx.annotation.RequiresApi;

import com.jme3.math.ColorRGBA;
//import java.awt.Window;
//import java.io.File;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 *
 * @author Administrator
 */
public class GameData {
    private static final int REQUEST_CODE = 6384;
    private static final int RESULT_OK = 1;
    private static final int PICKFILE_RESULT_CODE = 77;

    protected ArrayList<Checker> mychks = new ArrayList<>();
    protected ArrayList<Checker> aichks = new ArrayList<>();
    protected List<String> lines;
    protected ColorRGBA myclr;
    protected ColorRGBA aiclr;
    protected int dimensionX;
    protected int dimensionY;
    protected int dimensionZ;
    protected String move, gameName, gamerId, netgameId, error, gameMode;
    protected int smartLevel, mode;
    protected boolean foundDataFile;
    protected boolean gameSaved;
    protected float soundVolume;
    protected float deltaVolume;
    protected Boolean soundOn, nameInputMode, isSaveDialog, isLoadDialog;
    protected Context ctx;

    protected GameData(Context ctx) {
        this.foundDataFile = false;
        this.lines = new ArrayList();
        this.foundDataFile = false;
        this.gameName = "Input name: ";//lattice.ltc";
        this.gameMode = "";
        this.soundOn = true;
        this.isSaveDialog = false;
        this.isLoadDialog = false;
        //this.soundRepeator = 0;
        this.nameInputMode = false;
        //this.bmText = bmText;
        this.error = "ok";
        this.ctx = ctx;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void readSettings(String fileName, Context ctx) throws FileNotFoundException {
        foundDataFile = true;
        lines = readFile(fileName, ctx);
        if(!foundDataFile) return;
        //Path file = Paths.get("checker.txt");
        //lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        try {
            /// System.out.println("l " + s);
            String s = lines.get(0);
            //int k = s.indexOf(">");
            //int j = s.indexOf("<", 1);
            s = s.substring(s.indexOf(">") + 1, s.indexOf("<", 1));
            float r = Float.valueOf(s.substring(0, s.indexOf(",")));
            s = s.substring(s.indexOf(",") + 1);
            float g = Float.valueOf(s.substring(0, s.indexOf(",")));
            s = s.substring(s.indexOf(",") + 1);
            float b = Float.valueOf(s.substring(0, s.indexOf(",")));
            s = s.substring(s.indexOf(",") + 1);
            float a = Float.valueOf(s);
            myclr = new ColorRGBA(r, g, b, a);

            s = lines.get(1);
            s = s.substring(s.indexOf(">") + 1, s.indexOf("<", 1));
            r = Float.valueOf(s.substring(0, s.indexOf(",")));
            s = s.substring(s.indexOf(",") + 1);
            g = Float.valueOf(s.substring(0, s.indexOf(",")));
            s = s.substring(s.indexOf(",") + 1);
            b = Float.valueOf(s.substring(0, s.indexOf(",")));
            s = s.substring(s.indexOf(",") + 1);
            a = Float.valueOf(s);
            aiclr = new ColorRGBA(r, g, b, a);

            s = lines.get(2);
            move = s.substring(s.indexOf(">") + 1, s.indexOf("<", 1));

            s = lines.get(3);
            s = s.substring(s.indexOf(">") + 1, s.indexOf("<", 1));
            dimensionX = Integer.valueOf(s.substring(0, s.indexOf(",")));
            s = s.substring(s.indexOf(",") + 1);
            dimensionY = Integer.valueOf(s.substring(0, s.indexOf(",")));
            s = s.substring(s.indexOf(",") + 1);
            dimensionZ = Integer.valueOf(s);

            s = lines.get(4);
            s = s.substring(s.indexOf(">") + 1, s.indexOf("<", 1));
            smartLevel = Integer.valueOf(s);

            s = lines.get(5);
            s = s.substring(s.indexOf(">") + 1, s.indexOf("<", 1));
            soundVolume = Float.valueOf(s);
            s = lines.get(6);
            s = s.substring(s.indexOf(">") + 1, s.indexOf("<", 1));
            deltaVolume = Float.valueOf(s);

            s = lines.get(7);
            s = s.substring(s.indexOf(">") + 1, s.indexOf("<", 1));
            mode = Integer.valueOf(s);

        } catch (NullPointerException | StringIndexOutOfBoundsException ex) {
            foundDataFile = false;
            System.out.println(ex.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected ArrayList<String> readFile(String fileName, Context ctx) throws FileNotFoundException {
        FileInputStream fis = ctx.openFileInput(fileName);
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> allLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8))) {
            String line = "";
            while (line != null) {
                //stringBuilder.append(line).append('\n');
                line = reader.readLine();
                if (line != null) allLines.add(line);
            }
        } catch (IOException e) {
            foundDataFile = false;
            e.printStackTrace();
        }
        return allLines;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void loadGame(Context ctx, String fileName) throws FileNotFoundException {
        foundDataFile = true;
        String who = "";
        mychks.clear();
        aichks.clear();
        gamerId = "";
        netgameId = "";
        lines = readFile(fileName, ctx);
        if (lines.isEmpty()) return;
        try {
            for (String s : lines) {
                if (s.contains("mycolor")) {
                    s = s.substring(s.indexOf(">") + 1, s.indexOf("<", 1));
                    float r = Float.valueOf(s.substring(0, s.indexOf(",")));
                    s = s.substring(s.indexOf(",") + 1);
                    float g = Float.valueOf(s.substring(0, s.indexOf(",")));
                    s = s.substring(s.indexOf(",") + 1);
                    float b = Float.valueOf(s.substring(0, s.indexOf(",")));
                    s = s.substring(s.indexOf(",") + 1);
                    float a = Float.valueOf(s);
                    myclr = new ColorRGBA(r, g, b, a);
                }
                if (s.contains("aicolor")) {
                    s = s.substring(s.indexOf(">") + 1, s.indexOf("<", 1));
                    float r = Float.valueOf(s.substring(0, s.indexOf(",")));
                    s = s.substring(s.indexOf(",") + 1);
                    float g = Float.valueOf(s.substring(0, s.indexOf(",")));
                    s = s.substring(s.indexOf(",") + 1);
                    float b = Float.valueOf(s.substring(0, s.indexOf(",")));
                    s = s.substring(s.indexOf(",") + 1);
                    float a = Float.valueOf(s);
                    aiclr = new ColorRGBA(r, g, b, a);
                }
                if (s.contains("firstMove")) {
                    move = s.substring(s.indexOf(">") + 1, s.indexOf("<", 1));
                }
                if (s.contains("dimension")) {
                    s = s.substring(s.indexOf(">") + 1, s.indexOf("<", 1));
                    dimensionX = Integer.valueOf(s.substring(0, s.indexOf(",")));
                    s = s.substring(s.indexOf(",") + 1);
                    dimensionY = Integer.valueOf(s.substring(0, s.indexOf(",")));
                    s = s.substring(s.indexOf(",") + 1);
                    dimensionZ = Integer.valueOf(s);
                }
                if (s.contains("smart")) {
                    smartLevel = Integer.valueOf(s.substring(s.indexOf(">") + 1, s.indexOf("<", 1)));
                }
                if (s.contains("soundVolume")) {
                    soundVolume = Float.valueOf(s.substring(s.indexOf(">") + 1, s.indexOf("<", 1)));
                }
                if (s.contains("deltaVolume")) {
                    deltaVolume = Float.valueOf(s.substring(s.indexOf(">") + 1, s.indexOf("<", 1)));
                }

                if (s.contains("mycheckers")) who = "my";
                if (s.contains("aicheckers")) who = "ai";
                if (s.contains("<checker>")) {
                    s = s.substring(s.indexOf("<checker>") + 9, s.indexOf("</checker>"));
                    int x = Integer.valueOf(s.substring(0, s.indexOf(",")));
                    s = s.substring(s.indexOf(",") + 1);
                    int y = Integer.valueOf(s.substring(0, s.indexOf(",")));
                    s = s.substring(s.indexOf(",") + 1);
                    int z = Integer.valueOf(s.substring(0, s.indexOf(",")));
                    s = s.substring(s.indexOf(",") + 1);
                    boolean b;
                    if (s.equals("true")) b = true;
                    else b = false;
                    if (who.equals("my")) {
                        Checker c = new Checker(x, y, z, myclr);
                        c.mamka = b;
                        mychks.add(c);
                    }
                    if (who.equals("ai")) {
                        Checker c = new Checker(x, y, z, aiclr);
                        c.mamka = b;
                        aichks.add(c);
                    }
                }
            }
            foundDataFile = true;
        } catch (StringIndexOutOfBoundsException ex) {
            foundDataFile = false;
            System.out.println(ex.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void saveGame(ArrayList<Checker> mycheckers, ArrayList<Checker> aicheckers,
                            String firstMove, int dimensionX, int dimensionY, int dimensionZ,
                            int level, float vol, float del, Context ctx, int gMode) throws FileNotFoundException {
        this.gameSaved = true;
        String[] sm = new String[mycheckers.size() + 1];
        String[] sa = new String[aicheckers.size() + 1];
        int i = 0;
        sm[0] = "<mycheckers>";
        sa[0] = "<aicheckers>";
        for (Checker c : mycheckers) {
            i++;
            sm[i] = sm[i - 1] + "<checker>" + String.valueOf(c.x) + "," +
                    String.valueOf(c.y) + "," +
                    String.valueOf(c.z) + "," +
                    String.valueOf(c.mamka) + "</checker>" + "\n";
        }
        String mych = sm[i] + "</mycheckers>" + "\n";
        i = 0;
        for (Checker c : aicheckers) {
            i++;
            sa[i] = sa[i - 1] + "<checker>" + String.valueOf(c.x) + "," +
                    String.valueOf(c.y) + "," +
                    String.valueOf(c.z) + "," +
                    String.valueOf(c.mamka) + "</checker>" + "\n";
        }
        String aich = sa[i] + "</aicheckers>" + "\n";

        ColorRGBA myc = mycheckers.get(0).defaultcolor;
        String mycol = "<mycolor>" + String.valueOf(Math.round(myc.r * 10) * 0.1f) + "," +
                String.valueOf(Math.round(myc.g * 10) * 0.1f) + "," +
                String.valueOf(Math.round(myc.b * 10) * 0.1f) + "," +
                String.valueOf(Math.round(myc.a * 10) * 0.1f) + "</color>" + "\n";

        ColorRGBA aic = aicheckers.get(0).defaultcolor;
        String aicol = "<aicolor>" + String.valueOf(Math.round(aic.r * 10) * 0.1f) + "," +
                String.valueOf(Math.round(aic.g * 10) * 0.1f) + "," +
                String.valueOf(Math.round(aic.b * 10) * 0.1f) + "," +
                String.valueOf(Math.round(aic.a * 10) * 0.1f) + "</aicolor>" + "\n";
        firstMove = "<firstMove>" + firstMove + "</firstMove>" + "\n";
        String lev = "<smart>" + String.valueOf(level) + "</smart>" + "\n";
        String dimen = "<dimension>" + String.valueOf(dimensionX) + "," +
                String.valueOf(dimensionY) + "," +
                String.valueOf(dimensionZ) + "</dimension>" + "\n";
        String sv = "<soundVolume>" + String.valueOf(vol) + "</soundVolume>" + "\n";
        String dv = "<deltaVolume>" + String.valueOf(del) + "</deltaVolume>" + "\n";
        String gmd = "<mode>" + String.valueOf(gMode) + "</mode>" + "\n";
        //System.out.println("saveGame " + makeNewGameFile(ctx, GameStart.gameModeName[gMode]) + ".ltc");
        saveToFile(mycol + aicol + firstMove + dimen + mych + aich + lev + gmd,
                SaveFileActivity.fName, ctx);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void saveToFile(String settings, String fileName, Context ctx) throws FileNotFoundException {
        try (FileOutputStream fos = ctx.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write(settings.getBytes());
        } catch (IOException e) {
            gameSaved = false;
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void settingsToFile(ColorRGBA myc, ColorRGBA aic, String fm, int dx, int dy, int dz,
                                  int level, float vol, float del, Context ctx, int gMode) throws FileNotFoundException {

        String mycol = "<mycolor>" + String.valueOf(myc.r) + "," +
                String.valueOf(myc.g) + "," +
                String.valueOf(myc.b) + "," +
                String.valueOf(myc.a) + "</color>" + "\n";

        String aicol = "<aicolor>" + String.valueOf(aic.r) + "," +
                String.valueOf(aic.g) + "," +
                String.valueOf(aic.b) + "," +
                String.valueOf(aic.a) + "</aicolor>" + "\n";
        fm = "<firstMove>" + fm + "</firstMove>" + "\n";
        String dimen = "<dimension>" + String.valueOf(dx) + "," +
                String.valueOf(dy) + "," +
                String.valueOf(dz) + "</dimension>" + "\n";
        String lev = "<smart>" + String.valueOf(level) + "</smart>" + "\n";
        String sv = "<soundVolume>" + String.valueOf(vol) + "</soundVolume>" + "\n";
        String dv = "<deltaVolume>" + String.valueOf(del) + "</deltaVolume>" + "\n";
        String gmd = "<mode>" + String.valueOf(gMode) + "</mode>" + "\n";
        saveToFile(mycol + aicol + fm + dimen + lev + sv + dv + gmd, "checker.txt", ctx);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void addNetData(String uuid, String gameId, String fileName) {
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.append("<uuid>" + uuid + "</uuid>" + "\n" + "<gameId>" + gameId + "</gameId>" + "\n");
            writer.flush();
            gameSaved = true;
        } catch (IOException ex) {
            gameSaved = false;
            error = "cannot add data";
            System.out.println(ex.getMessage() + "gd.addNetData()");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void readFile1(String fileName) {
        try {
            Path file = Paths.get(fileName);
            lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            lines.clear();
            foundDataFile = false;
            System.out.println(ex.getMessage());
        }
    }
//**************************************************************************************
//************** get game files ".ltc" from data/app/ , filters ,***********************
//**************************************************************************************
    private File[] getGameFilesArray(Context ctx) {
        File f = ctx.getFilesDir();
        File[] files = f.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.toString().endsWith(".ltc");
            }
        });
        return files;
    }

    protected ArrayList<String> getModeFileNames(Context ctx) {
        File[] files = getGameFilesArray(ctx);
        ArrayList modeFiles = new ArrayList<>();
        for (File f : files)  modeFiles.add(f.getName());
        Collections.sort(modeFiles);
        return modeFiles; //sorted arraylist
    }

    /*private String makeNewGameFile(Context ctx, String gameModeName) {
        ArrayList<String> names = getModeFileNames(ctx);
        if (names.size() == 0) {
            //System.out.println("makeNewGameFile " + gameModeName );
            return gameModeName + "1";
        }
        //System.out.println("makeNewGameFile " + gameModeName );
        return gameModeName + getNextGameNameOrder(names);
    }

    private String getNextGameNameOrder(ArrayList<String> gameNames) {
        int n = 1;
        int k = 0;
        for (String name : gameNames) {
            k = Integer.parseInt(name.replaceAll("[^\\d]", ""));
            if (k >= n) n = k + 1;
            //System.out.println("getNextGameNameOrder " + n);
        }
        return String.valueOf(n);
    }*/
}



      /*@RequiresApi(api = Build.VERSION_CODES.O)
    protected void readFile(String fileName){
        try {
            Path file = Paths.get(fileName);
            lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        }
        catch(IOException ex){
            lines.clear();
            foundDataFile = false;
            System.out.println(ex.getMessage());
        }
    }*/
   /* @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void saveData(String data, String fileName) {
        gameSaved = true;
        try (FileWriter writer = new FileWriter(fileName, false)) {
            writer.write(data);
            // запись по символам
            //writer.append('\n');
            //writer.append('E');
            writer.flush();
        } catch (IOException ex) {
            gameSaved = false;
            System.out.println(ex.getMessage());
        }
    }*/

  /*private String showDialog(String dialog) {
        //Window activeWindow = getSelectedWindow(Window.getWindows());
        //Window activeWindow = javax.swing.FocusManager.getCurrentManager().getActiveWindow();
        //Component parent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        File file = new File("lattice.ltc");
        JFileChooser fileChooser = new JFileChooser();
        //Window[] ws = Window.getWindows();
        JFrame frame = new JFrame(); 
        frame.setSize(1,1);//400 width and 500 height; 
        //frame.setLocation(1,1);
        frame.removeAll();
        frame.setAlwaysOnTop(true);
        fileChooser.setDialogTitle("Specify a file to save"); 
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter( "game files", "ltc" ));
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setCurrentDirectory(file);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int num = 0;
        if (dialog.equals("save")) num = fileChooser.showSaveDialog(frame);
        if (dialog.equals("open")) num = fileChooser.showOpenDialog(frame);
        if (num == JFileChooser.APPROVE_OPTION) {
        file = fileChooser.getSelectedFile();
            // save to file
        }
        return file.getName();
    }*/
    
    /*protected Window getSelectedWindow(Window[] windows) {
    Window result = null;
    //for (int i = 0; i < windows.length; i++) {
    for (Window window: windows) if (window.isFocused()) return window;
        //Window window = windows[i];
        //if (window.isActive()) {
        //    result = window;
        //} 
        //else {
        //    Window[] ownedWindows = window.getOwnedWindows();
        //    if (ownedWindows != null) {
        //        result = getSelectedWindow(ownedWindows);
        //    }
        //}
        //}
    return null;
    //return result;
    } */

    

