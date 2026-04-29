import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class HTTPClient {

    public static final int PORT = 80;
    public static final String CRLF = "\r\n";
    public static final String EOH = CRLF + CRLF;
	public static final Charset ENCODING = StandardCharsets.ISO_8859_1; // encoding to use for reading/writing data
    public static final int CHUNK_SIZE = 512;				// size of fragment to process


    public static void main(String[] args) {
        try {
            // Read user input
            String host = args[0];
            String inputFile = "index.html";
            if (args.length > 1) {
                inputFile = args[1];
            }


            // build the connection
            Socket socket = new Socket(host, PORT);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

			// HINT: If the content length is not given in the HTTP respond's header,
			// use while((N_bytes = reader.read(buffer, 0, CHUNK_SIZE)) != -1 ){} 
            // ------------OUTPUT-----------
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(dataOutputStream, ENCODING));

            String reqPath;
            if (inputFile.startsWith("/")) {
                    reqPath = inputFile;
            } else {
                reqPath = "/" + inputFile;
            }
            printWriter.print("GET " + reqPath + " HTTP/1.1" + CRLF);
            printWriter.print("HOST: " + host + CRLF);
            printWriter.print("CONNECTION: close" + CRLF);
            printWriter.print("Accept: */*" + EOH);
            printWriter.flush();

            // ------------INPUT---------------
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream, ENCODING));
            String line = bufferedReader.readLine();
            int length = 0;
            if (line.length() > 8 && line.startsWith("200", 9)) {
                while(line != null && !line.isEmpty()) {
                    line = bufferedReader.readLine();
                    if (line != null && line.length() > 15 && line.startsWith("Content-Length:")) {
                        length = Integer.parseInt(line.substring(16).trim());
                    }
                }

                // SAVE FILE
                String safeReqPath = inputFile;
                if (inputFile.contains("/")) {
                    safeReqPath = inputFile.substring(inputFile.lastIndexOf("/") + 1);
                }
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("./resources/client_folder/" + safeReqPath, ENCODING));

                if (length > 0) {
                    // len known
                    char[] buf = new char[length];
                    bufferedReader.read(buf, 0, length);
                    bufferedWriter.write(new String(buf));
                } else {
                    // len unknown, alw stream
                    char[] buffer = new char[CHUNK_SIZE];
                    int nBytes;
                    while ((nBytes = bufferedReader.read(buffer, 0, CHUNK_SIZE)) != -1) {
                        bufferedWriter.write(buffer, 0, nBytes);
                    }
                }
                bufferedWriter.flush();
                bufferedWriter.close();
                System.out.println("Saved File: " + safeReqPath);

            } else if (line.length() > 8) {
                // PRINT ERROR
                System.out.print("Error: ");
                while(!line.isEmpty()) {
                    System.out.println(line);
                    line = bufferedReader.readLine();
                }
            }
            bufferedReader.close();
            socket.close();

        }catch (UnknownHostException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}