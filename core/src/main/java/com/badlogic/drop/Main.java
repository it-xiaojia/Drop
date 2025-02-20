package com.badlogic.drop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * 实现了 {@link com.badlogic.gdx.ApplicationListener} 接口，适用于所有平台。
 * 该类代表了游戏的主要游戏循环，处理游戏的渲染、逻辑和输入。
 */
public class Main implements ApplicationListener {
    // 纹理
    Texture backgroundTexture;
    Texture bucketTexture;
    Texture dropTexture;
    // 雨滴落到桶里的声音
    Sound dropSound;
    // 游戏背景音乐
    Music music;

    // 批量绘制2D纹理（如精灵、图片等）
    SpriteBatch spriteBatch;
    // 管理游戏窗口的视口
    FitViewport viewport;

    // 精灵-水桶
    Sprite bucketSprite;

    // 手指触摸屏幕的坐标
    Vector2 touchPos;

    // 下落的雨滴
    Array<Sprite> dropSprites;

    float dropTimer;

    // 雨滴和水桶的碰撞检测
    Rectangle bucketRectangle;
    Rectangle dropRectangle;

    @Override
    public void create() {
        // 加载纹理和音频资源
        // 为游戏背景、水桶和水滴加载图片纹理
        backgroundTexture = new Texture("background.png");
        bucketTexture = new Texture("bucket.png");
        dropTexture = new Texture("drop.png");
        // 加载水滴掉落声音效果
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        // 加载游戏背景音乐
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));

        // 初始化2D纹理绘制器
        // SpriteBatch用于批量绘制2D纹理，提高渲染效率
        spriteBatch = new SpriteBatch();
        // 初始化游戏视口
        // FitViewport确保游戏世界在不同设备上正确缩放和显示
        viewport = new FitViewport(8, 5);

        // 初始化水桶精灵
        // 创建水桶Sprite并设置其大小
        bucketSprite = new Sprite(bucketTexture);
        bucketSprite.setSize(1, 1);

        // 初始化用于获取触摸位置的向量
        touchPos = new Vector2();

        // 初始化水滴精灵列表
        dropSprites = new Array<>();

        // 初始化水桶和水滴的碰撞检测矩形
        bucketRectangle = new Rectangle();
        dropRectangle = new Rectangle();

        // 循环播放游戏背景音乐
        // 设置音乐循环播放和音量
        music.setLooping(true);
        music.setVolume(.5f);
        music.play();
    }

    /**
     * 重写resize方法以更新视口尺寸
     * 当窗口或显示区域的大小发生变化时，此方法会被调用，确保视口尺寸与新的窗口尺寸保持一致
     *
     * @param width  新的宽度
     * @param height 新的高度
     */
    @Override
    public void resize(int width, int height) {
        // 更新视口尺寸，以适应新的窗口或显示区域大小
        viewport.update(width, height, true);
    }

    /**
     * 重写render方法以实现游戏的渲染过程
     * 该方法是游戏循环的核心部分，负责处理游戏的输入、逻辑处理和绘制
     */
    @Override
    public void render() {
        // 处理用户输入，以便在游戏中实现交互
        input();
        // 执行游戏逻辑，如更新游戏状态、处理碰撞检测等
        logic();
        // 绘制游戏画面到屏幕上
        draw();
    }

    /**
     * 处理输入
     * 该方法用于处理用户通过键盘和触摸屏的输入，以控制游戏中的桶精灵（bucketSprite）的移动
     * 键盘控制适用于桌面环境，使用左右箭头键来移动桶精灵
     * 触摸屏控制适用于移动设备，通过触摸屏幕来移动桶精灵
     */
    private void input() {
        // 定义移动速度
        float speed = 4f;
        // 获取自上次渲染以来的时间差，用于计算移动距离
        float delta = Gdx.graphics.getDeltaTime();

        // 处理按键输入
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            // 当右箭头键被按下时，向右移动桶精灵
            bucketSprite.translateX(speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            // 当左箭头键被按下时，向左移动桶精灵
            bucketSprite.translateX(-speed * delta);
        }

        // 处理屏幕触摸
        if (Gdx.input.isTouched()) {
            // 当屏幕被触摸时，获取触摸位置
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            // 将触摸位置转换为游戏世界坐标
            viewport.unproject(touchPos);
            // 将桶精灵的中心位置设置为触摸位置的x坐标
            bucketSprite.setCenterX(touchPos.x);
        }
    }

    /**
     * 处理游戏逻辑
     */
    private void logic() {
        // 获取游戏世界的宽度和高度
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        // 获取水桶精灵的宽度和高度
        float bucketWidth = bucketSprite.getWidth();
        float bucketHeight = bucketSprite.getHeight();

        // 确保水桶的x坐标在游戏世界的有效范围内
        bucketSprite.setX(MathUtils.clamp(bucketSprite.getX(), 0, worldWidth - bucketWidth));

        // 获取自上次渲染以来的时间差
        float delta = Gdx.graphics.getDeltaTime();
        // 更新水桶的碰撞检测区域
        bucketRectangle.set(bucketSprite.getX(), bucketSprite.getY(), bucketWidth, bucketHeight);

        // 遍历所有水滴精灵，从最后一个开始
        for (int i = dropSprites.size - 1; i >= 0; i--) {
            Sprite dropSprite = dropSprites.get(i);
            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getHeight();

            // 根据时间差移动水滴，使其下落
            dropSprite.translateY(-2f * delta);
            // 更新水滴的碰撞检测区域
            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);

            // 如果水滴移出屏幕底部，则移除该水滴
            if (dropSprite.getY() < -dropHeight) dropSprites.removeIndex(i);
                // 如果水滴与水桶碰撞，则移除该水滴并播放音效
            else if (bucketRectangle.overlaps(dropRectangle)) {
                dropSprites.removeIndex(i);
                dropSound.play();
            }
        }

        // 累加时间，用于控制水滴生成的频率
        dropTimer += delta;
        // 每秒生成一个新的水滴
        if (dropTimer > 1f) {
            dropTimer = 0;
            createDroplet();
        }
    }

    /**
     * 绘制游戏画面
     * 本方法负责清空屏幕，设置视口，开始和结束精灵批处理，
     * 以及绘制背景和游戏对象（水桶和水滴）
     */
    private void draw() {
        // 清空屏幕，设置背景色为黑色
        ScreenUtils.clear(Color.BLACK);
        // 应用视口设置，以确保游戏画面的正确显示比例
        viewport.apply();
        // 设置精灵批处理的投影矩阵，以匹配当前视口的摄像机
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        // 开始精灵批处理
        spriteBatch.begin();

        // 获取世界宽度和高度，用于计算背景图的绘制尺寸
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // 绘制背景图，覆盖整个世界区域
        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        // 绘制水桶精灵
        bucketSprite.draw(spriteBatch);

        // 遍历并绘制所有水滴精灵
        for (Sprite dropSprite : dropSprites) {
            dropSprite.draw(spriteBatch);
        }

        // 结束精灵批处理
        spriteBatch.end();
    }

    /**
     * 创建一个水滴对象
     * <p>
     * 此方法用于初始化一个水滴对象，包括设置其大小、位置和纹理
     * 水滴的宽度和高度被设置为1，表示一个基本单位的大小
     * 水滴的纹理由dropTexture提供，位置随机设置在世界的顶部
     */
    private void createDroplet() {
        // 定义水滴的宽度和高度
        float dropWidth = 1;
        float dropHeight = 1;
        // 获取世界的世界宽度和高度
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // 创建一个水滴的精灵对象，并设置其大小为水滴的宽度和高度
        Sprite dropSprite = new Sprite(dropTexture);
        dropSprite.setSize(dropWidth, dropHeight);
        // 随机设置水滴的X坐标，确保其出现在世界的右边界之内
        dropSprite.setX(MathUtils.random(0f, worldWidth - dropWidth));
        // 将水滴的Y坐标设置为世界高度，即世界的顶部
        dropSprite.setY(worldHeight);
        // 将创建的水滴精灵添加到水滴精灵集合中
        dropSprites.add(dropSprite);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        // Destroy application's resources here.
    }
}
