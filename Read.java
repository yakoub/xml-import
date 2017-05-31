import javax.xml.stream.*;
import java.io.*;
import java.util.HashMap;

public class Read {
  public static String table;
  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      throw new Exception("missing file name");
    }
    FileInputStream file = new FileInputStream(args[0]);
    table = Write.table_name(args[0]);
    
    XMLInputFactory factory = XMLInputFactory.newInstance();
    factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
    
    XMLStreamReader reader = factory.createXMLStreamReader(file, "UTF-16LE");

    //System.out.println(reader.getEncoding());

    readerFlow(reader);
    System.out.println("*");

  }

  public static void readerFlow(XMLStreamReader reader) throws Exception {
    int count = 0;
    Boolean output = Boolean.FALSE;
    HashMap<String, String> record = new HashMap<String, String>();
    String key = "", name;

    while (reader.hasNext()) { // && count < 4
      int event = reader.next();
      switch (event) {
        case XMLStreamConstants.START_ELEMENT:
          name = reader.getName().toString();
          if (!"Line".equals(name) && !name.equals(table)) {
            key = reader.getName().toString();
          }
          break;

        case XMLStreamConstants.END_ELEMENT:
          name = reader.getName().toString();
          if (name == "Line") {
            Write.db_write(record);
            if (count++ > 100) {
              Write.db_batch();
              count = 0;
            }
            record.clear();
          }
          break;

        case XMLStreamConstants.CDATA:
        case XMLStreamConstants.CHARACTERS:
          if (key != "") { 
            record.put(key, reader.getText());
            key = "";
          }
          break;
      }
    }
    if (count > 0) {
      Write.db_batch();
    }
  }

  public static void outputText(HashMap<String, String> record) {
    for (String key : record.keySet()) {
      if (key != "BodyEng" && key != "BodyHeb") {
        System.out.println(key + ":" + record.get(key));
      }
    }
    System.out.println("================");
  }

}

