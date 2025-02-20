package com.badlogic.drop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * @author IT小佳
 * 创建日期： 2025/2/20
 * 描述： 游戏主类，继承自LibGDX的Game类。
 * 该类作为游戏的入口点，负责游戏的初始化和资源管理。
 * 在create()方法中，可以进行游戏的初始化设置，如加载资源、创建屏幕等。
 */
public class Drop extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    FitViewport viewport;
    public Texture backgroundTexture;

    @Override
    public void create() {
        batch = new SpriteBatch();
        if (font == null) {
            loadChineseFont();
        }

        viewport = new FitViewport(8, 5);

        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        backgroundTexture = new Texture("background.png");

        this.setScreen(new MainMenuScreen(this));
    }

    /**
     * 加载中文字体
     */
    private void loadChineseFont() {
        try {
            String fontFileName = "SourceHanSansSC-Normal-2.otf";
            Gdx.app.log("Drop", "Attempting to load font from: fonts/" + fontFileName);
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/" + fontFileName));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 20; // 设置字体大小
            parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + Gdx.files.internal("fonts/chinese.txt").readString();
            font = generator.generateFont(parameter);
            generator.dispose();
            Gdx.app.log("Drop", "Font loaded successfully.");
        } catch (Exception e) {
            Gdx.app.error("Drop", "Failed to load font.", e);
            // 备用字体加载
            font = new BitmapFont();
        }
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
