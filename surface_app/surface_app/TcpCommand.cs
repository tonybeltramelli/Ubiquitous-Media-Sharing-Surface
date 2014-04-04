using System;
using System.IO;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using Newtonsoft.Json;

namespace dk.itu.spct.tcp
{
    public class TCPCommand
    {
        //Server-Client actions
        public enum actions
        {
            start,      // 0 - Register device
            end,        // 1 - Unregist device
            request,    // 2 - Request for shared images
            send,       // 3 - Send image to device
            success     // 4 - Notify success
        }
        //Attributes
        private Gallery gallery;

        public TCPCommand(){
            initialize();
        }

        //Implementation

        //Setup class
        private void initialize() {
            gallery = Gallery.Instance;
        }
        //Procces client request
        public void ProcessAction(TcpServerConnection conn, string m_text) {
            dynamic json = textToJsonObj(m_text);
            if (json.Count > 0) {
                dynamic item = json[0];
                switch ((actions)item.action){
                    
                    // [{'action':'0','tag_id':'0xAAA'}]
                    case actions.start:
                        Console.WriteLine("--Device registered: {0}", (string)item.tag_id);
                        conn.Id = item.tag_id;
                        break;
                    
                    //[{'action':'1'}]
                    case actions.end:
                        Console.WriteLine("--Device disconnected: {0}", conn.Id);
                        conn.forceDisconnect();
                        gallery.removeDevice(conn.Id);
                        break;
                    
                    //[{'action':'3','name':'image1','size':'Bytes'}]
                    case actions.send:
                        
                        string file_name = (string)item.name;
                        int image_size = (int)item.size;

                        Console.WriteLine("--Recieve image '{0}' from '{1}' of size '{2}'", file_name, conn.Id, image_size);

                        byte[] data = conn.processImage(image_size);

                        Image img = new Image(file_name, data);
                        img.AddOwner(conn.Id);
                        gallery.AddImage(img);

                        sendSuccessNotification(conn, img.File_Name);

                        //TODO Remove: Testing purpose
                        //img.Bitmap.Save("../../images/copy_" + img.File_Name);

                        break;

                    //[{'action':'4'}]
                    case actions.success:
                        conn.waitForSuccess = false;
                        break;
                    
                    default:
                        Console.WriteLine("Unrecognized action!");
                        break;
                }
            }
        }
        //Request gallery image to client
        //[{'action':'2'}]
        public void requestGallery(TcpServerConnection conn){
            string data = @"[{'action':'" + actions.request + @"'}]";
            conn.sendData(new Message(data));
            Console.WriteLine("--Message sent to {0}: {1}", conn.Id, data);
        }
        //Generate JSON object from text
        //[{'action':'3'},{'file_name':'image1','data':'image1'}]
        public void sendImage(TcpServerConnection conn, int tag_id, Image img) {
            foreach (int owner in img.Owners) {
                if (owner.Equals(tag_id)) { return; }
            }

            byte[] bytes = img.ByteArray();
            string text = @"[{'action':'" + actions.send + @"','name':'" + img.File_Name + @"','size':'" + bytes.Length + @"'}]";

            Message message = new Message(text);
            message.data = bytes;
            conn.sendData(message);
            
            img.AddOwner(tag_id);

            Console.WriteLine("--Image sent to {0}: {1}", conn.Id, img.File_Name);
        }
        //Image transfered successfuly notificaiton
        public void sendSuccessNotification(TcpServerConnection conn, string message) {
            string text = @"[{'action':'" + actions.success + @"','message':'" + message + @"'}]";
            conn.sendData(new Message(text));
        }
        private dynamic textToJsonObj(string text) {
            return Newtonsoft.Json.JsonConvert.DeserializeObject(text);
        }
    }
}