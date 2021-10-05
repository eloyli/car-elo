package mph.trunksku.apps.myssh.util;

import java.io.CharArrayWriter;
import java.io.IOException;

public class eBase64 {
    static final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

	private static int r8;

    public static char[] encode(byte[] bArr) {
        byte[] bArr2 = bArr;
        CharArrayWriter charArrayWriter = new CharArrayWriter((4 * bArr2.length) / 3);
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < bArr2.length; i3++) {
            if (i == 0) {
                i2 = (bArr2[i3] & 255) << 16;
            } else if (i == 1) {
                i2 |= (bArr2[i3] & 255) << 8;
            } else {
                i2 |= bArr2[i3] & 255;
            }
            i++;
            if (i == 3) {
                charArrayWriter.write(alphabet[i2 >> 18]);
                charArrayWriter.write(alphabet[(i2 >> 12) & 63]);
                charArrayWriter.write(alphabet[(i2 >> 6) & 63]);
                charArrayWriter.write(alphabet[i2 & 63]);
                i = 0;
            }
        }
        if (i == 1) {
            charArrayWriter.write(alphabet[i2 >> 18]);
            charArrayWriter.write(alphabet[(i2 >> 12) & 63]);
            charArrayWriter.write(61);
            charArrayWriter.write(61);
        }
        if (i == 2) {
            charArrayWriter.write(alphabet[i2 >> 18]);
            charArrayWriter.write(alphabet[(i2 >> 12) & 63]);
            charArrayWriter.write(alphabet[(i2 >> 6) & 63]);
            charArrayWriter.write(61);
        }
        return charArrayWriter.toCharArray();
    }

    public static byte[] decode(char[] cArr) throws IOException {
        char[] cArr2 = cArr;
        byte[] bArr = new byte[4];
        byte[] obj = new byte[cArr2.length];
        int i = 0;
        int i2 = 0;
        for (char c : cArr2) {
            if (!(c == '\n' || c == '\r' || c == ' ' || c == '\t')) {
                int i3;
                IOException iOException;
                IOException iOException2;
                if (c >= 'A' && c <= 'Z') {
                    i3 = i;
                    i++;
                    bArr[i3] = (byte) (c - 65);
                } else if (c >= 'a' && c <= 'z') {
                    i3 = i;
                    i++;
                    bArr[i3] = (byte) ((c - 97) + 26);
                } else if (c >= '0' && c <= '9') {
                    i3 = i;
                    i++;
                    bArr[i3] = (byte) ((c - 48) + 52);
                } else if (c == '+') {
                    i3 = i;
                    i++;
                    bArr[i3] = (byte) 62;
                } else if (c == '/') {
                    i3 = i;
                    i++;
                    bArr[i3] = (byte) 63;
                } else if (c == '=') {
                    i3 = i;
                    i++;
                    bArr[i3] = (byte) 64;
                } else {
                    iOException = new IOException("Illegal char in base64 code.");
                    throw iOException;
                }
                if (i == 4) {
                    i = 0;
                    if (bArr[0] == (byte) 64) {
                        break;
                    } else if (bArr[1] == (byte) 64) {
                        iOException = new IOException("Unexpected '=' in base64 code.");
                        throw iOException;
                    } else if (bArr[2] == (byte) 64) {
                        i3 = i2;
                        i2++;
                        obj[i3] = (byte) ((((bArr[0] & 63) << 6) | (bArr[1] & 63)) >> 4);
                        break;
                    } else if (bArr[3] == (byte) 64) {
                        r8 = (((bArr[0] & 63) << 12) | ((bArr[1] & 63) << 6)) | (bArr[2] & 63);
                        i3 = i2;
                        i2++;
                        obj[i3] = (byte) (r8 >> 10);
                        i3 = i2;
                        i2++;
                        obj[i3] = (byte) (r8 >> 2);
                        break;
                    } else {
                        r8 = ((((bArr[0] & 63) << 18) | ((bArr[1] & 63) << 12)) | ((bArr[2] & 63) << 6)) | (bArr[3] & 63);
                        i3 = i2;
                        i2++;
                        obj[i3] = (byte) (r8 >> 16);
                        i3 = i2;
                        i2++;
                        obj[i3] = (byte) (r8 >> 8);
                        i3 = i2;
                        i2++;
                        obj[i3] = (byte) r8;
                    }
                } else {
                    continue;
                }
            }
        }
        byte[] obj2 = new byte[i2];
        System.arraycopy(obj, 0, obj2, 0, i2);
        return obj2;
    }


}


