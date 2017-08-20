package com.example.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * This class is a configuration class for websocket configurations
 * @author vbawej
 *
 */
@Configuration
@EnableWebSocketMessageBroker
public class BattleshipGameWebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	  /* (non-Javadoc)
	 * @see org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer#configureMessageBroker(org.springframework.messaging.simp.config.MessageBrokerRegistry)
	 */
	@Override
	    public void configureMessageBroker(MessageBrokerRegistry config) {
	       //uncomment to use inbuilt message broker and comment the stompbroker to use RabbitMQ
	       // config.setApplicationDestinationPrefixes("/app").enableSimpleBroker("/topic", "/queue");
	       //comment to use inbuilt message broker and uncomment the enableSimpleBroker to use internal mq processor
		  config.setApplicationDestinationPrefixes("/app").enableStompBrokerRelay("/topic", "/queue");
	    }

	    /* (non-Javadoc)
	     * @see org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer#registerStompEndpoints(org.springframework.web.socket.config.annotation.StompEndpointRegistry)
	     */
	    @Override
	    public void registerStompEndpoints(StompEndpointRegistry registry) {
	        registry.addEndpoint("/messages").withSockJS();
	    }

	
 
  
}