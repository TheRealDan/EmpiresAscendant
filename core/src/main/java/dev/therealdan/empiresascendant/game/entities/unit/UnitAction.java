package dev.therealdan.empiresascendant.game.entities.unit;

import com.badlogic.gdx.math.Vector2;
import dev.therealdan.empiresascendant.game.entities.Entity;
import dev.therealdan.empiresascendant.game.entities.ResourceNode;
import dev.therealdan.empiresascendant.game.entities.buildings.Building;

import java.util.List;

public class UnitAction {

    private Type type;
    private Vector2 move;
    private ResourceNode resourceNode;
    private Building building;

    private UnitAction() {
    }

    public UnitAction(Vector2 move) {
        this.move = move;
        this.type = Type.MOVE;
    }

    public UnitAction(ResourceNode harvest) {
        this.resourceNode = harvest;
        this.type = Type.HARVEST;
    }

    public UnitAction(Building building, Type type) {
        this.building = building;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public Vector2 getPosition() {
        switch (getType()) {
            default:
                return move;
            case HARVEST:
                return getResourceNode().getPosition().cpy();
            case DROP_OFF:
            case REPAIR:
                return getBuilding().getPosition().cpy();
        }
    }

    public ResourceNode getResourceNode() {
        return resourceNode;
    }

    public Building getBuilding() {
        return building;
    }

    public UnitAction copy() {
        UnitAction action = new UnitAction();
        action.type = type;
        action.move = move;
        action.resourceNode = resourceNode;
        action.building = building;
        return action;
    }

    public enum Type {
        MOVE,
        HARVEST,
        DROP_OFF, REPAIR
    }

    public static void moveTo(Vector2 position, List<Entity> entities, boolean queue) {
        int i = 0;
        for (Entity entity : entities) {
            if (!(entity instanceof Unit)) continue;
            if (entities.size() == 3 && entities.indexOf(entity) == 2) i = 5;
            Unit unit = (Unit) entity;
            unit.addAction(new UnitAction(position.cpy().add(Formation.getOffset(i).scl(unit.getWidth()))), queue);
            i++;
        }
    }
}
