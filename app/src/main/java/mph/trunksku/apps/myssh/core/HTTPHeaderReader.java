package mph.trunksku.apps.myssh.core;

import java.io.IOException;
import java.io.InputStream;

public class HTTPHeaderReader {
    InputStream in;

    public HTTPHeaderReader(InputStream stream) {
        this.in = stream;
    }

    public ResponseHeader read() throws IOException {
        StringBuilder builder = new StringBuilder();
        while (!builder.toString().endsWith("\r\n\r\n")) {
            int raw = this.in.read();
            if (raw != -1) {
                builder.append((char) raw);
            } else {
                throw new IOException("EOF reached. Aborting header read");
            }
        }
        return ResponseHeader.parse(builder.toString());
    }
}

