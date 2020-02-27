package jack.com.servicekeep.fork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;

public enum NativeRuntime {

    INSTANCE;

    static {
        try {
            System.loadLibrary("helper");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean runLocalUserCommand(String packageName, String command, StringBuilder stringBuilder) {
        try {
            Process process = Runtime.getRuntime().exec("sh");
            DataInputStream inputStream = new DataInputStream(process.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());
            outputStream.writeBytes("cd /data/data/" + packageName + "\n");
            outputStream.writeBytes(command + " &\n");
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            process.waitFor();
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            String s = new String(buffer);
            if (stringBuilder != null) {
                stringBuilder.append("CMD Result:\n" + s);
            }
        } catch (Exception e) {
            if (stringBuilder != null) {
                stringBuilder.append("Exception" + e.getMessage());
            }
            return false;
        }
        return true;
    }

    public String runExecutable(String packageName, String soFileName, String soFile, String serviceName) {
        String path = "/data/data/" + packageName;
        String cmd1 = path + "/lib/" + soFileName;
        String cmd2 = path + "/" + soFile;
        String cmd2_a1 = path + "/" + soFile + " " + serviceName;
        String cmd3 = "chmod 777" + cmd2;
        String cmd4 = "dd if=" + cmd1 + " of=" + cmd2;
        StringBuilder stringBuilder = new StringBuilder();
        if (!new File("/data/data/" + soFile).exists()) {
            runLocalUserCommand(packageName, cmd4, stringBuilder);
            stringBuilder.append(";");
        }
        runLocalUserCommand(packageName, cmd3, stringBuilder);
        stringBuilder.append(";");
        runLocalUserCommand(packageName, cmd2_a1, stringBuilder);
        stringBuilder.append(";");
        return stringBuilder.toString();
    }

    public native void startService(String srvname, String sdpath);

}
