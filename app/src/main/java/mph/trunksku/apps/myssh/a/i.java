package mph.trunksku.apps.myssh.a;

import java.net.Socket;

public class i {
    private static a a;
    private static b b;
    private static c c;

    public interface a {
        void a(int i);
    }

    public interface b {
        void c();
    }

    public interface c {
        boolean a(Socket socket);

        void b();
    }

    public static void a() {
        if (b != null) {
            b.c();
        }
    }

    static void a(int i) {
        if (a != null) {
            a.a(i);
        }
    }

    public static void a(Object obj) {
        if (obj instanceof a) {
            a = (a) obj;
        } else if (obj instanceof b) {
            b = (b) obj;
        } else if (obj instanceof c) {
            c = (c) obj;
        }
    }

    static boolean a(Socket socket) {
        return c != null && c.a(socket);
    }

    public static void b() {
        if (c != null) {
            c.b();
        }
    }
}

