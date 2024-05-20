import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class lab5 {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please state your choice for text manipulation...");
        System.out.println("UPPER case or lower case (U or L):");
        String caseChoice = scanner.nextLine().toUpperCase();

        System.out.println("How many characters to shift (number between 1-3):");
        int shiftAmount = Integer.parseInt(scanner.nextLine());

        System.out.println("Color of characters (R or Y):");
        String colorChoice = scanner.nextLine().toUpperCase();
        scanner.close();

        String[] fileNames = {"sample1.txt", "sample2.txt", "sample3.txt", "sample4.txt"};

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        for (String fileName : fileNames) {
            executorService.submit(() -> processFile(fileName, caseChoice, shiftAmount, colorChoice));
        }

        executorService.shutdown();
    }

    private static void processFile(String fileName, String caseChoice, int shiftAmount, String colorChoice) {
        ConcurrentMap<Integer, String[]> characterMap = new ConcurrentHashMap<>();
        Lock lock = new ReentrantLock();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            String originalText = "";
            String line;
            int index = 0;
            while ((line = bufferedReader.readLine()) != null) {
                originalText += line + "\n";

                String[] charInfo = new String[6];
                charInfo[0] = line;
                charInfo[1] = line;
                charInfo[5] = "0";

                characterMap.put(index++, charInfo);
            }

            for (String[] content : characterMap.values()) {
                CaseTransformation(content, caseChoice, lock);
                ShiftTransformation(content, shiftAmount, lock);
                ColorTransformation(content, colorChoice, lock);
            }

            System.out.println("Original for " + fileName + ":\n" + originalText);

            for (String[] content : characterMap.values()) {
                System.out.println("After Case Change for " + fileName + ":\n" + content[1]);
                System.out.println("After Shift for " + fileName + ":\n" + content[2]);
                System.out.println("After Color Change for " + fileName + ":\n" + content[3] + content[1] + ANSI_RESET);
                System.out.println("Number of Transformations: " + content[5]);
            }

        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private static void CaseTransformation(String[] content, String caseChoice, Lock lock) {
        lock.lock();
        try {
            String processed = caseChoice.equals("U") ? content[0].toUpperCase() : content[0].toLowerCase();
            content[1] = processed;
            content[5] = String.valueOf(Integer.parseInt(content[5]) + 1);
        } finally {
            lock.unlock();
        }
    }

    private static void ShiftTransformation(String[] content, int shiftAmount, Lock lock) {
        lock.lock();
        try {
            String shifted = "";
            for (char c : content[0].toCharArray()) {
                shifted += (char) (c + shiftAmount);
            }
            content[2] = shifted;
            content[5] = String.valueOf(Integer.parseInt(content[5]) + 1); // Increment transformation count
        } finally {
            lock.unlock();
        }
    }

    private static void ColorTransformation(String[] content, String colorChoice, Lock lock) {
        lock.lock();
        try {
            String colorCode = (colorChoice.equals("R")) ? ANSI_RED : ANSI_YELLOW;
            content[3] = colorCode;
            content[5] = String.valueOf(Integer.parseInt(content[5]) + 1);
        } finally {
            lock.unlock();
        }
    }
}
