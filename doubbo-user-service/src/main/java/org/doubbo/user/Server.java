package org.doubbo.user;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Server {

	public static void main(String[] args) {

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-context.xml");
		context.start();
		synchronized (Server.class) {
			while(true){
				try{
					Server.class.wait();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

}
