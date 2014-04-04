using System;
using System.Drawing;
using System.ComponentModel;
using System.Collections.Generic;

namespace dk.itu.spct
{
    //Gallery representation
    public class Gallery{

        private List<Image> images;

        //Get-Set
        public List<Image> Images{
            get {
                lock (images) {
                    return new List <Image> (images) ; 
                }
            }
        }

        //Singleton implementation
        private static Gallery instance;
        private Gallery() { initialize(); }
        public static Gallery Instance{
            get {
                if (instance == null){
                instance = new Gallery();
                }
                return instance;
            }
        }

        //Class implementation
        
        //Setup gallery
        private void initialize() {
            images = new List<Image>();
        }
        //Add image
        public void AddImage(Image img) {
            lock (images) {
                images.Add(img);
            }
        }
        //Remove owner from images
        public void removeDevice(string tag_id){
            lock (images) {
                foreach (Image img in images) {
                    if (img.RemoveOwner(tag_id) == 0) {
                        images.Remove(img);
                    }
                }
            }
        }
        //Destroy gallery
        public void destroy () {
            instance = null;
        }
    }
    
    //Image representation
    public class Image{

        private string m_file_name;
        private Bitmap m_bitmap;

        private List<string> owners;

        //GET-SET
        public string File_Name {
            get {
                return m_file_name;
            }
        }
        public Bitmap Bitmap {
            get{
                return m_bitmap;
            }
        }
        public List<string> Owners {
            get {
                return owners;
            }
        }

        //Create image <Image file name, data as string>
        public Image(string file_name, byte[] data) {
            if (!String.IsNullOrEmpty(file_name) && data.Length > 0){
                initialize();
                m_file_name = file_name;
                m_bitmap = genBitmap(data);
            }
        }

        //Class implementation
        
        //Setup image
        private void initialize() {
            owners = new List<string>();
        }
        //Add device owner to current image
        public void AddOwner(string tag_id) {
            if (!String.IsNullOrEmpty(tag_id)) {
                lock (owners) {
                    owners.Add(tag_id);
                }
            }
        }
        //Remove device owner from image
        public int RemoveOwner(string tag_id) {
            for (int i = 0; i < owners.Count; i++) {
                if (owners[i].Equals(tag_id)) {
                    lock (owners) {
                        owners.RemoveAt(i);
                    }
                }
            }
            return owners.Count;
        }
        //Get bitmap byte array
        public byte[] ByteArray() {
            ImageConverter converter = new ImageConverter();
            return (byte[])converter.ConvertTo(Bitmap, typeof(byte[]));
        }
        //Save image on hard drive
        private Bitmap genBitmap(byte[] data) {
            TypeConverter tc = TypeDescriptor.GetConverter(typeof(Bitmap));
            return (Bitmap)tc.ConvertFrom(data);
        }
    }
}