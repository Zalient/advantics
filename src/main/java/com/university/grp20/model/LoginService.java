package com.university.grp20.model;

import com.university.grp20.controller.FileProgressBarListener;
import com.university.grp20.controller.LoginListener;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class LoginService {
  private static final Logger logger = LogManager.getLogger(LoginService.class);

  private LoginListener loginListener;

  public void login(String enteredUsername, String enteredPassword) {
    logger.info("Attempting login");

    Connection conn = null;

    try {
      conn = connectDatabase();

      PreparedStatement statement = conn.prepareStatement("SELECT username FROM users WHERE username == ?");
      statement.setString(1, enteredUsername);
      boolean userExists = statement.executeQuery().next();

      if (userExists) {
        statement = conn.prepareStatement("SELECT password FROM users WHERE username == ?");
        statement.setString(1, enteredUsername);

        String storedPassword = statement.executeQuery().getString("password");

        logger.info("Stored password for username " + enteredUsername + " is " + storedPassword);

        if (enteredPassword.equals(storedPassword)) {
          User.setUsername(enteredUsername);
          User.setPassword(enteredPassword);
          // Get the role of the user
          statement = conn.prepareStatement("SELECT role FROM users WHERE username == ?");
          statement.setString(1, enteredUsername);
          User.setRole(statement.executeQuery().getString("role")); // May need to change this

          loginListener.loginMessage("Valid");
        } else {
          loginListener.loginMessage("Invalid");
        }
      } else {
        loginListener.loginMessage("Missing");
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (conn != null) {
        try {
          //Attempt to close the connection to the database
          conn.close();
          logger.info("Connection to database has been closed");
        } catch (SQLException e) {
          System.out.println(e.getMessage());
        }
      }

    }
  }

  public Connection connectDatabase() {
    Connection conn = null;
    try {
      // Connect to the database and commence setup
      conn = DriverManager.getConnection("jdbc:sqlite:./statsDatabase.db");
      setupDatabase(conn);
      logger.info("Database connected");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return conn;
  }

  public void setupDatabase(Connection conn) {
    try {
      // Determine is users table already exists
      PreparedStatement statement = conn.prepareStatement("SELECT name FROM sqlite_master WHERE name = ?");
      statement.setString(1, "users");
      boolean tableExists = statement.executeQuery().next();

      // If table doesn't already exist create it and insert starting data
      if (!tableExists) {
        logger.info("users table did not already exist");

        // Create table to store the users in the database
        statement = conn.prepareStatement("CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT, " +
                "name TEXT, " +
                "role TEXT);");
        statement.executeUpdate();

        // Insert example admin
        statement = conn.prepareStatement("INSERT INTO users (username, password, name, role) values (?, ?, ?, ?)");
        statement.setString(1, "Admin");
        statement.setString(2, "1");
        statement.setString(3, "Mr Admin");
        statement.setString(4, "Admin");
        statement.executeUpdate();

        // Insert example editor
        statement = conn.prepareStatement("INSERT INTO users (username, password, name, role) values (?, ?, ?, ?)");
        statement.setString(1, "Editor");
        statement.setString(2, "2");
        statement.setString(3, "Mr Editor");
        statement.setString(4, "Editor");
        statement.executeUpdate();

        // Insert example viewer
        statement = conn.prepareStatement("INSERT INTO users (username, password, name, role) values (?, ?, ?, ?)");
        statement.setString(1, "Viewer");
        statement.setString(2, "3");
        statement.setString(3, "Mr Viewer");
        statement.setString(4, "Viewer");
        statement.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

  }

  public boolean isDataLoaded() {
    boolean impressionTableExists = false, clickTableExists = false, serverTableExists = false;
    try {
      Connection conn = DriverManager.getConnection("jdbc:sqlite:./statsDatabase.db");

      PreparedStatement statement = conn.prepareStatement("SELECT name FROM sqlite_master WHERE name = ?");
      statement.setString(1, "impressionLog");
      impressionTableExists = statement.executeQuery().next();

      statement = conn.prepareStatement("SELECT name FROM sqlite_master WHERE name = ?");
      statement.setString(1, "clickLog");
      clickTableExists = statement.executeQuery().next();

      statement = conn.prepareStatement("SELECT name FROM sqlite_master WHERE name = ?");
      statement.setString(1, "serverLog");
      serverTableExists = statement.executeQuery().next();

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    logger.info("isDataLoaded is returning " + (impressionTableExists && clickTableExists && serverTableExists));

    return (impressionTableExists && clickTableExists && serverTableExists);
  }

  public void setOnLogin(LoginListener listener) {
    this.loginListener = listener;
  }


}