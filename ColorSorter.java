private static int getColor() {
    int colorId = colorSensor.getColorID();
    String colorName = ""; // Initialize color name as an empty string
    switch (colorId) {
        case Color.NONE:
            colorName = "NONE";
            break;
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
