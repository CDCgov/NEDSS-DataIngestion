package gov.cdc.srtedataservice.containers;

import java.lang.annotation.*;
import org.springframework.test.context.ContextConfiguration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ContextConfiguration(initializers = {MsSqlContainerInitializer.class})
public @interface UseNbsDatabaseContainer {}
