package com.github.rfsmassacre.heavenlibrary.files;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

/**
 * Handles storing and reading text via TXT files.
 */
@SuppressWarnings({"unused", "ResultOfMethodCallIgnored"})
public class TextManager
{
    private final JavaPlugin plugin;
    private final HashMap<String, List<String>> textCache;
    private final String folderName;

    /**
     * Constructor.
     *
     * @param plugin JavaPlugin handling this manager.
     * @param folderName Name of folder where everything will be held.
     */
    public TextManager(JavaPlugin plugin, String folderName)
    {
        this.plugin = plugin;
        this.textCache = new HashMap<>();
        this.folderName = folderName;

        //Create folder if needed
        File folder = new File(plugin.getDataFolder() + "/" + folderName);
        if (!folder.exists())
        {
            folder.mkdir();
        }
    }

    /**
     * Retrieve lines from text file.
     *
     * @param fileName Name of file.
     * @return Lines from text file.
     */
    public List<String> getTextFile(String fileName)
    {
        String fullName = folderName + "/" + fileName;
        if (!textCache.containsKey(fullName))
        {
            cacheTextFile(fileName);
        }

        return textCache.get(fullName);
    }

    /**
     * Clear cache of texts.
     */
    public void clearCacheFiles()
    {
        textCache.clear();
    }

    /**
     * Cache lines of text into memory.
     * @param fileName Name of file.
     */
    public void cacheTextFile(String fileName)
    {
        String fullName = folderName + "/" + fileName;
        try
        {
            File file = new File(plugin.getDataFolder() + "/" + fullName);
            if (!file.exists())
            {
                file.createNewFile();
                plugin.saveResource(folderName + "/" + fileName, true);
            }
            textCache.put(fullName, Files.readAllLines(file.toPath()));
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }
}
