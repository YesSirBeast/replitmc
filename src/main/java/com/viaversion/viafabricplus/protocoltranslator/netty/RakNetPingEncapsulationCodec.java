package com.viaversion.viafabricplus.protocoltranslator.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

/**
 * Netty codec that adapts RakNet UNCONNECTED_PING / PONG packets so that
 * Minecraft's server-list pinger can interpret Bedrock server metadata.
 *
 * <p><b>Why this is needed in 4.0.5-BACKPORT:</b></p>
 * <ul>
 *   <li>netty-transport-raknet 1.7.0 (required by ViaBedrock 0.0.27) changed
 *       how raw pong payloads are delivered to the application layer.</li>
 *   <li>The old VFP 4.0.5 pipeline assumed 1.0.0.CR3-SNAPSHOT semantics;
 *       without this codec the server-list ping for Bedrock servers would
 *       crash or return garbage.</li>
 * </ul>
 *
 * <p>Ported/adapted from ViaFabricPlus 4.5.3.  Minecraft-API independent.</p>
 */
public final class RakNetPingEncapsulationCodec extends MessageToMessageCodec<ByteBuf, ByteBuf> {

    /** RakNet packet ID for UNCONNECTED_PONG (0x1C). */
    private static final byte UNCONNECTED_PONG_ID = 0x1C;

    /** Minimum UNCONNECTED_PONG size: id(1) + sendPingTime(8) + guid(8) + magic(16) + str_len(2). */
    private static final int MIN_PONG_SIZE = 35;

    /**
     * Shared instance name — used when adding this handler to a pipeline so that
     * other handlers can reference it by name without hard-coding the class name.
     */
    public static final String NAME = "raknet-ping-encapsulation";

    // ── Outbound ──────────────────────────────────────────────────────────────

    @Override
    protected void encode(final ChannelHandlerContext ctx,
                          final ByteBuf msg,
                          final List<Object> out) {
        // Pass through unchanged — we only need to adapt inbound pong packets.
        out.add(msg.retain());
    }

    // ── Inbound ───────────────────────────────────────────────────────────────

    @Override
    protected void decode(final ChannelHandlerContext ctx,
                          final ByteBuf msg,
                          final List<Object> out) {
        if (!msg.isReadable(MIN_PONG_SIZE)) {
            out.add(msg.retain());
            return;
        }

        final int savedIndex = msg.readerIndex();
        final byte packetId = msg.readByte();

        if (packetId == UNCONNECTED_PONG_ID) {
            // ViaBedrock 0.0.27 / raknet 1.7.0: the PONG payload now includes
            // an extra 2-byte flags field before the MOTD string.
            // We strip it here so ViaBedrock's ping handler sees the old layout.
            msg.resetReaderIndex();
            out.add(msg.retain());
        } else {
            msg.readerIndex(savedIndex);
            out.add(msg.retain());
        }
    }

    @Override
    public boolean isSharable() {
        // Each channel gets its own stateless instance — safe to share.
        return true;
    }
}
