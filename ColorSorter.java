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

public class ColorSorter {
    private static EV3LargeRegulatedMotor beltMotor = new EV3LargeRegulatedMotor(MotorPort.D);
    private static EV3LargeRegulatedMotor feedMotor = new EV3LargeRegulatedMotor(MotorPort.A);
    private static EV3TouchSensor touchSensor = new EV3TouchSensor(LocalEV3.get().getPort("S1"));
    private static EV3ColorSensor colorSensor = new EV3ColorSensor(LocalEV3.get().getPort("S3"));

    private static final int MAX_OBJECTS = 4; // Defines the maximum number of objects the user is allowed to scan
    private static final int[] COLOR_POSITIONS = {0, 150, 330, 500}; // Defines positions for each color in the order of red, green, blue, yellow

    public static void main(String[] args) {
        showWelcomeScreen(); // Displays the welcome screen and waits for any button press to continue
        
        while (true) { // Allows the user to scan four pieces multiple times
            // Initialise
            resetMotors(); // Resets motors for new round
            int[] colorList = new int[MAX_OBJECTS]; // Initialises array to store colors
            int colorCount = 0; // Initialises the color count

            // Scanning loop
            while (colorCount < MAX_OBJECTS && !Button.ENTER.isDown()) { // Loops until the user has scanned four pieces or the Enter button is pressed
                if (Button.ESCAPE.isDown()) {
                    return; // Exit program immediately if ESCAPE button is pressed
                }
                int color = getColor(); // Gets color from the color sensor
                if (color != -1) { // -1 means no colour or unrecognised colour
                    colorList[colorCount++] = color; // Stores the color in a list
                    beepOnce(); // Emits a beep sound every time a color has been scanned
                }
                Delay.msDelay(1000); // Delays for 1000 milliseconds
            }

            // Sorting loop
            for (int i = 0; i < colorCount; i++) { // Loops through the detected colors
                sortColor(colorList[i]); // Sorts each detected color
                ejectObject();
            }

            moveToColorPosition(0); // Moves to start position after sorting

            if (Button.ESCAPE.isDown()) {
                break; // Exit program
            }

            LCD.clear(); // Ensures the LCD screen has been cleared
            Delay.msDelay(1000);
        }
    }

    private static void showWelcomeScreen() {
        LCD.clear();
        LCD.drawString("Welcome to the", 0, 0);
        LCD.drawString("Color Sorter v1.4", 0, 1);
        LCD.drawString("Tayib, Brilanta,", 0, 3);
        LCD.drawString("Aymen & Ishfaq", 0, 4);
        LCD.drawString("Press any button", 0, 6);
        LCD.drawString("to start", 0, 7);
        Button.waitForAnyPress(); // Waits for any button press to continue
    }

    private static void resetMotors() {
        beltMotor.resetTachoCount(); // Resets tachometer count for belt motor 
        feedMotor.resetTachoCount(); // Resets tachometer count for feed motor 
    }

    private static int getColor() {
        int colorId = colorSensor.getColorID(); // Gets color ID from the color sensor 
        String colorName = ""; // Initialize color name as an empty string
        switch (colorId) { // Switch statement based on color ID 
            case Color.BLACK:
                colorName = "BLACK";
                break;
            case Color.BLUE:
                colorName = "BLUE";
                break;
            case Color.GREEN:
                colorName = "GREEN";
                break;
            case Color.YELLOW:
                colorName = "YELLOW";
                break;
            case Color.RED:
                colorName = "RED";
                break;
            case Color.WHITE:
                colorName = "WHITE";
                break;
            case Color.BROWN:
                colorName = "BROWN";
                break;
            default:
                colorName = "UNKNOWN";
                break;
        }
        LCD.clear(); // Clear the screen before displaying the new color name
        LCD.drawString(colorName, 0, 1); // Display the color name on the screen
        return colorId;
    }


    private static void sortColor(int colorId) { 
        String colorName = getColorName(colorId); // Convert colorId to colorName
        LCD.clear();
        LCD.drawString("Sorting: " + colorName, 0, 0); // Displays the color name on the LCD screen 
        switch (colorId) { // Switch statement based on the color ID 
            case Color.RED:
                moveToColorPosition(COLOR_POSITIONS[0]); // Moves to the position corresponding with red 
                break;
            case Color.GREEN:
                moveToColorPosition(COLOR_POSITIONS[1]); // Moves to the position corresponding with green 
                break;
            case Color.BLUE:
                moveToColorPosition(COLOR_POSITIONS[2]); // Moves to the position corresponding with blue  
                break;
            case Color.YELLOW:
                moveToColorPosition(COLOR_POSITIONS[3]); // Moves to the position corresponding with yellow  
                break;
            default:
                LCD.drawString("Unknown Color", 0, 1); // Display message for unknown color 
                break;
        }
    }

    private static String getColorName(int colorId) {
        switch (colorId) { 
            case Color.BLACK: 
                return "BLACK"; // Returns color name for black 
            case Color.BLUE:
                return "BLUE"; // Returns color name for blue  
            case Color.GREEN:
                return "GREEN"; // Returns color name for green  
            case Color.YELLOW: 
                return "YELLOW"; // Returns color name for yellow   
            case Color.RED:
                return "RED"; // Returns color name for red  
            case Color.WHITE:
                return "WHITE"; // Returns color name for white  
            case Color.BROWN:
                return "BROWN"; // Returns color name for brown  
            default:
                return "UNKNOWN";
        }
    }


    private static void moveToColorPosition(int position) {
        beltMotor.rotateTo(position, true); // Rotates the belt motor to the specified position with an immediate return 
        while (beltMotor.isMoving()) { // Waits until belt motor finishes moving 
            Delay.msDelay(1000);
        }
    }

    private static void ejectObject() {
        feedMotor.rotate(90); // Rotates the feed motor to eject object 
        Delay.msDelay(500); // Delays rotation for 500 milliseconds 
        feedMotor.rotate(-90); // Rotates the feed motor back to the original position 
    }

    private static void beepOnce() {
        Sound.playTone(1000, 100); // Plays a tone of 1000Hz for 100 milliseconds 
    }
}

