package jnetproto.java;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class BinaryWriter implements IDataWriter {
    private final Charset _enc;
    private final DateTimeFormatter _fmt;
    private final OutputStream _stream;

    public BinaryWriter(OutputStream stream) {
        _enc = Charset.forName("UTF8");
        _fmt = DateTimeFormatter.ofPattern("SSSSSSS");
        _stream = stream;
    }

    @Override
    public void writeU8(byte value) throws IOException {
        _stream.write(new byte[]{value});
    }

    @Override
    public void writeI8(byte value) throws IOException {
        _stream.write(new byte[]{value});
    }

    @Override
    public void writeI16(short value) throws IOException {
        _stream.write(BitConverter.getBytes(value));
    }

    @Override
    public void writeI32(int value) throws IOException {
        _stream.write(BitConverter.getBytes(value));
    }

    @Override
    public void writeI64(long value) throws IOException {
        _stream.write(BitConverter.getBytes(value));
    }

    @Override
    public void writeF32(float value) throws IOException {
        _stream.write(BitConverter.getBytes(value));
    }

    @Override
    public void writeF64(double value) throws IOException {
        _stream.write(BitConverter.getBytes(value));
    }

    @Override
    public void writeF128(BigDecimal value) throws IOException
    {
        writeUtf8(value.toString());
    }

    @Override
    public void writeUtf8(String value) throws IOException
    {
        var bytes = value.getBytes(_enc);
        _stream.write((byte)bytes.length);
        _stream.write(bytes);
    }

    @Override
    public void writeDuration(Duration value) throws IOException {
        writeF64(value.toMillis());
    }

    @Override
    public void writeTimestamp(LocalDateTime value) throws IOException {
        var date = value.atZone(ZoneOffset.UTC);
        writeI64(date.toEpochSecond());
        writeI32(Integer.parseInt(date.format(_fmt)));
    }

    @Override
    public void writeGuid(UUID value) throws IOException {
        _stream.write(BitConverter.getBytes(value));
    }

    @Override
    public void writeObject(Object value) throws IOException {
        var kind = DataTypes.getKind(value.getClass());
        _stream.write(kind.ordinal());
        switch (kind)
        {
            case U8: writeU8((byte)value); break;
            case I8: writeI8((byte)value); break;
            case I16: writeI16((short)value); break;
            case I32: writeI32((int)value); break;
            case I64: writeI64((long)value); break;
            case F32: writeF32((float)value); break;
            case F64: writeF64((double)value); break;
            case F128: writeF128((BigDecimal)value); break;
            case UTF8: writeUtf8((String)value); break;
            case Duration: writeDuration((Duration) value); break;
            case Timestamp: writeTimestamp((LocalDateTime) value); break;
            case Guid: writeGuid((UUID) value); break;
            default: throw new IllegalArgumentException(kind.toString());
        }
    }

    @Override
    public void close() throws Exception {
        _stream.close();
    }
}