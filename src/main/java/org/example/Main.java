package org.example;

import java.sql.*;
import java.util.Scanner;

public class Main {
    private static Connection connection;
    public static void main(String[] args) {
        mostrarMenu();
    }
    public static void mostrarMenu(){
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("\nMenú. Que quieres hacer?: ");
            System.out.println("1. Crear un usuario sin monedas:");
            System.out.println("2. Iniciar sesion y ver monedas: ");
            System.out.println("3. Salir del menu");
            System.out.println("Selecciona una opcion: ");
            int opcion = scanner.nextInt();
            switch (opcion){
                case 1:
                    System.out.println("Ingrese el nuevo nombre de usuario");
                    String nombre = scanner.next();
                    System.out.println("Ingrese nueva contraseña");
                    String contraseña = scanner.next();
                    Usuario nuevoUsuario = new Usuario(nombre, contraseña);
                    crearUsuario(nuevoUsuario);
                    break;
                case 2:
                    System.out.println("Ingrese su nombre de usuario");
                    String nombreUsuario = scanner.next();
                    System.out.println("Ingrese su contraseña");
                    String contraseñaUsuario = scanner.next();
                    loginUsuario(nombreUsuario, contraseñaUsuario);
                    break;
                case 3:
                    System.out.println("Hasta la proxima!");
                    closeConection();
                    System.exit(0);
                default:
                    System.out.println("Opcion no valida");

            }
        }
    }

    public static void openConnection(){
        try{
            connection= DriverManager.getConnection("jdbc:mysql://localhost:3360/carteracripto", "root", "");
        }catch(SQLException throwables){
            throwables.printStackTrace();
        }
    }
    public static void closeConection(){
        try {
            connection.close();
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
    }
    public static void crearUsuario(Usuario usuario){
        openConnection();
        try(Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3360/carteracripto", "root", "")){
            String query = "INSERT INTO usuarios(nombre, contraseña) VALUES(?,?)";
            try(PreparedStatement preparedStatement= connection.prepareStatement(query)){
                preparedStatement.setString(1,usuario.getNombre());
                preparedStatement.setString(2,usuario.getContraseña());
                preparedStatement.execute();
                System.out.println("Nuevo usuario creado");
            }
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }finally {
            closeConection();
        }
    }
    public static void mostrarMonedas(int id_usuario){
        openConnection();
        try(Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3360/carteracripto", "root", "")){
            String query = "SELECT * FROM monedas WHERE id_usuario=?";
            try(PreparedStatement preparedStatement= connection.prepareStatement(query)){
                preparedStatement.setInt(1, id_usuario);
                ResultSet resultSet = preparedStatement.executeQuery();

                //mostrar por consola
                while(resultSet.next()){
                    String nombre = resultSet.getString("nombre");
                    double precio = resultSet.getDouble("precio");
                    int cantidadMonedas = resultSet.getInt("cantidad");
                    System.out.println("Nombre: "+nombre+" Precio: "+precio+" Cantidad: "+cantidadMonedas);
                }
            }
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }finally {
            closeConection();
        }
    }
    public static void loginUsuario(String nombre, String contraseña){
        openConnection();
        try(Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3360/carteracripto", "root", "")){
            String query = "SELECT id_usuario FROM usuarios WHERE nombre=? AND contraseña=?";
            try(PreparedStatement preparedStatement= connection.prepareStatement(query)){
                preparedStatement.setString(1, nombre);
                preparedStatement.setString(2, contraseña);
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()){
                    System.out.println("Bienvenido "+nombre);
                    mostrarMonedas(resultSet.getInt("id_usuario"));
                }else{
                    System.out.println("Usuario o contraseña incorrectos.");
                }
                resultSet.close();
            }
        }catch (SQLException throwables){
           throwables.printStackTrace();
        }finally {
            closeConection();
        }
    }
}
class Moneda{
    String nombre;
    double precio;
    int cantidad;
    public Moneda(String nombre, double precio, int cantidad){
        this.nombre=nombre;
        this.precio=precio;
        this.cantidad=cantidad;
    }
}
class Usuario{
    String nombre;

    public String getNombre() {
        return nombre;
    }

    public String getContraseña() {
        return contraseña;
    }
    String contraseña;
    public Usuario(String nombre, String contraseña){
        this.nombre=nombre;
        this.contraseña=contraseña;
    }

}