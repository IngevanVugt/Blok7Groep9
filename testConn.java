import java.sql.*;  // Using 'Connection', 'Statement' and 'ResultSet' classes in java.sql package


public class testConn {

    public static void main(String[] args) {
        try {
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://remotemysql.com:3306/NRCP73s0H5",
                        "NRCP73s0H5", "T10HedMmpO");   // For MySQL only
                // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement();
            // Step 3: Execute a SQL SELECT query. The query result is returned in a 'ResultSet' object.
            String strSelect = "select Sequentie, Sequentie_ID from Genoom";
            System.out.println("The SQL statement is: " + strSelect + "\n"); // Echo For debugging

            ResultSet rset = stmt.executeQuery(strSelect);

            // Step 4: Process the ResultSet by scrolling the cursor forward via next().
            //  For each row, retrieve the contents of the cells with getXxx(columnName).
            System.out.println("The records selected are:");
            int rowCount = 0;
            while(rset.next()) {   // Move the cursor to the next row, return false if no more row
                String title = rset.getString("Sequentie");
                int    qty   = rset.getInt("Sequentie_ID");
                System.out.println(title + ", " + qty);
                ++rowCount;
            }
            System.out.println("Total number of records = " + rowCount);

        } catch(SQLException ex) {
            ex.printStackTrace();
        }  // Step 5: Close conn and stmt - Done automatically by try-with-resources (JDK 7)
    }

}
