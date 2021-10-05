package mph.trunksku.apps.myssh.a;

import java.util.Locale;

class b {
    int a;
    private String b;

    b(String str, int i) {
        this.a = i;
        this.b = str;
    }

    private static long a(String str) {
        String[] split = str.split("\\.");
        return ((long) Integer.parseInt(split[3])) + (((0 + (Long.parseLong(split[0]) << 24)) + ((long) (Integer.parseInt(split[1]) << 16))) + ((long) (Integer.parseInt(split[2]) << 8)));
    }

    public long a() {
        return a(this.b);
    }

    public String toString() {
        return String.format(Locale.ENGLISH, "%s/%d", new Object[]{this.b, Integer.valueOf(this.a)});
    }
}

