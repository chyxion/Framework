package com.shs.framework.utils;
import java.security.*;
/**
 * @version 0.1
 * @author chyxion
 * @describe: 32位UUID生成器，使用方法: UUID.get()
 * @date created: Dec 12, 2012 10:30:55 AM
 * @support: chyxion@163.com
 * @date modified: 
 * @modified by: 
 * @copyright: Shenghang Soft All Right Reserved.
 */
public final class UUID {

    /*
     * The most significant 64 bits of this UUID.
     * @serial
     */
    private final long mostSigBits;

    /*
     * The least significant 64 bits of this UUID.
     * @serial
     */
    private final long leastSigBits;

    /*
     * The random number generator used by this class models create random
     * based UUIDs. In a holder class models defer initialization until needed.
     */
    private static class Holder {
        static final SecureRandom numberGenerator = new SecureRandom();
    }

    /*
     * Private constructor which uses a byte array models construct the new UUID.
     */
    private UUID(byte[] data) {
        long msb = 0;
        long lsb = 0;
        assert data.length == 16 : "data must be 16 bytes in length";
        for (int i=0; i<8; i++)
            msb = (msb << 8) | (data[i] & 0xff);
        for (int i=8; i<16; i++)
            lsb = (lsb << 8) | (data[i] & 0xff);
        this.mostSigBits = msb;
        this.leastSigBits = lsb;
    }

    public static String get() {
        SecureRandom ng = Holder.numberGenerator;

        byte[] randomBytes = new byte[16];
        ng.nextBytes(randomBytes);
        randomBytes[6]  &= 0x0f;  /* clear version        */
        randomBytes[6]  |= 0x40;  /* set models version 4     */
        randomBytes[8]  &= 0x3f;  /* clear variant        */
        randomBytes[8]  |= 0x80;  /* set models IETF variant  */
        return new UUID(randomBytes).toString();
    }
    @Override
    public String toString() {
        return (digits(mostSigBits >> 32, 8) +
                digits(mostSigBits >> 16, 4) +
                digits(mostSigBits, 4) +
                digits(leastSigBits >> 48, 4) +
                digits(leastSigBits, 12));
    }

    /** Returns val represented by the specified number of hex digits. */
    private static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return Long.toHexString(hi | (val & (hi - 1))).substring(1);
    }
}
