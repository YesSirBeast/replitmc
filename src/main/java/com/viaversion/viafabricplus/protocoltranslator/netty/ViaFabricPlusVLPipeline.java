package com.viaversion.viafabricplus.protocoltranslator.netty;

import com.viaversion.vialoader.netty.VLPipeline;
import com.viaversion.viaversion.api.connection.UserConnection;
import io.netty.channel.ChannelHandler;
import net.raphimc.viabedrock.netty.PacketCodec;

/**
 * ViaFabricPlus-specific {@link VLPipeline} implementation for Minecraft 1.21.4
 * (Yarn mappings).
 *
 * <p><b>Why a subclass?</b>
 * ViaLoader 4.0.3 was compiled against an older ViaBedrock build that had a class
 * named {@code PacketEncapsulationCodec}.  ViaBedrock 0.0.27 (Bedrock 1.26) renamed
 * that class to {@link PacketCodec}.  By overriding
 * {@link #createViaBedrockPacketEncapsulationHandler()} we swap in the renamed class
 * without ever reaching ViaLoader's {@code new PacketEncapsulationCodec()} bytecode.</p>
 *
 * <p><b>Minecraft 1.21.4 pipeline handler names</b> (Yarn-mapped, verified from
 * {@code ClientConnection.addHandlers} bytecode):</p>
 * <ul>
 *   <li>{@code "splitter"}  — packet-length frame decoder</li>
 *   <li>{@code "decoder"}   — packet byte→object decoder</li>
 *   <li>{@code "compress"}  — zlib compression/decompression codec</li>
 * </ul>
 *
 * <p>These string constants are what VLPipeline uses for
 * {@link #lengthCodecName()}, {@link #packetCodecName()}, and
 * {@link #compressionCodecName()} respectively so that {@code userEventTriggered}
 * can locate and replace the Java-side codecs when the protocol version changes.</p>
 */
public class ViaFabricPlusVLPipeline extends VLPipeline {

    public ViaFabricPlusVLPipeline(final UserConnection connection) {
        super(connection);
    }

    @Override
    protected String compressionCodecName() {
        return "compress";
    }

    @Override
    protected String packetCodecName() {
        return "decoder";
    }

    @Override
    protected String lengthCodecName() {
        return "splitter";
    }

    /**
     * Returns a {@link PacketCodec} instance (ViaBedrock 0.0.27 name) instead of
     * the removed {@code PacketEncapsulationCodec} that ViaLoader 4.0.3 references.
     * The JVM never executes VLPipeline's original bytecode for this method because
     * we override it here.
     */
    @Override
    protected ChannelHandler createViaBedrockPacketEncapsulationHandler() {
        return new PacketCodec();
    }
}
