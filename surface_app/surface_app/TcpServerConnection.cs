using System;
using System.IO;
using System.Collections.Generic;
using System.Net.Sockets;
using System.Text;
using System.Threading;

namespace dk.itu.spct.tcp
{
    public class Message
    {
        public string text;
        public byte[] data;

        public Message(String l_text) {
            text = l_text;
        }
    }

    public class TcpServerConnection
    {
        //Attributes
        private List<Message> messagesToSend;
        private TcpClient m_socket;
        private Encoding m_encoding;
        private string m_id;
        public Boolean waitForSuccess;

        public TcpServerConnection(TcpClient sock, Encoding encoding) {
            messagesToSend = new List<Message>();
            m_encoding = encoding;
            m_socket = sock;
            waitForSuccess = false;
        }

        //Get-Set
        public string Id {
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
                        if (!String.IsNullOrEmpty(message.text)) {
                            StreamWriter streamWriter = new StreamWriter(networkStream, m_encoding);
                            streamWriter.WriteLine(message.text);
                            streamWriter.Flush();

                            if (message.data != null) {
                                System.Threading.Thread.Sleep(1000);
                                waitForSuccess = true;
                                networkStream.Write(message.data, 0, message.data.Length);
                            }
                        }

                        lock (messagesToSend) {
                            messagesToSend.RemoveAt(0);
                        }
                    } catch (ObjectDisposedException) {
                        m_socket.Close();
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
