package gov.cdc.nbs.deduplication.algorithm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class MapperConfig {

  @Bean
  public ObjectMapper mapper() {
    return new ObjectMapper()
        .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
        .enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
  }

}
