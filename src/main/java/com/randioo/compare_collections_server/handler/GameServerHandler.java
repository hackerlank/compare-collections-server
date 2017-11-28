package com.randioo.compare_collections_server.handler;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.module.close.service.CloseService;
import com.randioo.compare_collections_server.protocol.ClientMessage.CS;
import com.randioo.compare_collections_server.protocol.Heart.HeartResponse;
import com.randioo.compare_collections_server.protocol.Heart.SCHeart;
import com.randioo.compare_collections_server.protocol.Match.MatchJoinGameResponse;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.handler.GameServerHandlerAdapter;
import com.randioo.randioo_server_base.log.LogSystem;
import com.randioo.randioo_server_base.module.login.LoginModelService;

@Component
public class GameServerHandler extends GameServerHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private String scHeartStr = SCHeart.class.getSimpleName();
    private String heartResponseStr = HeartResponse.class.getSimpleName();
    private String matchJoinGameResponse = MatchJoinGameResponse.class.getSimpleName();
    @Autowired
    private CloseService closeService;

    @Autowired
    private LoginModelService loginModelService;

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        logger.info("roleId:" + session.getAttribute("roleId") + " sessionCreated");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        logger.info("roleId:" + session.getAttribute("roleId") + " sessionOpened");
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        logger.info("roleId:" + session.getAttribute("roleId") + " sessionClosed");

        loginModelService.offline(session);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {

    }

    @Override
    public void exceptionCaught(IoSession session, Throwable e) throws Exception {
        Role role = RoleCache.getRoleBySession(session);
        Logger printLogger = role == null ? logger : role.logger;
        printLogger.error("", e);
        session.close(true);
    }

    @Override
    public void messageReceived(IoSession session, Object messageObj) throws Exception {

        try {
            CS message = (CS) messageObj;
            Role role = RoleCache.getRoleBySession(session);
            Logger printLogger = role == null ? logger : role.logger;
            printLogger.info(" 收到消息{}", message);
            this.actionDispatcher(message, session);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        String messageStr = message.toString();
        if (messageStr.contains(scHeartStr) || messageStr.contains(heartResponseStr)
                || messageStr.contains(matchJoinGameResponse))
            return;

        Role role = RoleCache.getRoleBySession(session);
        Logger printLogger = role == null ? logger : role.logger;
        printLogger.info(LogSystem.pretty(message));
    }

}
