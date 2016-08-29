package com.akulogics.gallery.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by zsolt_venczel on 2016.08.17
 */
public class AuthenticationService {

    public static final String PERMISSION_FILE = System.getProperty("permissionFile", "gallery.permitted.list");

    private AuthenticationService() {
    }

    private static AuthenticationService service = new AuthenticationService();

    public static AuthenticationService getService() {
        return service;
    }

    public GoogleIdToken fetchProfile(String token) {
        GoogleIdToken result = null;

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList("202284166040-281utnubjpu62cplqvuolla1vrg554up.apps.googleusercontent.com"))
                    .setIssuer("accounts.google.com")
                    .build();

            result = verifier.verify(token);

        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return result;
    }

    public boolean checkPathPermission(Path path, String userId) {
        boolean result = true;
        Path checkPath = null;

        if (Files.isDirectory(path)) {
            if (path.equals(FileService.GALLERY_PATH_ITEM)) {
                checkPath = path;
            } else {
                checkPath = path;
                result = checkPathPermission(path.getParent(), userId);
            }
        } else {
            result = checkPathPermission(path.getParent(), userId);
        }

        if (checkPath!=null && result) {
            Path permissionFilePath = path.resolve(PERMISSION_FILE);
            Set<String> permittedUsers = Collections.singleton(userId);

            if (Files.exists(permissionFilePath) && !Files.isDirectory(permissionFilePath))  {
                try (Stream<String> lines = Files.lines(permissionFilePath)) {
                    permittedUsers = lines.collect(Collectors.toSet());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            result = permittedUsers.contains(userId);
        }

        return result;
    }

}
