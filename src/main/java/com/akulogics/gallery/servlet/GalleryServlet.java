package com.akulogics.gallery.servlet;

import com.akulogics.gallery.bean.FileItem;
import com.akulogics.gallery.bean.GalleryItem;
import com.akulogics.gallery.service.FileService;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zsolt_venczel on 2016.08.16
 */
public class GalleryServlet extends HttpServlet {

    public static final String[] sizes = System.getProperty("sizes", "360,480,720,1080").split(",");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = (String)req.getAttribute(LoginServlet.SESSION_USER);
        String path = req.getParameter("path");

        List<GalleryItem> galleryItems = new LinkedList<>();

        if (userId != null) {
            FileService fileService = FileService.getService(userId);
            FileItem galleryDirectory = fileService.getFileItem(path);
            if (galleryDirectory!=null && galleryDirectory.isDirectory()) {
                for (FileItem item : fileService.getFileItems(galleryDirectory)
                        .stream().filter(fileItem -> !fileItem.isDirectory()).collect(Collectors.toList())) {
                    String pathParam = "/item?path=" + item.getPath();

                    GalleryItem galleryItem = new GalleryItem();

                    if (item.getMimeType()!=null && item.getMimeType().startsWith("video")) {
                        galleryItem.setDownloadUrl(pathParam);
                        galleryItem.setThumb("img/video-icon.jpg");
                        galleryItem.setPoster("img/video-icon.jpg");
                        galleryItem.setHtml("<div><video class=\"lg-video-object lg-html5\" controls>" +
                                "<source src=\"" + pathParam + "\" type=\"" + item.getMimeType() + "\"></video></div>");
                    } else {
                        galleryItem.setDownloadUrl(pathParam);
                        if (item.getPathFull()!=null) {
                            galleryItem.setSrc("/item?path=" + item.getPathFull());
                        } else {
                            galleryItem.setSrc(pathParam + "&height=1080");
                        }

                        if (item.getPathThumb()!=null) {
                            galleryItem.setThumb("/item?path=" + item.getPathThumb());
                        } else {
                            galleryItem.setThumb(pathParam + "&height=100");
                        }

                        String responsive = Arrays.stream(sizes).map((size) -> pathParam + "&height=" + size + " " + size)
                                .collect(Collectors.joining(","));
                        galleryItem.setResponsive(responsive);
                    }
                    galleryItems.add(galleryItem);
                }
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(resp.getOutputStream(), galleryItems);
    }
}
