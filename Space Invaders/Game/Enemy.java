import java.awt.*;//allows for implementation of Rectangle objects
import java.awt.Color; // allows for color of enemy bombs

//allows for images to be imported
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;


/*ENEMY USES METHODS AND ATTRIBUTES OF RECTANGLE CLASS: used for collision detection*/
public class Enemy extends Rectangle{
    
   Game g; 
   
   int speed;
   
   public BufferedImage enemy;
   public BufferedImage destroyed;
   
   
   //determines if enemy has been hit by player
   boolean hit = false;
   
   //address of image location
   public String address;
   
   //direction that all enemys are moving
   //static because it applies to all objects from this class
   public static String direction = "right";
   
   
   //determines whether player can fire a bullet again
   //player cannot fire until bullet contacts enemy or top of screen
   boolean readyToFire;
   boolean shot = false;
   
   
   //bomb is set to nothing initially
   Rectangle fillerRect = new Rectangle(0, 0, 0, 0);
   public Rectangle bomb = fillerRect;
   int initialBulletX;
   int initialBulletY;
   
      
   public Enemy(Game g, String a, int x, int y, int s){
      
      this.g = g; //to get components of Game class
      
      
      //position, speed, and size
      this.x = x;
      this.y = y;
      speed = s;
      
      this.width = 48;
      this.height = 48;
      
      this.address = a;
      
      
      
      try{
      
         enemy = ImageIO.read(getClass().getResourceAsStream(address));
      
      } catch (IOException e) {
      
         System.out.println("cannot read image");
      
      }
      
      try{
      
         destroyed = ImageIO.read(getClass().getResourceAsStream("/Game Art/destroyed.png"));
      
      } catch (IOException e) {
      
         System.out.println("cannot read image");
      
      
      
      }
   
   
   
   }
   
   
   //update method
   public void update(){
      
      //Each enemy has a 0.1% chance of dropping a bomb - since the update() method is called so many times
      //very quickly, bombs drop frequently
     
      if((int)(Math.random()*1000) == 999){
      
      //similar mechanics to Player class
      
      
      //bomb drops, and new one cannot be dropped until the first bomb contacts the bottom of screen or shield
         if(bomb == fillerRect){
            
            readyToFire = true;
            
         }
                    
         if(readyToFire == true){
            
            initialBulletY = y + 10;
            initialBulletX = x + 22;
            bomb = new Rectangle(initialBulletX, initialBulletY, 5, 7); //new bomb when the enemy is ready to fire
            shot = true; //shot = true allows for shoot() method, which drops y coordinate downwards
               
         }      
         
      } else {
         
         //bomb not dropped if random number doesn't = 999
         //or current bomb hasn't collided with a shield or bottom of screen
         readyToFire = false;
            
         if(collision(g.shields) || bomb.y >= 864){
               
            bomb = fillerRect;
            shot = false;
            readyToFire = true;
            
               
         }  
         
      }
         
   }
   
   
   //draw method: draws image of enemy and bomb      
   public void draw(Graphics2D g2){
      
      //hit is changed in killed() method, called in other classes
      if(!hit){
         
         g2.drawImage(enemy, this.x, this.y, this.width, this.height, null);  
         
         shoot(); //only works if shot is set to true; on the condition that the bullet fired has already contacted something
         
         g2.setColor(Color.RED);
         g2.fillRect(bomb.x, bomb.y, bomb.width, bomb.height);
         g2.setColor(Color.WHITE);
      
         
         
         //all enemies increase in x position if going right
         if(direction.equals("right")){
          
            x += speed;
            
         
          
         }
         
         //all enemies decrease in y position if going left
         if(direction.equals("left")){
         
            x -= speed;
           
         
         }
      
      
         //if any enemy reaches 650 or higher, game is over      
         if(y >= 650){
         
            g.gameOver = true;
         
         }
         
      } else {
         
         //if destroyed, the destroyed image is first printed, then shape of enemy is set to nothing      
         g2.drawImage(destroyed, this.x, this.y, this.width, this.height, null);
         
         this.x = 0;
         this.y = 0;
         this.width = 0;
         this.height = 0;
         
         
         
      }
      
                
      
   }
   
   public void shoot(){
   
      //on the condition that shot is true, the bomb drops
      //status of boolean shot is on the condition that the bullet collides with the bottom of screen or shield
   
      if(shot == true){
      
         bomb.y += 5;
      
      }
   
   }

   
   //determines if bomb collided with a shield
   //if there is collision, the shield loses 10 hp
   public boolean collision(Shield[] s){
   
   
      for(int i = 0; i < s.length; i++){
      
            
         if(bomb.intersects(s[i])){
         
            s[i].weaken();
         
            return true;
               
         }
         
         
         
      
      
      }
      
      return false;
   
   
   }
   
   
   //enemy is destroyed if called
   public void killed(){
   
   
      hit = true;
   
   
   }

   
}

