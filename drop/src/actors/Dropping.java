package actors;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Dropping
{
	private Integer tipo;
	private Rectangle rectangle;
	public Dropping(int tipo)
	{
		this.rectangle = new Rectangle();
		rectangle.x = MathUtils.random(0, 800 - 64);
		rectangle.y = 480;
		rectangle.width = 64;
		rectangle.height = 64;
		this.tipo=tipo;
	}
	public float getY()
	{
		return rectangle.y;
	}

	public float getX()
	{
		return rectangle.x;
	}

	public Rectangle getRectangle()
	{

		return rectangle;
	}

	public Integer getTipo()
	{
		return tipo;
	}
}
