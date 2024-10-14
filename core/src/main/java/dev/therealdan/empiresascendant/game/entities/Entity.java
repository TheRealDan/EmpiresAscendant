package dev.therealdan.empiresascendant.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import dev.therealdan.empiresascendant.main.EmpiresAscendantApp;

public abstract class Entity {

    private Vector2 position;
    private long health;
    private Color color;
    private int team;

    public Entity(Vector2 position, long health, Color color, int team) {
        this.position = position;
        this.health = health;
        this.color = color;
        this.team = team;
    }

    public void render(EmpiresAscendantApp app, Color outline) {
        app.batch.setColor(Color.WHITE);
        app.batch.draw(getTexture(getColor(), outline), getPosition().x - getWidth() / 2f, getPosition().y - getDepth() / 2f, getWidth(), getHeight());
    }

    public void setHealth(long health) {
        this.health = health;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public boolean heightContains(Vector2 position) {
        return getPosition().x - getWidth() / 2f < position.x && position.x < getPosition().x + getWidth() / 2f
            && getPosition().y - getDepth() / 2f < position.y && position.y < getPosition().y + getHeight() - getDepth() / 2f;
    }

    public boolean depthContains(Vector2 position) {
        return getPosition().x - getWidth() / 2f < position.x && position.x < getPosition().x + getWidth() / 2f
            && getPosition().y - getDepth() / 2f < position.y && position.y < getPosition().y + getDepth() / 2f;
    }

    public boolean within(Vector2 start, Vector2 end) {
        return Math.min(start.x, end.x) < getPosition().x && getPosition().x < Math.max(start.x, end.x)
            && Math.min(start.y, end.y) < getPosition().y && getPosition().y < Math.max(start.y, end.y);
    }

    public float getWidth() {
        return 100;
    }

    public float getDepth() {
        return getWidth() / 2f;
    }

    public float getHeight() {
        return getTexture().getHeight() / (getTexture().getWidth() / getWidth());
    }

    public String getTypeString() {
        return null;
    }

    public Type getEntityType() {
        return null;
    }

    public Texture getTexture() {
        return getTexture(null, null);
    }

    public Texture getTexture(Color mask, Color outline) {
        return null;
    }

    public Vector2 getPosition() {
        return position;
    }

    public long getHealth() {
        return health;
    }

    public Color getColor() {
        return color;
    }

    public int getTeam() {
        return team;
    }

    public enum Type {
        BUILDING, RESOURCE_NODE, UNIT
    }
}
