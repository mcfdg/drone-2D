package com.distlestudio.drone2d.body_object;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;
import com.distlestudio.drone2d.Res;
import com.distlestudio.drone2d.utils.Animation;

public class Propeller extends BodyObject {
    Animation animation;

    public Propeller(World world, float x, float y, float angle) {
        body = world.createBody(Res.bodyDef_dynamic);
        body.createFixture(Res.fixtureDef_propeller);
        body.setTransform(x, y, angle);
        sprite = new Sprite(Res.tex_propellerObject[0]);
        animation = new Animation(.5f, Res.tex_propellerObject, true);
    }

    public void update() {
        animation.update();
        sprite.setRegion(animation.getTexture());
        sprite.setSize(sprite.getRegionWidth(), sprite.getRegionHeight());
        super.update();
    }
}
