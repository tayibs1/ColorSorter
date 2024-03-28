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
    int getColorID();
    String getColorName(int colorId);
}

interface MotorController {
    void resetMotors();
    void moveToColorPosition(int position);
    void ejectObject();
}

interface GameLogic {
    boolean checkGuess(int[] userSequence);
    void displayCorrectSequence();
    int[] createSequence();
    void compareAndSortColor(int[] userSequence, int index);
}


public class WordleGame implements ColorScanner, MotorController, GameLogic {
    private EV3LargeRegulatedMotor beltMotor = new EV3LargeRegulatedMotor(MotorPort.D);
    private EV3LargeRegulatedMotor feedMotor = new EV3LargeRegulatedMotor(MotorPort.A);
    private EV3ColorSensor colorSensor = new EV3ColorSensor(LocalEV3.get().getPort("S3"));

    private final int MAX_OBJECTS = 4;
    private final int[] COLOR_POSITIONS = {0, 250, 450, 500}; // Positions for sorting
    private int[] correctSequence;
    private int tries = 0;
    private final int MAX_TRIES = 3; // Max number of tries
    private boolean guessCorrect = false;

    public static void main(String[] args) {
        try {
            WordleGame game = new WordleGame();
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
            correctSequence = createSequence();
            tries = 0;
            guessCorrect = false;

            while (tries < MAX_TRIES && !guessCorrect) {
                resetMotors();
                int[] userSequence = new int[MAX_OBJECTS];
                LCD.clear();
                int colorCount = 0;

                // User scanning loop
                while (colorCount < MAX_OBJECTS && !Button.ENTER.isDown()) {
                    if (Button.ESCAPE.isDown()) {
                        return; // Exit program immediately
                    }

                    if (Button.LEFT.isDown()) {
                        if (colorCount > 0) {
                            colorCount--;
                            LCD.clear();
                            LCD.drawString("Last scan removed", 0, 0);
                            Delay.msDelay(1000);
                            LCD.clear();
                            continue;
                        }
                    }

                    int color = getColorID();
                    if (color != -1) {
                        userSequence[colorCount] = color;
                        LCD.clear();
                        LCD.drawString(getColorName(color), 0, 0);
                        colorCount++;
                        beepOnce();
                    }
                    Delay.msDelay(1000);
                }

                // Compare and sort loop
                for (int i = 0; i < colorCount; i++) {
                    compareAndSortColor(userSequence, i);
                }

                guessCorrect = checkGuess(userSequence);
                tries++;
            }

            if (guessCorrect) {
                LCD.clear();
                LCD.drawString("Well done!", 0, 0);
                LCD.drawString("You Guessed Right!", 0, 1);
            } else {
                displayCorrectSequence();
                LCD.clear();
                LCD.drawString("You failed!", 0, 0);
            }

            LCD.drawString("Press ENTER ", 0, 5);
            LCD.drawString("to Replay ", 0, 6);
            LCD.drawString("ESC to exit", 0, 7);
            int buttonPress = Button.waitForAnyPress();
            if (buttonPress == Button.ID_ESCAPE) {
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
            return colorSensor.getColorID();
        } catch (Exception e) {
            displayErrorMessage("Sensor error");
            return -1; // Return a default value indicating failure.
        }
    }

    @Override
    public String getColorName(int colorId) {
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
        beltMotor.resetTachoCount();
    }

    @Override
    public void moveToColorPosition(int position) {
        beltMotor.rotateTo(position, true);
        while (beltMotor.isMoving()) {
            Delay.msDelay(100);
        }
    }

    @Override
    public void ejectObject() {
        feedMotor.rotate(90);
        Delay.msDelay(500);
        feedMotor.rotate(-90);
    }

    @Override
    public boolean checkGuess(int[] userSequence) {
        if (userSequence.length != correctSequence.length) {
            return false;
        }
        for (int i = 0; i < correctSequence.length; i++) {
            if (userSequence[i] != correctSequence[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void displayCorrectSequence() {
        LCD.clear();
        LCD.drawString("Correct sequence:", 0, 0);
        for (int i = 0; i < correctSequence.length; i++) {
            LCD.drawString(getColorName(correctSequence[i]), 0, i + 1);
        }
        Delay.msDelay(4000);
    }

    @Override
    public int[] createSequence() {
        int[] sequence = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
        shuffleArray(sequence);
        return sequence;
    }

    private void shuffleArray(int[] array) {
        Random rnd = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    @Override
    public void compareAndSortColor(int[] userSequence, int index) {
        int colorId = userSequence[index];
        int correctColor = correctSequence[index];
        int position;

        if (colorId == correctColor) {
            position = COLOR_POSITIONS[0];
            LCD.drawString(getColorName(colorId) + " is correct at " + index, 0, index + 2);
        } else if (containsColor(colorId)) {
            position = COLOR_POSITIONS[1];
            LCD.drawString(getColorName(colorId) + " wrong position", 0, index + 2);
        } else {
            position = COLOR_POSITIONS[2];
            LCD.drawString(getColorName(colorId) + " is wrong", 0, index + 2);
        }

        moveToColorPosition(position);
        Delay.msDelay(500);
        ejectObject();
        moveToColorPosition(COLOR_POSITIONS[0]);
    }

    private boolean containsColor(int colorId) {
        for (int color : correctSequence) {
            if (colorId == color) {
                return true;
            }
        }
        return false;
    }

    private void beepOnce() {
        Sound.playTone(1000, 100);
    }
}
