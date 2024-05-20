import java.io.*;
import java.net.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

public class Client {
    public static final String SERVER_ADDRESS = "localhost";
    public static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Attempting connection to the server...");


            ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
            SecretKey secretKey = (SecretKey) objectInput.readObject();

            String fileUrl = input.readLine();
            System.out.println("Received " + fileUrl);

            BufferedReader fileReader = new BufferedReader(new InputStreamReader(new URL(fileUrl).openStream()));
            StringBuilder fileContent = new StringBuilder();
            int ch;
            while ((ch = fileReader.read()) != -1) {
                fileContent.append((char) ch);
            }

            System.out.print("Please state your choice.. UPPER case or lower case (U or L): ");
            String caseType = console.readLine();
            System.out.print("Please state your choice.. How many characters to shift (number between 1-3): ");
            int shiftAmount = Integer.parseInt(console.readLine());
            System.out.print("Please state your choice.. Color of characters (R or Y): ");
            String colorCode = console.readLine();

            output.println(encryptData(String.valueOf(fileContent.length()), secretKey));
            output.println(encryptData(caseType, secretKey));
            output.println(encryptData(String.valueOf(shiftAmount), secretKey));
            output.println(encryptData(colorCode, secretKey));
            output.println(encryptData(fileContent.toString(), secretKey));


            System.out.println("Original:");
            System.out.println(fileContent.toString());
            System.out.println("After Case Change:");
            System.out.println(decryptData(input.readLine(), secretKey));
            System.out.println("After Shift:");
            System.out.println(decryptData(input.readLine(), secretKey));
            System.out.println("After Color:");
            System.out.println(decryptData(input.readLine(), secretKey));

        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }

    private static String encryptData(String data, SecretKey secretKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    private static String decryptData(String encryptedData, SecretKey secretKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if (encryptedData == null) {
            return null;
        }

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decodedData);
    }

}
