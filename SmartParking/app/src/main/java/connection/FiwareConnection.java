package connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

    public FiwareConnection()
    {
        client = new OkHttpClient();
    }


    public String getEntityById(String siteAddress, String entityId) throws IOException
    {
        String url = "http://" + siteAddress + "/v1/contextEntites/" + entityId;
        return doGetRequest(url);
    }

    public String getEntityByType(String siteAddress, String type) throws IOException
    {
        String url = "http://" + siteAddress + "/v1/queryContext/";
        String json = "{" + "\"entities\": [" + "{" + "\"type\": \"" + type + "\"," + "\"isPattern\": \"true\"," +  "\"id\": \".*\"" + "}]}";

        return doPostRequest(url, json);
    }

    public String getEntityAttributeValue(String attributeName, String entityId, String siteAddress, String property) throws IOException
    {
        String url = "http://" + siteAddress + "/v1/contextEntities/" + entityId + "/attributes/" + attributeName;
        String response = doGetRequest(url);
        String value = "";
        try
        {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray attributesArray = jsonResponse.getJSONArray("attributes");
            JSONObject attribute = attributesArray.getJSONObject(0);
            value = attribute.getString(property);
        }
        catch (JSONException jsonException)
        {
            jsonException.printStackTrace();
        }
        return value;
    }

    public boolean lookForEmptyParkingSpace(String entityId){

        return false;
    }

    public String searchBlockByStore(String storeName){

        return "";
    }

    public Double[] getLatLng(String str) {
        Double[] response = new Double[2];

        int endIndexLat = 0;
        int endIndexLng = 0;
        int startIndexLat = 0;
        int startIndexLng = 0;
        startIndexLat = str.indexOf("l") + 6;
        endIndexLat = str.indexOf(",") - 1;
        startIndexLng = str.indexOf("g") + 4;
        endIndexLng = str.indexOf("}") - 3;
        str = str.substring(startIndexLat);
        response[0] = Double.parseDouble(str.substring((startIndexLat), endIndexLat));
        response[1] = Double.parseDouble(str.substring(startIndexLng, endIndexLng));

        return response;
    }

    private synchronized String doGetRequest(String url) throws IOException
    {
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

    private synchronized String doPostRequest(String url, String json) throws IOException
    {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept","application/json")
                .addHeader("Connection", "close")
                .post(body)
                .build();

        Response response;
        response = client.newCall(request).execute();
        String stringResponse = response.body().string();
        response.body().close();

        return stringResponse;
    }


}