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

    public Building(Type type, Vector2 position, Color color, int team, Research research) {
        super(position, type.getHealth(research), color, team);
        this.type = type;
    }

    public void buildQueue(GameInstance instance) {
        if (getBuildQueue().isEmpty()) return;

        if (getCurrentAction().isUnitComplete()) {
            build(instance);
        } else if (start == 0) {
            start = System.currentTimeMillis();
        } else if (System.currentTimeMillis() - start > getCurrentAction().getBuildTime()) {
            build(instance);
            start = 0;
        }
    }

    public void build(GameInstance instance) {
        if (getBuildQueue().isEmpty()) return;
        BuildingAction action = getCurrentAction();

        if (action.isUnit() && instance.getPopulation(getColor()) + action.getUnit().getPop() > instance.getMaxPopulation(getColor())) {
            action.setUnitComplete(true);
            return;
        }

        getBuildQueue().remove(action);
        if (action.isForever()) {
            getBuildQueue().addLast(action);
            if (!instance.getResources().canPurchase(action.getUnit(), true)) return;
        }

        if (action.isUnit()) {
            Unit unit = instance.spawnUnit(action.getUnit(), getPosition().cpy(), getColor(), getTeam());
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
        app.batch.draw(getTexture(getColor(), outline), getPosition().x - getWidth() / 2f, getPosition().y - getDepth() / 2f, getWidth(), getHeight());
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
                return resource.equals(Resources.Resource.STONE) || resource.equals(Resources.Resource.GOLD);
            case LUMBER_CAMP:
                return resource.equals(Resources.Resource.WOOD);
        }
    }

    public boolean isUnderConstruction() {
        return getConstructionProgress() < 1.0;
    }

    public BuildingAction getCurrentAction() {
        if (getBuildQueue().isEmpty()) return null;
        BuildingAction action = getBuildQueue().get(0);
        if (!action.isUnitComplete()) return action;

        for (BuildingAction buildingAction : getBuildQueue()) {
            if (buildingAction.equals(action)) continue;
            if (buildingAction.isResearch()) return buildingAction;
        }
        return action;
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
    public Texture getTexture(Color mask, Color outline) {
        return getType().getTexture(mask, outline);
    }

    public enum Type {
        BIG_ROCK,
        HOUSE, MINING_CAMP, LUMBER_CAMP, MILL, FARM, BARRACKS;

        public List<Unit.Type> getUnits() {
            switch (this) {
                default:
                    return new ArrayList<>();
                case BIG_ROCK:
                    return List.of(Unit.Type.MAN);
                case BARRACKS:
                    return List.of(Unit.Type.MILITIA);
            }
        }

        public List<Research.Type> getResearch() {
            switch (this) {
                default:
                    return new ArrayList<>();
                case BIG_ROCK:
                    return List.of(Research.Type.FEUDAL_AGE, Research.Type.LOOM);
            }
        }

        public long getHealth(Research research) {
            switch (this) {
                default:
                    return 1;
                case BIG_ROCK:
                    return 2400;
                case HOUSE:
                    return 550;
                case MINING_CAMP:
                case LUMBER_CAMP:
                case MILL:
                    return 600;
                case FARM:
                    return 480;
                case BARRACKS:
                    return 1200;
            }
        }

        public long getCost(Resources.Resource resource) {
            switch (this) {
                case BIG_ROCK:
                    return resource.equals(Resources.Resource.WOOD) ? 275 : resource.equals(Resources.Resource.STONE) ? 100 : 0;
                case HOUSE:
                    return resource.equals(Resources.Resource.WOOD) ? 25 : 0;
                case MINING_CAMP:
                case LUMBER_CAMP:
                case MILL:
                    return resource.equals(Resources.Resource.WOOD) ? 100 : 0;
                case FARM:
                    return resource.equals(Resources.Resource.WOOD) ? 60 : 0;
                case BARRACKS:
                    return resource.equals(Resources.Resource.WOOD) ? 175 : 0;
            }
            return 0;
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
