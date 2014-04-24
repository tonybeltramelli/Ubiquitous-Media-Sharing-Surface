using dk.itu.spct.common;
using Microsoft.AspNet.SignalR;
using Microsoft.AspNet.SignalR.Hubs;
using SignalRSelfHost;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.IO;
using System.Net;
using System.Net.Sockets;

namespace dk.itu.spct.signalr
{
    class ConnectionManager
    {
        private const string M_REQUEST_GALLERY = "RequestGallery";
        private const string M_SEND_IMAGE_METADATA = "SendImageMetadata";
        private const string M_SEND_SUCCESS = "SendSuccess";
        private const string M_DISCONNECT = "Disconnect";

        TcpListener listener;

        private IHubContext context;                     //Server context
        private Dictionary<int, string> users;           //users<tag_id,connection_id>
        private Dictionary<string, int> connections;     //connections<connection_id,tag_id>

         //Singleton implementation
        private static ConnectionManager instance;
        private ConnectionManager() { Initialize(); }
        public static ConnectionManager Instance
        {
            get 
            {
                if (instance == null)
                {
                    instance = new ConnectionManager();
                }
                return instance;
            }
        }

        //Setup ConnectionManager class
        private void Initialize()
        {
            context = GlobalHost.ConnectionManager.GetHubContext<Listener>();
            users = new Dictionary<int, string>();
            connections = new Dictionary<string, int>();
        }

        //Start TCPImageListener
        public void StartImageListener(int port){
            listener = new TcpListener(IPAddress.Any, port + 1);
            listener.Start(5);
        }
        
        //------SRSServer-------

        //Add Connection
        public void AddConnection(int tag_id,string connection_id)
        {
            if(!users.ContainsKey(tag_id)){
                Console.WriteLine("--AddConnection: " + tag_id);
                connections.Add(connection_id, tag_id);
                users.Add(tag_id, connection_id);

                Gallery.Instance.AssignColor(tag_id);
            }
            else
            {
                //Update connection id
                string prev_conn = users[tag_id];
                users[tag_id] = connection_id;
                connections.Remove(prev_conn);
                connections.Add(connection_id, tag_id);
            }
        }
        //Receive Image Meta Data
        public void ReceiveImageMetaData(string connection_id, string file_name, int image_size)
        {
            //Add image to gallery
            byte[] data = receiveImage(image_size);
            ImageObject img = new ImageObject(file_name, data);
            if (connections.ContainsKey(connection_id))
            {
                img.AddOwner(connections[connection_id]);
                Gallery.Instance.AddImage(img);
                Console.WriteLine("--ReceiveImageMetaData: " + file_name + " " + img.Id);
                SendSuccess(connection_id);
            }

        }
      
        //------Surface-------

        //Request for images
        public void RequestGallery(int tag_id)
        {
            if (users.ContainsKey(tag_id))
            {
                Console.WriteLine("--RequestGallery: " + tag_id);
                IClientProxy proxy = context.Clients.Client(users[tag_id]);
                proxy.Invoke(M_REQUEST_GALLERY);
            }
        }
        //Send image to client
        public void SendImageMetaData(int tag_id, int image_id)
        {
            if (!users.ContainsKey(tag_id))
            {
                return;
            }
            ImageObject img = null;
            foreach (ImageObject img_tmp in Gallery.Instance.Images)
            {
                if (image_id == img_tmp.Id)
                {
                    img = img_tmp;
                    break;
                }
            }
            if (img != null)
            {
                if (img.Owners.Contains(tag_id))
                {
                    return;
                }
            }
            else
            {
                return;
            }

            byte[] bytes = img.ByteArray();

            Console.WriteLine("--SendImage: " + tag_id + " " + img.File_Name + " " + bytes.Length);

            IClientProxy proxy = context.Clients.Client(users[tag_id]);
            proxy.Invoke(M_SEND_IMAGE_METADATA, img.File_Name, bytes.Length);

            BackgroundWorker _backgroundWorker = new BackgroundWorker();
            _backgroundWorker.DoWork += sendImageData;
            _backgroundWorker.RunWorkerAsync(bytes);

            Gallery.Instance.Images.Remove(img);
            img.AddOwner(tag_id);
            Gallery.Instance.Images.Add(img);
        }
      
        //Disconnect device from server
        public void DisconnectDevice(int tag_id)
        {
            if (users.ContainsKey(tag_id))
            {
                Console.WriteLine("--Disconnect: " + tag_id);
                string conn_id = users[tag_id];
                connections.Remove(users[tag_id]);
                users.Remove(tag_id);
                IClientProxy proxy = context.Clients.Client(conn_id);
                proxy.Invoke(M_DISCONNECT);

                Gallery.Instance.removeDevice(tag_id);
            }
        }

        //------Private-------
        //Send sucess notification to client
        private void SendSuccess(string connection_id)
        {
            Console.WriteLine("--SendSuccess: " + connection_id);
            IClientProxy proxy = context.Clients.Client(connection_id);
            proxy.Invoke(M_SEND_SUCCESS);
        }
        //Receive Image Data
        private byte[] receiveImage(int image_Size)
        {
            TcpClient socket = listener.AcceptTcpClient();

            NetworkStream networkStream = socket.GetStream();
            byte[] buffer = new byte[2048];
            int bytes = 0;
            int bytesRead = 0;
            using (MemoryStream memStream = new MemoryStream())
            {
                while (bytesRead < image_Size)
                {
                    bytes = networkStream.Read(buffer, 0, buffer.Length);
                    memStream.Write(buffer, 0, bytes);
                    bytesRead += bytes;
                }
                memStream.Flush();
                socket.Close();
                return memStream.ToArray();
            }  
        }
        //Send Image Data
        private void sendImageData(object sender, DoWorkEventArgs e)
        {
            byte[] data = (byte[]) e.Argument;
            TcpClient socket = listener.AcceptTcpClient();
            NetworkStream ns = socket.GetStream();
            ns.Write(data, 0, data.Length);
            ns.Flush();
            socket.Close();
        }
    }
}
