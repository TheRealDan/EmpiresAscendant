package dev.therealdan.empiresascendant.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;

public class FontManager implements Disposable {

    private FreeTypeFontGenerator freeTypeFontGenerator;

    private HashMap<Integer, BitmapFont> fonts = new HashMap<>();

    public float scale;

    public FontManager() {
        freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Montserrat-Medium.ttf"));
    }

    public void draw(SpriteBatch batch, OrthographicCamera camera, String text, float x, float y, Color color) {
        draw(batch, camera, text, x, y, 16, color);
    }

    public void draw(SpriteBatch batch, OrthographicCamera camera, String text, float x, float y, float fontSize, Color color) {
        BitmapFont font = getFont(fontSize, camera);
        font.setColor(color);
        font.draw(batch, text, x, y, 0, Align.left, false);
    }

    public void draw(SpriteBatch batch, OrthographicCamera camera, String text, float x, float y, float width, float fontSize, Color color) {
        BitmapFont font = getFont(fontSize, camera);
        font.setColor(color);
        font.draw(batch, text, x, y, width, Align.left, true);
    }

    public void center(SpriteBatch batch, OrthographicCamera camera, String text, float x, float y, float fontSize, Color color) {
        BitmapFont font = getFont(fontSize, camera);
        font.setColor(color);
        font.draw(batch, text, x, y + font.getCapHeight() / 2f, 0, Align.center, false);
    }

    public float getWidth(SpriteBatch batch, OrthographicCamera camera, String text, float fontSize) {
        return getFont(fontSize, camera).draw(batch, text, Gdx.graphics.getWidth(), 0).width;
    }

    public float getHeight(SpriteBatch batch, OrthographicCamera camera, String text, float fontSize) {
        return getFont(fontSize, camera).draw(batch, text, Gdx.graphics.getWidth(), 0).height;
    }

    @Override
    public void dispose() {
        freeTypeFontGenerator.dispose();
    }

    private void generateFont(int fontSize) {
        FreeTypeFontGenerator.FreeTypeFontParameter freeTypeFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        freeTypeFontParameter.size = fontSize;
        fonts.put(fontSize, freeTypeFontGenerator.generateFont(freeTypeFontParameter));
    }

    private BitmapFont getFont(float fontSize, OrthographicCamera camera) {
        int font = (int) (fontSize * camera.zoom * scale);
        if (!fonts.containsKey(font)) generateFont(font);
        return fonts.get(font);
    }
}
