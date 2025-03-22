package com.zidong.classdesign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class txthelp {
    public txthelp(){

    }
    private static int gettxtrow(String readtxtfilepath) {
        int getpointrow=0;

        File file = new File(readtxtfilepath);
        int rowcount1=0;
        if(file.isFile() && file.exists())
        {

            try
            {

                FileInputStream fileInputStream1 = new FileInputStream(file);
                InputStreamReader inputStreamReader1 = new InputStreamReader(fileInputStream1);
                BufferedReader bufferedReader1 = new BufferedReader(inputStreamReader1);
                String text = null;
                while((text = bufferedReader1.readLine()) != null)
                {

                    if(text.charAt(0)=='*'){
                        continue;
                    }else
                    if(text.charAt(0)=='^'){
                        break;
                    }else {
                        getpointrow++;

                    }
                }
                bufferedReader1.close();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        return getpointrow;
    }
}
