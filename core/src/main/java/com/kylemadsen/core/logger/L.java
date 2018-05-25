package com.kylemadsen.core.logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class L {

    public static void v(String message, Object... args) {
        log.v(message, args);
    }

    public static void v(Throwable t, String message, Object... args) {
        log.v(t, message, args);
    }

    public static void d(String message, Object... args) {
        log.d(message, args);
    }

    public static void d(Throwable t, String message, Object... args) {
        log.d(t, message, args);
    }

    public static void i(String message, Object... args) {
        log.i(message, args);
    }

    public static void i(Throwable t, String message, Object... args) {
        log.i(t, message, args);
    }

    public static void w(String message, Object... args) {
        log.w(message, args);
    }

    public static void w(Throwable t, String message, Object... args) {
        log.w(t, message, args);
    }

    public static void e(String message, Object... args) {
        log.e(message, args);
    }

    public static void e(Throwable t, String message, Object... args) {
        log.e(t, message, args);
    }

    public static void add(ILogger log) {
        loggers.add(log);
    }

    public static void remove(ILogger log) {
        for (int i = 0, size = loggers.size(); i < size; i++) {
            if (loggers.get(i) == log) {
                loggers.remove(i);
                return;
            }
        }
        throw new IllegalArgumentException("Cannot remove log that was not added: " + log);
    }

    public static void removeAll() {
        loggers.clear();
    }

    static final List<ILogger> loggers = new CopyOnWriteArrayList<>();

    private static final ILogger log = new ILogger() {
        @Override public void v(String message, Object... args) {
            for (ILogger log : loggers) {
                log.v(message, args);
            }
        }

        @Override public void v(Throwable t, String message, Object... args) {
            for (ILogger log : loggers) {
                log.v(t, message, args);
            }
        }

        @Override public void d(String message, Object... args) {
            for (ILogger log : loggers) {
                log.d(message, args);
            }
        }

        @Override public void d(Throwable t, String message, Object... args) {
            for (ILogger log : loggers) {
                log.d(t, message, args);
            }
        }

        @Override public void i(String message, Object... args) {
            for (ILogger log : loggers) {
                log.i(message, args);
            }
        }

        @Override public void i(Throwable t, String message, Object... args) {
            for (ILogger log : loggers) {
                log.i(t, message, args);
            }
        }

        @Override public void w(String message, Object... args) {
            for (ILogger log : loggers) {
                log.w(message, args);
            }
        }

        @Override public void w(Throwable t, String message, Object... args) {
            for (ILogger log : loggers) {
                log.w(t, message, args);
            }
        }

        @Override public void e(String message, Object... args) {
            for (ILogger log : loggers) {
                log.e(message, args);
            }
        }

        @Override public void e(Throwable t, String message, Object... args) {
            for (ILogger log : loggers) {
                log.e(t, message, args);
            }
        }
    };

    private L() {
    }
}
