package jnetproto.java.tests;

import com.xafero.javaenums.BitFlag;
import com.xafero.javaenums.Enums;
import com.xafero.javaenums.flags.IntFlag;
import com.xafero.javaenums.units.ShortEnum;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class EnumTest {
    @DataProvider(name = "writeArgs")
    public Object[][] getWriteArgs() {
        return new Object[][]
                {
                        {"13000000130404000000000A0100730300000415000000", Season.Spring, "s", ErrorCode.None, new Days[]{Days.Monday, Days.Wednesday, Days.Friday}},
                        {"13000000130404020000000A01006103C8000418000000", Season.Autumn, "a", ErrorCode.OutlierReading, new Days[]{Days.Thursday, Days.Friday}},
                        {"13000000130404030000000A0100770364000402000000", Season.Winter, "w", ErrorCode.ConnectionLost, new Days[]{Days.Tuesday}},
                        {"13000000130404030000000A0100770300000400000000", Season.Winter, "w", null, null}
                };
    }

    @Test(dataProvider = "writeArgs")
    public void shouldWrite(String hex, Season what, String why, ErrorCode code, Days[] days)
            throws Exception {
        var bitty = new Bitty(what, why, Enums.notNull(ErrorCode.class, code), BitFlag.of32(Days.class, days));
        ComplexTest.testWrite(hex, bitty, r -> {
            try {
                return r.readObject(Bitty.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public record Bitty(Season What, String Why, ErrorCode Code, BitFlag<Days> Days) {
    }

    public enum Season {
        Spring,
        Summer,
        Autumn,
        Winter
    }

    public enum ErrorCode implements ShortEnum {
        None(0),
        Unknown(1),
        ConnectionLost(100),
        OutlierReading(200);

        private final short _value;

        ErrorCode(int value) {
            _value = (short) value;
        }

        @Override
        public Short asNumber() {
            return _value;
        }
    }

    public enum Days implements IntFlag {
        None(0b0000_0000), // 0
        Monday(0b0000_0001), // 1
        Tuesday(0b0000_0010), // 2
        Wednesday(0b0000_0100), // 4
        Thursday(0b0000_1000), // 8
        Friday(0b0001_0000), // 16
        Saturday(0b0010_0000), // 32
        Sunday(0b0100_0000), // 64
        Weekend(96); // Saturday | Sunday

        private final int _value;

        Days(int value) {
            _value = value;
        }

        @Override
        public Integer asNumber() {
            return _value;
        }
    }
}
