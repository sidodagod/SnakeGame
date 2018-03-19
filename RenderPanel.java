package snake;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;

@SuppressWarnings("serial")

public class RenderPanel extends JPanel
{

	public static final Color GREEN = new Color(1666073);

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Snake snake = Snake.snake;
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, 1000, 1000);
		int i = 1 * snake.snakeParts.size();
		for (Point point : snake.snakeParts)
		{
			g.setColor(new Color(i, i, i));
			g.fillRect(point.x * Snake.SCALE, point.y * Snake.SCALE, Snake.SCALE, Snake.SCALE);
			g.setColor(Color.WHITE);
			g.drawRect(point.x * Snake.SCALE, point.y * Snake.SCALE, Snake.SCALE, Snake.SCALE);
			i -= 1;
		}
		g.setColor(Color.BLACK);
		g.fillRect(snake.head.x * Snake.SCALE, snake.head.y * Snake.SCALE, Snake.SCALE, Snake.SCALE);
		g.setColor(Color.RED);
		g.fillRect(snake.cherry.x * Snake.SCALE, snake.cherry.y * Snake.SCALE, Snake.SCALE, Snake.SCALE);
		String string = "Score: " + snake.score + ", Length: " + snake.tailLength + ", Time: " + snake.time / 25;
		g.setColor(Color.WHITE);
		g.drawString(string, (int) (getWidth() / 2 - string.length() * 2.5f), 10);
		string = "Game Over!";
		g.setColor(Color.RED);
		if (snake.over)
		{
			g.drawString(string, (int) (getWidth() / 2 - string.length() * 2.5f), (int) snake.dim.getHeight() / 4);
		}
		string = "Paused!";
		if (snake.paused && !snake.over)
		{
			g.drawString(string, (int) (getWidth() / 2 - string.length() * 2.5f), (int) snake.dim.getHeight() / 4);
		}
	}
}