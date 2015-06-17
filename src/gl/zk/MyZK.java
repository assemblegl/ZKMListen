package gl.zk;

import gl.global.Context;
import gl.global.MyObject;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.ACL;

public class MyZK extends MyObject{
	private ZooKeeper zk;
	private String hostPort;
	private Watcher watcher; 
	private String preHostPort;
	private Context context;
	/*public MyZK(String hostPort,Watcher watcher){		
					
	}*/	
	public MyZK(){}
	
	//spring server use
	public boolean init(Watcher watcher){
		super.init(this.getClass());		
		printInfo("is created");
		boolean res = false;
		try {
			zk = new ZooKeeper(hostPort, 3000, watcher);
			return true;
		} catch (IOException e) {			
			printError(e.getMessage());
		}
		return res;
	}
	
	//client first use
	public boolean init(String hostPort,Watcher watcher,Context context){
		super.init(this.getClass());		
		printInfo("is created");
		this.context = context;
		this.watcher = watcher;
		boolean res = false;
		try {
			zk = new ZooKeeper(hostPort,3000,watcher);
			return true;
		} catch (IOException e) {			
			printError(e.getMessage());
		}
		return res;
	}	
	
	//after first time,client use
	public boolean init(String hostPort,Watcher watcher){
		super.init(this.getClass());		
		this.hostPort = hostPort;
		//this.watcher = watcher;			
		boolean res = false;
		int reTryNum = context.getReTryNum();
		int sleeptime = context.getSleeptime();
		
		boolean notEndMode = false;
		do{
			for(int i = 0;i<reTryNum;i++){
				try {
					zk = new ZooKeeper(hostPort,3000,watcher);
					return true;
				} catch (IOException e) {			
					printError(e.getMessage());
					
					try {
						printError("start sleep:"+sleeptime);
						Thread.sleep(sleeptime);
					} catch (InterruptedException e1) {
						printError(e1.getMessage());
					}
				}
			}
			
			if(null == preHostPort) {
				return res;
			}else{
				notEndMode = true;
			}
			
			//preHostPort != null,come in not end module, while (true) ,must connect old host or new Host
			for(int i = 0;i<reTryNum;i++){
				try {
					zk = new ZooKeeper(preHostPort,3000,watcher);
					return true;
				} catch (IOException e) {			
					printError(e.getMessage());
					
					try {
						printError("start sleep:"+sleeptime);
						Thread.sleep(sleeptime);
					} catch (InterruptedException e1) {
						printError(e1.getMessage());
					}
				}
			}			
		}while(notEndMode);		
		
		return res;
	}
	
	public void resetHostConf(String newHostPort){
		preHostPort = hostPort;		
		if(init(newHostPort,watcher) == false) return ;
	}
	
	public boolean checkNodeChildren(String node,boolean forceDelete){
		printInfo("checkNodeChildren,node:"+node+",forceDelete:"+forceDelete);
    	try {
			List<String> childList = zk.getChildren(node, false);
			if(childList.size()>0) {
				printError("error! node:"+node+" is not null");
				if(forceDelete == true){
					for(String childnode:childList){						
						deleteNode(node+"/"+childnode);
						printInfo("delete node:"+node+"/"+childnode);
					}
					return true;
				}				
				return false;
			}else{
				return true;
			}
									
		} catch (KeeperException | InterruptedException e) {			
			printError(e.getMessage());
			return false;
		}
    }
	
	public void checkNodeAndCreate(String node,int cmode){
		//printInfo("checkNodeAndCreate,node:"+node+",cmode:"+cmode);
		if(cmode <0 || cmode >3) return;
		
    	try {
			if(null == zk.exists(node, false)){
				
				if(cmode == 0) zk.create(node,node.getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
				
				if(cmode == 1) zk.create(node,node.getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
				
				if(cmode == 2)  zk.create(node,node.getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT_SEQUENTIAL);
				
				if(cmode == 3)  zk.create(node,node.getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
								
			}
		} catch (KeeperException | InterruptedException e) {
			printError(e.getMessage());
		}
    }
	
	public boolean deleteNode(String node){
		//printInfo("deleteNode,node:"+node);
		try {
			zk.delete(node,-1);
		} catch (InterruptedException | KeeperException e) {			
			printError(e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public void getChildren(String path,boolean watch,ChildrenCallback cb){
		printInfo("void getChildren,path:"+path+",watch:"+watch);
		zk.getChildren(path,watch,cb,null);
	}
	
	public  List<String> getChildren(String path, boolean watch){
		printInfo("List<String> getChildren,path:"+path+",watch:"+watch);
		List<String> reslist = null;
		try {
			reslist = zk.getChildren(path, watch);
		} catch (KeeperException | InterruptedException e) {
			printError(e.getMessage());
		}
		
		return reslist;
	}
	
	public byte[] getData(String node){
		//printInfo("getData,node:"+node);
		byte[] b = null;
		try {
			b = zk.getData(node,false,null);
		} catch (KeeperException | InterruptedException e) {			
			printError(e.getMessage());
		}
		
		return b;
	}

	public boolean setData(String path, byte[] data){
		//printInfo("setData,path:"+path+",data:"+data);
		boolean res = false;
		try {
			zk.setData(path,data,-1);
			res = true;
		} catch (KeeperException | InterruptedException e) {
			printError(e.getMessage());
		}
		
		return res;
	}
	
	public void exists(String path, boolean watch, StatCallback cb, Object ctx){
		printInfo("exists,path:"+path+",watch:"+watch);
		zk.exists(path,watch, cb,ctx);
	}
	
	public String create(String path,String data,CreateMode createMode){
		printInfo("create,path:"+path+",data:"+data+",createMode:"+createMode);
		String res = null;
		try {
			res = zk.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, createMode);
		} catch (KeeperException | InterruptedException e) {
			printError(e.getMessage());
		}		
		return res;
	}

	public String create(String path,byte[] data,CreateMode createMode){
		printInfo("create,path:"+path+",data:"+data+",createMode:"+createMode);
		String res = null;
		try {
			res = zk.create(path, data, Ids.OPEN_ACL_UNSAFE, createMode);
		} catch (KeeperException | InterruptedException e) {
			printError(e.getMessage());
		}		
		return res;
	}
	
	public ZooKeeper getZk() {
		return zk;
	}

	public void setZk(ZooKeeper zk) {
		this.zk = zk;
	}

	public String getHostPort() {
		return hostPort;
	}

	public void setHostPort(String hostPort) {
		this.hostPort = hostPort;
	}
	
}
