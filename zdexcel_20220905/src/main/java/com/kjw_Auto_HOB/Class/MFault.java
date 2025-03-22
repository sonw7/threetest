package com.kjw_Auto_HOB.Class;

import java.util.ArrayList;
import java.util.List;

public class MFault {
    public String MFname;
    public List<String> Fname=new ArrayList<>();

    public MFault() {
    }

    public MFault(String MFname, List<String> fname) {
        this.MFname = MFname;
        Fname = fname;
    }

}
