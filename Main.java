import java.io.IOException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        Assigment ass = new Assigment("zoharat", "$u5@sVYP");
        ass.fileToDataBase("C:/Users/zohar/IdeaProjects/Ass1_JDBC/films.csv");
        ass.calculateSimilarity();
        ass.printSimilarities(29);
    }
}


