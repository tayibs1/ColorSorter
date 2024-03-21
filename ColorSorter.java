private static void sortColor(int colorId) {
    String colorName = getColorName(colorId); // Convert colorId to colorName
    LCD.clear();
    LCD.drawString("Sorting Color: " + colorName, 0, 0);
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

private static String getColorName(int colorId) {
    switch (colorId) {
        case Color.NONE:
            return "NONE";
        case Color.BLACK:
            return "BLACK";
        case Color.BLUE:
            return "BLUE";
        case Color.GREEN:
            return "GREEN";
        case Color.YELLOW:
            return "YELLOW";
        case Color.RED:
            return "RED";
        case Color.WHITE:
            return "WHITE";
        case Color.BROWN:
            return "BROWN";
        default:
            return "UNKNOWN";
    }
}
