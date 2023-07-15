package gbw.riot.tftfieldanalysis.core.environmentloading;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFetch {
    /**
     * File + path from root to be retrieved and injected into <field>field</field>.
     */
    String file();

    /**
     * Will cancel program start if any file is missing but is required.
     */
    boolean required() default true;
}
