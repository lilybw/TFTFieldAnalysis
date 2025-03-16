package gbw.riot.tftfieldanalysis.core.environmentloading;

import org.springframework.context.ApplicationContext;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

//For any class marked with @RequestsEnvironmentRessource
//For a bean of that class provided in Spring's ApplicationContext
//For any field marked with @AutoFetch: Inject requested environment ressource
public class EnvironmentBootLoader {


    public static void run(ApplicationContext context, String rootPackage){
        Reflections reflections = new Reflections(rootPackage);
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(RequestsEnvironmentResource.class);
        Set<Object> missingBeans = new HashSet<>();
        Set<Object> beans = new HashSet<>();
        for(Class<?> type : types){
            try{
                Object bean = context.getBean(type.getName());
                beans.add(bean);
            }catch (Exception ignored){
                missingBeans.add(type.getName());
            }

        }

        Set<Method> methods = reflections.getMethodsAnnotatedWith(AutoFetch.class);

        if(missingBeans.isEmpty()){
            return;
        }

        System.err.println("Beans missing for ressource injection: ");
        for(Object missingBean : missingBeans){
            System.err.println("\t " + missingBean.getClass());
        }
    }

}
