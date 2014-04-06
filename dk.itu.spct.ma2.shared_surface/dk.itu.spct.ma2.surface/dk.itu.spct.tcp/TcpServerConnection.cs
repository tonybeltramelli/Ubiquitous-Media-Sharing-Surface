using System;
using System.IO;
using System.Collections.Generic;
using System.Net.Sockets;
using System.Net;
using System.Text;
using System.Threading;

namespace dk.itu.spct.tcp
{
    public class Message
    {
        public string text;
        public byte[] data;
        public bool active;

        public Message(String l_text) {
            text = l_text;
            active = true;
        }
    }

    public class TcpServerConnection
    {
        //Attributes
        private List<Message> messagesToSend;
        private TcpClient m_socket;
        private Encoding m_encoding;
        private int m_id;
        public Boolean waitForSuccess;

        public TcpServerConnection(TcpClient sock, Encoding encoding) {
            messagesToSend = new List<Message>();
            m_encoding = encoding;
            m_socket = sock;
            waitForSuccess = false;
        }

        //Get-Set
        public int Id {
            get {
                return m_id;
            }
            set {
                m_id = value;
            }
        }
        public TcpClient Socket {
            get {
                return m_socket;
            }
        }

        //Implementation

        //Is client connected?
        public bool connected() {
            try {
                if (m_socket != null && m_socket.Client != null && m_socket.Client.Connected) {
                    // Detect if client disconnected
                    if (m_socket.Client.Poll(0, SelectMode.SelectRead)) {
                        byte[] buff = new byte[1];
                        if (m_socket.Client.Receive(buff, SocketFlags.Peek) == 0) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            } catch {
                return false;
            }
        }
        //Read pending messages
        public void processIncoming (){
            TCPCommand command = new TCPCommand();
            lock (m_socket) {
                NetworkStream networkStream = m_socket.GetStream();
                if (networkStream.DataAvailable) {
                    StreamReader streamReader = new StreamReader(networkStream);
                    //StreamReader has a 4kB buffer and will try to read as much as possible on ReadLine().
                    while (networkStream.DataAvailable) {
                        command.ProcessAction(this,streamReader.ReadLine());
                    }
                }
            }
        }
        //Read image
        public byte[] processImage(int bytesToRead) {
            lock (m_socket) {
                NetworkStream networkStream = m_socket.GetStream();
                byte[] buffer = new byte[2048];
                int bytes = 0;
                int bytesRead = 0;
                using (MemoryStream memStream = new MemoryStream()) {
                    while (bytesRead < bytesToRead) {
                        bytes = networkStream.Read(buffer, 0, buffer.Length);
                        memStream.Write(buffer, 0, bytes);
                        bytesRead += bytes;
                    }
                    memStream.Flush();
                    return memStream.ToArray();
                }
            }
        }
        //Send pending messages
        public void processOutgoing() {
            lock (m_socket) {
                if (!m_socket.Connected) {
                    messagesToSend.Clear();
                    return;
                }
                if (messagesToSend.Count == 0) {
                    return;
                }

                if (!waitForSuccess) {
                    NetworkStream networkStream = m_socket.GetStream();
                    Message message = messagesToSend[0];
                    try {
                        if (message.active) {
                            message.active = false;
                            if (!String.IsNullOrEmpty(message.text)) {
                                StreamWriter streamWriter = new StreamWriter(networkStream, m_encoding);
                                streamWriter.WriteLine(message.text);
                                streamWriter.Flush();

                                if (message.data != null) {
                                    Console.WriteLine("Sending image...");
                                    waitForSuccess = true;
                                    TcpListener imglistener = new TcpListener(IPAddress.Any, TcpServer.Port + 1);
                                    imglistener.Start(1);
                                    TcpClient imgSocket = imglistener.AcceptTcpClient();
                                    NetworkStream ns = imgSocket.GetStream();
                                    ns.Write(message.data, 0, message.data.Length);
                                    ns.Flush();
                                    imglistener.Stop();
                                    imgSocket.Close();
                                }
                            }

                            lock (messagesToSend) {
                                messagesToSend.RemoveAt(0);
                            }
                        }
                    } catch (ObjectDisposedException e) {
                        m_socket.Close();
                        Console.WriteLine(e.Message);
                    }
                }
            }
        }
        //Add message to the queue
        public void sendData(Message message) {
            lock (messagesToSend) {
                messagesToSend.Add(message);
            }
        }
        //Disconnect client
        public void forceDisconnect() {
            lock (m_socket) {
                m_socket.Close();
            }
        }
    }
}
