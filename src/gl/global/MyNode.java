package gl.global;

import java.io.Serializable;

import org.apache.zookeeper.CreateMode;

public class MyNode implements Serializable{	
	public String NodeName;
	public String comment;
	public boolean mustExists;
	
	//master
	public boolean masterNeedcheckChildren;
	public boolean masterChildrenMustNull;
	public boolean masterNeedListenChildren;
	public boolean masterNeedListenNode;
	public int NodeMode;
	public int childrenMode;
	
	//client
	public boolean clientNeedListenNode;
	
	public MyNode(){}
	
	public MyNode(String NodeName){
		this.NodeName = NodeName;
	}		
	
	/** 
	 * @param NodeName
	 * @param comment
	 * @param mustExists
	 * @param masterNeedcheckChildren
	 * @param masterChildrenMustNull
	 * @param masterNeedListenChildren
	 * @param masterNeedListenNode
	 * @param NodeMode
	 * @param childrenMode
	 * @param clientNeedListenNode
	 */
	public MyNode(String NodeName,String comment,boolean mustExists,boolean masterNeedcheckChildren,boolean masterChildrenMustNull,boolean masterNeedListenChildren,boolean masterNeedListenNode,
			int NodeMode,int childrenMode,boolean clientNeedListenNode){
		this.NodeName                   = NodeName                ;
		this.comment                    = comment                 ;
		this.mustExists                 = mustExists              ;
		
		//master
		this.masterNeedcheckChildren    = masterNeedcheckChildren ;
		this.masterChildrenMustNull     = masterChildrenMustNull  ;
		this.masterNeedListenChildren   = masterNeedListenChildren;
		this.masterNeedListenNode       = masterNeedListenNode    ;
		this.NodeMode                   = NodeMode                ;
		this.childrenMode               = childrenMode            ;
		
		//client
		this.clientNeedListenNode       = clientNeedListenNode    ;		
	}

	public String getNodeName() {
		return NodeName;
	}

	public void setNodeName(String nodeName) {
		NodeName = nodeName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isMustExists() {
		return mustExists;
	}

	public void setMustExists(boolean mustExists) {
		this.mustExists = mustExists;
	}

	public boolean isMasterNeedcheckChildren() {
		return masterNeedcheckChildren;
	}

	public void setMasterNeedcheckChildren(boolean masterNeedcheckChildren) {
		this.masterNeedcheckChildren = masterNeedcheckChildren;
	}

	public boolean isMasterChildrenMustNull() {
		return masterChildrenMustNull;
	}

	public void setMasterChildrenMustNull(boolean masterChildrenMustNull) {
		this.masterChildrenMustNull = masterChildrenMustNull;
	}

	public boolean isMasterNeedListenChildren() {
		return masterNeedListenChildren;
	}

	public void setMasterNeedListenChildren(boolean masterNeedListenChildren) {
		this.masterNeedListenChildren = masterNeedListenChildren;
	}

	public boolean isMasterNeedListenNode() {
		return masterNeedListenNode;
	}

	public void setMasterNeedListenNode(boolean masterNeedListenNode) {
		this.masterNeedListenNode = masterNeedListenNode;
	}

	public int getNodeMode() {
		return NodeMode;
	}

	public void setNodeMode(int nodeMode) {
		NodeMode = nodeMode;
	}

	public int getChildrenMode() {
		return childrenMode;
	}

	public void setChildrenMode(int childrenMode) {
		this.childrenMode = childrenMode;
	}

	public boolean isClientNeedListenNode() {
		return clientNeedListenNode;
	}

	public void setClientNeedListenNode(boolean clientNeedListenNode) {
		this.clientNeedListenNode = clientNeedListenNode;
	}
	
	
	
}
