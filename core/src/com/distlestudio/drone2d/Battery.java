package com.distlestudio.drone2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.distlestudio.drone2d.utils.ActionListener;

public class Battery {

    Vector2 pos;
    Vector2 pos_fill;
    Vector2 pos_cross;

    private float energy;
    private float power = 1;
    private final float CAPACITY;

    private final int[] SCISSOR_BOX;

    int WIDTH_BAR = 52;
    int WIDTH_BAR_TOTAL = WIDTH_BAR + 19;

    boolean doDrain;
    boolean doCharge;

    float pitch = 1;
    float chargeLimit;
    float chargePower;

    public int maxLevel = 1;
    public int level = maxLevel;

    ActionListener actionListener;

    public Battery(float capacity, float power) {
        CAPACITY = capacity;
        this.power = power;
        energy = CAPACITY;
        pos = new Vector2(-Res.tex_battery.getRegionWidth() / 2f, -Drone2D.HEIGHT / 2f + 5);
        pos_fill = new Vector2(pos.x + 3, pos.y + 3);
        pos_cross = new Vector2(pos.x + 22, pos.y + 5);

        SCISSOR_BOX = new int[]{39, 8, WIDTH_BAR, 19};
    }

    public void update() {
        if (doDrain)
            drain();
        if (doCharge)
            charge();
    }

    public void drain(float factor) {
        if (energy > 0) {
            energy = Math.max(0, energy - power * factor * Gdx.graphics.getDeltaTime());
            if (energy == 0) {
                if (level > 1) {
                    setLevel(level - 1);
                    energy = CAPACITY;
                }
                Res.sound_batteryEmpty.play(1, pitch, 0);
            }
        }
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void drain() {
        drain(1);
    }

    public float getEnergy() {
        return energy;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void charge() {
        energy = Math.min(chargeLimit, energy + chargePower * Gdx.graphics.getDeltaTime());
        if (energy == CAPACITY && level < maxLevel) {
            energy = 0;
            setLevel(level + 1);
        }
    }

    public void setZero(){
        maxLevel = 0;
        setLevel(0);
    }

    public void setEnergy(float energy){
        this.energy = energy;
    }

    public void setLevel(int level) {
        this.level = level;
        if (actionListener != null)
            actionListener.action();
    }

    public void upgrade() {
        maxLevel++;
        setLevel(maxLevel);
        energy = CAPACITY;
    }

    public void setDrain(boolean doDrain) {
        this.doDrain = doDrain;
    }

    public void stopCharge() {
        doCharge = false;
    }

    public void setCharge(float energy) {
        doCharge = true;
        chargeLimit = Math.min(CAPACITY, this.energy + energy);
        chargePower = 1;
    }

    public void setCharge(Battery battery) {
        doCharge = true;
        chargeLimit = Math.min(CAPACITY, this.energy + battery.energy);
        chargePower = battery.power;
    }

    public void reset() {
        energy = CAPACITY;
    }

    public boolean isEmpty() {
        return energy == 0;
    }

    public float getFraction() {
        return energy / CAPACITY;
    }

    public void render(SpriteBatch batch, OrthographicCamera cam, float y) {
        batch.draw(Res.tex_battery, pos.x + cam.position.x, pos.y + cam.position.y + y);
        if (isEmpty())
            batch.draw(Res.tex_batteryCross, pos_cross.x + cam.position.x + .1f, pos_cross.y + cam.position.y  + y);

        batch.end();
        Gdx.gl20.glEnable(GL20.GL_SCISSOR_TEST);
        Gdx.gl20.glScissor(SCISSOR_BOX[0], SCISSOR_BOX[1] + (int)y, SCISSOR_BOX[2], SCISSOR_BOX[3]);
        batch.begin();

        batch.draw(Res.tex_batteryFill, pos_fill.x - WIDTH_BAR_TOTAL * (1 - getFraction()) + cam.position.x, pos_fill.y + cam.position.y  + y);

        batch.end();
        Gdx.gl20.glDisable(GL20.GL_SCISSOR_TEST);
        batch.begin();
    }
}
