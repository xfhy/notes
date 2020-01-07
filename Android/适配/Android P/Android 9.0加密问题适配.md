
原文:https://www.jianshu.com/p/2b22daa8e2f6

## 提供程序变更

从 Android P 开始，对于 AndroidOpenSSL (也称为 Conscrypt) 提供程序所重复的、来自于 BC 提供程序的部分功能，我们计划将予以弃用。

此改动仅会影响在调用 getInstance() 方法时明确指定 BC 提供程序的应用程序。

需要说明的一点是，我们此举的目的不是因为对 BC 提供程序的实施安全存在疑虑，而是因为重复功能会造成额外的成本和风险，却无法带来太多益处。

如果您在 getInstance() 调用中不会指定提供程序，则无需做出任何改动。

如果您按名称或实例指定提供程序 - 例如，Cipher.getInstance("AES/CBC/PKCS7PADDING", "BC") 或 Cipher.getInstance("AES/CBC/PKCS7PADDING", Security.getProvider("BC")) - 则 Android P 的行为将取决于您应用的目标 API 级别。

对于目标级别早于 P 的应用，调用会返回 BC 实施方法，并在应用日志中记录警告。对于目标级别为 Android P 或之后版本的应用，调用会抛出 NoSuchAlgorithmException。

为了解决此问题，您需要停止指定提供程序，并使用默认实施方法。

在后续的 Android 版本中，我们计划完全移除 BC 提供程序的弃用功能。在移除后，所有请求 BC 提供程序的调用 (不论按名称还是实例) 都会抛出 NoSuchAlgorithmException。

## 移除 Crypto 提供程序

在先前的帖子中，我们曾宣布自 Android Nougat 开始弃用 Crypto 提供程序。

此后，以 API 23 (Marshmallow) 或更早级别为目标的应用程序请求 Crypto 提供程序会成功，但以 API 24 (Nougat) 或之后级别为目标的应用程序请求则会失败。

在 Android P 中，我们计划完全移除 Crypto 提供程序。在移除后，所有对 SecureRandom.getInstance("SHA1PRNG", "Crypto") 的调用都会抛出 NoSuchProviderException。

请大家对自己的应用进行相应更新。


## 解决方案

加解密方式1:
```java
public final class Base64Utils {
    private final static char[] base64EncodeChars = new char[]{'A',
            'B',
            'C',
            'D',
            'E',
            'F',
            'G',
            'H',
            'I',
            'J',
            'K',
            'L',
            'M',
            'N',
            'O',
            'P',
            'Q',
            'R',
            'S',
            'T',
            'U',
            'V',
            'W',
            'X',
            'Y',
            'Z',
            'a',
            'b',
            'c',
            'd',
            'e',
            'f',
            'g',
            'h',
            'i',
            'j',
            'k',
            'l',
            'm',
            'n',
            'o',
            'p',
            'q',
            'r',
            's',
            't',
            'u',
            'v',
            'w',
            'x',
            'y',
            'z',
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            '+',
            '/'};

    private final static byte[] base64DecodeChars = new byte[]{12,
            45,
            12,
            12,
            36,
            52,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            56,
            -1,
            -1,
            15,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            62,
            -1,
            -1,
            -1,
            63,
            52,
            53,
            54,
            55,
            56,
            57,
            58,
            59,
            60,
            61,
            -1,
            -1,
            -1,
            -1,
            45,
            -1,
            -1,
            0,
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8,
            9,
            10,
            11,
            12,
            13,
            14,
            15,
            16,
            17,
            18,
            19,
            20,
            21,
            22,
            23,
            24,
            25,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            26,
            27,
            28,
            29,
            30,
            31,
            32,
            33,
            34,
            35,
            36,
            37,
            38,
            39,
            40,
            41,
            42,
            43,
            44,
            45,
            46,
            47,
            48,
            49,
            50,
            51,
            -1,
            -1,
            -1,
            -1,
            -1};

    /**
     * 加密
     */
    public static String encode(byte[] data) {
        if (data == null || data.length < 1) {

            return null;
        }

        StringBuilder sb = new StringBuilder();
        int len = data.length;
        int i = 0;
        int b1, b2, b3;
        while (i < len) {
            b1 = data[i++] & 0xff;
            if (i == len) {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
                sb.append("==");
                break;
            }
            b2 = data[i++] & 0xff;
            if (i == len) {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
                sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
                sb.append("=");
                break;
            }
            b3 = data[i++] & 0xff;
            sb.append(base64EncodeChars[b1 >>> 2]);
            sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
            sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);
            sb.append(base64EncodeChars[b3 & 0x3f]);
        }
        return sb.toString();
    }

    /**
     * 解密
     */
    public static byte[] decode(String str) {
        try {
            return decodePrivate(str);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    private static byte[] decodePrivate(String str) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        byte[] data = str.getBytes("US-ASCII");
        int len = data.length;
        int i = 0;
        int b1, b2, b3, b4;
        while (i < len) {
            do {
                b1 = base64DecodeChars[data[i++]];
            }
            while (i < len && b1 == -1);
            if (b1 == -1) {
                break;
            }
            do {
                b2 = base64DecodeChars[data[i++]];
            }
            while (i < len && b2 == -1);
            if (b2 == -1) {
                break;
            }
            sb.append((char) ((b1 << 2) | ((b2 & 0x30) >>> 4)));

            do {
                b3 = data[i++];
                if (b3 == 61) {
                    return sb.toString().getBytes("iso8859-1");
                }
                b3 = base64DecodeChars[b3];
            }
            while (i < len && b3 == -1);
            if (b3 == -1) {
                break;
            }
            sb.append((char) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));

            do {
                b4 = data[i++];
                if (b4 == 61) {
                    return sb.toString().getBytes("iso8859-1");
                }
                b4 = base64DecodeChars[b4];
            }
            while (i < len && b4 == -1);
            if (b4 == -1) {
                break;
            }
            sb.append((char) (((b3 & 0x03) << 6) | b4));
        }
        return sb.toString().getBytes("iso8859-1");
    }

    public static void main(String[] args) {
        String hello = "A hello world!";
        String encode = encode(hello.getBytes());
        System.out.println("加密后的字符串是 " + encode);
        byte[] decode = decode(encode);
        System.out.println("解密密后的字符串是 " + new String(decode));
    }

}

```