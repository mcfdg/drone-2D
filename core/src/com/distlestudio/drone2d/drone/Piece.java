package com.distlestudio.drone2d.drone;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.distlestudio.drone2d.Drone2D;
import com.distlestudio.drone2d.Res;
import com.distlestudio.drone2d.effect.SparkEffect;
import com.distlestudio.drone2d.utils.Funcs;

import java.util.Arrays;

public class Piece {
    Body[] bodies = new Body[3];
    Joint[] joints = new Joint[3];
    Sprite[] sprites = new Sprite[3];

    static final float ROPELENGTH = 3;
    int type;
    Vector2 lastAnchor;

    World world;

    PlayerDrone playerDrone;

    public Piece(int type, Body body, World world, PlayerDrone playerDrone, float x, float y, float x_anchor, float y_anchor) {
        this.type = type;
        this.world = world;
        this.playerDrone = playerDrone;
        createRope(body, world, x, y, x_anchor, y_anchor);
    }

    public Piece(int type, Piece piece, World world, PlayerDrone playerDrone, float x, float y) {
        this.type = type;
        this.world = world;
        this.playerDrone = playerDrone;
        createRope(piece.getLastBody(), world, x, y, piece.lastAnchor.x, piece.lastAnchor.y);
    }

    public Body getLastBody() {
        return bodies[bodies.length - 1];
    }

    public Vector2 getDotPosition() {
        return bodies[bodies.length - 1].getPosition();
    }

    void createRope(Body body, World world, float x, float y, float x_anchor, float y_anchor) {
        Body bodyA = body;
        Body bodyB;
        Vector2 anchorA = new Vector2(x_anchor * Drone2D.MPP, y_anchor * Drone2D.MPP);
        Vector2 anchorB = new Vector2();

        for (int i = 0; i < bodies.length - 1; i++) {
            bodyB = world.createBody(Res.bodyDef_rope);
            bodyB.createFixture(Res.fixtureDef_rope);
            bodyB.setTransform(x * Drone2D.MPP, y * Drone2D.MPP, 0);
            anchorB.set(-ROPELENGTH / 2 * Drone2D.MPP, 0);

            bodies[i] = bodyB;
            joints[i] = createJoint(world, bodyA, bodyB, anchorA, anchorB);
            sprites[i] = new Sprite(Res.tex_rope);
            sprites[i].setPosition(x, y);

            anchorA.set(ROPELENGTH / 2 * Drone2D.MPP, 0);
            bodyA = bodyB;
        }

        bodyB = world.createBody(Res.bodyDef_ropeDot);
        bodyB.createFixture(Res.fixtureDef_ropeDot);
        bodyB.setTransform(x * Drone2D.MPP, y * Drone2D.MPP, 0);
        bodyB.setUserData(this); // Only this part of the rope gets user data
        anchorB.set(0, (Res.itemDefinitions[type].getRegion().getRegionHeight() / 2f) * Drone2D.MPP);
        lastAnchor = new Vector2(0, -(Res.itemDefinitions[type].getRegion().getRegionHeight() * .5f));

        bodies[bodies.length - 1] = bodyB;
        joints[bodies.length - 1] = createJoint(world, bodyA, bodyB, anchorA, anchorB);
        sprites[bodies.length - 1] = new Sprite(Res.itemDefinitions[type].getRegion());
        sprites[bodies.length - 1].setPosition(x, y);
    }

    Joint createJoint(World world, Body bodyA, Body bodyB, Vector2 anchorA, Vector2 anchorB) {
        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        revoluteJointDef.localAnchorA.set(anchorA);
        revoluteJointDef.localAnchorB.set(anchorB);
        revoluteJointDef.collideConnected = false;
        revoluteJointDef.bodyA = bodyA;
        revoluteJointDef.bodyB = bodyB;

        return world.createJoint(revoluteJointDef);
    }

    void update() {
        for (int i = 0; i < bodies.length; i++) {
            sprites[i].setPosition(bodies[i].getPosition().x * Drone2D.PPM - sprites[i].getOriginX(), bodies[i].getPosition().y * Drone2D.PPM - sprites[i].getOriginY());
            sprites[i].setRotation((float) Math.toDegrees(bodies[i].getAngle()));
        }
    }

    void render(SpriteBatch batch) {
        for (int i = 0; i < bodies.length; i++)
            sprites[i].draw(batch);
    }

    void dispose(World world) {
        for (int i = 0; i < bodies.length; i++)
            bodies[i] = Funcs.destroyBody(world, bodies[i]);
        Arrays.fill(joints, null);
        joints = null;
        bodies = null;
    }

    public void redeem() {
        Vector2 pos_effect = bodies[bodies.length - 1].getPosition().scl(Drone2D.PPM);
        Drone2D.game.effects.add(new SparkEffect(pos_effect.x, pos_effect.y));
        Drone2D.game.collectedItems.add(type);
    }

    public void onTouchGround() {
        playerDrone.onRopeTouched(this);
    }
}
