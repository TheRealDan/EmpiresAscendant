package dev.therealdan.empiresascendant.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import dev.therealdan.empiresascendant.main.EmpiresAscendantApp;

public abstract class BaseScreen implements Screen, InputProcessor {

    protected EmpiresAscendantApp app;

    protected ScreenViewport viewport;
    protected OrthographicCamera camera;

    public BaseScreen(EmpiresAscendantApp app) {
        this.app = app;
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
    }

    @Override
    public void show() {
        app.inputMultiplexer.addProcessor(this);
    }

    @Override
    public void render(float delta) {
        camera.update();
        app.batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        app.inputMultiplexer.removeProcessor(this);
        dispose();
    }

    @Override
    public void dispose() {
    }

    public boolean containsMouse(float x, float y, float width, float height) {
        float mx = Gdx.input.getX() - Gdx.graphics.getWidth() / 2f;
        float my = -Gdx.input.getY() + Gdx.graphics.getHeight() / 2f;
        return x < mx && mx < x + width
            && y < my && my < y + height;
    }

    public Vector2 getMousePosition() {
        return getPosition(Gdx.input.getX(), Gdx.input.getY());
    }

    public Vector2 getPosition(int screenX, int screenY) {
        Vector3 vec = camera.unproject(new Vector3(screenX, screenY, 0));
        return new Vector2(vec.x, vec.y);
    }

    public float getX(float x) {
        return camera.unproject(new Vector3(x, 0, 0)).x;
    }

    public float getY(float y) {
        return camera.unproject(new Vector3(0, y, 0)).y;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
