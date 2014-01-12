package com.kmat.snake2;

import java.util.ArrayDeque;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class GameScreen implements Screen {
	public SnakeGame snakeGame;
	private GameInputProcessor gameInputProcessor;
	private InputMultiplexer inputMultiplexer;
	private FrameBuffer frameBuffer;
	private OrthographicCamera camera;
	
	private Sound scoreSound;
	
	private Texture snakeSegment;
	private Texture apple;
	private Texture wall;
	private Texture options;
	private SpriteBatch batch;
	
	private float elapsedTime;
	private float lastUpdate;
	private float speed;
	
	private boolean inProgress;
	private boolean isFocused;
	
	private int boardX;	//32
	private int boardY;	//22
	private byte board[][] = new byte[30][20];
	private Random random;
	public Direction direction;
	public int side;
	private int score;
	private float scoreMultiplier;
	
	private ArrayDeque<Vector2> snake;
	private Vector2 tempFragment;
	
	private Stage stage;
	Button optionsButton;
	Label scoreLabel;
	Label lengthLabel;
	Label multiplierLabel;
	Label highscoreLabel;
	
	private Preferences prefs;
	private int highscore;
	

	public enum Direction
	{
		NORTH,
		EAST,
		SOUTH,
		WEST
	}
	
	public GameScreen(SnakeGame snakeGame)
	{
		this.snakeGame = snakeGame;
		inProgress = false;
		isFocused = true;
		create();
		reset(7.0f, 1.0f);
	}
	
	public void reset(float speed, float countDown, float multiplier) {
		reset(speed, countDown);
		scoreMultiplier = multiplier;
		multiplierLabel.setText("MULTIPLIER: " + scoreMultiplier);
	}
	
	public void reset(float speed, float countDown) {
		elapsedTime = 0.0f;
		lastUpdate = countDown;
		this.speed = speed;
		boardX = 30;
		boardY = 20;
		fillBoard();
		placeApple();
		score = 0;
		scoreMultiplier = speed / 20.0f * 4;
		scoreLabel.setText("SCORE: " + (int)(score * scoreMultiplier));
		lengthLabel.setText("LENGTH: " + (score + 3));
		multiplierLabel.setText("MULTIPLIER: " + scoreMultiplier);
	}
	
	public void applyGracePeriod(float period)
	{
		lastUpdate += period;
	}

	private void placeApple() {
		int i,j;
		do
		{
			i = random.nextInt(boardX - 2) + 1;
			j = random.nextInt(boardY - 2) + 1;	
		}while(board[i][j] != 0);
		board[i][j] = 3;
	}

	private void fillBoard() {
		board = new byte[boardX][boardY];
		for(int i = 0; i < boardX; i++)
		{
			for(int j = 0; j < boardY; j++)
			{
				if(i == 0 || i == (boardX - 1) || j == 0 || j == (boardY - 1))
					board[i][j] = 1;
				else
					board[i][j] = 0;
			}
		}
		placeSnake();
	}

	private void placeSnake() {
		board[boardX / 2 - 1][boardY / 2] = 2;
		board[boardX / 2][boardY / 2] = 2;
		board[boardX / 2 + 1][boardY / 2] = 2;
		direction = Direction.WEST;
		side = 0;
		snake = new ArrayDeque<Vector2>();
		snake.offerFirst(new Vector2((boardX / 2 + 1), (boardY / 2)));
		snake.offerFirst(new Vector2((boardX / 2), (boardY / 2)));
		snake.offerFirst(new Vector2((boardX / 2 - 1), (boardY / 2)));
	}

	private void create()
	{	
		prefs = Gdx.app.getPreferences("Snake Preferences");
		highscore = prefs.getInteger("highscore" , 0);
		
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		gameInputProcessor = new GameInputProcessor();
		gameInputProcessor.setGame(this);
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(stage);
		inputMultiplexer.addProcessor(gameInputProcessor);
		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 1280, 720, false);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1280, 720);
		
		snakeSegment = new Texture(Gdx.files.internal("data/snake_segment.png"));
		snakeSegment.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		wall = new Texture(Gdx.files.internal("data/border.png"));
		wall.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		apple = new Texture(Gdx.files.internal("data/apple.png"));
		apple.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		options  = new Texture(Gdx.files.internal("data/ic_action_settings.png"));
		batch = new SpriteBatch();
		
		scoreSound = Gdx.audio.newSound(Gdx.files.internal("data/score.mp3"));
		
		random = new Random();
		boardX = 30;
		boardY = 20;
		
		ButtonStyle style = new ButtonStyle();
		Image image = new Image(options);
		style.up = image.getDrawable();
		
		LabelStyle labelStyle = new LabelStyle(snakeGame.menuScreen.getBitmapFont40(), Color.WHITE);
		
		

		
		highscoreLabel = new Label("HIGHSCORE: " + highscore, labelStyle);
		scoreLabel = new Label("SCORE: ", labelStyle);
		multiplierLabel = new Label("MULTIPLIER: ", labelStyle);
		lengthLabel = new Label("LENGTH: ", labelStyle);
		optionsButton = new Button(style);
		
		int tempX = Gdx.graphics.getWidth() / 8,
			tempY= Gdx.graphics.getHeight();
		
		optionsButton.setPosition(Gdx.graphics.getWidth() - 96, tempY - 96);
		
		lengthLabel.setPosition(tempX * 4, tempY - 58);
		scoreLabel.setPosition(tempX * 2 + (tempX * 2 / 3), tempY - 58);
		multiplierLabel.setPosition(tempX * 5 + tempX / 2, tempY - 58);
		highscoreLabel.setPosition(tempX * 1, tempY - 58);
		
		optionsButton.addListener(new ClickListener() {
			public void clicked (InputEvent event, float x, float y) {
				snakeGame.setScreen(snakeGame.menuScreen);
			}
		});
		
		stage.addActor(highscoreLabel);
		stage.addActor(scoreLabel);
		stage.addActor(lengthLabel);
		stage.addActor(multiplierLabel);
		stage.addActor(optionsButton);
	}
	
	@Override
	public void render(float delta) {
		delta = Math.min(delta, 0.17f);		//a bit higher than 1.0f/60.0f
		if(this.isFocused == true)
		{
			float tickTime = 1 / speed;
			elapsedTime += delta;
			while(lastUpdate < elapsedTime)
			{
				if(lastUpdate + tickTime <= elapsedTime)
				{
					if(inProgress == false)
					{
						inProgress = true;
					}
					lastUpdate += tickTime;
					proceed();
				}
				else
				{
					break;
				}
			}
			stage.act(delta);
			
			batch.setProjectionMatrix(camera.combined);
			Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			frameBuffer.begin();
			batch.begin();
			Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			int firstX = (int)((1280 / 32.0f - boardX) / 2 * 32);
			int firstY = (int)((720 / 32.0f - boardY) / 2 * 32);
			for(int i = 0; i < boardX; i++)
			{
				for(int j = 0; j < boardY; j++)
				{
					switch(board[i][j])
					{
					case 1:
						batch.draw(wall, firstX +  i * 32, firstY + (boardY - 1) * 32 - j * 32 + 25);
						break;
					case 2:
						batch.draw(snakeSegment, firstX +  i * 32, firstY + (boardY - 1)  * 32 - j * 32 + 25);
						break;
					case 3:
						batch.draw(apple, firstX + i * 32, firstY + (boardY - 1) * 32 -  j * 32 + 25);
						break;
					default:
						break;
					}
				}
			}
			batch.end();
			
			frameBuffer.end();
			
			batch.begin();
	
			batch.draw(frameBuffer.getColorBufferTexture(), 0.0f, 0.0f);
	
			batch.end();
	
			stage.draw();
		}
	}

	private void proceed() {
		if(side == 1)
		{
			if(direction == GameScreen.Direction.NORTH)
				direction = GameScreen.Direction.WEST;
			else if(direction == GameScreen.Direction.WEST)
				direction = GameScreen.Direction.SOUTH;
			else if(direction == GameScreen.Direction.SOUTH)
				direction = GameScreen.Direction.EAST;
			else if(direction == GameScreen.Direction.EAST)
				direction = GameScreen.Direction.NORTH;
		}
		else if(side == 2)
		{
			if(direction == GameScreen.Direction.NORTH)
				direction = GameScreen.Direction.EAST;
			else if(direction == GameScreen.Direction.EAST)
				direction = GameScreen.Direction.SOUTH;
			else if(direction == GameScreen.Direction.SOUTH)
				direction = GameScreen.Direction.WEST;
			else if(direction == GameScreen.Direction.WEST)
				direction = GameScreen.Direction.NORTH;
		}
		else if(side == 3)
		{
			if(direction != GameScreen.Direction.EAST)
				direction = GameScreen.Direction.WEST;
		}
		else if(side == 4)
		{
			if(direction != GameScreen.Direction.WEST)
				direction = GameScreen.Direction.EAST;
		}
		else if(side == 5)
		{
			if(direction != GameScreen.Direction.SOUTH)
				direction = GameScreen.Direction.NORTH;
		}
		else if(side == 6)
		{
			if(direction != GameScreen.Direction.NORTH)
				direction = GameScreen.Direction.SOUTH;
		}
		side = 0;
		
		int i = 0, j = 0;
		if(direction == GameScreen.Direction.NORTH)
		{
			i = 0;
			j = 1;
		}
		else if(direction == GameScreen.Direction.WEST)
		{
			i = -1;
			j = 0;
		}
		else if(direction == GameScreen.Direction.SOUTH)
		{
			i = 0;
			j = -1;
		}
		else if(direction == GameScreen.Direction.EAST)
		{
			i = 1;
			j = 0;
		}
		
		tempFragment = snake.peekFirst();
		switch(board[(int)tempFragment.x + i][(int)tempFragment.y +j])
		{
		case 0:
			tempFragment = snake.pollLast();
			board[(int)tempFragment.x][(int)tempFragment.y] = 0;
			tempFragment = snake.peekFirst();
			board[(int)tempFragment.x + i][(int)tempFragment.y + j] = 2;
			snake.offerFirst(new Vector2((int)tempFragment.x + i, (int)tempFragment.y + j));
			
			break;
		case 1:
			inProgress = false;
			reset(speed, 1.0f);
			break;
		case 2:
			inProgress = false;
			reset(speed, 1.0f);
			break;
		case 3:
			tempFragment = snake.peekFirst();
			board[(int)tempFragment.x + i][(int)tempFragment.y + j] = 2;
			snake.offerFirst(new Vector2((int)tempFragment.x + i, (int)tempFragment.y + j));
			score++;
			scoreSound.play(1.0f);
			placeApple();
			break;
		}
		if ((int)(score * scoreMultiplier) > highscore)
		{
			prefs.putInteger("highscore", (int)(score * scoreMultiplier));
			highscore = (int)(score * scoreMultiplier);
			prefs.flush();
		}
		
		highscoreLabel.setText("HIGHSCORE: " + highscore);
		scoreLabel.setText("SCORE: " + (int)(score * scoreMultiplier));
		lengthLabel.setText("LENGTH: " + (score + 3));
		multiplierLabel.setText("MULTIPLIER: " + scoreMultiplier);
	}

	@Override
	public void resize(int width, int height) {
		int tempX = width / 8;
		
		gameInputProcessor.setWidth(width);
		gameInputProcessor.setHeight(height);
		stage.setViewport(width, height, true);
		
		optionsButton.setPosition(width - 96, height - 96);
		
		lengthLabel.setPosition(tempX * 4, height - 58);
		scoreLabel.setPosition(tempX * 2 + (tempX * 2 / 3), height - 58);
		multiplierLabel.setPosition(tempX * 5 + tempX / 2, height - 58);
		highscoreLabel.setPosition(tempX * 1, height - 58);
		
		
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
		System.out.printf("paused game screen\n");
		isFocused = false;
	}

	@Override
	public void resume() {
		System.out.printf("resumed game screen\n");
		isFocused = true;
	}

	@Override
	public void dispose() {
		snakeSegment.dispose();
		apple.dispose();
		wall.dispose();
		options.dispose();
		stage.dispose();
		scoreSound.dispose();
	}

	public boolean isInProgress() {
		return inProgress;
	}

}
