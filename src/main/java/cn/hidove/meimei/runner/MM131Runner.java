package cn.hidove.meimei.runner;

import cn.hidove.meimei.service.impl.MM131ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MM131Runner implements ApplicationRunner {
    @Autowired
    private MM131ServiceImpl mm131ServiceImpl;

    @Value("${meimei.getHomePageList.start}")
    private boolean getHomePageListIsStart;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (getHomePageListIsStart) mm131();
    }

    @ResponseBody
    private void mm131() {
        List<String> list = new ArrayList<>();
        list.add("xinggan");
        list.add("qingchun");
        list.add("xiaohua");
        list.add("chemo");
        list.add("qipao");
        list.add("mingxing");
        new Thread(mm131ServiceImpl.startThread(list)).start();
        log.info(LocalDateTime.now() +"  爬取MM131全站列表线程 启动成功");
    }
}