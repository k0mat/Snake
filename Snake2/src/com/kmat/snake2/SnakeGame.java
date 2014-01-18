package com.kmat.snake2;

import com.badlogic.gdx.Game;

public class SnakeGame extends Game {
	public GameScreen gameScreen;
	public MenuScreen menuScreen;
	public LogoScreen logoScreen;

	@Override
	public void create() {
		logoScreen = new LogoScreen(this);
		menuScreen = new MenuScreen(this);
		gameScreen = new GameScreen(this);
		this.setScreen(logoScreen);

	}

}
