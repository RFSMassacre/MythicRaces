package com.github.rfsmassacre.heavenlibrary.databases;

import com.github.rfsmassacre.heavenlibrary.interfaces.SQLData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Define common functions used in all SQL databases.
 *
 * @param <T> Object type to store or query.
 */
abstract class SQLDatabase<T> implements SQLData<T>
{
    protected Connection connection;

    /**
     * Disconnect from database.
     *
     * @throws SQLException Expected to throw if host is no longer up.
     */
    @Override
    public void close() throws SQLException
    {
        if (connection != null && !connection.isClosed())
        {
            connection.close();
        }
    }

    /**
     * Update database with series of statements.
     *
     * @param sqls Series of queries to update in database.
     */
    @Override
    public void update(String... sqls)
    {
        try
        {
            Statement statement = connection.createStatement();
            for (String sql : sqls)
            {
                statement.executeUpdate(sql);
            }
            statement.close();
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Retrieve object from database.
     *
     * @param sql SQL statement.
     * @return Object from database.
     */
    @Override
    public List<T> query(String sql)
    {
        List<T> t = new ArrayList<>();

        try
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery();
            t = load(result);
            result.close();
            statement.close();
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
        }

        return t;
    }

    /**
     * Create table in database.
     *
     * @param tableName Name of table.
     * @param columns Column names for each column in table.
     */
    @Override
    public void createTable(String tableName, String... columns)
    {
        if (columns.length > 0)
        {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + String.join(", ",
                    Arrays.asList(columns)) + ")";
            update(sql);
        }
    }
}
