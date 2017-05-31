import java.sql.*;
import java.util.HashMap;
import java.util.Vector;
import java.util.Properties;
import java.io.File;

public class Write {

  private static Connection src2 = null;
  private static String table;
  private static PreparedStatement inserts;
  private static Vector<String> columns;

  public static void db_write(HashMap<String, String> record) throws Exception {
    if (src2 == null) {
      db_connect();
      db_create_table(record);
    }
    Vector<String> values = new Vector<String>();
    String value;
    int place = 1;
    for (String key : columns) {
      value = record.get(key);
      if (key.endsWith("ID")) {
        if (value.length() == 0) {
          inserts.setNull(place, Types.INTEGER);
        }
        else {
          inserts.setInt(place, Integer.parseInt(value));
        }
      }
      else {
        if (value.length() == 0) {
          inserts.setNull(place, Types.VARCHAR);
        }
        else {
          inserts.setString(place, value);
        }
      }
      place++;
      //byte[] value = record.get(key).getBytes("UTF-16LE");
      //values.add(new String(value, "UTF-8"));
    }
    inserts.addBatch();
  }

  public static void db_batch() throws Exception {
    inserts.executeBatch();
    System.out.print('#');
  }

  public static String table_name(String name) {
    File f = new File(name);
    name = f.getName();
    table = name.substring(0, name.lastIndexOf('.'));
    return table;
  }

  private static void db_create_table(HashMap<String, String> record) throws Exception {
    StringBuilder create = new StringBuilder();
    create.append("create table " + table + "(");
    columns = new Vector<String>();
    Vector<String> params = new Vector<String>();

    for (String key : record.keySet()) {
      columns.add(key);
      params.add("?");
      if (key.endsWith("ID")) {
        create.append(key + " bigint, ");
      }
      else {
        create.append(key + " longtext, ");
      }
    }
    String create2 = create.substring(0, create.lastIndexOf(","));
    create2 += ");";

    Statement st = src2.createStatement();
    st.executeUpdate("drop table if exists " + table);
    st.executeUpdate(create2);

    db_prepare_insert(columns, params);
  }

  private static void db_prepare_insert(Vector<String> columns, Vector<String> params) throws Exception {
    String insert_sql = "insert into " + table;
    insert_sql += "(" + String.join(",", columns) + ")";
    insert_sql += " values (" + String.join(",", params) + ")";
    inserts = src2.prepareStatement(insert_sql);
  }

  private static void db_connect() {
    Properties info = new Properties();
    info.setProperty("user", "drupal");
    info.setProperty("characterEncoding", "UTF-8");
    try {
      src2 = DriverManager.getConnection("jdbc:mysql://localhost/cinematic_src2", info);
    }
    catch (Exception e) {
      System.out.println("Error :" + e.getClass() + ": " +  e.getMessage());
    }
  }
}
