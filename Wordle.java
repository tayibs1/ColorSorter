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

public class ColorSorterRobot {
    private static EV3LargeRegulatedMotor beltMotor = new EV3LargeRegulatedMotor(MotorPort.D);
    private static EV3LargeRegulatedMotor feedMotor = new EV3LargeRegulatedMotor(MotorPort.A);
    private static EV3ColorSensor colorSensor = new EV3ColorSensor(LocalEV3.get().getPort("S3"));

    private static final int MAX_OBJECTS = 4;
    private static final int[] COLOR_POSITIONS = {0, 250, 450, 500}; // Correct position, correct color but wrong position, wrong color, initial position
    private static int[] CORRECT_SEQUENCE;
    private static int tries = 0;
    private static final int MAX_TRIES = 3; // Maximum number of tries
    private static boolean guessCorrect = false;

    public static void main(String[] args) {
        while (true) { // Allows multiple rounds of guessing
            CORRECT_SEQUENCE = createSequence();
            tries = 0; // Reset tries for the new round
            guessCorrect = false; // Reset guessCorrect for the new round
            LCD.clear();

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

                    // Remove the last scanned object if the left button is pressed
                    if (Button.LEFT.isDown()) {
                        if (colorCount > 0) {
                            colorCount--;
                            LCD.clear();
                            LCD.drawString("Last scan removed", 0, 0);
                            Delay.msDelay(1000); // Wait a bit for user to see message
                            LCD.clear();
                            continue; // Skip scanning and wait for next action
                        }
                    }

                    int color = getColor();
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
                    beltMotor.rotateTo(COLOR_POSITIONS[0]);
                }

                // Check guess correctness here (Implement your logic)
                guessCorrect = checkGuess(userSequence);
                tries++;

                // Wait a bit before next attempt
                Delay.msDelay(2000);
            }

            if (!guessCorrect) {
                // Display the correct sequence if the guess was incorrect
                displayCorrectSequence();
            }

            // Ask for replay or exit
            LCD.clear();
            LCD.drawString("Press ENTER to replay", 0, 5);
            LCD.drawString("ESC to exit", 0, 6);
            int buttonPress = Button.waitForAnyPress();
            if (buttonPress == Button.ID_ESCAPE) {
                break; // Exit the while loop and end the program
            }
        }
    }

    public static int[] createSequence() {
        int[] sequence = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
        shuffleArray(sequence); // Randomize the sequence
        return sequence;
    }

    private static void shuffleArray(int[] array) {
        Random rnd = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Swap
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
    
    private static void compareAndSortColor(int[] userSequence, int index) {
        int colorId = userSequence[index];
        int correctColor = CORRECT_SEQUENCE[index];
        int position;

        if (colorId == correctColor) {
            position = COLOR_POSITIONS[0]; // Correct color, correct position
            LCD.drawString(getColorName(colorId) + " is correct at " + index, 0, index + 2);
        } else if (containsColor(colorId)) {
            position = COLOR_POSITIONS[1]; // Correct color, wrong position
            LCD.drawString(getColorName(colorId) + " wrong position", 0, index + 2);
        } else {
            position = COLOR_POSITIONS[2]; // Wrong color
            LCD.drawString(getColorName(colorId) + " is wrong", 0, index + 2);
        }

        moveToColorPosition(position);
        Delay.msDelay(500); // Wait for sorting action
        ejectObject(); // Eject the object
        moveToColorPosition(COLOR_POSITIONS[3]); // Return to initial position
    }

    private static boolean containsColor(int colorId) {
        for (int color : CORRECT_SEQUENCE) {
            if (colorId == color) {
                return true;
            }
        }
        return false;
    }
    
    private static void ejectObject() {
        feedMotor.rotate(90); // Rotate the motor to eject the object
        Delay.msDelay(500); // Wait for half a second
        feedMotor.rotate(-90); // Rotate back to the original position
    }

    private static void resetMotors() {
        beltMotor.resetTachoCount();
    }

    private static int getColor() {
        int colorId = colorSensor.getColorID();
        return colorId;
    }

    private static void beepOnce() {
        Sound.playTone(1000, 100);
    }
    
    private static void moveToColorPosition(int position) {
        beltMotor.rotateTo(position, true);
        while (beltMotor.isMoving()) {
            Delay.msDelay(100);
        }
    }
    
    
    private static boolean checkGuess(int[] userSequence) {
        // Here, implement the logic to check if the user's guess is correct.
        // This is a placeholder implementation.
        return false; // For now, it always returns false.
    }

    private static void displayCorrectSequence() {
        LCD.clear();
        LCD.drawString("Correct sequence:", 0, 0);
        for (int i = 0; i < CORRECT_SEQUENCE.length; i++) {
            LCD.drawString(getColorName(CORRECT_SEQUENCE[i]), 0, i + 1);
        }
        Delay.msDelay(4000); // Display the correct sequence for a while
    }


    private static String getColorName(int colorId) {
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
}
