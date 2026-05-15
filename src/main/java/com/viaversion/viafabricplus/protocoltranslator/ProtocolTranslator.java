package com.viaversion.viafabricplus.protocoltranslator;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

/**
 * Central access point for the currently targeted protocol version.
 *
 * <p>This is the minimal stub needed so that all Bedrock mixin classes that
 * call {@code ProtocolTranslator.getTargetVersion()} compile correctly.
 * The real implementation lives in the full ViaFabricPlus core; this class
 * is provided here to satisfy the compiler for the 4.0.5-BACKPORT-bedrock126
 * delta source that only ships the new Bedrock-specific files.</p>
 *
 * <p>At runtime the full ViaFabricPlus 4.0.5-BACKPORT JAR (which already
 * contains {@code ProtocolTranslator}) is on the classpath, so this stub
 * must never end up in a final fat JAR alongside the real implementation.
 * Mark it {@code compileOnly} or rely on the fact that Loom strips
 * {@code modImplementation} → provided scope correctly.</p>
 */
public final class ProtocolTranslator {

    private static volatile ProtocolVersion targetVersion = ProtocolVersion.unknown;

    private ProtocolTranslator() {}

    /**
     * Returns the protocol version the player has chosen to connect with.
     *
     * <p>Bedrock mixins compare this against
     * {@code net.raphimc.viabedrock.api.BedrockProtocolVersion#bedrockLatest}
     * to decide whether to apply Bedrock-specific behaviour.</p>
     *
     * @return the current target {@link ProtocolVersion}; never {@code null}
     */
    public static ProtocolVersion getTargetVersion() {
        return targetVersion;
    }

    /**
     * Sets the target version.  Called by the ViaFabricPlus initialisation
     * sequence when the user selects a server version in the GUI.
     *
     * @param version the new target version; must not be {@code null}
     */
    public static void setTargetVersion(final ProtocolVersion version) {
        if (version == null) throw new NullPointerException("version");
        targetVersion = version;
    }
}
