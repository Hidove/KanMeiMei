package cn.hidove.meimei.task;

public interface MMTask {
    /**
     * 获取图片地址
     * @return
     */
    void getImageUrl();
    /**
     * 下载图片地址
     */
    void download();
    /**
     * 爬取首页更新的内容
     */
    void getHomePageList();
}
