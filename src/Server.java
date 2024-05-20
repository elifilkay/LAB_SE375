import java.io.*;
import java.net.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

public class Server {
    public static final String FILE_URL = "https://homes.izmirekonomi.edu.tr/eokur/sample0.txt";
    public static final int PORT = 12345;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Waiting for connection...");


            SecretKey secretKey = generateSecretKey();

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Connection established with: " + socket.getRemoteSocketAddress());
                System.out.println("Sending URL: " + FILE_URL);


                ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
                objectOutput.writeObject(secretKey);
                handleClient(socket, secretKey);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static SecretKey generateSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128); // Using AES with 128-bit key size
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void handleClient(Socket socket, SecretKey secretKey) {
        try (
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true)
        ) {

            output.println(FILE_URL);

            int fileLength = Integer.parseInt(input.readLine());
            String caseType = input.readLine();
            int shiftAmount = Integer.parseInt(input.readLine());
            String colorCode = input.readLine();

            System.out.println("User choices received: " + caseType + ", " + shiftAmount + ", " + colorCode);

            StringBuilder originalData = new StringBuilder();
            StringBuilder caseChanged = new StringBuilder();
            StringBuilder shiftedData = new StringBuilder();
            StringBuilder coloredData = new StringBuilder();

            for (int i = 0; i < fileLength; i++) {
                char original = (char) input.read();
                originalData.append(original);
                char transformed = transformCharacter(original, caseType, shiftAmount);
                caseChanged.append(caseTransform(original, caseType));
                shiftedData.append(transformed);
                String color = colorCode.equals("R") ? "\u001B[31m" : "\u001B[33m";
                coloredData.append(color).append(transformed).append("\u001B[0m");
            }

            output.println(encryptData(caseChanged.toString(), secretKey));
            output.println(encryptData(shiftedData.toString(), secretKey));
            output.println(encryptData(coloredData.toString(), secretKey));
        } catch (IOException e) {
            System.err.println("Client communication error: " + e.getMessage());
        }
    }


    private static char transformCharacter(char c, String caseType, int shiftAmount) {
        // Apply case transformation
        c = caseTransform(c, caseType);
        // Apply shift operation
        return (char) (c + shiftAmount);
    }

    private static char caseTransform(char c, String caseType) {
        return caseType.equals("U") ? Character.toUpperCase(c) : Character.toLowerCase(c);
    }

    private static String encryptData(String data, SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
