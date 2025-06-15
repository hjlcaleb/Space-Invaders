import javax.swing.*; //java swing allows for JFrame and 2D movement

//allows color of projectiles to be changed
import java.awt.*; //allows for implementation of Rectangle objects - used for collision detection 
//allows for keyboard input from user
import java.awt.event.KeyEvent; 
import java.awt.event.KeyListener;

public class Game extends JPanel implements Runnable, KeyListener{

   
   //JPanel allows game to be added
   //Runnable used to implement a thread that runs 60 frames per second
   //KeyListner gathers user keyboard input (shooting, movement)
   
   
   //screen is 1152x864 pixels
   private final int SCREEN_WIDTH = 1024;
   private final int SCREEN_HEIGHT = 768;
   
    
   //deciding factor to end thread
   public boolean gameOver = false; 
   
   
   //thread, allows for game to be projected on JFrame 60 frames per second
   Thread thread; 
   
   

   //two modes for player: normal and easier (woffinden mode):
   //normal mode is default mode, slower movement and don't have laser beams
   Player normal = new Player(this, "/Game Art/mainPlayer.png", SCREEN_WIDTH/2, SCREEN_HEIGHT - 50, 48, 48, 5); 
   Player woffinden = new Player(this, "/Game Art/woffinden.png", SCREEN_WIDTH/2, SCREEN_HEIGHT - 100, 96, 96, 10); 
   Player player = normal; 
   
   
   //borders used to keep enemies moving when they hit sides
   Rectangle border1 = new Rectangle(-50 , 0, 50, SCREEN_HEIGHT); 
   Rectangle border2 = new Rectangle(SCREEN_WIDTH , 0, 50, SCREEN_HEIGHT);
   
   private int numShields = 4;
   private int SHIELD_SIZE = 128;
   private int remainingSpace = SCREEN_WIDTH -  (SHIELD_SIZE*numShields);
   private int gap = remainingSpace / (numShields + 1);

   //list of shields
   public Shield shields[] = new Shield[numShields];
   
   
   //position of first enemy (using iteration, we will make a 11x5 array of enemies)
   int enemyPosX = 0;
   int enemyPosY = 50;
   public Enemy[][] enemies = new Enemy[5][11];
   //during iteration, each row will be assigned a different enemy
   String[] listOfEnemyPNG = new String[]{"/Game Art/5row.png", "/Game Art/4row.png", "/Game Art/3row.png", "/Game Art/2row.png", "/Game Art/1row.png"};
      
   int FPS = 60;

   
   
   
   
   //GAME CONSTRUCTOR
   public Game(){
      //adjusting screen settings
      this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
      this.setBackground(Color.black);
      this.setDoubleBuffered(true);   
      this.addKeyListener(this);
      this.setFocusable(true);
      this.setVisible(true);
      
   
      //iteration for creating array of enemies - iterates through rows first, then columns   
      for(int a = 0; a < enemies.length; a++){
      
         for(int b = 0; b < enemies[0].length; b++){
         
            //row index corresponds to index in the png list
            enemies[a][b] = new Enemy(this, listOfEnemyPNG[a], enemyPosX, enemyPosY, 1); //see Enemy class
            
            enemyPosX += 75; //enemies spaced out 75 pixels per row
         
         }
      
         enemyPosX = 0;
         
         //enemies spaced out 50 pixels per row
         enemyPosY += 50;
      
      }
      
      
      //iteration for creating array of enemies - fills shields list
      for(int i = 0; i < numShields; i++){
      
         shields[i] = new Shield(this, gap + (i * (SHIELD_SIZE + gap)), SCREEN_HEIGHT - 200); //see Shield class
      
      }
            
   }
   
   //begins execution of gameplay (thread)
   public void startThread(){
   
      thread = new Thread(this);
      
      thread.start();   
   }
   
   //run() is implicitly called when startThread() is called
   public void run(){
      
      //nanoseconds to seconds, every 1 second, the screen will be redrawn 60 times
      long timeToDraw = 1000000000/FPS;
      
      //next draw interval is when the current time reaches timeToDraw nanoseconds later
      long nextDrawTime = System.nanoTime() + timeToDraw;
      
      
      //main game loop, uses Thread.sleep to maintain 60 frames per second
      while(thread != null && !gameOver){
               
         
         //updates location, status of attributes of various components
         update(); 
         
         
         
         //draws components based on updated location
         //implicitly calls paintComponent()
         repaint(); 
                       
         
         
         
         //SLEEP TIME
         try{
            
            long sleepTime = nextDrawTime - System.nanoTime();
            
            sleepTime /= 1000000; //converting to milliseconds
            
            if(sleepTime < 0){
               
               sleepTime = 0;         
               
            }
            
            Thread.sleep(sleepTime); //pause for remaining time after updating and drawing
            
            nextDrawTime += timeToDraw;//repeat process but with new time
            
         } catch (Exception e){
            
            e.printStackTrace();
            
         }
         
      }
      
    
      
   
   
   }
   
   
   //calls respective update methods of various components
   //see specific classes for each purpose
   public void update(){
            
      player.update();
      
      //calls update method for all enemies
      for(Enemy e[]: enemies){
      
         for(Enemy f: e){
            
            f.update();
         
         
         }
      }
      
                  
      //check if all enemies are destroyed
      gameOver = checkWin();
      
      //check if player has been hit
      gameOver = player.isBombed(enemies);
      
   
   
   }
   
   
   //after updating locations and attributes of components, they are projected to screen
   public void paintComponent(Graphics g) {
   
      //2D Graphics is a subclass, so we first need to paint images on screen before becoming 2D Motion
      super.paintComponent(g);
      
      Graphics2D g2 = (Graphics2D)g; 
      
      g2.setFont(new Font("Courier", Font.PLAIN, 32)); 
      
      //call player draw method
      player.draw(g2);
      
      
      for(int i = 0; i < enemies.length; i++){
      
         for(int j = 0; j < enemies[0].length; j++){
         
               //call all shields' draw methods
         
            for(Shield s: shields){
               
               s.draw(g2);
                             
               
            }
                      
            //call all enemies' draw methods  
            enemies[i][j].draw(g2);
            
            
            //if an enemy in a far column touches the border, flip the direction 
            //and all enemies move down 50 pixels 
            if(enemies[i][j].intersects(border1)){
                              
               for(int c = 0; c < enemies[0].length; c ++){
                  
                  for(int r = 0; r < enemies.length; r++){
                     
                     enemies[r][c].y += 50;
                     
                  }
                  
                  
               }
                  
               Enemy.direction = "right";
               
            }
               
            if(enemies[i][j].intersects(border2)){
               
               for(int c = 0; c < enemies[0].length; c ++){
                  
                  for(int r = 0; r < enemies.length; r++){
                     
                     enemies[r][c].y += 50;
                     
                  }
                  
                  
               }
               
               Enemy.direction = "left";
               
            }
                            
            
         }
         
         //draw player score at corner (score is updated in other classes)
         g2.drawString("Score: " + Player.score, SCREEN_WIDTH - 200, 40);
                       
      } 
           
      
      
         
      
      //changing font size for winning or losing screen
      //print once game ends
      g2.setFont(new Font("Courier", Font.PLAIN, 70)); 
   
      if(checkWin()){
      
         g2.setColor(Color.GREEN);
         
         g2.drawString("Congratulations! You win!", 100 , 100);
      
      
      } else if(gameOver){
      
         g2.setColor(Color.RED);
         
         g2.drawString("GAME OVER.", 330, 100);
      
      }
      
      //cleans current panel for next drawing    
      g2.dispose();
      
               
   }
      
      
   //if there are still enemies on the board, return false
   public boolean checkWin(){
   
      
      for(int i = 0; i < enemies.length; i++){
      
         for(int j = 0; j < enemies[0].length; j++){
         
            if(!enemies[i][j].hit){ 
            
               return false;
            
            }
         
         
         }
      
      
      }
      
      
      
      return true;
   
   
   }
   
   //KEY LISTENER METHODS - Overwritten methods
   //these variables referenced throughout Player and Enemy classes
   public boolean leftPressed = false;
   public boolean rightPressed = false;
   public boolean spacePressed = false;


   //not needed
   @Override
   public void keyTyped(KeyEvent e){
   }
   
   
   //key code is returned, and variables are set
   //according to if they are pressed or released
   @Override
   public void keyPressed(KeyEvent e){
              
      if(e.getKeyCode() == KeyEvent.VK_LEFT) {
      
         leftPressed = true;
      
      
      }
      
      if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
         rightPressed = true; 
      
      
      }
      
      if(e.getKeyCode() == KeyEvent.VK_SPACE){
         
         spacePressed = true;
      
      }
      
      //distinguishes between game mode:
      //pressing W switches player to woffinden
      //pressing S switches player to normal
      if(e.getKeyCode() == KeyEvent.VK_W){
      
         player = woffinden; 
      
      }
      
      if(e.getKeyCode() == KeyEvent.VK_S){
      
         player = normal;     
         
      
      }
      
   }
      
      
    
   @Override
   public void keyReleased(KeyEvent e) {
      
      if(e.getKeyCode() == KeyEvent.VK_LEFT) {
      
         leftPressed = false;
      
      }
      
      if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
         rightPressed = false;   
      
      }
      
      if(e.getKeyCode() == KeyEvent.VK_SPACE){
        
         spacePressed = false;
      
      
      }
      
   }
    


   
   
}
  





