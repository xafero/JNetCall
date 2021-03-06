package jnetproto.java.core;

import com.xafero.javaenums.Enums;
import jnetbase.java.meta.Reflect;
import jnetbase.java.sys.BitConverter;
import jnetproto.java.api.IDataWriter;
import org.javatuples.Tuple;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.StreamSupport;

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
        writeUtf8(raw, false);
    }

    @Override
    public void writeChar(char value) throws IOException
    {
        writeI16((short) value);
    }

    @Override
    public void writeUtf8(String value) throws IOException
    {
        writeUtf8(value, true);
    }

    private void writeUtf8(String value, boolean wide) throws IOException {
        var bytes = value.getBytes(_enc);
        if (wide)
            writeI16((short) bytes.length);
        else
            writeI8((byte) bytes.length);
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
        var rank = Reflect.getRank(value);
        for (var dim = 0; dim < rank; dim++)
            writeI32(Array.getLength(value));
        for (int i = 0; i < Array.getLength(value); i++) {
            var item = Array.get(value, i);
            writeObject(item, true);
        }
    }

    @Override
    public void writeMap(Map<?,?> value) throws IOException {
        writeI32(value.size());
        for (var item : value.entrySet()) {
            var entry = (Map.Entry<?,?>) item;
            writeObject(entry.getKey(), true);
            writeObject(entry.getValue(), true);
        }
    }

    @Override
    public void writeTuple(Tuple value) throws IOException {
        var array = value.toArray();
        writeI8((byte)array.length);
        for (var i = 0; i < array.length; i++)
        {
            writeObject(array[i], false);
        }
    }

    @Override
    public void writeSet(Set<?> value) throws IOException {
        writeIterable(value, true);
    }

    @Override
    public void writeList(List<?> value) throws IOException {
        writeIterable(value, true);
    }

    @Override
    public void writeBag(Object[] value) throws IOException {
        writeI8((byte)value.length);
        for (var item : value)
            writeObject(item, false);
    }

    @Override
    public void writeBinary(byte[] value) throws IOException {
        writeI32(value.length);
        _stream.write(value);
    }

    private void writeIterable(Iterable<?> raw, boolean skipHeader) throws IOException {
        int count;
        Iterable<?> values;
        if (raw instanceof Collection<?> coll) {
            count = coll.size();
            values = raw;
        } else {
            var array = StreamSupport.stream(raw.spliterator(), false).toList();
            count = array.size();
            values = array;
        }
        writeI32(count);
        for (var entry : values)
            writeObject(entry, skipHeader);
    }

    @Override
    public void writeNull() {
    }

    @Override
    public void writeObject(Object value) throws IOException {
        writeObject(value, false);
    }

    private void writeObject(Object value, boolean skipHeader) throws IOException {
        var kind = DataTypes.getKind(value);
        if (!skipHeader)
        {
            _stream.write(DataTypes.getByte(kind));
            if (kind instanceof DataTypes.ArrayDt adt)
            {
                _stream.write(DataTypes.getByte(adt.Item()));
                _stream.write((byte)adt.Rank());
            }
            else if (kind instanceof DataTypes.MapDt mdt)
            {
                _stream.write(DataTypes.getByte(mdt.Key()));
                _stream.write(DataTypes.getByte(mdt.Val()));
            }
            else if (kind instanceof DataTypes.ListDt ldt)
            {
                _stream.write(DataTypes.getByte(ldt.Item()));
            }
        }
        if (kind instanceof DataTypes.EnumDt edt)
        {
            value = Enums.castToNumber(value, edt.Type());
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
            case Map: writeMap((Map<?,?>) value); break;
            case Tuple: writeTuple((Tuple) value); break;
            case Set: writeSet((Set<?>)value); break;
            case List: writeList((List<?>)value); break;
            case Bag: writeBag((Object[])value); break;
            case Binary: writeBinary((byte[])value); break;
            case Null: writeNull(); break;
            default: throw new IllegalArgumentException(kind.toString());
        }
    }

    @Override
    public void flush() throws IOException {
        _stream.flush();
    }

    @Override
    public void close() throws Exception {
        if (_stream == null)
            return;
        _stream.close();
    }
}
