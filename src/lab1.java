import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class lab1 {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please state your choice...");
        System.out.println("UPPER case or lower case (U or L):");
        String caseChoice = scanner.nextLine().toUpperCase();

        System.out.println("Please state your choice...");
        System.out.println("How many characters to shift (number between 1-3):");
        int shiftAmount = Integer.parseInt(scanner.nextLine());

        System.out.println("Please state your choice...");
        System.out.println("Color of characters (R or Y):");
        String colorChoice = scanner.nextLine().toUpperCase();

        String filePath = "sample.txt";

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String originalText = "";
            String transformedCaseText = "";
            String transformedShiftText = "";
            String transformedColorText = "";

            Map<Integer, String[]> characterMap = new HashMap<>(); // HashMap to store character details

            String line;
            int index = 0;
            while ((line = bufferedReader.readLine()) != null) {
                originalText += line + "\n";

                String[] charInfo = new String[4]; // Array to store character details
                charInfo[0] = line; // Original value

                // Case change
                if (caseChoice.equals("U")) {
                    charInfo[1] = line.toUpperCase(); // Upper case value
                } else if (caseChoice.equals("L")) {
                    charInfo[1] = line.toLowerCase(); // Lower case value
                }

                // Shift characters
                StringBuilder shiftedLine = new StringBuilder();
                for (char c : line.toCharArray()) {
                    shiftedLine.append((char) (c + shiftAmount));
                }
                charInfo[2] = shiftedLine.toString(); // Shifted value

                // Color change
                if (colorChoice.equals("R")) {
                    charInfo[3] = ANSI_RED; // Red color code
                } else if (colorChoice.equals("Y")) {
                    charInfo[3] = ANSI_YELLOW; // Yellow color code
                }

                characterMap.put(index++, charInfo); // Add character details to the map

                transformedCaseText += charInfo[1] + "\n";
                transformedShiftText += charInfo[2] + "\n";
                transformedColorText += charInfo[3] + charInfo[0] + ANSI_RESET + "\n";
            }

            System.out.println("Original:");
            System.out.println(originalText);

            System.out.println("After Case Change:");
            System.out.println(transformedCaseText);

            System.out.println("After Shift:");
            System.out.println(transformedShiftText);

            System.out.println("After Color Change:");
            System.out.println(transformedColorText);

            // Display the character map
            System.out.println("Character Map:");
            for (Map.Entry<Integer, String[]> entry : characterMap.entrySet()) {
                String[] charInfo = entry.getValue();
                System.out.println("Index: " + entry.getKey() +
                        ", Original: " + charInfo[0] +
                        ", Transformed: " + charInfo[1] +
                        ", Shifted: " + charInfo[2] +
                        ", Color: " + charInfo[3]);
            }

        } catch (IOException e) {
            System.err.println("An error occurred while reading the file: " + e.getMessage());
        }
    }
}