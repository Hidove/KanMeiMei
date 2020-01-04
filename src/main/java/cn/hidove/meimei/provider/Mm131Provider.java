package cn.hidove.meimei.provider;


import cn.hidove.meimei.model.ImageListModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class Mm131Provider {


    @Autowired
    private HttpProvieder httpProvieder;

    @Value("${mm131.namedWithId}")
    private boolean namingRules;

    public Map<String, String> getList(
            @RequestParam(value = "html") String html
    ) {
//        String html = httpProvieder.get(url, "https://www.mm131.net", "gbk");
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select(".main > .public-box > dd");
        Map MMMap = new HashMap();
        for (Element element : elements) {
            Elements elementsByTag = element.getElementsByTag("a");
            String title = elementsByTag.text();
            String link = elementsByTag.attr("href");
            if (link.contains("http")) {
                MMMap.put(title, link);
            }
        }
        return MMMap;
    }

    public List<String> getImageUrl(
            @RequestParam(value = "url", defaultValue = "1") String url,
            @RequestParam(value = "page", defaultValue = "1") Integer page
    ) {
        //https://www.mm131.net/xinggan/5297.html
        String id = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf(".html"));
        //https://img1.mmmw.net/pic/5297/2.jpg
        String response = httpProvieder.get(url, "https://www.mm131.net", "gbk");
        Document doc = Jsoup.parse(response);
        Elements select = doc.select("body > div.content > div.content-page > span.page-ch");
        String maxPageSize = select.first().text();
        maxPageSize = maxPageSize.replace("共", "");
        maxPageSize = maxPageSize.replace("页", "");
        List imageList = new ArrayList();
        for (int i = 1; i <= Integer.parseInt(maxPageSize); i++) {
            imageList.add("https://img1.mmmw.net/pic/" + id + "/" + i + ".jpg");
        }
        return imageList;
    }


    public String download(
            @RequestParam(value = "url", defaultValue = "1") String url,
            @RequestParam(value = "path", defaultValue = "/meimei") String path,
            @RequestParam(value = "title", defaultValue = "meimei.md") String title
    ) {
        byte[] response = httpProvieder.download(url, "https://www.mm131.net");
        if (response == null) {
            return null;
        }
        String[] split = url.split("/");
        String fileName = split[split.length - 1];
        if (namingRules) {
            path = path + split[split.length - 2];
        } else {
            path = path + title;
        }
        httpProvieder.write((LocalDateTime.now().toString() + "  BY  Hidove Ivey").getBytes(), path, title + ".md");
        return httpProvieder.write(response, path, fileName);
    }

    public Map<String, String> getHomePageList() {
        String url = "https://www.mm131.net/";
        String response = httpProvieder.get(url, "https://www.mm131.net", "gbk");
        Document doc = Jsoup.parse(response);
        Elements elements = doc.select(".main_top > ul > li.left-list_li");
        Map MMMap = new HashMap();
        for (Element element : elements) {
            Elements elementsByTag = element.getElementsByTag("a");
            String title = elementsByTag.text();
            String link = elementsByTag.attr("href");
            if (link.contains("http")) {
                MMMap.put(title, link);
            }
        }
        return MMMap;
    }

}
