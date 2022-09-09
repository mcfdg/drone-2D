package com.distlestudio.drone2d;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Paralax {
    Layer[] layers;
    int groundLevel = 0;

    public Paralax() {
        layers = new Layer[]{
                new Layer(Res.tex_buildings[2], .00f),
                new Layer(Res.tex_buildings[1], .01f),
                new Layer(Res.tex_buildings[0], .05f),
        };
    }

    public void update(Vector2 pos_cam) {
        for (Layer layer : layers)
            layer.update(pos_cam);
    }

    public void render(SpriteBatch batch, Vector2 pos_cam) {
        for (Layer layer : layers)
            layer.render(batch, pos_cam);
    }

    class Layer {
        Vector2 pos;
        TextureRegion tex;
        float factor;

        Layer(TextureRegion tex, float factor) {
            this.tex = tex;
            this.factor = factor;
            pos = new Vector2();
        }

        public void update(Vector2 pos_cam) {
            pos.set(- pos_cam.x * factor - tex.getRegionWidth()/2f, Math.min(0,- pos_cam.y * factor));
        }

        void render(SpriteBatch batch, Vector2 pos_cam) {
            batch.draw(tex, pos_cam.x + pos.x, pos_cam.y + pos.y - Drone2D.HEIGHT / 2f + groundLevel);
        }
    }
}
