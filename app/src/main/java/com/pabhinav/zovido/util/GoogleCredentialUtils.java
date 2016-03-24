package com.pabhinav.zovido.util;

import android.content.Context;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;

/**
 * @author pabhinav
 */
public class GoogleCredentialUtils {

    /** Need scopes for spread sheet access **/
    private static final String[] SCOPES = {
            "https://spreadsheets.google.com/feeds",
            "https://spreadsheets.google.com/feeds/spreadsheets/private/full",
            "https://docs.google.com/feeds"
    };

    /** Need to convert input stream into a File object **/
    private static File getTempPkc12File(Context context) throws IOException {

        InputStream pkc12Stream = context.getAssets().open("pK.p12");
        File tempPkc12File = File.createTempFile("temp_pkc12_file", "p12");
        OutputStream tempFileStream = new FileOutputStream(tempPkc12File);

        int read = 0;
        byte[] bytes = new byte[1024];
        while ((read = pkc12Stream.read(bytes)) != -1) {
            tempFileStream.write(bytes, 0, read);
        }
        return tempPkc12File;
    }

    /**
     * This method is used to fetch google credentials, used with spreadsheet scope.
     *
     * @param context context of calling activity
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static GoogleCredential getGoogleCredentials(Context context) throws GeneralSecurityException, IOException {

        return new GoogleCredential.Builder()
                .setTransport(new NetHttpTransport())
                .setJsonFactory(new JacksonFactory())
                .setServiceAccountId("zovido-827@zovido-1219.iam.gserviceaccount.com")
                .setServiceAccountScopes(Arrays.asList(SCOPES))
                .setServiceAccountPrivateKeyFromP12File(getTempPkc12File(context))
                .build();
    }
}
