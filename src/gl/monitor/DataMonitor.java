package gl.monitor;

import gl.global.MyObject;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DataMonitor extends MyObject implements Watcher { //, StatCallback
	private MyMonitor mymonitor;
    private Watcher chainedWatcher;
    private DataMonitorListener listener;
    
    boolean dead;
    byte prevData[];
    
    public DataMonitor(Watcher chainedWatcher,DataMonitorListener listener,Watcher watcher) {        
        super.init(this.getClass());
    	this.chainedWatcher = chainedWatcher;
        this.listener = listener;
        
        ApplicationContext ctx = new ClassPathXmlApplicationContext("gl/conf/applicationContext.xml");
        mymonitor = (MyMonitor)ctx.getBean("mymonitor");

        if(mymonitor.init(watcher) == false){
        	dead = true;
        	listener.closing(-1);
        }                    
    }
    
    @SuppressWarnings("incomplete-switch")
	public void process(WatchedEvent event) {
    	printInfo("process in,type:"+event.getType());
        String path = event.getPath();
        if (event.getType() == Event.EventType.None) {
            // We are are being told that the state of the
            // connection has changed
            switch (event.getState()) {
            case SyncConnected:
                // In this particular example we don't need to do anything
                // here - watches are automatically re-registered with 
                // server and any watches triggered while the client was 
                // disconnected will be delivered (in order of course)
                break;
            case Expired:
                // It's all over
                //dead = true;
                //listener.closing(KeeperException.Code.SessionExpired);
                break;
            }
        } else {
        	mymonitor.Process(path);      	
        }
                
        //Watcher continue
        if (chainedWatcher != null) {
            chainedWatcher.process(event);
        }
    }
    
   /* @SuppressWarnings("deprecation")
	public void processResult(int rc, String path, Object ctx, Stat stat) {
    	System.out.println("processResult in");
        boolean exists;
        switch (rc) {
        case Code.Ok:
            exists = true;
            break;
        case Code.NoNode:
            exists = false;
            break;
        case Code.SessionExpired:
        case Code.NoAuth:
            dead = true;
            //listener.closing(rc);
            return;
        default:
            // Retry errors
            zk.exists(zNode, true, this, null);
            return;
        }

        byte b[] = null;
        if (exists) {
            try {
                b = zk.getData(znode, false, null);
            } catch (KeeperException e) {
                // We don't need to worry about recovering now. The watch
                // callbacks will kick off any exception handling
                e.printStackTrace();
            } catch (InterruptedException e) {
                return;
            }
        }
        if ((b == null && b != prevData)
                || (b != null && !Arrays.equals(prevData, b))) {
            //listener.exists(b);
            prevData = b;
        }
    }*/
}