package dev.therealdan.empiresascendant.game;

import dev.therealdan.empiresascendant.game.entities.Entity;

import java.util.List;

public abstract class Util {

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
}
