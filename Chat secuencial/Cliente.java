import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cliente
 */
public class Cliente {

    private static final int MAXBYTES = 500;
    private static final String CONTEXTO = "UTF-8";
    public static Scanner sc = new Scanner(System.in);
    public static String nombreServidor = new String();

    public static void main(String[] args) {
        int numPuertoServidor;
        DatagramPacket pRecibido, pEnvio;
        InetAddress ipServidor;
        byte [] bufferLectura, bufferEscritura;
        boolean salir = false;
        DatagramSocket socketCliente;
        String nombre, hostServidor;
        String prev;
        String lineaRecibida;

        if(args.length < 3){
            System.out.println("Error, parámetro [host], [puerto] y [nombre]");
            System.exit(1);
        }

        hostServidor = args[0];
        nombre = args[2];
        numPuertoServidor = Integer.parseInt(args[1]);
        prev =  nombre + ">";

        try {
            ipServidor = InetAddress.getByName(hostServidor);
            socketCliente = new DatagramSocket();

            if (iniciarChat(socketCliente, ipServidor, nombre, numPuertoServidor)) {
                do {
                    System.out.println(prev);
                    String lineaLeer = sc.nextLine();

                    if (lineaLeer.length() == 0) {
                        System.out.println(prev+nombre+ " finaliza el chat...");
                        salir = true;
                    } else {
                        bufferEscritura = new byte[MAXBYTES];
                        bufferEscritura = lineaLeer.getBytes();
                        pEnvio = new DatagramPacket(bufferEscritura, bufferEscritura.length, ipServidor, numPuertoServidor);
                        socketCliente.send(pEnvio);

                        if (lineaLeer.equals(".")) {
                            System.out.println("Terminando la sesión...");
                            socketCliente.close();
                            System.exit(1);
                        }

                        //Esperamos respuesta
                        bufferLectura = new byte[MAXBYTES];
                        pRecibido = new DatagramPacket(bufferLectura, MAXBYTES, ipServidor, numPuertoServidor);
                        socketCliente.receive(pRecibido);
                        lineaRecibida = new String(pRecibido.getData(), 0, pRecibido.length(), CONTEXTO);
                        //Mostramos por pantalla
                        System.out.println(lineaRecibida);

                    }
                } while (true);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean iniciarChat(DatagramSocket sCliente, InetAddress ipServidor, String nombre, int numPuertoServidor) {
        try {
            //Para enviar
            System.out.println("Chat secuencia... Primero manda el cliente y responde le servidor");
            String lineaMandar = "@hola" + nombre + "@";
            byte [] bufferEscritura = new byte[MAXBYTES];
            bufferEscritura = lineaMandar.getBytes();
            DatagramPacket pqEnviar = new DatagramPacket(bufferEscritura, bufferEscritura.length, ipServidor, numPuertoServidor);
            sCliente.send(pqEnviar);

            //Para recibir
            byte [] bufferLectura = new byte[MAXBYTES];
            DatagramPacket pqRecibido = new DatagramPacket(bufferLectura, MAXBYTES, ipServidor, numPuertoServidor);
            sCliente.receive(pqRecibido);
            String lineaRecibida = new String(pqRecibido.getData(), 0, pqRecibido.getLength(), CONTEXTO);
            Pattern patSaludo = Pattern.compile("@hola#(.+)@");
            Matcher m = patSaludo.matcher(lineaRecibida);

            if (m.find()) {
                nombreServidor = m.group(1);
                System.out.println(lineaRecibida);
                return true;
            }else{
                return false;
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
        //return false;
        
    }

}