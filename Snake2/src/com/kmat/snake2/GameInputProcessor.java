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
				game.setLastPressedKey(Key.LEFT);
				//game.lastPressedKey = 3;
				return true;
			} else if (keycode == Input.Keys.D) {
				game.setLastPressedKey(Key.RIGHT);
				//game.lastPressedKey = 4;
				return true;
			} else if (keycode == Input.Keys.W) {
				game.setLastPressedKey(Key.UP);
				//game.lastPressedKey = 5;
				return true;
			} else if (keycode == Input.Keys.S) {
				game.setLastPressedKey(Key.DOWN);
				//game.lastPressedKey = 6;
				return true;
			} else
				return false;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (game.isInProgress()) {
			if (screenX < width / 2) {
				game.setLastPressedKey(Key.LEFTPRESS);
				//game.lastPressedKey = 1;
			} else {
				game.setLastPressedKey(Key.RIGHTPRESS);
				//game.lastPressedKey = 2;

			}
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
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
