package com.fun.learning.groovy;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

public class TomcatRunnable implements Runnable{

	Tomcat tomcat;
	
	int port = 8080;
	
	@Override
	public void run() {
		tomcat = new Tomcat();
		tomcat.setPort(port);
		File docBase = new File("src/test/resources");

		Context ctx = tomcat.addContext("/", docBase.getAbsolutePath());
		Tomcat.addServlet(ctx, "Groovy", "groovy.servlet.GroovyServlet");
		ctx.addServletMappingDecoded("*.groovy", "Groovy");
		
		try {
			tomcat.start();
		} catch (LifecycleException e) {
			e.printStackTrace();
		}
		tomcat.getServer().await();
	}
	
	public void shutDown() throws LifecycleException {
		tomcat.stop();
	}
	
	public void setPort(int port) {
		this.port = port;
	}

}
