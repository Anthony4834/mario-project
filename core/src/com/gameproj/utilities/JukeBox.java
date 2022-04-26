package com.gameproj.utilities;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.audio.Wav;
import com.badlogic.gdx.files.FileHandle;
import com.gameproj.GameOne;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

public class JukeBox {

    private HashMap<String, Sound> sounds = new HashMap<>();
    private HashMap<String, Music> music = new HashMap<>();
    private Sound current;

    public JukeBox init() {

        for(FileHandle f : Gdx.files.internal("sounds").list()) {
            String fName = f.toString().substring(7, f.toString().length() - 4).toLowerCase().replace(" ", "_");
            if(f.extension().equals("wav"))
                sounds.put(fName, Gdx.audio.newSound(f));

            else
                music.put(fName, Gdx.audio.newMusic(f));
        }

        return this;
    }

    public void play(String sound) {
        if(sounds.containsKey(sound)) {
            Sound s = sounds.get(sound);
            s.play();
            current = s;
        }
    }
    public void play(String sound, String x) {
        if(x.equals("noOverlap")) {
            if(current != null)
                current.stop();

            play(sound);
        }
    }
    public void play(String sound, boolean isMusic) {
        if(isMusic) {
            Music m = music.get(sound);
            m.setLooping(true);
            m.play();
            m.setVolume((float) 0.4);
        }
    }


}
