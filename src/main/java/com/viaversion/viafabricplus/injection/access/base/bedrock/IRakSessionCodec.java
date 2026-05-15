package com.viaversion.viafabricplus.injection.access.base.bedrock;

/**
 * Exposes RakNet session codec fields needed for custom ping encapsulation
 * and MTU negotiation.  Used by {@code MixinRakSessionCodec} and the new
 * {@code RakNetPingEncapsulationCodec}.
 *
 * <p>Same interface as 4.0.5-BACKPORT original; extended here with an extra
 * accessor needed by netty-transport-raknet 1.7.0.</p>
 */
public interface IRakSessionCodec {

    /** Returns the negotiated MTU size for this RakNet session. */
    int viafabricplus$getMtu();

    /** Returns the RakNet GUID of the remote peer. */
    long viafabricplus$getGuid();
}
