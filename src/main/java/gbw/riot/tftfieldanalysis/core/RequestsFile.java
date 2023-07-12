package gbw.riot.tftfieldanalysis.core;

import org.hibernate.tool.schema.TargetType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@RequestsEnvironmentResource
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequestsFile{
    /**
     * Path, filename and type expected. "." denotes wd root.
     */
    String file();

    /**
     * If true, and the target ressource is missing,
     * the program will exit.
     */
    boolean required() default true;

}
