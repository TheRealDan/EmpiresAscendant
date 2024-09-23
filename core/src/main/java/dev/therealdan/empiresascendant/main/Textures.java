package dev.therealdan.empiresascendant.main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;

public class Textures {

    public Texture box = new Texture("blank.png");
    public Texture selection = new Texture("selection.png");

    public static Texture applyOutline(Texture original, Color outline) {
        TextureData textureData = original.getTextureData();
        textureData.prepare();
        Pixmap texturePixmap = textureData.consumePixmap();

        Pixmap pixmap = new Pixmap(original.getWidth(), original.getHeight(), Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(texturePixmap, 0, 0);
        pixmap.setColor(outline);
        for (int x = 0; x < pixmap.getWidth(); x++) {
            for (int y = 0; y < pixmap.getHeight(); y++) {
                if (Color.rgba8888(Color.CLEAR) == pixmap.getPixel(x, y)) {
                    if (hasAdjacentSolidPixel(pixmap, x, y, outline, 4)) {
                        pixmap.drawPixel(x, y);
                    }
                }
            }
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private static boolean hasAdjacentSolidPixel(Pixmap pixmap, int x, int y, Color ignore, int size) {
        for (int xo = -size; xo <= size; xo++)
            for (int yo = -size; yo <= size; yo++)
                if (isSolid(pixmap.getPixel(x + xo, y + yo), ignore))
                    return true;
        return false;
    }

    private static boolean isSolid(int c, Color ignore) {
        return c != Color.rgba8888(ignore) && c != Color.rgba8888(Color.CLEAR);
    }
}
