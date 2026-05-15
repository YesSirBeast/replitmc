package com.viaversion.viafabricplus.util.bedrock;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses and validates NetherNet server address strings.
 *
 * <p>Bedrock 1.26 introduced <em>NetherNet</em>, a WebRTC-based peer-to-peer
 * transport.  Players connect to Bedrock Realms (and eventually direct servers)
 * via a connection identifier rather than a host:port pair.  The address format
 * used in ViaFabricPlus is:</p>
 * <pre>{@code nethernet:<connectionId>}</pre>
 * <p>where {@code connectionId} is an unsigned 64-bit decimal integer.</p>
 *
 * <p>NEW in ViaFabricPlus 4.5.3 (ViaBedrock 0.0.27).  No Minecraft-API
 * dependencies — pure Java utility class.</p>
 */
public final class NetherNetJsonRpcAddress {

    /** Prefix that identifies a NetherNet address string. */
    public static final String PREFIX = "nethernet:";

    private static final Pattern NETHERNET_PATTERN =
            Pattern.compile("^nethernet:(\\d{1,20})$", Pattern.CASE_INSENSITIVE);

    private NetherNetJsonRpcAddress() {}

    // ── Predicates ────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if {@code address} starts with {@code nethernet:}
     * followed by a numeric connection id.
     */
    public static boolean isNetherNetAddress(final String address) {
        if (address == null) return false;
        return NETHERNET_PATTERN.matcher(address.trim()).matches();
    }

    // ── Parsers ───────────────────────────────────────────────────────────────

    /**
     * Extracts the 64-bit connection identifier from a NetherNet address string.
     *
     * @param address  a validated {@code nethernet:<id>} string
     * @return the numeric connection ID
     * @throws IllegalArgumentException if the address is not a valid NetherNet address
     */
    public static long parseConnectionId(final String address) {
        final Matcher m = NETHERNET_PATTERN.matcher(address.trim());
        if (!m.matches()) {
            throw new IllegalArgumentException(
                    "Not a valid NetherNet address: " + address);
        }
        return Long.parseUnsignedLong(m.group(1));
    }

    /**
     * Converts a NetherNet address into a {@link NetherNetInetSocketAddress}
     * for use in the Minecraft connection stack.
     */
    public static NetherNetInetSocketAddress toSocketAddress(final String address) {
        return new NetherNetInetSocketAddress(parseConnectionId(address));
    }

    /**
     * Returns a dummy host string ({@code "0.0.0.0"}) used when Minecraft's
     * address parser needs a regular host for display purposes.
     */
    public static String extractHost(final String address) {
        return "0.0.0.0";
    }

    /** Returns the default Bedrock port (19132) for display purposes. */
    public static int extractPort(final String address) {
        return 19132;
    }
}
