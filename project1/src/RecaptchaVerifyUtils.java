import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class RecaptchaVerifyUtils{
	public static final String SECRET_KEY = "6Ledvo8UAAAAAPH2u9kJuWkdHvU8UR61076l1ryL";
    public static final String SITE_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public static void verify( String gRecaptchaResponse) throws Exception {
        if (gRecaptchaResponse == null || gRecaptchaResponse.length() == 0) {
        		throw new Exception("recaptcha verification failed: gRecaptchaResponse is null or empty");
        }
        
        //URL object
        URL verifyUrl = new URL(SITE_VERIFY_URL);
        
        //Create a connection to the URL
        HttpsURLConnection conn = (HttpsURLConnection) verifyUrl.openConnection();
        
        //Creates string to be sent to the server
        String postParams = "secret=" + SECRET_KEY + "&response=" + gRecaptchaResponse;
        
        //sends the request to the server
        conn.setDoOutput(true);
        
        //get output stream from the HttpsURLConnection object
        //then write data to stream (send data to server)
        
        OutputStream outStream = conn.getOutputStream();
        outStream.write(postParams.getBytes());
        
        outStream.flush();
        outStream.close();
        
        int responseCode = conn.getResponseCode();
        System.out.println("responseCode=" + responseCode);
        
        InputStream inputStream = conn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        
        JsonObject jsonObject = new Gson().fromJson(inputStreamReader, JsonObject.class);
        inputStreamReader.close();
        
        System.out.println("Response: " + jsonObject.toString());
        
        if(jsonObject.get("success").getAsBoolean()) {
        	return;
        }
        throw new Exception("recaptcha verification failed: response is " + jsonObject.toString());
    }
}