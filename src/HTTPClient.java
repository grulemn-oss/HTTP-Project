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

        System.out.println("client is requesting ... ");
        try {
            String host = args[0];
            String inputFile = args[1];

            // build the connection
            Socket socket = new Socket(host, PORT);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

			// HINT: If the content length is not given in the HTTP respond's header,
			// use while((N_bytes = reader.read(buffer, 0, CHUNK_SIZE)) != -1 ){} 
            // generate an egg
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(dataOutputStream, StandardCharsets.ISO_8859_1));
            if (inputFile == "") {
                printWriter.print("GET /index.html HTTP/1.1" + CRLF);
            } else {
                printWriter.print("GET " + inputFile + " HTTP/1.1" + CRLF);
            }
            printWriter.print("HOST: " + host + CRLF);
            printWriter.print("CONNECTION: close" + CRLF);
            printWriter.print("Accept: */*" + EOH);
            printWriter.flush();

            // read egg, shell by shell
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
            String line = bufferedReader.readLine();
            while(!line.isEmpty()){
                System.out.println(line);
                line = bufferedReader.readLine();
            }

            // feed the egg
            char[] buf = new char[100];
            bufferedReader.read(buf, 0, 17);
            System.out.println("Msg from server: " + new String(buf));


        }catch (UnknownHostException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
