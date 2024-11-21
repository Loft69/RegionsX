package dev.thew.regions.handler.database;

import lombok.SneakyThrows;
import dev.thew.regions.Regions;
import org.bukkit.Bukkit;

import java.sql.*;

public abstract class Database {

    private Connection connection;
    private final String url;

    @SneakyThrows
    protected Database(String url){

        this.url = url;

        connect();
        checkTables();

        Bukkit.getScheduler().runTaskTimerAsynchronously(Regions.getInstance(), this::reconnect, 20 * 60, 20 * 60);
    }

    @SneakyThrows
    private void connect() {
        connection = DriverManager.getConnection(url);
    }

    @SneakyThrows
    private void reconnect() {
        if (connection == null || connection.isClosed()) connect();
    }

    public abstract void checkTables();

    public void push(String sql, boolean async, Object... values) {

        boolean isSync = Bukkit.isPrimaryThread();

        if (!isSync || !async) pushInThisThread(sql, values);

        else
            Bukkit.getScheduler().runTaskAsynchronously(Regions.getInstance(), () -> pushInThisThread(sql, values));
    }

    @SneakyThrows
    private void pushInThisThread(String sql, Object... values) {
        PreparedStatement preparedStatement = prepareStatement(sql, values);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    @SneakyThrows
    public ResultSet pushWithReturn(String sql, Object... values) {
        PreparedStatement preparedStatement = prepareStatement(sql, values);
        return preparedStatement.executeQuery();
    }

    @SneakyThrows
    private PreparedStatement prepareStatement(String sql, Object... values) {

        if (connection == null || connection.isClosed()) connect();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        for (int index = 0; index < values.length; index++)
            setValueInPreparedStatement(preparedStatement, index + 1, values[index]);

        return preparedStatement;
    }

    @SneakyThrows
    private void setValueInPreparedStatement(PreparedStatement preparedStatement, int index, Object value) {
        if (value instanceof String v1) preparedStatement.setString(index, v1);
        else if (value instanceof Integer v1) preparedStatement.setInt(index, v1);
        else if (value instanceof Double v1) preparedStatement.setDouble(index, v1);
        else if (value instanceof Float v1) preparedStatement.setFloat(index, v1);
        else if (value instanceof Boolean v1) preparedStatement.setBoolean(index, v1);
        else if (value instanceof Byte v1) preparedStatement.setByte(index, v1);
        else if (value instanceof Short v1) preparedStatement.setShort(index, v1);
        else if (value instanceof Long v1) preparedStatement.setLong(index, v1);
        else if (value instanceof byte[] v1) preparedStatement.setBytes(index, v1);
        else preparedStatement.setString(index, value.toString());
    }

    @SneakyThrows
    public void close() {
        connection.close();
    }
}