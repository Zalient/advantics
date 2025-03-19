package com.university.grp20.model;

import com.university.grp20.controller.ProgressBarListener;
import com.university.grp20.controller.LoginListener;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;

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
    Connection conn = null;
    boolean impressionTableExists = false, clickTableExists = false, serverTableExists = false;
    try {
      conn = connectDatabase();

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

    logger.info("isDataLoaded is returning " + (impressionTableExists && clickTableExists && serverTableExists));

    return (impressionTableExists && clickTableExists && serverTableExists);
  }

  public void setOnLogin(LoginListener listener) {
    this.loginListener = listener;
  }

  public boolean doesUserExist(String enteredUsername) {
    Connection conn = null;
    boolean userExists = false;

    try {
      conn = connectDatabase();

      PreparedStatement statement = conn.prepareStatement("SELECT username FROM users WHERE username == ?");
      statement.setString(1, enteredUsername);
      userExists = statement.executeQuery().next();
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
    return userExists;

  }

  public boolean addUser(String username, String password, String role) {
    logger.info("Adding user: " + username + ", " + password + ", " + role);

    boolean success = false;
    Connection conn = null;

    try {
      conn = connectDatabase();
      PreparedStatement statement = conn.prepareStatement("INSERT INTO users (username, password, name, role) VALUES (?, ?, ?, ?)");
      statement.setString(1, username);
      statement.setString(2, password);
      statement.setString(3, "");
      statement.setString(4, role);

      statement.executeUpdate();

      success = true;
    } catch (SQLException e) {
      System.out.println(e.getMessage());
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

    return success;
  }

  public ArrayList<String> getAllUsers() {
    ArrayList<String> userList = new ArrayList<>();

    Connection conn = null;

    try {
      conn = connectDatabase();

      PreparedStatement statement = conn.prepareStatement("SELECT username FROM users");
      ResultSet returnedResult = statement.executeQuery();

      while (returnedResult.next()) {
        userList.add(returnedResult.getString("username"));
      }

    } catch (SQLException e) {
      System.out.println(e.getMessage());
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

    return userList;
  }

  public boolean changeUserPassword(String username, String newPassword) {
    boolean success = false;

    Connection conn = null;

    try {
      conn = connectDatabase();

      PreparedStatement statement = conn.prepareStatement("UPDATE users SET password = ? WHERE username = ?");
      statement.setString(1, newPassword);
      statement.setString(2, username);

      statement.executeUpdate();

      success = true;

    } catch (SQLException e) {
      System.out.println(e.getMessage());
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

    return success;
  }

  public boolean changeUserRole(String username, String newRole) {
    boolean success = false;

    Connection conn = null;

    try {
      conn = connectDatabase();

      PreparedStatement statement = conn.prepareStatement("UPDATE users SET role = ? WHERE username = ?");
      statement.setString(1, newRole);
      statement.setString(2, username);

      statement.executeUpdate();

      success = true;

    } catch (SQLException e) {
      System.out.println(e.getMessage());
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

    return success;
  }

  public String getUserPassword(String username) {

    Connection conn = null;
    String password = "";

    try {
      conn = connectDatabase();

      PreparedStatement statement = conn.prepareStatement("SELECT password FROM users WHERE username = ?");
      statement.setString(1, username);
      password = statement.executeQuery().getString("password");


    } catch (SQLException e) {
      System.out.println(e.getMessage());
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

    return password;
  }

  public String getUserRole(String username) {

    Connection conn = null;
    String role = "";

    try {
      conn = connectDatabase();

      PreparedStatement statement = conn.prepareStatement("SELECT role FROM users WHERE username = ?");
      statement.setString(1, username);
      role = statement.executeQuery().getString("role");


    } catch (SQLException e) {
      System.out.println(e.getMessage());
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

    return role;
  }


}
