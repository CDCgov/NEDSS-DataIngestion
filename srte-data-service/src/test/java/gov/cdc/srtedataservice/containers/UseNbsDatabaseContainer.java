package gov.cdc.srtedataservice.containers;

import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ContextConfiguration(initializers = {
    MsSqlContainerInitializer.class })
public @interface UseNbsDatabaseContainer {

}
