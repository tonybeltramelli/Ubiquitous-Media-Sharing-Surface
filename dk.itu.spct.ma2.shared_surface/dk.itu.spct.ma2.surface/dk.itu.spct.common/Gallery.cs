using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Windows;
using System.Windows.Media;
using System.Windows.Media.Imaging;

namespace dk.itu.spct.common
{
    //Gallery representation
    public class Gallery
    {
        private ObservableCollectionEx<ImageObject> images; //Gallery images
        private static Dictionary<int, string> colorchart;  //Colors assigned for clients tags

        //Get-Set
        public ObservableCollectionEx<ImageObject> Images {
            get {
                return images;
            }
        }
        public Dictionary<int, string>  Colorchart
        {
            get
            {
                return colorchart;
            }
        }

        //Singleton implementation
        private static Gallery instance;
        private Gallery() { initialize(); }
        public static Gallery Instance {
            get {
                if (instance == null) {
                    instance = new Gallery();
                }
                return instance;
            }
        }

        //Class implementation

        //Setup gallery
        private void initialize() {
            images = new ObservableCollectionEx<ImageObject>(new List<ImageObject>());
            colorchart = new Dictionary<int, string>();
        }
        //Assign random color to tag
        public void AssignColor(int tag_id){
            Random rand = new Random();
            byte[] colorBytes = new byte[3];
            rand.NextBytes(colorBytes);
            System.Windows.Media.Color randomColor = System.Windows.Media.Color.FromRgb(colorBytes[0], colorBytes[1], colorBytes[2]);
            colorchart.Add(tag_id, randomColor.ToString());
        }
        //Add image
        public void AddImage(ImageObject img) {
            lock (images) {
                images.Add(img);
            }
        }
        //Remove image
        public void removeImage(ImageObject img) {
            lock (images) {
                images.Remove(img);
            }
        }
        //Remove owner from images
        public void removeDevice(int tag_id) {
            lock (images) {
                List<ImageObject> toAdd = new List<ImageObject>();
                List<ImageObject> toRemove = new List<ImageObject>();
                foreach (ImageObject img in images) {
                    if (img.RemoveOwner(tag_id) > 0) {
                        toRemove.Add(img);
                        if (img.Owners.Count != 0) {
                            toAdd.Add(img);
                        }
                    }
                }

                //Update observable collection
                foreach (ImageObject img in toRemove)
                {
                    images.Remove(img);
                }
                foreach (ImageObject img in toAdd)
                {
                    images.Add(img);
                }

                colorchart.Remove(tag_id);
            }
        }
        //Destroy gallery
        public void destroy() {
            instance = null;
        }
    }

    //Image representation
    public class ImageObject
    {
        //Dynamic id
        static int m_count = 0;
        private int m_id;
        private string m_file_name;
        private Bitmap m_bitmap;
        private HashSet<int> owners;
        private HashSet<string> colors;

        //GET-SET
        public int Id {
            get {
                return m_id;
            }
        }
        public string File_Name {
            get {
                return m_file_name;
            }
        }
        public Bitmap Bitmap {
            get {
                return m_bitmap;
            }
        }
        public BitmapSource BitmapSource {
            get {
                return Imaging.CreateBitmapSourceFromBitmap(m_bitmap);
            }
        }
        public HashSet<int> Owners {
            get {
                return owners;
            }
        }
        public HashSet<String> Colors
        {
            get
            {
                return colors;
            }
        }
        public object DraggedElement {
            get;
            set;
        }

        //Create image <Image file name, data as string>
        public ImageObject(string file_name, byte[] data) {
            if (!String.IsNullOrEmpty(file_name) && data.Length > 0) {
                initialize();
                m_file_name = file_name;
                m_bitmap = genBitmap(data);
            }
        }
        public ImageObject(string fileName, Bitmap bitMap) {
            initialize();
            m_file_name = fileName;
            m_bitmap = bitMap;
        }

        //Class implementation

        //Setup image
        private void initialize() {
            owners = new HashSet<int>();
            colors = new HashSet<string>();
            m_count++;
            m_id = m_count;
        }
        //Add device owner to current image
        public void AddOwner(int tag_id) {

            string c = Gallery.Instance.Colorchart[tag_id];
            colors.Add(c);

            lock (owners) {
                owners.Add(tag_id);
            }
        }
        //Remove device owner from image
        public int RemoveOwner(int tag_id) {

            string c = Gallery.Instance.Colorchart[tag_id];
            colors.Remove(c);

            lock (owners) {
                return owners.RemoveWhere(id => id == tag_id);
            }
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

    public static class Imaging
    {
        public static BitmapSource CreateBitmapSourceFromBitmap(Bitmap bitmap) {
            if (bitmap == null)
                throw new ArgumentNullException("bitmap");

            return System.Windows.Interop.Imaging.CreateBitmapSourceFromHBitmap(
                bitmap.GetHbitmap(),
                IntPtr.Zero,
                Int32Rect.Empty,
                BitmapSizeOptions.FromEmptyOptions());
        }
    }

}