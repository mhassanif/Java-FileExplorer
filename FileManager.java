package myapp;
import java.io.File;
import java.io.IOException;

// abstract / generic file manager for different types
public abstract class FileManager {
    public abstract void openFile(File file) throws IOException;
    public abstract boolean renameFile(File file, String newName);
    public abstract boolean deleteFile(File file);
    public abstract boolean moveFile(File file, String destinationPath);
}
