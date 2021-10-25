package evalart_reto;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import conexion.connection;

/*
 @ Autor: Laura Maria Granados García
 Correo electrónico: granadosglm@gmail.com
 Celular: (+57) 313 552 7093

*/

public class reto_codigoton {

    static Connection conexion;
    public static void main(String[] args) throws IOException {

        conexion = connection.getConnection();

        // ----------------------------------- Extracción de la entrada -------------------------------------------
        // Archivo de entrada
        File file = new File("evalart_reto/entrada.txt");

        // Separación de filtros
        List<List<String>> lista_filtros = new ArrayList<List<String>>();

        // Lectura del archivo de entrada
        FileReader archivo_entrada = new FileReader (file);

        BufferedReader buffer_entrada = new BufferedReader(archivo_entrada);

        String line;
        List<String> item = new ArrayList<String>();
        int flag = 0;
        while ((line = buffer_entrada.readLine()) != null) {
            String nombre_filtro;

            if (line.startsWith("<")){
                if (flag != 0){
                    lista_filtros.add(item);
                    item = new ArrayList<String>();
                    item.clear();
                }
                item.add(line);
                flag = 1;
            } else {
                item.add(line);
            }
        }
        lista_filtros.add(item);
        // ------------------------------ Haciendo consulta y generando primer listado de clientes ----------------------

        List<String> resultado_final = new ArrayList<String>();

        for (List<String> filtro_entrada: lista_filtros){
            String[] result_sentence = generar_where(filtro_entrada);
            List<filtros> list_to_show = execute_query(result_sentence[1]); // Lista de objetos con resultados
            int n = list_to_show.size();
            boolean reiniciar = false;

            List<String> company_list = new ArrayList<String>();

            if ( n < 4 ){
                resultado_final.add(result_sentence[0] + "\n" + "CANCELADA");
                System.out.println(result_sentence[0] + "\n" + "CANCELADA");
            }
            else{
                // Se eliminan personas de la misma empresa
                do{
                    reiniciar = false;
                    for (int i=0; i < n && !reiniciar; i++){
                        for(int j = 0; j < n && !reiniciar; j++){
                            if (i != j){
                                if(list_to_show.get(i).getcompany() == list_to_show.get(j).getcompany()){
                                    // Comparar quién tiene más balance, y se borra
                                    if(list_to_show.get(i).gettotal_balance() < list_to_show.get(j).gettotal_balance()){
                                        list_to_show.remove(i);
                                        n = list_to_show.size();
                                        reiniciar = true;
                                    }
                                }
                            }
                        }
                    }
                } while(reiniciar);


                // Contar cuántos hombres y mujeres hay
                int n_males = contar_hombres(list_to_show);
                int n_females = list_to_show.size()-n_males;

                // Debe haber el mismo número de hombres y mujeres, y al tiempo dejar máximo 8 personas por mesa
                // adicionalmente, si después de sacar personas, quedan menos de 4 personas por mesa, esta
                // deberá ser cancelada
                if (n_males != n_females){
                    // Si hay más hombres que mujeres, hay que sacar hombres hasta que sea igual al número de mujeres
                    if (n_males > n_females){
                        do{
                            reiniciar = false;
                            for (int i=0; i < n && !reiniciar; i++){
                                for(int j = 0; j < n && !reiniciar; j++){
                                    if (i != j){
                                        if((list_to_show.get(i).getmale()==1) && (list_to_show.get(j).getmale() == 1)){ // Si ambos son hombres
                                            // Comparar quién tiene menos balance, y se borra
                                            if(list_to_show.get(i).gettotal_balance() < list_to_show.get(j).gettotal_balance()){
                                                list_to_show.remove(i);
                                                n = list_to_show.size();
                                                // Se debe reiniciar proceso de eliminación de hombres si el numero de hombres
                                                // sigue siendo diferente al numero de mujeres
                                                // Contar cuántos hombres y mujeres hay
                                                n_males = contar_hombres(list_to_show);
                                                n_females = list_to_show.size()-n_males;
                                                reiniciar = true;
                                            }
                                        }
                                    }
                                }
                            }
                        } while(reiniciar && (n_males != n_females));
                    }
                    else{ // Si hay más mujeres que hombres, hay que sacar mujeres hasta que sea igual al número de hombres
                        do{
                            List<filtros> list_to_show_female = new ArrayList<filtros>();
                            List<filtros> list_to_show_male = new ArrayList<filtros>();

                            // Separar hombres y mujeres
                            for (int k = 0; k < n; k++){
                                if (list_to_show.get(k).getmale() == 1){
                                    list_to_show_male.add(list_to_show.get(k));
                                }
                                else{
                                    list_to_show_female.add(list_to_show.get(k));
                                }
                            }
                            // Buscar mujer con menos balance
                            filtros m_balance = Collections.min(list_to_show_female, Comparator.comparing(c -> c.gettotal_balance()));

                            // Eliminar mujer con menos balance
                            for (int j = 0; j < n; j++){
                                if((list_to_show.get(j).getcode()).equals(m_balance.getcode())){
                                    list_to_show.remove(j);
                                    n = list_to_show.size(); // Actualización del número de personas
                                }
                            }

                            n_males = contar_hombres(list_to_show);
                            n_females = list_to_show.size()-n_males;

                            // System.out.println("Hombres:" + n_males);

                        } while(n_males != n_females);

                        // System.out.println("Resultado final, en la mesa " + result_sentence[0] + "hay " + n_males + " hombres y " + n_females + " mujeres");
                    }
                }

                // Desencripción de código
                for (int enc = 0; enc < list_to_show.size(); enc++){
                    if (list_to_show.get(enc).getencrypt() == 1){ // El código debe ser desenciptado
                        String aux = list_to_show.get(enc).getcode(); // Codigo encriptado
                        list_to_show.get(enc).setcode(desencriptar(aux));
                    }
                }

                // Segunda inspección sobre el número de personas
                if (list_to_show.size() < 4 ){
                    resultado_final.add(result_sentence[0] + "\n" + "CANCELADA");
                    System.out.println(result_sentence[0] + "\n" + "CANCELADA");
                }
                else{
                    // Si hay más de 8 personas, hay que eliminar personas, y dado que debe haber un
                    // numero igual de hombres y mujeres, en cada iteración, se eliminarían dos personas al tiempo
                    n = list_to_show.size(); // Actualización del número de personas
                    if(list_to_show.size() > 8 ){

                        do{
                            List<filtros> list_to_show_female = new ArrayList<filtros>();
                            List<filtros> list_to_show_male = new ArrayList<filtros>();

                            // Separar hombres y mujeres
                            for (int k = 0; k < n; k++){
                                if (list_to_show.get(k).getmale() == 1){
                                    list_to_show_male.add(list_to_show.get(k));
                                }
                                else{
                                    list_to_show_female.add(list_to_show.get(k));
                                }
                            }
                            // Buscar hombre y mujer con menos balance
                            filtros h_balance = Collections.min(list_to_show_male, Comparator.comparing(c -> c.gettotal_balance()));
                            filtros m_balance = Collections.min(list_to_show_female, Comparator.comparing(c -> c.gettotal_balance()));

                            // En caso que al sacar un mínimo, hubiera otro mínimo del mismo valor
                            // Se debe distinguir entonces por el código organizado en forma ascendente, es decir
                            // quedarse con el código menor

                            // Se comparará el mínimo encontrado hasta el momento con la lista de hombres
                            for (int i=0; i < list_to_show_male.size(); i++){

                                if (!h_balance.getcode().equals(list_to_show_male.get(i).getcode())){ // No se debe comparar el objeto con si mismo
                                    if(h_balance.gettotal_balance() == list_to_show.get(i).gettotal_balance()){ // Si el balance es igual
                                        // Se debe reemplazar con el que tenga menor código
                                        String code1 = h_balance.getcode();
                                        String code2 = list_to_show_male.get(i).getcode();
                                        if (code1.compareToIgnoreCase(code2) > 0){
                                            h_balance = list_to_show_male.get(i); // Se reemplaza por tener más valor
                                        }
                                    }
                                }
                            }

                            // Se comparará el mínimo encontrado hasta el momento con la lista de mujeres
                            for (int i=0; i < list_to_show_female.size(); i++){

                                if (!m_balance.getcode().equals(list_to_show_female.get(i).getcode())){ // No se debe comparar el objeto con si mismo
                                    if(m_balance.gettotal_balance() == list_to_show.get(i).gettotal_balance()){ // Si el balance es igual
                                        // Se debe reemplazar con el que tenga menor código
                                        String code1 = m_balance.getcode();
                                        String code2 = list_to_show_female.get(i).getcode();
                                        if (code1.compareToIgnoreCase(code2) > 0){
                                            m_balance = list_to_show_female.get(i); // Se reemplaza por tener más valor
                                        }
                                    }
                                }
                            }


                            // Eliminar hombre y mujer con menos balance
                            for (int j = 0; j < n; j++){
                                if((list_to_show.get(j).getcode()).equals(h_balance.getcode())){
                                    list_to_show.remove(j);
                                    n = list_to_show.size(); // Actualización del número de personas
                                }
                                else if((list_to_show.get(j).getcode()).equals(m_balance.getcode())){
                                    list_to_show.remove(j);
                                    n = list_to_show.size(); // Actualización del número de personas
                                }
                            }

                            n = list_to_show.size(); // Actualización del número de personas
                        } while(n>8);

                        String cadena_resultado = "";
                        System.out.println(result_sentence[0]);
                        for (filtros resultado_objeto: list_to_show){
                            if (cadena_resultado.equals("")){
                                cadena_resultado = cadena_resultado + resultado_objeto.getcode();
                            }
                            else{
                                cadena_resultado = cadena_resultado + "," + resultado_objeto.getcode();
                            }

                        }
                        System.out.println(cadena_resultado);
                    }
                    else{
                        String cadena_resultado = "";
                        System.out.println(result_sentence[0]);
                        for (filtros resultado_objeto: list_to_show){
                            if (cadena_resultado.equals("")){
                                cadena_resultado = cadena_resultado + resultado_objeto.getcode();
                            }
                            else{
                                cadena_resultado = cadena_resultado + "," + resultado_objeto.getcode();
                            }

                        }
                        System.out.println(cadena_resultado);
                    }
                }

                // Acá se hace la impresión de los asistentes por mesa en caso de que la mesa haya quedado
                // bien distribuida y no se haya cancelado

            }

        }

    }

    public static int contar_hombres(List<filtros> list_to_show){
        int n_males = 0;
        for (filtros obj_male: list_to_show){
            if (obj_male.getmale() == 1){
                n_males++;
            }
        }
        return n_males;
    }

    public static List<filtros> execute_query(String sentence) {

        List<filtros> recipe = new ArrayList<>();

        String sql = sentence;

        try {
            Statement statement = conexion.createStatement();
            ResultSet result = statement.executeQuery(sql);

            // Forma: (String code, int company, int encrypt, int TC_type, int UG_location, float total_balance)
            while (result.next()) {
                filtros var_recipe = new filtros(result.getString(1), result.getInt(2), result.getInt(3), result.getInt(4), result.getInt(5), result.getInt(6), result.getFloat(7));
                recipe.add(var_recipe);
            }
        } catch (SQLException exc) {
            System.out.println("No pudo traer registros");
        }
        return recipe;
    }

    public static String[] generar_where(List<String> filtro){
        List<String> sentencia_salida1 = new ArrayList<String>();
        List<String> sentencia_salida2 = new ArrayList<String>();

        String[] sentencia_salida = new String[2];
        sentencia_salida[1] = "SELECT client.code, client.company, client.encrypt, client.male,  client.type, client.location, sum(account.balance) as total_balance" +
                                    " FROM client, account" +
                                    " WHERE client.id = account.client_id";

        for (String element: filtro){ // Recorre por ejemplo los elementos del array [Mesa 1, UG:2, RI:500000]
            String[] element_split = element.split(":");
            if (element_split[0].equals("TC")){
                sentencia_salida1.add("type=" + element_split[1]);
            }
            else if (element_split[0].equals("UG")){
                sentencia_salida1.add("location=" + element_split[1]);
            }
            else if (element_split[0].equals("RI")){
                sentencia_salida2.add("sum(balance)>=" + element_split[1]);
            }
            else if (element_split[0].equals("RF")){
                sentencia_salida2.add("sum(balance)<=" + element_split[1]);
            }
            else{ // Se trata del nombre de la mesa, no del filtro
                sentencia_salida[0] = element;
            }

        }

        // Agregando filtro TC o UG
        for (String filtro1: sentencia_salida1){
            sentencia_salida[1] = sentencia_salida[1] + " AND " + filtro1;
        }

        sentencia_salida[1] = sentencia_salida[1] + " GROUP BY account.client_id";

        // Agregando filtro de balance RI o RF
        for (String filtro2: sentencia_salida2){
            sentencia_salida[1] = sentencia_salida[1] + " HAVING " + filtro2;
        }

        sentencia_salida[1] = sentencia_salida[1] + " ORDER BY total_balance DESC";

        return sentencia_salida;
    }

    public static String desencriptar(String code) throws IOException{
        // Creación de la URL
        String enlace = "https://test.evalartapp.com/extapiquest/code_decrypt/" + code;

        URL url = new URL(enlace);

        URLConnection con = url.openConnection();
        InputStream input =con.getInputStream();

        BufferedReader buffer = new BufferedReader(new InputStreamReader(input));

        String line = null;
        String codigo = null;
        while ((line = buffer.readLine()) != null) {
            codigo = line;
        }

        codigo = codigo.replace("\"", "");
        return codigo;
    }
}
