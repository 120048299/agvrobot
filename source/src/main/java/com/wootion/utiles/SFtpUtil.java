package com.wootion.utiles;

import com.jcraft.jsch.*;
import com.wootion.agvrobot.utils.FileUtil;
import com.wootion.commons.Result;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Vector;

public class SFtpUtil {
    private static final Logger logger = LoggerFactory.getLogger(SFtpUtil.class);


    public Session session = null;
    public ChannelSftp sftp = null;

    public   int  connect(String ftpHost, String ftpUserName,
                                    String ftpPassword, int ftpPort)  {
        try {
            JSch jsch=new JSch();
            session = jsch.getSession(ftpUserName,ftpHost,ftpPort);
            if(ftpPassword!=null ){
                session.setPassword(ftpPassword);
            }
            Properties config=new Properties();
            config.put("StrictHostKeyChecking","no");
            session.setConfig(config);
            session.connect();
            Channel channel=session.openChannel("sftp");
            channel.connect();
            sftp=(ChannelSftp)channel;
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("FTP连接失败，请正确配置。");
            return -1;
        }
    }


    //改变目录路径
    public boolean changeWorkingDirectory(String directory) {
        try {
            sftp.cd(directory);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("改变目录路径失败");
        }
        return false;
    }
    //得到当前工作目录路径
    public String getPwd() throws Exception {
        try {
            return sftp.pwd();

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("读取当前位置失败");
        }
        return null;
    }

    /**
     * 创建多层目录文件，如果有ftp服务器已存在该文件，则不创建，如果无，则创建
     * 保持之前的pwd位置
     * @param remote
     * @return
     * @throws Exception
     */
    public boolean createDirs(String remote) throws Exception {
        boolean success = true;
        String directory = remote + "/";
        String pwd=getPwd();
        try{
            // 如果远程目录不存在，则递归创建远程服务器目录
            if (!directory.equalsIgnoreCase("/") && !existPath(new String(directory))) {
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
                    if (!existPath(path)) {
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
                    String currentWorkkingDir=sftp.pwd();
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
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            changeWorkingDirectory(pwd);
        }
        return success;
    }

    //判断ftp服务器目录是否存在
    public boolean existPath(String path) {
        String pwd=null;
        try{
            pwd=sftp.pwd();
            sftp.cd(path);
            return true;
        }catch (Exception e){
            //e.printStackTrace();
        }finally {
            try{
                if(pwd!=null){
                    sftp.cd(pwd);//返回之前的当前目录
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    //判断ftp服务器文件是否存在
    public boolean existFile(String path,String fileName) {
        String pwd=null;
        try{
            pwd=sftp.pwd();
            Vector vector=sftp.ls(path);
            for (Object obj:vector) {
                if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
                    ChannelSftp.LsEntry lsEntry = (com.jcraft.jsch.ChannelSftp.LsEntry) obj;
                    String itemFile = lsEntry.getFilename();
                    if (itemFile.equals(fileName)) {
                        return true;
                    }
                }
            }
            return false;
        }catch (Exception e){
            //e.printStackTrace();
        }finally {
            try{
                if(pwd!=null){
                    sftp.cd(pwd);//返回之前的当前目录
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    //创建子目录
    public boolean makeDirectory(String dir) {
        try {
            sftp.mkdir(dir);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("创建文件夹" + dir + " 失败！");
        }
        return false;
    }

    //在服务器上移动,全路径
    public boolean renameFile(String oldPath,String newPath) {
        String pwd=null;
        try{
            sftp.rename(oldPath,newPath);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    /** * 删除文件 *
     * @param pathName FTP服务器保存目录 *
     * @param fileName 要删除的文件名称 *
     * @return */
    public boolean deleteFile(String pathName, String fileName) {
        String pwd=null;
        try {
            pwd=sftp.pwd();
            System.out.println("开始删除文件");
            //切换FTP目录
            sftp.cd(pathName);
            sftp.rm(fileName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error ("删除文件失败pathName="+pathName+",fileName="+fileName);
        } finally {
            try{
                if(pwd!=null){
                    sftp.cd(pwd);//返回之前的当前目录
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 递归
     * @param remotePath
     * @return
     */
    public boolean deleteDir(String remotePath,boolean delPath){
        if(null==remotePath || "".equals(remotePath)){
            return false;
        }
        String pwd=null;
        try {
            pwd=sftp.pwd();
            sftp.cd(remotePath);
            System.out.println("to delete dir "+remotePath);
            Vector vector = sftp.ls(remotePath);
            for (Object obj:vector){
                if(obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry){
                    ChannelSftp.LsEntry lsEntry=(com.jcraft.jsch.ChannelSftp.LsEntry)obj;
                    SftpATTRS attrs =lsEntry.getAttrs();
                    String fileName=lsEntry.getFilename();
                    if(fileName.equals(".")||fileName.equals("..")){
                        continue;
                    }else if(attrs.isDir()){
                        boolean ret2=deleteDir(remotePath+"/"+fileName,true);
                        System.out.println("delete remotePath="+remotePath+",ret="+ret2);
                    }else{
                        boolean ret1=this.deleteFile(remotePath,fileName);
                        System.out.println("delete file remotePath="+remotePath+",fileName="+fileName+",ret="+ret1);
                    }
                    System.out.println(fileName);
                }
            }
            System.out.println("delete path "+remotePath);
            if(delPath){
                sftp.rmdir(remotePath);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        finally {
            try{
                if(pwd!=null){
                    sftp.cd(pwd);//返回之前的当前目录
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     *
     * @param remotePath
     * @param remoteFileName
     * @param localPath  ,should end with seperator
     * @param localFileName
     * @return
     */
    public boolean downloadFile(String remotePath, String remoteFileName, String localPath, String localFileName) {
        String pwd=null;
        try {
            FileUtil.createDir(localPath);
            pwd=sftp.pwd();
            //切换文件路径
            sftp.cd(remotePath);
            Vector vector = sftp.ls(remotePath);
            for (Object obj:vector) {
                if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
                    ChannelSftp.LsEntry lsEntry = (com.jcraft.jsch.ChannelSftp.LsEntry) obj;
                    SftpATTRS attrs = lsEntry.getAttrs();
                    String fileName = lsEntry.getFilename();
                    if (fileName.equals(remoteFileName)) {
                        sftp.get(fileName,localPath + localFileName);
                        File localFile =new File(localPath+localFileName);
                        if(!localFile.exists()){
                            logger.error("download file failed 本地路径"+localPath+localFileName);
                            return false;
                        }else{
                            logger.info("download file success 本地路径"+localPath+localFileName);
                            return true;
                        }
                    }
                }
            }
            logger.error("download file failed 本地路径"+localPath+localFileName);
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("download file failed 本地路径"+localPath+localFileName);
            return false;
        }
        finally {
            try {
                sftp.cd(pwd);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *   非递归，只下载子文件,全部
     * @param remotePath
     * @param localPath
     * @return
     */
    public int downloadDir(String remotePath, String localPath) {
        File file = null;
        try {
            boolean isDownload = false;
            file = new File(localPath );
            if (file.exists()) {
                file.delete();
            }
            file.mkdirs();
            sftp.cd(remotePath);
            //todo FTPFile[] ftpFiles = ftpClient.listFiles();
             Vector vector=sftp.ls(remotePath);

            for (Object obj:vector){
                if(obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry){
                    ChannelSftp.LsEntry lsEntry=(com.jcraft.jsch.ChannelSftp.LsEntry)obj;
                    SftpATTRS attrs =lsEntry.getAttrs();
                    String fileName=lsEntry.getFilename();
                    if(fileName.equals(".") || fileName.equals("..")){
                        continue;
                    }else if(attrs.isDir()){
                        continue;
                    }else{
                        sftp.get(fileName,localPath + fileName);
                        logger.info(">>>>>>>>robot FTP-->sever 文件下载成功！本地路径：" + localPath + fileName);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.info(">>>>>>>>robot FTP-->sever 文件下载失败。" + e.toString());
            return -1;
        }
        finally {

        }
        return 0;
    }


    /**
     * 目录不存在不上传
     * @param remotePath 路径末尾带/
     * @param remoteFileName
     * @param localPath
     * @param localFileName
     * @return
     */
    public boolean uploadFile(String remotePath, String remoteFileName, String localPath, String localFileName) {
        String pwd=null;
        try {
            pwd=sftp.pwd();
            boolean hasRemoteDir=this.changeWorkingDirectory(remotePath);
            if(!hasRemoteDir){
                logger.info(">>>>>>>>FTP-->uploadFile--远程目录不存在!");
                return false;
            }
            sftp.lcd(localPath);
            sftp.put(localFileName,remoteFileName,ChannelSftp.OVERWRITE);
            if(existFile(remotePath,remoteFileName)){
                return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(pwd!=null){
                    sftp.cd(pwd);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     *  同步上传 本地目录下的文件，没有更深的子目录
     * @param remotePath 相对与ftp根目录，user 的home目录
     * @param localPath
     * @param style 0:非图片文件直接覆盖，对于图片文件（JPG，jpg），如果不存在或者大小不同则上传; 1 同步前删除目标目录中的文件
     * @return -1 localPath 不存在
     */
    public Result syncPresetDir(String remotePath, String localPath, int style) {
       File localDir = null;
        FileInputStream inputStream=null;
        //String remotePath=remotePath2.replace("preset","preset2");

        try {
            sftp.lcd(localPath);
            localDir = new File(localPath );
            if (!localDir.exists() || !localDir.isDirectory()) {
                return ResultUtil.build(-1,"本地目录不存在"+localPath,null);
            }
            File[] localFiles=localDir.listFiles();
            boolean hasRemoteDir=existPath(remotePath);
            FTPFile[] ftpFiles=null;
            if(style==1 && hasRemoteDir){
                this.deleteDir(remotePath,false);
                hasRemoteDir=false;
            }
            if(!hasRemoteDir){
                boolean flag=createDirs(remotePath);
                if(!flag){
                    return ResultUtil.build(-1,"远程创建目录失败"+remotePath,null);
                }
            }
            sftp.cd(remotePath);
            Vector vector=sftp.ls(remotePath);
            for(File localFile:localFiles){
                boolean needUpload=true;
                String localFileName=localFile.getName();
                if(!needUploadToRobot(localFileName)){
                    continue;
                }
                String remoteFTPFile=null;
                //find 对应的ftp上的文件
                if(vector!=null ){
                    SftpATTRS attrs=this.findIn(vector,localFileName);
                    if(attrs!=null)  {
                       /* long localTime  = localFile.lastModified();
                        long remoteTime=attrs.getMTime();
                        if(localTime>remoteTime){
                            needUpload=true;
                            remoteFTPFile=localFileName;
                        }*/
                       if (localFileName.endsWith("jpg") || localFileName.endsWith("JPG")){
                           if(localFile.length()==attrs.getSize()){
                               logger.info("jpg is same name and same size,ignore."+localFileName);
                               needUpload=false;
                           }
                       }
                    }
                }

                if(needUpload){
                    sftp.put(localFileName,localFileName,ChannelSftp.OVERWRITE);
                    logger.info("upload file ok :"+localFileName);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.build(-1,"上传文件失败,发生异常",null);
        }
        return  ResultUtil.build(1,"上传文件,同步成功",null);
    }

    private boolean needUploadToRobot(String localFileName){
        if(localFileName.endsWith("ini")  || localFileName.endsWith("xml")   ){
            return true;
        }
        if(localFileName.equals("zoom0_infrared")  ||  localFileName.equals("zoom0_infrared.jpg")){
            return true;
        }
        if(localFileName.matches("^zoom[0-9].jpg$")){
            return true;
        }
        return false;
    }

    private SftpATTRS findIn(Vector vector,String findName){
        for (Object obj:vector) {
            if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
                ChannelSftp.LsEntry lsEntry = (com.jcraft.jsch.ChannelSftp.LsEntry) obj;
                SftpATTRS attrs = lsEntry.getAttrs();
                String fileName = lsEntry.getFilename();
                if(fileName.equals(findName)){
                    return attrs;
                }
            }
        }
        return null;
    }

    public void disConnect(){
        /*try {
            if(ftpClient!=null){
                ftpClient.disconnect();
            }
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }

    public static void main(String args[]){
        String ftpUrl="ros:ros@192.168.100.94";
        if(ftpUrl==null){
            ftpUrl="ros:ros@localhost";
        }
        String host=ftpUrl.substring(ftpUrl.indexOf("@")+1);
        String userName=ftpUrl.substring(0,ftpUrl.indexOf(":"));
        String password=ftpUrl.substring(ftpUrl.indexOf(":")+1,ftpUrl.indexOf("@"));
        SFtpUtil sFtpUtil=new SFtpUtil();
        int connected=sFtpUtil.connect(host,userName,password,22);
        if(connected!=1){
            return ;
        }

        sFtpUtil.renameFile("/home/ros/preset/ptz000015/20190801_100613/zoom0_20190801_110939_00.jpg",
                "/home/ros/preset/ptz000015/20190801_100613/last/zoom0_20190801_110939_00.jpg");
       /* String remotePath="/home/ros/temp/abc6/";
        String remoteFile="bbb.txt";
        String local="/home/ros/temp/abc6/";
        String localFile="2.txt";
        try{
            String s=sFtpUtil.getPwd();
            System.out.println(s);
            Result result=sFtpUtil.syncPresetDir(remotePath,local,0);
            System.out.println(result);
            s=sFtpUtil.getPwd();
            System.out.println(s);
        }catch (Exception e){
            e.printStackTrace();
        }*/
        System.out.println("end");
    }
}
