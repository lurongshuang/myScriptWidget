package com.example.myscriptwidget.certificate;

/**
 * The <code>MyCertificate</code> class provides the bytes of the user
 * certificate used to grant the access to the MyScript technologies.
 */
public final class MyCertificate {
    /**
     * Returns the bytes of the user certificate.
     *
     * @return The bytes of the user certificate.
     */
    public static byte[] getBytes() {
        return BYTES;
    }

    /**
     * The bytes of the user certificate.
     */
    private static final byte[] BYTES = new byte[]{};

} // end of: class MyCertificate
