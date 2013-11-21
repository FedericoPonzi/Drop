package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class LoseScreen implements Screen
{
	final Drop game;
	OrthographicCamera camera;
	Music theEnd;
	public LoseScreen(final Drop game)
	{
		theEnd = Gdx.audio.newMusic(Gdx.files.internal("the-end.mp3"));
		this.game = game;
		this.camera = new OrthographicCamera();
		camera.setToOrtho(false,800,480);
	}

	@Override
    public void render(float delta)
    {
	    // TODO Auto-generated method stub
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		
		game.batch.begin();
		game.font.draw(game.batch, "You Lose!",400, 240);
		game.batch.end();
		if (Gdx.input.isTouched())
		{
			game.setScreen(new GameScreen(game));
			dispose();
		}
    }

	@Override
    public void resize(int width, int height)
    {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void show()
    {
		theEnd.play();
	    
    }

	@Override
    public void hide()
    {
	    theEnd.stop();
    }

	@Override
    public void pause()
    {
	    theEnd.stop();
    }

	@Override
    public void resume()
    {

	    
    }

	@Override
    public void dispose()
    {

	    
    }

}
