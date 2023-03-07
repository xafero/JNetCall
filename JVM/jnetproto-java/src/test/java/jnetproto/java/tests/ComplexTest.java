package jnetproto.java.tests;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jnetbase.java.meta.ParamName;
import jnetbase.java.meta.TypeToken;
import jnetproto.java.beans.ProtoConvert;
import jnetproto.java.beans.ProtoSettings;

public final class ComplexTest {

    @DataProvider(name = "writeArgs2")
    public Object[][] getWriteArgs2() {
        return new Object[][]
        {
            {"380000001302130213040A0100630A01006D13010961000E0A010100000001006813040A0100640A01006E13010962000E0A01010000000100690207", 'C'},
            {"380000001302130213040A0100630A01006D13010961000E0A010100000001006813040A0100640A01006E13010962000E0A01010000000100690207", 'N'},
            {"360000001301130213040A0100630A01006D13010961000E0A010100000001006813040A0100640A01006E13010962000E0A0101000000010069", 'B'},
            {"360000001301130213040A0100630A01006D13010961000E0A010100000001006813040A0100640A01006E13010962000E0A0101000000010069", 'M'},
            {"34000000130213040A0100630A01006D13010961000E0A010100000001006813040A0100640A01006E13010962000E0A0101000000010069", 'A'},
            {"34000000130213040A0100630A01006D13010961000E0A010100000001006813040A0100640A01006E13010962000E0A0101000000010069", 'L'},
            {"020000001300", 'D'},
            {"020000001300", 'O'}
        };
    }

    @Test(dataProvider = "writeArgs2")
    public void ShouldColl(String hex, char mode) throws Exception {
        boolean isArray0 = mode == 'A';
        boolean isList1 = mode == 'M';
        boolean isArray1 = mode == 'B';
        boolean isList2 = mode == 'N';
        boolean isArray2 = mode == 'C';
        boolean isListE = mode == 'O';
        boolean isArrayE = mode == 'D';

        Call example1 = new Call("c", "m", new Object[] { 'a' }, new String[] { "h" });
        Call example2 = new Call("d", "n", new Object[] { 'b' }, new String[] { "i" });

        Object value = isArrayE ? new Call[0]
                : isListE ? new ArrayList<Call>()
                : isArray2 ? new CallArrayBag2(new Call[] { example1, example2 }, (byte)0x07)
                : isList2 ? new CallListBag2(Arrays.asList(  example1, example2  ), (byte)0x07)
                : isArray1 ? new CallArrayBag1(new Call[] { example1, example2 })
                : isList1 ? new CallListBag1( Arrays.asList( example1, example2  ))
                : isArray0 ? new Call[] { example1, example2 }
                : Arrays.asList(  example1, example2  );

        Function<ProtoConvert, Object> creator = r -> {
            try {
                return isArrayE ?  r.readObject(Call[].class)
                : isListE ?   r.readObject(  new TypeToken<List<Call>>() {}  )
                : isArray2 ?  r.readObject(CallArrayBag2.class)
                : isList2 ?   r.readObject(CallListBag2.class)
                : isArray1 ?   r.readObject(CallArrayBag1.class)
                : isList1 ?   r.readObject(CallListBag1.class)
                : isArray0 ?  r.readObject(Call[].class)
                :     r.readObject(  new TypeToken<List<Call>>() {}  );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        testWrite(hex, value, creator);
    }

    @DataProvider(name = "writeArgs")
    public Object[][] getWriteArgs() {
        return new Object[][]
                {
                        {"1400000013020A0C007468697320697320676F6F64030800", 3, null, 5d, "this is good"},
                        {"0C00000013020A040063726170030600", 2, null, 4d, "crap"},
                        {"2600000013040A03004333330A03005365741302042A0000000A01006C0E0A0102000000010069010073", 42, 33L, null, "Set"},
                        {"2500000013040A020043370A0300476574130204030000000A01006C0E0A0102000000010069010073", 3, 7L, null, "Get"},
                        {"230000001304040A0000000504000000000000000700000000000014400A070074687269667479", 10, 4L, 5d, "thrifty"},
                        {"1F0000001304040500000005030000000000000007000000000000F03F0A03006E6F74", 5, 3L, 1d, "not"},
                        {"110000001302040A0000000A070074687269667479", 10, null, null, "thrifty"},
                        {"0D000000130204050000000A03006E6F74", 5, null, null, "not"}
                };
    }

    @Test(dataProvider = "writeArgs")
    public void ShouldWrite(String hex, int number, Long bigNumber, Double decimals, String name)
            throws Exception {
        boolean isErr = bigNumber == null && decimals == null;
        boolean isCall = bigNumber != null && decimals == null;
        boolean isRes = bigNumber == null && decimals != null;

        Object value = isCall ? new Call("C" + bigNumber, name,
                new Object[]{number, "l"}, new String[]{"i", "s"})
                : isErr ? new Invalid(number, name)
                : isRes ? new Result(name, (short) (decimals + number))
                : new Example(number, bigNumber, decimals, name);

        Function<ProtoConvert, Object> creator = r -> {
            try {
                return isCall ? r.readObject(Call.class)
                        : isErr ? r.readObject(Invalid.class)
                        : isRes ? r.readObject(Result.class)
                        : r.readObject(Example.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        testWrite(hex, value, creator);
    }

    static void testWrite(String hex, Object value, Function<ProtoConvert, Object> creator)
            throws Exception {
    	ProtoSettings s = new ProtoSettings();

        ByteArrayOutputStream[] mem = new ByteArrayOutputStream[1];
        try (ProtoConvert writer = createWriter(mem, s)) {
            writer.writeObject(value);
            String actual = BinaryTest.toHex(mem[0]);
            assertEquals(actual, hex);

            try (ProtoConvert reader = createReader(mem[0], s)) {
                Object obj = creator.apply(reader);

                if (!(obj instanceof Call) && !(obj instanceof EnumTest.Texted) &&
                        !(obj instanceof Call[]) && !(obj instanceof Collection) &&
                        !(obj instanceof CallArrayBag1) && !(obj instanceof CallArrayBag2) &&
                        !(obj instanceof CallListBag1) && !(obj instanceof CallListBag2))
                {
                    assertEquals(value, obj);
                    return;
                }
                Gson gson = (new GsonBuilder()).create();
                String valueJson = gson.toJson(value);
                String objJson = gson.toJson(obj);
                assertEquals(valueJson, objJson);
            }
        }
    }

    private static ProtoConvert createWriter(ByteArrayOutputStream[] mem, ProtoSettings s)
    {
        return new ProtoConvert(null, mem[0] = new ByteArrayOutputStream(), s);
    }

    private static ProtoConvert createReader(ByteArrayOutputStream mem, ProtoSettings s)
    {
        return new ProtoConvert(new ByteArrayInputStream(mem.toByteArray()), null, s);
    }

    public static final class Example {
    	private final int number;
    	private final long bigNumber;
    	private final double decimals;
    	private final String name;
    	
    	public Example(@ParamName("Number") int number, 
    			@ParamName("BigNumber") long bigNumber, 
    			@ParamName("Decimals") double decimals,
    			@ParamName("Name") String name) {
    		this.number=number;
    		this.bigNumber=bigNumber;
    		this.decimals=decimals;
    		this.name=name;
    	}
    	
    	public int Number() {
			return number;
		}
    	public long BigNumber() {
			return bigNumber;
		}
    	public double Decimals() {
			return decimals;
		}
    	public String Name() {
			return name;
		}
	}
    
    public static final class Invalid {
    	private final int what;
    	private final String why;
    	
    	public Invalid(@ParamName("What") int what, 
    			@ParamName("Why") String why) { 
    		this.what=what;
    		this.why=why;
    	} 
    	
    	public int What() {
			return what;
		}
    	public String Why() {
			return why;
		}
	}

    public static final class Call {
    	private final String c;
    	private final String m;
    	private final Object[] a;
    	private final String[] h;
    	
    	public Call(@ParamName("C") String c, 
    			@ParamName("M") String m, 
    			@ParamName("A") Object[] a, 
    			@ParamName("H") String[] h) { 
    		this.c=c;
    		this.m=m;
    		this.a=a;
    		this.h=h;
    	}
    	
    	public String C() {
			return c;
		}
    	public String M() {
			return m;
		}
    	public Object[] A() {
			return a;
		}
    	public String[] H() {
			return h;
		}
	}
    
    public static final class Result {
    	private final Object r;
    	private final short s;
    	
    	public Result(@ParamName("R") Object r, 
    			@ParamName("S") short s) { 
    		this.r=r;
    		this.s=s;
    	} 
    	
    	public Object R() {
			return r;
		}
    	public short S() {
			return s;
		}
	}

    public static final class CallListBag2 {
    	private final List<Call> calls;
    	private final byte ord;
    	
    	public CallListBag2(@ParamName("Calls") List<Call> calls, 
    			@ParamName("Ord") byte ord) { 
    		this.calls=calls;
    		this.ord=ord;
    	} 
    	
    	public List<Call> Calls() {
			return calls;
		}
    	public byte Ord() {
			return ord;
		}
	}
    
    public static final class CallListBag1 {
    	private final List<Call> calls;
    	
    	public CallListBag1(@ParamName("Calls") List<Call> calls) { 
    		this.calls=calls;
    	}
    	
    	public List<Call> Calls() {
			return calls;
		}
	}
    
    public static final class CallArrayBag2 { 
    	private final Call[] calls;
    	private final byte ord;
    	
    	public CallArrayBag2(@ParamName("Calls") Call[] calls, 
    			@ParamName("Ord") byte ord) { 
    		this.calls=calls;
    		this.ord=ord;
    	} 
    	
    	public Call[] Calls() {
			return calls;
		}
    	public byte Ord() {
			return ord;
		}
	}
    
    public static final class CallArrayBag1 { 
    	private final Call[] calls;
    	
    	public CallArrayBag1(@ParamName("Calls") Call[] calls) { 
    		this.calls=calls;
    	} 
    	
    	public Call[] Calls() {
			return calls;
		}
	}
}
