import java.awt.*;  //allows for implementation of Rectangle objects

//allows for images to be drawn
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;


/*SHIELD USES METHODS AND ATTRIBUTES OF RECTANGLE CLASS: used for collision detection*/
public class Shield extends Rectangle{

   Game g; //uses components from Game
   
   public BufferedImage shield;
   
   //determines if shield is hit by either an enemy or player bullet
   //(in woffinden mode, shields are not contacted by beams)
   boolean hit = false;
   
   //each shield has 200 hit points
   public int hp;
  
   
   
   //SHIELD CONSTRUCTOR
   public Shield(Game g, int x, int y){
      
      this.g = g;
      
      //position and size of shields (there are 4)
      this.x = x;
      this.y = y;
      
      this.width = 144;
      this.height = 124;
      
            
      hp = 200;
      
      
      try{
      
         shield = ImageIO.read(getClass().getResourceAsStream("/Game Art/shield.png"));
      
      } catch (IOException e) {
      
         System.out.println("cannot read image");
      
      }
      
      
   
   }
   
   
   
   //SHIELD draw method
   public void draw(Graphics2D g2){
   
   //as long as the shield has more than 0 hp, it draws the image, 
   //and text showing how many hitpoints that shield has
   
      if(hp > 0){
      
         g2.drawImage(shield, this.x, this.y, this.width, this.height, null);
      
         g2.setFont(new Font("Courier", Font.PLAIN, 32));
         
         g2.setColor(Color.black);
         
         g2.drawString("HP: " + hp, this.x + 10, this.y + 50); 
         
         g2.setColor(Color.white);
      
      
      } else {
         
         //if shield is dead, it is condensed to nothing
         this.x = 0;
         this.y = 0;
         this.width = 0;
         this.height = 0;
      
      }
      
     
   
   
   }
   
   //if shield is hit by player or enemy bullet, then it loses health by 10 units
   public void weaken(){
   
      hp -= 10;
   
   
   }
   
}
