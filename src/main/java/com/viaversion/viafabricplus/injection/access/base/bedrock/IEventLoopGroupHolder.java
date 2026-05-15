package com.viaversion.viafabricplus.injection.access.base.bedrock;

import io.netty.channel.EventLoopGroup;

/**
 * Exposes the Netty EventLoopGroup stored on ClientConnection so that
 * the RakNet / NetherNet pipeline provider can reuse the same thread-pool
 * instead of creating a new one for each Bedrock session.
 *
 * <p>Ported from ViaFabricPlus 4.5.3 (core/bedrock) → 4.0.5-BACKPORT
 * naming convention (base/bedrock). Yarn 1.21.4 mappings.</p>
 */
public interface IEventLoopGroupHolder {

    /**
     * Returns the cached UDP {@link EventLoopGroup} used for RakNet/NetherNet
     * transports, or {@code null} when no Bedrock session is active.
     */
    EventLoopGroup viafabricplus$getEventLoopGroup();

    /**
     * Stores the UDP {@link EventLoopGroup} so it can be shut down cleanly
     * when the connection is closed.
     */
    void viafabricplus$setEventLoopGroup(EventLoopGroup group);
}
