using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Media;
using Microsoft.Surface.Presentation.Controls;
using System.Windows.Media.Imaging;

namespace surface_app
{
    class PhonePin
    {
        #region Private Fields

        // tag ID
        private int tagID;

        // does this instance survive after the physical device is removed?
        private bool persistent = false;

        // size and shape of the phone place-holder
        private Geometry pinSahpe;
        
        // position of the phone relative to the table
        private Vector offset;
        
        // phone's image gallery
        // private HashSet<BitmapImage> gallery; 

        #endregion

        #region Initialization

        public PhonePin(int tag)
        {
            this.tagID = tag;
        }

        #endregion

        #region Public Methods

        public void refreshGallery()
        {
            // TODO: 
            // loop through all the gallery from Daniel
            // do something with each image that has ownerID == this.tagID
        }

        public bool isPersistent()
        {
            return persistent;
        }

        public void makePersistent()
        {
            persistent = true;
        }

        public Vector Offset
        {
            get
            {
                return offset;
            }
        }

        #endregion
    }
}
