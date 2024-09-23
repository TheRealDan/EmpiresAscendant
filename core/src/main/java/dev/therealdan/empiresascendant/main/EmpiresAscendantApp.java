package dev.therealdan.empiresascendant.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import dev.therealdan.empiresascendant.screens.GameScreen;

import java.util.LinkedList;
import java.util.List;

public class EmpiresAscendantApp extends Game {

    public FontManager font;
    public Textures textures;

    public InputMultiplexer inputMultiplexer;
    public SpriteBatch batch;

    private LinkedList<Screen> screens = new LinkedList<>();

    @Override
    public void create() {
        font = new FontManager();
        textures = new Textures();
        inputMultiplexer = new InputMultiplexer();
        batch = new SpriteBatch();

        addScreen(new GameScreen(this));

        Gdx.app.getGraphics().setVSync(false);
        Gdx.app.getGraphics().setForegroundFPS(-1);

        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void dispose() {
        for (Screen screen : getScreens())
            screen.dispose();
        font.dispose();
        batch.dispose();
    }

    @Override
    public void pause() {
        for (Screen screen : getScreens())
            screen.pause();
    }

    @Override
    public void resume() {
        for (Screen screen : getScreens())
            screen.resume();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0.3f, 0, 1);
        batch.begin();
        for (Screen screen : getScreens())
            screen.render(Gdx.graphics.getDeltaTime());
        batch.end();
        batch.setShader(null);
    }

    @Override
    public void resize(int width, int height) {
        batch.dispose();
        batch = new SpriteBatch();
        font.scale = Gdx.graphics.getWidth() / 1000f;
        for (Screen screen : getScreens())
            screen.resize(width, height);
    }

    @Override
    public void setScreen(Screen screen) {
        for (Screen each : getScreens())
            each.hide();
        screens.clear();
        addScreen(screen);
    }

    public void addScreen(Screen screen) {
        if (screen != null) {
            screens.addFirst(screen);
            screen.show();
            screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    public void removeScreen(Screen screen) {
        if (screens.contains(screen)) {
            screen.hide();
            screens.remove(screen);
        }
    }

    public List<Screen> getScreens() {
        return screens;
    }

    @Override
    public Screen getScreen() {
        return !screens.isEmpty() ? screens.get(0) : null;
    }
}
