package dev.therealdan.empiresascendant.game;

import com.badlogic.gdx.graphics.Color;
import dev.therealdan.empiresascendant.game.entities.buildings.Building;
import dev.therealdan.empiresascendant.game.entities.unit.Unit;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Resources {

    public HashMap<Resource, Long> resources = new HashMap<>();

    public void add(Resources resources) {
        for (Map.Entry<Resource, Long> entry : resources.getStock())
            add(entry.getKey(), entry.getValue());
        resources.clear();
    }

    public void add(Resource resource, long amount) {
        if (amount == 0) return;
        resources.put(resource, count(resource) + amount);
    }

    public void remove(Resource resource, long amount) {
        if (amount == 0) return;
        resources.put(resource, count(resource) - amount);
    }

    public void clear() {
        resources.clear();
    }

    public void purchase(Unit.Type unit) {
        for (Resource resource : Resource.values())
            remove(resource, unit.getCost(resource));
    }

    public void purchase(Research.Type research) {
        for (Resource resource : Resource.values())
            remove(resource, research.getCost(resource));
    }

    public void purchase(Building.Type building) {
        for (Resource resource : Resource.values())
            remove(resource, building.getCost(resource));
    }

    public boolean canDeposit(Building building) {
        for (Map.Entry<Resource, Long> resource : getStock())
            if (!building.canDeposit(resource.getKey()))
                return false;
        return true;
    }

    public boolean canPurchase(Unit.Type unit, boolean purchase) {
        for (Resource resource : Resource.values())
            if (count(resource) < unit.getCost(resource))
                return false;
        if (purchase) purchase(unit);
        return true;
    }

    public boolean canPurchase(Research.Type research, boolean purchase) {
        for (Resource resource : Resource.values())
            if (count(resource) < research.getCost(resource))
                return false;
        if (purchase) purchase(research);
        return true;
    }

    public boolean canPurchase(Building.Type building, boolean purchase) {
        for (Resource resource : Resource.values())
            if (count(resource) < building.getCost(resource))
                return false;
        if (purchase) purchase(building);
        return true;
    }

    public long total() {
        int count = 0;
        for (Map.Entry<Resource, Long> entry : getStock())
            count += entry.getValue();
        return count;
    }

    public long count(Resource resource) {
        return resources.getOrDefault(resource, 0L);
    }

    public Set<Map.Entry<Resource, Long>> getStock() {
        return resources.entrySet();
    }

    public enum Resource {
        WOOD, STONE, FOOD;

        public String getName() {
            return toString().substring(0, 1) + toString().substring(1).toLowerCase();
        }

        public Color getColor() {
            switch (this) {
                default:
                    return Color.WHITE;
                case WOOD:
                    return Color.BROWN;
                case STONE:
                    return Color.DARK_GRAY;
                case FOOD:
                    return Color.RED;
            }
        }
    }
}
