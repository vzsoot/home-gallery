package com.akulogics.gallery.service;

import com.akulogics.gallery.bean.CacheableItem;
import com.akulogics.gallery.bean.DirectoryItem;
import com.akulogics.gallery.bean.FileItem;
import com.akulogics.gallery.bean.PermissionItem;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.Collections;

/**
 * Created by zsolt_venczel on 2016.08.17
 */
public class AuthenticationService {

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

    public boolean checkPathPermission(String path, String userId) {
        boolean result = true;
        DirectoryItem checkItem = null;

        FileItem fileItem = FileService.getService().fetchFileItem(path);

        if (fileItem.getItemType() == CacheableItem.ItemType.DIRECTORY) {
            if (fileItem.getParent()==null) {
                checkItem = (DirectoryItem)fileItem;
            } else {
                checkItem = (DirectoryItem)fileItem;
                result = checkPathPermission(fileItem.getParent().getPath(), userId);
            }
        } else if (fileItem.getParent()!=null) {
            result = checkPathPermission(fileItem.getParent().getPath(), userId);
        }

        if (checkItem!=null && result) {
            PermissionItem permissionItem = checkItem.getPermissionItem();

            if (permissionItem!=null) {
                result = permissionItem.getPermission().contains(userId);
            }
        }

        return result;
    }

}
