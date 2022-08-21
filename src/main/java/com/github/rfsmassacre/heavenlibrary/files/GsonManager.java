package com.github.rfsmassacre.heavenlibrary.files;

import com.github.rfsmassacre.heavenlibrary.interfaces.FileData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Handles storing and reading data via Gson.
 *
 * @param <T> Class type of object to be saved or read.
 */
@SuppressWarnings({"unused", "ResultOfMethodCallIgnored"})
public abstract class GsonManager<T> implements FileData<T>
{
    protected final JavaPlugin plugin;
    private final File folder;
    private final Class<T> clazz;

    /**
     * Constructor.
     *
     * @param plugin JavaPlugin handling this manager.
     * @param folderName Name of folder where everything will be held.
     * @param clazz Class type of the object being handled.
     */
    public GsonManager(JavaPlugin plugin, String folderName, Class<T> clazz)
    {
        this.plugin = plugin;
        this.folder = new File(plugin.getDataFolder() + "/" + folderName);
        folder.mkdir();
        this.clazz = clazz;
    }

    /**
     * Read object from file.
     *
     * Please note that all objects inside objects have to be serializable or else you will get an exception on reading.
     *
     * @param fileName Name of file.
     * @return Object from file.
     */
    @Override
    public T read(String fileName)
    {
        File file = getFile(fileName);

        try
        {
            if (file.exists())
            {
                BufferedReader reader = Files.newBufferedReader(file.toPath());
                return new Gson().fromJson(reader, clazz);
            }
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }

        return null;
    }

    /**
     * Read object from file asynchronously.
     *
     * Please note that all objects inside objects have to be serializable or else you will get an exception on reading.
     *
     * @param fileName Name of file.
     * @param task Task that accepts T as an argument.
     */
    public void read(String fileName, Consumer<T> task)
    {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> task.accept(read(fileName)));
    }

    /**
     * Write brand new blank file and copy contents.
     *
     * @param fileName Name of file.
     * @param overwrite Make new file over already existing file.
     */
    @Override
    public void copy(String fileName, boolean overwrite)
    {
        InputStream stream = plugin.getResource(fileName);
        InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(stream));
        Gson gson = new Gson();
        T t = gson.fromJson(reader, clazz);

        try
        {
            File file = getFile(fileName);
            if (overwrite)
            {
                file.delete();
            }

            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            gson.toJson(t, writer);
            writer.flush();
            writer.close();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Write brand new blank file and copy contents asynchronously.
     *
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
     * Write object to file.
     *
     * Please note that all objects inside objects have to be serializable or else you will get an exception on writing.
     *
     * @param fileName Name of file.
     * @param t Data or object to be updated into file.
     */
    @Override
    public void write(String fileName, T t)
    {
        File file = getFile(fileName);

        try
        {
            if (!file.exists())
            {
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(file);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(t, writer);
            writer.flush();
            writer.close();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Write object to file asynchronously.
     *
     * Please note that all objects inside objects have to be serializable or else you will get an exception on writing.
     *
     * @param fileName Name of file.
     * @param t Data or object to be updated into file.
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
     *
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
     * Retrieve file object from file name.
     *
     * @param fileName Name of file.
     * @return File object.
     */
    @Override
    public File getFile(String fileName)
    {
        return new File(folder.getPath() + "/" + fileName + (fileName.endsWith(".json") ? "" : ".json"));
    }

    /**
     * Return all objects.
     *
     * @return All objects.
     */
    public Set<T> all()
    {
        File[] files = folder.listFiles();
        if (files == null)
        {
            return Collections.emptySet();
        }

        Set<T> all = new HashSet<>();
        for (File file : files)
        {
            all.add(read(file.getName()));
        }

        return all;
    }
}
