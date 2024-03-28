package pack;

import java.util.Random;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.utility.Delay;

interface ColorScanner {
    int getColorID(); // Method to get the color ID 
    String getColorName(int colorId); // Method to get the color name based on the color ID 
}

interface MotorController {
    void resetMotors();
    void moveToColorPosition(int position); // Method to move to a specified color position
    void ejectObject();
}

interface GameLogic {
    boolean checkGuess(int[] userSequence); // Method to check the user's guess 
    void displayCorrectSequence();
    int[] createSequence(); // Method to create a random color sequence 
    void compareAndSortColor(int[] userSequence, int index); // Method to compare and sort colors 
}

// Defines the main class for the Wordle game implementing the ColorScanner, MotorController, and GameLogic interface 
public class WordleGame implements ColorScanner, MotorController, GameLogic {
    private EV3LargeRegulatedMotor beltMotor = new EV3LargeRegulatedMotor(MotorPort.D);
    private EV3LargeRegulatedMotor feedMotor = new EV3LargeRegulatedMotor(MotorPort.A);
    private EV3ColorSensor colorSensor = new EV3ColorSensor(LocalEV3.get().getPort("S3"));

    private final int MAX_OBJECTS = 4; // Only allows user to scan four pieces at a time 
    private final int[] COLOR_POSITIONS = {0, 250, 450, 500}; // Positions for sorting based on the correctness of the guess
    private int[] correctSequence; // Array to store the correct color sequence 
    private int tries = 0;
    private final int MAX_TRIES = 3; // Max number of tries
    private boolean guessCorrect = false; // Flag to indicate if the user's guess is correct 

    public static void main(String[] args) {
        try {
            WordleGame game = new WordleGame(); // Creates an instance of WordleGame and start the game 
            game.run();
        } catch (Exception e) {
            LCD.clear();
            LCD.drawString("Error:", 0, 0);
            LCD.drawString(e.getMessage(), 0, 1);
        }
    }

    public void run() {
      try {
    	  
	    if (checkBatteryAndDisplayIfLow()) {
	        return; // Exit the program if battery is low
	    }
        // Welcome screen
        LCD.clear();
        LCD.drawString("Welcome to Wordle v1.4", 0, 0);
        LCD.drawString("Tayib, Brilanta,", 0, 1);
        LCD.drawString("Aymen, Ishfaq", 0, 2);
        LCD.drawString("Press any button to start", 0, 4);
        Button.waitForAnyPress();
        LCD.clear();

        while (true) {
        	
            if (checkBatteryAndDisplayIfLow()) {
                break; // Break out of the game loop if the battery is low
            }
            correctSequence = createSequence(); // Generates a random color sequence 
            tries = 0; // Resets the number of tries 
            guessCorrect = false; // Resets the guess status 

            while (tries < MAX_TRIES && !guessCorrect) {
		// Resets motors and variables 
                resetMotors();
                int[] userSequence = new int[MAX_OBJECTS];
                LCD.clear();
                int colorCount = 0;

                // User scanning loop
                while (colorCount < MAX_OBJECTS && !Button.ENTER.isDown()) {
                    if (Button.ESCAPE.isDown()) {
                        return; // Exit program immediately
                    }

                    if (Button.LEFT.isDown()) { // Allows user to re-scan a piece if left button is pressed 
                        if (colorCount > 0) {
                            colorCount--;
                            LCD.clear();
                            LCD.drawString("Last scan removed", 0, 0);
                            Delay.msDelay(1000);
                            LCD.clear();
                            continue;
                        }
                    }

                    int color = getColorID(); // Retrieves the color ID from the color sensor 
                    if (color != -1) { // If the color is recognised: 
                        userSequence[colorCount] = color; // Stores the color in the user's sequence array 
                        LCD.clear();
                        LCD.drawString(getColorName(color), 0, 0); // Displays color name on LCD screen 
                        colorCount++;
                        beepOnce();
                    }
                    Delay.msDelay(1000);
                }

                // Compare and sort loop
                for (int i = 0; i < colorCount; i++) {
                    compareAndSortColor(userSequence, i); // Compares and sorts each color in the user's sequence 
                }

                guessCorrect = checkGuess(userSequence); // Checks if user's guess is correct
                tries++; // Increments the number of tries 
            }
	
            if (guessCorrect) {
                LCD.clear();
                LCD.drawString("Well done!", 0, 0);
                LCD.drawString("You Guessed Right!", 0, 1);  // Displays the result of the guess 
            } else {
                displayCorrectSequence();
                LCD.clear();
                LCD.drawString("You failed!", 0, 0);
            }
	
            LCD.drawString("Press ENTER ", 0, 5); // Prompts user to replay or exit 
            LCD.drawString("to Replay ", 0, 6);
            LCD.drawString("ESC to exit", 0, 7);
            int buttonPress = Button.waitForAnyPress(); // Waits for a button to be pressed 
            if (buttonPress == Button.ID_ESCAPE) { // Exits loop and ends program if escape button is pressed 
                break;
            }
        }} catch (Exception e) {
            displayErrorMessage(e.getMessage());
        }
    }
    
    private boolean checkBatteryAndDisplayIfLow() {
        double voltage = LocalEV3.get().getPower().getVoltage();
        if (voltage < 6.0) {
            LCD.clear();
            LCD.drawString("Low Battery Voltage", 0, 0);
            Delay.msDelay(2000); // Display the message for 2 seconds before continuing/returning.
            return true;
        }
        return false;
    }
    
    private void displayErrorMessage(String message) {
        LCD.clear();
        LCD.drawString("Error:", 0, 0);
        LCD.drawString(message, 0, 1);
        Delay.msDelay(2000); // Show the error for 2 seconds
    }

    @Override
    public int getColorID() {
        try {
            return colorSensor.getColorID(); // Returns the color ID detected by the color sensor 
        } catch (Exception e) {
            displayErrorMessage("Sensor error");
            return -1; // Returns a default value indicating failure
        }
    }

    @Override
    public String getColorName(int colorId) { // Returns name of the color corresponding to the given color ID 
        switch (colorId) {
            case Color.NONE: return "NONE";
            case Color.BLACK: return "BLACK";
            case Color.BLUE: return "BLUE";
            case Color.GREEN: return "GREEN";
            case Color.YELLOW: return "YELLOW";
            case Color.RED: return "RED";
            case Color.WHITE: return "WHITE";
            case Color.BROWN: return "BROWN";
            default: return "UNKNOWN";
        }
    }

    @Override
    public void resetMotors() {
        beltMotor.resetTachoCount(); // Resets the tachometer count for the belt motor 
    }

    @Override
    public void moveToColorPosition(int position) {
        beltMotor.rotateTo(position, true); // Rotates the belt motor to the specified position 
        while (beltMotor.isMoving()) {
            Delay.msDelay(100); // Delays for 100 milliseconds 
        }
    }

    @Override
    public void ejectObject() {
        feedMotor.rotate(90);  // Rotates the feed motor to eject the object 
        Delay.msDelay(500);
        feedMotor.rotate(-90); // Rotates the feed motor back to the original position 
    }

    @Override
    public boolean checkGuess(int[] userSequence) {
        if (userSequence.length != correctSequence.length) {
            return false; // Returns false if the lengths of the user's sequence and the correct sequence do not match 
        }
        for (int i = 0; i < correctSequence.length; i++) {
            if (userSequence[i] != correctSequence[i]) {
                return false; // Returns false if any color in the user's sequence does not match the correct sequence 
            }
        }
        return true; // Returns true if the user's sequence matches the correct sequence 
    }

    @Override
    public void displayCorrectSequence() {
        LCD.clear();
        LCD.drawString("Correct sequence:", 0, 0); // Displays a message indicating the correct sequence 
        for (int i = 0; i < correctSequence.length; i++) {
            LCD.drawString(getColorName(correctSequence[i]), 0, i + 1); // Displays each color in the correct sequence 
        }
        Delay.msDelay(4000);
    }

    @Override
    public int[] createSequence() {
        int[] sequence = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW}; // Creates a sequence of colors 
        shuffleArray(sequence); // Shuffles the sequence randomly
        return sequence;
    }

    private void shuffleArray(int[] array) {
        Random rnd = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1); // Generates a random index within the array bounds 
            int temp = array[index];  // Swaps the elements 
            array[index] = array[i];
            array[i] = temp;
        }
    }

    @Override
    public void compareAndSortColor(int[] userSequence, int index) {
        int colorId = userSequence[index]; // Retrieves the color ID from the user's sequence
        int correctColor = correctSequence[index]; // Retrieves the correct color from the correct sequence 
        int position;

        if (colorId == correctColor) {
            position = COLOR_POSITIONS[0]; // Sets position to indicate correct color and correct position
            LCD.drawString(getColorName(colorId) + " is correct at " + index, 0, index + 2);
        } else if (containsColor(colorId)) {
            position = COLOR_POSITIONS[1]; // Sets position to indicate correct color but wrong position
            LCD.drawString(getColorName(colorId) + " wrong position", 0, index + 2);
        } else {
            position = COLOR_POSITIONS[2]; // Sets position to indicate wrong color
            LCD.drawString(getColorName(colorId) + " is wrong", 0, index + 2);
        }

        moveToColorPosition(position); // Moves the belt motor to the specified position
        Delay.msDelay(500);
        ejectObject();
        moveToColorPosition(COLOR_POSITIONS[0]); // Moves the belt motor to the initial position
    }

    private boolean containsColor(int colorId) {
        for (int color : correctSequence) {
            if (colorId == color) {
                return true; // Returns true if the correct sequence contains the specified color
            }
        }
        return false; // Returns false if the correct sequence does not contain the specified color
    }

    private void beepOnce() {
        Sound.playTone(1000, 100); // Emits a beep sound of 1000 Hz for 100 milliseconds 
    }
}
