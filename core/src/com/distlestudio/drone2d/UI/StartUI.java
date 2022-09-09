package com.distlestudio.drone2d.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.distlestudio.drone2d.Drone2D;
import com.distlestudio.drone2d.Res;
import com.distlestudio.drone2d.utils.ActionListener;
import com.distlestudio.drone2d.utils.Animation;
import com.distlestudio.drone2d.utils.Counter;

public class StartUI extends UI {

    Counter counter_blink;

    boolean showPressToPlay;

    float yCoins;
    float width_coins;
    float xCoinsNumber;

    GlyphLayout glyphLayout_coins;

    Animation animation_coin;

    static final int SPACING_COIN_AND_NUMBER = 2;

    public StartUI() {
        glyphLayout_coins = new GlyphLayout();

        counter_blink = new Counter(new ActionListener() {
            @Override
            public void action() {
                showPressToPlay = !showPressToPlay;
                counter_blink.start();
            }
        }, 2f).start();

        yCoins = Drone2D.HEIGHT / 2f - 20;

        animation_coin = new Animation(.55f, Res.tex_coin, true);
    }

    @Override
    void update_visible() {
        if ((Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(-1)) && Drone2D.checkFreeTouchAction())
            Drone2D.game.start();

        counter_blink.update();
        animation_coin.update();

        glyphLayout_coins.setText(Drone2D.font, Integer.toString(Drone2D.gameData.coins));

        width_coins = glyphLayout_coins.width + SPACING_COIN_AND_NUMBER + Res.tex_coin[0].getRegionWidth();
        xCoinsNumber = -width_coins / 2 + SPACING_COIN_AND_NUMBER + Res.tex_coin[0].getRegionWidth();
    }

    @Override
    void render_visible(SpriteBatch batch, OrthographicCamera cam) {
        batch.draw(Res.tex_press_to_start, -Res.tex_press_to_start.getRegionWidth() / 2f,
                -Res.tex_press_to_start.getRegionHeight() / 2f - 10);

        batch.draw(Res.tex_coinBox, -Res.tex_coinBox.getRegionWidth() / 2, yCoins - 3 + cam.position.y);
        batch.draw(animation_coin.getTexture(), Math.round(-width_coins / 2) - .5f, yCoins + cam.position.y);

        Drone2D.font.draw(batch, Integer.toString(Drone2D.gameData.coins), xCoinsNumber, yCoins + 7 + cam.position.y);
    }
}
