package org.example.event;

import java.util.concurrent.CopyOnWriteArrayList;

public class DataChangeEventBus {

    public interface DataChangeListener {
        void onDataChanged();
    }

    private static final CopyOnWriteArrayList<DataChangeListener> listeners = new CopyOnWriteArrayList<>();

    public static void onRegister(DataChangeListener listener) {
        if (listener != null) {
            listeners.addIfAbsent(listener);
        }
    }

    public static void onUnregister(DataChangeListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    public static void notifyChange() {
        for (DataChangeListener l : listeners) {
            try {
                l.onDataChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void clearAll() {
        listeners.clear();
    }
}
