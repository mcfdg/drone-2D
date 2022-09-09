package com.distlestudio.drone2d.drone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.distlestudio.drone2d.Battery;
import com.distlestudio.drone2d.Drone2D;
import com.distlestudio.drone2d.DroneDefinition;
import com.distlestudio.drone2d.Res;
import com.distlestudio.drone2d.body_object.BodyObject;
import com.distlestudio.drone2d.body_object.Propeller;
import com.distlestudio.drone2d.utils.Funcs;

public class Drone {
    Sprite sprite;
    Sprite[] sprite_propeller;
    Sprite[] sprite_air;
    float[] progress_propeller;
    float[] progress_propellerAir;
    float[] propellerSpeed;
    public boolean[] propellerExploded;
    boolean[] doExplodePropeller;
    Fixture[] fixture_propeller;
    TextureRegion[] tex_propeller;
    public Vector2[] pos_propeller;

    public Battery battery;
    public Body body;
    public Vector2 pos;
    public Vector2 origin;

    World world;
    DroneDefinition definition;
    Actor actor;

    int actorID;
    boolean infiniteBattery;

    Vector2 contactPoint = new Vector2();

    public static final float DENSITY = 150;
    static final float THRUST_MAX = 60;
    static final float MIN_VISIBLE_ROTATION = 6;
    static final float IMPULSE_EXPLOSION = 40;
    static final float ASSISTING_PROPELLER_POWER = .9f;
    static final float C_STABILIZING_POWER = .1f;

    static final float PROPELLER_SPEED_MAX = 50;
    static final float PROPELLER_SPEED_SLOWDOWN = 50;
    static final float PROPELLER_ANGULAR_VELOCITY = 1;
    static final float PROPELLER_VELOCITY = 5;

    static final int OUT_OF_FIELD_MARGIN = 30;

    public Drone(float x, float y, World world, Battery battery, DroneDefinition definition) {
        this.world = world;
        this.battery = battery;
        this.definition = definition;
        pos = new Vector2(x, y);

        progress_propeller = new float[2];
        progress_propellerAir = new float[2];
        propellerSpeed = new float[2];
        propellerExploded = new boolean[2];
        doExplodePropeller = new boolean[2];
        fixture_propeller = new Fixture[2];

        pos_propeller = new Vector2[2];
        body = definition.create(world, this, pos);
        fixture_propeller[0] = definition.fixture_leftPropeller;
        fixture_propeller[1] = definition.fixture_rightPropeller;
        pos_propeller[0] = definition.pos_propeller[0];
        pos_propeller[1] = definition.pos_propeller[1];
    }

    public void initSprites(Vector2 origin, TextureRegion tex, TextureRegion[] tex_propeller) {
        this.origin = origin;
        this.tex_propeller = tex_propeller;
        sprite = new Sprite(tex);
        sprite.setOrigin(origin.x, origin.y);
        sprite.setPosition(pos.x - sprite.getOriginX(), pos.y - sprite.getOriginY());

        sprite_propeller = new Sprite[2];
        sprite_air = new Sprite[2];
        for (int i = 0; i < 2; i++) {
            sprite_propeller[i] = new Sprite(tex_propeller[0]);
            sprite_propeller[i].setOrigin(origin.x, origin.y);
            sprite_propeller[i].setPosition(
                    pos.x - sprite_propeller[i].getOriginX(),
                    pos.y - sprite_propeller[i].getOriginY()
            );
            sprite_propeller[i].setFlip(i == 1, false);

            sprite_air[i] = new Sprite(Res.tex_propellerAir[0]);
            if (i == 1)
                sprite_air[i].setOrigin(10.5f, 7);
            else
                sprite_air[i].setOrigin(-3.5f, 7);

            sprite_air[i].setPosition(
                    pos.x - definition.pos_propeller[i].x * Drone2D.PPM,
                    pos.y - definition.pos_propeller[i].y * Drone2D.PPM
            );
        }
    }

    public void update() {
        force_thrust[0].set(0, 0);
        force_thrust[1].set(0, 0);

        pos.set(0, 0);
        pos.mulAdd(body.getPosition(), Drone2D.PPM);

        sprite.setPosition(pos.x - sprite.getOriginX(), pos.y - sprite.getOriginY());
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));

        for (int i = 0; i < 2; i++) {
            sprite_propeller[i].setRegion(tex_propeller[(int) (progress_propeller[i] * tex_propeller.length)]);
            sprite_propeller[i].setPosition(pos.x - sprite_propeller[i].getOriginX(), pos.y - sprite_propeller[i].getOriginY());
            sprite_propeller[i].setRotation((float) Math.toDegrees(body.getAngle()));
            sprite_propeller[i].setFlip(i == 1, false);

            sprite_air[i].setRegion(Res.tex_propellerAir[(int) (progress_propellerAir[i] * Res.tex_propellerAir.length)]);
            sprite_air[i].setPosition(
                    pos.x - definition.pos_propeller[i].x * Drone2D.PPM - sprite_air[i].getWidth() / 2,
                    pos.y - definition.pos_propeller[i].y * Drone2D.PPM - sprite_air[i].getHeight() / 2
            );
            sprite_air[i].setRotation((float) Math.toDegrees(body.getAngle()));
            sprite_air[i].setFlip(i == 1, false);

            pos_propeller_absolute[i].set(body.getWorldPoint(pos_propeller[i]));
            propellerSpeed[i] = Math.max(0, propellerSpeed[i] - PROPELLER_SPEED_SLOWDOWN * Gdx.graphics.getDeltaTime());
            progress_propeller[i] = (progress_propeller[i] + propellerSpeed[i] * Gdx.graphics.getDeltaTime() * .2f) % 1;
            progress_propellerAir[i] = (progress_propellerAir[i] + propellerSpeed[i] * Gdx.graphics.getDeltaTime() * .1f) % 1;

            if (propellerSpeed[i] < 3)
                sprite_air[i].setRegion(0, 0, 0, 0);

            if (doExplodePropeller[i]) {
                explodePropeller(i);
                doExplodePropeller[i] = false;
            }
        }

        if (Math.abs(sprite.getRotation()) < MIN_VISIBLE_ROTATION) {
            sprite.setRotation(0);
            for (int i = 0; i < 2; i++)
                sprite_propeller[i].setRotation(0);
        }

        battery.update();

        if (actor != null) {
            actor.update(body);

            float thrustLeft = actor.getAction(0);
            float thrustRight = actor.getAction(1);

            if (thrustLeft != 0) thrust(0, thrustLeft);
            if (thrustRight != 0) thrust(1, thrustRight);
        }
    }

    Vector2[] pos_propeller_absolute = new Vector2[]{new Vector2(), new Vector2()};
    Vector2[] force_thrust = new Vector2[]{new Vector2(), new Vector2()};

    void thrust(int propeller, float power) {
        if (!propellerExploded[propeller] && !battery.isEmpty()) {
            force_thrust[propeller].set(0, THRUST_MAX * power);
            force_thrust[propeller].rotateRad(body.getAngle());
            animatePropeller(propeller, Math.max(0, power - .5f) * 2);
            body.applyForce(force_thrust[propeller], pos_propeller_absolute[propeller], true);
            if (!infiniteBattery) battery.drain(power * .5f);
        }
    }

    void animatePropeller(int propeller, float speed) {
        propellerSpeed[propeller] = Math.max(propellerSpeed[propeller], speed * PROPELLER_SPEED_MAX);
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);

        for (int i = 0; i < 2; i++)
            if (!propellerExploded[i])
                sprite_propeller[i].draw(batch);
    }

    public void propellerCollision(Fixture fixture, Vector2 contactPoint) {
        this.contactPoint.set(contactPoint);
        if (fixture == fixture_propeller[0])
            doExplodePropeller[0] = true;
        if (fixture == fixture_propeller[1])
            doExplodePropeller[1] = true;
    }

    public void explodePropeller(int i) {
        if (!propellerExploded[i]) {
            Vector2 impulse = new Vector2(IMPULSE_EXPLOSION * propellerSpeed[i] / PROPELLER_SPEED_MAX, 0);
            Vector2 pos_center = body.getWorldCenter();
            impulse.rotateRad((float) Math.atan2(pos_center.y - pos_propeller_absolute[i].y, pos_center.x - pos_propeller_absolute[i].x));
            body.applyLinearImpulse(impulse, pos_center, true);
            body.destroyFixture(fixture_propeller[i]);
            fixture_propeller[i] = null;
            propellerExploded[i] = true;
            playHurtSound();
            BodyObject bodyObject = new Propeller(world, pos_propeller_absolute[i].x, pos_propeller_absolute[i].y, body.getAngle());

            int sign = 1;
            if (i == 0)
                sign = -1;

            bodyObject.setVelocity(sign * PROPELLER_VELOCITY * (float) Math.cos(body.getAngle()),
                    sign * PROPELLER_VELOCITY * (float) Math.sin(body.getAngle()));

            bodyObject.body.setAngularVelocity(MathUtils.random(-PROPELLER_ANGULAR_VELOCITY, PROPELLER_ANGULAR_VELOCITY));
            Drone2D.game.bodyObjects.add(bodyObject);
        }
    }

    public void setActor(int actorID, float[] args) {
        this.actorID = actorID;

        switch (actorID) {
            case Actor.PLAYER:
                actor = new PlayerActor(this);
                break;
            case Actor.RANDOM_AI:
                actor = new RandomActor(this);
                break;
            case Actor.HOVER:
                actor = new HoverActor(this, args);
                break;
        }
    }

    public void setActor(int actorID) {
        setActor(actorID, null);
    }

    public void setInfiniteBattery(boolean infiniteBattery) {
        this.infiniteBattery = infiniteBattery;
    }

    public boolean leftPlayingField(Vector2 pos_cam) {
        return leftPlayingField(pos_cam, Drone2D.WIDTH, Drone2D.HEIGHT);
    }

    public boolean leftPlayingField(Vector2 pos_cam, int playingFieldWidth, int playingFieldHeight) {
        if (pos.y < pos_cam.y - playingFieldHeight / 2f - OUT_OF_FIELD_MARGIN)
            return true;
        if (pos.y > pos_cam.y + playingFieldHeight / 2f + OUT_OF_FIELD_MARGIN)
            return true;
        if (pos.x < pos_cam.x - playingFieldWidth / 2f - OUT_OF_FIELD_MARGIN)
            return true;
        if (pos.x > pos_cam.x + playingFieldWidth / 2f + OUT_OF_FIELD_MARGIN)
            return true;

        return false;
    }

    public boolean isInView(Vector2 pos_cam) {
        if (pos.y < pos_cam.y - Drone2D.WIDTH / 2f - OUT_OF_FIELD_MARGIN)
            return false;
        if (pos.y > pos_cam.y + Drone2D.HEIGHT / 2f + OUT_OF_FIELD_MARGIN)
            return false;
        if (pos.x < pos_cam.x - Drone2D.WIDTH / 2f - OUT_OF_FIELD_MARGIN)
            return false;
        if (pos.x > pos_cam.x + Drone2D.HEIGHT / 2f + OUT_OF_FIELD_MARGIN)
            return false;

        return true;
    }

    public void renderShapes(ShapeRenderer sr) {
        for (int i = 0; i < 2; i++) {
            if (force_thrust[i].len() != 0) {
                if (force_thrust[i].len() > .99 * THRUST_MAX)
                    sr.setColor(Color.GREEN);
                else
                    sr.setColor(Color.WHITE);

                force_thrust[i].set(force_thrust[i].x * .1f, force_thrust[i].y * .1f);
                sr.line(pos_propeller_absolute[i].x * Drone2D.PPM, pos_propeller_absolute[i].y * Drone2D.PPM,
                        pos_propeller_absolute[i].x * Drone2D.PPM + force_thrust[i].x, pos_propeller_absolute[i].y * Drone2D.PPM + force_thrust[i].y);
            }
        }
        sr.setColor(Color.WHITE);
        sr.circle(contactPoint.x * Drone2D.PPM, contactPoint.y * Drone2D.PPM, 5);

    }

    public void playHurtSound() {
        playHurtSound(1, 1);
    }

    public void setToHover(float x, float y) {
        body.setFixedRotation(true);
        body.setLinearDamping(.1f);
        setActor(Actor.HOVER, new float[]{x, y});
    }

    public void stopHover() {
        body.setFixedRotation(false);
        body.setLinearDamping(0);
    }

    public void playHurtSound(float volume, float pitch) {
//        if (isInView(Vector2.Zero))
        Res.sound_hurt.play(volume, MathUtils.random(pitch - .15f, pitch + .15f), 0);
    }

    public void dispose(World world) {
        body = Funcs.destroyBody(world, body);
    }
}
