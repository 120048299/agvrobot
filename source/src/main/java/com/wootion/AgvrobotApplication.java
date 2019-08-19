package com.wootion;

import com.wootion.robot.MemRobot;
import com.wootion.robot.MemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

@ServletComponentScan
@SpringBootApplication
@EnableTransactionManagement
public class AgvrobotApplication extends SpringBootServletInitializer {

	private static final Logger logger = LoggerFactory.getLogger(AgvrobotApplication.class);

/*	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {

		return builder.sources(AgvrobotApplication.class);
	}*/

	public static void main(String[] args) {
		SpringApplication.run(AgvrobotApplication.class, args);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("******************** "+Thread.currentThread().getName() + ": shutdown begin********************");
			List<MemRobot> list= MemUtil.getMemRobotList();
			for(MemRobot memRobot:list){
				//
			}
		}));
	}



}
