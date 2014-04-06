using System;
using Microsoft.Surface.Presentation.Controls;

using dk.itu.spct.common;
using dk.itu.spct.tcp;

namespace dk.itu.spct.ma2.surface
{
    /// <summary>
    /// Interaction logic for SurfaceWindow1.xaml
    /// </summary>
    public partial class SurfaceWindow1 : SurfaceWindow
    {

        private TcpServer server;
        /// <summary>
        /// Default constructor.
        /// </summary>
        public SurfaceWindow1() {
            InitializeComponent();
            scatter.ItemsSource = Gallery.Instance.Images;
            server = new TcpServer();
            server.Start();
        }

        public void Request(Object sender, EventArgs e) {
            server.requestDeviceGallery(1);
        }

        /// <summary>
        /// Occurs when the window is about to close. 
        /// </summary>
        /// <param name="e"></param>
        protected override void OnClosed(EventArgs e) {
            base.OnClosed(e);
            server.Stop();
        }
    }
}