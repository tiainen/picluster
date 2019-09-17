package com.gluonhq.picluster.server;

import com.gluonhq.cloudlink.enterprise.sdk.javaee.CloudLinkClient;
import com.gluonhq.cloudlink.enterprise.sdk.javaee.domain.ObjectData;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ExternalRequestHandler {

    private static final Logger logger = Logger.getLogger(ExternalRequestHandler.class.getName());

    public static final String GLUONLIST_BLOCKS = "blocks-v1";
    private static final long PROCESS_BLOCKS_INTERVAL = 10;

    private static final int TIMEOUT_SECONDS = 10;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final AutonomousDatabaseWriter autonomousDatabaseWriter;
    private final CloudLinkClient cloudLinkClient;

    public ExternalRequestHandler(AutonomousDatabaseWriter autonomousDatabaseWriter, CloudLinkClient cloudLinkClient) {
        this.autonomousDatabaseWriter = autonomousDatabaseWriter;
        this.cloudLinkClient = cloudLinkClient;
    }

    public void startListening() {
        this.scheduler.scheduleAtFixedRate(() -> processBlocks(), 0, PROCESS_BLOCKS_INTERVAL, SECONDS);
    }

    private void processBlocks() {
        try {
            List<ObjectData> rawBlocks = cloudLinkClient.getList(GLUONLIST_BLOCKS, Function.identity());
            logger.info("Processing blocks, got: " + rawBlocks.size());
            rawBlocks.stream()
                    .map(Task::new)
                    .filter(task -> !TaskQueue.taskAlreadyAdded(task))
                    .forEach(TaskQueue::add);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Something went wrong while processing blocks from Gluon CloudLink.", e);
        }
    }
}
