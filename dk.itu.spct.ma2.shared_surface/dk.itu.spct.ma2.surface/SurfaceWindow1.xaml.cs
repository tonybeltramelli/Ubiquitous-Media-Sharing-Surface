using System;
using System.Windows;
using Microsoft.Surface.Presentation.Controls;

using dk.itu.spct.common;
using dk.itu.spct.signalr;
using Microsoft.Surface.Presentation;
using System.Windows.Media;
using System.Windows.Controls;
using System.Windows.Input;
using System.Collections.Generic;
using System.Drawing;
using System.ComponentModel;
using SignalRSelfHost;

namespace dk.itu.spct.ma2.surface
{
    /// <summary>
    /// Interaction logic for SurfaceWindow1.xaml
    /// </summary>
    public partial class SurfaceWindow1 : SurfaceWindow
    {

        private BackgroundWorker _backgroundWorker = new BackgroundWorker();    //Server background worker

        /// <summary>
        /// Default constructor.
        /// </summary>
        public SurfaceWindow1() {
            InitializeComponent();
            scatter.ItemsSource = Gallery.Instance.Images;

            //Start server
            _backgroundWorker.DoWork += _backgroundWorker_DoWork;
            _backgroundWorker.RunWorkerCompleted += _backgroundWorker_Completed;
            _backgroundWorker.RunWorkerAsync();
        }

        //Run server in a separate thread
        public void _backgroundWorker_DoWork(object sender, DoWorkEventArgs e) { new SRServer(); }
        //Server stop event
        private void _backgroundWorker_Completed(object sender, RunWorkerCompletedEventArgs e) { Console.WriteLine("--Server stopper"); }

        /// <summary>
        /// Occurs when the window is about to close. 
        /// </summary>
        /// <param name="e"></param>
        protected override void OnClosed(EventArgs e) {
            base.OnClosed(e);
        }

        private void Touch_Down(object sender, System.Windows.Input.TouchEventArgs e)
        {
            FrameworkElement findSource = e.OriginalSource as FrameworkElement;
            ScatterViewItem draggedElement = null;

            // Find the ScatterViewItem object that is being touched.
            while (draggedElement == null && findSource != null) {
                if ((draggedElement = findSource as ScatterViewItem) == null) {
                    findSource = VisualTreeHelper.GetParent(findSource) as FrameworkElement;
                }
            }

            if (draggedElement == null) {
                return;
            }

            ImageObject data = draggedElement.Content as ImageObject;
            if (data == null) {
                return;
            }

            // Set the dragged element. This is needed in case the drag operation is canceled.
            data.DraggedElement = draggedElement;

            ContentControl cursorVisual = new ContentControl() {
                Content = draggedElement.DataContext,
                Style = FindResource("CursorStyle") as Style
            };

            // Create a list of input devices, and add the device passed to this event handler.
            List<InputDevice> devices = new List<InputDevice>();
            devices.Add(e.Device);

            // If there are touch devices captured within the element, add them to the list of input devices.
            foreach (InputDevice device in draggedElement.TouchesCapturedWithin) {
                if (device != e.Device) {
                    devices.Add(device);
                }
            }

            // Get the drag source object.
            ItemsControl dragSource = ItemsControl.ItemsControlFromItemContainer(draggedElement);

            // Start the drag-and-drop operation.
            SurfaceDragCursor cursor =
                SurfaceDragDrop.BeginDragDrop(
                  dragSource,                   // The ScatterView object that the cursor is dragged out from.
                  draggedElement,               // The ScatterViewItem object that is dragged from the drag source.
                  cursorVisual,                 // The visual element of the cursor.
                  draggedElement.DataContext,   // The data attached with the cursor.
                  devices,                      // The input devices that start dragging the cursor.
                  DragDropEffects.Move);        // The allowed drag-and-drop effects of the operation.

            // If the cursor was created, the drag-and-drop operation was successfully started.
            if (cursor != null) {
                // Hide the ScatterViewItem.
                draggedElement.Visibility = Visibility.Hidden;
                // This event has been handled.
                e.Handled = true;
            }
        }

        private void DragCanceled(object sender, SurfaceDragDropEventArgs e) {
            ImageObject data = e.Cursor.Data as ImageObject;
            ScatterViewItem item = data.DraggedElement as ScatterViewItem;
            if (item != null) {
                item.Visibility = Visibility.Visible;
                item.Orientation = e.Cursor.GetOrientation(this);
                item.Center = e.Cursor.GetPosition(this);
            }
        }

    }
}