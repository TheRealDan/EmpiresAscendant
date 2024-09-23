package dev.therealdan.empiresascendant.game.entities.unit;

import com.badlogic.gdx.math.Vector2;

public abstract class Formation {

    public static Vector2 getOffset(int i) {
        int x = 0;
        int y = 0;
        int traveled = 0;
        int direction = 0;
        int distance = 1;
        boolean further = true;

        while (i > 0) {
            if (direction == 0) {
                x++;
            } else if (direction == 1) {
                y--;
            } else if (direction == 2) {
                x--;
            } else if (direction == 3) {
                y++;
            }

            traveled++;
            i--;

            if (traveled >= distance) {
                traveled = 0;
                direction = (direction + 1) % 4;
                further = !further;
                if (further) distance++;
            }
        }

        return new Vector2(x, y);
    }
}
