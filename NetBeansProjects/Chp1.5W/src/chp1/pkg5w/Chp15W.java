
package chp1.pkg5w;

import java.util.Scanner;

/**
 *
 * @author CEOCarlAllen
 */
public class Chp15W {

    
    public static void main(String[] args) {
        
        // created a Object ot use the Scanner
        Scanner keyboard = new Scanner(System.in);
        
        // Declared a boolean variable and a double variable to hold 
        boolean outCome = true; 
        double x, y;
        
        
      
         System.out.println("Enter a number between 0 and 1: ");
            x = keyboard.nextDouble();
        
         System.out.println("Enter another number between 0 and 1: "); 
            y = keyboard.nextDouble(); 
        
    
              // wrote a if condition to check if the number entered by the
              // 
               if (x < 0 || x > 1 && y < 0 || y > 1){
                   System.out.println("false");
                   }
                   else {
                   System.out.println(outCome); 
                 }
        
   }
}

  

