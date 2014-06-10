package com.kmat.snake2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class LevelSelector {
	private int levelWidth;
	private int levelHeight;
	private String level;
	private Tile[][] board;
	private Texture standard;
	private Texture nowalls;
	private Texture levelTexture;
	private float levelMultiplier;

	public LevelSelector() {
		standard = new Texture(Gdx.files.internal("data/standard.png"));
		nowalls = new Texture(Gdx.files.internal("data/nowalls.png"));
	}

	public void initLevel(String level) {
		this.level = level;
		if (this.level == " Standard") {
			standardLevel();
		} else if (this.level == " No walls") {
			nowallsLevel();
		} else {

		}
	}

	private void nowallsLevel() {
		levelMultiplier = 0.75f;
		levelTexture = nowalls;
		levelWidth = 31;
		levelHeight = 21;
		board = new Tile[levelWidth][levelHeight];
		for (int i = 0; i < levelWidth; i++) {
			for (int j = 0; j < levelHeight; j++) {
				if (i == 0 || i == (levelWidth - 1) || j == 0
						|| j == (levelHeight - 1))
					board[i][j] = Tile.WARPWALL;
				else
					board[i][j] = Tile.EMPTY;
			}
		}
	}

	private void standardLevel() {
		levelMultiplier = 1.0f;
		levelTexture = standard;
		levelWidth = 31;
		levelHeight = 21;
		board = new Tile[levelWidth][levelHeight];
		for (int i = 0; i < levelWidth; i++) {
			for (int j = 0; j < levelHeight; j++) {
				if (i == 0 || i == (levelWidth - 1) || j == 0
						|| j == (levelHeight - 1))
					board[i][j] = Tile.WALL;
				else
					board[i][j] = Tile.EMPTY;
			}
		}
	}

	public int getLevelWidth() {
		return levelWidth;
	}

	public int getLevelHeight() {
		return levelHeight;
	}

	public Tile[][] getBoard() {
		return board;
	}

	public String getLevel() {
		return level;
	}

	public Texture getLevelTexture() {
		return levelTexture;
	}
	
	public void dispose(){
		standard.dispose();
		nowalls.dispose();
	}

	public float getLevelMultiplier() {
		return levelMultiplier;
	}

}
