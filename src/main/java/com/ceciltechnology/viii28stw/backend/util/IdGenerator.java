package com.ceciltechnology.viii28stw.backend.util;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class IdGenerator implements IdentifierGenerator {

    private static IdGenerator uniqueInstance;
    private static final Random RANDOM = new Random();

    public static synchronized IdGenerator getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new IdGenerator();
        }
        return uniqueInstance;
    }

    public String generate() {
        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("ssSSS");
        String output = "1".concat(formatter.format(today));
        return output;
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor ssci, Object o) {
        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyHHmmssSSS");
        String output = formatter.format(today);
        return output;
    }

    @Deprecated
    private Serializable oldGenerate(SharedSessionContractImplementor ssci, Object o) {
        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmssSSS");
        String output = formatter.format(today);

        String alphabet = "_-@*&";
        char ins = alphabet.charAt(new Random().nextInt(alphabet.length()));
        int generatedInt = new Random().nextInt(output.length());

        String idGen = output;

        idGen = new StringBuilder(idGen).insert(idGen.length() - generatedInt, ins).toString();
        return randomAlphabetic(3) + idGen + randomAlphabetic(1);
    }

    private static String randomAlphabetic(int count) {
        return random(count, true, false);
    }

    private static String random(int count, boolean letters, boolean numbers) {
        return random(count, 0, 0, letters, numbers);
    }

    private static String random(int count, int start, int end, boolean letters, boolean numbers) {
        return random(count, start, end, letters, numbers, null, RANDOM);
    }

    private static String random(int count, int start, int end, boolean letters, boolean numbers, char[] chars, Random random) {
        if (count == 0) return "";
        if (count < 0) throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");

        if (start == 0 && end == 0) {
            end = 123;
            start = 32;
            if (!letters && !numbers) {
                start = 0;
                end = Integer.MAX_VALUE;
            }
        }

        char[] buffer = new char[count];
        int gap = end - start;

        while (count-- != 0) {
            char ch;
            ch = chars == null ? (char) (random.nextInt(gap) + start) : chars[(random.nextInt(gap) + start)];

            if ((letters && Character.isLetter(ch)) || (numbers && Character.isDigit(ch)) || (!letters && !numbers)) {
                if (ch >= 56320 && ch <= 57343) {
                    if (count == 0) {
                        count++;
                    } else {
                        buffer[count] = ch;
                        count--;
                        buffer[count] = ((char) (55296 + random.nextInt(128)));
                    }
                } else if (ch >= 55296 && ch <= 56191) {
                    if (count == 0) {
                        count++;
                    } else {
                        buffer[count] = ((char) (56320 + random.nextInt(128)));
                        count--;
                        buffer[count] = ch;
                    }
                } else if (ch >= 56192 && ch <= 56319) {
                    count++;
                } else {
                    buffer[count] = ch;
                }
            } else {
                count++;
            }
        }
        return new String(buffer).replace(" ", "-");
    }

}
