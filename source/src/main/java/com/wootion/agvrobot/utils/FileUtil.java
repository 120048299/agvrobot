package com.wootion.agvrobot.utils;

import com.wootion.task.ReadScaleQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;

public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static String usrHome = System.getProperty("user.home");
    public static String osName = System.getProperty("os.name");

    /**
     * @return 指向web默认根目录 ，其下包括 front,jar,report,ptzset,video,audio等子目录
     */
    public static String getBasePath() {
        String basePath= "";
        if (osName.startsWith("Win")){ //for debug
            basePath = usrHome+"\\web\\";
        }else {
            basePath = usrHome+"/web/"; //real env
        }
        return basePath;
    }

    public static String getUserHome() {
        String userHome= "";
        if (osName.startsWith("Win")){ //for debug
            userHome = usrHome+"\\";
        }else {
            userHome = usrHome+"/"; //real env
        }
        return userHome;
    }


    public static String getForeignDetectPath(String uid) {
        return getUserHome() + "picture"  + File.separator + "foreign_detect"  + File.separator + uid + File.separator;
    }

    public static String getFindScalePath(String uid) {
        return getUserHome() + "picture"  + File.separator + "find_scale"  + File.separator + uid + File.separator;
    }

    public static String getPicturePath(String uid) {
        return getUserHome() + "picture"  + File.separator + uid + File.separator;
    }

    public static String getPresetPath(String uid) {
        return getUserHome() + "preset" + File.separator + uid + File.separator;
    }

    public static String dateRandom() {
        int random = (int) (Math.random() * 1000);
        String str = DateUtil.dateToString(new java.util.Date(), "yyyyMMdd-") + random;
        return str;
    }

    // 文件名去掉扩展名
    public static String getFileNameNoExt(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    public void addLine(String file, String conent) {
        FileWriter fw = null;
        try {
//如果文件存在，则追加内容；如果文件不存在，则创建文件
            File f=new File(file);
            fw = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            out.write(conent + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteLine(String file, String lineToRemove) {

        try {

            File inFile = new File(file);

            if (!inFile.isFile()) {
                System.out.println("Parameter is not an existing file");
                return;
            }

            //Construct the new file that will later be renamed to the original filename.
            File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

            BufferedReader br = new BufferedReader(new FileReader(file));
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

            String line = null;

            //Read from the original file and write to the new
            //unless content matches data to be removed.
            while ((line = br.readLine()) != null) {

                if (!line.trim().equals(lineToRemove)) {

                    pw.println(line);
                    pw.flush();
                }
            }
            pw.close();
            br.close();

            //Delete the original file
            if (!inFile.delete()) {
                System.out.println("Could not delete file");
                return;
            }

            //Rename the new file to the filename the original file had.
            if (!tempFile.renameTo(inFile))
                System.out.println("Could not rename file");

        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }

    public static String ReadFile(String Path){
        BufferedReader reader = null;
        String laststr = "";
        try{
            FileInputStream fileInputStream = new FileInputStream(Path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while((tempString = reader.readLine()) != null){
                laststr += tempString;
            }
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return laststr;
    }

    public void FileOutputStreamTest(String str,String filePath){
        FileOutputStream fos;
        try {
            fos=new FileOutputStream(filePath);
            fos.write(str.getBytes());
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteFile (String fileName) {
        try{
            File file = new File(fileName);
            return file.delete();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static int createDir(String dirName){
        File destDirFile=new File(dirName);
        if(!destDirFile.exists()){
            boolean ret=destDirFile.mkdirs();
            if(!ret){
                return -1;
            }
        }
        return 0;
    }

    //如果目标目录不存在则创建
    public static int copyFile (String srcFileName,String toFileName) {
        try{
            String destDir= toFileName.substring(0,toFileName.lastIndexOf("/"));
            File destDirFile=new File(destDir);
            if(!destDirFile.exists()){
                boolean ret=destDirFile.mkdirs();
                if(!ret){
                    return -1;
                }
            }
            File fromFile = new File(srcFileName);
            File toFile = new File(toFileName);
            Files.copy(fromFile.toPath(),toFile.toPath());
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

    public static boolean isLinuxSystem(){
        String osName=System.getProperty("os.name");
        if(osName.equalsIgnoreCase("linux")){
            return true;
        }else{
            return false;
        }
    }

    /**
     *
     * @param shellCommand
     * @return  0 为正常执行
     */
    public static int callShell(String shellCommand) {
        if(!isLinuxSystem()){
            logger.error("操作系统不是linux");
            return -1;
        }
        try{
            String[] cmd = new String[]{"sh", "-c", shellCommand};
            Process process = Runtime.getRuntime().exec(cmd);
            int exitValue = process.waitFor();
            System.out.println("FileUtil.callShell "+ shellCommand+" exitValue="+exitValue);
            return exitValue;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    public static int createLinkFile(String sourceFileName,String linkFileName){
        String command="ln "+sourceFileName+" "+linkFileName;
        int ret=FileUtil.callShell(command);
        return ret;
    }


    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            File myFilePath = new File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + File.separator + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + File.separator + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }
}
