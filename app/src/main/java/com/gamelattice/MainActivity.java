package com.gamelattice;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;

import androidx.annotation.NonNull;
import android.os.Bundle;
import com.jme3.app.AndroidHarness;
import java.util.logging.Level;
import java.util.logging.LogManager;
import com.jme3.math.Vector2f;
import com.yodo1.mas.Yodo1Mas;
import com.yodo1.mas.error.Yodo1MasError;

public class MainActivity extends AndroidHarness {
    private static final int ORIENTATION_LANDSCAPE = 1;
    static protected Display display;
    static protected DisplayMetrics dm;
    static int screenHeight;
    //static SoundAndroid soundEffects;
    //AssetManager ama;
    //static Typeface typeFace;
    static int screenWidth;
    private Vector2f v2f;
    static protected View view ;

    public MainActivity() {
        appClass = "com.gamelattice.GameStart";
        exitDialogTitle = "Exit?";
        exitDialogMessage = "Are you sure you want to quit?";
        // view  = findViewById(R.id.editText);
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //dm = getResources().getDisplayMetrics();
        // Set the default logging level (default=Level.INFO, Level.ALL=All Debug Info)
        LogManager.getLogManager().getLogger("").setLevel(Level.INFO);

    }

        @Override
        public void onCreate (Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Yodo1Mas.getInstance().init(this, "ca-app-pub-5784311004955543~1150310191", new Yodo1Mas.InitListener() {
                @Override
                public void onMasInitSuccessful() {
                }

                @Override
                public void onMasInitFailed(@NonNull Yodo1MasError error) {
                }
            });
    }
    /*@Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) newConfig.orientation = Configuration.ORIENTATION_LANDSCAPE;
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
      //
        //if (newConfig.orientation == Configuration.ORIENTATION_REVERSE_PORTRAIT) newConfig.orientation = Configuration.ORIENTATION_LANDSCAPE;
    }*/

    /*@Override
     protected void onCreate() {
        super.onCreate(this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        //if (this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

     }*/

    // @Override
    // protected void onRestart() {
    //     super.onRestart();
    //     GameStart app = new GameStart();
    //     app.begin();
    // }
    /*@Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }*/
}
