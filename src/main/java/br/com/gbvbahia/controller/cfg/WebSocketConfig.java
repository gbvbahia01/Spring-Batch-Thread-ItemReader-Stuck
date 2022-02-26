package br.com.gbvbahia.controller.cfg;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

   public static final String TOPIC_URL = "/topic";
   public static final String API_PREFIX = "/ws/";
   
   @Override
   public void registerStompEndpoints(StompEndpointRegistry registry) {
      registry.addEndpoint("batch-websocket").withSockJS();
   }
   
   @Override
   public void configureMessageBroker(MessageBrokerRegistry registry) {
      registry.enableSimpleBroker(TOPIC_URL);
      registry.setApplicationDestinationPrefixes(API_PREFIX);
   }
   
}