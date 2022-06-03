package jnethotel.java.linux.impl;

import com.sun.jna.Library;
import jnethotel.java.interop.api.nethost_library;

import java.nio.ByteBuffer;

public interface nethost_library_unix
    extends nethost_library<ByteBuffer>, Library {
}
