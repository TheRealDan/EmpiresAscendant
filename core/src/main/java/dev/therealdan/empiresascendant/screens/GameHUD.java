package dev.therealdan.empiresascendant.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import dev.therealdan.empiresascendant.game.Research;
import dev.therealdan.empiresascendant.game.Resources;
import dev.therealdan.empiresascendant.game.Util;
import dev.therealdan.empiresascendant.game.entities.Entity;
import dev.therealdan.empiresascendant.game.entities.ResourceNode;
import dev.therealdan.empiresascendant.game.entities.buildings.Building;
import dev.therealdan.empiresascendant.game.entities.buildings.BuildingAction;
import dev.therealdan.empiresascendant.game.entities.unit.Unit;
import dev.therealdan.empiresascendant.game.entities.unit.UnitAction;
import dev.therealdan.empiresascendant.game.entities.unit.units.Man;
import dev.therealdan.empiresascendant.main.EmpiresAscendantApp;
import dev.therealdan.empiresascendant.main.Keyboard;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameHUD extends BaseScreen {

    private GameScreen game;

    private boolean interacting;
    private Unit.Type unit;
    private Research.Type research;
    private BuildingAction buildingAction;
    private Building.Type building, toBuild;
    private float resourceWidth = 0;
    private float resourceHeight = 0;
    private float entityWidth = 0;
    private float entityHeight = 100;

    public GameHUD(EmpiresAscendantApp app, GameScreen game) {
        super(app);
        this.game = game;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (getToBuild() != null) {
            Building ghost = new Building(getToBuild(), getMousePosition());
            ghost.setConstructionProgress(0.999);
            ghost.render(app, null);
        }

        float spacing = 10;
        float owidth = resourceWidth, width;
        float height = resourceHeight;
        float x = -Gdx.graphics.getWidth() / 2f + spacing;
        float oy = -Gdx.graphics.getHeight() / 2f + spacing, y = oy;

        float resourceWidth = 0;
        float resourceHeight = 0;
        if (getGame().getInstance().getResources().total() > 0) {
            app.batch.setColor(Color.CORAL);
            app.batch.draw(app.textures.box, x, y, owidth, height);
            x += spacing;
            y += spacing;
            height = app.font.getHeight(app.batch, camera, "", 16);
            width = height;
            for (Map.Entry<Resources.Resource, Long> entry : getGame().getInstance().getResources().getStock()) {
                app.batch.setColor(entry.getKey().getColor());
                app.batch.draw(app.textures.box, x, y, width, height);
                x += width + spacing;
                y += app.font.getHeight(app.batch, camera, "", 16);
                String text = entry.getKey().getName() + " " + entry.getValue();
                app.font.draw(app.batch, camera, text, x, y, 16, Color.WHITE);
                resourceWidth = Math.max(resourceWidth, width + app.font.getWidth(app.batch, camera, text, 16) + spacing * 3f);
                x -= width + spacing;
                y += spacing;
            }
            y += app.font.getHeight(app.batch, camera, "", 16);
            app.font.draw(app.batch, camera, "Resources", x, y, 16, Color.WHITE);
            y += spacing;
            resourceHeight = Math.max(resourceHeight, y - oy);
            x += owidth;
        }
        this.resourceWidth = resourceWidth;
        this.resourceHeight = resourceHeight;

        y = oy;
        interacting = false;
        switch (getGame().getSelected().size()) {
            case 0:
                break;
            case 1:
                if (getGame().getSelected().get(0) instanceof Building) {
                    buildingHUD(Collections.singletonList((Building) getGame().getSelected().get(0)), x, y, entityWidth, entityHeight, spacing);
                    break;
                } else if (getGame().getSelected().get(0) instanceof ResourceNode) {
                    resourceNodeHUD(Collections.singletonList((ResourceNode) getGame().getSelected().get(0)), x, y, entityWidth, entityHeight, spacing);
                    break;
                } else if (getGame().getSelected().get(0) instanceof Unit) {
                    unitHUD(Collections.singletonList((Unit) getGame().getSelected().get(0)), x, y, entityWidth, entityHeight, spacing);
                    break;
                }
            default:
                if (Util.sameEntityType(getGame().getSelected())) {
                    Entity entity = getGame().getSelected().get(0);
                    if (entity instanceof Building) {
                        List<Building> buildings = (List<Building>) (List<?>) getGame().getSelected();
                        buildingHUD(buildings, x, y, entityWidth, entityHeight, spacing);
                    } else if (entity instanceof ResourceNode) {
                        List<ResourceNode> resourceNodes = (List<ResourceNode>) (List<?>) getGame().getSelected();
                        resourceNodeHUD(resourceNodes, x, y, entityWidth, entityHeight, spacing);
                    } else if (entity instanceof Unit) {
                        List<Unit> units = (List<Unit>) (List<?>) getGame().getSelected();
                        unitHUD(units, x, y, entityWidth, entityHeight, spacing);
                    }
                }
                break;
        }
    }

    private void buildingHUD(List<Building> buildings, float ox, float oy, float width, float height, float spacing) {
        Building building = buildings.get(0);
        if (containsMouse(ox, oy, width, height)) interacting = true;
        app.batch.setColor(Color.CORAL);
        app.batch.draw(app.textures.box, ox, oy, width, height);
        height -= spacing * 2f;
        width = height;
        ox += spacing;
        oy += spacing;
        app.batch.setColor(Color.WHITE);
        app.batch.draw(app.textures.box, ox, oy, width, height);
        app.batch.draw(building.getTexture(), ox, oy, width, height);
        float entityWidth = width + spacing * 2f;
        ox += width + spacing;
        float y = oy + height;
        String text = building.getType().getName() + (buildings.size() > 1 ? " (" + buildings.size() + ")" : "");
        app.font.draw(app.batch, camera, text, ox, y, 16, Color.WHITE);
        width = app.font.getWidth(app.batch, camera, text, 16);
        y -= app.font.getHeight(app.batch, camera, text, 16) + spacing;
        HashMap<String, Integer> actions = new HashMap<>();
        if (buildings.stream().anyMatch(Building::isUnderConstruction)) {
            double constructionPercentage = buildings.stream().mapToDouble(b -> b.isUnderConstruction() ? b.getConstructionProgress() : 0).sum() / buildings.stream().mapToDouble(b -> b.isUnderConstruction() ? 1 : 0).sum() * 100;
            text = Double.toString(constructionPercentage).split("\\.")[0] + "% Constructed";
            actions.put(text, actions.getOrDefault(text, 0) + 1);
        }
        if (buildings.stream().anyMatch(b -> !b.getBuildQueue().isEmpty())) {
            for (Building each : buildings) {
                if (each.getBuildQueue().isEmpty()) continue;
                BuildingAction action = each.getBuildQueue().get(0);
                text = action.isResearch() ? "Researching " + action.getResearch().getName() : "Creating " + action.getUnit().getName();
                actions.put(text, actions.getOrDefault(text, 0) + 1);
            }
        }
        for (Map.Entry<String, Integer> entry : actions.entrySet()) {
            text = entry.getKey() + (entry.getValue() > 1 ? " (" + entry.getValue() + ")" : "");
            app.font.draw(app.batch, camera, text, ox, y, 10, Color.WHITE);
            width = Math.max(width, app.font.getWidth(app.batch, camera, text, 10));
            y -= app.font.getHeight(app.batch, camera, "", 10) + spacing;
        }
        entityWidth += width + spacing;
        if (buildings.stream().allMatch(Building::isUnderConstruction)) {
            this.entityWidth = entityWidth;
            return;
        }
        this.entityWidth = entityWidth;
        entityWidth = 0;
        ox += width + spacing;
        y = oy;
        height = (height - spacing) / 2f;
        width = height;
        float x = ox;
        this.research = null;
        for (Research.Type research : building.getType().getResearch()) {
            if (getGame().getInstance().getResearch().isComplete(research)) continue;
            if (containsMouse(x, y, width, height)) this.research = research;
            app.batch.setColor(Color.WHITE);
            app.batch.draw(app.textures.box, x, y, width, height);
            app.batch.draw(research.getTexture(), x, y, width, height);
            if (!getGame().getInstance().getResources().canPurchase(research, false)) {
                app.batch.setColor(new Color(0, 0, 0, 0.5f));
                app.batch.draw(app.textures.box, x, y, width, height);
            }
            x += width + spacing;
        }
        entityWidth = Math.max(entityWidth, x - ox);
        x = ox;
        y += height + spacing;
        this.unit = null;
        for (Unit.Type unit : building.getType().getUnits()) {
            if (!getGame().getInstance().getResearch().isComplete(unit.getRequirements())) continue;
            if (containsMouse(x, y, width, height)) this.unit = unit;
            app.batch.setColor(Color.WHITE);
            app.batch.draw(app.textures.box, x, y, width, height);
            app.batch.draw(unit.getTexture(), x, y, width, height);
            if (!getGame().getInstance().getResources().canPurchase(unit, false)) {
                app.batch.setColor(new Color(0, 0, 0, 0.5f));
                app.batch.draw(app.textures.box, x, y, width, height);
            }
            x += width + spacing;
        }
        entityWidth = Math.max(entityWidth, x - ox);
        x = ox;
        y += height + spacing * 2f;
        this.buildingAction = null;
        for (BuildingAction action : building.getBuildQueue()) {
            if (containsMouse(x, y, width, height)) {
                this.interacting = true;
                this.buildingAction = action;
            }
            app.batch.setColor(action.isForever() ? Color.GOLD : Color.WHITE);
            app.batch.draw(app.textures.box, x, y, width, height);
            app.batch.draw(action.getTexture(), x, y, width, height);
            app.batch.setColor(new Color(0, 0, 0, 0.5f));
            if (building.getBuildQueue().getFirst().equals(action))
                app.batch.draw(app.textures.box, x, y, width, Math.max(0, Math.min(height, height - (height * ((float) (System.currentTimeMillis() - building.getStart()) / action.getBuildTime())))));
            x += width + spacing;
        }
        app.batch.setColor(Color.WHITE);
        this.entityWidth += entityWidth;
    }

    private void resourceNodeHUD(List<ResourceNode> resourceNodes, float x, float y, float width, float height, float spacing) {
        ResourceNode resourceNode = resourceNodes.get(0);
        long total = 0;
        for (ResourceNode each : resourceNodes)
            total += each.getResources().total();
        if (containsMouse(x, y, width, height)) interacting = true;
        app.batch.setColor(Color.CORAL);
        app.batch.draw(app.textures.box, x, y, width, height);
        height -= spacing * 2f;
        width = height;
        x += spacing;
        y += spacing;
        app.batch.setColor(Color.WHITE);
        app.batch.draw(app.textures.box, x, y, width, height);
        app.batch.draw(resourceNode.getTexture(), x, y, width, height);
        float entityWidth = width + spacing * 2f;
        x += width + spacing;
        y += height;
        String text = resourceNode.getType().getName() + (resourceNodes.size() > 1 ? "s (" + resourceNodes.size() + ")" : "");
        app.font.draw(app.batch, camera, text, x, y, 16, Color.WHITE);
        width = app.font.getWidth(app.batch, camera, text, 16);
        y -= app.font.getHeight(app.batch, camera, text, 16) + spacing;
        text = resourceNode.getType().getResource().getName() + " " + total;
        app.font.draw(app.batch, camera, text, x, y, 10, Color.WHITE);
        width = Math.max(width, app.font.getWidth(app.batch, camera, text, 10));
        this.entityWidth = entityWidth + width + spacing;
    }

    private void unitHUD(List<Unit> units, float ox, float oy, float width, float oheight, float spacing) {
        Unit unit = units.get(0);
        if (containsMouse(ox, oy, width, oheight)) interacting = true;
        app.batch.setColor(Color.CORAL);
        app.batch.draw(app.textures.box, ox, oy, width, oheight);
        oheight -= spacing * 2f;
        float height = oheight;
        width = height;
        float x = ox + spacing;
        oy += spacing;
        app.batch.setColor(Color.WHITE);
        app.batch.draw(app.textures.box, x, oy, width, height);
        app.batch.draw(unit.getTexture(), x, oy, width, height);
        app.batch.setColor(Color.WHITE);
        x += width + spacing;
        oy += height;
        String text = unit.getType().getName() + (units.size() > 1 ? "s (" + units.size() + ")" : "");
        app.font.draw(app.batch, camera, text, x, oy, 16, Color.WHITE);
        float y = oy - app.font.getHeight(app.batch, camera, text, 16) - spacing;
        width = app.font.getWidth(app.batch, camera, text, 16) + spacing;
        if (unit.getType().equals(Unit.Type.MAN)) {
            height = (height - spacing * 2f - app.font.getHeight(app.batch, camera, "", 16)) / 2f;
            List<Man> men = (List<Man>) (List<?>) units;
            boolean second = true;
            for (Resources.Resource resource : Resources.Resource.values()) {
                long total = 0;
                for (Man man : men)
                    total += man.getResources().count(resource);
                if (total == 0) continue;
                y -= height;
                app.batch.setColor(resource.getColor());
                app.batch.draw(app.textures.box, x, y, height, height);
                x += height + spacing;
                y += height - (height - app.font.getHeight(app.batch, camera, "", 10)) / 2f;
                text = resource.getName() + " " + total;
                app.font.draw(app.batch, camera, text, x, y, 10, Color.WHITE);
                x -= height + spacing;
                y -= (height - (height - app.font.getHeight(app.batch, camera, "", 10)) / 2f) + spacing;
                width = Math.max(width, height + spacing + app.font.getWidth(app.batch, camera, text, 10) + spacing);
                second = !second;
                if (second) {
                    x += width;
                    y = oy - app.font.getHeight(app.batch, camera, text, 16) - spacing;
                    width = 0;
                }
            }
            x += width;
            height = (oheight - spacing) / 2f;
            width = height;
            y = oy - height;
            second = true;
            this.building = null;
            for (Building.Type building : Building.Type.values()) {
                if (containsMouse(x, y, width, height)) this.building = building;
                app.batch.setColor(Color.WHITE);
                app.batch.draw(app.textures.box, x, y, width, height);
                app.batch.draw(building.getTexture(), x, y, width, height);
                if (!getGame().getInstance().getResources().canPurchase(building, false)) {
                    app.batch.setColor(new Color(0, 0, 0, 0.5f));
                    app.batch.draw(app.textures.box, x, y, width, height);
                }
                y -= height + spacing;
                second = !second;
                if (second) {
                    x += width + spacing;
                    y = oy - height;
                }
            }
        }
        this.entityWidth = x - ox + width + spacing;
    }

    public boolean isInteracting() {
        return interacting;
    }

    public Unit.Type getUnit() {
        return unit;
    }

    public Research.Type getResearch() {
        return research;
    }

    public BuildingAction getBuildingAction() {
        return buildingAction;
    }

    public Building.Type getBuilding() {
        return building;
    }

    public Building.Type getToBuild() {
        return toBuild;
    }

    public GameScreen getGame() {
        return game;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.ESCAPE:
                if (getToBuild() != null) {
                    this.toBuild = null;
                } else if (!getGame().getSelected().isEmpty()) {
                    getGame().getSelected().clear();
                }
                break;
        }

        return super.keyDown(keycode);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        switch (button) {
            case Input.Buttons.LEFT:
                if (getToBuild() != null) {
                    if (getGame().getInstance().getResources().canPurchase(getToBuild(), true)) {
                        Building building = getGame().getInstance().spawnBuilding(getToBuild(), getGame().getMousePosition());
                        for (Entity entity : getGame().getSelected())
                            if (entity instanceof Man)
                                ((Man) entity).addAction(new UnitAction(building, UnitAction.Type.REPAIR), Keyboard.isShiftHeld());
                    }
                    toBuild = null;
                    return true;
                } else if (getBuilding() != null) {
                    if (getGame().getInstance().getResources().canPurchase(getBuilding(), false))
                        toBuild = getBuilding();
                } else if (getGame().getSelected().size() == 1 && getGame().getSelected().get(0) instanceof Building) {
                    Building building = (Building) getGame().getSelected().get(0);
                    if (getUnit() != null) {
                        for (int i = 0; i < (Keyboard.isShiftHeld() ? 5 : 1); i++)
                            if (getGame().getInstance().getResources().canPurchase(getUnit(), true))
                                building.getBuildQueue().add(new BuildingAction(getUnit(), false));
                    } else if (getResearch() != null) {
                        if (getGame().getInstance().getResources().canPurchase(getResearch(), true))
                            building.getBuildQueue().add(new BuildingAction(getResearch()));
                    }
                }
                break;
            case Input.Buttons.RIGHT:
                if (getGame().getSelected().size() == 1 && getGame().getSelected().get(0) instanceof Building) {
                    Building building = (Building) getGame().getSelected().get(0);
                    if (getUnit() != null) {
                        building.getBuildQueue().add(new BuildingAction(getUnit(), true));
                    }
                }
                break;
        }

        return isInteracting();
    }
}
