package com.foodfusion.foodfusion.Youtube;

import android.content.Context;

import com.foodfusion.foodfusion.R;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
/**
 * Created by Rameez on 3/21/2018.
 */

/**
 * Shared class used by every sample. Contains methods for authorizing a user and caching credentials.
 */
public class Auth {
    private  static Context context;
public Auth(Context context){
    this.context=context;
}
    /**
     * Define a global instance of the HTTP transport.
     */
    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Define a global instance of the JSON factory.
     */
    public static final JsonFactory JSON_FACTORY = new JacksonFactory();

    /**
     * This is the directory that will be used under the user's home directory where OAuth tokens will be stored.
     */
    private static final String CREDENTIALS_DIRECTORY = ".oauth-credentials";

    /**
     * Authorizes the installed application to access user's protected data.
     *
     * @param scopes              list of scopes needed to run youtube upload.
     * @param credentialDatastore name of the credential datastore to cache OAuth tokens
     */
    public static Credential authorize(List<String> scopes, String credentialDatastore){

//        {
//            "installed": {
//            "client_id": "Enter your client id here",
//                    "client_secret": "Enter your client secret here"
//        }
//        }
//        JSONObject obj=new JSONObject();
//        JSONObject obj1 = new JSONObject();
//        try {
//            obj.put("client_id", "3");
//            obj.put("project_id", "NAME OF STUDENT");
//            obj.put("auth_uri", "3rd");
//            obj.put("curriculum", "Arts");
//            obj.put("birthday", "5/5/1993");
//        }catch (Exception ex){
//
//        }
        try {
            // Load client secrets.
//        Reader clientSecretReader = new InputStreamReader(Auth.class.getResourceAsStream(context.getResources().openRawResource(R.raw.client_secrets)));
            Reader clientSecretReader = new InputStreamReader(context.getResources().openRawResource(R.raw.client_secrets));
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, clientSecretReader);

            // Checks that the defaults have been replaced (Default = "Enter X here").
//        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
//                || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
//            System.out.println(
//                    "Enter Client ID and Secret from https://console.developers.google.com/project/_/apiui/credential "
//                            + "into src/main/resources/client_secrets.json");
//            System.exit(1);
//        }

            // This creates the credentials datastore at ~/.oauth-credentials/${credentialDatastore}
            File dataDirectory = new File(context.getFilesDir(), CREDENTIALS_DIRECTORY);
            FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(dataDirectory);
            DataStore<StoredCredential> datastore = fileDataStoreFactory.getDataStore(credentialDatastore);

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes).setCredentialDataStore(datastore)
                    .build();

            // Build the local server and bind it to port 8080
            LocalServerReceiver localReceiver = new LocalServerReceiver.Builder().setPort(8080).build();

            // Authorize.
            return new AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user");
//            GoogleAccountCredential.usingOAuth2(
//                    context, Arrays.asList(SCOPES))
//                    .setBackOff(new ExponentialBackOff())
        }
        catch (Exception ex){
            String ee=ex.toString();
            return null;
        }
    }
}