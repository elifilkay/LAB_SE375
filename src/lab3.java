import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class lab3 {
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

            Map<Integer, String[]> characterMap = new HashMap<>();

            String line;
            int index = 0;
            while ((line = bufferedReader.readLine()) != null) {
                originalTextBuilder.append(line).append("\n");

                String[] charInfo = new String[6];
                charInfo[0] = line;
                charInfo[5] = "3";

                characterMap.put(index++, charInfo);
            }

            System.out.println("Creating threads for transformations...");
            CaseThread caseThread = new CaseThread(characterMap, caseChoice);
            ShiftThread shiftThread = new ShiftThread(characterMap, shiftAmount);
            ColorThread colorThread = new ColorThread(characterMap, colorChoice);

            System.out.println("Starting threads...");
            caseThread.start();
            shiftThread.start();
            colorThread.start();

            System.out.println("Waiting for threads to finish...");
            try {
                shiftThread.join();
                caseThread.join();
                colorThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Original:");
            System.out.println(originalTextBuilder);
            System.out.println("After Case Change:");
            characterMap.forEach((i, content) -> System.out.println(content[1]));
            System.out.println("After Shift:");
            characterMap.forEach((i, content) -> System.out.println(content[2]));
            System.out.println("After Color Change:");
            characterMap.forEach((i, content) -> System.out.println(content[3] + content[1] + ANSI_RESET));
            System.out.println("Number of Transformations:");
            int totalTransformations = characterMap.values().stream().mapToInt(arr -> arr.length).sum();
            int[] transformationArray = new int[totalTransformations];
            Arrays.fill(transformationArray, 3);
            System.out.println(Arrays.toString(transformationArray));

        } catch (IOException e) {
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
            synchronized (characterMap) {
                characterMap.forEach((i, content) -> {
                    String processed = caseChoice.equals("U") ? content[0].toUpperCase() : content[0].toLowerCase();
                    content[1] = processed;
                    content[5] = String.valueOf(Integer.parseInt(content[5]) + 1);
                });
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
            synchronized (characterMap) {
                characterMap.forEach((i, content) -> {
                    StringBuilder shifted = new StringBuilder();
                    for (char c : content[0].toCharArray()) {
                        shifted.append((char) (c + shiftAmount));
                    }
                    content[2] = shifted.toString();
                    content[5] = String.valueOf(Integer.parseInt(content[5]) + 1);
                });
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
            synchronized (characterMap) {
                characterMap.forEach((i, content) -> {
                    String colorCode = colorChoice.equals("R") ? ANSI_RED : ANSI_YELLOW;
                    content[3] = colorCode;
                    content[5] = String.valueOf(Integer.parseInt(content[5]) + 1);
                });
            }
        }
    }
}
