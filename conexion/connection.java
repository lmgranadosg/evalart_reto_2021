package conexion;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.*;
import org.json.simple.parser.*;

public class connection {

    public static Connection getConnection(){
        JSONParser parser = new JSONParser();
        Connection con = null;

        try {
            String credentials_path = System.getProperty("user.dir") + "/conexion/db_create.json";
            JSONObject jsonObject = (JSONObject)parser.parse(new FileReader(credentials_path));

            String host     = (String)jsonObject.get("db_ip");
            String port     = (String)jsonObject.get("dp_port");
            String username = (String)jsonObject.get("db_user");
            String password = (String)jsonObject.get("db_pssword");
            String dbURL = "jdbc:mysql://"+host+":"+port+"/evalart_reto" ;

            con = DriverManager.getConnection(dbURL, username, password);
            if( con != null )
                // System.out.println ( "Conectado" );
                ;
        }
        catch( SQLException | FileNotFoundException ex ) {
            ex.printStackTrace();
        }
        catch (IOException | ParseException ex) {
            ex.printStackTrace();
        }

        return con;
    }

    public static void main(String[] args){
        getConnection();
    }
}
