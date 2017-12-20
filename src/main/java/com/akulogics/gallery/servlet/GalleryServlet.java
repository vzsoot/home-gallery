package com.akulogics.gallery.servlet;

import com.akulogics.gallery.bean.DirectoryItem;
import com.akulogics.gallery.bean.GalleryItem;
import com.akulogics.gallery.service.AuthenticationService;
import com.akulogics.gallery.service.FileService;
import com.akulogics.gallery.service.LoggerService;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zsolt_venczel on 2016.08.16
 */
@WebServlet(urlPatterns = "/gallery", loadOnStartup = 1)
public class GalleryServlet extends HttpServlet {

    @Override
    public void init() {
        LoggerService.log("GalleryServlet init.");
    }

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    FileService fileService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String userId = (String) req.getAttribute(LoginServlet.SESSION_USER);
        String path = URLDecoder.decode(req.getParameter("path"), "utf8");

        List<GalleryItem> galleryItems = new LinkedList<>();

        if (userId != null && authenticationService.checkPathPermission(path, userId)) {
            LoggerService.log(userId, "Open gallery path: " + path);

            DirectoryItem galleryDirectory = fileService.fetchDirectoryItem(path);
            if (galleryDirectory != null) {
                galleryDirectory.getFiles().stream()
                        .filter(item -> authenticationService.checkPathPermission(item.getPath(), userId))
                        .sorted((item1, item2) -> item1.getPath().compareToIgnoreCase(item2.getPath()))
                        .forEach(item -> {
                            String pathParam = "/item?path=" + item.getPath();

                            GalleryItem galleryItem = new GalleryItem();

                            if (item.getMimeType() != null && item.getMimeType().startsWith("video")) {
                                galleryItem.setDownloadUrl(pathParam);
                                galleryItem.setThumb("img/video-icon.jpg");
                                galleryItem.setPoster("img/video-icon.jpg");
                                galleryItem.setHtml("<div><video class=\"lg-video-object lg-html5\" controls>" +
                                        "<source src=\"" + pathParam + "\" type=\"" + item.getMimeType() + "\"></video></div>");
                            } else {
                                galleryItem.setDownloadUrl(pathParam);
                                if (item.getPathFull() != null) {
                                    try {
                                        galleryItem.setSrc("/item?path=" + URLEncoder.encode(item.getPathFull(), "UTF-8"));
                                    } catch (UnsupportedEncodingException e) {
                                        LoggerService.log(userId, e.getMessage());
                                    }
                                } else {
                                    galleryItem.setSrc(pathParam + "&height=1080");
                                }

                                if (item.getPathThumb() != null) {
                                    galleryItem.setThumb("/item?path=" + item.getPathThumb());
                                } else {
                                    galleryItem.setThumb(pathParam + "&height=100");
                                }
                            }
                            galleryItems.add(galleryItem);
                        });
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(resp.getOutputStream(), galleryItems);
    }
}
