package server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.*;

public class GetCommand implements Command{

    DataBase dataBase;
    JsonElement keyRequest;
    private final List<JsonElement> resolveCode;

    public GetCommand(DataBase database, JsonElement keyRequest) {
        this.dataBase = database;
        this.keyRequest = keyRequest;
        resolveCode = new ArrayList<>();
    }

    @Override
    public void execute() {

        if (!keyRequest.isJsonArray()) {
            JsonElement message = dataBase.getEntry(keyRequest.getAsString());
            JsonElement returnMessageEle = message == null ? getJsonElement() : getJsonElement(message);
            this.resolveCode.add(returnMessageEle);
        }else {

            JsonArray getArr = keyRequest.getAsJsonArray();
            if (getArr.size() == 1) {
                JsonElement test = dataBase.getEntry(getArr.get(0).getAsString());
                JsonElement returnMessageEle = getJsonElement(test);
                this.resolveCode.add(returnMessageEle);

            }else {
                JsonElement MainKey = dataBase.getEntry(getArr.get(0).getAsString());
                System.out.println(getArr.get(getArr.size() - 1));
                JsonElement chainMessage = null;
                for (int i = 1; i < getArr.size(); i++) {
                    chainMessage = MainKey.getAsJsonObject().get(getArr.get(i).getAsString());
                }
                JsonElement returnMessageEle = getJsonElement(chainMessage);
                this.resolveCode.add(Objects.requireNonNull(returnMessageEle));
            }
        }
    }

    private JsonElement getJsonElement(JsonElement message) {
        String returnMessage = "{'response': 'OK', " +
                "'value': " + message + "}";
        return JsonParser.parseString(returnMessage);
    }

    private JsonElement getJsonElement() {
        String returnMessage = "{'response': 'No such key', " +
                "'reason': 'No such key'}";

        return JsonParser.parseString(returnMessage);
    }

    @Override
    public List<JsonElement> getResolveCode() {
        return resolveCode;
    }
}