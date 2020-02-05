package AssignmentPackage;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

class Symbol {
    public char ch;
    public double low; //low range
    public double high; //high range

    Symbol(){
        ch = ' ';
        low = 0.0; //initial value of low
        high = 1.0; //initial value of high
    }
}

public class Arihtmetic {
    public static Vector<Symbol> v = new Vector();

    public static void compression (String file) throws IOException {
       String input = new String (Files.readAllBytes(Paths.get(file))); //read all data from input.txt file
       Vector<Character> ch = new Vector();
       Vector<Double> prob = new Vector();

       for (int  i = 0 ; i < input.length () ; i++){
           char c = input.charAt(i); //get each character
           int idx = ch.indexOf(c); //get the index of that character
           if(idx == -1){ //if the character doesn't exist before
               ch.add(c); //add the character to the ch vector
               prob.add(1.0); //add 1 as it's exist 1 time
           }
           else {
               prob.set(idx,(prob.get(idx)+1)); //increment the counter of existence
           }
       }

       for (int i = 0 ; i < prob.size () ; i++){
           prob.set (i,(prob.get (i))/input.length()); //calculate the probability of each character
       }

       for (int  i = 0 ; i < ch.size () ; i++){  //arrange the characters and their probability from A -> z
           for (int  j = i+1 ; j < ch.size () ; j++){
               if (ch.get (i) > ch.get (j)){
                   char temp = ch.get(i);
                   ch.set (i, ch.get(j)); //swap between characters
                   ch.set (j,temp);
                   double temp2 = prob.get(i);
                   prob.set (i,prob.get(j)); //swap between probabilities of the characters
                   prob.set (j,temp2);
               }
           }
       }

       double sum = 0.0;
       for (int  i = 0 ; i < ch.size() ; i++){ //calculate accumulative probability scale
           Symbol s  = new Symbol ();
           v.add (s);
           v.get(i).ch = ch.get(i);
           v.get(i).low = sum;  //low at first will be the sum which is equal zero
           sum += prob.get (i);  //add the sum to the next probability in order to make accumulative probability scale
           v.get(i).high = sum;
       }

       double lower = 0.0; double upper = 0.0; double range = 0.0; double temp = 0.0; double  result = 0.0;
       int index = ch.indexOf (input.charAt (0));
       lower = v.get (index).low;
       upper = v.get (index).high;

       for (int  i = 1 ; i < input.length (); i++){
           range = upper - lower;
           temp = lower;
           index = ch.indexOf (input.charAt (i));
           lower = temp + (range * v.get(index).low); //lower  = lower + range * high_range(symbol)
           upper = temp + (range * v.get(index).high); //upper  = lower + range * low_range(symbol)
       }

       result = (lower + upper)/2; //get a range which will be the output (middle)
       String length = Integer.toString(input.length()); //get the length of the data and convert it to string to concatenate
       length += "\n";
       length += result;
       PrintWriter out = new PrintWriter("compression.txt");
       out.print(length);
       System.out.println (length);
        /*System.out.println(prob.get(0));
        System.out.println(prob.get(1));
        System.out.println(prob.get(2));*/
       out.flush();
       out.close();
    }

    public static void decompression (String file) throws IOException {
        String input = new String (Files.readAllBytes(Paths.get(file)));
        String s = "";
        int k;
        for( k = 0 ; k < input.length() ; k++ ){
            if( input.charAt(k) == '\n' ){
                k ++ ;
                break ;
            }
            s += input.charAt(k) ;
        }

        String s1 = input.substring(k) ;  //will take the values starting from index k
        int x = Integer.parseInt(s) ; //convert s string into integer
        double code = Double.parseDouble(s1) ; //convert s2 string into double
        String data = "" ;
        double lower = 0.0; double upper = 0.0; double range = 0.0; double temp = 0.0; double  result = 0.0; double rc = 0.0; //remainder code

        for( int i = 0 ; i < v.size() ; i++ ){
            if( code <= v.get(i).high ){ //if code less than high concatenate the data with this character
                data += v.get(i).ch ;
                lower = v.get(i).low ;
                upper = v.get(i).high ;
                break;
            }
        }

        for( int i = 1 ; i < x ; i++ ){
            rc = code - lower ;
            range = upper - lower ;
            result = rc/range ; //result = code - lower / range
            for( int j = 0 ; j < v.size(); j++ ){
                if( result <= v.get(j).high ){
                    data += v.get(j).ch ;
                    temp = lower ;
                    lower = temp + (range * v.get(j).low) ;
                    upper = temp + (range * v.get(j).high) ;
                    break ;
                }
            }
        }

        PrintWriter out = new PrintWriter("decompression.txt") ;
        out.print(data) ;
        System.out.println(data);
        out.flush() ;
        out.close() ;
    }

    public static void main (String[] args) throws IOException {
        compression("input.txt");
        decompression ("compression.txt");
        GUI gui=new GUI();
        gui.setVisible(true);
    }
}
