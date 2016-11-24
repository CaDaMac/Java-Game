import java.applet.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.*;
import java.io.*;
/**
 * Handles all sound playing.
 * @author CaDaMac
 * @version 1.0
 *
 */
public class soundPlayer {
	private TreeMap<String, AudioClip> songs;
	private URL songPath;
	
	/**
	 * Creates a new soundPlayer
	 */
	public soundPlayer() {
		System.out.println("NEW SOUNDPLAYER MADE");
		songs = new TreeMap<String, AudioClip>();
	}
	/*
	public static void main(String[] args) {
		soundPlayer s = new soundPlayer();
		//s.add("Equinox");
		s.add("gun");
		//s.add("gun");
		for(int a = 0; a < 120; a++){
			s.justPlay();
			
			System.out.println(s.toString());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	*/
	/**
	 * Adds and plays a new sound
	 * @param sound name
	 */
	public void add(String sound) {
		try {
			songPath = new URL("file:" + sound + ".wav");
			songs.put(sound, Applet.newAudioClip(songPath));
			System.out.println("Added new sound: " + sound);
		} 
		catch (MalformedURLException e) {
			System.err.println("The Sound Wasn't found");
		}
		songs.get(sound).play();
	}
	/**
	 * Stops and removes a sound
	 * @param sound name
	 */
	public void remove(String sound) {
		for(String s: songs.keySet()) {
			if(s.equals(sound)) {
				songs.remove(s);
			}
		}
	}
	/**
	 * Adds and plays a sound. Plays in a loop
	 * @param sound name
	 */
	public void addLoopingSound(String sound) {
		try {
			songPath = new URL("file:" + sound + ".wav");
			songs.put(sound, Applet.newAudioClip(songPath));
		} 
		catch (MalformedURLException e) {
			System.err.println("The Sound Wasn't found");
		}
		songs.get(sound).loop();
	}
	/**
	 * Plays all sounds in current list. Restarts currently playing sound
	 */
	public void justPlay() {
		for(String a: songs.keySet()){
			songs.get(a).play();
		}
	}
	/**
	 * Stops the playback of all current sounds
	 */
	public void pause(){
		for(String a: songs.keySet()){
			songs.get(a).stop();
		}
	}
	/*
	public void fastForward(double seconds){
		System.err.println("S.O.L. MUTHAH BUCKAH!!!!");
		System.exit(3245);
	}
	*/
}
