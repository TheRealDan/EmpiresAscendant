package dev.therealdan.empiresascendant.game;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Research {

    private static HashMap<String, Texture> textures = new HashMap<>();

    private HashSet<Type> completed = new HashSet<>();

    public void complete(Type research) {
        completed.add(research);
    }

    public boolean isComplete(Type... research) {
        for (Type type : research)
            if (!completed.contains(type))
                return false;
        return true;
    }

    public boolean isComplete(List<Type> research) {
        for (Type type : research)
            if (!completed.contains(type))
                return false;
        return true;
    }

    public enum Type {
        SPEARMAN,
        MEDIEVAL_AGE;

        public long getCost(Resources.Resource resource) {
            switch (this) {
                case SPEARMAN:
                    switch (resource) {
                        default:
                            return 0;
                        case WOOD:
                        case FOOD:
                            return 250;
                    }
                case MEDIEVAL_AGE:
                    return 1000;
            }
            return 0;
        }

        public List<Type> getRequirements() {
            switch (this) {
                default:
                    return new ArrayList<>();
            }
        }

        public String getName() {
            return toString().substring(0, 1) + toString().replace("_", " ").toLowerCase().substring(1);
        }

        public Texture getTexture() {
            String key = toString();
            if (!textures.containsKey(key)) textures.put(key, new Texture(key + ".png"));
            return textures.get(key);
        }
    }
}
