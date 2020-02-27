package com.vietmobi.mobile.audiovideocutter.ui.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

public class PublicMethod {

    public static String getFileLength(double d) {
        double d2 = d / 1024.0d;
        double d3 = d2 / 1024.0d;
        double d4 = d3 / 1024.0d;
        if (d < 1024.0d) {
            StringBuilder sb = new StringBuilder();
            sb.append(d);
            sb.append(" bytes");
            return sb.toString();
        } else if (d2 < 1024.0d) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(new BigDecimal(d2).setScale(2, 4).toString());
            sb2.append(" kb");
            return sb2.toString();
        } else if (d3 < 1024.0d) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(new BigDecimal(d3).setScale(2, 4).toString());
            sb3.append(" mb");
            return sb3.toString();
        } else {
            StringBuilder sb4 = new StringBuilder();
            sb4.append(new BigDecimal(d4).setScale(2, 4).toString());
            sb4.append(" gb");
            return sb4.toString();
        }
    }

    public static String converTime(String str) {
        DecimalFormat decimalFormat = new DecimalFormat("00");
        try {
            int parseFloat = (int) Float.parseFloat(str.toString());
            if (parseFloat == 0) {
                return "00:00";
            }
            if (parseFloat < 60) {
                StringBuilder sb = new StringBuilder();
                sb.append("00:");
                sb.append(decimalFormat.format((long) parseFloat));
                return sb.toString();
            }
            int i = parseFloat / 60;
            int i2 = parseFloat % 60;
            if (i < 60) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(decimalFormat.format((long) i));
                sb2.append(":");
                sb2.append(decimalFormat.format((long) i2));
                return sb2.toString();
            }
            int i3 = i / 60;
            StringBuilder sb3 = new StringBuilder();
            sb3.append(decimalFormat.format((long) i3));
            sb3.append(":");
            sb3.append(decimalFormat.format((long) (i % 60)));
            sb3.append(":");
            sb3.append(decimalFormat.format((long) i2));
            return sb3.toString();
        } catch (Exception unused) {
            return "00:00";
        }
    }

    public static void mergerFile(String str, ArrayList<String> arrayList) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(str);
            Vector vector = new Vector();
            ArrayList arrayList2 = new ArrayList();
            for (int i = 0; i < arrayList.size(); i++) {
                FileInputStream fileInputStream = new FileInputStream((String) arrayList.get(i));
                vector.add(fileInputStream);
                arrayList2.add(fileInputStream);
            }
            SequenceInputStream sequenceInputStream = new SequenceInputStream(vector.elements());
            byte[] bArr = new byte[1024];
            while (true) {
                int read = sequenceInputStream.read(bArr);
                if (read == -1) {
                    break;
                }
                fileOutputStream.write(bArr, 0, read);
            }
            fileOutputStream.close();
            for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                ((FileInputStream) arrayList2.get(i2)).close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static double getTimeCurent(String str) {
        String[] split = str.split(":");
        double d = 0.0d;
        if (Integer.parseInt(split[0]) > 0) {
            d = 0.0d + ((double) (Integer.parseInt(split[0]) * 60 * 60));
        }
        if (Integer.parseInt(split[1]) > 0) {
            d += (double) (Integer.parseInt(split[1]) * 60);
        }
        if (Integer.parseInt(split[2]) > 0) {
            d += (double) Integer.parseInt(split[2]);
        }
        return d * 1000.0d;
    }
}
