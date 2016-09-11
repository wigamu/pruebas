package cliente;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;
import java.util.ArrayList;

import javax.swing.*;

public class Cliente
{

    public static void main( String[] args )
    {
        // TODO Auto-generated method stub

        MarcoCliente mimarco = new MarcoCliente( );

        mimarco.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

    }

}

class MarcoCliente extends JFrame
{

    public MarcoCliente( )
    {

        setBounds( 800, 200, 200, 350 );

        LaminaMarcoCliente milamina = new LaminaMarcoCliente( );

        add( milamina );

        setVisible( true );

        addWindowListener( new EnvioOnline( ) );
    }

}

// --------------Envio de señal online
class EnvioOnline extends WindowAdapter // hereda de la clase Adaptadora
{
    public void windowOpened( WindowEvent e )
    {
        try
        {
            Socket misocket = new Socket( "172.16.10.134", 8888 );
            PaqueteEnvio datos = new PaqueteEnvio( );
            datos.setMensaje( " online" );

            /*
             * Flujo de datos ObjectOutputStream para poder enviar el objeto datos a través del socket y lo reciba el servidor, de esta forma sabe que esta online.
             */
            ObjectOutputStream paqueteDatos = new ObjectOutputStream( misocket.getOutputStream( ) );
            paqueteDatos.writeObject( datos );
            misocket.close( );

        }
        catch( Exception e2 )
        {
            // TODO: handle exception
        }
    }
}// --------------------------

class LaminaMarcoCliente extends JPanel implements Runnable
{

    public LaminaMarcoCliente( )
    {
        String nickUsuario = JOptionPane.showInputDialog( "Nick: " );

        JLabel nNick = new JLabel( "Nick: " );

        add( nNick );

        nick = new JLabel( );

        nick.setText( nickUsuario );

        add( nick );

        JLabel texto = new JLabel( "Online: " );

        add( texto );

        ip = new JComboBox( );


        add( ip );

        campochat = new JTextArea( 12, 20 );

        add( campochat );

        campo1 = new JTextField( 20 );

        add( campo1 );

        btnEnviar = new JButton( "Enviar" );

        EnviaTexto enviaMensaje = new EnviaTexto( );

        btnEnviar.addActionListener( enviaMensaje );
        add( btnEnviar );
        Thread mihilo = new Thread( this );
        mihilo.start( );

    }
    private class EnviaTexto implements ActionListener
    {

        @Override
        public void actionPerformed( ActionEvent e )
        {
            // TODO Auto-generated method stub
            // System.out.println( campo1.getText( ));

            // Crear socket

            campochat.append( "\n" + campo1.getText( ) );
            //campo1.setText( "" );
            
            try
            {
                // se conecta al servidor
                Socket cliente = new Socket( "172.16.10.134", 8888 );
                PaqueteEnvio datos = new PaqueteEnvio( );
                datos.setNick( nick.getText( ) );
                datos.setIp( ip.getSelectedItem( ).toString( ) );
                datos.setMensaje( campo1.getText( ) );
                ObjectOutputStream paqueteDatos = new ObjectOutputStream( cliente.getOutputStream( ) );
                paqueteDatos.writeObject( datos );
                cliente.close( );
            }
            catch( UnknownHostException e1 )
            {
                // TODO Auto-generated catch block
                e1.printStackTrace( );
            }
            catch( IOException e1 )
            {
                // TODO Auto-generated catch block
                System.out.println( e1.getMessage( ) );
            }

        }

    }

    private JTextField campo1;

    private JComboBox ip;

    private JLabel nick;

    private JTextArea campochat;

    private JButton btnEnviar;

    @Override
    public void run( )
    {
        // TODO Auto-generated method stub
        try
        {
            ServerSocket servidorCliente = new ServerSocket( 9090 );
            // Canal donde va recibir e paquete
            Socket cliente;
            PaqueteEnvio paqueteRecibido;

            while( true )
            {
                cliente = servidorCliente.accept( );
                ObjectInputStream flujoEntrada = new ObjectInputStream( cliente.getInputStream( ) );
                // Informacion que le ha lelgado del servidor
                paqueteRecibido = ( PaqueteEnvio )flujoEntrada.readObject( );

                // Si no ha recibido el mensaje online
                if( !paqueteRecibido.getMensaje( ).equals( " online" ) )
                {
                    campochat.append( "\n" + paqueteRecibido.getNick( ) + ": " + paqueteRecibido.getMensaje( ) );

                }
                else
                { // El cliente se acaba de conectar

                    ArrayList<String> IpsMenu = new ArrayList<String>( );
                    IpsMenu = paqueteRecibido.getIps( );

                    ip.removeAllItems( );

                    for( String z : IpsMenu )
                    {
                        ip.addItem( z );
                    }
                    // campochat.append( "\n" + paqueteRecibido.getIps( ) );
                }
            }
        }
        catch( Exception e )
        {
            System.out.println( e.getMessage( ) );
        }

    }

}

class PaqueteEnvio implements Serializable
{
    private String nick, ip, mensaje;

    private ArrayList<String> ips;

    public ArrayList<String> getIps( )
    {
        return ips;
    }

    public void setIps( ArrayList<String> ips )
    {
        this.ips = ips;
    }

    public String getNick( )
    {
        return nick;
    }

    public void setNick( String nick )
    {
        this.nick = nick;
    }

    public String getIp( )
    {
        return ip;
    }

    public void setIp( String ip )
    {
        this.ip = ip;
    }

    public String getMensaje( )
    {
        return mensaje;
    }

    public void setMensaje( String mensaje )
    {
        this.mensaje = mensaje;
    }

}