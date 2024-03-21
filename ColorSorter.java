package pack;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.Color;
import lejos.utility.Delay;

public class ABC {
    private static EV3LargeRegulatedMotor beltMotor = new EV3LargeRegulatedMotor(MotorPort.D);
    private static EV3LargeRegulatedMotor feedMotor = new EV3LargeRegulatedMotor(MotorPort.A);
    private static EV3TouchSensor touchSensor = new EV3TouchSensor(LocalEV3.get().getPort("S1"));
    private static EV3ColorSensor colorSensor = new EV3ColorSensor(LocalEV3.get().getPort("S3"));

    private static final int MAX_OBJECTS = 4;
    private static final int[] COLOR_POSITIONS = {0, 150, 330, 500}; // red, green, blue, yellow


    public static void main(String[] args) {
        while (true) {
            // Initialise
            resetMotors();
            int[] colorList = new int[MAX_OBJECTS];
            int colorCount = 0;

            // Scanning loop
            while (colorCount < MAX_OBJECTS && !Button.ENTER.isDown()) {
                if (Button.ESCAPE.isDown()) {
                    return; // Exit program immediately
                }
                int color = getColor();
                if (color != -1) { // -1 means no colour or unrecognised colour
                    colorList[colorCount++] = color;
                    beepOnce();
                }
                Delay.msDelay(1000);
            }

            // Sorting loop
            for (int i = 0; i < colorCount; i++) {
                sortColor(colorList[i]);
                ejectObject();
            }
            
            // Move to start position after sorting
            moveToColorPosition(0);

            // Check for back button press to exit or continue for a new round of scanning and sorting
            if (Button.ESCAPE.isDown()) {
                break; // Exit program
            }

            LCD.clear();
            // Optional: Add a small delay or a beep here to indicate readiness for next round
            Delay.msDelay(1000);
        }
    }

    private static void resetMotors() {
        beltMotor.resetTachoCount();
        feedMotor.resetTachoCount();
    }

    private static int getColor() {
        int colorId = colorSensor.getColorID();
        return colorId;
    }

    private static void sortColor(int colorId) {
        LCD.clear();
        LCD.drawString("Sorting Color: " + colorId, 0, 0);
        switch (colorId) {
            case Color.RED:
                moveToColorPosition(COLOR_POSITIONS[0]);
                break;
            case Color.GREEN:
                moveToColorPosition(COLOR_POSITIONS[1]);
                break;
            case Color.BLUE:
                moveToColorPosition(COLOR_POSITIONS[2]);
                break;
            case Color.YELLOW:
                moveToColorPosition(COLOR_POSITIONS[3]);
                break;
            default:
                LCD.drawString("Unknown Color", 0, 1);
                break;
        }
    }

    private static void moveToColorPosition(int position) {
        beltMotor.rotateTo(position, true);
        while (beltMotor.isMoving()) {
            Delay.msDelay(1000);
        }
    }

    private static void ejectObject() {
        feedMotor.rotate(90);
        Delay.msDelay(500);
        feedMotor.rotate(-90);
    }

    private static void beepOnce() {
        Sound.playTone(1000, 100);
    }
}
