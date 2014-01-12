package com.kmat.snake2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;


public class MenuScreen implements Screen {
	private SnakeGame snakeGame;
	private Stage stage;
	private FreeTypeFontGenerator generator;
	private BitmapFont bitmapButtonFont;
	private BitmapFont bitmapFont40;
	private TextButtonStyle buttonStyle;
	private NinePatch ninePatch;
	private NinePatchDrawable patchDrawable;
	private Slider diffSlider;
	private Label diffLabel;

	
	public MenuScreen(SnakeGame snakeGame)
	{
		this.snakeGame = snakeGame;
		create();
	}
	
	private void create()
	{

		
		stage = new Stage();
		generator = new FreeTypeFontGenerator(Gdx.files.internal("data/RobotoCondensed-Bold.ttf"));
		bitmapButtonFont = generator.generateFont(80);
		bitmapFont40 = generator.generateFont(40);
		ninePatch = new NinePatch(new Texture(Gdx.files.internal("data/test.png")), 4, 4, 4, 5);
		patchDrawable = new NinePatchDrawable(ninePatch);
		buttonStyle = new TextButtonStyle(patchDrawable, patchDrawable, patchDrawable, bitmapButtonFont);
		
		LabelStyle labelStyle = new LabelStyle(bitmapFont40, Color.WHITE);
		
		SliderStyle sliderStyle = new SliderStyle();
		sliderStyle.knob = new Image(new Texture(Gdx.files.internal("data/knob.png"))).getDrawable();
		sliderStyle.background = new Image(new Texture(Gdx.files.internal("data/slider2.png"))).getDrawable();
		
		diffSlider = new Slider(1.0f, 20.0f, 0.5f, false, sliderStyle);
		diffSlider.setValue(7.0f);
		diffSlider.setPosition(Gdx.graphics.getWidth() / 2 - diffSlider.getWidth() / 2, Gdx.graphics.getHeight() / 2 + 100);
		
		diffSlider.addCaptureListener(new ChangeListener(){
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						diffLabel.setText("Snake speed: " + diffSlider.getValue());
						
					}
					
				});
		
		final TextButton button = new TextButton("  START  ", buttonStyle);
		button.setPosition(Gdx.graphics.getWidth() / 2 - button.getWidth() / 2, Gdx.graphics.getHeight() / 2 - button.getHeight() / 2);
		button.addListener(new ClickListener() {
			public void clicked (InputEvent event, float x, float y) {
				snakeGame.setScreen(snakeGame.gameScreen);
				snakeGame.gameScreen.reset(diffSlider.getValue(), 1.0f);
			}
		});
		
		diffLabel = new Label("Snake speed: " + diffSlider.getValue(), labelStyle);
		diffLabel.setPosition(Gdx.graphics.getWidth() / 2 - diffLabel.getWidth() / 2, Gdx.graphics.getHeight() / 2 + 120 + diffLabel.getHeight());
		
		stage.addActor(diffLabel);
		stage.addActor(button);
		stage.addActor(diffSlider);
	}
	
	@Override
	public void render(float delta) {
		stage.act(delta);
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		//stage.setViewport(width, height, true);

	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		generator.dispose();
		stage.dispose();

	}

	public BitmapFont getBitmapButtonFont() {
		return bitmapButtonFont;
	}
	
	public BitmapFont getBitmapFont40() {
		return bitmapFont40;
	}

}
