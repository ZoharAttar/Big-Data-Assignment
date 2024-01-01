import java.io.IOException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        Assigment ass = new Assigment("username", "passwoed");
        ass.fileToDataBase("films.csv");
        ass.calculateSimilarity();
        ass.printSimilarities(29);
    }
}


