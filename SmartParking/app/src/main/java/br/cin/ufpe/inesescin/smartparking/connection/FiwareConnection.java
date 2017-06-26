package br.cin.ufpe.inesescin.smartparking.connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import br.cin.ufpe.inesescin.smartparking.util.Constants;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by João Pedro on 18/07/16.
 */
public class FiwareConnection {

    private OkHttpClient client;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public FiwareConnection() {
        client = new OkHttpClient();
    }


    public String getEntityById(String siteAddress, String entityId) throws IOException {
        String url = "http://" + siteAddress + "/v1/contextEntities/" + entityId;
        return doGetRequest(url);
    }

    public int countEntityByAttribute(String siteAddress, String attribute) throws IOException {
        int answer = -1;
        int entityId = -1;
        String response = "";
        while (!response.equalsIgnoreCase(getErrorMessage(entityId))) {
            entityId++;
            answer++;
            String url = "http://" + siteAddress + "/v1/contextEntities/" + entityId + "/attributes/" + attribute;
            response = doGetRequest(url);
            response = response.replace('"',' ');
        }
        return answer;
    }

    public String[] getEntitiesByType(String siteAddress, String type) throws IOException, JSONException {
        String[] response = new String[7];
        String url = siteAddress + "/v1/queryContext/";
        String json = "{" + "\"entities\": [" + "{" + "\"type\": \"" + type + "\"," + "\"isPattern\": \"true\"," + "\"id\": \".*\"" + "}]}";
        JSONObject jObj = new JSONObject(doPostRequest(url, json));
        JSONArray jarr = jObj.getJSONArray("contextResponses");
        for(int i = 0; i < jarr.length();i++){
            JSONObject jObj2 = (jarr.getJSONObject(i)).getJSONObject("contextElement");
            JSONArray jArr2 = jObj2.getJSONArray("attributes");
            if(jArr2.length() >= 6){
                JSONObject jObj3 = jArr2.getJSONObject(5);
                String emptySpaces = jObj3.getString("value");
                response[i] = emptySpaces;
            }else{
                JSONObject jObj3 = jArr2.getJSONObject(0);
                String emptySpaces = jObj3.getString("value");
                response[i] = emptySpaces;
            }
            if(i == 6){
                i = jarr.length();
            }
        }
        return response;
    }

    public String getEntityByType(String siteAddress, String type) throws IOException {
        String url = "http://" + siteAddress + "/v1/queryContext/";
        String json = "{" + "\"entities\": [" + "{" + "\"type\": \"" + type + "\"," + "\"isPattern\": \"true\"," + "\"id\": \".*\"" + "}]}";
        return doPostRequest(url, json);
    }

    public String getEntityAttributeValue(String attributeName, String entityId, String siteAddress, String property) throws IOException {
        String url = "http://" + siteAddress + "/v1/contextEntities/" + entityId + "/attributes/" + attributeName;
        String response = doGetRequest(url);
        String value = "";
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray attributesArray = jsonResponse.getJSONArray("attributes");
            JSONObject attribute = attributesArray.getJSONObject(0);
            value = attribute.getString(property);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        return value;
    }

    public boolean lookForEmptyParkingSpace(String entityId) throws IOException, JSONException {
        boolean answer = false;
        String response = getEntityAttributeValue("vagas",entityId,Constants.FIWARE_ADDRESS,"value");
        JSONArray jsonResponseArray = new JSONArray(response);
        for(int i = 0;i < jsonResponseArray.length();i++){
            JSONObject jsonObject = jsonResponseArray.getJSONObject(i);
            if(jsonObject.getString("ocupado").equalsIgnoreCase("False")){
                answer = true;
                return answer;
            }

        }

        return answer;
    }

    public String searchAdjacentBlockID(String atualEntityID, int position) throws IOException, JSONException {
        String answer = "";
        String response = getEntityAttributeValue("blocosAdjacentesID",atualEntityID,Constants.FIWARE_ADDRESS,"value");
        JSONArray jsonResponseArray = new JSONArray(response);
        if(position < jsonResponseArray.length()){
            JSONObject jsonObject = jsonResponseArray.getJSONObject(position);
            answer = jsonObject.getString("id");
            return answer;
        }else{
            JSONObject jsonObject = jsonResponseArray.getJSONObject(0);
            searchAdjacentBlockID(jsonObject.getString("id"),0);
        }
        return answer;
    }

    public String getBlockIDByStore(String storeName) throws IOException, JSONException {
        int objNum = countEntityByAttribute(Constants.FIWARE_ADDRESS,"latLng");
        String answer = "";
        for(int i = 0; i < objNum;i++){
            String response = getEntityAttributeValue("lojas",Integer.toString(i), Constants.FIWARE_ADDRESS,"value");
            JSONArray jsonResponseArray = new JSONArray(response);
            for(int j = 0; j < jsonResponseArray.length();j++){
                JSONObject jsonObj = jsonResponseArray.getJSONObject(j);
                if(jsonObj.getString("nome").equalsIgnoreCase(storeName)){
                    answer = Integer.toString(i);
                    return answer;
                }
            }
        }
        return answer;
    }

    public String getBlockNameByID(String entityId) throws IOException, JSONException {
        String response = "";
        String answer = "";
        String request = "{\"entities\": [{\"type\":\"Block\",\"isPattern\": \"true\", \"id\":"+ entityId +"}]}";
        response = doPostRequest(Constants.FIWARE_ADDRESS+"/v1/queryContext",request);
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray contextArray = jsonResponse.getJSONArray("contextResponses");
        JSONObject contextObject = contextArray.getJSONObject(0);
        JSONObject contextElements = contextObject.getJSONObject("contextElement");
        JSONArray attributesArray = contextElements.getJSONArray("attributes");
        for(int i = 0;i < attributesArray.length();i++){
            if(attributesArray.getJSONObject(i).getString("name").equalsIgnoreCase("nome")){
                answer = attributesArray.getJSONObject(i).getString("value");
            }
        }
        return answer;
    }

    public String getBlockIDByName(String blockName) throws IOException, JSONException {
        //int objNum = countEntityByAttribute(Constants.FIWARE_ADDRESS,"latLng");
        int objNum = 2;
        String answer = "";
        for(int i = 0;i < objNum;i++){
            String possibleName = getBlockNameByID(i+"");
            if(possibleName.equalsIgnoreCase(blockName)){
                answer = ""+i;
            }
        }
        return answer;
    }

    private synchronized String doGetRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("Connection", "close")
                .build();

        Response response;
        response = client.newCall(request).execute();

        String stringResponse = response.body().string();
        response.body().close();

        return stringResponse;
    }

    private synchronized String doPostRequest(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url("http://"+url)
                .addHeader("Accept", "application/json")
                .addHeader("Connection", "close")
                .post(body)
                .build();

        Response response;
        response = client.newCall(request).execute();
        String stringResponse = response.body().string();
        response.body().close();

        return stringResponse;
    }

    //These are private connections to the node.js server
    public String getBlockIDByPreferences(String username,String url) throws IOException, JSONException {
        String res = "";
        url = url + "getUserPreferences";
        String jsonReq = "{\"userEmail\" : " + "\"" + username + "\"" + "}";
        String response = doPostRequest(url,jsonReq);
        JSONObject jsonResponse = new JSONObject(response);
        res = jsonResponse.getString("blockID");
        return res;
    }

    public void updateUserPreferences(String username, String blockID, String url) throws IOException {
        String jsonReq = "{\"userEmail\" : " + "\"" + username + "\"" + ", \"blockID\" :" + "\"" + blockID + "\"" + "}";
        url = url + "updateUserPreference";
        doPostRequest(url,jsonReq);
    }

    public void addUserPreferences(String username, String blockID, String url) throws IOException {
        url = url + "addUserPreference";
        String jsonReq = "{ \"userEmail\" : " + "\"" + username + "\"" +" , \"visitedBlocks\" : [{ \"blockID\" : " + "\"" +  blockID + "\"" + ", \"timesVisited\" : \"1\"}]}";
        doPostRequest(url,jsonReq);
    }

    public Boolean checkForUserPreferences(String username,String url) throws IOException, JSONException {
        Boolean res = false;
        String jsonReq = "{\"userEmail\" : " + "\"" + username + "\"" + "}";
        url = url + "checkForUser";
        String response = doPostRequest(url,jsonReq);
        JSONObject jsonResponse = new JSONObject(response);
        res = jsonResponse.getBoolean("value");
        return res;
    }

    public String getErrorMessage(int entityId){
        String response = "";
        response = "{\n" +
                "   statusCode  : {\n" +
                "     code  :  404 ,\n" +
                "     reasonPhrase  :  No context element found ,\n" +
                "     details  :  Entity id: /"+entityId+"/ \n" +
                "  }\n" +
                "}\n";
        return response;
    }
}