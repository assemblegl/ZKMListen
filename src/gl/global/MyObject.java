package gl.global;

import org.apache.log4j.Logger;

public class MyObject {
	private static Logger logger = Logger.getLogger(MyObject.class);
	private String className; 
	
	public void init(Class ClassName){
		this.className = ClassName.getName();
	}
	
	public void printError(String Content){		
		String ec=className+","+Content;				
		System.out.println(ec);
		logger.error(ec);
	}
	
	public void printInfo(String Content){
		String ec=className+","+Content;		
		System.out.println(ec);
		logger.info(ec);
	}

}
