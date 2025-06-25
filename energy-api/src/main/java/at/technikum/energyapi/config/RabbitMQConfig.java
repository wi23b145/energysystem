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

    // Definieren von Konstanten für die Namen der Queues, des Exchanges und der Routing-Keys
    public static final String PRODUCER_QUEUE = "producer.queue";  // Queue für PRODUCER-Nachrichten
    public static final String USAGE_QUEUE    = "usage.queue";     // Queue für USAGE-Nachrichten
    public static final String PERCENT_QUEUE  = "percent.queue";   // Queue für PERCENT-Nachrichten
    public static final String ENERGY_QUEUE   = "energy-data-queue";  // Queue für ENERGY-Nachrichten
    public static final String EXCHANGE       = "energy.exchange";   // Exchange, über den die Nachrichten versendet werden

    public static final String ROUTING_KEY_ENERGY = "energy";    // Routing-Key für ENERGY-Nachrichten
    public static final String ROUTING_KEY_PERCENT = "percent";  // Routing-Key für PERCENT-Nachrichten
    public static final String ROUTING_KEY_PRODUCED = "produced"; // Routing-Key für PRODUCER-Nachrichten
    public static final String ROUTING_KEY_USED = "used";  // Routing-Key für USAGE-Nachrichten


    // 1. Erstellen eines DirectExchange Beans
    @Bean
    public DirectExchange exchange() {
        // Definiert den Direct Exchange mit dem Namen "energy.exchange"
        return new DirectExchange(EXCHANGE);
    }

    // 2. Erstellen der Queue-Beans für alle definierten Queues
    @Bean
    public Queue energyQueue() {
        // Definiert die Queue für die ENERGY-Nachrichten
        return new Queue(ENERGY_QUEUE);
    }

    @Bean
    public Queue percentQueue() {
        // Definiert die Queue für die PERCENT-Nachrichten
        return new Queue(PERCENT_QUEUE);
    }

    @Bean
    public Queue producerQueue() {
        // Definiert die Queue für die PRODUCER-Nachrichten
        return new Queue(PRODUCER_QUEUE);
    }

    @Bean
    public Queue usageQueue() {
        // Definiert die Queue für die USAGE-Nachrichten
        return new Queue(USAGE_QUEUE);
    }

    // 3. Erstellen der Bindings zwischen den Queues und dem Exchange mit den entsprechenden Routing-Keys
    @Bean
    public Binding energyBinding(Queue energyQueue, DirectExchange exchange) {
        // Verbindet die "energy" Queue mit dem Exchange und verwendet den Routing-Key "energy"
        return BindingBuilder.bind(energyQueue).to(exchange).with(ROUTING_KEY_ENERGY);
    }

    @Bean
    public Binding percentBinding(Queue percentQueue, DirectExchange exchange) {
        // Verbindet die "percent" Queue mit dem Exchange und verwendet den Routing-Key "percent"
        return BindingBuilder.bind(percentQueue).to(exchange).with(ROUTING_KEY_PERCENT);
    }

    @Bean
    public Binding producerBinding(Queue producerQueue, DirectExchange exchange) {
        // Verbindet die "producer" Queue mit dem Exchange und verwendet den Routing-Key "produced"
        return BindingBuilder.bind(producerQueue).to(exchange).with(ROUTING_KEY_PRODUCED);
    }

    @Bean
    public Binding usageBinding(Queue usageQueue, DirectExchange exchange) {
        // Verbindet die "usage" Queue mit dem Exchange und verwendet den Routing-Key "used"
        return BindingBuilder.bind(usageQueue).to(exchange).with(ROUTING_KEY_USED);
    }

    // 4. MessageConverter (für JSON-Nachrichten)
    // Diese Bean definiert den MessageConverter, der verwendet wird, um Nachrichten im JSON-Format zu konvertieren.
    @Bean
    public MessageConverter jsonMessageConverter() {
        // Verwendung von Jackson2JsonMessageConverter, um Nachrichten zwischen Java-Objekten und JSON zu konvertieren
        return new Jackson2JsonMessageConverter();
    }

    // 5. RabbitTemplate mit JSON-Konverter
    // Diese Bean erstellt das RabbitTemplate, das für die Kommunikation mit RabbitMQ verwendet wird.
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        // Initialisiere das RabbitTemplate mit einer ConnectionFactory (die Verbindung zu RabbitMQ)
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        // Setze den MessageConverter, damit alle Nachrichten als JSON konvertiert werden
        template.setMessageConverter(jsonMessageConverter());
        // Gib das RabbitTemplate zurück
        return template;
    }
}

//