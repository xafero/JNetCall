package jnethotel.java.linux.impl;

import java.nio.ByteBuffer;

import com.sun.jna.Library;

import jnethotel.java.interop.api.nethost_library;

public interface nethost_library_unix
    extends nethost_library<ByteBuffer>, Library {
}
