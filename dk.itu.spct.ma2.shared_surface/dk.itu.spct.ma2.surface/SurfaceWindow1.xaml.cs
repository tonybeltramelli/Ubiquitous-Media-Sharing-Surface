using System;
using System.Windows;
using Microsoft.Surface.Presentation.Controls;

using dk.itu.spct.common;
using dk.itu.spct.tcp;
using System.Windows.Input;

namespace dk.itu.spct.ma2.surface
{
    /// <summary>
    /// Interaction logic for SurfaceWindow1.xaml
    /// </summary>
    public partial class SurfaceWindow1 : SurfaceWindow
    {
        private EventData dataObj;

        /// <summary>
        /// Default constructor.
        /// </summary>
        public SurfaceWindow1() {
            InitializeComponent();
            scatter.ItemsSource = Gallery.Instance.Images;
            TcpServer.Instance.Start();
            dataObj = new EventData();
        }

        /// <summary>
        /// Occurs when the window is about to close. 
        /// </summary>
        /// <param name="e"></param>
        protected override void OnClosed(EventArgs e) {
            base.OnClosed(e);
            TcpServer.Instance.Stop();
        }
        // Get the current mouse position
        private void Img_TouchDown(object sender, System.Windows.Input.TouchEventArgs e) {
            dataObj.touch = e.GetTouchPoint(null);
        }
        // Image touch release
        private void Img_TouchUp(object sender, System.Windows.Input.TouchEventArgs e) {
            var va = FindCommonVisualAncestor((DependencyObject)e.OriginalSource);
            ScatterViewItem svi = e.OriginalSource as ScatterViewItem;
            ImageObject img = ((ImageObject)svi.Content);
            if(img != null){
                dataObj.Img_id = img.Id;
                dataObj.file_name = ((ImageObject)svi.Content).File_Name;
                DataObject da = new DataObject("data", dataObj);
                DragDrop.DoDragDrop(va, da, DragDropEffects.Move);
            }
        }
        //Container for data sent between events
        public class EventData
        {
            public int Img_id;
            public string file_name;
            public TouchPoint touch;
        }
    }
}