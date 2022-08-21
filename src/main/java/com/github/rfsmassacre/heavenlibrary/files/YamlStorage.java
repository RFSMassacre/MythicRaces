package com.github.rfsmassacre.heavenlibrary.files;


import com.github.rfsmassacre.heavenlibrary.interfaces.FileData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "ResultOfMethodCallIgnored"})
public abstract class YamlStorage<T> implements FileData<T>
{
    protected JavaPlugin plugin;
    protected File folder;
    protected String folderName;

    /**
     * Constructor for YamlManager.
     * @param plugin Plugin where files will be for.
     * @param folderName Name of folder.
     */
    public YamlStorage(JavaPlugin plugin, String folderName)
    {
        this.plugin = plugin;
        this.folderName = folderName;
        this.folder = new File(plugin.getDataFolder().getPath() + "/" + folderName);
    }

    /**
     * Read from file and convert into whatever data or object needed.
     * @param fileName Name of file.
     * @return Data or object read from the file.
     */
    @Override
    public T read(String fileName)
    {
        return load(YamlConfiguration.loadConfiguration(getFile(fileName)));
    }

    /**
     * Read from file and convert into whatever data or object needed asynchronously.
     * @param fileName Name of file.
     * @param task Task that accepts T as an argument.
     */
    public void read(String fileName, Consumer<T> task)
    {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> task.accept(read(fileName)));
    }

    /**
     * Do nothing. This manager does not copy.
     */
    @Deprecated
    @Override
    public void copy(String fileName, boolean overwrite)
    {
        //Do nothing. There is no default storage.
    }

    /**
     * Write data of object into the file.
     * @param fileName Name of file.
     * @param t Generic type.
     */
    @Override
    public void write(String fileName, T t)
    {
        try
        {
            save(t).save(getFile(fileName));
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Write data of object into the file asynchronously.
     * @param fileName Name of file.
     * @param t Generic type.
     * @param async Do this asynchronously.
     */
    public void write(String fileName, T t, boolean async)
    {
        if (async)
        {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> write(fileName, t));
        }
        else
        {
            write(fileName, t);
        }
    }

    /**
     * Delete specified file.
     * @param fileName Name of file.
     */
    @Override
    public void delete(String fileName)
    {
        File file = getFile(fileName);
        if (file.exists())
        {
            file.delete();
        }
    }

    /**
     * Delete specified file.
     * @param fileName Name of file.
     */
    public void delete(String fileName, boolean async)
    {
        if (async)
        {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> delete(fileName));
        }
        else
        {
            delete(fileName);
        }
    }

    /**
     * Retrieve file object from file name.
     * @param fileName Name of file.
     * @return File object.
     */
    @Override
    public File getFile(String fileName)
    {
        return new File(folder.getPath() + "/" + fileName + (fileName.endsWith(".yml") ? "" : ".yml"));
    }

    public File[] getFiles()
    {
        return folder.listFiles();
    }

    public abstract T load(YamlConfiguration configuration);

    public abstract YamlConfiguration save(T t);
}
