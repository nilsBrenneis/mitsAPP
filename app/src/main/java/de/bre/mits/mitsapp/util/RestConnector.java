package de.bre.mits.mitsapp.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import android.util.Log;

import java.util.Collections;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class RestConnector {
    private static final String TAG = "RestConnector"; //tag für logkatze
    private static HttpHeaders requestHeaders;
    private static RestTemplate restTemplate;

    /**
     * takes uri with parameters in it to send with HTTP.GET to data provider
     * @param uri with parameters
     * @return status message from data provider
     */
    public static JSONArray getRessource(String uri) {
        connect();
        try {
            Log.d(TAG, Settings.AUTH_URI);
            //make network request
            ResponseEntity response = restTemplate.exchange(Settings.PROVIDER_URL + uri, HttpMethod.GET, new HttpEntity<>(requestHeaders), String.class);

            String responseStr = response.getBody().toString();
            return new JSONArray(responseStr);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                Log.e(TAG, "Error during authorization. HTTP 401");
            } else {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }
    }

    /**
     * takes uri with parameters in it to send with HTTP.PUT to data provider
     * @param uri with parameters
     * @return status message from data provider
     */
    public static HttpStatus putRessource(String uri) {
        connect();
        try {
            Log.d(TAG, Settings.AUTH_URI);
            //make the network request
            ResponseEntity response = restTemplate.exchange(Settings.PROVIDER_URL + uri, HttpMethod.PUT, new HttpEntity<>(requestHeaders), String.class);
                return response.getStatusCode();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                Log.e(TAG, "Error during authorization. HTTP 401");
            } else {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }
            return e.getStatusCode();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return HttpStatus.I_AM_A_TEAPOT; //for the lols @ 2am
        }
    }

    /**
     * use entered credentials form login screen to check if they are correct.
     * was intended to use for oauth2 but now to demonstrate how passwords
     * in data provider are managed
     * @param username entered username
     * @param password entered password
     * @return true if authenticated
     */
    public static boolean authenticate(String username, String password) {
        Helpermethods.getEditor().putString("username", username);
        Helpermethods.getEditor().putString("password", password);
        Helpermethods.getEditor().commit();
        connect();

        try {
            Log.d(TAG, Settings.AUTH_URI);
            //make network request
            ResponseEntity response = restTemplate.exchange(Settings.PROVIDER_URL + Settings.AUTH_URI + "?username=" + username + "&password=" + password, HttpMethod.GET, new HttpEntity<>(requestHeaders), String.class);
            String responseStr = response.getBody().toString();

            JSONArray jArr = new JSONArray(responseStr);
            JSONObject jobj = jArr.getJSONObject(0);

            return jobj.get("authenticated").equals("true");

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                Log.e(TAG, "Error during authorization. HTTP 401");
            } else {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            Helpermethods.getEditor().remove("username");
            Helpermethods.getEditor().remove("password");
            return false;
        }
    }


    /**
     * use saved password to populate header
     * for further use
     */
    private static void connect() {
        String username = Helpermethods.getpref().getString("username", "");
        String password = Helpermethods.getpref().getString("password", "");
        destroySSL();

        //populate HTTP Basic Authentitcation header
        HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
        requestHeaders = new HttpHeaders();
        requestHeaders.setAuthorization(authHeader);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));


        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    }


    /**
     * I am ashamed of having written this method. It makes the app accept all
     * x509 certificates because of its prototype stage.
     */ //TODO kick method out after prototype stage
    private static void destroySSL() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }
}

