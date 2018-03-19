package snake;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.Timer;

public class Snake implements ActionListener {
	private ArrayList<Integer> nums = new ArrayList<Integer>();
	private int totalRuns = 0;
	private ArrayList<Integer> numsOriginal = new ArrayList<Integer>();
	private BufferedWriter bw2;
	private int run = 1;
	private int[] run1 = new int[14];
	private int[] run2 = new int[14];
	private int[] run3 = new int[14];
	private int[] run4 = new int[14];
	private int[] run5 = new int[14];
	private int chanceUp;
	private int chanceDown;
	private int chanceLeft;
	private int chanceRight;
	private int cD = 0;
	private int currentGen;
	private static int distanceFromWall;
	private static int distanceFromSelfU;
	private static int distanceFromSelfD;
	private static int distanceFromSelfR;
	private static int distanceFromSelfL;
	Scanner sC;
	private boolean over2;
	private Point cherry2;
	private Point head2;
	private Timer timer2;
	public static Snake snake; // the snake var
	private JFrame jframe;// jframe creation
	private RenderPanel renderPanel;// render panel var creation
	private Timer timer = new Timer(1, this);// creates a time that uses the snake class implementing action listener
												// as
												// its action listener var for constructor and 60 millisecond delay
	public ArrayList<Point> snakeParts = new ArrayList<Point>();// holds array of points that have x and y values
	public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3, SCALE = 50;// scale of 50 for easy math and
																				// constants
																				// for directions
	public int ticks = 0, direction = DOWN, score, tailLength = 10, time;// ticks for time math, direction var to hold
																			// current direction, score to hold score,
																			// taillength to hold length of snake, time
																			// to hold seconds
	public Point head, cherry;// points for the head of snake and cherry
	public boolean over = false, paused;// booleans for end game or pause
	public Dimension dim;// dimension of jframe
	private boolean inUse = false;// used to fix error in which the player would press 2 keys within the 60
									// millisecond time and surpass the if statement by changing the direction mid
									// update

	public Snake() {
		dim = Toolkit.getDefaultToolkit().getScreenSize();// setting up jframe
		jframe = new JFrame("Snake");// creates new jframe
		jframe.setVisible(true);// sets it to visible
		jframe.setSize(1000, 1025);// size of 1000 by 1000
		jframe.setResizable(false);// not resizable because of set num of squares
		jframe.setLocation(dim.width / 2 - jframe.getWidth() / 2, dim.height / 2 - jframe.getHeight() / 2);// sets
																											// location
		jframe.add(renderPanel = new RenderPanel());// adds the renderpanel that has an override of paintComponent to
													// jframe
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// lets you exit jframe using red x
		// adds the keylistener that changes direction
		try {
			sC = new Scanner(new File("gennum.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		currentGen = sC.nextInt();
		try {
			sC = new Scanner(new File("nums.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (sC.hasNextInt()) {
			nums.add(sC.nextInt());
		}

	}

	public static void main(String[] args) {
		snake = new Snake();
		snake.startGame();
		snake.startAI();
	}

	public void startAI() {
		distanceFromWall = snake.blocksFromWall();
	}

	private int getChoice() {
		int lChance = 0;
		int rChance = 0;
		int uChance = 0;
		int dChance = 0;
		rChance += nums.get(0) - head.x;
		lChance += head.x + nums.get(1);
		uChance += head.y + nums.get(2);
		dChance += nums.get(3) - head.y;
		rChance += distanceFromSelfR + nums.get(4);
		uChance += distanceFromSelfU + nums.get(5);
		lChance += distanceFromSelfL + nums.get(6);
		dChance += distanceFromSelfD + nums.get(7);
		rChance += cherry.x + nums.get(8);
		lChance += nums.get(9) - cherry.x;
		uChance += nums.get(10) - cherry.y;
		dChance += cherry.y + nums.get(11);

		for (int i = 0; i < snakeParts.size(); i++) {
			if (snakeParts.get(i).x - head.x == 1 && head.y == snakeParts.get(i).y) {
				rChance = -10000;
			}
			if (head.x - snakeParts.get(i).x == 1 && head.y == snakeParts.get(i).y) {
				lChance = -10000;
			}
			if (head.y - snakeParts.get(i).y == 1 && head.x == snakeParts.get(i).x) {
				uChance = -10000;
			}
			if (snakeParts.get(i).y - head.y == 1 && head.x == snakeParts.get(i).x) {
				dChance = -10000;
			}
		}

		if (head.x == 19) {
			rChance = -10000;
		}
		if (head.y == 19) {
			dChance = -10000;
		}
		if (head.y == 0) {
			uChance = -10000;
		}
		if (head.x == 0) {
			lChance = -10000;
		}
		if (direction == UP) {
			dChance = -10000;
		}
		if (direction == DOWN) {
			uChance = -10000;
		}
		if (direction == RIGHT) {
			lChance = -10000;
		}
		if (direction == LEFT) {
			rChance = -10000;
		}
		System.out.println("uChance" + uChance + " dChance" + dChance + " lChance" + lChance + " rChance" + rChance);
		int largest = uChance;
		int ret = UP;
		if (dChance > largest) {
			largest = dChance;
			ret = DOWN;
		}
		if (rChance > largest) {
			largest = rChance;
			ret = RIGHT;
		}
		if (lChance > largest) {
			largest = rChance;
			ret = LEFT;
		}
		return ret;
	}

	private int xDisToCherry() {
		return cherry.x - head.x;
	}

	private int yDisToCherry() {
		return cherry.y - head.y;
	}

	public void startGame() {
		over = false;// instantiates vars
		paused = false;
		time = 0;
		score = 0;
		tailLength = 14;
		ticks = 0;
		direction = DOWN;
		head = new Point(0, -1);
		snakeParts.clear();
		cherry = new Point((int) (Math.random() * 16), (int) (Math.random() * 16));
		timer.start();// this is where game actually starts so every 60 milliseconds it sends an
						// actionevent to the actionPerformed override and executes the code in the body
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		renderPanel.repaint();// method that automatically repaints paintComponent
		ticks++;// increment ticks
		if(totalRuns > 1000)
		{
			timer.stop();
		}
		if (!over && ticks > 10) {
			keyPressed(getChoice());
		}
		if (over) {
			newGen();
		}
		if(time >= 400)
		{
			newGen();
		}
		if (ticks % 2 == 0 && head != null && !over && !paused)// checking if it is correct time to update
		{
			time++;
			snakeParts.add(new Point(head.x, head.y));// adds new point with same location as head

			if (direction == UP) {
				if (head.y - 1 >= 0 && noTailAt(head.x, head.y - 1))// checks to make sure if next spot is available in
																	// current direction
				{
					head = new Point(head.x, head.y - 1);
				} else {
					over = true;// game over

				}
			} // rest of these are just like that one
			else if (direction == DOWN) {
				if (head.y + 1 < 20 && noTailAt(head.x, head.y + 1)) {
					head = new Point(head.x, head.y + 1);
				} else {
					over = true;
				}
			} else if (direction == LEFT) {
				if (head.x - 1 >= 0 && noTailAt(head.x - 1, head.y)) {
					head = new Point(head.x - 1, head.y);
				} else {
					over = true;
				}
			} else if (direction == RIGHT) {
				if (head.x + 1 < 20 && noTailAt(head.x + 1, head.y)) {
					head = new Point(head.x + 1, head.y);
				} else {
					over = true;
				}
			}

			if (snakeParts.size() > tailLength)// if snake is too big cut the head off
			{
				snakeParts.remove(0);

			}

			if (cherry != null)// if it cant find cherry
			{
				if (head.equals(cherry)) {
					score += 10;
					tailLength++;
					cherry.setLocation((int) (Math.random() * 16), (int) (Math.random() * 16));// new cherry
				}
			}
			inUse = false;// tells program that the current move is done processing
		}
	}

	public boolean noTailAt(int x, int y)// helper method
	{
		for (Point point : snakeParts) {
			if (point.equals(new Point(x, y))) {
				return false;
			}
		}
		return true;
	}

	public void keyPressed(int i)// if a key is pressed
	{
		if (i == LEFT && direction != RIGHT && !inUse || i == LEFT && direction != RIGHT && !inUse)// if the key is A or
																									// left arrow
		{
			inUse = true;// sets in use to true until the current move is done processing
			direction = LEFT;
		} else if ((i == RIGHT && direction != LEFT && !inUse || i == RIGHT) && direction != LEFT && !inUse) {
			inUse = true;
			direction = RIGHT;
		} else if ((i == UP && direction != DOWN && !inUse || i == UP) && direction != DOWN && !inUse) {
			inUse = true;
			direction = UP;
		} else if ((i == DOWN && direction != UP && !inUse || i == DOWN) && direction != UP && !inUse) {
			inUse = true;
			direction = DOWN;
		}
		if (i == KeyEvent.VK_SPACE)// for resart game or unpause
		{
			if (over) {
				startGame();
			} else {
				paused = !paused;
			}
		}
	}

	public int blocksFromWall() {
		int ret = 0;
		if (direction == UP) {
			ret = head.y;
		} else if (direction == DOWN) {
			ret = 20 - head.y;
		} else if (direction == RIGHT) {
			ret = 20 - head.x;
		} else if (direction == LEFT) {
			ret = head.x;
		}
		System.out.println("blocksFromWall: " + ret);
		return ret;

	}

	public int currentDir() {
		return direction;
	}

	public Point getCherryPos() {
		return cherry;
	}

	public Point getHeadPos() {
		return head;
	}

	public boolean gameStatus() {
		return over;
	}

	public void disFromSelf() {
		int tempX = head.x;
		int tempY = head.y;
		ArrayList<Integer> temp = new ArrayList<Integer>();
		int ret = 0;

		for (int i = 0; i < snakeParts.size(); i++) {
			if (snakeParts.get(i).x == tempX) {
				temp.add(snakeParts.get(i).y);
			}
		}
		if (temp.size() == 0) {
			ret = 21;
		} else {
			int smallest = temp.get(0);
			for (int i = 1; i < temp.size(); i++) {
				if (temp.get(i) > smallest) {
					smallest = temp.get(i);
				}
			}
			ret = smallest - head.y;
			distanceFromSelfU = ret;
			temp = new ArrayList<Integer>();
			ret = 0;
		}

		for (int i = 0; i < snakeParts.size(); i++) {
			if (snakeParts.get(i).x == tempX) {
				temp.add(snakeParts.get(i).y);
			}
		}
		if (temp.size() == 0) {
			ret = 21;
		} else {
			int smallest = temp.get(0);
			for (int i = 1; i < temp.size(); i++) {
				if (temp.get(i) < smallest) {
					smallest = temp.get(i);
				}
			}
			ret = head.y - smallest;
			distanceFromSelfD = ret;
			temp = new ArrayList<Integer>();
			ret = 0;
		}

		for (int i = 0; i < snakeParts.size(); i++) {
			if (snakeParts.get(i).y == tempY) {
				temp.add(snakeParts.get(i).x);
			}
		}
		if (temp.size() == 0) {
			ret = 21;
		} else {
			int smallest = temp.get(0);
			for (int i = 1; i < temp.size(); i++) {
				if (temp.get(i) < smallest) {
					smallest = temp.get(i);
				}
			}
			ret = smallest - head.x;
			distanceFromSelfR = ret;
			temp = new ArrayList<Integer>();
			ret = 0;
		}

		for (int i = 0; i < snakeParts.size(); i++) {
			if (snakeParts.get(i).y == tempY) {
				temp.add(snakeParts.get(i).x);
			}
		}
		if (temp.size() == 0) {
			ret = 21;
		} else {
			int smallest = temp.get(0);
			for (int i = 1; i < temp.size(); i++) {
				if (temp.get(i) > smallest) {
					smallest = temp.get(i);
				}
			}
			ret = head.x - smallest;
			distanceFromSelfL = ret;
			temp = new ArrayList<Integer>();
			ret = 0;
		}

	}

	public void newGen() {
		if (run == 1) {
			for (int i = 0; i < nums.size(); i++) {
				numsOriginal.add(nums.get(i));
			}
		}
		for (int i = 0; i < 12; i++) {
			if (run == 1) {
				run1[i] = nums.get(i);
			} else if (run == 2) {
				run2[i] = nums.get(i);
			} else if (run == 3) {
				run3[i] = nums.get(i);
			} else if (run == 4) {
				run4[i] = nums.get(i);
			} else if (run == 5) {
				run5[i] = nums.get(i);
			}
		}
		if (run == 1) {
			run1[12] = score;
			run1[13] = time;
		} else if (run == 2) {
			run2[12] = score;
			run2[13] = time;
		} else if (run == 3) {
			run3[12] = score;
			run3[13] = time;
		} else if (run == 4) {
			run4[12] = score;
			run4[13] = time;
		} else if (run == 5) {
			run5[12] = score;
			run5[13] = time;
		}
		int randomI = (int)(Math.random() * 9 + 1);
		for (int i = 0; i < nums.size(); i++) {
			if (run == 1) {
				nums.set(i, nums.get(i) + randomI);
			} else if (run == 2) {
				nums.set(i, nums.get(i) - randomI);
			} else if (run == 3) {
				nums.set(i, nums.get(i) + randomI);
			} else if (run == 4) {
				nums.set(i, nums.get(i) - randomI);
			} else if (run == 5) {
				nums.set(i, nums.get(i));
			}
			randomI = (int)(Math.random() * 9 + 1);
		}
		run++;
		totalRuns++;
		if (run == 6) {
			run = 1;
			try {
				getBestRun();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			startGame();
		}
	}

	private void getBestRun() throws IOException
	{
		try {
			bw2 = new BufferedWriter(new FileWriter("nums.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int best = run1[12];
		int bestN = 1;
		if(run2[12] > best)
		{
			best = run2[12];
			bestN = 2;
		}
		if(run3[12] > best)
		{
			best = run3[12];
			bestN = 3;
		}
		if(run4[12] > best)
		{
			best = run4[12];
			bestN = 4;
		}
		if(run5[12] > best)
		{
			best = run5[12];
			bestN = 5;
		}
		if(run1[12] == 0 && run2[12] == 0 && run3[12] == 0 && run4[12] == 0 && run5[12] == 0)
		{
			bestN = (int)(Math.random() * 4 + 1);
		}
		
			if(bestN == 1)
			{
				String content = run1[0] + " " + run1[1] + " " + run1[2] + " " + run1[3] + " " + run1[4] + " " + run1[5] + " " + run1[6] + " " + run1[7] + " " + run1[8] + " " + run1[9] + " " + run1[10] + " " + run1[11];
				try {
					bw2.write(content);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(bestN == 2)
			{
				String content = run2[0] + " " + run2[1] + " " + run2[2] + " " + run2[3] + " " + run2[4] + " " + run2[5] + " " + run2[6] + " " + run2[7] + " " + run2[8] + " " + run2[9] + " " + run2[10] + " " + run2[11];
				try {
					bw2.write(content);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(bestN == 3)
			{
				String content = run3[0] + " " + run3[1] + " " + run3[2] + " " + run3[3] + " " + run3[4] + " " + run3[5] + " " + run3[6] + " " + run3[7] + " " + run3[8] + " " + run3[9] + " " + run3[10] + " " + run3[11];
				try {
					bw2.write(content);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(bestN == 4)
			{
				String content = run4[0] + " " + run4[1] + " " + run4[2] + " " + run4[3] + " " + run4[4] + " " + run4[5] + " " + run4[6] + " " + run4[7] + " " + run4[8] + " " + run4[9] + " " + run4[10] + " " + run4[11];
				try {
					bw2.write(content);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(bestN == 5)
			{
				String content = run5[0] + "\n" + run5[1] + "\n" + run5[2] + "\n" + run5[3] + "\n" + run5[4] + "\n" + run5[5] + "\n" + run5[6] + "\n" + run5[7] + "\n" + run5[8] + "\n" + run5[9] + "\n" + run5[10] + "\n" + run5[11];
				try {
					bw2.write(content);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			bw2.close();
		
	}

	private void addGenData() throws IOException {
		try (FileWriter fw = new FileWriter("myfile.txt", true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.println("Score:" + score + ", Time" + time);
		} catch (IOException e) {
			throw e;
		}
	}

}