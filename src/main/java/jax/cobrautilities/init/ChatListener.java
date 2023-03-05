package jax.cobrautilities.init;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jax.cobrautilities.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatListener implements IEventListener {

    String originalString = "";
    String searchString = "word: ";
    String mcpath = Minecraft.getMinecraft().mcDataDir.getAbsolutePath();
    private long waitTimeMillis = 6000;
    
    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) throws IOException {

        IChatComponent chat = event.message;
        String unformatted = chat.getUnformattedText();
        String formatted = chat.getFormattedText();
        
        //do equation
        if (unformatted.toLowerCase().contains("equation: ")) {
        	long equationStartTime = System.currentTimeMillis();
            String equation = unformatted.toLowerCase();
            equation = equation.replaceAll("<.*?> ", "");
            equation = equation.substring(23);
            System.out.println(equation);
            final double result = solveEquation(equation);
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BLUE + "value of equation is: " +String.valueOf(result)));
            long equationEndTime = System.currentTimeMillis();
            final double duration = Math.round(((equationEndTime+waitTimeMillis) - equationStartTime));
            Timer equationtimer = new Timer();
            equationtimer.schedule(new TimerTask() {
                @Override
                public void run() {
                	Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "Time to solve equation: " + duration));
                    Minecraft.getMinecraft().thePlayer.sendChatMessage(Double.toString(result));
                }
            }, waitTimeMillis);
        }
        
        //unscramble word
        if (unformatted.toLowerCase().contains("word: ")) {
        	long startTime = System.currentTimeMillis();
            String scrambledWord = getScrambledWord(unformatted.toLowerCase());
            waitTimeMillis = scrambledWord.length() * 350;
            if (scrambledWord.length() > 8) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BLUE + "word over 8 char, not unscrambling"));
            } else {
                final String unscrambled = unscrambleWord(scrambledWord);
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "scrambled word is: " + scrambledWord));
                if (unscrambled == null) {
                    System.out.println("unscrambled is null");
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "[CU] " + EnumChatFormatting.RED.BOLD + "unscrambled word does " + EnumChatFormatting.DARK_RED.BOLD.UNDERLINE + "not exist " + EnumChatFormatting.RED.BOLD + "in dictionary or was" + EnumChatFormatting.DARK_RED.BOLD.UNDERLINE + "null"));
                } else {
                    System.out.println("client should have sent unscrambled message: " + unscrambled);
                    long endTime = System.currentTimeMillis();
                    final double duration = Math.round(((endTime+waitTimeMillis) - startTime));
                    // Use a TimerTask to delay sending the unscrambled word by 1 second
                    TimerTask task = new TimerTask() {
                        public void run() {
                            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BLUE + "debug timer: " + String.valueOf(duration) + " milliseconds, the word was: " + unscrambled));
                            Minecraft.getMinecraft().thePlayer.sendChatMessage(unscrambled); // send unscrambled word to chat
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, waitTimeMillis);
                }
            }
        }
        
        if (unformatted.toLowerCase().contains(" has won the chat game in ") || unformatted.toLowerCase().contains("The event has ended and unfortunately no one got the word in time.")) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(mcpath + "/config/cu/wordlog.txt", true));
            String addtodict = unformatted.substring(unformatted.lastIndexOf(" ") + 1);
            String removeperiod = addtodict.replace(".", "");
            writer.write(System.lineSeparator() + removeperiod);
            writer.close();
        }
    }

    public static double solveEquation(String equation) {
        //still in string, extract numbers, example equation to know where we're at (8 * 8) / (6 + 2)
    	//first brackets
    	//first num
    	int firstnum = Integer.parseInt(equation.substring(1, 2));
    	//first operator
    	String firstoper = equation.substring(3,4);
    	//second num
    	int secondnum = Integer.parseInt(equation.substring(5,6));
    	//first equation in brackets(8 * 8)
    	double firstequation = 0;
    	//find the operator for first equation
    	switch(firstoper) { 
    	case "+":
    		firstequation = firstnum + secondnum;
    		break;
        case "-":
            firstequation = firstnum - secondnum;
            break;
        case "*":
        	firstequation = firstnum * secondnum;
        	break;
        case "/":
        	firstequation = firstnum / secondnum;
        	break;
        case "^":
        	firstequation = Math.pow(firstnum, secondnum);
        	break;
    	}
    	
    	//second brackets
    	//third num
    	int thirdnum = Integer.parseInt(equation.substring(11,12));
    	//first operator
    	String secondoper = equation.substring(13,14);
    	//second num
    	int fourthnum = Integer.parseInt(equation.substring(15,16));
    	//first equation in brackets(8 * 8)
    	double secondequation = 0;
    	//find the operator for first equation
    	switch(secondoper) { 
    	case "+":
    		secondequation = thirdnum + fourthnum;
    		break;
        case "-":
        	secondequation = thirdnum - fourthnum;
        	break;
        case "*":
        	secondequation = thirdnum * fourthnum;
        	break;
        case "/":
        	secondequation = thirdnum / fourthnum;
        	break;
        case "^":
        	secondequation = Math.pow(thirdnum, fourthnum);
        	break;
    	}
    	
    	//middle operator
    	String middleoper = equation.substring(8,9);
    	double finalanswer = 0;
    	switch(middleoper) {
    	case "+":
    		finalanswer = firstequation + secondequation;
    		break;
        case "-":
        	finalanswer = firstequation - secondequation;
        	break;
        case "*":
        	finalanswer = firstequation * secondequation;
        	break;
        case "/":
        	finalanswer = firstequation / secondequation;
        	break;
        case "^":
        	finalanswer = Math.pow(firstequation, secondequation);
        	break;
    	}
    	return finalanswer;
    }
    
    //removes all the text before our scrambled word
    public String getScrambledWord(String message) {
        // get text after "word: "
        message = message.replaceAll("<.*?> ", "");
        return message.substring(19);
    }

    //unscambles the word
    public String unscrambleWord(String scrambled) {
    	ArrayList<String> itsthisone = new ArrayList<String>();
        ArrayList<String> possibleWords = new ArrayList<String>();
        ArrayList<String> dictionaryWords = new ArrayList<String>();
        ArrayList<String> knownWords = new ArrayList<String>();

        // get input stream of dictionary file after downloading it
        String filename = mcpath + "/config/cu/moby.txt";
        String knownWordFile = mcpath + "/config/cu/knownwords.txt";
        
        try (BufferedReader bufferedReader0 = new BufferedReader(new FileReader(knownWordFile))) {
        	String bla;
        	while ((bla = bufferedReader0.readLine()) != null) {
        		knownWords.add(bla.toLowerCase());
        	}
        } catch (IOException e1) {
        	System.out.println("error reading knownwords file");
        	e1.printStackTrace();
        }
        

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                dictionaryWords.add(line.toLowerCase());
            }
        } catch (IOException e) {
            System.out.println("Error reading dictionary file");
            e.printStackTrace();
        }

        // generate all possible permutations of the scrambled word
        ArrayList<String> permutations = generatePermutations(scrambled);
        
        
        // find all permutations that are in our dictionary
        for (String permutation : permutations) {
        	if (knownWords.contains(permutation)) {
        		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_GREEN + "word was known: " + permutation));
        		return permutation;
        	}
            if (dictionaryWords.contains(permutation)) {
                possibleWords.add(permutation);
            }
        }

        // sort possibleWords by length in ascending order
        Collections.sort(possibleWords, wordComparator);

        // return first word in possibleWords
        if (!possibleWords.isEmpty()) {
            return possibleWords.get(0);
        } else {
            return null;
        }
    }

    //packages the words from the helper into an arraylist
    private ArrayList<String> generatePermutations(String str) {
        ArrayList<String> permutations = new ArrayList<String>();
        generatePermutationsHelper("", str, permutations);
        return permutations;
    }

    //randomizes the letters
	private void generatePermutationsHelper(String prefix, String str, ArrayList<String> permutations) {
	    int n = str.length();
	    if (n == 0) {
	        permutations.add(prefix);
	    } else {
	        for (int i = 0; i < n; i++) {
	            generatePermutationsHelper(prefix + str.charAt(i), str.substring(0, i) + str.substring(i + 1, n), permutations);
	        }
	    }
	}

	Comparator<String> wordComparator = new Comparator<String>() {
	    public int compare(String word1, String word2) {
	        return Integer.compare(word1.length(), word2.length());
	    }
	};
	
	@Override
	public void invoke(Event event) {
		// TODO Auto-generated method stub
		
	}
}