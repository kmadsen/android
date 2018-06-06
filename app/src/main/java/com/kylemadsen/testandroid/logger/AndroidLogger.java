package com.kylemadsen.testandroid.logger;

import android.util.Log;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.currentTimeMillis;

public enum AndroidLogger implements ILogger {
    INSTANCE;

    private static final Pattern ANONYMOUS_CLASS = Pattern.compile("\\$\\d+$");
    private static final ThreadLocal<String> NEXT_TAG = new ThreadLocal<>();

    private static String createTag() {
        String tag = NEXT_TAG.get();
        if (tag != null) {
            NEXT_TAG.remove();
            return tag;
        }

        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (stackTrace.length < 6) {
            throw new IllegalStateException(
                    "Synthetic stack trace didn't have enough elements");
        }
        tag = stackTrace[5].getClassName();
        Matcher m = ANONYMOUS_CLASS.matcher(tag);
        if (m.find()) {
            tag = m.replaceAll("");
        }
        return tag.substring(tag.lastIndexOf('.') + 1);
    }

    public static ILogger getInstance() {
        return INSTANCE;
    }

    static String formatString(String message, Object... args) {
        // If no varargs are supplied, treat it as a request to log the string without formatting.
        return args.length == 0 ? message : String.format(message, args);
    }

    @Override public void v(String message, Object... args) {
        logMessage(Log.VERBOSE, formatString(message, args), null);
    }

    @Override public void v(Throwable t, String message, Object... args) {
        logMessage(Log.VERBOSE, formatString(message, args), t);
    }

    @Override public void d(String message, Object... args) {
        logMessage(Log.DEBUG, formatString(message, args), null);
    }

    @Override public void d(Throwable t, String message, Object... args) {
        logMessage(Log.DEBUG, formatString(message, args), t);
    }

    @Override public void i(String message, Object... args) {
        logMessage(Log.INFO, formatString(message, args), null);
    }

    @Override public void i(Throwable t, String message, Object... args) {
        logMessage(Log.INFO, formatString(message, args), t);
    }

    @Override public void w(String message, Object... args) {
        logMessage(Log.WARN, formatString(message, args), null);
    }

    @Override public void w(Throwable t, String message, Object... args) {
        logMessage(Log.WARN, formatString(message, args), t);
    }

    @Override public void e(String message, Object... args) {
        logMessage(Log.ERROR, formatString(message, args), null);
    }

    @Override public void e(Throwable t, String message, Object... args) {
        logMessage(Log.ERROR, formatString(message, args), t);
    }

    private void logMessage(int priority, String message, Throwable t) {
        if (message == null || message.length() == 0) {
            if (t != null) {
                message = getStackTraceString(t);
            } else {
                // Swallow message if it's null and there's no throwable.
                return;
            }
        } else if (t != null) {
            message += "\n" + getStackTraceString(t);
        }

        long threadId = Thread.currentThread().getId();
        String tag = createTag();
        if (message.length() < 4000) {
            String formattedMessage = String.format(Locale.ENGLISH, "%s_%d_%d: %s", tag, threadId, currentTimeMillis(), message);
            Log.println(priority, tag, formattedMessage);
        } else {
            String[] lines = message.split("\n");
            for (String line : lines) {
                String formattedMessage = String.format(Locale.ENGLISH, "%s_%d_%d: %s", tag, threadId, currentTimeMillis(), message);
                Log.println(priority, tag, formattedMessage);
            }
        }
    }

    public static String getStackTraceString(Throwable t) {
        StringBuilder sb = new StringBuilder();
        Thread thread = Thread.currentThread();
        sb.append(String.format("%s%n", t.getMessage()));
        for (StackTraceElement ste : t.getStackTrace()) {
            sb.append(String.format(Locale.ENGLISH, "(%d) %s:%d %s.%s%n", thread.getId(), ste.getFileName(), ste.getLineNumber(),
                    ste.getClassName(), ste.getMethodName()));
        }
        return sb.toString();
    }
}
