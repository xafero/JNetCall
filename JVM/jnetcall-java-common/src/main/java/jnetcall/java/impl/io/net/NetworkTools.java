package jnetcall.java.impl.io.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;

public final class NetworkTools {

	public static SocketAddress toEndPoint(String host, int port) {
		var addr = new InetSocketAddress(host, port);
		return addr;
	}

	private static ByteBuffer tryRead(ReadableByteChannel stream, int size, ByteBuffer prefix) 
			throws IOException 
	{
		var skip = prefix != null ? prefix.capacity() : 0;
		size += skip;
		var buffer = ByteBuffer.allocate(size);
		int got;
		if (prefix == null) {
			got = stream.read(buffer);
		} else {
			buffer.put(prefix.position(0));
			var tmp = stream.read(buffer);
			got = tmp + skip;
		}
		if (size != got) {
			throw new UnsupportedOperationException(size + " != " + got);
		}
		buffer.position(0);
		return buffer;
	}

	public static ByteBuffer readWithSize(ReadableByteChannel stream) throws IOException {
		var sizeBytes = tryRead(stream, 4, null);
		var size = sizeBytes.order(ByteOrder.nativeOrder()).getInt();
		var bytes = tryRead(stream, size, sizeBytes);
		return bytes;
	}
}
