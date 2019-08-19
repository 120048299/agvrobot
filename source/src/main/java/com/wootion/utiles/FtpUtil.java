package com.wootion.utiles;

import com.wootion.commons.Result;
import com.wootion.task.TaskRemoteManage;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.SocketException;
import java.util.Calendar;

public class FtpUtil {
    private static final Logger logger = LoggerFactory.getLogger(FtpUtil.class);

    public FTPClient ftpClient = null;

    public   int  connect(String ftpHost, String ftpUserName,
                                    String ftpPassword, int ftpPort)  {
        ftpClient = new FTPClient();
        ftpClient.setControlEncoding("utf-8");
        ftpClient.setDataTimeout(60000);       //设置传输超时时间为60秒
        ftpClient.setConnectTimeout(60000);       //连接超时为60秒
        try {
            ftpClient.connect(ftpHost, ftpPort);// 连接FTP服务器
            ftpClient.login(ftpUserName, ftpPassword);// 登陆FTP服务器
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                logger.error("未连接到FTP，用户名或密码错误。");
                ftpClient.disconnect();
                return -1;
            }

        } catch (SocketException e) {
            e.printStackTrace();
            logger.error("FTP的IP地址可能错误，请正确配置。");
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("FTP的端口错误,请正确配置。");
            return -1;
        }
        return 1;
    }


    //改变目录路径
    public boolean changeWorkingDirectory(String directory) throws Exception {
        boolean flag = true;
        try {
            //flag = getDefaultFtpClient().changeWorkingDirectory(directory);
            flag = ftpClient.changeWorkingDirectory(directory);
            if (flag) {
                System.out.println("进入文件夹" + directory + " 成功！");

            } else {
                System.out.println("进入文件夹" + directory + " 失败！");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new Exception("改变目录路径失败");
        }
        return flag;
    }

    //创建多层目录文件，如果有ftp服务器已存在该文件，则不创建，如果无，则创建

    public boolean createDirecroty(String remote) throws Exception {
        boolean success = true;
        String directory = remote + "/";
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(new String(directory))) {
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf("/", start);
            String path = "";
            String paths = "";
            while (true) {
                String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), "iso-8859-1");
                path = path + "/" + subDirectory;
                System.out.println("***************");
                System.out.println("subDirectory:"+subDirectory);
                System.out.println("path:"+path);
                //false表示当前文件夹下没有文件
                if (!existFile(path)) {
                    System.out.println("path:"+path+";existFile");
                    if (makeDirectory(subDirectory)) {
                        System.out.println("path:"+path+";makeDirectory");
                        changeWorkingDirectory(subDirectory);
                        System.out.println("path:"+path+";changeWorkingDirectory");
                    } else {
                        System.out.println("创建目录[" + subDirectory + "]失败");
                        return false;
                        //changeWorkingDirectory(subDirectory);
                    }
                } else {
                    System.out.println("path:"+path+";changeWorkingDirectory1");
                    changeWorkingDirectory(path);//subDirectory

                    System.out.println("path:"+path+";changeWorkingDirectory2");
                }
                String currentWorkkingDir=ftpClient.printWorkingDirectory();
                System.out.println("currentWorkkingDir="+currentWorkkingDir);
                paths = paths + "/" + subDirectory;
                start = end + 1;
                end = directory.indexOf("/", start);
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        }
        return success;
    }

    //判断ftp服务器文件是否存在
    public boolean existFile(String path) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        String currentWorkingDir=ftpClient.printWorkingDirectory();
        //FTPFile[] ftpFileArr = getDefaultFtpClient().listFiles(path);
       /* if (ftpFileArr.length > 0) {
            flag = true;
        }*/
        flag = ftpClient.changeWorkingDirectory(path);
        ftpClient.changeWorkingDirectory(currentWorkingDir);//返回之前的当前目录
        return flag;
    }

    //创建目录
    public boolean makeDirectory(String dir) {
        boolean flag = true;
        try {
//            flag = getDefaultFtpClient().makeDirectory(dir);
            flag = ftpClient.makeDirectory(dir);
            if (flag) {
                System.out.println("创建文件夹" + dir + " 成功！");

            } else {
                System.out.println("创建文件夹" + dir + " 失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }



    /** * 删除文件 *
     * @param pathName FTP服务器保存目录 *
     * @param fileName 要删除的文件名称 *
     * @return */
    public boolean deleteFile(String pathName, String fileName) throws Exception{
        boolean flag;

        try {
            System.out.println("开始删除文件");
            this.ftpClient=ftpClient;
            //切换FTP目录
            ftpClient.changeWorkingDirectory(pathName);
            ftpClient.dele(fileName);
            ftpClient.logout();
            flag = true;
            System.out.println("删除文件成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("删除文件失败");
        } finally {
            if(ftpClient.isConnected()){
                try{
                    ftpClient.disconnect();
                }catch(IOException e){
                    e.printStackTrace();
                    throw new Exception("删除文件失败");
                }
            }
        }
        return flag;
    }


    public boolean deleteDir(String remotePath){
        if(null==remotePath || "".equals(remotePath)){
            return false;
        }
        try {
            boolean ret=ftpClient.changeWorkingDirectory(remotePath);
            if(!ret){
                return false;
            }
            System.out.println("to delete dir "+remotePath);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile ftpFile : ftpFiles) {
                if(ftpFile.isDirectory()){
                    deleteDir(remotePath+"/"+ftpFile.getName());
                }else{
                    ret=ftpClient.deleteFile(ftpFile.getName());
                    System.out.println("delete file "+ftpFile.getName()+":ret="+ret);
                }
            }
            System.out.println("delete path "+remotePath);
            ftpClient.removeDirectory(remotePath);
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        finally {

        }
        return true;
    }

    public boolean downloadFile(String remotePath, String remoteFileName, String localPath, String localFileName) {
        FileOutputStream outputStream = null;
        File file = null;
        boolean isDownload = false;
        try {
            file = new File(localPath + localFileName);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            //切换文件路径
            //ftpClient.makeDirectory(remotePath);
            ftpClient.changeWorkingDirectory(remotePath);

            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile ftpFile : ftpFiles) {
                if (ftpFile.getName().equals(remoteFileName)) {
                    outputStream = new FileOutputStream(file);
                    isDownload = ftpClient.retrieveFile(ftpFile.getName(), outputStream);
                    break;
                }
            }
            if (isDownload) {
                logger.info(">>>>>>>>FTP-->downloadFile--文件下载成功！本地路径：" + file);
            }
            else {
                throw new RuntimeException("FTP-->downloadFile--文件下载失败！请检查！");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                outputStream.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isDownload;
    }


    public int downloadDir(String remotePath, String localPath) {
        FileOutputStream outputStream = null;
        File file = null;
        try {
            boolean isDownload = false;
            file = new File(localPath );
            if (file.exists()) {
                file.delete();
            }
            file.mkdirs();
            boolean hasRemoteDir=ftpClient.changeWorkingDirectory(remotePath);
            if(!hasRemoteDir){
                logger.info(">>>>>>>>FTP-->uploadFile--远程目录不存在!");
                return -1;
            }
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile ftpFile : ftpFiles) {
                File newLocalFile=new File(localPath,ftpFile.getName());
                if(newLocalFile.exists()){
                    newLocalFile.delete();
                }
                outputStream = new FileOutputStream(newLocalFile);
                isDownload = ftpClient.retrieveFile(ftpFile.getName(), outputStream);
                if (isDownload) {
                    logger.info(">>>>>>>>FTP-->downloadFile--文件下载成功！本地路径：" + file);
                }
                else {
                    logger.error("FTP-->downloadFile--文件下载失败！请检查！"+remotePath+" "+ftpFile.getName());
                    return -2;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        finally {
            try {
                outputStream.close();
                ftpClient.disconnect();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }


    public void uploadFile(String remotePath, String remoteFileName, String localPath, String localFileName) {
        FileInputStream inputStream = null;
        ftpClient.enterLocalPassiveMode();//设置成被动FTP模式
        try {
            boolean hasRemoteDir=ftpClient.changeWorkingDirectory(remotePath);
            if(!hasRemoteDir){
                logger.info(">>>>>>>>FTP-->uploadFile--远程目录不存在!");
                return ;
            }

            inputStream = new FileInputStream(new File(localPath + localFileName));
            //可上传多文件
            boolean isUpload = ftpClient.storeFile(remoteFileName, inputStream);
            if (isUpload) {
                logger.info(">>>>>>>>FTP-->uploadFile--文件上传成功!");
            } else {
                logger.info(">>>>>>>>FTP-->uploadFile--文件上传失败!");
                throw new RuntimeException("文件上传失败!");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(inputStream!=null){
                    inputStream.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     *  同步上传目录
     * @param remotePath 相对与ftp根目录，user 的home目录
     * @param localPath
     * @param style 0:非图片文件直接覆盖，对于图片文件（JPG，jpg），如果不存在或者大小不同则上传; 1 同步前删除目标目录中的文件
     * @return -1 localPath 不存在
     */
    public Result syncDir(String remotePath, String localPath, int style) {
        File localDir = null;
        FileInputStream inputStream=null;
        //String remotePath=remotePath2.replace("preset","preset2");

        try {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            localDir = new File(localPath );
            if (!localDir.exists() || !localDir.isDirectory()) {
                return ResultUtil.build(-1,"本地目录不存在"+localPath,null);
            }
            File[] localFiles=localDir.listFiles();
            boolean hasRemoteDir=ftpClient.changeWorkingDirectory(remotePath);
            FTPFile[] ftpFiles=null;
            if(!hasRemoteDir){
                boolean flag=createDirecroty(remotePath);
                if(!flag){
                    return ResultUtil.build(-1,"远程创建目录失败"+remotePath,null);
                }
            }else{
                ftpFiles = ftpClient.listFiles();
                if(style==1){
                    for(FTPFile ftpFile:ftpFiles){
                        ftpClient.deleteFile(remotePath+File.separator+ftpFile.getName());
                    }
                }
            }
            ftpFiles = ftpClient.listFiles();
            for(File localFile:localFiles){
                boolean needUpload=true;
                String localFileName=localFile.getName();
                FTPFile remoteFTPFile=null;
                //find 对应的ftp上的文件
                if(ftpFiles!=null ){
                    for(FTPFile ftpFile:ftpFiles){
                        if(ftpFile.getName().equals(localFileName)){
                            remoteFTPFile=ftpFile;
                            break;
                        }
                    }
                }
                if(remoteFTPFile!=null){
                   /* long localTime  = localFile.lastModified();
                    long remoteTime = remoteFTPFile.getTimestamp().getTimeInMillis();
                    if(localTime>remoteTime){
                        needUpload=true;
                    }*/
                  if (localFileName.endsWith("jpg") || localFileName.endsWith("JPG")){
                    if(localFile.length()==remoteFTPFile.getSize()){
                        logger.info("jpg is same ,ignore."+localFileName);
                        needUpload=false;
                    }
                  }
                }else{
                    needUpload=true;
                }
                if(needUpload){
                    inputStream = new FileInputStream(localFile);
                    //可上传多文件
                    boolean isUpload = ftpClient.storeFile(localFileName, inputStream);
                    if (!isUpload) {
                        logger.error("上传文件失败"+localFileName);
                        return  ResultUtil.build(-2,"上传文件失败"+localFileName,null);
                    }
                    inputStream.close();
                    logger.info("上传文件成功"+localFileName);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.build(-3,"上传文件失败,发生异常",null);
        }
        return  ResultUtil.build(1,"syncPresetDir 同步成功",null);
    }

    public void disConnect(){
        try {
            if(ftpClient!=null){
                ftpClient.disconnect();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
        String ftpUrl="ros:ros@192.168.100.94";
        if(ftpUrl==null){
            ftpUrl="ros:ros@localhost";
        }
        String host=ftpUrl.substring(ftpUrl.indexOf("@")+1);
        String userName=ftpUrl.substring(0,ftpUrl.indexOf(":"));
        String password=ftpUrl.substring(ftpUrl.indexOf(":")+1,ftpUrl.indexOf("@"));
        FtpUtil ftpUtil=new FtpUtil();
        int connected=ftpUtil.connect(host,userName,password,21);

        String ptzSetPath="/home/ros/temp/robot/source/src";
        try{
            ftpUtil.deleteDir(ptzSetPath);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("d");
    }
}
