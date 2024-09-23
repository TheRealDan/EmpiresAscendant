package dev.therealdan.empiresascendant.game;

import dev.therealdan.empiresascendant.game.entities.Entity;

import java.util.List;

public abstract class Util {

    public static boolean sameEntityType(List<Entity> entities) {
        return getEntityType(entities) != null;
    }

    public static String getEntityType(List<Entity> entities) {
        String entityType = "";
        for (Entity entity : entities) {
            if (entityType.isEmpty()) entityType = entity.getEntityType();
            if (!entity.getEntityType().equals(entityType)) {
                entityType = null;
                break;
            }
        }
        return entityType;
    }
}
