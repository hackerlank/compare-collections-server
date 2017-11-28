package com.randioo.compare_collections_server.entity.po;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

import com.google.protobuf.Message;
import com.randioo.compare_collections_server.protocol.ClientMessage.CS;
import com.randioo.compare_collections_server.protocol.Heart.CSHeart;
import com.randioo.compare_collections_server.protocol.Heart.HeartRequest;
import com.randioo.compare_collections_server.protocol.Heart.HeartResponse;
import com.randioo.compare_collections_server.protocol.Heart.SCHeart;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

public class ProtoHeartFactory implements KeepAliveMessageFactory {

    private Message heartRequest = CS.newBuilder().setHeartRequest(HeartRequest.newBuilder()).build();
    private Message heartResponse = SC.newBuilder().setHeartResponse(HeartResponse.newBuilder()).build();
    private Message scHeart = SC.newBuilder().setSCHeart(SCHeart.newBuilder()).build();
    private Message csHeart = CS.newBuilder().setCSHeart(CSHeart.newBuilder()).build();

    @Override
    public Object getRequest(IoSession arg0) {
        return scHeart;
    }

    @Override
    public Object getResponse(IoSession arg0, Object arg1) {
        return heartResponse;
    }

    @Override
    public boolean isRequest(IoSession arg0, Object arg1) {
        boolean isRequest = heartRequest.equals(arg1);
        return isRequest;
    }

    @Override
    public boolean isResponse(IoSession arg0, Object arg1) {
        boolean isResponse = csHeart.equals(arg1);
        return isResponse;
    }

}