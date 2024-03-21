import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.hardware.lcd.LCD;
import java.awt.Color;
import lejos.utility.Delay;
import lejos.hardware.Sound;



public class SortingColours { 

    private static EV3LargeRegulatedMotor conveyorMotor = new EV3LargeRegulatedMotor(MotorPort.A);
    private static EV3LargeRegulatedMotor sorterMotor = new EV3LargeRegulatedMotor(MotorPort.B);
    private static EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S1);
    private static EV3TouchSensor touchSensor = new EV3TouchSensor(SensorPort.S2); //i cant remember which ports it was 
    private static int sortedCount = 0; //counter to keep track of the number of counters that have been sorted 

    public static void main(String[] args) {
        LCD.drawString("Press any button to start", 0, 0);
        Button.waitForAnyPress(); //waits for user to press a button before starting the sorting process 
        
        while (sortedCount < 5) { //loops five times to allows user to sort 5 counters 
            sortPiece();
        }
        
        LCD.drawString("Program Completed", 0, 0);
        closeResources(); //ensures the resources are closed properly 
    }


    private static void closeResources() {
        conveyorMotor.close();
        sorterMotor.close();
        colorSensor.close();
        touchSensor.close();
    }

    private static void beepOnce() { 
        Sound.playTone(1000, 100); //produces a beep sound from the robot 
    }
 
    //method using a sample provider to get the color ID detected by the color sensor 
    private static int getColor() {
        SampleProvider colorProvider = colorSensor.getColorID();
        float[] colorSample = new float[colorProvider.sampleSize()];
        colorProvider.fetchSample(colorSample, 0);
        return (int) colorSample[0]; //ensures value returned is an integer 
    }

    private static void sortPiece() {

        int color = getColor();
        LCD.clear(); //clears LCD display 

        if (color == Color.RED) {
            sortedCount++; //increments the sorted count 
            beepOnce(); //beep sound every time user scans a piece 
            LCD.drawString("Color is: RED", 0, 0); //displays which color the piece is on the screen 
            conveyorMotor.rotateTo(0); //conveyor motor doesn't move at all as the red basket will be placed right in front of it 
            sorterMotor.rotateTo(90); //sorter motor rotates forwards 90 degrees to dispense the color piece into the basket 
            Delay.msDelay(100); //delays rotation for 100 milliseconds 
            sorterMotor.rotateTo(-90); //sorter motor rotates backwards 90 degrees once dispensed to its original position 

        } else if (color == Color.BLUE) {
            sortedCount++;
            beepOnce();
            LCD.drawString("Color is: BLUE", 0, 0);
            conveyorMotor.rotateTo(90); //conveyor motor moves foward by rotating 90 degrees to be placed in front of the blue basket 
            sorterMotor.rotateTo(90);
            Delay.msDelay(100);
            sorterMotor.rotateTo(-90);

        } else if (color == Color.GREEN) {
            sortedCount++;
            beepOnce();
            LCD.drawString("Color is: GREEN", 0, 0);
            conveyorMotor.rotateTo(180); //conveyor motor moves foward by rotating 1800 degrees to be placed in front of the green basket 
            sorterMotor.rotateTo(90);
            Delay.msDelay(100);
            sorterMotor.rotateTo(-90);

        } else if (color == Color.YELLOW) {
            sortedCount++;
            beepOnce();
            LCD.drawString("Color is: YELLOW", 0, 0);
            conveyorMotor.rotateTo(270); //conveyor motor moves foward by rotating 270 degrees to be placed in front of the yellow basket 
            sorterMotor.rotateTo(90);
            Delay.msDelay(100);
            sorterMotor.rotateTo(-90);
        } else {
            beepOnce();
            LCD.drawString("Color is: not recognised.", 0, 0); //alerts user that the color is unrecognised on the display 
        }
    }
}





//attempt with the other way to get a color we can try both

    //private static int getColor() {
        
       //SensorMode colorIDMode = colorSensor.getColorIDMode();
       //SampleProvider colorProvider = colorIDMode;
        //float[] colorSample = new float[colorProvider.sampleSize()];
        //colorProvider.fetchSample(colorSample, 0);
        //return (int) colorSample[0];
        
            // this was an implementation of getColorIDMode from a website, can try both 
            // float[] colorSample = new float[1];
            // getColorIDMode().fetchSample(colorSample,0);
            // return (int) colorSample[0];
    //}

    //private static void sortPiece() {
        //int color = getColor();
        //if (color == 5) {
            //LCD.drawString("Color is: RED", 0, 0);
            //conveyorMotor.rotateTo(0);
            //sorterMotor.rotateTo(90);
            //Delay.msDelay(100);
            //sorterMotor.rotateTo(-90);
        //} else if (color == 2) {
            //LCD.drawString("Color is: BLUE", 0, 0);
            //conveyorMotor.rotateTo(90);
            //sorterMotor.rotateTo(90);
            //Delay.msDelay(100);
            //sorterMotor.rotateTo(-90);
        //} else if (color == 3) {
            //LCD.drawString("Color is: GREEN", 0, 0);
            //conveyorMotor.rotateTo(180);
            //sorterMotor.rotateTo(90);
            //Delay.msDelay(100);
            //sorterMotor.rotateTo(-90);
       // } else if (color == 4) {
            //LCD.drawString("Color is: YELLOW", 0, 0);
            //conveyorMotor.rotateTo(270);
            //sorterMotor.rotateTo(90);
            //Delay.msDelay(100);
            //sorterMotor.rotateTo(-90);
        //}
