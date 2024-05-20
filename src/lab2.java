import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class lab2 {
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
            StringBuilder originalTextBuilder = new StringBuilder();
            StringBuilder transformedCaseTextBuilder = new StringBuilder();
            StringBuilder transformedShiftTextBuilder = new StringBuilder();
            StringBuilder transformedColorTextBuilder = new StringBuilder();

            Map<Integer, String[]> characterMap = new HashMap<>();

            String line;
            int index = 0;
            while ((line = bufferedReader.readLine()) != null) {
                originalTextBuilder.append(line).append("\n");

                String[] charInfo = new String[4];
                charInfo[0] = line;

                characterMap.put(index++, charInfo);
            }

            System.out.println("Creating threads for transformations...");
            System.out.println("Case Thread created.");
            Thread caseThread = new CaseThread(characterMap, caseChoice);
            System.out.println("Shift Thread created.");
            Thread shiftThread = new ShiftThread(characterMap, shiftAmount);
            System.out.println("Color Thread created.");
            Thread colorThread = new ColorThread(characterMap, colorChoice);

            System.out.println("Starting threads...");
            caseThread.start();
            shiftThread.start();
            colorThread.start();

            System.out.println("Waiting for threads to finish...");
            shiftThread.join();
            caseThread.join();
            colorThread.join();

            for (int i = 0; i < characterMap.size(); i++) {
                String[] v = characterMap.get(i);
                transformedCaseTextBuilder.append(v[1]);
                transformedShiftTextBuilder.append(v[2]);
                transformedColorTextBuilder.append(v[3]).append(v[1]).append(ANSI_RESET);
            }

            System.out.println("Original:");
            System.out.println(originalTextBuilder);
            System.out.println("After Case Change:");
            System.out.println(transformedCaseTextBuilder);
            System.out.println("After Shift:");
            System.out.println(transformedShiftTextBuilder);
            System.out.println("After Color Change:");
            System.out.println(transformedColorTextBuilder);
            System.out.println("Hashmap Content:");
            System.out.println(characterMap);

        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    static class CaseThread extends Thread {
        private final Map<Integer, String[]> characterMap;
        private final String caseChoice;

        CaseThread(Map<Integer, String[]> characterMap, String caseChoice) {
            this.characterMap = characterMap;
            this.caseChoice = caseChoice;
        }

        @Override
        public void run() {
            for (Map.Entry<Integer, String[]> entry : characterMap.entrySet()) {
                String[] value = entry.getValue();
                if (!value[0].equals("\n")) {
                    if (caseChoice.equals("U")) {
                        value[1] = value[0].toUpperCase();
                    } else {
                        value[1] = value[0].toLowerCase();
                    }
                }
            }
        }
    }

    static class ShiftThread extends Thread {
        private final Map<Integer, String[]> characterMap;
        private final int shiftAmount;

        ShiftThread(Map<Integer, String[]> characterMap, int shiftAmount) {
            this.characterMap = characterMap;
            this.shiftAmount = shiftAmount;
        }

        @Override
        public void run() {
            for (Map.Entry<Integer, String[]> entry : characterMap.entrySet()) {
                String[] value = entry.getValue();
                if (!value[0].equals("\n")) {
                    StringBuilder shiftedLine = new StringBuilder();
                    for (char c : value[0].toCharArray()) {
                        shiftedLine.append((char) (c + shiftAmount));
                    }
                    value[2] = shiftedLine.toString();
                }
            }
        }
    }

    static class ColorThread extends Thread {
        private final Map<Integer, String[]> characterMap;
        private final String colorChoice;

        ColorThread(Map<Integer, String[]> characterMap, String colorChoice) {
            this.characterMap = characterMap;
            this.colorChoice = colorChoice;
        }

        @Override
        public void run() {
            for (Map.Entry<Integer, String[]> entry : characterMap.entrySet()) {
                String[] value = entry.getValue();
                if (!value[0].equals("\n")) {
                    value[3] = colorChoice.equals("R") ? ANSI_RED : ANSI_YELLOW;
                }
            }

        }

    }

}
