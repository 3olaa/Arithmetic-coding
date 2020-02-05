package AssignmentPackage;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

public class GUI extends JFrame {

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
        for (int  i = 0 ; i < ch.size () ; i++){ //calculate accumulative probability scale
            Symbol s  = new Symbol ();
            v.add (s);
            v.get(i).ch = ch.get(i);
            v.get (i).low = sum;  //low at first will be the sum which is equal zero
            sum += prob.get (i);  //add the sum to the next probability in order to make accumulative probability scale
            v.get (i).high = sum;
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
        //System.out.println (length);
        out.flush();
        out.close();
    }

    public static void decompression (String file) throws IOException {
        String input = new String (Files.readAllBytes(Paths.get("compression.txt")));
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
        int x = Integer.parseInt(s) ; //convert s1 string into integer
        double code = Double.parseDouble(s1) ; //convert s2 string into double
        String data = "" ;
        double lower = 0.0; double upper = 0.0; double range = 0.0; double temp = 0.0; double  result = 0.0; double rc = 0.0;

        for( int i = 0 ; i < v.size() ; i++ ){
            if( code <= v.get(i).high ){
                data += v.get(i).ch ;
                lower = v.get(i).low ;
                upper = v.get(i).high ;
                break ;
            }
        }

        for( int i = 1 ; i < x ; i++ ){
            rc = code - lower ;
            range = upper - lower ;
            result = rc/range ;
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

        PrintWriter out = new PrintWriter(file) ;
        out.print(data) ;
        //System.out.println(data);
        out.flush() ;
        out.close() ;
    }

    private JTextField textField;
    private JTextField textField_1;

    public static void main(String[] args){

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GUI window = new GUI();
                    window.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public GUI() {
        // add(this.root);
        setTitle("GUI OF ARITHMETIC");
        setBounds(100, 100, 450, 300);

        textField = new JTextField();
        textField.setColumns(10);


        textField_1 = new JTextField();
        textField_1.setColumns(10);

        JLabel label = new JLabel("Write the path of the file you want to compress please");
        label.setFont(new Font("Tahoma", Font.PLAIN, 12));

        JLabel label2 = new JLabel("Write the path of the file you want to decompress in");
        label2.setFont(new Font("Tahoma", Font.PLAIN, 12));

        JButton btnCompress = new JButton("Compress");

        btnCompress.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String s = textField.getText();

                try {
                   compression (s);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        btnCompress.setFont(new Font("Tahoma", Font.PLAIN, 16));

        JButton
                btnDecompress = new JButton("Decompress");
        btnDecompress.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String s1 = textField_1.getText();

                try {
                    decompression (s1);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        btnDecompress.setFont(new Font("Tahoma", Font.PLAIN, 16));
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(35)
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addComponent(label2, GroupLayout.PREFERRED_SIZE, 295, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(textField_1, GroupLayout.PREFERRED_SIZE, 294, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(textField, GroupLayout.PREFERRED_SIZE, 294, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
                                                .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
                                                        .addComponent(btnCompress, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(btnDecompress, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE))
                                                .addComponent(label, Alignment.LEADING)))
                                .addContainerGap(95, Short.MAX_VALUE))
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(23)
                                .addComponent(label)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(textField, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
                                .addGap(26)
                                .addComponent(label2, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(textField_1, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addComponent(btnCompress, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnDecompress, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE))
                                .addGap(25))
        );
        getContentPane().setLayout(groupLayout);
    }
}


