package saviing.common.context;

import java.util.UUID;

public class TraceContext {
    
    private static final ThreadLocal<String> traceIdHolder = new ThreadLocal<>();
    
    public static void setTraceId(String traceId) {
        traceIdHolder.set(traceId);
    }
    
    public static String getTraceId() {
        return traceIdHolder.get();
    }
    
    public static String generateNewTraceId() {
        String traceId = UUID.randomUUID().toString();
        setTraceId(traceId);
        return traceId;
    }
    
    public static void clear() {
        traceIdHolder.remove();
    }
    
    public static boolean hasTraceId() {
        return traceIdHolder.get() != null;
    }
}