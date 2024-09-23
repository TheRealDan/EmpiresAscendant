package dev.therealdan.empiresascendant.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import dev.therealdan.empiresascendant.game.Resources;
import dev.therealdan.empiresascendant.main.Textures;

import java.util.HashMap;

public class ResourceNode extends Entity {

    private static HashMap<String, Texture> textures = new HashMap<>();

    private ResourceNode.Type type;
    private int variation;
    private Resources resources;

    public ResourceNode(ResourceNode.Type type, Vector2 position, int variation) {
        super(position);
        this.type = type;
        this.variation = variation;
        this.resources = new Resources();

        getResources().add(getType().getResource(), getType().getResourceCount());
    }

    public ResourceNode.Type getType() {
        return type;
    }

    public int getVariation() {
        return variation;
    }

    public Resources getResources() {
        return resources;
    }

    @Override
    public String getEntityType() {
        return getType().toString();
    }

    @Override
    public Texture getTexture(Color outline) {
        return outline == null ? getType().getTexture(getVariation()) : getType().getTexture(getVariation(), outline);
    }

    public enum Type {
        TREE, ROCK, BERRY_BUSH;

        public long getResourceCount() {
            switch (this) {
                default:
                    return 1000;
                case ROCK:
                    return 5000;
            }
        }

        public Resources.Resource getResource() {
            switch (this) {
                case TREE:
                    return Resources.Resource.WOOD;
                case ROCK:
                    return Resources.Resource.STONE;
                case BERRY_BUSH:
                    return Resources.Resource.FOOD;
            }
            return null;
        }

        public int countVariations() {
            switch (this) {
                default:
                    return 1;
                case TREE:
                case ROCK:
                    return 3;
                case BERRY_BUSH:
                    return 6;
            }
        }

        public String getName() {
            return toString().substring(0, 1) + toString().replace("_", " ").toLowerCase().substring(1);
        }

        public Texture getTexture(int variation, Color outline) {
            String key = toString() + variation + Color.rgba8888(outline);
            if (!textures.containsKey(key)) textures.put(key, Textures.applyOutline(getTexture(variation), outline));
            return textures.get(key);
        }

        public Texture getTexture(int variation) {
            String key = toString() + variation;
            if (!textures.containsKey(key)) textures.put(key, new Texture(key + ".png"));
            return textures.get(key);
        }
    }
}
