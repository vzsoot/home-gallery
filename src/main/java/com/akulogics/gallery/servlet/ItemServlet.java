package com.akulogics.gallery.servlet;

import com.akulogics.gallery.bean.CacheableItem;
import com.akulogics.gallery.bean.FileItem;
import com.akulogics.gallery.service.AuthenticationService;
import com.akulogics.gallery.service.FileService;
import com.akulogics.gallery.service.LoggerService;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by zsolt_venczel on 2016.08.17
 */
public class ItemServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        LoggerService.log("ItemServlet init.");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = (String)req.getAttribute(LoginServlet.SESSION_USER);

        if (userId!=null) {
            String path = req.getParameter("path");

            try (OutputStream out = resp.getOutputStream()){

                FileItem fileItem = FileService.getService().fetchFileItem(path);

                if (fileItem != null && fileItem.getItemType() == CacheableItem.ItemType.FILE &&
                        AuthenticationService.getService().checkPathPermission(path, userId)) {
                    Integer height;

                    try {
                        String heightParam = req.getParameter("height");
                        height = Integer.parseInt(heightParam);
                    } catch (NumberFormatException ex) {
                        height = null;
                    }

                    resp.setContentType(fileItem.getMimeType());

                    if (height != null && !fileItem.getMimeType().startsWith("video")) {

                        System.gc();

                        BufferedImage image = ImageIO.read(fileItem.getFile());

                        int scaledHeight = height;
                        int scaledWidth = image.getWidth() * height / image.getHeight();

                        BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
                        Graphics2D g = scaledImage.createGraphics();
                        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g.setComposite(AlphaComposite.Src);
                        g.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
                        g.dispose();

                        ImageIO.write(scaledImage, "jpeg", out);
                        out.flush();
                        out.close();
                    } else {
                        String range = req.getHeader("Range");
                        Long skip = 0L;

                        if (range != null && range.startsWith("bytes")) {
                            String rangeChunks[] = range.split("[-=]");
                            if (rangeChunks.length > 1) {
                                try {
                                    skip = Long.parseLong(rangeChunks[1].trim());
                                } catch (NumberFormatException ex) {
                                    skip = 0L;
                                }
                            }
                        }

                        if (!fileItem.getMimeType().startsWith("video")) {
                            resp.setHeader("Content-disposition", "attachment; filename=" + fileItem.getFile().getName());
                        } else {
                            String contentRange = "bytes " + skip + "-" + (fileItem.getFile().length() - 1) + "/" + fileItem.getFile().length();

                            resp.setHeader("Accept-Ranges", "bytes");
                            resp.setHeader("Content-Length", Long.toString(fileItem.getFile().length() - skip));
                            resp.setHeader("Content-Range", contentRange);
                            resp.setStatus(206);

                            LoggerService.log(userId, "Playing video: " + path + "; Range: " + contentRange);
                        }
                        out.flush();

                        try (FileInputStream in = new FileInputStream(fileItem.getFile())) {
                            //noinspection ResultOfMethodCallIgnored
                            in.skip(skip);
                            byte[] buffer = new byte[4096];
                            int length;
                            while ((length = in.read(buffer)) > 0) {
                                out.write(buffer, 0, length);
                            }
                            in.close();
                            out.flush();
                        }
                    }
                }
            } catch (Throwable ex) {
                LoggerService.log(userId, "Path: " + path + "; Error: " + ex.getMessage());
            }
        }
    }
}
