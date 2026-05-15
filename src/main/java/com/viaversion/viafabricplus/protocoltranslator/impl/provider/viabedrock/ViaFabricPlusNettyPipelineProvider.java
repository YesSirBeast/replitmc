package com.viaversion.viafabricplus.protocoltranslator.impl.provider.viabedrock;

import com.viaversion.viaversion.api.connection.UserConnection;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.raphimc.viabedrock.netty.CompressionCodec;
import net.raphimc.viabedrock.netty.raknet.AesEncryptionCodec;
import net.raphimc.viabedrock.protocol.data.enums.bedrock.generated.PacketCompressionAlgorithm;
import net.raphimc.viabedrock.protocol.provider.NettyPipelineProvider;

import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;

/**
 * Provides Bedrock-specific Netty pipeline stages to ViaBedrock.
 *
 * <p><b>ViaBedrock 0.0.27 API (Bedrock 1.26):</b>
 * {@link NettyPipelineProvider} now declares two abstract methods:
 * {@link #enableCompression} and {@link #enableEncryption}.
 * The old {@code setupPipeline} no longer exists; pipeline initialisation
 * for RakNet / NetherNet is now handled via dedicated mixins and
 * {@code MixinClientConnection}.</p>
 *
 * <p>Ported from ViaFabricPlus 4.5.3 — same class path as 4.0.5-BACKPORT.</p>
 */
public class ViaFabricPlusNettyPipelineProvider extends NettyPipelineProvider {

    private static final String COMPRESSION_CODEC_NAME = "bedrock-compression";
    private static final String ENCRYPTION_CODEC_NAME  = "bedrock-encryption";

    /**
     * Called by ViaBedrock when the server sends a {@code StartGamePacket} that
     * enables compression.  Inserts a {@link CompressionCodec} into the pipeline.
     *
     * @param userConnection the ViaVersion user connection for this channel
     * @param algorithm      the compression algorithm negotiated with the server
     * @param compressionThreshold minimum packet size (bytes) before compressing
     */
    @Override
    public void enableCompression(final UserConnection userConnection,
                                  final PacketCompressionAlgorithm algorithm,
                                  final int compressionThreshold) {

        final Channel channel = userConnection.getChannel();
        if (channel == null) return;

        final ChannelPipeline pipeline = channel.pipeline();
        if (pipeline.get(COMPRESSION_CODEC_NAME) == null) {
            pipeline.addBefore("via-codec", COMPRESSION_CODEC_NAME,
                    new CompressionCodec(algorithm, compressionThreshold));
        }
    }

    /**
     * Called by ViaBedrock after the Bedrock handshake when the server enables
     * encryption.  Inserts an {@link AesEncryptionCodec} into the pipeline.
     *
     * @param userConnection the ViaVersion user connection for this channel
     * @param secretKey      the shared AES-256-GCM key agreed during the handshake
     */
    @Override
    public void enableEncryption(final UserConnection userConnection,
                                 final SecretKey secretKey) {

        final Channel channel = userConnection.getChannel();
        if (channel == null) return;

        final ChannelPipeline pipeline = channel.pipeline();
        if (pipeline.get(ENCRYPTION_CODEC_NAME) == null) {
            try {
                pipeline.addBefore("via-codec", ENCRYPTION_CODEC_NAME,
                        new AesEncryptionCodec(secretKey));
            } catch (NoSuchPaddingException | NoSuchAlgorithmException
                     | InvalidAlgorithmParameterException | InvalidKeyException e) {
                throw new RuntimeException("Failed to initialise Bedrock AES encryption", e);
            }
        }
    }
}
