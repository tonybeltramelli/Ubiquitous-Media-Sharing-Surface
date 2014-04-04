using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Media;
// using dk.itu.spct.Gallery;
using dk.itu.spct;

namespace surface_app
{
    class DevicesManager
    {
        #region Private Memebers
        // collection of registered devices
        private Dictionary<int, PhonePin> activeMembers;

        private Gallery commonGallery;

        #endregion

        #region Initialization

        public DevicesManager()
        {

        }

        #endregion

        #region Public Methods

        public void addActiveMember(int tagID)
        {
            PhonePin device = new PhonePin(tagID);
            activeMembers.Add(tagID, device);
        }

        public void removedPhysicalDevice(int tagID)
        {
            if(! activeMembers[tagID].isPersistent())
            {
                activeMembers.Remove(tagID);
            }
        }

        public void makePhonePinPersistent(int tagID)
        {
            activeMembers[tagID].makePersistent();
        }

        public PhonePin getActiveMember(int tagID)
        {
            return activeMembers[tagID];
        }

        public Dictionary<int, PhonePin> ActiveMembers
        {
            get
            {
                return activeMembers;
            }
        }

        //public void addImageToDevice(int tagID, Image img)
        //{
            // add the image to the phonePin gallery
          //  activeMembers[tagID].refreshGallery();
            // send image through the network
        //}

        public void addImageToDevice(int tagID, int imgID)
        {
            // add the image to the phonePin gallery
            activeMembers[tagID].refreshGallery();
            // loop through the gallery and find the img with imgID

                // send the instance to Daniel
        }

        public void updatedGallery()
        {
            // a new user's gallery has been added, so loop through the entire thing
            foreach (Image img in commonGallery.Images)
            {

            }

        }

        #endregion

        #region Private Methods

        #endregion

    }
}
