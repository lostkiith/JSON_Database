package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class DataBase {

    private Map<String, JsonElement> dataBase;


    public DataBase() {

        Path path = Paths.get("./src/server/data/db.json");
        if (Files.exists(path)) {
            try {
                dataBase = readJSON();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            dataBase = new HashMap<>();
            try {
                writeJSON(dataBase);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public JsonElement getEntry(String key) {

        return dataBase.get(key);
    }

    public void setEntry(String key, JsonElement value)
    {
        dataBase.put(key,value);
        try {
            writeJSON(dataBase);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JsonElement deleteEntry(String key) {

        JsonElement returnMessage = dataBase.remove(key);
        try {
            writeJSON(dataBase);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnMessage;
    }

    private static void writeJSON(Map<String, JsonElement> db) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        File file = new File("./src/server/data/db.json");
        FileWriter writer = new FileWriter(file);
        writer.write(gson.toJson(db));
        writer.close();
    }

    private static Map<String, JsonElement> readJSON() throws FileNotFoundException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type empMapType = new TypeToken<Map<String, JsonElement>>() {}.getType();
        BufferedReader bufferedReader = new BufferedReader(
                new FileReader("./src/server/data/db.json"));
        return gson.fromJson(bufferedReader, empMapType);
    }
}
