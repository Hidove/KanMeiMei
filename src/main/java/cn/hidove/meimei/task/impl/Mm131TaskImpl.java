package cn.hidove.meimei.task.impl;

import cn.hidove.meimei.mapper.ImageListMapper;
import cn.hidove.meimei.mapper.ImageUrlMapper;
import cn.hidove.meimei.model.ImageListModel;
import cn.hidove.meimei.model.ImageUrlModel;
import cn.hidove.meimei.provider.Mm131Provider;
import cn.hidove.meimei.task.MMTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@Configuration
@Slf4j
@EnableScheduling
public class Mm131TaskImpl implements MMTask {

    @Autowired
    private Mm131Provider mm131Provider;

    @Autowired
    private ImageListMapper imageListMapper;

    @Autowired
    private ImageUrlMapper imageUrlMapper;

    @Value("${meimei.image.savePath}")
    private String imageSavePath;


    @Scheduled(cron = "${cron.getImageUrl}")
    public void getImageUrl() {
        List<ImageListModel> imageListModels = imageListMapper.imageUrl();
        if (imageListModels.size() == 0) {
            log.warn("获取图片主题Url失败，可能数据库已经空啦~~~ status：" + " faild");
            return;
        }
        ImageListModel imageUrl = imageListModels.get(0);
        if (!imageUrl.getUrl().contains("http")) {
            imageListMapper.update(imageUrl.getId(), System.currentTimeMillis());
            log.warn("获取主题地址 URL=[" + imageUrl.getUrl() + "] faild");
            return;
        }
        List<String> list = mm131Provider.getImageUrl(imageUrl.getUrl(), 1);
        String sql = "INSERT ignore INTO meimei_image(`parent_id`,`title`,`url`,`key`,`createtime`,`updatetime`) VALUES";
        for (int i = 0; i < list.size(); i++) {
            sql += "('" + imageUrl.getId() + "','" + imageUrl.getTitle() + "','" + list.get(i) + "','mm131'," + System.currentTimeMillis() + ",0)";
            if (i < list.size() - 1) {
                sql += ",";
            }
        }
        imageUrlMapper.execute(sql);
        imageListMapper.update(imageUrl.getId(), System.currentTimeMillis());
        log.info("获取主题地址 URL=[" + imageUrl.getUrl() + "] success");
    }

    @Scheduled(cron = "${cron.download}")
    public void download(
    ) {
        List<ImageUrlModel> imageUrlModels = imageUrlMapper.imageUrl();
        if (imageUrlModels.size() == 0) {
            log.warn("获取下载任务失败，可能数据库已经空啦~~~ status：" + " faild");
            return;
        }
        ImageUrlModel imageUrl = imageUrlModels.get(0);
        if (!imageUrl.getUrl().contains("http")) {
            imageListMapper.update(imageUrl.getId(), System.currentTimeMillis());
            log.error("Download URL=[" + imageUrl.getUrl() + "]" + " faild");
            return;
        }
        if (!imageSavePath.endsWith("/")) imageSavePath += "/";
        String path = System.getProperty("user.dir") + "/" + imageSavePath;
        mm131Provider.download(imageUrl.getUrl(), path, imageUrl.getTitle());
        imageUrlMapper.updateUpdatetimeById(imageUrl.getId(), System.currentTimeMillis());
        log.info("Download URL=[" + imageUrl.getUrl() + "]" + " success");
    }


    /**
     * 爬取首页更新的内容
     */
    @Scheduled(cron = "${cron.getHomePageList}")
    public void getHomePageList() {
        Map<String, String> map = mm131Provider.getHomePageList();
        String sql = "INSERT ignore INTO meimei_list(`title`,`url`,`key`,`createtime`,`updatetime`) VALUES";
        Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();
        Integer index = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sql += "('" + entry.getKey() + "','" + entry.getValue() + "','mm131','" + System.currentTimeMillis() + "','0')";
            index++;
            if (index < map.size()) {
                sql += ",";
            }
        }
        if (map.size() > 0) {
            log.info("获取首页数据列表成功");
        } else {
            log.info("获取首页数据列表失败");
        }
        imageListMapper.execute(sql);
        LocalDateTime.now();
    }
}
