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


    public static void main(String[] args) {
        LCD.drawString("Press any button to start", 0, 0);
        Button.waitForAnyPress();
        sortPiece();
 
        LCD.drawString("Program Completed", 0, 0);
        closeResources();
    }


    private static void closeResources() {
        conveyorMotor.close();
        sorterMotor.close();
        colorSensor.close();
        touchSensor.close();
    }

    private static void beepOnce() {
        Sound.playTone(1000, 100);
    }
 

    private static int getColor() {
        
        SampleProvider colorProvider = colorSensor.getColorID();
        float[] colorSample = new float[colorProvider.sampleSize()];
        colorProvider.fetchSample(colorSample, 0);
        return (int) colorSample[0];
    }

    private static void sortPiece() {

        int color = getColor();
        LCD.clear();

        if (color == Color.RED) {
            beepOnce();
            LCD.drawString("Color is: RED", 0, 0);
            conveyorMotor.rotateTo(0);
            sorterMotor.rotateTo(90);
            Delay.msDelay(100);
            sorterMotor.rotateTo(-90);

        } else if (color == Color.BLUE) {
            beepOnce();
            LCD.drawString("Color is: BLUE", 0, 0);
            conveyorMotor.rotateTo(90);
            sorterMotor.rotateTo(90);
            Delay.msDelay(100);
            sorterMotor.rotateTo(-90);

        } else if (color == Color.GREEN) {
            beepOnce();
            LCD.drawString("Color is: GREEN", 0, 0);
            conveyorMotor.rotateTo(180);
            sorterMotor.rotateTo(90);
            Delay.msDelay(100);
            sorterMotor.rotateTo(-90);

        } else if (color == Color.YELLOW) {
            beepOnce();
            LCD.drawString("Color is: YELLOW", 0, 0);
            conveyorMotor.rotateTo(270);
            sorterMotor.rotateTo(90);
            Delay.msDelay(100);
            sorterMotor.rotateTo(-90);
        } else {
            beepOnce();
            LCD.drawString("Color is: not recognised.", 0, 0);
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
