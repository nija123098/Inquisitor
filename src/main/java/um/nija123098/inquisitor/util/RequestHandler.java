package um.nija123098.inquisitor.util;

import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Made by nija123098 on 10/10/2016
 */
public class RequestHandler {
    private static final Timer REQUEST_TIMER = new Timer();
    private static final AtomicBoolean OFF = new AtomicBoolean(false);
    public static void request(Request request){
        RequestBuffer.request(() -> {
            try{request.request();
            } catch (DiscordException e) {
                Log.error("Error in request due to DiscordException for reason " + e.getMessage());
            } catch (MissingPermissionsException e) {
                request.missingPermissions(e);
            }
        });
    }
    public static void request(long millis, Request request){
        REQUEST_TIMER.add(millis, request);
    }
    public static void schedule(long millis, Request request){
        REQUEST_TIMER.schedule(millis, request);
    }
    public static void turnOff(){
        OFF.set(true);
    }
    @FunctionalInterface
    public interface Request{
        void request() throws DiscordException, RateLimitException, MissingPermissionsException;
        default void missingPermissions(MissingPermissionsException e){
        }
    }
    private static class Timer implements Runnable {
        private final Map<Long, List<Request>> requestMap;
        private Timer() {
            this.requestMap = new ConcurrentHashMap<>();
            new Thread(this).start();
        }
        public void add(long millis, RequestHandler.Request request){
            millis += System.currentTimeMillis();
            schedule(millis, request);
        }
        public void schedule(long millis, RequestHandler.Request request){
            List<RequestHandler.Request> requests = this.requestMap.computeIfAbsent(millis, k -> new ArrayList<>(1));
            requests.add(request);
        }
        @Override
        public void run() {
            List<RequestHandler.Request> requests;
            long previous = System.currentTimeMillis();
            long delta;
            while (true){
                if (OFF.get()){
                    break;
                }
                delta = System.currentTimeMillis() - previous;
                if (delta > 0){
                    ++previous;
                    requests = this.requestMap.get(previous);
                    this.requestMap.remove(previous);
                    if (requests != null){
                        requests.forEach(request -> {
                            try{request.request();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        });
                    }
                }
            }
        }
    }
}
