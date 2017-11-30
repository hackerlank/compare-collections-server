package com.randioo.compare_collections_server.module.login.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import com.randioo.randioo_server_base.utils.TimeUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.randioo.compare_collections_server.cache.local.GameCache;
import com.randioo.compare_collections_server.dao.RoleDAO;
import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.close.service.CloseService;
import com.randioo.compare_collections_server.module.login.LoginConstant;
import com.randioo.compare_collections_server.module.login.component.LoginConfig;
import com.randioo.compare_collections_server.module.match.service.MatchService;
import com.randioo.compare_collections_server.module.role.service.RoleService;
import com.randioo.compare_collections_server.protocol.Entity.GameState;
import com.randioo.compare_collections_server.protocol.Entity.RoleData;
import com.randioo.compare_collections_server.protocol.Error.ErrorCode;
import com.randioo.compare_collections_server.protocol.Login.LoginGetRoleDataResponse;
import com.randioo.compare_collections_server.protocol.Login.SCLoginOtherSide;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.compare_collections_server.util.Tool;
import com.randioo.randioo_server_base.annotation.BaseServiceAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.db.GameDB;
import com.randioo.randioo_server_base.entity.RoleInterface;
import com.randioo.randioo_server_base.log.LoggerProxy;
import com.randioo.randioo_server_base.module.login.LoginHandler;
import com.randioo.randioo_server_base.module.login.LoginInfo;
import com.randioo.randioo_server_base.module.login.LoginModelConstant;
import com.randioo.randioo_server_base.module.login.LoginModelService;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.Ref;
import com.randioo.randioo_server_base.utils.SessionUtils;
import com.randioo.randioo_server_base.utils.StringUtils;

@BaseServiceAnnotation(value = "loginService", order = 1)
@Service("loginService")
public class LoginServiceImpl extends ObserveBaseService implements LoginService {

    private static Logger roleRootLogger = LoggerFactory.getLogger(Role.class);
    @Autowired
    private RoleDAO roleDao;

    @Autowired
    private LoginModelService loginModelService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private GameDB gameDB;

    @Autowired
    private MatchService matchService;

    @Autowired
    private CloseService closeService;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public void init() {
        // 初始化所有已经有过的帐号和昵称
        add(RoleCache.getNameSet(), roleDao.getAllNames());
        add(RoleCache.getAccountSet(), roleDao.getAllAccounts());

        loginModelService.setLoginHandler(new LoginHandlerImpl());
    }

    private void add(Map<String, String> map, List<String> list) {
        for (String str : list) {
            map.put(str, str);
        }
    }

    private class LoginHandlerImpl implements LoginHandler {

        @Override
        public RoleInterface getRoleInterfaceFromDBById(int roleId) {
            return roleDao.get(roleId);
        }

        @Override
        public RoleInterface getRoleInterfaceFromDBByAccount(String account) {
            return roleDao.getRoleByAccount(account);
        }

        @Override
        public void loginRoleModuleDataInit(RoleInterface roleInterface) {
            // 将数据库中的数据放入缓存中
            Role role = (Role) roleInterface;
            role.logger = LoggerProxy.proxyByName(roleRootLogger,
                    MessageFormat.format("[account:{0}]", role.getAccount()));
            roleService.roleInit(role);

            role.logger.info("登陆数据初始化");
        }

        @Override
        public boolean createRoleCheckAccount(LoginInfo info, Ref<Integer> errorCode) {
            // 账号姓名不可为空
            if (StringUtils.isNullOrEmpty(info.getAccount())) {
                errorCode.set(LoginConstant.CREATE_ROLE_NAME_SENSITIVE);
                return false;
            }

            return true;
        }

        @Override
        public RoleInterface createRole(LoginInfo loginInfo) {
            LoginConfig loginConfig = (LoginConfig) loginInfo;
            String account = loginConfig.getAccount();
            String name = loginConfig.getNickname();

            Role role = new Role();
            role.setAccount(account);
            role.setName(name);

            role.logger = LoggerProxy.proxyByName(roleRootLogger,
                    MessageFormat.format("[account:{0}]", role.getAccount()));

            roleService.newRoleInit(role);
            roleService.roleInit(role);

            role.logger.info("创建角色成功");

            SqlSession sqlSession = null;
            try {
                sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);

                sqlSession.getMapper(RoleDAO.class).insert(role);

                sqlSession.commit();
                sqlSession.clearCache();
            } catch (Exception e) {
                logger.error("", e);
                sqlSession.rollback();
                return null;
            } finally {
                if (sqlSession != null) {
                    sqlSession.close();
                }
            }
            return role;
        }

        @Override
        public void noticeOtherPlaceLogin(Object session) {
            SessionUtils.sc(session, SC.newBuilder().setSCLoginOtherSide(SCLoginOtherSide.newBuilder()).build());
        }

        @Override
        public void closeCallback(Object session) {
            Role role = (Role) RoleCache.getRoleBySession(session);
            try {
                closeService.asynManipulate(role);
            } catch (Exception e) {
                if (role != null && role.logger != null) {
                    role.logger.error("sessionClosed error:", e);
                }
                logger.error("", e);
            }
        }

    }

    @Override
    public GeneratedMessage getRoleData(LoginInfo loginInfo, Object ioSession) {

        Ref<Integer> errorCode = new Ref<>();

        RoleInterface roleInterface = loginModelService.getRoleData(loginInfo, errorCode, ioSession);

        if (roleInterface != null) {
            Role role = (Role) roleInterface;

            // 刷新用户头像
            LoginConfig loginConfig = (LoginConfig) loginInfo;
//            role.setHeadImgUrl(loginConfig.getHeadImageUrl());
//            role.setName(loginConfig.getNickname());

            role.logger.info("登陆成功 头像地址:{} , MAC地址:{}", loginConfig.getHeadImageUrl(), loginConfig.getMacAddress());

            return SC.newBuilder()
                    .setLoginGetRoleDataResponse(
                            LoginGetRoleDataResponse.newBuilder()
                                    .setErrorCode(ErrorCode.OK.getNumber())
                                    .setRoleData(getRoleData(role)))
                    .build();
        }

        ErrorCode errorEnum = null;
        switch (errorCode.get()) {
        case LoginModelConstant.GET_ROLE_DATA_NOT_EXIST:
            errorEnum = ErrorCode.NO_ROLE_DATA;
            break;
        case LoginModelConstant.GET_ROLE_DATA_IN_LOGIN:// TODO gaide
            errorEnum = ErrorCode.IN_LOGIN;
            break;
        }
        SC sc = SC.newBuilder()
                .setLoginGetRoleDataResponse(LoginGetRoleDataResponse.newBuilder().setErrorCode(errorEnum.getNumber()))
                .build();

        return sc;
    }

    @Override
    public RoleData getRoleData(Role role) {
        roleService.roleInit(role);

        int roleId = Tool.regExpression(role.getAccount(), "[0-9]*") ? Integer.parseInt(role.getAccount()) : role.getRoleId();
        Game game = GameCache.getGameMap().get(role.getGameId());
        // 游戏不存在或游戏已经结束,钥匙不存在
        String lockString = game == null || game.getGameState() == GameState.GAME_STATE_END ? null : game.getGameConfig()
                .getRoomId();
        RoleData.Builder builder = RoleData.newBuilder()
                .setRoleId(roleId)
                .setPoint(1000)
                .setSex(role.getSex())
                .setName(role.getName())
                .setHeadImageUrl(role.getHeadImgUrl() != null ? role.getHeadImgUrl() : "")
                .setRandiooCoin(role.getRandiooMoney())
                .setGold(role.getGold())
                .setRoomCard(role.getRoomCard());
        ByteString gameOverSCBytes = role.getGameOverSC();
        // 如果有录像数据就放入
        if (gameOverSCBytes != null) {
            builder.setGameOverSC(gameOverSCBytes);
        }

        if (lockString != null) {
            builder.setRoomId(lockString);
        }
        return builder.build();
    }

    @Override
    public Role getRoleById(int roleId) {
        RoleInterface roleInterface = loginModelService.getRoleInterfaceById(roleId);
        return roleInterface == null ? null : (Role) roleInterface;
    }

    @Override
    public Role getRoleByAccount(String account) {
        RoleInterface roleInterface = loginModelService.getRoleInterfaceByAccount(account);
        return roleInterface == null ? null : (Role) roleInterface;
    }
}
