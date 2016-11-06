package um.nija123098.inquisitor.bot;

import um.nija123098.inquisitor.command.Registry;
import um.nija123098.inquisitor.util.FileHelper;
import um.nija123098.inquisitor.util.Log;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Made by nija123098 on 11/6/2016
 */
public class TimerBot implements Runnable {
    private Thread thread;
    private final List<TimerTask> tasks;
    public TimerBot() {
        this.tasks = new ArrayList<TimerTask>();
        thread = new Thread(this);
        for (String path : FileHelper.getPaths("timers")) {
            List<String> strings = FileHelper.getStrings(path);
            Class clazz = Registry.getTimerTask(strings.get(strings.size() - 1));
            if (clazz != null){
                strings.remove(strings.size() - 1);
                for (Constructor constructor : clazz.getConstructors()) {
                    try{constructor.newInstance(strings);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public void register(TimerTask task){
        synchronized (this.tasks){
            this.tasks.add(task);
        }
    }
    public void close(){
        synchronized (this.tasks){
            FileHelper.cleanDir("timers");
            this.tasks.forEach(timerTask -> {
                List<String> strings = timerTask.close();
                strings.add(timerTask.getClass().getName());
                FileHelper.writeStrings("timers\\" + FileHelper.getUniqueName("timers"), strings);
            });
        }
        this.thread = null;
    }
    @Override
    public void run() {
        long last = System.currentTimeMillis();
        while (true){
            if (this.thread == null){
                break;
            }
            long n = System.currentTimeMillis();
            long delta = n - last;
            last = n;
            if (delta > 0){
                if (delta > 1){
                    Log.warn("Timer Delta too long: " + delta);
                }
                synchronized (tasks){
                    tasks.forEach(TimerTask::tick);
                }
            }
        }
    }
}
