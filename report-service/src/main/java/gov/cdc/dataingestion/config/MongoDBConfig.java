package gov.cdc.dataingestion.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

/***
 *Mongo DB config.
 */
@Configuration
public class MongoDBConfig {

    /**
     * mongodb config
     *
     * @param databaseFactory factory
     * @param converter converter
     * @return mongo template
     */
    @Bean
    public MongoTemplate mongoTemplate(
            final MongoDatabaseFactory databaseFactory,
                                       final MappingMongoConverter converter) {
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return new MongoTemplate(databaseFactory, converter);
    }

}