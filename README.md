# xml-import
at some project i was required to import xml data from client's previous system .
but given the xml was encoded in character set UTF-16LE using microsoft software then linux mysql and php failed to import it .
so i thought java is the closest competitor to microsoft and indeed the java xml reader was succesful .

the program main script import.sh reads xml documents under /data directory using Read.java
and connect to mysql database to create the required table and insert data for each xml record using Write.java .
