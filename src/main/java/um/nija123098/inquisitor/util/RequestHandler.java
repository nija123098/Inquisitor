package um.nija123098.inquisitor.util;

import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Made by nija123098 on 10/10/2016
 */
public class RequestHandler {
    private static final Timer REUQEST_TIMER = new Timer();
    private static volatile int count;
    public static void request(Request request){
        ++count;
        RequestBuffer.request(() -> {
            try {
                request.request();
                --count;
            } catch (DiscordException | MissingPermissionsException e) {
                if (e instanceof DiscordException && e.getMessage().contains("CloudFlair")){
                    throw new RateLimitException("CloudFlair thwarting", 100, "WHAT?", false);
                }// may remove thwarting at next version bump
                e.printStackTrace();
            }
        });
    }
    public static void request(long millis, Request request){
        REUQEST_TIMER.add(millis, request);
    }
    public static int requestCount() {
        return count;
    }
    @FunctionalInterface
    public interface Request{
        void request() throws DiscordException, RateLimitException, MissingPermissionsException;
    }
    private static class Timer implements Runnable {
        private Map<Long, List<Request>> requestMap;
        private Timer() {
            new Thread(this);
            this.requestMap = new ConcurrentHashMap<Long, List<Request>>();
        }
        public void add(long millis, RequestHandler.Request request){
            List<RequestHandler.Request> requests = this.requestMap.get(millis);
            if (request == null){
                requests = new ArrayList<Request>(1);
                this.requestMap.put(millis, requests);
            }
            requests.add(request);
        }
        @Override
        public void run() {
            List<RequestHandler.Request> requests;
            long previous = System.currentTimeMillis();
            long n = System.currentTimeMillis();
            long delta;
            while (true){
                delta = previous - n;
                if (delta > 0){
                    ++previous;
                    requests = this.requestMap.get(previous);
                    if (requests != null){
                        requests.forEach(RequestHandler::request);
                    }
                }
            }
        }
    }
}
