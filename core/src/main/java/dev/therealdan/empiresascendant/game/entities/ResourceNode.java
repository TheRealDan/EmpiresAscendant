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
        super(position, 100, Color.WHITE, 0);
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
    public String getTypeString() {
        return getType().toString();
    }

    @Override
    public Entity.Type getEntityType() {
        return Entity.Type.RESOURCE_NODE;
    }

    @Override
    public Texture getTexture(Color mask, Color outline) {
        return getType().getTexture(getVariation(), mask, outline);
    }

    public enum Type {
        TREE, ROCK, BERRY_BUSH, GOLD_ROCK, FARM;

        public long getResourceCount() {
            switch (this) {
                default:
                    return 1000;
                case ROCK:
                    return 5000;
                case FARM:
                    return 175;
            }
        }

        public Resources.Resource getResource() {
            switch (this) {
                case TREE:
                    return Resources.Resource.WOOD;
                case ROCK:
                    return Resources.Resource.STONE;
                case BERRY_BUSH:
                case FARM:
                    return Resources.Resource.FOOD;
                case GOLD_ROCK:
                    return Resources.Resource.GOLD;
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
                case GOLD_ROCK:
                    return 6;
                case FARM:
                    return 0;
            }
        }

        public String getName() {
            return toString().substring(0, 1) + toString().replace("_", " ").toLowerCase().substring(1);
        }

        public Texture getTexture(int variation, Color mask, Color outline) {
            String key = toString() + variation;
            if (mask != null) key += "M" + Color.rgba8888(mask);
            if (outline != null) key += "O" + Color.rgba8888(outline);
            if (!textures.containsKey(key)) textures.put(key, Textures.update(getTexture(variation), Color.MAGENTA, mask, outline));
            return textures.get(key);
        }

        public Texture getTexture(int variation) {
            String key = variation == 0 ? toString() : toString() + variation;
            if (!textures.containsKey(key)) textures.put(key, new Texture(key + ".png"));
            return textures.get(key);
        }
    }
}
