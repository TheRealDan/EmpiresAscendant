package dev.therealdan.empiresascendant.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import dev.therealdan.empiresascendant.game.GameInstance;
import dev.therealdan.empiresascendant.game.Util;
import dev.therealdan.empiresascendant.game.entities.Entity;
import dev.therealdan.empiresascendant.game.entities.ResourceNode;
import dev.therealdan.empiresascendant.game.entities.buildings.Building;
import dev.therealdan.empiresascendant.game.entities.unit.Unit;
import dev.therealdan.empiresascendant.game.entities.unit.UnitAction;
import dev.therealdan.empiresascendant.main.EmpiresAscendantApp;
import dev.therealdan.empiresascendant.main.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameScreen extends BaseScreen {

    private GameInstance instance;

    private Entity hovering;
    private List<Entity> selected = new ArrayList<>();
    private Vector2 startSelection;
    private List<Entity> selection = new ArrayList<>();
    private Vector2 startPan;

    public GameScreen(EmpiresAscendantApp app) {
        super(app);
        instance = new GameInstance();
        app.addScreen(new GameHUD(app, this));
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        getInstance().tick();

        if (startPan != null) {
            Vector2 difference = startPan.cpy().sub(getMousePosition());
            camera.position.add(difference.x, difference.y, 0);
        }

        float speed = 500 * camera.zoom;
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || (Gdx.input.isKeyPressed(Input.Keys.W) && wasdMoveCamera()))
            camera.position.add(new Vector3(0, speed * delta, 0));
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || (Gdx.input.isKeyPressed(Input.Keys.S) && wasdMoveCamera()))
            camera.position.add(new Vector3(0, -speed * delta, 0));
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || (Gdx.input.isKeyPressed(Input.Keys.A) && wasdMoveCamera()))
            camera.position.add(new Vector3(-speed * delta, 0, 0));
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || (Gdx.input.isKeyPressed(Input.Keys.D) && wasdMoveCamera()))
            camera.position.add(new Vector3(speed * delta, 0, 0));

        Entity hovering = null;
        for (Entity entity : getInstance().getEntities().stream().sorted((o1, o2) -> o1.getPosition().y == o2.getPosition().y ? 0 : o1.getPosition().y < o2.getPosition().y ? 1 : -1).collect(Collectors.toList())) {
            selection.remove(entity);
            if (startSelection != null && (entity.within(startSelection, getMousePosition()) || entity.equals(getHovering()))) selection.add(entity);
            if (entity.heightContains(getMousePosition())) hovering = entity;
            entity.render(app, getSelected().contains(entity) ? Color.WHITE : entity.equals(getHovering()) || getSelection().contains(entity) ? new Color(1, 1, 1, 0.5f) : null);
        }
        this.hovering = hovering;

        if (startSelection != null) {
            app.batch.setColor(new Color(1, 1, 1, 0.1f));
            app.batch.draw(app.textures.box, startSelection.x, startSelection.y, getMousePosition().x - startSelection.x, getMousePosition().y - startSelection.y);
        }
    }

    public boolean wasdMoveCamera() {
        return getSelected().isEmpty();
    }

    public Entity getHovering() {
        return hovering;
    }

    public List<Entity> getSelected() {
        return selected;
    }

    public List<Entity> getSelection() {
        return selection;
    }

    public GameInstance getInstance() {
        return instance;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        switch (button) {
            case Input.Buttons.LEFT:
                if (getHovering() != null) {
                    if (Keyboard.isShiftHeld()) {
                        if (getSelected().contains(getHovering())) {
                            getSelected().remove(getHovering());
                        } else {
                            getSelected().add(getHovering());
                        }
                    } else {
                        if (getSelected().contains(getHovering()) && getSelected().size() == 1) {
                            for (Entity entity : getInstance().getEntities())
                                if (!entity.equals(getHovering()) && entity.getEntityType().equals(getHovering().getEntityType()))
                                    if (entity.within(getPosition(0, 0), getPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())))
                                        getSelected().add(entity);
                        } else {
                            getSelected().clear();
                            getSelected().add(getHovering());
                        }
                    }
                } else {
                    if (!Keyboard.isShiftHeld()) getSelected().clear();
                    startSelection = getMousePosition();
                }
                break;
            case Input.Buttons.MIDDLE:
                startPan = getMousePosition();
                break;
            case Input.Buttons.RIGHT:
                if (!getSelected().isEmpty() && Util.sameEntityType(getSelected()) && getSelected().get(0) instanceof Building) {
                    List<Building> buildings = (List<Building>) (List<?>) getSelected();
                    UnitAction action;
                    if (getHovering() != null && getHovering() instanceof ResourceNode) {
                        ResourceNode resourceNode = (ResourceNode) getHovering();
                        action = new UnitAction(resourceNode);
                    } else {
                        action = new UnitAction(getMousePosition());
                    }
                    for (Building building : buildings)
                        building.setUnitAction(action.copy());
                } else {
                    if (getHovering() != null && getHovering() instanceof ResourceNode) {
                        ResourceNode resourceNode = (ResourceNode) getHovering();
                        for (Entity entity : getSelected())
                            if (entity instanceof Unit)
                                ((Unit) entity).addAction(new UnitAction(resourceNode), Keyboard.isShiftHeld());
                    } else if (getHovering() != null && getHovering() instanceof Building) {
                        Building building = (Building) getHovering();
                        for (Entity entity : getSelected())
                            if (entity instanceof Unit)
                                ((Unit) entity).addAction(new UnitAction(building, UnitAction.Type.REPAIR), Keyboard.isShiftHeld());
                    } else {
                        UnitAction.moveTo(getMousePosition(), getSelected(), Keyboard.isShiftHeld());
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        switch (button) {
            case Input.Buttons.LEFT:
                if (startSelection != null) {
                    for (Entity entity : getInstance().getEntities())
                        if (entity.within(startSelection, getMousePosition()))
                            getSelected().add(entity);
                    startSelection = null;
                }
                break;
            case Input.Buttons.MIDDLE:
                startPan = null;
                break;
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        camera.zoom = Math.max(0.5f, camera.zoom + amountY / 10f);
        return false;
    }
}
