package com.distlestudio.drone2d.drone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.distlestudio.drone2d.Battery;
import com.distlestudio.drone2d.Drone2D;
import com.distlestudio.drone2d.Res;

import java.util.ArrayList;


public class PlayerDrone extends Drone {

    public int reasonFailed;
    public int score;
    public int redeemRopeUntil;

    ArrayList<Piece> pieces = new ArrayList<>();
    ArrayList<Integer> piecesToAdd = new ArrayList<>();

    static final float ANGULAR_DAMPING = 2;

    public PlayerDrone(float x, float y, World world) {
        super(x, y, world, new Battery(5, 1), Res.definition_playerDrone);
        this.world = world;
        pos = new Vector2(x, y);
        body.setAngularDamping(ANGULAR_DAMPING);

        initSprites(new Vector2(10.5f, 4f), Res.tex_playerDrone, Res.tex_playerDronePropeller);

        Res.pe_wind.start();
    }

    public PlayerDrone(float x, float y, World world, boolean isBackground) {
        super(x, y, world, new Battery(15, 1), Res.definition_playerDrone);
        this.world = world;
        pos = new Vector2(x, y);
        body.setAngularDamping(ANGULAR_DAMPING);

        if(isBackground)
            initSprites(new Vector2(10.5f, 4f), Res.tex_normalDrone_background, Res.tex_propeller_background);
    }

    public void update() {
        super.update();

        for (Piece piece : pieces)
            piece.update();

        if (pieces.size() - 1 >= redeemRopeUntil) {
            addScore(1);
            pieces.get(pieces.size() - 1).redeem();
            pieces.get(pieces.size() - 1).dispose(world);
            pieces.remove(pieces.size() - 1);
            Res.sound_success.play(1, .8f, 0);
        }

        for (Integer i: piecesToAdd)
            addPiece(i);
        piecesToAdd.clear();

        for(ParticleEmitter pe: Res.pe_wind.getEmitters()) {
            pe.getAngle().setHighMin((float) Math.toDegrees(body.getAngle()) - 90 - 10);
            pe.getAngle().setHighMax((float) Math.toDegrees(body.getAngle()) - 90 + 10);
            pe.getAngle().setLowMin((float) Math.toDegrees(body.getAngle()) - 90);

            pe.getRotation().setHighMin((float) Math.toDegrees(body.getAngle()));
            pe.getRotation().setHighMax((float) Math.toDegrees(body.getAngle()));
            pe.getRotation().setHighMax((float) Math.toDegrees(body.getAngle()));

            if(propellerSpeed[0] > 0 && pe.getEmission().getHighMin() == 0)
                pe.reset();

            pe.getEmission().setHigh(propellerSpeed[0] * .25f);
            pe.setPosition(pos_propeller_absolute[0].x * Drone2D.PPM, pos_propeller_absolute[0].y * Drone2D.PPM);
        }
    }

    @Override
    public void renderShapes(ShapeRenderer sr) {
        super.renderShapes(sr);
        sr.circle(pos_propeller_absolute[0].x * Drone2D.PPM, pos_propeller_absolute[0].y * Drone2D.PPM, 2);
    }

    public boolean isThrusting(int i) {
        if (actor.getClass() == PlayerActor.class)
            return actor.isActingOnPropeller(i);
        return false;
    }

    public float getPropellerSpeedFraction() {
        return (propellerSpeed[0] + propellerSpeed[1]) / 2 / PROPELLER_SPEED_MAX;
    }

    public float getPropellerSpeedFraction(int propeller) {
        return propellerSpeed[propeller] / PROPELLER_SPEED_MAX;
    }

    public void addPiece(int type) {
        redeemRopeUntil++;
        Piece newPiece;
        if (pieces.size() == 0)
            newPiece = new Piece(type, body, world, this, pos.x - 4, pos.y - 4, 0, -2);
        else {
            newPiece = new Piece(type, pieces.get(pieces.size() - 1), world, this,
                    pieces.get(pieces.size() - 1).getDotPosition().x * Drone2D.PPM,
                    pieces.get(pieces.size() - 1).getDotPosition().y * Drone2D.PPM
            );
        }
        pieces.add(newPiece);
//        Res.sound_collect.play(1, MathUtils.random(.8f, 1.1f), 0);
        Res.sound_collect.play(.8f, 1, 0);
    }

    public void addPiece_delayed(int type) {
        piecesToAdd.add(type);
    }

    public void render(SpriteBatch batch) {
        super.render(batch);

        for (Piece piece : pieces)
            piece.render(batch);
    }

    public void onRopeTouched(Piece piece) {
        redeemRopeUntil = pieces.indexOf(piece);
    }

    public void addScore(int add) {
        score += add;
    }

    public void onLand() {
        Res.sound_land.play(1, Math.max(1.5f, body.getLinearVelocity().len()), 0);
    }

    public void dispose(World world) {
        super.dispose(world);
        for (Piece piece : pieces)
            piece.dispose(world);
        pieces.clear();
    }

    public float getVolume() {
        float volume = Math.max(0, -.5f + getPropellerSpeedFraction());

        float multiplier = 1.3f;

        if (actor.isActingOnPropeller(0) && actor.isActingOnPropeller(1))
            multiplier += .7f;

        volume *= multiplier;

        return volume;
    }

    public float getPitch() {
        float pitch = Math.max(propellerSpeed[0], propellerSpeed[1]);
        pitch /= PROPELLER_SPEED_MAX;
        pitch = .4f + pitch * .6f;
        return pitch;
    }
}
