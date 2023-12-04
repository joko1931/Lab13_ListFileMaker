import java.util.Scanner;
import java.util.ArrayList;
import java.io.IOException;
import javax.swing.JFileChooser;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.nio.file.Path;
import java.io.PrintWriter;


public class Main
{
    public static void main(String[] args)
    {
        ArrayList<String> itemList = new ArrayList<>();
        Scanner in = new Scanner(System.in);
        boolean done = false;
        String itemChoice = "";
        boolean needsToBeSaved = false;
        String fileName = "";

        do {
            itemChoice = displayMenu(in, itemList);
            switch (itemChoice)
            {
                case "A":
                    addItem(in, itemList);
                    needsToBeSaved = true;
                    break;
                case "D":
                    deleteItem(in, itemList);
                    needsToBeSaved = true;
                    break;
                case "V":
                    displayItem(itemList);
                    needsToBeSaved = true;
                    break;
                case "C":
                    clearList(itemList);
                    needsToBeSaved = true;
                    break;
                case "O":
                    openList(in, itemList, needsToBeSaved);
                    needsToBeSaved = true;
                    break;
                case "S":
                    saveList(itemList, fileName);
                    needsToBeSaved = false;
                    break;
                case "Q":
                    if (SafeInput.getYNConfirm(in, "Are you sure you want to exit"))
                    {
                        if(needsToBeSaved)
                        {
                            if (SafeInput.getYNConfirm(in, "You have a unsaved list, Save"))
                            {
                                saveList(itemList, fileName);
                            }
                        }
                        done = true;
                    }
                    break;
            }
        } while (!done);
    }

    public static void addItem(Scanner in, ArrayList itemList)
    {
        String itemAdd = SafeInput.getNonZeroLenString(in, "Type then press enter to add.");
        itemList.add(itemAdd);
    }

    public static void deleteItem(Scanner in, ArrayList itemList)
    {
        int itemDelete = SafeInput.getRangedInt(in, "Type list number then enter to delete an item.", 1, itemList.size());
        itemList.remove(itemDelete - 1);
    }

    public static void displayItem(ArrayList itemList)
    {
        for (int i = 0; i < itemList.size(); i++)
        {
            System.out.println(itemList.get(i));
        }
    }
    public static void clearList(ArrayList itemList)
    {
        itemList.clear();
    }

    private static String openList(Scanner in, ArrayList itemList, boolean needsToBeSaved)
    {
        if (needsToBeSaved)
        {
            String prompt = "Opening a new file will overwrite current list, Continue";
            boolean newList = SafeInput.getYNConfirm(in, prompt);
            if (!newList)
            {
                return "";
            }
        }
        clearList(itemList);
        Scanner newFile;
        JFileChooser fileOpen = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
        fileOpen.setFileFilter(filter);
        String line;
        Path file = new File(System.getProperty("user.dir")).toPath();
        file = file.resolve("src");
        fileOpen.setCurrentDirectory(file.toFile());


        try
        {
            if (fileOpen.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            {
                file = fileOpen.getSelectedFile().toPath();
                newFile = new Scanner(file);
                System.out.println("Opening File: " + file.getFileName());
                while (newFile.hasNextLine())
                {
                    line = newFile.nextLine();
                    itemList.add(line);
                }
                newFile.close();
            }
            else
            {
                System.out.println("No file selected. Reverting......");
            }
        }
        catch (IOException ex)
        {
            System.out.println("ERROR: IOException");
        }
        return file.toFile().toString();
    }

    public static void saveList (ArrayList itemList, String fileName)
    {
        PrintWriter outputFile;
        Path file = new File(System.getProperty("user.dir")).toPath();
        if (fileName.equals(""))
        {
            file = file.resolve("src\\list.txt");
        }
        else
        {
            file = file.resolve(fileName);
        }

        try
        {
            outputFile = new PrintWriter(file.toString());
            for (int i = 0; i < itemList.size(); i++)
            {
                outputFile.println(itemList.get(i));
            }
            outputFile.close();
            System.out.printf("File \"%s\" saved!\n", file.getFileName());
        }
        catch (IOException ex)
        {
            System.out.println("IOException Error");
        }
    }

    private static String displayMenu(Scanner in, ArrayList itemList)
    {
        if (itemList.isEmpty())
        {
            System.out.println("Empty List");
        }
        else
        {
            System.out.println("Current list:");
            for (int i = 0; i < itemList.size(); i++)
            {
                System.out.printf("    %d. %s\n", i + 1 , itemList.get(i));
            }
        }
        return SafeInput.getRegExString(in, "Type then enter to select:\n    A: Add\n    D: Delete\n    V: View\n    C: Clear\n    O: Open\n    S: Save\n    Q: Quit\n", "[AaCcDdOoSsVvQq]").toUpperCase();
    }
}