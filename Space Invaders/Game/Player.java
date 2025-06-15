import java.awt.*; //allows for implementation of Rectangle objects
import java.awt.Color;//set colors of bullets or beams

//allows for images to be drawn
import java.awt.image.BufferedImage; 
import javax.imageio.ImageIO;
import java.io.IOException;

/*PLAYER USES METHODS AND ATTRIBUTES OF RECTANGLE CLASS: used for collision detection*/
public class Player extends Rectangle{
   
   Game g;
   
   int speed; //change in position x
   
   public BufferedImage ship;
   public BufferedImage destroyedShip;
   
   //determines if player has been hit by enemy bomb
   boolean hit = false;
   
   
   //address - png address
   public String address;
   
   
   //score doesn't change regardless of switch to woffinden or normal mode
   public static int score;
   
   
   //determines whether player can fire a bullet again
   //player cannot fire until bullet contacts enemy or top of screen
   boolean readyToFire;
   boolean shot = false;
   
   
   //bullet is set to nothing initially
   Rectangle fillerRect = new Rectangle(0, 0, 0, 0);
   Rectangle bullet = fillerRect; 
    
   
   //ONLY FOR WOFFINDEN MODE!! 
   Rectangle additionalBeam = fillerRect;
   

   int initialBulletX = 0;
   int initialBulletY = 0;
         
   public Player(Game g, String address, int x, int y, int w, int h, int s){
      
      this.g = g; //g is set for this class to access game variables
      
      
      //initial position, speed, and size of player
      this.x = x;
      this.y = y;
      this.width = w;
      this.height = h;
      speed = s;
      
      
      this.address = address;
      
      //ship set to given address and location in files      
      try{
         
         ship = ImageIO.read(getClass().getResourceAsStream(address));
         
      } catch (IOException e) {
         
         System.out.println("cannot read image");
         
      }
      
      
      //destroyedShip set to destroyed image png and location in files  
      try{
      
         destroyedShip = ImageIO.read(getClass().getResourceAsStream("/Game Art/boom.png"));
      
      } catch (IOException e) {
      
         System.out.println("cannot read image");
      
      }
      
      
   
   }
   
   //update method: based on user key input (left, or right)
   //player's x position is changed
   //see shooting mechanics within method   
   public void update(){
   
      
      if(g.leftPressed == true && x > 10){
         
         x -= speed;
         
      }
      
      if(g.rightPressed == true && x <= 1152 - 48 - 10){
         
         x += speed;
         
      } 
           
      
      //if space bar is pressed:
      if(g.spacePressed == true){
      
         if(bullet == fillerRect){
            
            readyToFire = true; 
         
         }
         
         if(readyToFire == true){
            
            //bullet is fired from instant location of player
            
            initialBulletY = y + 10; 
            initialBulletX = x + 22;
            
            //if normal mode, player has one bullet - lot smaller
            if(this.address.equals("/Game Art/mainPlayer.png")){
            
               bullet = new Rectangle(initialBulletX, initialBulletY, 3, 6 );
               shot = true;
               //shot is set to true to make bullet move up
            
            } else {
               
            //if woffinden mode, player gets two beams
               bullet = new Rectangle(initialBulletX + 30, 0, 5, 818);
               additionalBeam = new Rectangle(initialBulletX + 8, 0, 5, 816);
               
               //shot is not set to anything since beams take up whole screen - they don't need moving up
               
               
               
               //if enemy is hit by beams, they are destroyed and points are adjusted for player
               //beams are wide, which means it might contact the enemy multiple times per millisecond
               //points in woffinden mode are inflated a lot
               for(int i = 0; i < g.enemies.length; i++){
               
                  for(int j = 0; j < g.enemies[0].length; j++){
                  
                     if(bullet.intersects(g.enemies[i][j]) || additionalBeam.intersects(g.enemies[i][j])){
                        
                        g.enemies[i][j].hit = true;
                        
                        this.adjustPoints(g.enemies[i][j]);
                     
                     }
                  
                  }
               
               }
            
            }
         }
         
         
      }   else {
      
      //if space bar is released, bullets cannot refire until it contacts something
      //-see collision method below
      
         readyToFire = false;
      
      
         if(bullet.y <= 0 || collision(g.enemies, g.shields)){
               
            bullet = fillerRect;
            additionalBeam = fillerRect;
            shot = false;
            readyToFire = true;
               
         }
            
         
            
      } 
      
   
      
   
   
   }
      
   
   //draw method for player, draws image, beams/bullets   
   public void draw(Graphics2D g2){
   
      //only draws if the player isn't hit
      if(!hit){  
      
      //woffinden beams are red
      
         if(this.address.equals("/Game Art/woffinden.png")){
         
            g2.setColor(Color.RED);
         
         
         
         } else {
         
            g2.setColor(Color.WHITE);
         
         
         }
         
         //draw the main image, bullet/beam, second beam if woffinden mode
      
         g2.drawImage(ship, x, y, this.width, this.height, null);
         
         shoot();
         
         g2.fillRoundRect(bullet.x, bullet.y, bullet.width, bullet.height, 15, 10);
         
         if(additionalBeam != null){
         
         
            g2.fillRoundRect(additionalBeam.x, additionalBeam.y, additionalBeam.width, additionalBeam.height, 15, 10);
         
         
         
         }
         
           
      } else {
      
      //if player is hit, ship is destroyed visually
         
         g2.drawImage(destroyedShip, x, y, this.width, this.height, null);
      
      
      }     
      
   }
   
   
   //only runs for normal mode
   //when shot is true, bullet moves up
   public void shoot(){
   
      if(shot == true && this.address.equals("/Game Art/mainPlayer.png")){
      
         bullet.y -= 15;
         
         g.spacePressed = false;
      
      }
   
   }
   
   
   //determines if player is hit - used in update gameOver status in Game
   public boolean isBombed(Enemy[][] enemies){
   
      for(Enemy[] a: enemies){
      
         for(Enemy b: a){
            
            if(b.bomb.intersects(this)){
                           
               hit = true;
            
               return true;
            
            }
         
         
         }
      
      }
      
      return false;   
   
   }
   
   //determines if bullet hits shield or enemy, and adjusts shield hp or enemy status accordingly
   public boolean collision(Enemy[][] e, Shield[] s){
     
     
      for(Shield t : s){
      
         if(bullet.intersects(t)){
            
            t.weaken();
            return true;
            
         }
      
      
      }
       
      for(Enemy[] f: e){
      
         for(Enemy g: f){
            
            if(bullet.intersects(g)){
               
               g.killed();
               //that enemy is destroyed
               
               adjustPoints(g);
               
               return true;
               
            }
         
         
         }
      
      
      }
      
   
      
      return false;
   
   
   }
   
   
   //based on which row the enemy ships are in, 
   //each are assigned a point value for when they are destroyed
   public void adjustPoints(Enemy e){
   
      
      if(e.address.equals("/Game Art/5row.png")){
         
         score += 30;
         
      } else if(e.address.equals("/Game Art/4row.png") || e.address.equals("/Game Art/3row.png")){
         
         score += 20;
         
      } else if(e.address.equals("/Game Art/2row.png") || e.address.equals("/Game Art/1row.png")) {
         
         score += 10;
         
      }
      
      
      
   }
   
   
      
}
   





