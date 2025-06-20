package at.technikum.energyapi.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String PRODUCER_QUEUE = "producer.queue";
    public static final String USAGE_QUEUE    = "usage.queue";
    public static final String PERCENT_QUEUE  = "percent.queue";
    public static final String EXCHANGE       = "energy.exchange";

    @Bean Queue producerQueue()    { return new Queue(PRODUCER_QUEUE, true); }
    @Bean Queue usageQueue()       { return new Queue(USAGE_QUEUE, true); }
    @Bean Queue percentQueue()     { return new Queue(PERCENT_QUEUE, true); }
    @Bean DirectExchange exchange(){ return new DirectExchange(EXCHANGE); }

    @Bean Binding bindProducer(Queue producerQueue, DirectExchange ex) {
        return BindingBuilder.bind(producerQueue).to(ex).with("produced");
    }
    @Bean Binding bindUsage(Queue usageQueue, DirectExchange ex) {
        return BindingBuilder.bind(usageQueue).to(ex).with("used");
    }
    @Bean Binding bindPercent(Queue percentQueue, DirectExchange ex) {
        return BindingBuilder.bind(percentQueue).to(ex).with("percent");
    }
}
