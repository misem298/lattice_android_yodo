package com.gamelattice;

import android.content.res.AssetManager;
import android.media.SoundPool;
import android.os.Build;
import androidx.annotation.RequiresApi;
import java.io.IOException;

public class SoundAndroid {
    protected SoundPool soundPool;
    protected int click_audio;
    protected int pod_audio;
    protected int trol_audio;
    protected int chime_audio;
    protected int bell_audio;
    protected int pick_audio;
    protected int cymb_audio;
    protected int burst_audio;
    protected int note_audio;
    protected int move_audio;
    protected int heart_audio;
    protected int win_audio;
    protected int expls_audio;
    protected int coin_audio;
    protected int ocean_audio;
    protected int fanf_audio;
    protected int pick1_audio;
    protected int gong_audio;
    protected int neon_audio;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SoundAndroid(AssetManager am) throws IOException {
        soundPool = new SoundPool.Builder().setMaxStreams(3).build();
        this.click_audio = soundPool.load(am.openFd("Sounds/click.ogg"), 0);
        this.pod_audio = soundPool.load(am.openFd("Sounds/pod.ogg"), 0);
        this.trol_audio = soundPool.load(am.openFd("Sounds/trolley.ogg"), 0);
        this.chime_audio = soundPool.load(am.openFd("Sounds/chime_up.ogg"), 0);
        this.bell_audio = soundPool.load(am.openFd("Sounds/bell.ogg"), 0);
        this.pick_audio = soundPool.load(am.openFd("Sounds/pick.ogg"), 0);
        this.cymb_audio = soundPool.load(am.openFd("Sounds/cymbals.ogg"), 0);
        this.burst_audio = soundPool.load(am.openFd("Sounds/burst.ogg"), 0);
        this.move_audio = soundPool.load(am.openFd("Sounds/move.ogg"), 0);
        this.note_audio = soundPool.load(am.openFd("Sounds/note.ogg"), 0);
        this.heart_audio = soundPool.load(am.openFd("Sounds/heart.ogg"), 0);
        this.win_audio = soundPool.load(am.openFd("Sounds/win.ogg"), 0);
        this.expls_audio = soundPool.load(am.openFd("Sounds/explosion.ogg"), 0);
        this.coin_audio = soundPool.load(am.openFd("Sounds/coin_roll.ogg"), 0);
        this.ocean_audio = soundPool.load(am.openFd("Sounds/ocean.ogg"), 0);
        this.fanf_audio = soundPool.load(am.openFd("Sounds/fanfare.ogg"), 0);
        this.pick1_audio = soundPool.load(am.openFd("Sounds/pick1.ogg"), 0);
        this.gong_audio = soundPool.load(am.openFd("Sounds/gong3.ogg"), 0);
        this.neon_audio = soundPool.load(am.openFd("Sounds/neon.ogg"), 0);
    }
}
