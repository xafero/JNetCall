package jnethotel.java.api;

import java.io.IOException;

public interface IVmRef {

    String getVmDll();

    void loadLib() throws IOException;

    ICoreClr getCoreClr();
}
