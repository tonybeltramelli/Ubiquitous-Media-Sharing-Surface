using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using System.Xml;
using Microsoft.Surface;
using Microsoft.Surface.Presentation;
using Microsoft.Surface.Presentation.Controls;
using Microsoft.Surface.Presentation.Input;
using dk.itu.spct;
using System.Drawing;
using System.Collections.ObjectModel;

namespace surface_app
{
    /// <summary>
    /// Demonstrates infrastructure capabilities based on several Surface controls.
    /// </summary>
    public partial class MainWindow : SurfaceWindow
    {
        // Flag to indicate whether tags are supported on the current hardware.
        private static readonly bool areTagsSupported = InteractiveSurface.PrimarySurfaceDevice.IsTagRecognitionSupported;

        public ScatterView scatter;

        private Gallery gallery;

        private ObservableCollection<string> images;

        /// <summary>
        /// Default constructor.
        /// </summary>
        public MainWindow()
        {
            // Add handlers for window availability events.
            AddWindowAvailabilityHandlers();

            //Add Images into Sctter View 
            // scatter.ItemsSource = System.IO.Directory.GetFiles(@"C:\Users\Public\Pictures\Sample Pictures", "*.jpg");
            

            //System.Threading.Thread.Sleep(1000);

            TestImage testImg = new TestImage("\\Resources\\Koala.jpg", "tag");
            images.Add("\\Resources\\Koala.jpg");

            scatter.ItemsSource = "/Resources"; //Gallery.Instance.Images;
            
            // BitmapImage img = new BitmapImage(new Uri("\\Resources\\Koala.jpg", UriKind.Relative));
            // System.Windows.Controls.Image img = new System.Windows.Controls.Image();
            // img.Source = new Bitmap(new Uri("\\Resources\\Koala.jpg", UriKind.Relative));
            // dk.itu.spct.Image testImg = new dk.itu.spct.Image("Koala.jsp", img);
            // Gallery.Instance.AddImage("");

        }

        /// <summary>
        /// Occurs when the window is about to close.
        /// </summary>
        /// <param name="e"></param>
        protected override void OnClosed(EventArgs e)
        {
            base.OnClosed(e);

            // Remove handlers for window availability events.
            RemoveWindowAvailabilityHandlers();
        }



        /// <summary>
        /// Adds handlers for window availability events.
        /// </summary>
        private void AddWindowAvailabilityHandlers()
        {
            // Subscribe to surface window availability events.
            ApplicationServices.WindowInteractive += OnWindowInteractive;
            ApplicationServices.WindowNoninteractive += OnWindowNoninteractive;
            ApplicationServices.WindowUnavailable += OnWindowUnavailable;
        }

        /// <summary>
        /// Removes handlers for window availability events.
        /// </summary>
        private void RemoveWindowAvailabilityHandlers()
        {
            // Unsubscribe from surface window availability events.
            ApplicationServices.WindowInteractive -= OnWindowInteractive;
            ApplicationServices.WindowNoninteractive -= OnWindowNoninteractive;
            ApplicationServices.WindowUnavailable -= OnWindowUnavailable;
        }

        /// <summary>
        /// This is called when the user can interact with the application's window.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void OnWindowInteractive(object sender, EventArgs e)
        {
            //TODO: enable audio, animations here
        }

        /// <summary>
        /// This is called when the user can see but not interact with the application's window.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void OnWindowNoninteractive(object sender, EventArgs e)
        {
            //TODO: Disable audio here if it is enabled

            //TODO: optionally enable animations here
        }

        /// <summary>
        /// This is called when the application's window is not visible or interactive.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void OnWindowUnavailable(object sender, EventArgs e)
        {
            //TODO: disable audio, animations here
        }
    }

    public class TestImage
    {
        private string path;
        private string tag;

        public TestImage(string path, string tag)
        {
            this.path = path;
            this.tag = tag;
        }

    }
}