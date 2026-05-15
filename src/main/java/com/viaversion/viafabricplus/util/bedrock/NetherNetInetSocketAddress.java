package com.viaversion.viafabricplus.util.bedrock;

import java.net.InetSocketAddress;

/**
 * Sentinel {@link InetSocketAddress} subtype that carries a NetherNet
 * (WebRTC-based) connection identifier instead of a real IP/port pair.
 *
 * <p>When the player enters an address in the format {@code nethernet:<id>},
 * ViaFabricPlus creates one of these and passes it through the Minecraft
 * connection stack.  The pipeline provider then detects it and initialises
 * the WebRTC channel instead of a normal TCP/RakNet channel.</p>
 *
 * <p>NEW in ViaFabricPlus 4.5.3 (ViaBedrock 0.0.27) — required for
 * Bedrock 1.26 NetherNet support.  This class is Minecraft-API-independent
 * and can be compiled standalone.</p>
 */
public final class NetherNetInetSocketAddress extends InetSocketAddress {

    private final long connectionId;

    /**
     * @param connectionId  the 64-bit NetherNet connection identifier found
     *                      in the server address string after {@code nethernet:}
     */
    public NetherNetInetSocketAddress(final long connectionId) {
        // We use a dummy loopback address so Minecraft's socket code does not
        // reject it, but the pipeline provider intercepts before any real
        // TCP/UDP socket is opened.
        super("127.0.0.1", 19132);
        this.connectionId = connectionId;
    }

    /** Returns the raw NetherNet 64-bit connection identifier. */
    public long getConnectionId() {
        return connectionId;
    }

    /**
     * Convenience check used in pipeline code to avoid
     * {@code instanceof} casts everywhere.
     */
    public static boolean isNetherNet(final InetSocketAddress address) {
        return address instanceof NetherNetInetSocketAddress;
    }

    @Override
    public String toString() {
        return "nethernet:" + connectionId;
    }
}
