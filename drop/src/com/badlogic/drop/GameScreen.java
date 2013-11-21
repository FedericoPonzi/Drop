package com.badlogic.drop;

import java.util.Iterator;

import actors.Dropping;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen
{
	final Drop game;
	Preferences pref;
	int record;
	Texture dropImage;
	Texture bucketImage;
	Texture rockImage;
	Sound dropSound;
	Music rainMusic;
	Sound stoneCatched;
	OrthographicCamera camera;
	Rectangle bucket;
	Array<Dropping> raindrops;
	long lastDropTime;
	int dropsGathered;
	int dropsLost;
	int level;
	int displayLevel;
	int tipo;
	long spawnTime = 1000000000;

	public GameScreen(final Drop gam)
	{
		
		pref = Gdx.app.getPreferences("Drop");
		if(pref.contains("Record"))
		{
			System.out.println("E' presente.");
			System.out.println("Prima" + record);
			record = pref.getInteger("Record");
			System.out.println("dopo: " + record);
		}
			else
		{
			pref.putInteger("Record", 0);
			record = pref.getInteger("Record");
		}
		System.err.println(pref.contains("Record"));
		this.game = gam;
		level = 200;
		displayLevel = 1;
		// load the images for the droplet and the bucket, 64x64 pixels each
		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		rockImage = new Texture(Gdx.files.internal("stone.png"));
		// load the drop sound effect and the rain background "music"
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		rainMusic.setLooping(true);
		stoneCatched = Gdx.audio.newSound(Gdx.files.internal("stoneCatched.wav"));
		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		// create a Rectangle to logically represent the bucket
		bucket = new Rectangle();
		bucket.x = 800 / 2 - 64 / 2; // center the bucket horizontally
		bucket.y = 20; // bottom left corner of the bucket is 20 pixels above
		               // the bottom screen edge
		bucket.width = 64;
		bucket.height = 64;

		// create the raindrops array and spawn the first raindrop
		raindrops = new Array<Dropping>();
		spawnRaindrop();

	}

	private void spawnRaindrop()
	{
		Dropping raindrop;
		if (MathUtils.random(0, 2) == 0)
		{
			raindrop = new Dropping(1);
		}
		else
		{
			raindrop = new Dropping(0);
		}
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();

	}

	@Override
	public void render(float delta)
	{
		
		// clear the screen with a dark blue color. The
		// arguments to glClearColor are the red, green
		// blue and alpha component in the range [0,1]
		// of the color to be used to clear the screen.
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		game.batch.setProjectionMatrix(camera.combined);

		// begin a new batch and draw the bucket and
		// all drops
		game.batch.begin();
		game.font.draw(game.batch, "Drops Collected: " + dropsGathered
		        + " | Drops Lost: " + dropsLost + " | Level:" + displayLevel +" | Record:" + record,
		        0, 480);
		game.batch.draw(bucketImage, bucket.x, bucket.y);
		for (Dropping raindrop : raindrops)
		{
			if (raindrop.getTipo().equals(0))
			{
				game.batch.draw(dropImage, raindrop.getX(), raindrop.getY());
			}
			else
			{
				game.batch.draw(rockImage, raindrop.getX(), raindrop.getY());
			}
		}
		game.batch.end();

		// process user input
		if (Gdx.input.isTouched())
		{
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - 64 / 2;
		}
		if (Gdx.input.isKeyPressed(Keys.LEFT))
		    bucket.x -= 400 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
		    bucket.x += 400 * Gdx.graphics.getDeltaTime();

		// make sure the bucket stays within the screen bounds
		if (bucket.x < 0) bucket.x = 0;
		if (bucket.x > 800 - 64) bucket.x = 800 - 64;

		// check if we need to create a new raindrop
		if (TimeUtils.nanoTime() - lastDropTime > spawnTime) spawnRaindrop();

		// move the raindrops, remove any that are beneath the bottom edge of
		// the screen or that hit the bucket. In the later case we increase the
		// value our drops counter and add a sound effect.
		Iterator<Dropping> iter = raindrops.iterator();
		while (iter.hasNext())
		{
			Dropping droppingActor = iter.next();
			Rectangle raindrop = droppingActor.getRectangle();
			raindrop.y -= level * Gdx.graphics.getDeltaTime();
			if (raindrop.y + 64 < 0)
			{
				if (droppingActor.getTipo() == 0)
				{
					dropsLost++;
					stoneCatched.play();
				}
				iter.remove();
			}
			if (raindrop.overlaps(bucket))
			{
				if (droppingActor.getTipo().equals(0))
				{
					dropsGathered++;
					dropSound.play();
				}
				else
				{
					dropsLost++;
					stoneCatched.play();
				}
				iter.remove();
			}
		}
		if (dropsLost >= 3)
		{
			game.setScreen(new LoseScreen(game));
		}
		switch (dropsGathered)
		{
			case 10:
				level = 250;
				spawnTime -= 500;
				displayLevel = 2;
				break;
			case 25:
				level = 300;
				spawnTime -=  1500;
				displayLevel = 3;
				break;
			case 40:
				level = 450;
				spawnTime -= 2500;
				displayLevel = 4;
				break;
		}
		updateRecord();
	}
	private void updateRecord()
	{
		if(record < dropsGathered)
		{
			record = dropsGathered;
			pref.putInteger("Record",dropsGathered);
			pref.flush();
		}
	}
	@Override
	public void resize(int width, int height)
	{
	}

	@Override
	public void show()
	{
		// start the playback of the background music
		// when the screen is shown
		rainMusic.play();
	}

	@Override
	public void hide()
	{
		rainMusic.stop();
	}

	@Override
	public void pause()
	{
		rainMusic.stop();
	}

	@Override
	public void resume()
	{
	}

	@Override
	public void dispose()
	{
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
	}

}
