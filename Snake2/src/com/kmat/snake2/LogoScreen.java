package com.kmat.snake2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class LogoScreen implements Screen {
	private SnakeGame snakeGame;
	private float time;
	private Texture logoTexture;
	private Image logoImage;
	private Stage stage;
	private Table table;

	public LogoScreen(SnakeGame snakeGame) {
		this.snakeGame = snakeGame;
	}

	@Override
	public void render(float delta) {
		stage.act(delta);
		time += delta;
		if (time >= 2.0f) {
			snakeGame.setScreen(snakeGame.menuScreen);
		}
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		stage = new Stage();
		table = new Table();
		stage.addActor(table);
		table.setFillParent(true);
		logoTexture = new Texture(Gdx.files.internal("data/logo.png"));
		logoImage = new Image(logoTexture);
		logoImage.setPosition(
				(Gdx.graphics.getWidth() / 2.0f)
						- (logoTexture.getWidth() / 2.0f),
				(Gdx.graphics.getHeight() / 2.0f)
						- (logoTexture.getHeight() / 2.0f));
		stage.addActor(logoImage);

	}

	@Override
	public void hide() {
		this.dispose();

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
		logoTexture.dispose();
		stage.dispose();

	}

}
