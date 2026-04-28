import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

public class HTTPServer {

    public static final int PORT = 80;
    public static final String IP = "127.0.0.1";
    public static final String CRLF = "\r\n";
    public static final String EOH = CRLF + CRLF;
	public static final Charset ENCODING = StandardCharsets.ISO_8859_1; // encoding to use for reading/writing
    public static final File ROOT_DIR = new File("resources/server_folder");

    public static void main(String[] args){

        System.out.println("server is listening to port 80");
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);

            while(true){

                Socket socket = serverSocket.accept();
                System.out.println("get connection from IP: " + socket.getRemoteSocketAddress());

                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                // ------------INPUT---------------
                // read egg, shell by shell
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
                String path = "";
                String line = bufferedReader.readLine();
                while(!line.isEmpty()) {
                    if (line.startsWith("GET ")) {
                        path = line.split(" ")[1];
                    }
                    System.out.println(line);
                    line = bufferedReader.readLine();
                }

                // -------------BODY-----------------
                if (path.equals("/") || path.isEmpty()) {
                    path = "/index.html";
                }
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }
                // Still need to find a way to search the server_folder for files within subfolders
                File file = new File(ROOT_DIR, path);

                // ------------OUTPUT---------------
                // generate an egg

                PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(dataOutputStream, StandardCharsets.ISO_8859_1));

                if (file.exists() && !file.isDirectory()) {
                    String body = new String(Files.readAllBytes(file.toPath()), ENCODING);

                    printWriter.print("HTTP/1.1 200 OK" + CRLF);
                    printWriter.print("Content-Type: text/html" + CRLF);
                    printWriter.print("Content-Length: " + body.length() + CRLF);
                    printWriter.print("Accept: */*" + EOH);
                    printWriter.print(body);
                } else {

                    printWriter.print("HTTP/1.1 404 Not Found" + CRLF);
                    printWriter.print("Content-Length: 0" + CRLF);
                    printWriter.print("Connection: close" + EOH);
                    System.out.println("Error 404: The requested file '" + path + "' does not exist.");
                }
                printWriter.flush();
                printWriter.close();
                bufferedReader.close();
                socket.close();
            }

        }catch (NoSuchFileException e){
            // Somehow this needs to print the 404 not found from test 1
            // (I put it in this catch block but that may not be the best way to handle it)
//            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(dataOutputStream, StandardCharsets.ISO_8859_1));
//            printWriter.print("HTTP/1.1 404 Not Found" + CRLF);
//            printWriter.print("Content-Type: text/html" + CRLF);
//            printWriter.print("Content-Length: " + body.length() + CRLF);
//            printWriter.print("Accept: */*" + EOH);
//            printWriter.flush();
//            printWriter.print(body);
//
//            printWriter.close();
//            bufferedReader.close();
//            socket.close();
        }catch (UnknownHostException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }


    }





}
