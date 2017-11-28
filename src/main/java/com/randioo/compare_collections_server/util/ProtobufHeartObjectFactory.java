package com.randioo.compare_collections_server.util;

import com.google.protobuf.GeneratedMessage;
import com.randioo.compare_collections_server.protocol.ClientMessage.CS;
import com.randioo.compare_collections_server.protocol.Heart.CSHeart;
import com.randioo.compare_collections_server.protocol.Heart.HeartRequest;
import com.randioo.compare_collections_server.protocol.Heart.HeartResponse;
import com.randioo.compare_collections_server.protocol.Heart.SCHeart;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

public class ProtobufHeartObjectFactory {

    private GeneratedMessage heartRequest = CS.newBuilder().setHeartRequest(HeartRequest.newBuilder()).build();
    private GeneratedMessage heartResponse = SC.newBuilder().setHeartResponse(HeartResponse.newBuilder()).build();
    private GeneratedMessage ScHeart = SC.newBuilder().setSCHeart(SCHeart.newBuilder()).build();
    private GeneratedMessage CsHeart = CS.newBuilder().setCSHeart(CSHeart.newBuilder()).build();

    public GeneratedMessage getRequest() {
        return heartRequest;
    }

    public GeneratedMessage getResponse() {
        return heartResponse;
    }

    public GeneratedMessage getSC() {
        return ScHeart;
    }

    public GeneratedMessage getCS() {
        return CsHeart;
    }
}
