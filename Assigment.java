import java.io.IOException;
import java.sql.*;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.Class;
public class Assigment {

    String connectionURL;

    public Assigment(String user_name, String password) {
        this.connectionURL = "jdbc:sqlserver://132.72.64.124:1433;databaseName="+user_name+";user="+user_name+";" +"password="+password+";encrypt=false;";
    }


    public void fileToDataBase(String path) {
        PreparedStatement ps_insert = null;
        Connection con = null;

        try{
            String line = "";
            String splitBy = ",";
            BufferedReader br = new BufferedReader(new FileReader(path));
            int counter = 1;

            // Define connection to SQL SERVER:
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//            Assigment con1 = new Assigment("zoharat", "$u5@sVYP");
            con = DriverManager.getConnection(this.connectionURL);


            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                String[] movie = line.split(splitBy);    // use comma as separator
                String insert_query = "insert into MediaItems(MID, PROD_YEAR, TITLE) values (?, ?, ?)";
                ps_insert = con.prepareStatement(insert_query);
                ps_insert.setInt(1, counter);
                ps_insert.setString(2, movie[1]);
                ps_insert.setString(3, movie[0]);
                ps_insert.executeUpdate();
                counter++;

            }
            // Close resources
            con.commit();
            ps_insert.close();
            con.close();
        }
        catch (ClassNotFoundException e) {e.printStackTrace();}
        catch (SQLException e ) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}

        finally {
            try{
                if (ps_insert!=null){ps_insert.close();}
            }
            catch (SQLException e){e.printStackTrace();}
            try{
                if (con!=null){con.close();}
            }
            catch (SQLException e){e.printStackTrace();}
        }

    }

    public void calculateSimilarity() {
        PreparedStatement ps_insert = null;
        Connection con = null;
        CallableStatement cstnt1 =null;
        CallableStatement cstnt = null;
        ResultSet rs2 = null;
        Statement stmt = null;

        try{
            // Define connection to SQL SERVER:
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//            Assigment con1 = new Assigment("zoharat", "$u5@sVYP");
            con = DriverManager.getConnection(this.connectionURL);

            // call the Maximal distance function
            cstnt = con.prepareCall("{?=call dbo.MaximalDistance()}");
            cstnt.registerOutParameter(1, Types.FLOAT);
            cstnt.execute();
            float max = cstnt.getFloat(1);

            //call the SinCalculation function
            cstnt1 = con.prepareCall("{?=call dbo.SimCalculation(?,?,?)}");
            cstnt1.setFloat(4, max);
            cstnt1.registerOutParameter(1, Types.FLOAT);

            stmt = con.createStatement();

            rs2 = stmt.executeQuery("SELECT m1.MID as MID1, m2.MID as MID2 FROM MediaItems as m1 CROSS JOIN MediaItems as m2 WHERE m1.MID<m2.MID");

            while (rs2.next()) {

                //execute the simCalc function after passing the argumemts
                int a = rs2.getInt("MID1");
                int b = rs2.getInt("MID2");
                cstnt1.setInt(2, a);
                cstnt1.setInt(3, b);
                cstnt1.execute();
                float res = cstnt1.getFloat(1);

                //insert the result to the similarity table
                String insert_query = "insert into Similarity(MID1, MID2, SIMILARITY) values (?, ?, ?)";
                ps_insert = con.prepareStatement(insert_query);
                ps_insert.setInt(1, a);
                ps_insert.setInt(2, b);
                ps_insert.setFloat(3, res);
                ps_insert.executeUpdate();


            }
            con.commit();
            ps_insert.close();
            stmt.close();
            cstnt1.close();
            cstnt.close();
            rs2.close();
            con.close();
        }
        catch (ClassNotFoundException e) {e.printStackTrace();}
        catch (SQLException e ) {e.printStackTrace();}

        finally {
            try{
                if (ps_insert!=null){ps_insert.close();}
                if (stmt!=null){stmt.close();}
                if (cstnt1!=null){cstnt1.close();}
                if (cstnt!=null){cstnt.close();}
                if (rs2!=null){rs2.close();}
            }
            catch (SQLException e){e.printStackTrace();}
            try{
                if (con!=null){con.close();}
            }
            catch (SQLException e){e.printStackTrace();}
        }


    }

    public void printSimilarities(long mid) {
        PreparedStatement ps =null;
        ResultSet rs = null;
        Connection con = null;

        try{
            // Define connection to SQL SERVER:
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        Assigment con1 = new Assigment("zoharat", "$u5@sVYP");
            con = DriverManager.getConnection(this.connectionURL);

            String query = "SELECT MediaItems.TITLE, Similarity.SIMILARITY\n" +
                    "FROM MediaItems\n" +
                    "JOIN Similarity ON MediaItems.MID = CASE\n" +
                    "    WHEN Similarity.MID1 = ? THEN Similarity.MID2\n" +
                    "    ELSE Similarity.MID1\n" +
                    "END\n" +
                    "WHERE (? IN (Similarity.MID1, Similarity.MID2)) AND Similarity.SIMILARITY >= 0.3\n" +
                    "ORDER BY Similarity.SIMILARITY ASC\n";

            ps = con.prepareStatement(query);
            ps.setLong(1, mid);
            ps.setLong(2, mid);
            rs = ps.executeQuery();
            while (rs.next()) {
                String title = rs.getString("TITLE");
                float sim = rs.getFloat("SIMILARITY");
                System.out.println(title + sim);
            }
            con.commit();
            ps.close();
            rs.close();
            con.close();
        }
        catch (ClassNotFoundException e) {e.printStackTrace();}
        catch (SQLException e ) {e.printStackTrace();}

        finally {
            try{
                if (ps!=null){ps.close();}
                if (rs!=null){rs.close();}
            }
            catch (SQLException e){e.printStackTrace();}
            try{
                if (con!=null){con.close();}
            }
            catch (SQLException e){e.printStackTrace();}
        }
    }
}



