package dev.therealdan.empiresascendant.game.entities.buildings;

import com.badlogic.gdx.graphics.Texture;
import dev.therealdan.empiresascendant.game.Research;
import dev.therealdan.empiresascendant.game.entities.unit.Unit;

public class BuildingAction {

    private Unit.Type unit;
    private Research.Type research;
    private boolean forever = false;

    public BuildingAction(Unit.Type unit, boolean forever) {
        this.unit = unit;
        this.forever = forever;
    }

    public BuildingAction(Research.Type research) {
        this.research = research;
    }

    public boolean isUnit() {
        return unit != null;
    }

    public boolean isResearch() {
        return research != null;
    }

    public long getBuildTime() {
        return isResearch() ? getResearch().getTime() : getUnit().getTime();
    }

    public Texture getTexture() {
        if (isUnit()) return getUnit().getTexture();
        if (isResearch()) return getResearch().getTexture();
        return null;
    }

    public Unit.Type getUnit() {
        return unit;
    }

    public Research.Type getResearch() {
        return research;
    }

    public boolean isForever() {
        return forever;
    }
}
