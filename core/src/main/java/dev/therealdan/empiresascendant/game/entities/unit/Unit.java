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

    public Unit(Unit.Type type, Vector2 position, Color color, int team, Research research) {
        super(position, type.getHealth(research), color, team);
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
    public Texture getTexture(Color mask, Color outline) {
        return getType().getTexture(mask, outline);
    }

    public enum Type {
        MAN, MILITIA;

        public long getHealth(Research research) {
            switch (this) {
                default:
                    return 1;
                case MAN:
                    return 25 + (research.isComplete(Research.Type.LOOM) ? 15 : 0);
                case MILITIA:
                    return 40;
            }
        }

        public long getCost(Resources.Resource resource) {
            switch (this) {
                case MAN:
                    switch (resource) {
                        default:
                            return 0;
                        case FOOD:
                            return 50;
                    }
                case MILITIA:
                    switch (resource) {
                        default:
                            return 0;
                        case FOOD:
                            return 60;
                        case GOLD:
                            return 20;
                    }
            }
            return 0;
        }

        public long getTime() {
            switch (this) {
                default:
                    return 0;
                case MAN:
                case MILITIA:
                    return 2 * 1000;
            }
        }

        public List<Research.Type> getRequirements() {
            switch (this) {
                default:
                    return new ArrayList<>();
            }
        }

        public String getName() {
            return toString().substring(0, 1) + toString().replace("_", " ").toLowerCase().substring(1);
        }

        public Texture getTexture(Color mask, Color outline) {
            String key = toString();
            if (mask != null) key += "M" + Color.rgba8888(mask);
            if (outline != null) key += "O" + Color.rgba8888(outline);
            if (!textures.containsKey(key)) textures.put(key, Textures.update(getTexture(), Color.MAGENTA, mask, outline));
            return textures.get(key);
        }

        public Texture getTexture() {
            String key = toString();
            if (!textures.containsKey(key)) textures.put(key, new Texture(key + ".png"));
            return textures.get(key);
        }
    }
}
