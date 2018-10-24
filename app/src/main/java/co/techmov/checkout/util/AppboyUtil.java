package co.techmov.checkout.util;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import co.techmov.checkout.task.VoidTask;

/**
 * Created by victor on 09-05-15.
 */
public class AppboyUtil {

    final String PF_ANDROID_GROUP_IDENTIFIER = "43843606-5adb-4a77-a13c-d785769aedcc";

    public void sendMessage(final String userId, final String title, final String alertMessage){
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        org.json.JSONObject appboyNotification = new JSONObject();
                        org.json.JSONObject message = new JSONObject();
                        org.json.JSONArray ids = new JSONArray();
                        org.json.JSONObject devicesMessage = new JSONObject();
                        ids.put(0,userId);

                        message.put("title", title);
                        message.put("alert", alertMessage);
                        devicesMessage.put("android_push", message);

                        appboyNotification.put("app_group_id", PF_ANDROID_GROUP_IDENTIFIER);
                        appboyNotification.put("messages", devicesMessage);
                        appboyNotification.put("external_user_ids", ids);

                        Restclient restclient = new Restclient("/messages/send");
                        restclient.prepareClient();
                        StringBuilder result = restclient.execute(appboyNotification.toString());
                        Log.d("WS-RESULT",result.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            task.execute();
    }

    private class Restclient {
        public static final String URI = "https://api.appboy.com";
        public static final String USER = "api@pidafacil.com";
        public static final String PASS = "IadfogwfoOnHH0nnS3tmeE0XlwK46m";
        private String credentials = Restclient.USER.concat(":").concat(Restclient.PASS);
        private byte[] credBase64 = (Base64.encodeBase64(credentials.getBytes()));
        private JSONObject params = new JSONObject();
        private HttpClient client;
        private HttpPost post;
        private String mapping;
        private HttpResponse response;

        /**
         * Constructor del cliente del WS. Recibe el mappeo de la ruta que accede.
         * www.host.com/mapping
         * @param mapping
         */
        public Restclient(String mapping) {
            this.mapping = mapping;
            prepareClient();
        }

        public void setMapping(String mapping) {
            this.mapping = mapping;
        }

        /***
         * Limpia los parametros
         */
        public void clearParams(){
            this.params = new JSONObject();
        }

        /**
         * Agrega un parametro tipo String para enviarlo al host.
         * @param name
         * @param value
         */
        public void addParam(String name, String value){
            try {
                params.put(name, value);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                System.out.println("El parametro "+name+" no pudo ser agregado ("+e.getMessage()+")");
            }
        }

        /**
         * Antes de agregar los parametros, debe preparar el cliente seteando los valores de las cabeceras.
         */
        public void prepareClient(){
            params = new JSONObject();
            client = new DefaultHttpClient();
            post = new HttpPost(Restclient.URI.concat(mapping));
            post.addHeader("Content-Type","application/json");
        }

        /**
         * Agrega los parametros a la cabecera httpPost
         */
        public void prepareParams(){
            try {
                Log.d("INFO: ", "JSON params " + params.toString());
                StringEntity entity = new StringEntity(params.toString(), "UTF-8");
                post.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        /**
         * Ejecuta la peticion solicitada.
         * @return
         * @throws ClientProtocolException
         * @throws IOException
         * @throws JSONException
         */
        public StringBuilder execute() throws ClientProtocolException, IOException, JSONException{
            response = client.execute(post);
            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            return (result);
        }

        /**
         * Ejecuta una peticion enviando contenido tipo StringBuffer directamente en lugar de parametros.
         * @param jsonStr
         * @return
         * @throws JSONException
         * @throws IOException
         * @throws ClientProtocolException
         */
        public StringBuilder execute(String jsonStr) throws ClientProtocolException, IOException, JSONException{
            try {
                Log.d("CLIENT", jsonStr);
                StringEntity entity = new StringEntity(jsonStr.toString(), "UTF-8");
                post.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return execute();
        }

    }

}
