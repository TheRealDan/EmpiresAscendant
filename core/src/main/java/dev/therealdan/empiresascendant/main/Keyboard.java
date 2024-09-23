package dev.therealdan.empiresascendant.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public abstract class Keyboard {

    public static boolean isShiftHeld() {
        return Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
    }
}
