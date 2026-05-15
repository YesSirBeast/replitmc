package com.viaversion.viafabricplus.injection.access.base.bedrock;

/**
 * Exposes the <em>original</em> (pre-redirect) host and port stored on
 * {@link net.minecraft.client.network.ServerAddress} so that the Bedrock
 * pipeline can retrieve the real address after it was replaced with a
 * loopback address for RakNet / NetherNet routing.
 *
 * <p>Ported from ViaFabricPlus 4.5.3 (core/bedrock) → 4.0.5-BACKPORT
 * naming convention (base/bedrock). Yarn 1.21.4 mappings.</p>
 */
public interface IServerAddress {

    /** Returns the address typed by the user before any Bedrock redirect. */
    String viafabricplus$getOriginalAddress();

    /** Returns the port typed by the user before any Bedrock redirect. */
    int viafabricplus$getOriginalPort();

    /** Stores the original address (called by MixinServerAddress). */
    void viafabricplus$setOriginalAddress(String address);

    /** Stores the original port (called by MixinServerAddress). */
    void viafabricplus$setOriginalPort(int port);
}
