package myapp;

import java.awt.Desktop;
//for opening files with their default applications
import java.io.File;
import java.io.IOException;

//inherits from abstract class File Manager
public class LocalFileManager extends FileManager {

    @Override
    public void openFile(File file) throws IOException {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (file.exists()) {
                desktop.open(file);  // open  with default app
            } else {
                throw new IOException("File not found");
            }
        }
    }

    @Override
    public boolean renameFile(File file, String newName) {
        File newFile = new File(file.getParent(), newName);
        return file.renameTo(newFile); // rename and retrun status
    }
        
    @Override
    public boolean moveFile(File file, String destinationPath) {
    	File destinationFile = new File(destinationPath, file.getName());
    	return file.renameTo(destinationFile);
    }
    
    
    // recursive / cascade delete
//    public boolean deleteFile(File file) {
//        return file.delete();
//    }
    
    @Override
    public boolean deleteFile(File f) {
        if (f.isDirectory()) {
            // List all files and directories within the directory
            File[] files = f.listFiles();
            if (files != null) {
                for (File file : files) {
                    // Recursively delete each file and directory
                	deleteFile(file);
                }
            }
        }
        //delete current directory / file
        return f.delete();
    }


}
