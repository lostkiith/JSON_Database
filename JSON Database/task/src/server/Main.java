package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {

    private static final int SERVER_PORT = 34522;
    private static final MessageBroker messageBroker = new MessageBroker();
    private static final DataBase database = new DataBase();
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Lock readLock = lock.readLock();
    private static final Lock writeLock = lock.writeLock();

    public static void main(String[] args) {

        try (ServerSocket server = new  ServerSocket(SERVER_PORT)) {
            System.out.println("Server started!");
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            while (true) {
                Socket socket = server.accept();
                executor.submit(() -> {
                    try {
                        Processing(socket, server);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void Processing(Socket socket, ServerSocket server) throws IOException {

        DataInputStream input = new DataInputStream(socket.getInputStream());
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        // format request
        Gson gson = new Gson();

        JsonElement jsonElement = JsonParser.parseString(input.readUTF());
        JsonObject jsonRequest = jsonElement.getAsJsonObject();

        if (jsonRequest.get("type").getAsString().equals("exit")) {
            input.close();
            output.close();
            socket.close();
            server.close();
            System.exit(0);
        }
        // process the request
        MessageProcessing(output, jsonRequest);
        socket.close();
    }

    // Util methods
    private static void MessageProcessing(DataOutputStream output,  JsonObject request) throws IOException {
        Command commandOp;
        List<JsonElement> resolveMessage;
        Gson gson = new Gson();

        if (request.get("type").getAsString().equals("get")) {
            readLock.lock();
            JsonElement keyRequest = request.getAsJsonObject().get("key");
            commandOp = new GetCommand(Main.database, keyRequest);
            resolveMessage = executeCommands(commandOp);
            String message = gson.toJson(resolveMessage);
            output.writeUTF(message);
            readLock.unlock();
        }

        if (request.get("type").getAsString().equals("set")) {
            writeLock.lock();
            commandOp = new SetCommand(Main.database, request);
            resolveMessage = executeCommands(commandOp);
            String message = gson.toJson(resolveMessage);
            output.writeUTF(message);
            writeLock.unlock();
        }


        if (request.get("type").getAsString().equals("delete")) {
            writeLock.lock();
            commandOp = new DeleteCommand(Main.database, request);
            resolveMessage = executeCommands(commandOp);
            String message = gson.toJson(resolveMessage);
            output.writeUTF(message);
            writeLock.unlock();

        }
    }
    private static List<JsonElement> executeCommands(Command command) {
        messageBroker.setCommand(command);
        messageBroker.executeCommand();
        return messageBroker.getResolveCode();
    }
}
