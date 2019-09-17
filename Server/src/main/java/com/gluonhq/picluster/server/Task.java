package com.gluonhq.picluster.server;

import java.util.UUID;

import com.gluonhq.cloudlink.enterprise.sdk.javaee.domain.ObjectData;

public class Task {

    final String id;
    String url;
    String answer;
    boolean processing = false;

    public Task(ObjectData rawBlock) {
        this.id = rawBlock.getUid();
        this.url = rawBlock.getPayload();
    }

}
