import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Package {

    public static void main(String[] args) throws IllegalAccessException {
        Bean bean = new Bean();
        bean.setSql("sqlsql");
        bean.setKey("TEST");
        bean.setSe(0);

        Class clazz = bean.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            System.out.println(field.getName());
            System.out.println(field.getType());
            System.out.println(field.get(bean));
            Annotation[] annotations = field.getAnnotations();
        }
        String methodName = "getSql";
        try {
            Method m = clazz.getMethod(methodName, null);
            try {
                Object invoke = m.invoke(bean, null);
                System.out.println(invoke);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
