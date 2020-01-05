package cn.hidove.meimei.controller;

import cn.hidove.meimei.dto.FileInfoDTO;
import cn.hidove.meimei.provider.FileProvider;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class FileController {

    @Value("${meimei.image.savePath}")
    private String filePath;

    @GetMapping("file/**")
    public String index(HttpServletRequest request, Model model) {

        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        path = path.replace("/file", "");
        String basePath = System.getProperty("user.dir") + "/" + filePath;
        String tmpPath = basePath.replace("\\", "/");
        if (path.length() != 0) {
            basePath += path;
        }
        File[] files = new File(basePath).listFiles();
        List<FileInfoDTO> folderList = new ArrayList<>();
        List<FileInfoDTO> fileList = new ArrayList<>();
        if (files != null && files.length != 0) {
            for (File file : files) {
                FileInfoDTO fileInfoDTO = new FileInfoDTO();
                fileInfoDTO.setName(file.getName());
                fileInfoDTO.setUpdateTime(new SimpleDateFormat("yyy-MM-dd HH:mm:ss").format(new Date(file.lastModified())));
                fileInfoDTO.setPath(file.getPath().replace("\\", "/").replace(tmpPath, ""));
                if (file.isDirectory()) {
                    fileInfoDTO.setSize(new FileProvider().formatFileSize(FileUtils.sizeOfDirectory(file)));
                    fileInfoDTO.setType(true);
                    folderList.add(fileInfoDTO);
                } else {
                    fileInfoDTO.setSize(new FileProvider().formatFileSize(file.length()));
                    fileInfoDTO.setType(false);
                    fileList.add(fileInfoDTO);
                }
            }
        }
        model.addAttribute("folderList", folderList);
        model.addAttribute("fileList", fileList);
        model.addAttribute("path", path);
        return "list";
    }


    @PostMapping("file/**")
    public String image(HttpServletRequest request, Model model) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String url = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            url += ":" + request.getServerPort();
        }
        path = path.replace("/file", "");
        url += "/images" + path;
        String[] split = url.split("/");
        model.addAttribute("path", path);
        model.addAttribute("url", url);
        model.addAttribute("name", split[split.length - 1]);
        return "image";
    }
}
