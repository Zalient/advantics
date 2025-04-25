package com.university.grp20.model;

import com.university.grp20.controller.LoginListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;

public class LoginService {
  private static final Logger logger = LogManager.getLogger(LoginService.class);

  private LoginListener loginListener;

  public String[] hashPassword(String plainTextPassword) {
    String[] stringArray = new String[2];
    try {
      SecureRandom random = new SecureRandom();
      byte[] salt = new byte[16];
      random.nextBytes(salt);

      KeySpec spec = new PBEKeySpec(plainTextPassword.toCharArray(), salt, 65536, 128);
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
      byte[] hash = factory.generateSecret(spec).getEncoded();
      Base64.Encoder encoder = Base64.getEncoder();

      String hashedPassword = encoder.encodeToString(hash);
      String saltString = encoder.encodeToString(salt);

      stringArray[0] = hashedPassword;
      stringArray[1] = saltString;
    } catch (NoSuchAlgorithmException e) {
      System.err.println("Hash algorithm could not be found.");
    } catch (InvalidKeySpecException e) {
      System.err.println("The key spec was invalid.");
    }
    return stringArray;
  }

  public boolean validatePassword(String enteredPassword, String storedHashedPassword, String storedSalt) {
    String enteredHashedPassword = null;
    try {
      Base64.Decoder decoder = Base64.getDecoder();

      byte[] salt = decoder.decode(storedSalt);

      KeySpec spec = new PBEKeySpec(enteredPassword.toCharArray(), salt, 65536, 128);
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
      byte[] hash = factory.generateSecret(spec).getEncoded();
      Base64.Encoder encoder = Base64.getEncoder();

      enteredHashedPassword = encoder.encodeToString(hash);

    } catch (NoSuchAlgorithmException e) {
      System.err.println("Hash algorithm could not be found.");
    } catch (InvalidKeySpecException e) {
      System.err.println("The key spec was invalid.");
    }

    if (enteredHashedPassword != null) {
      return enteredHashedPassword.equals(storedHashedPassword);
    } else {
      return false;
    }

  }

  public void login(String enteredUsername, String enteredPassword) {
    logger.info("Attempting login");

    Connection conn = null;

    try {
      conn = connectDatabase();

      PreparedStatement statement =
              conn.prepareStatement("SELECT username FROM users WHERE username == ?");
      statement.setString(1, enteredUsername);
      boolean userExists = statement.executeQuery().next();

      if (userExists) {
        statement = conn.prepareStatement("SELECT password, salt FROM users WHERE username == ?");
        statement.setString(1, enteredUsername);

        ResultSet resultSet = statement.executeQuery();
        String storedPassword = resultSet.getString("password");
        String storedSalt = resultSet.getString("salt");

        logger.info("Stored password for username " + enteredUsername + " is " + storedPassword);
        logger.info("Stored salt for username " + enteredUsername + " is " + storedSalt);


        if (validatePassword(enteredPassword, storedPassword, storedSalt)) {
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
          // Attempt to close the connection to the database
          conn.close();
          logger.info("Connection to database has been closed");
        } catch (SQLException e) {
          System.out.println(e.getMessage());
        }
      }
    }
  }

  public Connection connectDatabase() {
    Connection conn;
    try {
      // Connect to the database and commence setup
      conn = DriverManager.getConnection("jdbc:sqlite:./users.db");
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
      PreparedStatement statement =
              conn.prepareStatement("SELECT name FROM sqlite_master WHERE name = ?");
      statement.setString(1, "users");
      boolean tableExists = statement.executeQuery().next();

      // If table doesn't already exist create it and insert starting data
      if (!tableExists) {
        logger.info("users table did not already exist");

        // Create table to store the users in the database
        statement =
                conn.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS users ("
                                + "username TEXT PRIMARY KEY, "
                                + "password TEXT, "
                                + "salt TEXT, "
                                + "role TEXT);");
        statement.executeUpdate();

        String password;
        String[] hashedArray;
        String hashedPassword;
        String salt;

        // Insert example admin
        password = "1";
        hashedArray = hashPassword(password);
        hashedPassword = hashedArray[0];
        salt = hashedArray[1];
        statement =
                conn.prepareStatement(
                        "INSERT INTO users (username, password, salt, role) values (?, ?, ?, ?)");
        statement.setString(1, "Admin");
        statement.setString(2, hashedPassword);
        statement.setString(3, salt);
        statement.setString(4, "Admin");
        statement.executeUpdate();

        // Insert example editor
        password = "2";
        hashedArray = hashPassword(password);
        hashedPassword = hashedArray[0];
        salt = hashedArray[1];
        statement =
                conn.prepareStatement(
                        "INSERT INTO users (username, password, salt, role) values (?, ?, ?, ?)");
        statement.setString(1, "Editor");
        statement.setString(2, hashedPassword);
        statement.setString(3, salt);
        statement.setString(4, "Editor");
        statement.executeUpdate();

        // Insert example viewer
        password = "3";
        hashedArray = hashPassword(password);
        hashedPassword = hashedArray[0];
        salt = hashedArray[1];
        statement =
                conn.prepareStatement(
                        "INSERT INTO users (username, password, salt, role) values (?, ?, ?, ?)");
        statement.setString(1, "Viewer");
        statement.setString(2, hashedPassword);
        statement.setString(3, salt);
        statement.setString(4, "Viewer");
        statement.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean isDataLoaded() {
    Connection conn = null;
    boolean impressionTableExists, clickTableExists, serverTableExists;
    try {
      conn = connectDatabase();

      PreparedStatement statement =
              conn.prepareStatement("SELECT name FROM sqlite_master WHERE name = ?");
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
          // Attempt to close the connection to the database
          conn.close();
          logger.info("Connection to database has been closed");
        } catch (SQLException e) {
          System.out.println(e.getMessage());
        }
      }
    }

    logger.info(
            "isDataLoaded is returning "
                    + (impressionTableExists && clickTableExists && serverTableExists));

    return (impressionTableExists && clickTableExists && serverTableExists);
  }

  public void setOnLogin(LoginListener listener) {
    this.loginListener = listener;
  }

  public boolean doesUserExist(String enteredUsername) {
    Connection conn = null;
    boolean userExists;

    try {
      conn = connectDatabase();

      PreparedStatement statement =
              conn.prepareStatement("SELECT username FROM users WHERE username == ?");
      statement.setString(1, enteredUsername);
      userExists = statement.executeQuery().next();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (conn != null) {
        try {
          // Attempt to close the connection to the database
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
      String[] hashedArray = hashPassword(password);
      String hashedPassword = hashedArray[0];
      String salt = hashedArray[1];

      conn = connectDatabase();
      PreparedStatement statement =
              conn.prepareStatement(
                      "INSERT INTO users (username, password, salt, role) VALUES (?, ?, ?, ?)");
      statement.setString(1, username);
      statement.setString(2, hashedPassword);
      statement.setString(3, salt);
      statement.setString(4, role);

      statement.executeUpdate();

      success = true;
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    } finally {
      if (conn != null) {
        try {
          // Attempt to close the connection to the database
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
          // Attempt to close the connection to the database
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
      String[] hashedArray = hashPassword(newPassword);
      String hashedPassword = hashedArray[0];
      String salt = hashedArray[1];
      conn = connectDatabase();
      PreparedStatement statement =
              conn.prepareStatement("UPDATE users SET password = ?, salt = ? WHERE username = ?");
      statement.setString(1, hashedPassword);
      statement.setString(2, salt);
      statement.setString(3, username);

      statement.executeUpdate();

      success = true;

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    } finally {
      if (conn != null) {
        try {
          // Attempt to close the connection to the database
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

      PreparedStatement statement =
              conn.prepareStatement("UPDATE users SET role = ? WHERE username = ?");
      statement.setString(1, newRole);
      statement.setString(2, username);

      statement.executeUpdate();

      success = true;

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    } finally {
      if (conn != null) {
        try {
          // Attempt to close the connection to the database
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

      PreparedStatement statement =
              conn.prepareStatement("SELECT password FROM users WHERE username = ?");
      statement.setString(1, username);
      password = statement.executeQuery().getString("password");

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    } finally {
      if (conn != null) {
        try {
          // Attempt to close the connection to the database
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

      PreparedStatement statement =
              conn.prepareStatement("SELECT role FROM users WHERE username = ?");
      statement.setString(1, username);
      role = statement.executeQuery().getString("role");

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    } finally {
      if (conn != null) {
        try {
          // Attempt to close the connection to the database
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
