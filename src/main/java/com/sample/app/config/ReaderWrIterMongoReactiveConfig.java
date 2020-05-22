package com.sample.app.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.connection.netty.NettyStreamFactoryFactory;
import com.mongodb.reactivestreams.client.MongoClient;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.autoconfigure.mongo.ReactiveMongoClientFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;

import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(ReaderWriterMongoProperties.class)
public class ReaderWrIterMongoReactiveConfig {

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(
            @Qualifier("reactiveReaderMongoDatabaseFactory") ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory,
            MongoConverter converter) {
        return new ReactiveMongoTemplate(reactiveMongoDatabaseFactory, converter);
    }

    @Bean
    public ReactiveMongoTemplate reactiveWriterMongoTemplate(
            @Qualifier("reactiveWriterMongoDatabaseFactory") ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory,
            MongoConverter converter) {
        return new ReactiveMongoTemplate(reactiveMongoDatabaseFactory, converter);
    }

    @Primary
    @Bean
    public SimpleReactiveMongoDatabaseFactory reactiveReaderMongoDatabaseFactory(
            ReaderWriterMongoProperties properties,
            @Qualifier("reactiveStreamsReaderMongoClient") MongoClient mongoClient) {
        String database = properties.getReader().getMongoClientDatabase();
        return new SimpleReactiveMongoDatabaseFactory(mongoClient, database);
    }

    @Bean
    public SimpleReactiveMongoDatabaseFactory reactiveWriterMongoDatabaseFactory(
            ReaderWriterMongoProperties properties,
            @Qualifier("reactiveStreamsWriterMongoClient") MongoClient mongoClient) {
        String database = properties.getWriter().getMongoClientDatabase();
        return new SimpleReactiveMongoDatabaseFactory(mongoClient, database);
    }

    @Bean
    public MongoClient reactiveStreamsReaderMongoClient(ReaderWriterMongoProperties properties, Environment environment,
                                                        ObjectProvider<MongoClientSettingsBuilderCustomizer> builderCustomizers,
                                                        ObjectProvider<MongoClientSettings> settings) {
        ReactiveMongoClientFactory factory = new ReactiveMongoClientFactory(properties.getReader(), environment,
                builderCustomizers.orderedStream().collect(Collectors.toList()));
        return factory.createMongoClient(settings.getIfAvailable());
    }

    @Bean
    public MongoClient reactiveStreamsWriterMongoClient(ReaderWriterMongoProperties properties, Environment environment,
                                                        ObjectProvider<MongoClientSettingsBuilderCustomizer> builderCustomizers,
                                                        ObjectProvider<MongoClientSettings> settings) {
        ReactiveMongoClientFactory factory = new ReactiveMongoClientFactory(properties.getWriter(), environment,
                builderCustomizers.orderedStream().collect(Collectors.toList()));
        return factory.createMongoClient(settings.getIfAvailable());
    }


    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({SocketChannel.class, NioEventLoopGroup.class})
    static class NettyDriverConfiguration {

        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE)
        ReaderWrIterMongoReactiveConfig.NettyDriverMongoClientSettingsBuilderCustomizer nettyDriverCustomizer(
                ObjectProvider<MongoClientSettings> settings) {
            return new ReaderWrIterMongoReactiveConfig.NettyDriverMongoClientSettingsBuilderCustomizer(settings);
        }

    }

    /**
     * {@link MongoClientSettingsBuilderCustomizer} to apply Mongo client settings.
     */
    private static final class NettyDriverMongoClientSettingsBuilderCustomizer
            implements MongoClientSettingsBuilderCustomizer, DisposableBean {

        private final ObjectProvider<MongoClientSettings> settings;

        private volatile EventLoopGroup eventLoopGroup;

        private NettyDriverMongoClientSettingsBuilderCustomizer(ObjectProvider<MongoClientSettings> settings) {
            this.settings = settings;
        }

        @Override
        public void customize(MongoClientSettings.Builder builder) {
            if (!isStreamFactoryFactoryDefined(this.settings.getIfAvailable())) {
                NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
                this.eventLoopGroup = eventLoopGroup;
                builder.streamFactoryFactory(
                        NettyStreamFactoryFactory.builder().eventLoopGroup(eventLoopGroup).build());
            }
        }

        @Override
        public void destroy() {
            EventLoopGroup eventLoopGroup = this.eventLoopGroup;
            if (eventLoopGroup != null) {
                eventLoopGroup.shutdownGracefully().awaitUninterruptibly();
                this.eventLoopGroup = null;
            }
        }

        private boolean isStreamFactoryFactoryDefined(MongoClientSettings settings) {
            return settings != null && settings.getStreamFactoryFactory() != null;
        }

    }
}
