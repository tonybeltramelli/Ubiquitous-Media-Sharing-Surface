using System;

using dk.itu.spct.common;

namespace dk.itu.spct.tcp
{
    public class TCPCommand
    {
        //Server-Client actions
        public enum actions
        {
            start,      
            request,
            send,
            success
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
                        Console.WriteLine("-Device registered: {0}", (string)item.tag_id);
                        conn.Id = item.tag_id;
                        break;
                    
                    //[{'action':'3','name':'image1','size':'Bytes'}]
                    case actions.send:
                        
                        string file_name = (string)item.name;
                        int image_size = (int)item.size;

                        Console.WriteLine("-Recieve image '{0}' from '{1}' of size '{2}'", file_name, conn.Id, image_size);

                        byte[] data = conn.processImage(image_size);

                        ImageObject img = new ImageObject(file_name, data);
                        img.AddOwner(conn.Id);
                        gallery.AddImage(img);

                        sendSuccessNotification(conn, img.File_Name);
                        break;

                    //[{'action':'4'}]
                    case actions.success:
                        Console.WriteLine("-Success received from '{0}'", conn.Id);
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
            string data = @"[{""action"":""" + actions.request + @"""}]";
            conn.sendData(new Message(data));
            Console.WriteLine("-Message sent to {0}: {1}", conn.Id, data);
        }
        //Generate JSON object from text
        //[{'action':'3'},{'file_name':'image1','data':'image1'}]
        public void sendImage(TcpServerConnection conn, int tag_id, int image_id) {
            ImageObject img = null;
            foreach(ImageObject img_tmp in Gallery.Instance.Images){
                if (image_id == img_tmp.Id) {
                    img = img_tmp;
                    break;
                }
            }
            if (img != null) {
                if (img.Owners.Contains(tag_id)) {
                    return;
                }
            } else {
                return;
            }

            byte[] bytes = img.ByteArray();

            string text = @"[{""action"":""" + actions.send + @""",""name"":""" + img.File_Name + @""",""size"":""" + bytes.Length + @"""}]";

            Message message = new Message(text);
            message.data = bytes;
            conn.sendData(message);

            Gallery.Instance.Images.Remove(img);
            img.AddOwner(tag_id);
            Gallery.Instance.Images.Add(img);

            Console.WriteLine("-Image sent to {0}: {1} size {2}", conn.Id, img.File_Name,bytes.Length);
        }
        //Image transfered successfuly notificaiton
        public void sendSuccessNotification(TcpServerConnection conn, string message) {
            string text = @"[{""action"":""" + actions.success + @""",""messag"":""" + message + @"""}]";
            conn.sendData(new Message(text));
        }
        private dynamic textToJsonObj(string text) {
            return Newtonsoft.Json.JsonConvert.DeserializeObject(text);
        }
    }
}