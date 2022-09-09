package com.distlestudio.drone2d.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.distlestudio.drone2d.Drone2D;
import com.distlestudio.drone2d.Res;
import com.distlestudio.drone2d.UI.UI;
import com.distlestudio.drone2d.button.PropellerButton;
import com.distlestudio.drone2d.utils.ActionListener;
import com.distlestudio.drone2d.utils.Counter;
import com.distlestudio.drone2d.utils.Funcs;

public class PlayUI extends UI {

    PropellerButton[] propellerButton;
    GlyphLayout glyphLayout_score;

    Vector2 pos_score;
    Vector2 pos_score_absolute;

    float yBattery;
    float yButtons;

    float yButtonsDst = 5 - Drone2D.HEIGHT / 2f;

    public PlayUI() {
        pos_score = new Vector2(0, (int) (Drone2D.HEIGHT / 2f) - 20);
        pos_score_absolute = new Vector2();

        glyphLayout_score = new GlyphLayout();

        propellerButton = new PropellerButton[]{
                new PropellerButton(105 - Drone2D.WIDTH / 2f, yButtonsDst),
                new PropellerButton(6 - Drone2D.WIDTH / 2f, yButtonsDst),
        };
    }

    @Override
    void update_visible() {
        yBattery = Funcs.map(0, .6f, -50, 0, progress);
        yButtons = Funcs.map(.4f, 1, yButtonsDst - 50, yButtonsDst, progress);

        for (int i = 0; i < 2; i++){
            propellerButton[i].pos.y = yButtons;
            propellerButton[i].setPropellerVisible(!Drone2D.game.playerDrone.propellerExploded[i]);
            propellerButton[i].update();
        }
        glyphLayout_score.setText(Drone2D.font, Integer.toString(Drone2D.game.playerDrone.score));
    }

    @Override
    void render_visible(SpriteBatch batch, OrthographicCamera cam) {
        batch.draw(Res.tex_buttonsBackground,
                cam.position.x + 3 - Drone2D.WIDTH / 2f,
                cam.position.y + 3 - Drone2D.HEIGHT / 2f + yBattery);
        for (int i = 0; i < 2; i++)
            propellerButton[i].render(batch, cam);
        Drone2D.game.playerDrone.battery.render(batch, cam, yBattery);

        pos_score_absolute.set(pos_score.x + cam.position.x + .5f, pos_score.y + cam.position.y);
        pos_score_absolute.set(pos_score.x + cam.position.x, pos_score.y + cam.position.y);

        Drone2D.font.draw(batch, Integer.toString(Drone2D.game.playerDrone.score),
                (float) Math.floor(pos_score_absolute.x - glyphLayout_score.width / 2),
                (float) Math.round(pos_score_absolute.y));
    }

    @Override
    public void show() {
        super.show();
    }

    public void pressPropellerButton(int i) {
        propellerButton[i].press();
    }
}
