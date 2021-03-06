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
	private boolean muted;

	private Texture snakeSegment;
	private Texture apple;
	private Texture options;
	private Texture soundOn;
	private Texture soundMuted;
	private Texture levelTexture;
	private SpriteBatch batch;

	private double elapsedTime;
	private double lastUpdate;
	private double speed;

	private boolean inProgress;
	private boolean isFocused;

	private LevelSelector levelSelector;
	private int secondChance;
	private int boardWidth;
	private int boardHeight;
	private Tile[][] board;
	private Random random;
	public Direction snakeDirection;
	public Key lastPressedKey;
	private int score;
	private float scoreMultiplier;

	private ArrayDeque<Vector2> snake;
	private Vector2 tempFragment;

	private Stage stage;
	private Button optionsButton;
	private Button soundButton;
	private Label scoreLabel;
	private Label lengthLabel;
	private Label multiplierLabel;
	private Label highscoreLabel;

	private Preferences prefs;
	private int highscore;

	public GameScreen(SnakeGame snakeGame) {
		this.snakeGame = snakeGame;
		inProgress = false;
		isFocused = true;
		secondChance = 2;
		create();
	}

	public void reset(double speed, float gracePeriod, String level) {
		elapsedTime = 0.0f;
		lastUpdate = gracePeriod;
		this.speed = speed;
		secondChance = 2;
		score = 0;
		levelSelector.initLevel(level);
		boardWidth = levelSelector.getLevelWidth();
		boardHeight = levelSelector.getLevelHeight();
		board = levelSelector.getBoard();
		levelTexture = levelSelector.getLevelTexture();
		scoreMultiplier = (float) (speed / 20.0 * 4) * levelSelector.getLevelMultiplier();
		scoreLabel.setText("SCORE: " + (int) (score * scoreMultiplier));
		lengthLabel.setText("LENGTH: " + (score + 3));
		multiplierLabel.setText("MULTIPLIER: " + scoreMultiplier);
		placeApple();
		placeSnake();
	}

	public void applyGracePeriod(float period) {
		lastUpdate += period;
	}

	private void placeApple() {
		int i, j;
		do {
			i = random.nextInt(boardWidth - 2) + 1;
			j = random.nextInt(boardHeight - 2) + 1;
		} while (board[i][j] != Tile.EMPTY);
		board[i][j] = Tile.APPLE;
	}

	private void placeSnake() {
		board[boardWidth / 2 - 1][boardHeight / 2] = Tile.SNAKE;
		board[boardWidth / 2][boardHeight / 2] = Tile.SNAKE;
		board[boardWidth / 2 + 1][boardHeight / 2] = Tile.SNAKE;
		snakeDirection = Direction.WEST;
		lastPressedKey = Key.NONE;
		snake = new ArrayDeque<Vector2>();
		snake.offerFirst(new Vector2((boardWidth / 2 + 1), (boardHeight / 2)));
		snake.offerFirst(new Vector2((boardWidth / 2), (boardHeight / 2)));
		snake.offerFirst(new Vector2((boardWidth / 2 - 1), (boardHeight / 2)));
	}

	private void create() {
		prefs = Gdx.app.getPreferences("Snake Preferences");
		highscore = prefs.getInteger("highscore", 0);

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
		apple = new Texture(Gdx.files.internal("data/apple.png"));
		options = new Texture(Gdx.files.internal("data/ic_action_settings.png"));
		soundOn = new Texture(Gdx.files.internal("data/ic_action_volume_on.png"));
		soundMuted = new Texture(Gdx.files.internal("data/ic_action_volume_muted.png"));
		batch = new SpriteBatch();

		scoreSound = Gdx.audio.newSound(Gdx.files.internal("data/score.mp3"));
		muted = false;

		random = new Random();
		levelSelector = new LevelSelector();

		ButtonStyle style = new ButtonStyle();
		style.up = new Image(options).getDrawable();
		optionsButton = new Button(style);
		optionsButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				snakeGame.setScreen(snakeGame.menuScreen);
			}
		});

		ButtonStyle soundStyle = new ButtonStyle();
		soundStyle.up = new Image(soundOn).getDrawable();
		soundStyle.checked = new Image(soundMuted).getDrawable();
		soundButton = new Button(soundStyle);
		soundButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				muted = !muted;
			}
		});

		LabelStyle labelStyle = new LabelStyle(snakeGame.menuScreen.getBitmapTextFont(), Color.WHITE);

		highscoreLabel = new Label("HIGHSCORE: " + highscore, labelStyle);
		scoreLabel = new Label("SCORE: ", labelStyle);
		multiplierLabel = new Label("MULTIPLIER: ", labelStyle);
		lengthLabel = new Label("LENGTH: ", labelStyle);

		int tempX = Gdx.graphics.getWidth() / 8, tempY = Gdx.graphics.getHeight();

		optionsButton.setPosition(Gdx.graphics.getWidth() - 96, tempY - 96);
		soundButton.setPosition(optionsButton.getX(), optionsButton.getY() - 96);
		lengthLabel.setPosition(tempX * 4, tempY - lengthLabel.getHeight());
		scoreLabel.setPosition(tempX * 2 + (tempX * 2 / 3), tempY - scoreLabel.getHeight());
		multiplierLabel.setPosition(tempX * 5 + tempX / 2, tempY - multiplierLabel.getHeight());
		highscoreLabel.setPosition(tempX * 1, tempY - highscoreLabel.getHeight());

		stage.addActor(soundButton);
		stage.addActor(highscoreLabel);
		stage.addActor(scoreLabel);
		stage.addActor(lengthLabel);
		stage.addActor(multiplierLabel);
		stage.addActor(optionsButton);
	}

	@Override
	public void render(float delta) {
		delta = Math.min(delta, 0.17f); // a bit higher than 1.0f/60.0f
		if (this.isFocused == true) {
			float tickTime = (float) (1 / speed);
			elapsedTime += delta;
			while (lastUpdate < elapsedTime) {
				if (lastUpdate + tickTime <= elapsedTime) {
					if (inProgress == false) {
						inProgress = true;
					}
					if (this.secondChance < 2) {
						this.secondChance++;
					}
					lastUpdate += tickTime;
					proceed();
				} else {
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

			int firstX = (int) ((1280 / 32.0f - boardWidth) / 2 * 32);
			int firstY = (int) ((720 / 32.0f - boardHeight) / 2 * 32);
			batch.draw(levelTexture, firstX + 16, firstY + 40);
			for (int i = 0; i < boardWidth; i++) {
				for (int j = 0; j < boardHeight; j++) {
					switch (board[i][j]) {
					case WALL:
						break;
					case SNAKE:
						batch.draw(snakeSegment, firstX + i * 32, firstY + (boardHeight - 1) * 32 - j * 32 + 25);
						break;
					case APPLE:
						batch.draw(apple, firstX + i * 32, firstY + (boardHeight - 1) * 32 - j * 32 + 25);
						break;
					case WARPWALL:
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
		if (lastPressedKey == Key.LEFTPRESS) {
			if (snakeDirection == Direction.NORTH)
				snakeDirection = Direction.WEST;
			else if (snakeDirection == Direction.WEST)
				snakeDirection = Direction.SOUTH;
			else if (snakeDirection == Direction.SOUTH)
				snakeDirection = Direction.EAST;
			else if (snakeDirection == Direction.EAST)
				snakeDirection = Direction.NORTH;
		} else if (lastPressedKey == Key.RIGHTPRESS) {
			if (snakeDirection == Direction.NORTH)
				snakeDirection = Direction.EAST;
			else if (snakeDirection == Direction.EAST)
				snakeDirection = Direction.SOUTH;
			else if (snakeDirection == Direction.SOUTH)
				snakeDirection = Direction.WEST;
			else if (snakeDirection == Direction.WEST)
				snakeDirection = Direction.NORTH;
		} else if (lastPressedKey == Key.LEFT) {
			if (snakeDirection != Direction.EAST)
				snakeDirection = Direction.WEST;
		} else if (lastPressedKey == Key.RIGHT) {
			if (snakeDirection != Direction.WEST)
				snakeDirection = Direction.EAST;
		} else if (lastPressedKey == Key.UP) {
			if (snakeDirection != Direction.SOUTH)
				snakeDirection = Direction.NORTH;
		} else if (lastPressedKey == Key.DOWN) {
			if (snakeDirection != Direction.NORTH)
				snakeDirection = Direction.SOUTH;
		}
		lastPressedKey = Key.NONE;

		int i = 0, j = 0;
		if (snakeDirection == Direction.NORTH) {
			i = 0;
			j = 1;
		} else if (snakeDirection == Direction.WEST) {
			i = -1;
			j = 0;
		} else if (snakeDirection == Direction.SOUTH) {
			i = 0;
			j = -1;
		} else if (snakeDirection == Direction.EAST) {
			i = 1;
			j = 0;
		}

		tempFragment = new Vector2(snake.peekFirst().x, snake.peekFirst().y);
		collisionCheck(i, j);

		if ((int) (score * scoreMultiplier) > highscore) {
			prefs.putInteger("highscore", (int) (score * scoreMultiplier));
			highscore = (int) (score * scoreMultiplier);
			prefs.flush();
		}

		highscoreLabel.setText("HIGHSCORE: " + highscore);
		scoreLabel.setText("SCORE: " + (int) (score * scoreMultiplier));
		lengthLabel.setText("LENGTH: " + (score + 3));
		multiplierLabel.setText("MULTIPLIER: " + scoreMultiplier);
	}

	private void collisionCheck(int i, int j) {
		switch (board[(int) tempFragment.x + i][(int) tempFragment.y + j]) {
		case EMPTY:
			board[(int) tempFragment.x + i][(int) tempFragment.y + j] = Tile.SNAKE;
			snake.offerFirst(new Vector2((int) tempFragment.x + i, (int) tempFragment.y + j));
			tempFragment = snake.pollLast();
			board[(int) tempFragment.x][(int) tempFragment.y] = Tile.EMPTY;
			break;
		case WALL:

			if (this.secondChance >= 2) {
				this.secondChance = 0;
			} else {
				inProgress = false;
				reset(speed, 1.0f, levelSelector.getLevel());
			}
			break;
		case SNAKE:
			if (this.secondChance >= 2) {
				this.secondChance = 0;
			} else {
				inProgress = false;
				reset(speed, 1.0f, levelSelector.getLevel());
			}
			break;
		case APPLE:
			board[(int) tempFragment.x + i][(int) tempFragment.y + j] = Tile.SNAKE;
			snake.offerFirst(new Vector2((int) tempFragment.x + i, (int) tempFragment.y + j));
			score++;
			if (!muted) {
				scoreSound.play(1.0f);
			}
			placeApple();
			break;
		case WARPWALL:
			if (tempFragment.x + i == 0 || tempFragment.x + i == boardWidth - 1) {
				if (tempFragment.x == 1) {
					tempFragment.x = boardWidth - 1;
				} else {
					tempFragment.x = 0;
				}
				collisionCheck(i, j);
			} else if (tempFragment.y + j == 0 || tempFragment.y + j == boardHeight - 1) {
				if (tempFragment.y == 1) {
					tempFragment.y = boardHeight - 1;
				} else {
					tempFragment.y = 0;
				}
				collisionCheck(i, j);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void resize(int width, int height) {
		int tempX = width / 8;

		gameInputProcessor.setWidth(width);
		gameInputProcessor.setHeight(height);
		stage.setViewport(width, height, true);

		optionsButton.setPosition(width - 96, height - 96);
		soundButton.setPosition(optionsButton.getX(), optionsButton.getY() - 96);

		lengthLabel.setPosition(tempX * 4, height - lengthLabel.getHeight());
		scoreLabel.setPosition(tempX * 2 + (tempX * 2 / 3), height - scoreLabel.getHeight());
		multiplierLabel.setPosition(tempX * 5 + tempX / 2, height - multiplierLabel.getHeight());
		highscoreLabel.setPosition(tempX * 1, height - highscoreLabel.getHeight());

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
		isFocused = false;
	}

	@Override
	public void resume() {
		isFocused = true;
	}

	@Override
	public void dispose() {
		snakeSegment.dispose();
		apple.dispose();
		options.dispose();
		stage.dispose();
		scoreSound.dispose();
		levelSelector.dispose();
	}

	public boolean isInProgress() {
		return inProgress;
	}

	public void setLastPressedKey(Key key) {
		this.lastPressedKey = key;

	}

}
