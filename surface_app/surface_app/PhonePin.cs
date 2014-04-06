using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Media;
using Microsoft.Surface.Presentation;
using Microsoft.Surface.Presentation.Controls;
using System.Windows.Media.Imaging;
using System.Collections.ObjectModel;
namespace surface_app
{
    class PhonePin : ScatterView
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
            // Change Background to transparent, or Drag Drop hit test will by pass it if its Background is null.
            Background = Brushes.Transparent;
            AllowDrop = true;

            Loaded += new RoutedEventHandler(OnLoaded);
            Unloaded += new RoutedEventHandler(OnUnloaded);

            this.tagID = tag;
        }

        #endregion

        #region Public Methods

        /// <summary>
        /// Property to identify whether the ScatterViewItem can be dragged.
        /// </summary>
        public static readonly DependencyProperty AllowDragProperty = DependencyProperty.Register(
            "AllowDrag",
            typeof(bool),
            typeof(PhonePin),
            new PropertyMetadata(true/*defaultValue*/));

        /// <summary>
        /// Getter of AllowDrag AttachProperty.
        /// </summary>
        /// <param name="element"></param>
        /// <returns></returns>
        public static bool GetAllowDrag(DependencyObject element)
        {
            if (!(element is ScatterViewItem))
            {
                throw new InvalidOperationException(Properties.Resources.AllowDragOnlyOnScatterViewItem);
            }

            return (bool)element.GetValue(AllowDragProperty);
        }

        /// <summary>
        /// Setter of AllowDrag AttachProperty.
        /// </summary>
        /// <param name="element"></param>
        /// <param name="value"></param>
        public static void SetAllowDrag(DependencyObject element, bool value)
        {
            if (!(element is ScatterViewItem))
            {
                throw new InvalidOperationException(Properties.Resources.AllowDragOnlyOnScatterViewItem);
            }

            element.SetValue(AllowDragProperty, value);
        }


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

        #region Private Methods

        private void OnLoaded(object sender, RoutedEventArgs e)
        {
            SurfaceDragDrop.AddDropHandler(this, OnCursorDrop);
            AddHandler(ScatterViewItem.ContainerManipulationStartedEvent, new ContainerManipulationStartedEventHandler(OnManipulationStarted));
        }

        private void OnUnloaded(object sender, RoutedEventArgs e)
        {
            SurfaceDragDrop.RemoveDropHandler(this, OnCursorDrop);
            RemoveHandler(ScatterViewItem.ContainerManipulationStartedEvent, new ContainerManipulationStartedEventHandler(OnManipulationStarted));
        }

        private void OnManipulationStarted(object sender, RoutedEventArgs args)
        {
            ScatterViewItem svi = args.OriginalSource as ScatterViewItem;
            if (svi != null && PhonePin.GetAllowDrag(svi))
            {
                svi.BeginDragDrop(svi.DataContext);
            }
        }

        private void OnCursorDrop(object sender, SurfaceDragDropEventArgs args)
        {
            SurfaceDragCursor droppingCursor = args.Cursor;

            // Add dropping Item that was from another drag source.
            if (droppingCursor.CurrentTarget == this && droppingCursor.DragSource != this)
            {
                if (!Items.Contains(droppingCursor.Data))
                {
                    Items.Add(droppingCursor.Data);

                    var svi = ItemContainerGenerator.ContainerFromItem(droppingCursor.Data) as ScatterViewItem;
                    if (svi != null)
                    {
                        svi.Center = droppingCursor.GetPosition(this);
                        svi.Orientation = droppingCursor.GetOrientation(this);
                        svi.Height = droppingCursor.Visual.ActualHeight;
                        svi.Width = droppingCursor.Visual.ActualWidth;
                        svi.SetRelativeZIndex(RelativeScatterViewZIndex.Topmost);
                    }
                }
            }
        }

        #endregion
    }
}
