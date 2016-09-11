package cliente;

import javax.swing.*;

import jdk.internal.util.xml.impl.ReaderUTF8;

import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import cliente.*;

public class Servidor
{

    public static void main( String[] args )
    {
        // TODO Auto-generated method stub

        MarcoServidor mimarco = new MarcoServidor( );

        mimarco.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        System.out.println( "prueba" );

    }
}

class MarcoServidor extends JFrame implements Runnable
{

    public MarcoServidor( )
    {

        setBounds( 1200, 300, 280, 350 );

        JPanel milamina = new JPanel( );

        milamina.setLayout( new BorderLayout( ) );

        areatexto = new JTextArea( );

        milamina.add( areatexto, BorderLayout.CENTER );

        add( milamina );

        setVisible( true );

        Thread mihilo = new Thread( this );

        mihilo.start( );

    }

    @Override
    public void run( )
    {
        // Codigo escucha
        // System.out.println( "Estoy a la escucha" );
        try
        {

            ServerSocket servidor = new ServerSocket( 8888 );
            String nick, ip, mensaje;
            ArrayList<String> listaIp = new ArrayList<String>( );
            PaqueteEnvio paqueteRecibido;

            while( true )
            {
                // Acepta cualquier conexion exterior
                Socket misock = servidor.accept( );

                // Flujo de datos
                ObjectInputStream paqueteDatos = new ObjectInputStream( misock.getInputStream( ) );

                // lee el flujo de datos y lo que se encuentra que lo almacen en
                // paquete recicibido
                paqueteRecibido = ( PaqueteEnvio )paqueteDatos.readObject( );

                nick = paqueteRecibido.getNick( );
                ip = paqueteRecibido.getIp( );
                mensaje = paqueteRecibido.getMensaje( );

                if( !mensaje.equals( " online" ) )
                {

                    areatexto.append( "\n" + nick + ": " + mensaje + " para " + ip );

                    // Puente de comunicacion por donde fluiran los datos
                    Socket enviaDestinatario = new Socket( ip, 9090 );
                    // Flujo de datos para enviar el Paquete al destinatario
                    ObjectOutputStream paqueteReenvio = new ObjectOutputStream( enviaDestinatario.getOutputStream( ) );
                    // Escribe dentro del el objeto el paquete recibido del
                    // cliente
                    paqueteReenvio.writeObject( paqueteRecibido );
                    // Cierra Socket

                    paqueteReenvio.close( );

                    enviaDestinatario.close( );

                    // Flujo de datos
                    /*
                     * DataInputStream flujoEntrada = new DataInputStream( misock.getInputStream( ) );
                     * 
                     * // Leer mensaje
                     * 
                     * String mensajeText = flujoEntrada.readUTF( );
                     * 
                     * areatexto.append( "\n" + mensajeText );
                     */

                    misock.close( );
                }
                else
                { // Primera vez que se conecta
                  // ---------------------DETECTA ONLINE--------------------------//
                    InetAddress localizacion = misock.getInetAddress( );

                    String ipRemota = localizacion.getHostAddress( );

                    System.out.println( "Online" + ipRemota );

                    listaIp.add( ipRemota );

                    // Paquete arraylist para enviar a los clientes para actualizar combo con
                    // los clientes conectados
                    paqueteRecibido.setIps( listaIp );

                    for( String z : listaIp )
                    {

                        // Enviar
                        System.out.println( "Array: " + z );
                        // Puente de comunicacion por donde fluiran los datos
                        Socket enviaDestinatario = new Socket( z, 9090 );
                        // Flujo de datos para enviar el Paquete al destinatario
                        ObjectOutputStream paqueteReenvio = new ObjectOutputStream( enviaDestinatario.getOutputStream( ) );
                        // Escribe dentro del el objeto el paquete recibido del
                        // cliente
                        paqueteReenvio.writeObject( paqueteRecibido );
                        // Cierra Socket

                        paqueteReenvio.close( );

                        enviaDestinatario.close( );

                        misock.close( );

                    }

                    // -------------------------------------------------------------//
                }

            }
        }
        catch( IOException | ClassNotFoundException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace( );
        }
    }

    private JTextArea areatexto;
}
