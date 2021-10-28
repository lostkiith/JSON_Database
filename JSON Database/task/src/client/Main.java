package client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.gson.*;


import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Main {

    private final static String address = "127.0.0.1";
    private final static int port = 34522;

    @Parameter(names={"-in"})
    static String name = "";
    @Parameter(names={"-t"})
    static String type = "";
    @Parameter(names={"-k"})
    static String key = "";
    @Parameter(names={"-v"})
    static String Value = "";

    public static void main(String[] args) {

        Main main = new Main();
        JCommander.newBuilder().addObject(main).build().parse(args);
        main.run();
    }

    private void run() {
        try (Socket socket = new Socket(address, port);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
            System.out.println("Client started!");

            // create message and format it to JSON
            Gson gson = new Gson();
            Map<String, String> request = new HashMap<>();
            String message;

            if (!name.equals("")) {
                BufferedReader bufferedReader = new BufferedReader(
                        new FileReader("./src/client/data/"+name));

                JsonElement jsonElement = JsonParser.parseReader(bufferedReader);
                JsonObject jsonRequest = jsonElement.getAsJsonObject();

                message = gson.toJson(jsonRequest);

            } else {
                if (!type.equals("set")) {
                    request.put("type", type);
                    request.put("key", key);

                } else {
                    request.put("type", type);
                    request.put("key", key);
                    request.put("value", Value);
                }
                message = gson.toJson(request);
            }


            // send message to the client.
            System.out.println("Sent: "+message);
            output.writeUTF(message);

            // read response and print.
            String response = input.readUTF();

            System.out.println("Received: "+response);

        } catch (IOException e) {
            System.out.println("Error! The server is offline.");
        }

    }
}