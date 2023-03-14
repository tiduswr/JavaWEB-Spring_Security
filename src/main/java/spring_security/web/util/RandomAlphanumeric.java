package spring_security.web.util;

import java.util.Random;

public class RandomAlphanumeric {
    public static String generateRandomAlphaNumeric(int length) {
        String alphaNumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder builder = new StringBuilder(length);
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(alphaNumeric.length());
            builder.append(alphaNumeric.charAt(index));
        }

        return builder.toString();
    }
}
