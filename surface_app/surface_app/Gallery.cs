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
        public void RemoveImage(Image img) {
            lock (images) {
                images.Remove(img);
            }
        }
        //Remove owner from images
        public void removeDevice(string tag_id) {
            foreach (Image img in images) {
                img.RemoveOwner(tag_id);
            }
        }
        //Destroy gallery
        public void destroy () {
            foreach (Image img in images) {
                foreach(string owner in img.Owners)
                img.RemoveOwner(owner);
            }
            lock(images){
                images.Clear();
            }
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
        public void RemoveOwner(string tag_id) {
            for (int i = 0; i < owners.Count; i++) {
                if (owners[i].Equals(tag_id)) {
                    lock (owners){
                        owners.RemoveAt(i);
                    }
                    if (owners.Count == 0) {
                        Gallery.Instance.RemoveImage(this);
                    }
                    return;
                }
            }
        }
        //Get bitmap byte array
        public byte[] ByteArray() {
            byte[] data;
            ImageConverter converter = new ImageConverter();
            data = (byte[])converter.ConvertTo(Bitmap, typeof(byte[]));
            Console.WriteLine(data.Length);
            return data;
        }
        //Save image on hard drive
        private Bitmap genBitmap(byte[] data) {
            TypeConverter tc = TypeDescriptor.GetConverter(typeof(Bitmap));
            return (Bitmap)tc.ConvertFrom(data);
        }
    }
}