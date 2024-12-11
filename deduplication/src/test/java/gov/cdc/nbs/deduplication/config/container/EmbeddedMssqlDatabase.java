package gov.cdc.nbs.deduplication.config.container;

import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ContextConfiguration(initializers = MsSqlContainerInitializer.class)
public @interface EmbeddedMssqlDatabase {
}