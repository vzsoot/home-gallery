package com.akulogics.gallery.servlet;

import com.akulogics.gallery.bean.DirectoryItem;
import com.akulogics.gallery.bean.FileItem;
import com.akulogics.gallery.service.AuthenticationService;
import com.akulogics.gallery.service.FileService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Created by zsolt_venczel on 2016.08.17
 */
public class GalleriesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final Object userId = req.getAttribute(LoginServlet.SESSION_USER);
        String htmlResponse = "";

        if (userId!=null) {
            BiConsumer<DirectoryItem, StringBuilder> galleryLinkBuilder = (fileItem, response) -> {
                String directoryName = fileItem.getFile().getName().trim();
                if (!fileItem.isEmpty()) {
                    try {
                        response.append("<a href=\"#").append(URLEncoder.encode(fileItem.getPath(), "utf8"))
                                .append("\" onclick=\"doGalleryClick('")
                                .append(fileItem.getPath())
                                .append("', this)\">");
                        response.append(directoryName);
                        response.append("</a>");
                    } catch (UnsupportedEncodingException ex) {
                        ex.printStackTrace();
                    }
                }
            };

            BiConsumer<DirectoryItem, StringBuilder> listItemBuilder = (directoryItem, response) -> {
                String directoryName = directoryItem.getFile().getName();
                String chunks[] = directoryName.split("-");
                String name = chunks[0].trim();
                String subGalleryNames = galleryList(directoryItem.getPath(), userId.toString(), galleryLinkBuilder).trim();
                String date = chunks.length>1 ? chunks[1].trim() : "";
                String description = Arrays.stream(chunks).skip(2).map(String::trim).collect(Collectors.joining(", "));

                if (directoryItem.isEmpty()) {
                    response.append("<li><h3 class=\"name\">").append(name.trim()).append("</h3>");
                } else {
                    try {
                        response.append("<li><a href=\"#").append(URLEncoder.encode(directoryItem.getPath(), "utf8"))
                                .append("\" onclick=\"doGalleryClick('").append(directoryItem.getPath()).append("', this)\">")
                                .append("<h3 class=\"name\">").append(name.trim()).append("</h3></a>");
                    } catch (UnsupportedEncodingException ex) {
                        ex.printStackTrace();
                    }
                }
                response.append("<p class=\"date\">");
                response.append(date);
                response.append("</p><i><p class=\"description\">");
                response.append(description);
                response.append("</p></i>");
                if (subGalleryNames.length()>0) {
                    response.append("<div class=\"sub-gallery\">");
                    response.append(subGalleryNames);
                    response.append("</div>");
                }
                response.append("</li>");
            };

            htmlResponse = galleryList("", userId.toString(), listItemBuilder);
        }

        OutputStreamWriter out = new OutputStreamWriter(new BufferedOutputStream(resp.getOutputStream()));
        out.write(htmlResponse);
        out.close();
    }

    protected String galleryList(String path, String userId, BiConsumer<DirectoryItem, StringBuilder> itemBuilder) {
        final StringBuilder response = new StringBuilder();
        DirectoryItem gallery = FileService.getService().fetchDirectoryItem(path);

        gallery.getDirectories().stream()
                .filter(item -> AuthenticationService.getService().checkPathPermission(item.getPath(), userId))
                .sorted((item1, item2) -> item1.getPath().compareToIgnoreCase(item2.getPath()))
                .forEach(directoryItem -> itemBuilder.accept(directoryItem, response));

        return response.toString();
    }

}
