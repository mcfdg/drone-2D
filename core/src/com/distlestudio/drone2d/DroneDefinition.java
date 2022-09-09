package com.distlestudio.drone2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.distlestudio.drone2d.drone.Drone;
import com.distlestudio.drone2d.drone.PlayerDrone;
import com.distlestudio.drone2d.utils.Funcs;

public class DroneDefinition {

    float[] vertices_drone;
    float[] vertices_propeller_left;
    float[] vertices_propeller_right;

    FixtureDef fixtureDef;

    FixtureDef fixtureDef_leftPropeller;
    FixtureDef fixtureDef_rightPropeller;

    public Fixture fixture;
    public Fixture fixture_leftPropeller;
    public Fixture fixture_rightPropeller;

    public Vector2[] pos_propeller;

    short categoryBits = Res.MASK_DRONE;
    short maskBits;
    short additionalCategory = 0;
    short additionalMasks = 0;

    public DroneDefinition(float[] vertices_drone, float[] vertices_propeller_left, float[] vertices_propeller_right) {
        this.vertices_drone = vertices_drone;
        this.vertices_propeller_left = vertices_propeller_left;
        this.vertices_propeller_right = vertices_propeller_right;
        init(vertices_drone, vertices_propeller_left, vertices_propeller_right);
    }

    public DroneDefinition(float[] vertices_drone, float[] vertices_propeller_left, float[] vertices_propeller_right, short additionalCategory, short additionalMasks) {
        this.vertices_drone = vertices_drone;
        this.vertices_propeller_left = vertices_propeller_left;
        this.vertices_propeller_right = vertices_propeller_right;
        this.additionalMasks = additionalMasks;
        this.additionalCategory = additionalCategory;
        init(vertices_drone, vertices_propeller_left, vertices_propeller_right);
    }

    public void init(float[] vertices_drone, float[] vertices_propeller_left, float[] vertices_propeller_right) {
        maskBits = (short) (Res.MASK_ZERO | Res.MASK_ITEM | Res.MASK_DRONE | Res.MASK_ROPE | additionalMasks);

        // Main body
        fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        vertices_drone = Funcs.multiplyVertices(vertices_drone, Drone2D.MPP);
        shape.set(vertices_drone);
        fixtureDef.shape = shape;
        fixtureDef.density = PlayerDrone.DENSITY;
        fixtureDef.friction = .75f;
        fixtureDef.filter.categoryBits = (short)(categoryBits | additionalCategory);
        fixtureDef.filter.maskBits = maskBits;

        // Left propeller of drone
        fixtureDef_leftPropeller = new FixtureDef();
        PolygonShape shape_dronePropellerLeft = new PolygonShape();
        vertices_propeller_left = Funcs.multiplyVertices(vertices_propeller_left, Drone2D.MPP);
        shape_dronePropellerLeft.set(vertices_propeller_left);
        fixtureDef_leftPropeller.shape = shape_dronePropellerLeft;
        fixtureDef_leftPropeller.density = PlayerDrone.DENSITY;
        fixtureDef_leftPropeller.isSensor = true;
        fixtureDef_leftPropeller.friction = .75f;
        fixtureDef_leftPropeller.filter.categoryBits = (short)(categoryBits | additionalCategory);
        fixtureDef_leftPropeller.filter.maskBits = maskBits;

        // Right propeller of drone
        fixtureDef_rightPropeller = new FixtureDef();
        PolygonShape shape_dronePropellerRight = new PolygonShape();
        vertices_propeller_right = Funcs.multiplyVertices(vertices_propeller_right, Drone2D.MPP);
        shape_dronePropellerRight.set(vertices_propeller_right);
        fixtureDef_rightPropeller.shape = shape_dronePropellerRight;
        fixtureDef_rightPropeller.density = PlayerDrone.DENSITY;
        fixtureDef_rightPropeller.isSensor = true;
        fixtureDef_rightPropeller.friction = .75f;
        fixtureDef_rightPropeller.filter.categoryBits = (short)(categoryBits | additionalCategory);
        fixtureDef_rightPropeller.filter.maskBits = maskBits;

        pos_propeller = new Vector2[2];
        pos_propeller[0] = new Vector2((vertices_propeller_left[0] + vertices_propeller_left[2]) / 2, (vertices_propeller_left[1] + vertices_propeller_left[3]) / 2);
        pos_propeller[1] = new Vector2((vertices_propeller_right[0] + vertices_propeller_right[2]) / 2, (vertices_propeller_right[1] + vertices_propeller_right[3]) / 2);
    }

    public Body create(World world, Drone drone, Vector2 pos) {
        Body body = world.createBody(Res.bodyDef_dynamic);
        fixture = body.createFixture(fixtureDef);
        fixture_leftPropeller = body.createFixture(fixtureDef_leftPropeller);
        fixture_rightPropeller = body.createFixture(fixtureDef_rightPropeller);
        body.setTransform(pos.x * Drone2D.MPP, pos.y * Drone2D.MPP, 0);
        body.setUserData(drone);

        return body;
    }
}