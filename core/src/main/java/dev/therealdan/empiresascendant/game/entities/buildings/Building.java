package dev.therealdan.empiresascendant.game.entities.buildings;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import dev.therealdan.empiresascendant.game.GameInstance;
import dev.therealdan.empiresascendant.game.Research;
import dev.therealdan.empiresascendant.game.Resources;
import dev.therealdan.empiresascendant.game.entities.Entity;
import dev.therealdan.empiresascendant.game.entities.unit.Unit;
import dev.therealdan.empiresascendant.game.entities.unit.UnitAction;
import dev.therealdan.empiresascendant.main.EmpiresAscendantApp;
import dev.therealdan.empiresascendant.main.Textures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Building extends Entity {

    private static HashMap<String, Texture> textures = new HashMap<>();

    private Type type;

    private LinkedList<BuildingAction> buildQueue = new LinkedList<>();
    private long start = 0;
    private UnitAction unitAction = null;
    private double constructionProgress = 0;

    public Building(Type type, Vector2 position) {
        super(position);
        this.type = type;
    }

    public void buildQueue(GameInstance instance) {
        if (getBuildQueue().isEmpty()) return;

        if (start == 0) {
            start = System.currentTimeMillis();
        } else if (System.currentTimeMillis() - start > getBuildQueue().get(0).getBuildTime()) {
            build(instance);
            start = 0;
        }
    }

    public void build(GameInstance instance) {
        if (getBuildQueue().isEmpty()) return;
        BuildingAction action = getBuildQueue().get(0);
        getBuildQueue().remove(action);
        if (action.isForever()) {
            getBuildQueue().addLast(action);
            if (!instance.getResources().canPurchase(action.getUnit(), true)) return;
        }

        if (action.isUnit()) {
            Unit unit = instance.spawnUnit(action.getUnit(), getPosition().cpy());
            unit.queueAction(getUnitAction().copy());
        } else if (action.isResearch()) {
            instance.getResearch().complete(action.getResearch());
        }
    }

    public void setUnitAction(UnitAction unitAction) {
        this.unitAction = unitAction;
    }

    public void setConstructionProgress(double constructionProgress) {
        this.constructionProgress = Math.min(1, constructionProgress);
    }

    @Override
    public void render(EmpiresAscendantApp app, Color outline) {
        app.batch.setColor(isUnderConstruction() ? new Color(1, 1, 1, 0.5f) : Color.WHITE);
        app.batch.draw(getTexture(outline), getPosition().x - getWidth() / 2f, getPosition().y - getDepth() / 2f, getWidth(), getHeight());
    }

    public boolean canDeposit(Resources.Resource resource) {
        switch (getType()) {
            default:
                return false;
            case BIG_ROCK:
                return true;
            case MILL:
                return resource.equals(Resources.Resource.FOOD);
            case MINING_CAMP:
                return resource.equals(Resources.Resource.STONE);
            case LUMBER_CAMP:
                return resource.equals(Resources.Resource.WOOD);
        }
    }

    public boolean isUnderConstruction() {
        return getConstructionProgress() < 1.0;
    }

    public Type getType() {
        return type;
    }

    public LinkedList<BuildingAction> getBuildQueue() {
        return buildQueue;
    }

    public long getStart() {
        return start;
    }

    public UnitAction getUnitAction() {
        return unitAction != null ? unitAction : new UnitAction(getPosition().cpy().add(0, -150));
    }

    public double getConstructionProgress() {
        return constructionProgress;
    }

    @Override
    public float getWidth() {
        switch (getType()) {
            default:
                return super.getWidth();
            case BIG_ROCK:
                return 200;
        }
    }

    @Override
    public float getHeight() {
        if (isUnderConstruction()) return (float) (getDepth() / 2f + (super.getHeight() - getDepth() / 2f) * getConstructionProgress());
        return super.getHeight();
    }

    @Override
    public String getTypeString() {
        return getType().toString();
    }

    @Override
    public Entity.Type getEntityType() {
        return Entity.Type.BUILDING;
    }

    @Override
    public Texture getTexture(Color outline) {
        return outline == null ? getType().getTexture() : getType().getTexture(outline);
    }

    public enum Type {
        BIG_ROCK,
        HOUSE, MINING_CAMP, LUMBER_CAMP, MILL, FARM;

        public List<Unit.Type> getUnits() {
            switch (this) {
                default:
                    return new ArrayList<>();
                case BIG_ROCK:
                    return List.of(new Unit.Type[]{Unit.Type.MAN, Unit.Type.SPEARMAN});
            }
        }

        public List<Research.Type> getResearch() {
            switch (this) {
                default:
                    return new ArrayList<>();
                case BIG_ROCK:
                    return List.of(new Research.Type[]{Research.Type.SPEARMAN, Research.Type.MEDIEVAL_AGE});
            }
        }

        public long getCost(Resources.Resource resource) {
            switch (this) {
                case BIG_ROCK:
                    return 1000;
                case HOUSE:
                    return resource.equals(Resources.Resource.WOOD) ? 25 : 0;
                case MINING_CAMP:
                case LUMBER_CAMP:
                case MILL:
                    return resource.equals(Resources.Resource.WOOD) ? 100 : 0;
                case FARM:
                    return resource.equals(Resources.Resource.WOOD) ? 60 : 0;
            }
            return 0;
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
