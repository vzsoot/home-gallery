package com.akulogics.gallery.servlet;

import com.akulogics.gallery.bean.FileItem;
import com.akulogics.gallery.service.FileService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
            BiConsumer<FileItem, StringBuilder> galleryLinkBuilder = (fileItem, response) -> {
                String directoryName = fileItem.getFile().getName().trim();
                if (!fileItem.isEmpty()) {
                    response.append("<i><a href=\"#").append(fileItem.getPath())
                            .append("\" onclick=\"doGalleryClick('")
                            .append(fileItem.getPath())
                            .append("', this)\">");
                    response.append(directoryName);
                    response.append("</a></i><br/>");
                }
            };

            BiConsumer<FileItem, StringBuilder> listItemBuilder = (fileItem, response) -> {
                String directoryName = fileItem.getFile().getName();
                String chunks[] = directoryName.split("-");
                String name = chunks[0].trim();
                String subGalleryNames = galleryList(fileItem.getPath(), userId.toString(), galleryLinkBuilder).trim();
                String date = chunks.length>1 ? chunks[1].trim() : "";
                String description = Arrays.stream(chunks).skip(2).map(String::trim).collect(Collectors.joining(", "));

                if (fileItem.isEmpty()) {
                    response.append("<li><h3 class=\"name\">").append(name.trim()).append("</h3>");
                } else {
                    response.append("<li><a href=\"#").append(fileItem.getPath())
                            .append("\" onclick=\"doGalleryClick('").append(fileItem.getPath()).append("', this)\">")
                            .append("<h3 class=\"name\">").append(name.trim()).append("</h3></a>");
                }
                response.append("<p class=\"date\">");
                response.append(date);
                response.append("</p><i><p class=\"description\">");
                response.append(description);
                response.append("</p></i>");
                if (subGalleryNames.length()>0) {
                    response.append(subGalleryNames);
                }
                response.append("</li>");
            };

            htmlResponse = galleryList("", userId.toString(), listItemBuilder);
        }

        OutputStreamWriter out = new OutputStreamWriter(new BufferedOutputStream(resp.getOutputStream()));
        out.write(htmlResponse);
        out.close();
    }

    protected String galleryList(String path, String userId, BiConsumer<FileItem, StringBuilder> itemBuilder) {
        final StringBuilder response = new StringBuilder();
        FileService fileService = FileService.getService(userId);

        FileItem gallery = fileService.getFileItem(path);

        fileService.getFileItems(gallery).stream()
                .filter(FileItem::isDirectory)
                .sorted((item1, item2)->item1.getPath().compareToIgnoreCase(item2.getPath()))
                .forEach(fileItem -> itemBuilder.accept(fileItem, response));

        return response.toString();
    }

}
