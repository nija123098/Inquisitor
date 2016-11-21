package um.nija123098.inquisitor.util;

import sx.blah.discord.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Made by nija123098 on 10/10/2016
 */
public class RequestHandler {
    private static final Timer REUQEST_TIMER = new Timer();
    public static void request(Request request){
        RequestBuffer.request(() -> {
            try{request.request();
            } catch (DiscordException e) {
                Log.error("Error in request due to DiscordException for reason " + e.getMessage());
            } catch (MissingPermissionsException e) {
                request.missingPermissions();
            }
        });
    }
    public static void request(long millis, Request request){
        REUQEST_TIMER.add(millis, request);
    }
    @FunctionalInterface
    public interface Request{
        void request() throws DiscordException, RateLimitException, MissingPermissionsException;
        default void missingPermissions(){
        }
    }
    private static class Timer implements Runnable {
        private Map<Long, List<Request>> requestMap;
        private Timer() {
            this.requestMap = new ConcurrentHashMap<Long, List<Request>>();
            new Thread(this).start();
        }
        public void add(long millis, RequestHandler.Request request){
            millis += System.currentTimeMillis();
            List<RequestHandler.Request> requests = this.requestMap.get(millis);
            if (requests == null){
                requests = new ArrayList<Request>(1);
                this.requestMap.put(millis, requests);
            }
            requests.add(request);
        }
        @Override
        public void run() {
            List<RequestHandler.Request> requests;
            long previous = System.currentTimeMillis();
            long delta;
            while (true){
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
