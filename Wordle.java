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
    private static int[] userSequence = new int[MAX_OBJECTS];

    public static void main(String[] args) {
        int attempts = 0;
        
        while (true) {
            resetMotors();
            LCD.clear();
            LCD.drawString("Attempt: " + attempts, 0, 0);
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
                compareAndSortColor(i);
            }

            // Reset for next round or exit
            if (Button.ESCAPE.isDown()) {
                break; // Exit program
            }

            attempts++;
            LCD.clear();
            Delay.msDelay(1000);
        }
    }

    private static void compareAndSortColor(int index) {
        int colorId = userSequence[index];
        int correctColor = CORRECT_SEQUENCE[index];
        int position;

        if (colorId == correctColor) {
            position = COLOR_POSITIONS[0]; // Correct color, correct position
            LCD.drawString(getColorName(colorId) + " is correct at " + index, 0, index + 1);
        } else if (containsColor(colorId)) {
            position = COLOR_POSITIONS[1]; // Correct color, wrong position
            LCD.drawString(getColorName(colorId) + " wrong position", 0, index + 1);
        } else {
            position = COLOR_POSITIONS[2]; // Wrong color
            LCD.drawString(getColorName(colorId) + " is wrong", 0, index + 1);
        }

        moveToColorPosition(position);
        Delay.msDelay(500); // Wait for sorting action
        moveToColorPosition(COLOR_POSITIONS[3]); // Return to initial position
    }

    // Checks if the given color is in the correct sequence
    private static boolean containsColor(int colorId) {
        for (int color : CORRECT_SEQUENCE) {
            if (colorId == color) {
                return true;
            }
        }
        return false;
    }

    // Reset motors, clear screen, etc.
    private static void resetMotors() {
        beltMotor.resetTachoCount();
    }

    // Method to get color, beepOnce(), moveToColorPosition(int position), getColorName(int colorId) remain the same
}
