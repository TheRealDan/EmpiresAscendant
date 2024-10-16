package dev.therealdan.empiresascendant.game;

import com.badlogic.gdx.graphics.Color;
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
    private Color color = Color.BLUE;
    private int team = 1;

    public GameInstance() {
        buildings.add(new Building(Building.Type.BIG_ROCK, new Vector2(), Color.BLUE, 1, getResearch()));
        getBuildings().get(0).setConstructionProgress(1);
            getBuildings().get(0).getBuildQueue().add(new BuildingAction(Unit.Type.MAN, false));
            getBuildings().get(0).build(this);

        for (int i = 0; i < 100; i++) {
            for (ResourceNode.Type type : ResourceNode.Type.values()) {
                if (type.equals(ResourceNode.Type.FARM)) continue;
                resourceNodes.add(new ResourceNode(type, new Vector2(
                    random.nextInt(2500) * (random.nextBoolean() ? 1 : -1),
                    random.nextInt(2500) * (random.nextBoolean() ? 1 : -1)
                ), random.nextInt(type.countVariations()) + 1));
            }
        }
    }

    public void tick() {
        for (Building building : getBuildings()) {
            building.buildQueue(this);
            if (building.getType().equals(Building.Type.FARM) && !building.isUnderConstruction()) {
                buildings.remove(building);
                resourceNodes.add(new ResourceNode(ResourceNode.Type.FARM, building.getPosition(), 0));
            }
        }

        for (ResourceNode resourceNode : getResourceNodes())
            if (resourceNode.getResources().total() <= 0)
                resourceNodes.remove(resourceNode);

        for (Unit unit : units)
            unit.action(this);
    }

    public int getPopulation(Color color) {
        int pop = 0;
        for (Unit unit : units)
            if (unit.getColor().equals(color))
                pop += unit.getType().getPop();
        return pop;
    }

    public int getMaxPopulation(Color color) {
        int pop = 0;
        for (Building building : buildings)
            if (building.getColor().equals(color))
                if (List.of(Building.Type.BIG_ROCK, Building.Type.HOUSE).contains(building.getType()))
                    if (!building.isUnderConstruction())
                        pop += 5;
        return pop;
    }

    public Unit spawnUnit(Unit.Type type, Vector2 position, Color color, int team) {
        Unit unit;
        switch (type) {
            default:
                unit = new Unit(type, position, color, team, getResearch());
                break;
            case MAN:
                unit = new Man(position, color, team, getResearch());
                break;
        }
        units.add(unit);
        return unit;
    }

    public Building spawnBuilding(Building.Type type, Vector2 position, Color color, int team) {
        Building building;
        switch (type) {
            default:
                building = new Building(type, position, color, team, getResearch());
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

    public Color getColor() {
        return color;
    }

    public int getTeam() {
        return team;
    }
}
