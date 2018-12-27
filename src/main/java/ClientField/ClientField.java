package ClientField;

import SafeContext.SRP;

import java.math.BigInteger;
import java.util.Random;
import java.util.UUID;

public class ClientField {

    private String salt;
    private BigInteger x;
    private long pass_verifier;
    private String pass;
    private String id;
    private int a;
    private long aBig;
    public String key;
    private long bBig;
    private String u;
    private String m;
    private String r;


    ClientField(String id, String pass) {
        new SRP();
        this.pass = pass;
        this.id = id;
        salt = UUID.randomUUID().toString();
        String hash = SRP.getHash((salt + this.pass).getBytes());
        x = new BigInteger(hash, 16);
        System.out.println(x);
        // pass_verifier= SRP.powMod(SRP.getG(),,SRP.getN());
        BigInteger pv = BigInteger.valueOf(SRP.getG()).modPow(x, BigInteger.valueOf(SRP.getN()));
        pass_verifier = Long.valueOf(pv.toString());
    }


    public String getM() {
        return m;
    }

    void keyComp() {
        BigInteger S;
        BigInteger U = new BigInteger(u, 16);
        long ex1 = bBig - SRP.getK() * pass_verifier;
        BigInteger ex2 = U.multiply(x).add(BigInteger.valueOf(a));
        S = BigInteger.valueOf(ex1).modPow(ex2, BigInteger.valueOf(SRP.getN()));
        key = SRP.getHash(S.toString().getBytes());
    }

    String confirmationHash() {
        BigInteger ex1 = new BigInteger(SRP.getHash(Long.valueOf(SRP.getN()).toString().getBytes()), 16);
        BigInteger ex2 = new BigInteger(SRP.getHash(Long.valueOf(SRP.getG()).toString().getBytes()), 16);
        String arg1 = ex1.xor(ex2).toString();
        byte[] args = (arg1 + SRP.getHash(id.getBytes()) + salt + aBig + bBig + key).getBytes();
        m = SRP.getHash(args);
        return m;
    }

    String compR() {
        r = SRP.getHash((aBig + m + key).getBytes());
        return r;
    }

    public String getR() {
        return r;
    }

    long compA() {
        a = randomNatural();
        aBig = SRP.powMod(SRP.getG(), a, SRP.getN());
        return aBig;
    }

    String scrambler() {
        long a = aBig;
        long b = bBig;
        String con = a + String.valueOf(b);
        String u = SRP.getHash(con.getBytes());
        this.u = u;
        return u;
    }

    int randomNatural() {
        Random random = new Random(System.currentTimeMillis());
        return random.nextInt(1000000 - 10000) + 10000;
    }


    public String getSalt() {
        return salt;
    }

    public long getPass_verifier() {
        return pass_verifier;
    }

    public void setbBig(long bBig) {
        this.bBig = bBig;
    }


    public String getId() {
        return id;
    }

}