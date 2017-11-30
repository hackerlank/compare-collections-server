package com.randioo.compare_collections_server;

import com.randioo.randioo_server_base.config.ConfigLoader;
import com.randioo.randioo_server_base.config.GlobleArgsLoader;
import com.randioo.randioo_server_base.config.GlobleJsonLoader;
import com.randioo.randioo_server_base.config.GlobleXmlLoader;
import com.randioo.randioo_server_base.init.GameServer;
import com.randioo.randioo_server_base.log.LogSystem;
import com.randioo.randioo_server_base.sensitive.SensitiveWordDictionary;
import com.randioo.randioo_server_base.utils.SpringContext;

/**
 * Hello world!
 *
 */
public class compare_collections_serverApp {

    /**
     * @param args2
     * @author wcy 2017年8月17日
     */
    public static void main(String[] args) {
        GlobleXmlLoader.init("./server.xml");
        GlobleJsonLoader.init("./config.json");
        GlobleArgsLoader.init(args);

        LogSystem.init(compare_collections_serverApp.class);

        ConfigLoader.loadConfig("com.randioo.compare_collections_server.entity.file", "./config.zip");
        SensitiveWordDictionary.readAll("./sensitive.txt");

        SpringContext.initSpringCtx("classpath:ApplicationContext.xml");

        ((GameServer) SpringContext.getBean(GameServer.class)).start();
    }
}
