import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class CustomClassLoader extends ClassLoader {

    private final File mClassFile;

    public CustomClassLoader(File classFile) {
        this.mClassFile = classFile;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        final File classFile = this.mClassFile;
        Objects.requireNonNull(classFile, "class file is null");
        if (!classFile.exists()) {
            throw new IllegalArgumentException("class file is not exists");
        }
        try {
            final Path classFilePath = Paths.get(new URI("file", null, classFile.getAbsolutePath(), null));
            byte[] classByteArrays = Files.readAllBytes(classFilePath);
            byte[] decryptClassByteArrays = decryptClassByteArrays(classByteArrays);
            return defineClass(name, decryptClassByteArrays, 0, decryptClassByteArrays.length);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return super.findClass(name);
    }


    private byte[] decryptClassByteArrays(byte[] source) {
        byte[] result = new byte[source.length];
        for (int i = 0; i < source.length; i++) {
            result[i] = (byte) (255 - source[i]);
        }
        return result;
    }


    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        final CustomClassLoader customClassLoader = new CustomClassLoader(new File("./Hello.xlass"));
        final Class<?> foundClass = customClassLoader.findClass("Hello");
        Object o = foundClass.getConstructor().newInstance();
        o.getClass().getMethod("hello").invoke(o);
    }
}



