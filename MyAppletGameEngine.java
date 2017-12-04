/*******************************************************************************************************************************
*
*	MyAppletGameEngine.java 
*	
*	- Basic Applet2D-Framework (Images, DoubleBuffering, Keys, Mouse, MainLoop)
*
*	03.12.2017 - excluded the main loop thread.
*	03.12.2017 - added frame independent movement
*
*	created: 03.12.2017 - 17:33
*	last edited: 03.12.2017 - 23:47
*
*	author: Michael Fronzek	
*
*******************************************************************************************************************************/
package appletengine;

import java.util.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;

public class MyAppletGameEngine extends Applet implements KeyListener, MouseListener, MouseMotionListener//, Runnable
{
	private static final long serialVersionUID = 1L;
	
	final float PI = 3.14159f;
	final float bog = PI/180.0f;
	
	//our mouse position variables
	int xpos;
	int ypos;

	//offscreen image and offscreen graphics for DoubleBuffering
	Image oI;
	Graphics oG;
	
	//thread for our class
	Thread main;
	boolean gamestart;
	
	//some variables for animation
	Image img;
	Image bigImg; 
	Image img2;
	Image hud;
	
	String side;
	float x,y;
	float t;
	float alpha;
	
	boolean up;
	boolean down;
	boolean left;
	boolean right;
	
	ArrayList<Point2D> stars = new ArrayList<Point2D>();
	
	//frames per second calculation
	double start,elapsed;
 
	//called when applet is started
	public void init() 
	{
		//let the animated star go to the right
		side = "right";
	    t = 0;
	    alpha = 0;
	    
		//initial x and y of the hat
		x = 160;
		y = 160;
		
		//time measurement
		elapsed = 0;
		start = System.nanoTime()/1000000;
		
		//applet size
		this.setSize(800,600);
		
		//thread this class to create our main loop
		//main = new Thread(this);
		gamestart = true;

		//create the offscreen image for DoubleBuffering
		oI = createImage(800,600);
	    oG = oI.getGraphics();
	    
	    //load images
	    try 
	    {
	    	img 	= ImageIO.read(new File("c:/users/mic/desktop/strawberry.png"));
	    	bigImg 	= ImageIO.read(new File("c:/users/mic/desktop/strawberry2.jpg"));
	    	img2 	= ImageIO.read(new File("c:/users/mic/desktop/F5S4.png"));
	    	hud 	= ImageIO.read(new File("c:/users/mic/desktop/hud.png"));
	    }
	    catch (IOException e) { }
	 
	    //add Event Listener to this class
	    this.addKeyListener(this);
	    this.addMouseListener(this);
	    this.addMouseMotionListener(this);
	    
	
	    //start the main loop
	//    main.start();
	}
/*
	//the main loop
	public void run() 
	{		
		// the game loop:
		while(gamestart) 
		{
			// animation from left to right
			if(side.equals("left")) {
				t = t - 1f;
			} else {
				t = t + 1f;
			}
			
			if (t > 200) {
				side = "left";
			}
			if (t < 0) {
				side = "right";
			}
			
			// animation brake
			try {
				Thread.sleep(20);
			} catch (Exception e) {}
		}
	}
*/
	// override update to just call our paint()
	public void update(Graphics g) 
	{
		paint(g);
	}
	
	public void paint(Graphics g) 
	{
		//calculate frames per second
		elapsed = System.nanoTime()/1000000 - start;
		start = System.nanoTime()/1000000;
		int fps = (int)(1000.0f/elapsed);

		//repaint the background
		//oG.setColor(Color.BLACK);
		//oG.fillRect(0, 0, 800, 600);
		oG.drawImage(bigImg, 0, 0, 800, 600, null);
		
		//draw game elements:
		oG.drawString("(" + ((int)t+300) + ":280)", (int)t+300,280);
		oG.drawImage(img, (int)t+300, 300, 32, 32, null);
		
		if(alpha < 360) 
		{
			alpha = alpha + 90 * (float)elapsed/1000;
		}
		else 
		{
			alpha = 0;
		}
		
		oG.drawImage(img, (int)(400+(100*Math.cos(alpha * bog)))-16, (int)(300+(100*Math.sin(alpha * bog)))-16, 32, 32, null);
		
		if(side.equals("left")) {
			t = t - 100f * (float)elapsed/1000;
		} else {
			t = t + 100f * (float)elapsed/1000;
		}
		
		if (t > 200) {
			side = "left";
		}
		if (t < 0) {
			side = "right";
		}
		
		if(up) 
		{
			y = y - 200 * (float)elapsed/1000; 
		}
		
		if(down) 
		{
			y = y + 200 * (float)elapsed/1000;
		}

		if(left) 
		{
			x = x - 200 * (float)elapsed/1000; 
		}		

		if(right) 
		{
			x = x + 200 * (float)elapsed/1000;
		}

		oG.drawImage(img2, (int)x, (int)y, 64, 64, null);
		
		int oldx = 400;
		int oldy = 300;
		
		for(Point2D s:stars) 
		{
			
			oG.setColor(Color.WHITE);
			//oG.drawLine(oldx, oldy, s.x, s.y);
			oG.drawImage(img, s.x-20, s.y-20, (int)(40+Math.random()*4), (int)(40+Math.random()*4), null);
			oG.drawString(stars.indexOf(s) + "(" + s.x + ":" + s.y + ")", s.x-60,s.y-20);
			oldx = s.x;
			oldy = s.y;
		}

		// head up display
		oG.drawImage(hud, 0, 0, 800, 600, null);
		
		//mouse pointer
		oG.drawImage(img, xpos-10, ypos-10, 20, 20, null);
		
		//draw information
		oG.setColor(Color.WHITE);
		oG.setFont( new Font( "Verdana", Font.BOLD, 18 ) );
		oG.drawString(fps + " fps", 10, 40);
		oG.setColor(new Color(0,255,255));
		oG.drawString("frame: " + elapsed + " ms", 10, 20);
		oG.drawString("stars: " + stars.size(), 700, 20);
		oG.drawString("X: " + xpos + " Y: " + ypos, 10, 580);
		
		oG.setFont( new Font( "Arial", Font.PLAIN, 9 ) );

		oG.drawLine(400,300,(int)(400+(100*Math.cos(alpha * bog))), (int)(300+(100*Math.sin(alpha * bog))));
		oG.drawString("" + alpha,(int)(400+(100*Math.cos(alpha * bog)))-40, (int)(300+(100*Math.sin(alpha * bog))));		
		//switch DoubleBuffer Pages
		g.drawImage( oI, 0, 0, this );
		repaint();
	}

	public void mouseMoved(MouseEvent e)
	{
		// get mouse position
		xpos = e.getX();
		ypos = e.getY();
	}
	
	public void mousePressed (MouseEvent e) 
	{
		//add some stars to our stars list
		Point2D p = new Point2D();

		xpos = e.getX();
		ypos = e.getY();
		
		p.x = xpos;
		p.y = ypos;
		
		stars.add(p);
	}
	
	public void keyPressed(KeyEvent e) 
	{
		// keyChars
		if(e.getKeyChar() == 'w') 
		{
			up = true;
		}
		
		if(e.getKeyChar() == 's') 
		{
			down = true;
		}

		if(e.getKeyChar() == 'a') 
		{
			left = true;
		}		

		if(e.getKeyChar() == 'd') 
		{
			right = true;
		}
		
		// keyCodes
		if(e.getKeyCode() == KeyEvent.VK_SPACE) 
		{
			gamestart=false;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP) 
		{
			up = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_DOWN) 
		{
			down = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_LEFT) 
		{
			left = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) 
		{
			right = true;
		}
	}

	public void keyReleased(KeyEvent e) 
	{
		if(e.getKeyChar() == 'w') 
		{
			up = false;
		}
		
		if(e.getKeyChar() == 's') 
		{
			down = false;
		}

		if(e.getKeyChar() == 'a') 
		{
			left = false;
		}		

		if(e.getKeyChar() == 'd') 
		{
			right = false;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP) 
		{
			up = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_DOWN) 
		{
			down = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_LEFT) 
		{
			left = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) 
		{
			right = false;
		}
	}
	
	public void keyTyped(KeyEvent e) {}
	public void mouseClicked (MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {}
	public void mouseReleased (MouseEvent e) {} 
	
	public void mouseEntered (MouseEvent e) 
	{
		 this.requestFocus();
	}
	
	public void mouseExited (MouseEvent e) {}

}  