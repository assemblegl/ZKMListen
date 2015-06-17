package gl.main;

import gl.monitor.Executor;

import org.apache.log4j.Logger;

public class Main {
	private static Logger logger = Logger.getLogger(Main.class);
	
	public static void main(String[] args) {
		
		//ApplicationContext ctx = new ClassPathXmlApplicationContext("gl/conf/applicationContext.xml");							
		/*Executor executor = (Executor)ctx.getBean("executor");	
		executor.run();					
		System.exit(0);*/

		 /*if (args.length != 2) {
        System.err.println("USAGE: Executor hostPort znode filename program [args ...]");
        System.exit(2);
    	}*/
		//String hostPort = args[0];
		//String znode = args[1];
		
		String hostPort = "59.151.111.57:2181";
    	String zNode = "/znode";
    	String errorNode = "/errornode";
    	String zPersisNode = "/zpnode";
    	String confNode = "/conf";
    	String dataNode = "/datanode";
    	String signDataNode = "/signdatanode";
    	String runNameNode = "/runnamenode";
    	String signRunNode = "/signrunnode";
    	
    	System.out.println("server is created");
        try {
            //new Executor(hostPort,zNode,zPersisNode,errorNode,confNode,dataNode,signDataNode,runNameNode,signRunNode);
            new Executor();
        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}

}
