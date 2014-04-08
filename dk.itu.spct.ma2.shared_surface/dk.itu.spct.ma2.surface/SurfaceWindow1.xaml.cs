using System;
using System.Windows;
using Microsoft.Surface;
using Microsoft.Surface.Presentation.Controls;
using Microsoft.Surface.Presentation.Input;

using dk.itu.spct.common;
using dk.itu.spct.tcp;
using System.Windows.Controls;
using Microsoft.Surface.Presentation;
using System.Windows.Media;
using System.Drawing;

namespace dk.itu.spct.ma2.surface
{
    /// <summary>
    /// Interaction logic for SurfaceWindow1.xaml
    /// </summary>
    public partial class SurfaceWindow1 : SurfaceWindow
    {
        /// <summary>
        /// Default constructor.
        /// </summary>
        public SurfaceWindow1() {
            InitializeComponent();
            scatter.ItemsSource = Gallery.Instance.Images;
            TcpServer.Instance.Start();
        }

        /// <summary>
        /// Occurs when the window is about to close. 
        /// </summary>
        /// <param name="e"></param>
        protected override void OnClosed(EventArgs e) {
            base.OnClosed(e);
            TcpServer.Instance.Stop();
        }

        private void Img_TouchMove(object sender, System.Windows.Input.TouchEventArgs e) {
            var va = FindCommonVisualAncestor((DependencyObject)e.OriginalSource);
            ScatterViewItem svi = e.OriginalSource as ScatterViewItem;
            ImageObject img = (ImageObject)svi.Content;
            DataObject da = new DataObject("img_id", img.Id);
            DragDrop.DoDragDrop(va, da, DragDropEffects.Move);
        }

        //TODO Remove
        private void Img_PreviewMouseMove(object sender, System.Windows.Input.MouseEventArgs e) {
            var va = FindCommonVisualAncestor((DependencyObject)e.OriginalSource);
            ScatterView sv = (ScatterView)sender;
            System.Windows.Controls.Image img = e.OriginalSource as System.Windows.Controls.Image;
            String id = ((Label)((Grid)VisualTreeHelper.GetParent(img)).FindName("img_id")).Content.ToString();
            DataObject da = new DataObject("img_id", id);
            DragDrop.DoDragDrop(va, da, DragDropEffects.Move);
        }
    }
}