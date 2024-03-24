package pack;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.utility.Delay;

public class Wordle {
    private static EV3LargeRegulatedMotor beltMotor = new EV3LargeRegulatedMotor(MotorPort.D);
    private static EV3ColorSensor colorSensor = new EV3ColorSensor(LocalEV3.get().getPort("S3"));

    private static final int MAX_OBJECTS = 4;
    private static final int[] COLOR_POSITIONS = {0, 150, 330, 500}; // Correct position, correct color but wrong position, wrong color, initial position
    private static final int[] CORRECT_SEQUENCE = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
    private static int tries = 0; 
    private static int correctCount = 0;
    private static boolean guessCorrect = false;

    public static void main(String[] args) {
        LCD.drawString("Welcome to Lego Wordle!", 0, 0);
        Delay.msDelay(2000);
        
        LCD.drawString("You have four tries to guess", 0, 0); //explanation of game to user for clarity 
        LCD.drawString("the correct sequence of four colours.", 0, 2);
        Delay.msDelay(2000);

        LCD.drawString("The robot will scan each colour", 0, 0);
        LCD.drawString("and sort it based on the following factors:", 0, 2);
        Delay.msDelay(2000);

        LCD.drawString("Correct colour and correct position:", 0, 0);
        LCD.drawString("Sorted into left basket", 0, 2);
        Delay.msDelay(2000);

        LCD.drawString("Correct colour but incorrect position:", 0, 0);
        LCD.drawString("Sorted into middle basket", 0, 2);
        Delay.msDelay(2000);

        LCD.drawString("Incorrect colour:", 0, 0);
        LCD.drawString("Sorted into right basket", 0, 2);
        Delay.msDelay(2000);
        
        while (tries < 4 || guessCorrect == false) {
            resetMotors();
            int[] userSequence = new int[MAX_OBJECTS];
            LCD.clear();
            int colorCount = 0;

            // User scanning loop
            while (colorCount < MAX_OBJECTS && !Button.ENTER.isDown()) {
                if (Button.ESCAPE.isDown()) {
                    return; // Exit program immediately
                }
                int color = getColor();
                if (color != -1) { // -1 means no colour or unrecognised colour
                    userSequence[colorCount++] = color;
                    beepOnce();
                }
                Delay.msDelay(1000);
            }

            // Compare and sort loop
            for (int i = 0; i < MAX_OBJECTS; i++) {
                compareAndSortColor(userSequence, i);
            }

    
            LCD.drawString("Your guess: " + userSequence, 0, 0);
            LCD.drawString("Tries left: " + (4 - tries), 0, 1);
            resetMotors();
            beltMotor.rotateTo(COLOR_POSITIONS[0], true); //rotate belt motor back to the start after every guess round 
            

            if (correctCount == 4) { 
                LCD.drawString("Well done!", 0, 0);
                LCD.drawString("You have correctly guessed the color sequence:", 0, 2);
                LCD.drawString("" + CORRECT_SEQUENCE, 0, 3);
                guessCorrect = true; //exits while loop and game finishes 
            } else if ((tries == 4) && (guessCorrect == false)) { //handles scenario of user running out of tries 
                LCD.drawString("Sorry, you have run out of tries.", 0, 0);
                LCD.drawString("The correct color sequence was:", 0, 2);
                LCD.drawString("" + CORRECT_SEQUENCE, 0, 3);
            }
                

            // Reset for next round or exit
            if (Button.ESCAPE.isDown()) {
                break; // Exit program
            }

            tries++;
            LCD.clear();
            Delay.msDelay(2000); // Wait before the next attempt
        }
    }

    private static void compareAndSortColor(int[] userSequence, int index) {
        int colorId = userSequence[index];
        int correctColor = CORRECT_SEQUENCE[index];
        int position;

        if (colorId == correctColor) {
            position = COLOR_POSITIONS[0]; // Correct color, correct position
            correctCount++;
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
        beltMotor.rotateTo(position, false);
        while (beltMotor.isMoving()) {
            Delay.msDelay(100);
        }
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
