package com.gluonhq.iotmonitor.monitor;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.concurrent.atomic.AtomicReference;

public class Model {

    static ObservableMap<String, Node> nodeMapper = FXCollections.observableHashMap();
    static ObservableList<Node> unresponsiveNodes = FXCollections.observableArrayList();
    
    static {
        nodeMapper.addListener((MapChangeListener<String, Node>) change -> {
            AtomicReference<Node> node = new AtomicReference<>();
            final ChangeListener<Boolean> unresponsiveListener = (o, oldValue, newValue) -> {
                if (newValue) {
                    unresponsiveNodes.add(node.get());
                } else {
                    unresponsiveNodes.remove(node.get());
                }
            };
            if (change.wasAdded()) {
                node.set(change.getValueAdded());
                change.getValueAdded().unresponsiveProperty().addListener(unresponsiveListener);
            } else if (change.wasRemoved()) {
                node.set(change.getValueRemoved());
                change.getValueRemoved().unresponsiveProperty().removeListener(unresponsiveListener);
            }
        });
    }

    static Node getNodeById(String s) {
        return nodeMapper.computeIfAbsent(s, id -> new Node(s));
    }

}
