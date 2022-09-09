package com.distlestudio.drone2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.distlestudio.drone2d.drone.InfinitePlatformDrone;
import com.distlestudio.drone2d.scenes.Game;
import com.distlestudio.drone2d.utils.Funcs;

public class BatteryUpgrade {
    World world;
    Body body;
    public Vector2 pos;
    public Vector2 pos_goal;
    public boolean isCollected;
    Sprite sprite;

    float progress_float;
    public int type;

    public BatteryUpgrade(InfinitePlatformDrone infinitePlatformDrone, World world) {
        pos = new Vector2(infinitePlatformDrone.pos.x, infinitePlatformDrone.pos.y + 30);
        this.world = world;
        pos_goal = new Vector2(pos);
        createBody(infinitePlatformDrone);
        type = (int) (Math.random() * Res.itemDefinitions.length);
        sprite = new Sprite(Res.tex_batteryUpgrade);
        sprite.setPosition((int) (pos.x - sprite.getWidth() / 2), (int) (pos.y - sprite.getHeight() / 2));
        progress_float = (float) (Math.random() * Math.PI * 2);
    }

    void createBody(InfinitePlatformDrone infinitePlatformDrone) {
        body = world.createBody(Res.bodyDef_dynamic);
        body.createFixture(Res.fixtureDef_batteryUpgrade);
        body.setTransform(pos.x * Drone2D.MPP, pos.y * Drone2D.MPP, 0);
        body.setUserData(this);

        WeldJointDef weldJointDef = new WeldJointDef();
        weldJointDef.bodyA = body;
        weldJointDef.bodyB = infinitePlatformDrone.body;
        weldJointDef.localAnchorA.set(0, -20 * Drone2D.MPP);
        world.createJoint(weldJointDef);
    }

    public void update() {
        pos.set(body.getPosition()).scl(Drone2D.PPM);
    }

    public void render(SpriteBatch batch) {
        progress_float = (progress_float + Gdx.graphics.getDeltaTime() * 5) % ((float) Math.PI * 2);
//        sprite.setPosition((int) (pos.x - sprite.getWidth() / 2), (int) (pos.y - sprite.getHeight() / 2 + 2 * (float) Math.cos(progress_float)));
        sprite.setPosition((int) (pos.x - sprite.getWidth() / 2) -.5f, (int) (pos.y - sprite.getHeight() / 2 + 2 * (float) Math.cos(progress_float)));
        sprite.draw(batch);
    }

    public void onCollect() {
        isCollected = true;
    }

    public void dispose() {
        body = Funcs.destroyBody(world, body);
    }
}
