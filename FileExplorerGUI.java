	package myapp;
	import javax.swing.*;
	import java.awt.*;
	import java.awt.event.*;
	import java.io.File;
	import java.io.IOException;
	import java.awt.datatransfer.StringSelection;
	import java.awt.datatransfer.Clipboard;
	
	// inherits methods and properties from JFrame
	// add own components, layout managers, and event listeners

	
	public class FileExplorerGUI extends JFrame {
	    private JList<String> fileList;  // List to display file names only
	    private DefaultListModel<String> listModel;
	    //simpliy data handling --> ensure changes to data are automatically reflected in GUI	    
	    private LocalFileManager fileManager;
	    // composition of Local File Manager --> logic seprated from gui
	    private File currentDirectory;
	    private JLabel currentPathLabel;  // Label to display the current directory path
	    
	    //constructor
	    public FileExplorerGUI() {
	        fileManager = new LocalFileManager();
	        currentDirectory = new File(System.getProperty("user.home"));  // Start at users home directory
	
	        // Set GUI
	        setTitle("File Explorer");
	        setSize(800, 600);
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setLayout(new BorderLayout());
	
	        // label at the top to show the current directory path
	        currentPathLabel = new JLabel(currentDirectory.getAbsolutePath());
	        JPanel pathPanel = new JPanel();
	        pathPanel.setLayout(new FlowLayout(FlowLayout.CENTER));  
	        pathPanel.add(currentPathLabel);  
	        add(pathPanel, BorderLayout.NORTH); // add to top
	
	
	        // Create file list (now only displays file names)
	        listModel = new DefaultListModel<>();
	        fileList = new JList<>(listModel);
	        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        JScrollPane scrollPane = new JScrollPane(fileList);
	        add(scrollPane, BorderLayout.CENTER);
	
	        // populate list with current initial directory --> home
	        displayDirectoryContents(currentDirectory);
	
	        // double click listener 
	        fileList.addMouseListener(new MouseAdapter() {
	            public void mouseClicked(MouseEvent e) {
	                if (e.getClickCount() == 2) {
	                    String selectedFileName = fileList.getSelectedValue();
	                    if (selectedFileName != null) {
	                        File selectedFile = new File(currentDirectory, selectedFileName);
	                        if (selectedFile.isDirectory()) {
	                            // if clicked object is directory --> go in
	                            currentDirectory = selectedFile;
	                            displayDirectoryContents(currentDirectory);
	                        } else {
	                            // else if its a file open it 
	                            try {
	                            	// call method to open it
	                                fileManager.openFile(selectedFile);
	                            } catch (IOException ex) {
	                                JOptionPane.showMessageDialog(null, "Failed to open file: " + ex.getMessage());
	                            }
	                        }
	                    }
	                }
	            }
	        });
	
	        // Create left panel with buttons using BoxLayout
	        JPanel buttonPanel = new JPanel();
	        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));  // Vertical stack layout
	        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // Padding around panel
	        add(buttonPanel, BorderLayout.WEST);
	        
	        // use helper method to set buttons (implemented below)
	        JButton upButton = createStyledButton("Up");
	        JButton viewPropertiesButton = createStyledButton("View Properties");
	        JButton openButton = createStyledButton("Open");
	        JButton renameButton = createStyledButton("Rename");
	        JButton deleteButton = createStyledButton("Delete");
	        JButton moveButton = createStyledButton("Move");
	        JButton copyPathButton = createStyledButton("Copy Path");
	
	        // Add buttons to the panel 
	        buttonPanel.add(upButton);
	        buttonPanel.add(Box.createVerticalStrut(10)); // Add space between buttons
	        buttonPanel.add(viewPropertiesButton);
	        buttonPanel.add(Box.createVerticalStrut(10));  
	        buttonPanel.add(openButton);
	        buttonPanel.add(Box.createVerticalStrut(10));
	        buttonPanel.add(renameButton);
	        buttonPanel.add(Box.createVerticalStrut(10));
	        buttonPanel.add(deleteButton);
	        buttonPanel.add(Box.createVerticalStrut(10));
	        buttonPanel.add(moveButton);
	        buttonPanel.add(Box.createVerticalStrut(10));
	        buttonPanel.add(copyPathButton); 
	        
	        // action listeners for each button
	        upButton.addActionListener(e -> navigateUp());
	        viewPropertiesButton.addActionListener(e -> viewProperties());
	        openButton.addActionListener(e -> openSelectedFile());
	        renameButton.addActionListener(e -> renameSelectedFile());
	        deleteButton.addActionListener(e -> deleteSelectedFile());
	        moveButton.addActionListener(e -> moveSelectedFile());
	        copyPathButton.addActionListener(e -> copyPathToClipboard()); 
	    }
	
	    // Helper method to create styled buttons
	    private JButton createStyledButton(String text) {
	        JButton button = new JButton(text);
	        button.setAlignmentX(Component.CENTER_ALIGNMENT);  // Center horizontally
	        button.setPreferredSize(new Dimension(120, 30));  // Set size 
	        button.setMaximumSize(new Dimension(150, 30));  
	        button.setFont(new Font("Arial", Font.PLAIN, 14));  // Set font
	        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));  // Add padding in button
	        return button;
	    }
	
	    // display contents of directory  JList
	    private void displayDirectoryContents(File directory) {
	        listModel.clear();
	        currentPathLabel.setText(directory.getAbsolutePath());  
	        // Update top label with the current directory path
	        File[] files = directory.listFiles();
	        if (files != null) {
	            for (File file : files) {
	                listModel.addElement(file.getName());  // Only display file name
	            }
	        }
	    }
	
	    // Navigate Up to parent directory
	    private void navigateUp() {
	        if (currentDirectory.getParentFile() != null) {
	            currentDirectory = currentDirectory.getParentFile();
	            displayDirectoryContents(currentDirectory);
	        } else {
	            JOptionPane.showMessageDialog(this, "Already at the root directory");
	        }
	    }
	
	    // View properties of the selected file
	    private void viewProperties() {
	        String selectedFileName = fileList.getSelectedValue();
	        if (selectedFileName != null) {
	            File selectedFile = new File(currentDirectory, selectedFileName);
	            String properties = "File Name: " + selectedFile.getName() + "\n" +
	                                "Path: " + selectedFile.getAbsolutePath() + "\n" +
	                                "Size: " + selectedFile.length() + " bytes\n" +
	                                "Readable: " + selectedFile.canRead() + "\n" +
	                                "Writable: " + selectedFile.canWrite() + "\n" +
	                                "Executable: " + selectedFile.canExecute();
	            JOptionPane.showMessageDialog(this, properties);
	        } else {
	            JOptionPane.showMessageDialog(this, "No file selected");
	        }
	    }
	
	    // Open the selected file
	    private void openSelectedFile() {
	        String selectedFileName = fileList.getSelectedValue();
	        if (selectedFileName != null) {
	            File selectedFile = new File(currentDirectory, selectedFileName);
	            if (selectedFile.isFile()) {
	                try {
	                	// call file manager method 
	                    fileManager.openFile(selectedFile);
	                } catch (IOException e) {
	                    JOptionPane.showMessageDialog(this, "Failed to open file: " + e.getMessage());
	                }
	            }
	        }
	    }
	
	    // rename the selected file
	    private void renameSelectedFile() {
	        String selectedFileName = fileList.getSelectedValue();
	        if (selectedFileName != null) {
	            File selectedFile = new File(currentDirectory, selectedFileName);
	            String newName = JOptionPane.showInputDialog(this, "Enter new name:");
	            if (newName != null && !newName.trim().isEmpty()) {
	            	// call method from manager and recive status
	                if (fileManager.renameFile(selectedFile, newName)) {
	                    JOptionPane.showMessageDialog(this, "File renamed successfully");
	                    displayDirectoryContents(currentDirectory);
	                } else {
	                    JOptionPane.showMessageDialog(this, "Failed to rename file");
	                }
	            }
	        }
	    }
	
	    // delete the selected file
	    private void deleteSelectedFile() {
	        String selectedFileName = fileList.getSelectedValue();
	        if (selectedFileName != null) {
	            File selectedFile = new File(currentDirectory, selectedFileName);
	            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this file?", "Delete Confirmation", JOptionPane.YES_NO_OPTION);
	            if (confirm == JOptionPane.YES_OPTION) {
	            	// call method from manager and recive status
	                if (fileManager.deleteFile(selectedFile)) {
	                    JOptionPane.showMessageDialog(this, "File deleted successfully");
	                    displayDirectoryContents(currentDirectory);
	                } else {
	                    JOptionPane.showMessageDialog(this, "Failed to delete file");
	                }
	            }
	        }
	    }
	
	    //Move the selected file to a new directory
	    private void moveSelectedFile() {
	        String selectedFileName = fileList.getSelectedValue();
	        if (selectedFileName != null) {
	            File selectedFile = new File(currentDirectory, selectedFileName);
	            String destinationPath = JOptionPane.showInputDialog(this, "Enter destination directory path:");
	            if (destinationPath != null && !destinationPath.trim().isEmpty()) {
	            	if (fileManager.moveFile(selectedFile, destinationPath)) {
	            	    JOptionPane.showMessageDialog(this, "File moved successfully");
	            	    displayDirectoryContents(currentDirectory);
	            	} else {
	            	    JOptionPane.showMessageDialog(this, "Failed to move file");
	            	}
	            }
	        }
	    }
	
	    // Copy the current directory path to the clipboard
	    private void copyPathToClipboard() {
	        StringSelection stringSelection = new StringSelection(currentDirectory.getAbsolutePath());
	        // from java.awt.Toolkit imported above
	        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	        clipboard.setContents(stringSelection, null);
	        JOptionPane.showMessageDialog(this, "Path copied to clipboard");
	    }
	    
	    // java swing gui thread saftey and updating
	    public static void main(String[] args) {
	        SwingUtilities.invokeLater(() -> new FileExplorerGUI().setVisible(true));
	    }
	}
