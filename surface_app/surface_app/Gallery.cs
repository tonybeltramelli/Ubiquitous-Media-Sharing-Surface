﻿using System;
using System.Drawing;
using System.ComponentModel;
using System.Collections.Generic;
using System.Collections.ObjectModel;

namespace dk.itu.spct
{
    //Gallery representation
    public class Gallery{

        private ObservableCollection<Image> images;

        //Get-Set
        public ObservableCollection<Image> Images
        {
            get {
                lock (images) {
                    return new ObservableCollection<Image>(images); 
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
            images = new ObservableCollection<Image>();
        }
        //Add image
        public void AddImage(Image img) {
            lock (images) {
                images.Add(img);
            }
        }
        //Remove owner from images
        public void removeDevice(int tag_id){
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

        private HashSet<int> owners;

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
        public HashSet<int> Owners {
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

        public Image(string fileName, Bitmap bitMap)
        {
            initialize();
            m_file_name = fileName;
            m_bitmap = bitMap;
        }
        //Class implementation
        
        //Setup image
        private void initialize() {
            owners = new HashSet<int>();
        }
        //Add device owner to current image
        public void AddOwner(int tag_id) {
            lock (owners) {
                owners.Add(tag_id);
            }
        }
        //Remove device owner from image
        public int RemoveOwner(int tag_id)
        {
            lock(owners){
                owners.RemoveWhere(id => id == tag_id);
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