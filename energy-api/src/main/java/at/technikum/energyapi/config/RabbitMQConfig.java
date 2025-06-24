package at.technikum.energyapi.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PRODUCER_QUEUE = "producer.queue";
    public static final String USAGE_QUEUE    = "usage.queue";
    public static final String PERCENT_QUEUE  = "percent.queue";
    public static final String ENERGY_QUEUE   = "energy-data-queue";
    public static final String EXCHANGE       = "energy.exchange";

    public static final String ROUTING_KEY_ENERGY = "energy";
    public static final String ROUTING_KEY_PERCENT = "percent";
    public static final String ROUTING_KEY_PRODUCED = "produced";
    public static final String ROUTING_KEY_USED = "used";


    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue energyQueue() {
        return new Queue(ENERGY_QUEUE);
    }

    @Bean
    public Queue percentQueue() {
        return new Queue(PERCENT_QUEUE);
    }

    @Bean
    public Queue producerQueue() {
        return new Queue(PRODUCER_QUEUE);
    }

    @Bean
    public Queue usageQueue() {
        return new Queue(USAGE_QUEUE);
    }

    // Bindings
    @Bean
    public Binding energyBinding(Queue energyQueue, DirectExchange exchange) {
        return BindingBuilder.bind(energyQueue).to(exchange).with(ROUTING_KEY_ENERGY);
    }

    @Bean
    public Binding percentBinding(Queue percentQueue, DirectExchange exchange) {
        return BindingBuilder.bind(percentQueue).to(exchange).with(ROUTING_KEY_PERCENT);
    }

    @Bean
    public Binding producerBinding(Queue producerQueue, DirectExchange exchange) {
        return BindingBuilder.bind(producerQueue).to(exchange).with(ROUTING_KEY_PRODUCED);
    }

    @Bean
    public Binding usageBinding(Queue usageQueue, DirectExchange exchange) {
        return BindingBuilder.bind(usageQueue).to(exchange).with(ROUTING_KEY_USED);
    }
    // 4. MessageConverter (für JSON Nachrichten)
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 5. RabbitTemplate mit JSON-Konverter
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
