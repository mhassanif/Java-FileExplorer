package myapp;
import javax.swing.SwingUtilities;
//SwingUtilities manage GUI updates on the Event Dispatch Thread 

public class Main {
    public static void main(String[] args) {
        // lanuched using utiltilities for thread safety in GUI creation
        SwingUtilities.invokeLater(() -> {
            FileExplorerGUI explorer = new FileExplorerGUI();  
            explorer.setVisible(true);  
        });
    }
}
