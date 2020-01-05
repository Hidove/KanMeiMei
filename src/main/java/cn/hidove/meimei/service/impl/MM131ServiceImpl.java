package cn.hidove.meimei.service.impl;

import cn.hidove.meimei.mapper.ImageListMapper;
import cn.hidove.meimei.provider.HttpProvider;
import cn.hidove.meimei.provider.Mm131Provider;
import cn.hidove.meimei.service.MMService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class MM131ServiceImpl implements MMService {

    @Autowired
    private Mm131Provider mm1313Provider;

    @Autowired
    private ImageListMapper imageListMapper;

    @Autowired
    private HttpProvider httpProvider;

    @Value("${meimei.MaximumThreads}")
    private Integer MaximumThreads;

    @Value("${mm131.startPage}")
    private Integer startPage;

    @Value("${mm131.maxPage}")
    private Integer maxPage;

    private ThreadPoolExecutor poolExecutor;
    //获取整个网站的
    public String startThread(List<String> list) {
        poolExecutor = new ThreadPoolExecutor(2, MaximumThreads, 3, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        String status = "";
        for (String category : list) {
            poolExecutor.execute(()->{
                action(category);
            });
        }
        return LocalDateTime.now() + status;
    }

    private String action(String category) {
        String html = httpProvider.get("https://www.mm131.net/" + category, "https://www.mm131.net/", "gbk");
        Element last = Jsoup.parse(html).select("body > div.main > dl > dd.page > a").last();
        String href = last.attr("href");
        String[] split = href.split("[._]");
        if (split.length == 0) {
            log.error("获取分类数据失败");
            return "failed";
        }
        log.info("获取分类数据成功 URL=[https://www.mm131.net/" + category + "]");
        String categ = split[1];
        int tMaxPage = Integer.parseInt(split[2]);
        if (maxPage != 0 && tMaxPage > maxPage) {
            tMaxPage = maxPage;
        }
        int tStartPage = 1;

        if (startPage != 0 && tMaxPage > startPage) {
            tStartPage = startPage;
        }
        if (tStartPage > tMaxPage) {
            while (true) {
                new Thread(()->{
                    System.out.println(LocalDateTime.now() + "startPage比maxPage还要大是什么意思？直接崩溃吧~~~~");
                }).start();
            }
        }
        List<String> list = new ArrayList<>();
        for (int i = tStartPage; i <= tMaxPage; i++) {
            String url = "";
            if (i == 1) {
                url = "https://www.mm131.net/" + category + "/";
            } else {
                url = "https://www.mm131.net/" + category + "/list_" + categ + "_" + i + ".html";
            }
            list.add(url);
        }
        log.info("启动" +category+ "线程处理中");
        Threading(list);
        log.info("启动" +category+ "线程处理成功");
        return "success";
    }

    private void Threading(List<String> list) {
        for (int i = 0; i < list.size(); i ++) {
            int finalI = i;
            poolExecutor.execute(() -> {
                String res = httpProvider.get(list.get(finalI), "https://www.mm131.net/", "gbk");
                if (res == null) {
                    log.error("URL=[" + list.get(finalI) + "]" + " faild");
                    return;
                } else {
                    log.info("URL=[" + list.get(finalI) + "]" + " success");
                }
                String[] split = list.get(finalI).split("/");
                String category = split[split.length - 2];
                getListAndSave(res,category);
            });
        }
    }

    /**
     * 获取列表
     */
    private void getListAndSave(String html,String category) {
        Map<String, String> map = mm1313Provider.getList(html);
        if (map.size() == 0) {
            log.error("getListAndSave执行失败");
            return;
        }
        String sql = "INSERT ignore INTO meimei_list(`title`,`category`,`url`,`key`,`createtime`,`updatetime`) VALUES";
        int index = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sql += "('" + entry.getKey() + "','"+ category + "','" + entry.getValue() + "','mm131','" + System.currentTimeMillis() + "','0')";
            index++;
            if (index < map.size()) {
                sql += ",";
            }
        }
        imageListMapper.execute(sql);
    }
}
