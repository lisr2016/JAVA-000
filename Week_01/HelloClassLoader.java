import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;

public class HelloClassLoader extends ClassLoader {
    public static void main(String[] args) {
        try {
            Class<?> aClass = new HelloClassLoader().findClass("Hello");
            Object obj = aClass.newInstance();
            Method method = aClass.getMethod("hello");
            method.invoke(obj);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes;
        try {
            File file = new File(this.getClass().getResource("/Hello.xlass").getPath());
            FileInputStream inputStream = new FileInputStream(file);
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            int nextValue = 0;
            while ( (nextValue = inputStream.read()) != -1 ) {
                byteStream.write(255 - nextValue);
            }
            bytes = byteStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return super.findClass(name);
        }
        return defineClass(name, bytes, 0, bytes.length);
    }
}