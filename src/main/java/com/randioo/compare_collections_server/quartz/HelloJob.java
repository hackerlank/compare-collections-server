package com.randioo.compare_collections_server.quartz;

import com.randioo.compare_collections_server.entity.po.Game;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author zsy
 * @Description:
 * @create 2017-11-27 18:11
 **/
public class HelloJob  implements Job{
    private Game game;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println(game.getGameId());
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
