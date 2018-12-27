package ServerField;

import SafeContext.SRP;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

public class ServerField {

    ServerField(String id, Account acc) {
        new SRP();
        accounts.put(id, acc);
    }

    public HashMap<String, Account> accounts = new HashMap<String, Account>();


    void receive(String userId, long aBig) {
        accounts.get(userId).setaBig(aBig);
    }

    void generateB(String userId) {
        int b = randomNatural();
        accounts.get(userId).setB(b);
        long passV = accounts.get(userId).getPass_verifier();
        long bBig = (SRP.getK() * passV + SRP.powMod(SRP.getG(), b, SRP.getN())) % SRP.getN();
        accounts.get(userId).setbBig(bBig);
    }

    void keyCompute(String userId) {
        BigInteger S;
        Account acc = accounts.get(userId);
        BigInteger U = new BigInteger(acc.getU(), 16);
        long aBig = acc.getaBig();
        long b = acc.getB();
        long v = acc.getPass_verifier();
        BigInteger ex1 = BigInteger.valueOf(v).modPow(U, BigInteger.valueOf(SRP.getN()));
        S = ex1.multiply(BigInteger.valueOf(aBig)).modPow(BigInteger.valueOf(b),BigInteger.valueOf(SRP.getN()));
        acc.setKey(SRP.getHash(S.toString().getBytes()));
    }

    void confirmationHash(String userId) {
        Account acc = accounts.get(userId);
        BigInteger ex1 = new BigInteger(SRP.getHash(Long.valueOf(SRP.getN()).toString().getBytes()), 16);
        BigInteger ex2 = new BigInteger(SRP.getHash(Long.valueOf(SRP.getG()).toString().getBytes()), 16);
        String arg1 = ex1.xor(ex2).toString();
        byte[] args = (arg1 + SRP.getHash(userId.getBytes()) + acc.getUser_salt() + acc.getaBig() + acc.getbBig() + acc.getKey()).getBytes();
        acc.setM(SRP.getHash(args));
    }

    void compR(String userId) {
        Account acc = accounts.get(userId);
        acc.setR(SRP.getHash((acc.getaBig() + acc.getM() + acc.getKey()).getBytes()));
    }

    public String scrambler(String userId) {
        long a = accounts.get(userId).getaBig();
        long b = accounts.get(userId).getbBig();
        String con = a + String.valueOf(b);
        String u = SRP.getHash(con.getBytes());
        accounts.get(userId).setU(u);
        return u;
    }


    private static int randomNatural() {
        Random random = new Random(System.currentTimeMillis());
        return random.nextInt(1000000 - 10000) + 10000;
    }

}

