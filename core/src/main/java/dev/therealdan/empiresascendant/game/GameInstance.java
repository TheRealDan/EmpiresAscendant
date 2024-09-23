package dev.therealdan.empiresascendant.game;

import com.badlogic.gdx.math.Vector2;
import dev.therealdan.empiresascendant.game.entities.Entity;
import dev.therealdan.empiresascendant.game.entities.ResourceNode;
import dev.therealdan.empiresascendant.game.entities.buildings.Building;
import dev.therealdan.empiresascendant.game.entities.buildings.BuildingAction;
import dev.therealdan.empiresascendant.game.entities.unit.Unit;
import dev.therealdan.empiresascendant.game.entities.unit.units.Man;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameInstance {

    private Random random = new Random();

    private HashSet<Building> buildings = new HashSet<>();
    private HashSet<ResourceNode> resourceNodes = new HashSet<>();
    private HashSet<Unit> units = new HashSet<>();
    private Resources resources = new Resources();
    private Research research = new Research();

    public GameInstance() {
        buildings.add(new Building(Building.Type.BIG_ROCK, new Vector2()));
        getBuildings().get(0).setConstructionProgress(1);
        getBuildings().get(0).getBuildQueue().add(new BuildingAction(Unit.Type.MAN, false));
        getBuildings().get(0).build(this);

        for (int i = 0; i < 100; i++) {
            for (ResourceNode.Type type : ResourceNode.Type.values()) {
                resourceNodes.add(new ResourceNode(type, new Vector2(
                    random.nextInt(2500) * (random.nextBoolean() ? 1 : -1),
                    random.nextInt(2500) * (random.nextBoolean() ? 1 : -1)
                ), random.nextInt(type.countVariations()) + 1));
            }
        }
    }

    public void tick() {
        for (Building building : buildings)
            building.buildQueue(this);

        for (ResourceNode resourceNode : getResourceNodes())
            if (resourceNode.getResources().total() <= 0)
                resourceNodes.remove(resourceNode);

        for (Unit unit : units)
            unit.action(this);
    }

    public Unit spawnUnit(Unit.Type type, Vector2 position) {
        Unit unit;
        switch (type) {
            default:
                unit = new Unit(type, position);
                break;
            case MAN:
                unit = new Man(position);
                break;
        }
        units.add(unit);
        return unit;
    }

    public Building spawnBuilding(Building.Type type, Vector2 position) {
        Building building;
        switch (type) {
            default:
                building = new Building(type, position);
                break;
        }
        buildings.add(building);
        return building;
    }

    public List<Building> getBuildings() {
        return new ArrayList<>(buildings);
    }

    public List<ResourceNode> getResourceNodes() {
        return new ArrayList<>(resourceNodes);
    }

    public List<Unit> getUnits() {
        return new ArrayList<>(units);
    }

    public List<Entity> getEntities() {
        return Stream.of(buildings, resourceNodes, units).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public Resources getResources() {
        return resources;
    }

    public Research getResearch() {
        return research;
    }
}
