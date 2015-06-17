package gl.monitor;

import gl.global.Context;
import gl.global.MyNode;
import gl.global.MyObject;
import gl.zk.MyZK;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.KeeperException.Code;


public class MyMonitor extends MyObject implements AsyncCallback.ChildrenCallback{
	private MyZK myzk;
	private String zNode;
	private String zPersisNode;
	private String errorNode;
	private Context context;
	int preZNodeNum;	
		
	public boolean init(Watcher watcher){
		super.init(this.getClass());
		
		if(context.serverInit(watcher) == false ) return false;		
		this.zNode = context.getzNode();
		this.zPersisNode = context.getzPersisNode();
		this.errorNode = context.getErrorNode();				
		this.myzk = context.getMyzk();
		
		Map<String,MyNode> mapNode = context.getMapPathNode();
		for(String nodename:mapNode.keySet()){
			MyNode mynode = mapNode.get(nodename);
			
			if(mynode.isMustExists()){
				myzk.checkNodeAndCreate(mynode.NodeName,mynode.getNodeMode());
			}
			
			if(mynode.isMasterNeedcheckChildren()){
				myzk.checkNodeChildren(mynode.NodeName,(mynode.isMasterChildrenMustNull() == true)?true:false);
			}
			
			if(mynode.isMasterNeedListenChildren()){
				myzk.getChildren(mynode.NodeName,true,this); 
			}			
		}      
        return true;
	}		
	
	public void Process(String path){
		// Something has changed on the node, let's find out			
		if(null == path) return;
		
		MyNode mynode = context.getMapPathNode().get(path);
		
		if(null == mynode) {
			printError("warning!get from mapNode is null:node:"+path);
			return;
		}
		
		if(mynode.isMasterNeedListenChildren()){
			myzk.getChildren(path,true,this);
		}
	}	
	
	@SuppressWarnings("deprecation")
	public void processResult(int rc, String path, Object ctx, List<String> nodeList){
    	printInfo("go in child processResult in,rc:"+rc+",path:"+path);
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
            //dead = true;
            //listener.closing(rc);
            return;
        default:
            // Retry errors           
        	if(null == path) return;   		
    		MyNode mynode = context.getMapPathNode().get(path);    		
    		if(null == mynode) {
    			printError("warning!get from mapNode is null:node:"+path);
    			return;
    		}   		
    		if(mynode.isMasterNeedListenChildren()){
    			myzk.getChildren(path,true,this);
    		}        	
            return;
        }
       
        if (exists) {        	
        	if(path.equals(errorNode)){
        		processErrorNode(nodeList);
        	}else if(path.equals(zNode)){
        		processCheckNode(nodeList);
        	}       	            
        }
    	    	
    }
	
	private void processErrorNode(List<String> enodeList){
		printInfo("go in processErrorNode");		
    	for(String node:enodeList){    		    		
    		byte[] errorbytes = myzk.getData(errorNode+"/"+node);
            if(null == errorbytes){
            	printError("error! myzk.getData is null,node:"+errorNode+"/"+node);
            	return;
            }
            
            System.out.println("error content:"+new String(errorbytes));
            //call msg
            if(myzk.deleteNode(errorNode+"/"+node) == false) {                
            	printError("error! myzk.deleteNode,node:"+errorNode+"/"+node);
            	return;
            }            
            printInfo(errorNode+"/"+node+" is deleted");    		
    	}
    }
	
	@SuppressWarnings("unchecked")
	private void processCheckNode(List<String> znodeList){
		printInfo("go in processCheckNode");
		
    	int znodesize = znodeList.size();
    	
    	//znodesize++,new node come in 
    	if(znodesize >= preZNodeNum){
    		preZNodeNum = znodesize;
    		String newNode = context.getNewNode();
    		List<String> newNodelist = myzk.getChildren(newNode, false);
    		
    		
    		for(String node:newNodelist){
    			printInfo("client ip:"+node+" connected");
    			if(myzk.deleteNode(newNode+"/"+node) == false){
    				printError("error!myzk.deleteNode node:"+newNode+"/"+node);
    			}
    		}    		   		
    		return;
    	}
    	   
    	List<String> zplist = myzk.getChildren(zPersisNode, false);
		
		if(null == zplist){
			printError("error! myzk.getChildren,zplist is null");
			return;
		}
		if(zplist.size() == znodesize){
    		return;
    	}
		
		List<String> sublist = (List<String>)CollectionUtils.subtract(zplist, znodeList);
		for(String subnode:sublist){			
			if(myzk.deleteNode(zPersisNode+"/"+subnode) == false){
				printError("error!myzk.deleteNode node:"+subnode);
			}
			printError("error! node:"+subnode+" is not connected");
		}		 	   	
    }

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
		

}

