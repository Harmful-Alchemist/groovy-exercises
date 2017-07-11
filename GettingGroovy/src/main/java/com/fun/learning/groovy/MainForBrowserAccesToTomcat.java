package com.fun.learning.groovy;

import com.fun.learning.groovy.TomcatRunnable;

public class MainForBrowserAccesToTomcat {

	static TomcatRunnable tomcat;
	static int port = 5000;
	
	public static void main(String[] args) throws InterruptedException {
		tomcat = new TomcatRunnable();
		tomcat.setPort(port);
		new Thread(tomcat).start();
		while(true) {
			Thread.sleep(5000);
		}
	}

}
