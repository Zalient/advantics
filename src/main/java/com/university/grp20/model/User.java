package com.university.grp20.model;

public class User {
  private static String username;
  // I'm not sure if the name is necessary at the moment
  private static String name;
  private static String password;
  private static String role;

  public static void logOut() {
    username = null;
    name = null;
    password = null;
    role = null;
  }

  public static void setUsername(String newUsername) {
    username = newUsername;
  }

  public static void setName(String newName) {
    name = newName;
  }

  public static void setPassword(String newPassword) {
    password = newPassword;
  }

  public static void setRole(String newRole) {
    role = newRole;
  }

  public static String getUsername() {
    return username;
  }

  public static String getName() {
    return name;
  }

  public static String getPassword() {
    return password;
  }

  public static String getRole() {
    return role;
  }
}
