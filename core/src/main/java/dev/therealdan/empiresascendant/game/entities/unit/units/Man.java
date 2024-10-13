package dev.therealdan.empiresascendant.game.entities.unit.units;

import com.badlogic.gdx.math.Vector2;
import dev.therealdan.empiresascendant.game.GameInstance;
import dev.therealdan.empiresascendant.game.Resources;
import dev.therealdan.empiresascendant.game.entities.ResourceNode;
import dev.therealdan.empiresascendant.game.entities.buildings.Building;
import dev.therealdan.empiresascendant.game.entities.unit.Unit;
import dev.therealdan.empiresascendant.game.entities.unit.UnitAction;

public class Man extends Unit {

    private Resources resources;
    private long start = 0;

    public Man(Vector2 position) {
        super(Type.MAN, position);
        resources = new Resources();
    }

    @Override
    public UnitAction action(GameInstance instance) {
        UnitAction action = super.action(instance);
        if (action == null) return null;

        switch (action.getType()) {
            case HARVEST:
                if (getResources().total() < getMaxCarry()) {
                    if (action.getResourceNode().getResources().total() <= 0) {
                        getActionQueue().remove(action);
                        if (getActionQueue().isEmpty()) {
                            ResourceNode resource = null;
                            float closest = Float.MAX_VALUE;
                            for (ResourceNode each : instance.getResourceNodes()) {
                                if (!each.getType().equals(action.getResourceNode().getType())) continue;
                                float distance = each.getPosition().dst(getPosition());
                                if (distance < closest) {
                                    closest = distance;
                                    resource = each;
                                }
                            }
                            if (resource != null) getActionQueue().addFirst(new UnitAction(resource));
                        }
                    } else if (action.getPosition().dst(getPosition()) > 1) {
                        moveTowards(action.getPosition());
                        start = 0;
                    } else {
                        if (start == 0) {
                            start = System.currentTimeMillis();
                        } else if (System.currentTimeMillis() - start > getHarvestSpeed()) {
                            start = 0;
                            action.getResourceNode().getResources().remove(action.getResourceNode().getType().getResource(), 1);
                            getResources().add(action.getResourceNode().getType().getResource(), 1);
                        }
                    }
                } else {
                    Building building = null;
                    float closest = Float.MAX_VALUE;
                    for (Building each : instance.getBuildings()) {
                        if (!getResources().canDeposit(each)) continue;
                        float distance = each.getPosition().dst(getPosition());
                        if (distance < closest) {
                            closest = distance;
                            building = each;
                        }
                    }
                    if (building == null) {
                        getActionQueue().remove(action);
                    } else {
                        getActionQueue().addFirst(new UnitAction(building, UnitAction.Type.DROP_OFF));
                    }
                }
                break;
            case DROP_OFF:
                if (action.getPosition().dst(getPosition()) > 1) {
                    moveTowards(action.getPosition());
                } else {
                    instance.getResources().add(getResources());
                    getActionQueue().remove(action);
                }
                break;
            case REPAIR:
                if (action.getPosition().dst(getPosition()) > 1) {
                    moveTowards(action.getPosition());
                    start = 0;
                } else if (action.getBuilding().isUnderConstruction()) {
                    if (start == 0) {
                        start = System.currentTimeMillis();
                    } else if (System.currentTimeMillis() - start > getConstructSpeed()) {
                        start = 0;
                        action.getBuilding().setConstructionProgress(action.getBuilding().getConstructionProgress() + 0.01);
                    }
                } else {
                    getActionQueue().remove(action);
                    if (getActionQueue().isEmpty()) setAction(action.getBuilding().getUnitAction());
                }
                break;
        }

        return action;
    }

    public long getMaxCarry() {
        return 10;
    }

    public long getHarvestSpeed() {
        return 1000;
    }

    public long getConstructSpeed() {
        return 100;
    }

    public Resources getResources() {
        return resources;
    }
}
