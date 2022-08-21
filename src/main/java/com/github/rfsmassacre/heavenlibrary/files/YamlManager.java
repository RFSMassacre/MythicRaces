package com.github.rfsmassacre.heavenlibrary.files;

import com.github.rfsmassacre.heavenlibrary.interfaces.FileData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

/**
 * Spigot sided manager for YML files.
 */
@SuppressWarnings({"unused", "ResultOfMethodCallIgnored"})
public abstract class YamlManager implements FileData<YamlConfiguration>
{
    protected JavaPlugin plugin;
    protected File folder;
    protected String folderName;
    protected YamlConfiguration yaml;
    protected YamlConfiguration defaultYaml;

    /**
     * Constructor for YamlManager.
     * @param plugin Plugin where files will be for.
     * @param folderName Name of folder.
     * @param fileName Name of file.
     */
    public YamlManager(JavaPlugin plugin, String folderName, String fileName)
    {
        this.plugin = plugin;
        this.folderName = folderName;
        this.folder = new File(plugin.getDataFolder().getPath() + "/" + folderName);
        this.yaml = read(fileName);

        InputStream stream = plugin.getResource(fileName);
        if (stream != null)
        {
            InputStreamReader reader = new InputStreamReader(stream);
            this.defaultYaml = YamlConfiguration.loadConfiguration(reader);
        }

        if (!getFile(fileName).exists())
        {
            write(fileName, defaultYaml);
        }

        this.yaml = read(fileName);
    }

    /**
     * Read from file and convert into YamlConfiguration.
     * @param fileName Name of file.
     * @return Data or object read from the file.
     */
    @Override
    public YamlConfiguration read(String fileName)
    {
        return YamlConfiguration.loadConfiguration(getFile(fileName));
    }

    /**
     * Read from file and convert into YamlConfiguration asynchronously.
     * @param fileName Name of file.
     * @param task Task that accepts YamlConfiguration as an argument.
     */
    public void read(String fileName, Consumer<YamlConfiguration> task)
    {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> task.accept(read(fileName)));
    }

    /**
     * Copy a new file with format.
     * @param fileName Name of file.
     * @param overwrite Make new file over already existing file.
     */
    @Override
    public void copy(String fileName, boolean overwrite)
    {
        InputStream stream = plugin.getResource(fileName);
        if (stream != null)
        {
            InputStreamReader reader = new InputStreamReader(stream);
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(reader);
            File file = getFile(fileName);
            try
            {
                if (overwrite)
                {
                    configuration.save(file);
                }
                else if (!file.exists())
                {
                    configuration.save(file);
                }
            }
            catch (IOException exception)
            {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Copy a new file with format asynchronously.
     * @param fileName Name of file.
     * @param overwrite Make new file over already existing file.
     * @param async Do this asynchronously.
     */
    public void copy(String fileName, boolean overwrite, boolean async)
    {
        if (async)
        {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> copy(fileName, overwrite));
        }
        else
        {
            copy(fileName, overwrite);
        }
    }

    /**
     * Write data of object into the file.
     * @param fileName Name of file.
     * @param configuration Configuration file.
     */
    @Override
    public void write(String fileName, YamlConfiguration configuration)
    {
        try
        {
            configuration.save(getFile(fileName));
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Write data of object into the file asynchronously.
     * @param fileName Name of file.
     * @param configuration Configuration file.
     * @param async Do this asynchronously.
     */
    public void write(String fileName, YamlConfiguration configuration, boolean async)
    {
        if (async)
        {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> write(fileName, configuration));
        }
        else
        {
            write(fileName, configuration);
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
     * Delete specified file asynchronously.
     * @param fileName Name of file.
     * @param async Do this asynchronously.
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
}
