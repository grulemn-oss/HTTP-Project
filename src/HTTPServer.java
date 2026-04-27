import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HTTPServer {

    public static final int PORT = 80;
    public static final String IP = "127.0.0.1";
    public static final String CRLF = "\r\n";
    public static final String EOH = CRLF + CRLF;
	public static final Charset ENCODING = StandardCharsets.ISO_8859_1; // encoding to use for reading/writing 

    public static void main(String[] args){

        System.out.println("server is listening to port 80");
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);

            while(true){

                Socket socket = serverSocket.accept();
                System.out.println("get connection from IP: " + socket.getRemoteSocketAddress());

                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                // Hint: use
				//	String body = new String(Files.readAllBytes(file.toPath()), ENCODING);
				// to read a file and convert it into a string 
                // read egg, shell by shell
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
                String line = bufferedReader.readLine();
                while(!line.isEmpty()){
                    System.out.println(line);
                    line = bufferedReader.readLine();
                }

                // generate an egg
                String response = "Hello World HTTP!";
                PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(dataOutputStream, StandardCharsets.ISO_8859_1));
                printWriter.print("HTTP/1.1 200 OK" + CRLF);
                printWriter.print("Content-Type: text/html; charset=ISO-8859-1" + CRLF);
                printWriter.print("Content-Length: " + response.length() + CRLF);
                printWriter.print("Accept: */*" + EOH);
                printWriter.flush();
                printWriter.print(response);

                printWriter.close();
                bufferedReader.close();
                socket.close();
            }

        }catch (UnknownHostException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }




    }





}
