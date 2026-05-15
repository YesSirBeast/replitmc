package com.viaversion.viafabricplus.injection.access.base.bedrock;

/**
 * Exposes ViaBedrock's ChunkTracker internals to the VFP Mixin layer.
 * Identical interface to 4.0.5-BACKPORT original; kept for compatibility.
 */
public interface IChunkTracker {

    boolean viafabricplus$isChunkLoaded(int chunkX, int chunkZ);
}
