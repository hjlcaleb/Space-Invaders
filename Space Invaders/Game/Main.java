import javax.swing.*; //swing allows for implementation of JFrame and JPanel
/*
BASIC GAME MECHANICS:
- left and right arrow to move player left and right
- space bar to shoot bullet
- W to switch game mode to woffinden-mode
- S to switch back to normal mode
*/


//implements new JFrame - adjusting settings
public class Main { 

   public static void main(String[] args){
    
      //instantiate new JFrame
      JFrame window = new JFrame("SPACE INVADERS"); 
      
      
      //adjusting screen settings of JFrame - more settings for actual Game()
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      window.setResizable(false); 
      
      
      //new game object, added to window - manages main mechanics
      Game game = new Game();
      window.add(game);
      
      
      //adjusts contents of game to fit dimensions of JFrame
      window.pack();
        
    
      //will by default move JFrame to center of screen
      window.setLocationRelativeTo(null); 
      
      
      //sets contents of window and Game() to be visible
      window.setVisible(true);  
      
      
      // run method in Game
      game.startThread();
      
     
   }



}