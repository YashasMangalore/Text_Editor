import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;

public class TextEditor implements ActionListener
{
    JFrame frame;
    JMenuBar menuBar;
    JMenu file, edit;
    JMenuItem newFile, saveFile, openFile, close;
    JMenuItem cut, copy, paste, selectAll, undo, redo;
    JTextArea textArea;
    UndoManager undoManager; // UndoManager for managing undo and redo operations
    StringBuilder text = new StringBuilder();
    TextEditor()
    {
        //Initialize a frame and menu bar
        frame=new JFrame();
        menuBar=new JMenuBar();
        //Initialize  menu bar and Textarea
        file=new JMenu("File");
        edit=new JMenu("Edit");
        textArea=new JTextArea();
        textArea.setBorder(new EmptyBorder(5, 5, 5, 5)); // Add padding around the text area
        // Set font
        Font font = new Font("Consolas", Font.ROMAN_BASELINE, 17);
        textArea.setFont(font);
        //Initialize file menu and edit menu items
        newFile=new JMenuItem("New window   ");
        newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        openFile=new JMenuItem("Open");
        openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        saveFile=new JMenuItem("Save as");
        saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        close=new JMenuItem("Close");
        close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
        cut=new JMenuItem("Cut");
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        copy=new JMenuItem("Copy");
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        paste=new JMenuItem("Paste");
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        selectAll=new JMenuItem("Select All ");
        selectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        undo = new JMenuItem("Undo");
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        redo = new JMenuItem("Redo");
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));
        //Add action listener
        newFile.addActionListener(this);
        openFile.addActionListener(this);
        saveFile.addActionListener(this);
        close.addActionListener(this);
        cut.addActionListener(this);
        copy.addActionListener(this);
        paste.addActionListener(this);
        selectAll.addActionListener(this);
        undo.addActionListener(this);
        redo.addActionListener(this);

        //add menu items to file and edit menu
        file.add(newFile);
        file.add(openFile);
        file.add(saveFile);
        file.add(close);
        edit.add(cut);
        edit.add(copy);
        edit.add(paste);
        edit.add(selectAll);
        edit.add(undo);
        edit.add(redo);
        //add file and edit to menu bar
        menuBar.add(file);
        menuBar.add(edit);

        frame.setJMenuBar(menuBar);
        JPanel panel=new JPanel();
        panel.setBorder(new EmptyBorder(5,5,5,5));
        panel.setLayout(new BorderLayout(0,0));
        panel.add(textArea,BorderLayout.CENTER);
        // Wrap the text area in a scroll pane
        JScrollPane scrollPane = new JScrollPane(textArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane);
        frame.add(panel);
//set dimensions
        frame.setSize(800, 500);// Set the initial size of the frame explicitly
        frame.setTitle("Text Editor");
        frame.setLocationRelativeTo(null); // Centers the frame on the screen
        frame.setVisible(true);
        // Initialize UndoManager
        undoManager = new UndoManager();
        // Add UndoManager to the JTextArea
        textArea.getDocument().addUndoableEditListener(undoManager);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        if(actionEvent.getSource()==cut)
        {//performs cut
//            textArea.cut();
            // Clear the StringBuilder before appending new text
            text.delete(0, text.length());
            text.append(textArea.getSelectedText());
            textArea.replaceRange("", textArea.getSelectionStart(), textArea.getSelectionEnd());
        }
        else if(actionEvent.getSource()==copy)
        {//textArea.copy();
            // Clear the StringBuilder before appending new text
            text.delete(0, text.length());
            text.append(textArea.getSelectedText());
        }
        else if(actionEvent.getSource()==paste)
        {//textArea.paste();
            textArea.insert(text.toString(),textArea.getCaretPosition());
        }
        else if(actionEvent.getSource()==selectAll)
        {//textArea.selectAll();
            int textLength = textArea.getText().length();
            // Set the selection range from the start to the end of the text
            textArea.setSelectionStart(0);
            textArea.setSelectionEnd(textLength);
        }
        else if(actionEvent.getSource()==close)
        {
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to close?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION)
            {
                // Clear clipboard content
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(""), null);
                System.exit(0);
            }
        }
        else if(actionEvent.getSource()==openFile)
        {
            // Create a file filter to accept only .txt files
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
            JFileChooser fileChooser=new JFileChooser("C:");
            fileChooser.setFileFilter(filter);
            int chooseOption = fileChooser.showOpenDialog(null);
            if(chooseOption==JFileChooser.APPROVE_OPTION)
            {
                File file= fileChooser.getSelectedFile();//get selected file
                String filePath=file.getPath();
                try
                {//initialize file reader
                    FileReader fileReader=new FileReader(filePath);
                    //initialize buffer reader
                    BufferedReader bufferedReader=new BufferedReader(fileReader);
                    String intermediate, output="";

                    //read file line by line
                    while((intermediate=bufferedReader.readLine()) != null)
                    {
                        output += (intermediate + "\n");
                    }
                    textArea.setText(output);
                    // Scroll to the top
                    textArea.setCaretPosition(0);
                    // Close readers
                    bufferedReader.close();
                    fileReader.close();
                }
                catch (IOException i)
                {
                    i.printStackTrace();
                }
            }
        }
        else if(actionEvent.getSource()==saveFile)
        {
            JFileChooser fileChooser=new JFileChooser("C:");
            int chooseOption =fileChooser.showSaveDialog(null);
            if(chooseOption==JFileChooser.APPROVE_OPTION)
            {
                //create a new file with chosen directory path and file name
                File file=new File(fileChooser.getSelectedFile().getAbsoluteFile()+".txt");
                try
                {
                    FileWriter fileWriter=new FileWriter(file);
                    BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
                    textArea.write(bufferedWriter);
                    bufferedWriter.close();
                    fileWriter.close();
                }
                catch(IOException i)
                {
                    i.printStackTrace();
                }
            }
        }
        else if(actionEvent.getSource()==newFile)
        {
            TextEditor newtextEditor=new TextEditor();
        }
        else if (actionEvent.getSource() == undo) {
            if (undoManager.canUndo()) {
                undoManager.undo();
            } else {
                JOptionPane.showMessageDialog(null, "Nothing to undo.");
            }
        }
        else if (actionEvent.getSource() == redo) {
            if (undoManager.canRedo()) {
                undoManager.redo();
            } else {
                JOptionPane.showMessageDialog(null, "Nothing to redo.");
            }
        }
    }

    public static void main(String[] args)
    {
        TextEditor textEditor=new TextEditor();
    }
}