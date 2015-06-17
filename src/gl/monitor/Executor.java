package gl.monitor;

import gl.global.Context;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class Executor implements Watcher, Runnable,DataMonitorListener
{   
    private DataMonitor dm;       
  
    public Executor() {    	
    	dm = new DataMonitor(null,this,this);
        run();
    }

    public void process(WatchedEvent event) {
        dm.process(event);
    }

    public void run() {
        try {
            synchronized (this) {
                while (!dm.dead) {
                    wait();
                }
            }
        } catch (InterruptedException e) {
        }
    }
    
    public void closing(int rc) {
        synchronized (this) {
            notifyAll();
        }
    }


}