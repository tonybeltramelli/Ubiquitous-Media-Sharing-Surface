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
    class Test
    {
        private static TcpServer server;
        private static TcpClient tcpClient_1;
        private static TcpClient tcpClient_2;
        private static Image image;
        private const int m_server_port = 5555;

        static void Main(string[] args) {
            server = new TcpServer();
            server.Start();

            tcpClient_1 = new TcpClient();
            tcpClient_2 = new TcpClient();

            try {
                tcpClient_1.Connect("127.0.0.1", m_server_port);
                tcpClient_2.Connect("127.0.0.1", m_server_port);
            } catch (Exception e) {
                Console.WriteLine(e);
            }

            //Register device 1
            registerDevice(1, tcpClient_1);

            //Request for gallery
            System.Threading.Thread.Sleep(1000);
            server.requestDeviceGallery(1);
            proccessRequest(1, tcpClient_1);

            sendImage(1, "1.png", tcpClient_1);
            sendImage(1, "2.png", tcpClient_1);

            //Register device 3
            registerDevice(3, tcpClient_2);

            //Request for gallery
            System.Threading.Thread.Sleep(1000);
            server.requestDeviceGallery(3);
            proccessRequest(3, tcpClient_2);

            sendImage(3, "3.png", tcpClient_2);

            //Wait for Ioannis
            Console.WriteLine("Wait for device to test...");
            Console.ReadLine();
            server.requestDeviceGallery(2);

            Console.WriteLine("Send images...");
            Console.ReadLine();

            //Send image
            image = Gallery.Instance.Images[0];
            server.SendImage(2, image);
            //receiveImage(3, image, tcpClient_2);

            //Send image
            image = Gallery.Instance.Images[1];
            server.SendImage(2, image);
            //receiveImage(3, image, tcpClient_2);

            //Disconnect device
            //server.DisconnectDevice(1);
            //tcpClient_1.Close();

            Console.Read();
            Console.WriteLine("End...");
        }

        public static void registerDevice(int tag_id, TcpClient tcp) {
            NetworkStream ns = tcp.GetStream();
            StreamWriter sw = new StreamWriter(ns);
            String request = @"[{'action':'0','tag_id':'" + tag_id  + "'}]";
            sw.WriteLine(request);
            sw.Flush();
        }
        public static void proccessRequest(int tag_id, TcpClient tcp) {
            NetworkStream ns = tcp.GetStream();
            StreamReader sr = new StreamReader(ns);
            Console.WriteLine("+Client " + tag_id + ": " + sr.ReadLine());
        }
        public static void sendImage(int tag_id,string file_name,TcpClient tcp) {

            NetworkStream ns = tcp.GetStream();
            StreamReader sr = new StreamReader(ns);
            StreamWriter sw = new StreamWriter(ns);
            FileStream fs;
            byte[] data;

            fs = new FileStream("../../images/" + file_name, FileMode.Open, FileAccess.Read);
            data = new byte[fs.Length];
            String request = @"[{'action':'2','name':'" + file_name + "','size':'" + data.Length + "'}]";
            sw.WriteLine(request);
            sw.Flush();
            fs.Read(data, 0, data.Length);
            fs.Flush();
            fs.Close();
            ns.Write(data, 0, data.Length);
            ns.Flush();

            Console.WriteLine("+Client " + tag_id + ": " + sr.ReadLine());
        }
        public static void receiveImage(int tag_id, Image image, TcpClient tcp) {

            byte[] buffer = new byte[2048];
            int bytes = 0;
            int bytesRead = 0;
            byte[] data;

            NetworkStream ns = tcp.GetStream();
            StreamReader sr = new StreamReader(ns);
            StreamWriter sw = new StreamWriter(ns);
            TcpClient imageSocket = new TcpClient();

            Console.WriteLine("+Client " + tag_id + ": " + sr.ReadLine());

            imageSocket.Connect("127.0.0.1", m_server_port + 1);
            NetworkStream imageStream = imageSocket.GetStream();
            using (MemoryStream memStream = new MemoryStream()) {
                while (bytesRead < image.ByteArray().Length) {
                    bytes = imageStream.Read(buffer, 0, buffer.Length);
                    memStream.Write(buffer, 0, bytes);
                    bytesRead += bytes;
                }
                data = memStream.ToArray();
                TypeConverter tc = TypeDescriptor.GetConverter(typeof(Bitmap));
                Bitmap bitmap = (Bitmap)tc.ConvertFrom(data);
                bitmap.Save("../../images/test_" + image.File_Name);
            }

            //Send image received notification
            String request = @"[{'action':'3'}]";
            sw.WriteLine(request);
            sw.Flush();
        }
        public static void printGallery() {
            System.Threading.Thread.Sleep(2000);
            foreach (Image img in Gallery.Instance.Images) {
                Console.WriteLine("Image: " + img.File_Name);
                foreach (int owner in img.Owners) {
                    Console.Write("-" + owner);
                }
                Console.WriteLine();
            }
        }
    }
}
