package jnetproto.java.tests;

import com.xafero.javaenums.BitFlag;
import com.xafero.javaenums.Enums;
import com.xafero.javaenums.flags.IntFlag;
import com.xafero.javaenums.units.ShortEnum;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class EnumTest {
    @DataProvider(name = "writeArgs1")
    public Object[][] getWriteArgs1() {
        return new Object[][]
                {
                        {"13000000130404000000000A0100730300000415000000", Season.Spring, "s", ErrorCode.None, new Days[]{Days.Monday, Days.Wednesday, Days.Friday}},
                        {"13000000130404020000000A01006103C8000418000000", Season.Autumn, "a", ErrorCode.OutlierReading, new Days[]{Days.Thursday, Days.Friday}},
                        {"13000000130404030000000A0100770364000402000000", Season.Winter, "w", ErrorCode.ConnectionLost, new Days[]{Days.Tuesday}},
                        {"13000000130404030000000A0100770300000400000000", Season.Winter, "w", null, null}
                };
    }

    @Test(dataProvider = "writeArgs1")
    public void shouldWrite1(String hex, Season what, String why, ErrorCode code, Days[] days)
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

    @DataProvider(name = "writeArgs2")
    public Object[][] getWriteArgs2() {
        return new Object[][]
                {
                        {"2900000013030E04010200000001000000020000000E0A01020000000100680100770E030102000000C8006400", new Season[]{Season.Summer, Season.Autumn}, new String[]{"h", "w"}, new ErrorCode[]{ErrorCode.OutlierReading, ErrorCode.ConnectionLost}},
                        {"2000000013030E040101000000010000000E0A01010000000100680E030101000000C800", new Season[]{Season.Summer}, new String[]{"h"}, new ErrorCode[]{ErrorCode.OutlierReading}},
                        {"1700000013030E0401000000000E0A01000000000E030100000000", new Season[0], new String[0], new ErrorCode[0]}
                };
    }

    @Test(dataProvider = "writeArgs2")
    public void shouldWrite2(String hex, Season[] whats, String[] whys, ErrorCode[] codes)
            throws Exception {
        var texted = new Texted(whats, whys, codes);
        ComplexTest.testWrite(hex, texted, r -> {
            try {
                return r.readObject(Texted.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public record Bitty(Season What, String Why, ErrorCode Code, BitFlag<Days> Days) {
    }

    public record Texted(Season[] Whats, String[] Whys, ErrorCode[] Codes) {
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
