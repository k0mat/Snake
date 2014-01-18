package com.kmat.snake2;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class GameInputProcessor implements InputProcessor {
	private float width;
	private float height;
	private GameScreen game;

	@Override
	public boolean keyDown(int keycode) {
		if (game.isInProgress()) {
			if (keycode == Input.Keys.A) {
				game.side = 3;
				return true;
			} else if (keycode == Input.Keys.D) {
				game.side = 4;
				return true;
			} else if (keycode == Input.Keys.W) {
				game.side = 5;
				return true;
			} else if (keycode == Input.Keys.S) {
				game.side = 6;
				return true;
			} else
				return false;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (game.isInProgress()) {
			if (screenX < width / 2) {
				game.side = 1;
			} else {
				game.side = 2;

			}
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public GameScreen getGame() {
		return game;
	}

	public void setGame(GameScreen game) {
		this.game = game;
	}

}
