import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.Cursor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
/**
 * This class handles saving scores to "scores.dat" and displaying said scores on a new JFrame
 * 
 * @author CaDaMac
 * @version 1.0
 *
 */

public class ScoreKeeper{	
	private JFrame scoreDispFrame;
	private JTextArea scoresText;
	
	private static int saveScore;
	
	/**
	 * Make a new ScoreKeeper and the new score to "scores.dat" (if its high enough to be in the top 10)
	 * @param Score
	 */
	public ScoreKeeper(int points){
		System.out.println("points " + points);
		
		saveScore = points;
		new ScoreKeeper();
	}
	
	private ScoreKeeper(){
		saveTheScore();
		scoreDispFrame = new JFrame("High Scores");
		scoreDispFrame.setResizable(false);
		scoreDispFrame.setSize(210, 357);
		scoreDispFrame.setLayout(new FlowLayout());
		scoreDispFrame.setAlwaysOnTop(true);
		
		Color backGroudColor = new Color(239,239,239);
			scoresText = new JTextArea(getScores());
			scoresText.setFocusable(false);
			scoreDispFrame.add(scoresText);
			scoresText.setBackground(backGroudColor);
			scoresText.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
		scoreDispFrame.setVisible(true);
		
	}
	/*
	public static void main(String[] args){
		
		ScoreKeeper test = new ScoreKeeper(90, 9, 4);
		ScoreKeeper test2 = new ScoreKeeper();
	}
	*/
	
	
	private String getScores(){
		ArrayList<String> tmp = new ArrayList<String>();
		String ret = "";
		try {
			Scanner scan = new Scanner(new File("Galaga Scores.dat"));
			while(scan.hasNext()){
				tmp.add(scan.nextLine());
			}
		}
		catch (FileNotFoundException e) {
			return "No Scores";
		}
		for(String a: tmp){
			ret += a + "\n";
		}
		
		return ret;
	}
	/**
	 * Returns the highest score from the "scores.dat" file.<br>
	 * Returns 30000 if no file can be found.
	 * @return high score
	 */
	public static ArrayList<String> getHighScore(){
		ArrayList<String> tmp = new ArrayList<String>();
//		String ret = "";
		try {
			Scanner scan = new Scanner(new File("Galaga Scores.dat"));
			while(scan.hasNext()){
				tmp.add(scan.nextLine());
			}
		}
		catch (FileNotFoundException e) {
			tmp.add("30000");
			return tmp;
		}
		
		
		return tmp;
	}
	/**
	 * Sets the high score JFrame visible
	 * @param set visible
	 */
	public void setVis(boolean set){
		scoreDispFrame.setVisible(set);
	}
	
	private void saveTheScore(){
		ArrayList<Integer> scores = new ArrayList<Integer>();
		
		try {
			//Look for the current set of scores
			Scanner scan = new Scanner(new File("Galaga Scores.dat"));
			System.out.println("Galaga scrores file found 119");
			
			System.out.println("Has Lines" + scan.hasNext() + " Line: 121");
			while(scan.hasNext()){
				scores.add(Integer.parseInt(scan.nextLine()));
			} 
			System.out.println("scores: " + scores);
			try {
				
				PrintWriter fileOut = new PrintWriter(new FileWriter("Galaga Scores.dat"));
				int count = 0;
				//Prints out the higher scores
				System.out.println("scores.size() = " + scores.size());
				while(count < scores.size() && scores.get(count) > saveScore){
					fileOut.println(scores.get(count));
					System.out.println("Printed Scores: " + scores.get(count));
					count++;
				}
				//Prints out current Score if count is less then 10
				if(count < 10){
					System.out.println("Current Score: " + saveScore);
					fileOut.println(saveScore);
				}
				//Continues printing if a (was count) is less then 10
				for(int a = count; a < scores.size() && a < 10; a++){
					System.out.println("Printed Scores: " + scores.get(a));
					fileOut.println(scores.get(a));
				}
				fileOut.close();
				System.out.println("New File made");
			} 
			catch (IOException e) {
				//Not expected to hit. EVER. PrintWriter failed some how...
				System.out.println("File failed to print");
			}
		} 
		catch (FileNotFoundException e) {
			//Makes a new Score file if file not fount
			System.out.println("File not Found");
			try {
				//Prints out the only score
				PrintWriter fileOut = new PrintWriter(new FileWriter("Galaga Scores.dat"));
				fileOut.println(saveScore);
				
				fileOut.close();
			} 
			catch (IOException e1) {
				System.out.println("File Failed to write");
			}
			
		}
		//Done printing
	}
}
