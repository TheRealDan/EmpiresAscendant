package dev.therealdan.empiresascendant.game.entities.unit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import dev.therealdan.empiresascendant.game.GameInstance;
import dev.therealdan.empiresascendant.game.Research;
import dev.therealdan.empiresascendant.game.Resources;
import dev.therealdan.empiresascendant.game.entities.Entity;
import dev.therealdan.empiresascendant.main.Textures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Unit extends Entity {

    private static HashMap<String, Texture> textures = new HashMap<>();

    private Unit.Type type;

    private LinkedList<UnitAction> actionQueue = new LinkedList<>();

    public Unit(Unit.Type type, Vector2 position) {
        super(position);
        this.type = type;
    }

    public UnitAction action(GameInstance instance) {
        if (getActionQueue().isEmpty()) return null;
        UnitAction action = getActionQueue().getFirst();

        if (action.getType().equals(UnitAction.Type.MOVE)) {
            moveTowards(action.getPosition());
            if (action.getPosition().cpy().sub(getPosition()).len() < 1)
                getActionQueue().remove(action);
        }

        return action;
    }

    protected void moveTowards(Vector2 position) {
        getPosition().add(position.cpy().sub(getPosition()).nor().scl(Gdx.graphics.getDeltaTime() * getSpeed() * 10f));
    }

    public void addAction(UnitAction action, boolean queue) {
        if (queue) {
            queueAction(action);
        } else {
            setAction(action);
        }
    }

    public void setAction(UnitAction action) {
        getActionQueue().clear();
        queueAction(action);
    }

    public void queueAction(UnitAction action) {
        getActionQueue().addLast(action);
    }

    public float getSpeed() {
        return 10;
    }

    public Unit.Type getType() {
        return type;
    }

    public LinkedList<UnitAction> getActionQueue() {
        return actionQueue;
    }

    @Override
    public float getWidth() {
        return super.getWidth() / 2f;
    }

    @Override
    public String getTypeString() {
        return getType().toString();
    }

    @Override
    public Entity.Type getEntityType() {
        return Entity.Type.UNIT;
    }

    @Override
    public Texture getTexture(Color outline) {
        return outline == null ? getType().getTexture() : getType().getTexture(outline);
    }

    public enum Type {
        MAN, SPEARMAN,
        KNIGHT, SPEARMAN_KNIGHT;

        public long getCost(Resources.Resource resource) {
            switch (this) {
                case MAN:
                    switch (resource) {
                        default:
                            return 0;
                        case FOOD:
                            return 50;
                    }
                case SPEARMAN:
                    switch (resource) {
                        default:
                            return 0;
                        case WOOD:
                        case FOOD:
                            return 60;
                    }
            }
            return 0;
        }

        public List<Research.Type> getRequirements() {
            switch (this) {
                default:
                    return new ArrayList<>();
                case SPEARMAN:
                    return List.of(new Research.Type[]{Research.Type.SPEARMAN});
            }
        }

        public String getName() {
            return toString().substring(0, 1) + toString().replace("_", " ").toLowerCase().substring(1);
        }

        public Texture getTexture(Color outline) {
            String key = toString() + Color.rgba8888(outline);
            if (!textures.containsKey(key)) textures.put(key, Textures.applyOutline(getTexture(), outline));
            return textures.get(key);
        }

        public Texture getTexture() {
            String key = toString();
            if (!textures.containsKey(key)) textures.put(key, new Texture(key + ".png"));
            return textures.get(key);
        }
    }
}
