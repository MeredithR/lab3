package SafeContext;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.sqrt;

public class SRP {

    private static long n;
    private static long fi;
    private static final int k = 3;
    private static long g;

    public SRP() {
        n = 212501*2+1;
        fi = n - 1;
        g = primitiveRoot(numberFactorization());
    }

    public static long getN() {
        return n;
    }

    public static int getK() {
        return k;
    }

    public static long getG() {
        return g;
    }


    private boolean isPrime(int n) {
        for (int i = 2; i <= sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    private int randomPrime() {
        int a = 20000;
        int b = 2100000;
        int i;
        Random random = new Random(System.currentTimeMillis());
        int res = random.nextInt(b - a) + a;
        for (i = res; i < 2 * res; i++) {
            if (isPrime(i) && isPrime((i - 1) / 2)) {
                return i;
            }
        }
        return i;
    }

    public static String getHash(byte[] inputBytes) {
        String hashValue = "";
        try {

            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] digestBytes = messageDigest.digest(inputBytes);
            hashValue = DatatypeConverter.printHexBinary(digestBytes).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashValue;
    }


    private ArrayList<Long> numberFactorization() {
        ArrayList<Long> fact = new ArrayList<Long>();
        long number1 = fi;
        while (number1 % 2 == 0) {
            number1 /= 2;
            fact.add(2L);
        }
        for (int i = 3; i * i <= number1; i += 2) {
            if (number1 % i == 0) {
                number1 /= i;
                fact.add((long) i);
                i -= 2;
            }
        }
        if (number1 != 1) {
            fact.add(number1);
        }
        System.out.println(fact);
        return fact;
    }


    private static long primitiveRoot(ArrayList<Long> fact) {
        int g;
        for (g = 1; g <= fi + 1; g++) {
            boolean check = true;
            for (Long aFact : fact) {
                long a = powMod(g, fi / aFact, fi + 1);
                check &= a != 1;
            }
            if (check) {
                return g;
            }
        }
        return 0;
    }

    public static long powMod(long base, long exp, long mod) {
        long d = 1;
        while (exp > 1) {
            if (exp % 2 == 0) {
                base = base * base % mod;
                exp /= 2;
            } else {
                exp -= 1;
                d = d * base % mod;
            }
        }
        return d * base % mod;
    }
}
