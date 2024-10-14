package dev.therealdan.empiresascendant.game;

import dev.therealdan.empiresascendant.game.entities.Entity;
import dev.therealdan.empiresascendant.game.entities.buildings.Building;
import dev.therealdan.empiresascendant.game.entities.buildings.BuildingAction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public abstract class Util {

    public static boolean hasRequirements(GameInstance instance, Research.Type research) {
        for (Building building : instance.getBuildings())
            for (BuildingAction action : building.getBuildQueue())
                if (action.isResearch() && action.getResearch().equals(research))
                    return false;

        switch (research) {
            default:
                return true;
            case FEUDAL_AGE:
                List<Building.Type> requiredBuildings = List.of(Building.Type.BARRACKS, Building.Type.MILL, Building.Type.LUMBER_CAMP, Building.Type.MINING_CAMP);
                HashSet<Building.Type> buildings = new HashSet<>();
                for (Building building : instance.getBuildings())
                    if (!building.isUnderConstruction() && requiredBuildings.contains(building.getType()))
                        buildings.add(building.getType());
                return buildings.size() >= 2;
        }
    }

    public static boolean sameEntityType(List<Entity> entities) {
        return getEntityType(entities) != null;
    }

    public static Entity.Type getEntityType(List<Entity> entities) {
        Entity.Type entityType = null;
        for (Entity entity : entities) {
            if (entityType == null) entityType = entity.getEntityType();
            if (!entity.getEntityType().equals(entityType)) {
                entityType = null;
                break;
            }
        }
        return entityType;
    }

    public static boolean sameType(List<Entity> entities) {
        return getType(entities) != null;
    }

    public static String getType(List<Entity> entities) {
        String type = null;
        for (Entity entity : entities) {
            if (type == null) type = entity.getTypeString();
            if (!entity.getTypeString().equals(type)) {
                type = null;
                break;
            }
        }
        return type;
    }

    public static Building shortestQueue(List<Building> buildings) {
        Building buildingWithShortestQueue = null;
        int shortestSize = Integer.MAX_VALUE;
        for (Building building : buildings) {
            int size = building.getBuildQueue().size();
            if (buildingWithShortestQueue == null || size < shortestSize) {
                shortestSize = size;
                buildingWithShortestQueue = building;
            }
        }
        return buildingWithShortestQueue;
    }
}
