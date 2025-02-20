package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * @author IT小佳
 * 创建日期： 2025/2/20
 * 描述： 游戏主菜单屏幕类
 * 该类负责显示游戏的主菜单界面，处理用户在主菜单上的交互操作，如开始游戏、查看帮助、退出游戏等。
 */
public class MainMenuScreen implements Screen {
    final Drop game;

    public MainMenuScreen(final Drop game) {
        this.game = game;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();

        game.batch.draw(game.backgroundTexture, 0, 0, game.viewport.getWorldWidth(), game.viewport.getWorldHeight());
        game.font.setColor(Color.RED);
        game.font.getData().setScale(0.015f);
        game.font.draw(game.batch, "欢迎来到Drop！！！", 2.5f, 3);
        game.font.setColor(Color.WHITE);
        game.font.getData().setScale(game.viewport.getWorldHeight() / Gdx.graphics.getHeight());
        game.font.draw(game.batch, "点击任意位置开始游戏！", 2.7f, 2);
        game.font.draw(game.batch, "二次开发作者：IT小佳", 5.5f, 0.5f);

        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
