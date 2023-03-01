import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;


import java.io.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class App {

    public static void main(String[] args) throws GeneralSecurityException, IOException {

       String token = Objects.requireNonNull(action()).getAccessToken();
        System.out.println("TOKEN: " + token);
        printDados(Objects.requireNonNull(token));
    }

    private static Credential action() throws IOException, GeneralSecurityException {
        try {

            GoogleClientSecrets clientSecrets = new GoogleClientSecrets();

            GoogleClientSecrets.Details details = new GoogleClientSecrets.Details();


//            Rabobank
//            details.setClientSecret("bSw7Q~cKYsnVCnGJx3K2ZZCqkv9OPsxQaf9d1");
//            details.setClientId("8719247d-9f9d-4e15-8df2-fbaac412bea5");
//            details.setRedirectUris(Arrays.asList("http://localhost", "http://localhost:61578/Callback"));

//            google
            details.setClientId("1020962943809-o4vr0pied2pl3sl3djs574fsgj8ve08c.apps.googleusercontent.com");
            details.setClientSecret("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");
            details.setRedirectUris(Arrays.asList("urn:ietf:wg:oauth:2.0:oob","http://localhost"));


            clientSecrets.setFactory(GsonFactory.getDefaultInstance());
            clientSecrets.setInstalled(details);


            //TODO
            HttpTransport httpTransport = new NetHttpTransport.Builder().doNotValidateCertificate().build();
            LocalServerReceiver localServerReceiver = new LocalServerReceiver.Builder().setPort(61578).build();

            // set up authorization code flow
//            Rabobank
//            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                    httpTransport, GsonFactory.getDefaultInstance(), clientSecrets,
//                    Arrays.asList("email", "profile", "openid"))
//                    .setAuthorizationServerEncodedUrl("https://login.microsoftonline.com/6e93a626-8aca-4dc1-9191-ce291b4b75a1/oauth2/authorize?prompt=login")
//                    .setTokenServerUrl(new GenericUrl("https://login.microsoftonline.com/6e93a626-8aca-4dc1-9191-ce291b4b75a1/oauth2/v2.0/token"))
//                    .build();

// google
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, GsonFactory.getDefaultInstance(), clientSecrets,
                    Arrays.asList("https://www.googleapis.com/auth/userinfo.email", "https://www.googleapis.com/auth/userinfo.profile"))
//                    .setAuthorizationServerEncodedUrl("https://accounts.google.com/o/oauth2/auth")
//                    .setTokenServerUrl(new GenericUrl("https://oauth2.googleapis.com/token"))
                    .build();

            return new AuthorizationCodeInstalledApp(flow, localServerReceiver).authorize("");

        } catch (GoogleJsonResponseException e) {
            System.out.println(e.getDetails());
        }
        return null;
    }


    private static void printDados(String access_token) throws IOException {
        URL url = new URL(" https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + access_token);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestProperty("accept", "application/json");

        InputStream responseStream = connection.getInputStream();

        System.out.println(converToString(responseStream));

    }


    private static String converToString(InputStream responseStream) {

        return new BufferedReader(
                new InputStreamReader(responseStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}
