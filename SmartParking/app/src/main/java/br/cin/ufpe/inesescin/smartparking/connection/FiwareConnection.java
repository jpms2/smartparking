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
        int objNum = countEntityByAttribute("130.206.119.206:1026","latLng");
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

    public Double[] getLatLng(String entityID) throws IOException, JSONException {
        Double[] answer = new Double[2];
        String response = getEntityAttributeValue("latLng",entityID, Constants.FIWARE_ADDRESS,"value");
        JSONArray jsonResponseArray = new JSONArray(response);
        JSONObject jsonObj = jsonResponseArray.getJSONObject(0);
        answer[0] = Double.parseDouble(jsonObj.getString("lat"));
        answer[1] = Double.parseDouble(jsonObj.getString("lng"));
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
                .url(url)
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