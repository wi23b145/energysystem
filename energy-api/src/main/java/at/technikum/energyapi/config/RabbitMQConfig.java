package at.technikum.energyapi.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;                  // ← make sure this is from spring-amqp
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit   // ← switch on @RabbitListener processing
public class RabbitMQConfig {

    public static final String PRODUCER_QUEUE = "energy.produced";
    public static final String USER_QUEUE     = "energy.consumed";
    public static final String EXCHANGE       = "energy.exchange";
    public static final String PERCENT_QUEUE  = "energy.percent";

    @Bean
    public Queue producerQueue() {              // ← added @Bean here
        return new Queue(PRODUCER_QUEUE, false);
    }

    @Bean
    public Queue userQueue() {
        return new Queue(USER_QUEUE, false);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding bindingProducer(Queue producerQueue, DirectExchange exchange) {
        return BindingBuilder
                .bind(producerQueue)
                .to(exchange)
                .with("produced");
    }

    @Bean
    public Binding bindingUser(Queue userQueue, DirectExchange exchange) {
        return BindingBuilder
                .bind(userQueue)
                .to(exchange)
                .with("consumed");
    }
}
