using System;
using System.IO;
using System.Collections.Generic;
using System.ComponentModel;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Drawing;

namespace dk.itu.spct.tcp
{
    public partial class TcpServer : Component
    {
        //Constants
        private static int m_port = 5555; //Server listening port
        private const int m_idleTime = 50;
        //Server
        private List<TcpServerConnection> connections;
        private Thread listenThread;
        private Thread sendThread;
        private TcpListener listener;
        private Encoding m_encoding;
        private bool m_isOpen;
        
        //GET-SET
        public static int Port{
            get{
                return m_port;
             }
        }

        public TcpServer() {
            initialize();
        }

        //Setup server
        private void initialize() {
            connections = new List<TcpServerConnection>();
            listenThread = null;
            sendThread = null;
            listener = null;
            m_isOpen = false;
            m_encoding = Encoding.Unicode;
        }

        //Server implementation

        //Run server
        public void Start() {
            lock (this) {

                if (m_isOpen) {
                    return;
                }

                try {
                    listener = new TcpListener(IPAddress.Any, m_port);
                    listener.Start(5);
                } catch (Exception) {
                    listener.Stop();
                    return;
                }

                m_isOpen = true;
                //Star listener and sender
                listenThread = new Thread(new ThreadStart(runListener));
                listenThread.Start();
                sendThread = new Thread(new ThreadStart(runSender));
                sendThread.Start();
            }
        }
        //Run listener to accept connections
        private void runListener() {
            while (m_isOpen) {
                try {
                    if (listener.Pending()) {
                        TcpClient socket = listener.AcceptTcpClient();
                        TcpServerConnection conn = new TcpServerConnection(socket, m_encoding);
                        lock (connections) {
                            connections.Add(conn);
                        }
                    } else {
                        System.Threading.Thread.Sleep(m_idleTime);
                    }
                } catch (Exception e) {
                    Console.WriteLine(e.Message);
                }
            }
        }
        //Run sender to proccess pendind messages
        private void runSender() {
            while (m_isOpen) {
                try {
                    List<int> toRemove = new List<int>();
                    for (int i = 0; i < connections.Count; i++){
                        TcpServerConnection l_con = connections[i];
                        if (l_con.connected()) {
                            l_con.processOutgoing();
                            l_con.processIncoming();
                        } else {
                            toRemove.Add(i);
                        }
                    }
                    foreach (int id in toRemove) {
                        Console.WriteLine("-Device disconnected: " + connections[id].Id);
                        lock (connections) {
                            Gallery.Instance.removeDevice(connections[id].Id);
                            connections.RemoveAt(id);
                        }
                    }
                } catch (Exception){}
            }
        }
        //Send image to specific device
        public void SendImage(int tag_id, ImageObject img) {
            foreach (TcpServerConnection conn in connections) {
                if (conn.Id == tag_id){
                    TCPCommand command = new TCPCommand();
                    command.sendImage(conn, tag_id, img);
                    return;
                }
            }
        }
        //Request for images
        public void requestDeviceGallery(int tag_id) {
            foreach (TcpServerConnection conn in connections) {
                if (conn.Id == tag_id) {
                    TCPCommand command = new TCPCommand();
                    command.requestGallery(conn);
                    return;
                }
            }
        }
        //Disconnect device from server
        public void DisconnectDevice(int tag_id) {
            foreach (TcpServerConnection conn in connections) {
                if (conn.Id == tag_id) {
                    conn.forceDisconnect();
                    return;
                }
            }
        }
        //Stop server
        public void Stop() {
            if (!m_isOpen) {
                return;
            }

            lock (this) {

                Console.WriteLine("-Server stopped");

                m_isOpen = false;
                foreach (TcpServerConnection conn in connections) {
                    conn.forceDisconnect();
                }
                // Terminate listener thread
                try {
                    if (listenThread.IsAlive) {
                        listenThread.Interrupt();
                        Thread.Yield();
                        if (listenThread.IsAlive) {
                            listenThread.Abort();
                        }
                    }
                } catch (System.Security.SecurityException) { }
                // Terminate sender thread
                try {
                    if (sendThread.IsAlive) {
                        sendThread.Interrupt();
                        Thread.Yield();
                        if (sendThread.IsAlive) {
                            sendThread.Abort();
                        }
                    }
                } catch (System.Security.SecurityException) { }
            }

            listener.Stop();

            lock (connections) {
                connections.Clear();
            }

            Gallery.Instance.destroy();

            listenThread = null;
            sendThread = null;
            GC.Collect();
        }
    }
}
