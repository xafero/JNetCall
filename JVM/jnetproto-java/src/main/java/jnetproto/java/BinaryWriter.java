package jnetproto.java;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
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
    public void writeBool(boolean value) throws IOException {
        writeI8((byte) (value ? 1 : 0));
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
        var raw = value.toString();
        writeUtf8(raw);
    }

    @Override
    public void writeChar(char value) throws IOException
    {
        writeI16((short) value);
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
        var raw = (double) value.toMillis();
        writeF64(raw);
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
    public void writeArray(Object value) throws IOException {
        var rank = BitConverter.getRank(value);
        for (var dim = 0; dim < rank; dim++)
            writeI32(Array.getLength(value));
        for (int i = 0; i < Array.getLength(value); i++) {
            var item = Array.get(value, i);
            writeObject(item, true);
        }
    }

    @Override
    public void writeObject(Object value) throws IOException {
        writeObject(value, false);
    }

    private void writeObject(Object value, boolean skipHeader) throws IOException {
        var kind = DataTypes.getKind(value);
        if (!skipHeader)
        {
            _stream.write(kind.Kind().ordinal());
            if (kind instanceof DataTypes.ArrayDt adt)
            {
                _stream.write(adt.Item().Kind().ordinal());
                _stream.write((byte)adt.Rank());
            }
        }
        switch (kind.Kind())
        {
            case Bool: writeBool((boolean)value); break;
            case I8: writeI8((byte)value); break;
            case I16: writeI16((short)value); break;
            case I32: writeI32((int)value); break;
            case I64: writeI64((long)value); break;
            case F32: writeF32((float)value); break;
            case F64: writeF64((double)value); break;
            case F128: writeF128((BigDecimal)value); break;
            case Char: writeChar((char)value); break;
            case UTF8: writeUtf8((String)value); break;
            case Duration: writeDuration((Duration) value); break;
            case Timestamp: writeTimestamp((LocalDateTime) value); break;
            case Guid: writeGuid((UUID) value); break;
            case Array: writeArray(value); break;
            default: throw new IllegalArgumentException(kind.toString());
        }
    }

    @Override
    public void close() throws Exception {
        _stream.close();
    }
}
