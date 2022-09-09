package com.distlestudio.drone2d.effect;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.particles.emitters.Emitter;
import com.badlogic.gdx.math.Vector2;
import com.distlestudio.drone2d.Res;

public class SparkEffect extends Effect {
    public SparkEffect(float x, float y) {
        super(x,y);
        Res.pe_spark.setPosition(x,y);
        Res.pe_spark.start();
    }

    @Override
    public void update() {

    }

    @Override
    public void render(SpriteBatch batch) {

    }
}
