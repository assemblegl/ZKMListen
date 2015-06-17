package gl.global;

import gl.zk.MyZK;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;

public class Context extends MyObject{
	private MyZK myzk;
	private String zNode;
	private String zPersisNode;
	private String errorNode;
	private String confNode;
	private String dataNode;
	private String signDataNode;
	private String runNameNode;
	private String runLogNode;
	
	private String signRunNode;
	private String hostConfNode;
	private String newNode;	
	private String localhost = null;
	private Map<String,MyNode> mapNameNode;
	private Map<String,MyNode> mapPathNode;
	private int reTryNum;
	private int sleeptime;
	
	public Context(){
		super.init(this.getClass());				
	}
	
	public boolean serverInit(Watcher watcher){				
		if(myzk.init(watcher) == false) return false;
		
		initNode();
		if(saveParameter(confNode) == false) return false;		
		return true;
	}
	
	private String getlocalIP() throws Exception{
		String ip = null;
		InetAddress addr = null;
		Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
		while (enumeration.hasMoreElements()) {
		    NetworkInterface networkInterface = enumeration.nextElement();
		 
		    if (networkInterface.isUp()) {		        
		        Enumeration<InetAddress> addressEnumeration = networkInterface.getInetAddresses();
		        
		        while (addressEnumeration.hasMoreElements()) {
		        	 addr = addressEnumeration.nextElement();
		        	 if(addr.isLoopbackAddress() || addr.isLinkLocalAddress()) continue;
		        	 
		        	 //local ip
		        	 if(addr.isSiteLocalAddress()){
		        		 ip = addr.getHostAddress(); 
		        	 //outer net ip
		        	 }else{
		        		 ip = addr.getHostAddress();
		        		 return ip;
		        	 }		        			        	 
		        }		        		        
		    }
		}
		
		printInfo("isSiteLocalAddress:"+addr.isSiteLocalAddress()+",ip:"+ip);
   	 
   	 	//not have outer net ip,get local anyone
		return ip;
		
	}
	
	public boolean clientInit(String hostPort,Watcher watcher,String confNode){
		try {
			localhost = getlocalIP();
		} catch (Exception e) {
			printError(e.getMessage());
			return false;
		}
		
		if(null == localhost){
			printError("localhost is null,break");
			return false;
		}		
		
		this.confNode = confNode;
		myzk = new MyZK();
		if(myzk.init(hostPort,watcher,this) == false) return false;
		
		if(loadParameter(confNode) == false) return false;
				
		if(connectServer(zNode,zPersisNode,newNode) == false){
			printError("error in connectServer,break");
			return false;
		}		
		return true;		
	}
	
	private boolean loadParameter(String conf){
		//load conf
		byte[] confbytes = myzk.getData(confNode);
		if(null == confbytes) {
			printError("confbytes is null,break");
			return false;
		}
		printInfo("confbytes:"+confbytes);				
				
		ByteArray2Object(confbytes);
		
		if(null == mapNameNode) {
			printError("mapNameNode is null,break");
			return false;
		}										
		
		initNode();		
		mapPathNode = buildMapPath(mapNameNode);
		return true;
	}
	
	private void initNode(){
		zNode = mapNameNode.get("zNode").NodeName;
		zPersisNode = mapNameNode.get("zPersisNode").NodeName;
		errorNode = mapNameNode.get("errorNode").NodeName;
		dataNode = mapNameNode.get("dataNode").NodeName;
		signDataNode = mapNameNode.get("signDataNode").NodeName;						
		runNameNode = mapNameNode.get("runNameNode").NodeName;
		signRunNode = mapNameNode.get("signRunNode").NodeName;
		hostConfNode = mapNameNode.get("hostConfNode").NodeName;
		newNode = mapNameNode.get("newNode").NodeName;
		confNode = mapNameNode.get("confNode").NodeName;
		runLogNode = mapNameNode.get("runLogNode").NodeName;
	}
	
	private Map<String,MyNode> buildMapPath(Map<String,MyNode> mapNode){
		Map<String,MyNode> mapNode2 = new HashMap<String,MyNode>();
		
		for(String nodename:mapNode.keySet()){		
			MyNode mynode = mapNode.get(nodename);
			mapNode2.put(mynode.NodeName,mynode);
		}
						
//		mapNode2.put(mapNode.get("zNode").NodeName,mapNode.get("zNode"));
//		mapNode2.put(mapNode.get("zPersisNode").NodeName,mapNode.get("zPersisNode"));
//		mapNode2.put(mapNode.get("errorNode").NodeName,mapNode.get("errorNode"));
//		mapNode2.put(mapNode.get("dataNode").NodeName,mapNode.get("dataNode"));
//		mapNode2.put(mapNode.get("signDataNode").NodeName,mapNode.get("signDataNode"));
//		mapNode2.put(mapNode.get("runNameNode").NodeName,mapNode.get("runNameNode"));
//		mapNode2.put(mapNode.get("signRunNode").NodeName,mapNode.get("signRunNode"));
//		mapNode2.put(mapNode.get("hostConfNode").NodeName,mapNode.get("hostConfNode"));
//		mapNode2.put(mapNode.get("newNode").NodeName,mapNode.get("newNode"));
		return mapNode2;
	}
	
	private boolean saveParameter(String conf){				
    	byte[] bparam = object2ByteArray();		
		if(null == bparam){
			printError("error in saveParameter,List2ByteArray return null,break");
			return false;
		}
		
		initNode();
		
		if(myzk.setData(conf,bparam) == false) {
			printError("error in myzk.setData,conf:"+conf+",break");
			return false;
		}
		
		
		mapPathNode = buildMapPath(mapNameNode);
		return true;    	
    }
	
	private boolean connectServer(String zNode,String zPersisNode,String newNode){				
		
		String strp = myzk.create(zPersisNode+"/"+localhost,localhost,CreateMode.PERSISTENT);
		String strn = myzk.create(newNode+"/"+localhost,localhost,CreateMode.PERSISTENT);
		String strz = myzk.create(zNode+"/"+localhost,localhost,CreateMode.EPHEMERAL);
		
		if( null == strz || null == strp || null == strn) return false;
		
		return true;
	}
	
	public byte[] object2ByteArray() {
		byte[] barray = null;
		try {
			ByteArrayOutputStream baos= new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(mapNameNode);
			oos.writeObject(reTryNum);
			oos.writeObject(sleeptime);
			barray = baos.toByteArray();			
		} catch (IOException e) {			
			printError(e.getMessage());			
		}
		return barray;
		
	}
	
	public void ByteArray2Object(byte[] barray) {		
		//Object object = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(barray));
			mapNameNode = (Map<String,MyNode>)ois.readObject();
			reTryNum = (Integer)ois.readObject();
			sleeptime = (Integer)ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			printError(e.getMessage());		
		}		
	}
		
	public MyZK getMyzk() {
		return myzk;
	}

	public void setMyzk(MyZK myzk) {
		this.myzk = myzk;
	}

	public String getzNode() {
		return zNode;
	}

	public void setzNode(String zNode) {
		this.zNode = zNode;
	}

	public String getzPersisNode() {
		return zPersisNode;
	}

	public void setzPersisNode(String zPersisNode) {
		this.zPersisNode = zPersisNode;
	}

	public String getErrorNode() {
		return errorNode;
	}

	public void setErrorNode(String errorNode) {
		this.errorNode = errorNode;
	}

	public String getConfNode() {
		return confNode;
	}

	public void setConfNode(String confNode) {
		this.confNode = confNode;
	}

	public String getDataNode() {
		return dataNode;
	}

	public void setDataNode(String dataNode) {
		this.dataNode = dataNode;
	}

	public String getSignDataNode() {
		return signDataNode;
	}

	public void setSignDataNode(String signDataNode) {
		this.signDataNode = signDataNode;
	}

	public String getRunNameNode() {
		return runNameNode;
	}

	public void setRunNameNode(String runNameNode) {
		this.runNameNode = runNameNode;
	}

	public String getSignRunNode() {
		return signRunNode;
	}

	public void setSignRunNode(String signRunNode) {
		this.signRunNode = signRunNode;
	}

	public String getHostConfNode() {
		return hostConfNode;
	}

	public void setHostConfNode(String hostConfNode) {
		this.hostConfNode = hostConfNode;
	}

	public int getReTryNum() {
		return reTryNum;
	}

	public void setReTryNum(int reTryNum) {
		this.reTryNum = reTryNum;
	}

	public int getSleeptime() {
		return sleeptime;
	}

	public void setSleeptime(int sleeptime) {
		this.sleeptime = sleeptime;
	}

	public Map<String, MyNode> getMapNameNode() {
		return mapNameNode;
	}

	public void setMapNameNode(Map<String, MyNode> mapNameNode) {
		this.mapNameNode = mapNameNode;
	}

	public Map<String, MyNode> getMapPathNode() {
		return mapPathNode;
	}

	public void setMapPathNode(Map<String, MyNode> mapPathNode) {
		this.mapPathNode = mapPathNode;
	}

	public String getNewNode() {
		return newNode;
	}

	public void setNewNode(String newNode) {
		this.newNode = newNode;
	}

	public String getRunLogNode() {
		return runLogNode;
	}

	public void setRunLogNode(String runLogNode) {
		this.runLogNode = runLogNode;
	}

	public String getLocalhost() {
		return localhost;
	}

	public void setLocalhost(String localhost) {
		this.localhost = localhost;
	}		
	
	
}
