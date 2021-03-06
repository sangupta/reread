package com.sangupta.reread;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main entry point for starting the server.
 * 
 * @author sangupta
 *
 */
@SpringBootApplication
@ComponentScan("com.sangupta.reread")
public class ReReadServer {

    public static void main(String[] args) {
    	SpringApplication.run(ReReadServer.class, args);
    }

}
