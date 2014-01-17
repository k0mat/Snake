package com.kmat.snake2;

public class LevelSelector {
	private int levelWidth;
	private int levelHeight;
	private String level;
	private byte board[][];
	
	public LevelSelector()
	{		
	}
	
	public void initLevel(String level)
	{
		this.level = level;
		if(this.level == " Standard")
		{
			standardLevel();
		}
		else if(this.level == " No walls")
		{
			nowallsLevel();
		}
		else
		{
			
		}
	}
	
	private void nowallsLevel() {
		levelWidth = 30;
		levelHeight = 20;
		board = new byte[30][20];
		for(int i = 0; i < levelWidth; i++)
		{
			for(int j = 0; j < levelHeight; j++)
			{
				if(i == 0 || i == (levelWidth - 1) || j == 0 || j == (levelHeight - 1))
					board[i][j] = 4;
				else
					board[i][j] = 0;
			}
		}
	}

	private void standardLevel() {
		levelWidth = 30;
		levelHeight = 20;
		board = new byte[30][20];
		for(int i = 0; i < levelWidth; i++)
		{
			for(int j = 0; j < levelHeight; j++)
			{
				if(i == 0 || i == (levelWidth - 1) || j == 0 || j == (levelHeight - 1))
					board[i][j] = 1;
				else
					board[i][j] = 0;
			}
		}
	}

	public int getLevelWidth() {
		return levelWidth;
	}
	public int getLevelHeight() {
		return levelHeight;
	}

	public byte[][] getBoard() {
		return board;
	}

	public String getLevel() {
		return level;
	}
	

}
