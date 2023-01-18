import TestClasses.*;
import org.example.PropertySourcesPlaceholderConfigurer;
import org.example.Registry;
import org.example.RegistryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    Registry r;

    @BeforeEach
    public void init() throws Exception {
        r = new Registry();
        r.registerInstance("properties", "C:\\work\\infinno-course-project-7\\src\\main\\resources\\config.properties");
        r.registerInstance(PropertySourcesPlaceholderConfigurer.class, new PropertySourcesPlaceholderConfigurer());
    }

    @Test
    public void autoInject() throws Exception {
        B inst = r.getInstance(B.class);

        assertNotNull(inst);
        assertNotNull(inst.aField);
    }

    @Test
    public void injectInstance() throws Exception {
        B b = new B();
        r.registerInstance(b);
        C inst = r.getInstance(C.class);

        assertNotNull(inst);
        assertSame(b, inst.bField);
    }

    @Test
    public void injectNamedInstance() throws Exception {
        A a = new A();
        r.registerInstance("iname", a);
        F inst = r.getInstance(F.class);

        assertNotNull(inst);
        assertSame(a, inst.iname);
    }

    @Test
    public void injectStringProperty() throws Exception {
        String email = "name@yahoo.com";
        r.registerInstance("email", email);
        FS inst = r.getInstance(FS.class);

        assertNotNull(inst);
        assertNotNull(inst.email);
        assertSame(inst.email, email);
    }

    @Test
    public void constructorInject() throws Exception {
        E inst = r.getInstance(E.class);

        assertNotNull(inst);
        assertNotNull(inst.aField);
    }

    @Test
    public void injectInterface() throws Exception {
        r.registerImplementation(AI.class, A.class);
        B inst = r.getInstance(B.class);

        assertNotNull(inst);
        assertNotNull(inst.aField);
    }

    @Test
    public void injectDefaultImplementationForInterface() throws Exception {
        DI inst = r.getInstance(DI.class);
        assertNotNull(inst);
    }

    @Test()
    public void injectMissingDefaultImplementationForInterface() throws Exception {
        assertThrows(RegistryException.class, () -> {
            AI inst = r.getInstance(AI.class);
        });
    }

    @Test
    public void decorateInstance() throws Exception {
        C ci = new C();
        r.decorateInstance(ci);

        assertNotNull(ci.bField);
        assertNotNull(ci.bField.aField);
    }

    @Test
    public void initializer() throws Exception {
        String email = "name@yahoo.com";
        r.registerInstance("email", email);
        FSI inst = r.getInstance(FSI.class);

        assertNotNull(inst);
        assertNotNull(inst.email);
        assertEquals(inst.email, "mailto:" + email);
    }

    @Test
    public void circularDependencyDetection() throws Exception {
        G inst = r.getInstance(G.class);
        assertEquals(inst, inst.hfield.gField);
    }

    @Test
    public void lazyLoading() throws Exception {
        I i = r.getInstance(I.class);

        assertNull(i.cField.bField);
        i.cField.printMessage();
        assertNotNull(i.cField.bField);
    }

    @Test
    public void propertySet() throws Exception {
        J j = r.getInstance(J.class);

        assertEquals(2, j.number);
    }
}