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
        public ScatterView scatter;

        private Gallery gallery;

        private ObservableCollection<string> images;

        private const int DragThreshold = 15;
        
        // List to store the input devices those do not need do the dragging check.
        private List<InputDevice> ignoredDeviceList = new List<InputDevice>();

        // Flag to indicate whether tags are supported on the current hardware.
        private static readonly bool areTagsSupported = InteractiveSurface.PrimarySurfaceDevice.IsTagRecognitionSupported;


        /// <summary>
        /// Default constructor.
        /// </summary>
        public MainWindow()
        {
            // Add handlers for window availability events.
            AddWindowAvailabilityHandlers();


            TestImage testImg = new TestImage("\\Resources\\Koala.jpg", "tag");
            images = new ObservableCollection<string>();
            images.Add("\\Resources\\Koala.jpg");

            scatter.ItemsSource = "/Resources"; //Gallery.Instance.Images;
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


#region Visualization Events
        /// <summary>
        /// Handles the Visualation Added Event for the TagVisualizer.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void OnVisualizationAdded(object sender, TagVisualizerEventArgs e)
        {
            //CardValidationPanel panel = e.TagVisualization as CardValidationPanel;
            //if (panel != null)
            //{
            //    panel.IdentityValidated += new EventHandler<IdentityValidatedEventArgs>(OnIdentityValidated);
            //}
        }
        
        /// <summary>
        /// Occurs once the user has successfully validated their identity.
        /// </summary>
        private void OnIdentityValidated(object sender, IdentityValidatedEventArgs e)
        {
            ScatterViewItem scatterViewItem = new ScatterViewItem();

            // LibraryStack does not need to be dragged.
            DragDropScatterView.SetAllowDrag(scatterViewItem, false);

            if (e != null)
            {
                scatterViewItem.Center = e.ValidationCenter;
                scatterViewItem.Orientation = e.ValidationOrientation;
            }
            scatterViewItem.Style = (Style)FindResource("StackScatterViewItemStyle");
            scatterViewItem.Content = new LibraryStack();
            ScatterLayer.Items.Add(scatterViewItem);
        }

#endregion

#region ShoppingList Drag Drop Code

        /// <summary>
        /// Handles the PreviewTouchDown event for the ShoppingList.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void OnImagePreviewTouchDown(object sender, TouchEventArgs e)
        {
            ignoredDeviceList.Remove(e.Device);
            InputDeviceHelper.ClearDeviceState(e.Device);

            InputDeviceHelper.InitializeDeviceState(e.Device);
        }

        /// <summary>
        /// Handles the PreviewTouchMove event for the ShoppingList.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void OnImagePreviewTouchMove(object sender, TouchEventArgs e)
        {
            // If this is a touch device whose state has been initialized when its down event happens
            if (InputDeviceHelper.GetDragSource(e.Device) != null)
            {
                StartDragDrop(scatter, e);
            }
        }

        /// <summary>
        /// Handles the PreviewTouchUp event for the ShoppingList.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void OnImagePreviewTouchUp(object sender, TouchEventArgs e)
        {
            ignoredDeviceList.Remove(e.Device);
            InputDeviceHelper.ClearDeviceState(e.Device);
        }

        /// <summary>
        /// Handles the PreviewMouseLeftButtonDown event for the ShoppingList.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void OnImagePreviewMouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            ignoredDeviceList.Remove(e.Device);
            InputDeviceHelper.ClearDeviceState(e.Device);

            InputDeviceHelper.InitializeDeviceState(e.Device);
        }

        /// <summary>
        /// Handles the PreviewMouseMove event for the ShoppingList.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void OnImagePreviewMouseMove(object sender, MouseEventArgs e)
        {
            // If this is a mouse whose state has been initialized when its down event happens
            if (InputDeviceHelper.GetDragSource(e.Device) != null)
            {
                StartDragDrop(scatter, e);
            }
        }

        /// <summary>
        /// Handles the PreviewMouseLeftButtonUp event for the ShoppingList.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void OnImagePreviewMouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {
            ignoredDeviceList.Remove(e.MouseDevice);
            InputDeviceHelper.ClearDeviceState(e.Device);
        }

        private void OnImageDragCanceled(object sender, SurfaceDragDropEventArgs e)
        {
            XmlElement draggingData = (XmlElement)e.Cursor.Data;
            if (draggingData != null)
            {
                ResetListBoxItem(draggingData);
            }
        }

        private void OnImageDragCompleted(object sender, SurfaceDragCompletedEventArgs e)
        {
            XmlElement draggingData = (XmlElement)e.Cursor.Data;
            if (draggingData != null)
            {
                ResetListBoxItem(draggingData);
            }
        }

        private void ResetListBoxItem(XmlElement itemData)
        {
            //SurfaceListBoxItem sourceListBoxItem = null;
            //foreach (object item in scatter.)
            //{
            //    if (((XmlElement)item).OuterXml == itemData.OuterXml)
            //    {
            //        sourceListBoxItem = PhonePin.ItemContainerGenerator.ContainerFromItem(item) as SurfaceListBoxItem;
            //    }
            //}

            //if (sourceListBoxItem != null)
            //{
            //    sourceListBoxItem.Opacity = 1.0;
            //}
        }

        /// <summary>
        /// Try to start Drag-and-drop for a listBox.
        /// </summary>
        /// <param name="sourceListBox"></param>
        /// <param name="e"></param>
        private void StartDragDrop(ScatterView sourceListBox, InputEventArgs e)
        {
            // Check whether the input device is in the ignore list.
            if (ignoredDeviceList.Contains(e.Device))
            {
                return;
            }

            InputDeviceHelper.InitializeDeviceState(e.Device);

            Vector draggedDelta = InputDeviceHelper.DraggedDelta(e.Device, (UIElement)sourceListBox);

            // If this input device has moved more than Threshold pixels horizontally,
            // put it to the ignore list and never try to start drag-and-drop with it.
            if (Math.Abs(draggedDelta.X) > DragThreshold)
            {
                ignoredDeviceList.Add(e.Device);
                return;
            }

            // If this input device has moved less than Threshold pixels vertically 
            // then this is not a drag-and-drop yet.
            if (Math.Abs(draggedDelta.Y) < DragThreshold)
            {
                return;
            }

            ignoredDeviceList.Add(e.Device);

            // try to start drag-and-drop,
            // verify that the cursor the input device was placed at is a ListBoxItem
            DependencyObject downSource = InputDeviceHelper.GetDragSource(e.Device);
            Debug.Assert(downSource != null);

            SurfaceListBoxItem draggedListBoxItem = GetVisualAncestor<SurfaceListBoxItem>(downSource);
            if (draggedListBoxItem == null)
            {
                return;
            }

            // Get Xml source.
            XmlElement data = draggedListBoxItem.Content as XmlElement;

            // Data should be copied, because the Stack rejects data of the same instance.
            data = data.Clone() as XmlElement;

            // Create a new ScatterViewItem as cursor visual.
            ScatterViewItem cursorVisual = new ScatterViewItem();
            cursorVisual.Style = (Style)FindResource("ScatterItemStyle");
            cursorVisual.Content = data;

            IEnumerable<InputDevice> devices = null;

            TouchEventArgs touchEventArgs = e as TouchEventArgs;
            if (touchEventArgs != null)
            {
                devices = MergeInputDevices(draggedListBoxItem.TouchesCapturedWithin, e.Device);
            }
            else
            {
                devices = new List<InputDevice>(new InputDevice[] { e.Device });
            }

            SurfaceDragCursor cursor = SurfaceDragDrop.BeginDragDrop(scatter, draggedListBoxItem, cursorVisual, data, devices, DragDropEffects.Copy);

            if (cursor == null)
            {
                return;
            }

            // Reset the input device's state.
            InputDeviceHelper.ClearDeviceState(e.Device);
            ignoredDeviceList.Remove(e.Device);

            draggedListBoxItem.Opacity = 0.5;
            e.Handled = true;
        }

        /// <summary>
        /// Attempts to get an ancestor of the passed-in element with the given type.
        /// </summary>
        /// <typeparam name="T">Type of ancestor to search for.</typeparam>
        /// <param name="descendent">Element whose ancestor to find.</param>
        /// <param name="ancestor">Returned ancestor or null if none found.</param>
        /// <returns>True if found, false otherwise.</returns>
        private static T GetVisualAncestor<T>(DependencyObject descendent) where T : class
        {
            T ancestor = null;
            DependencyObject scan = descendent;
            ancestor = null;

            while (scan != null && ((ancestor = scan as T) == null))
            {
                scan = VisualTreeHelper.GetParent(scan);
            }

            return ancestor;
        }

        /// <summary>
        /// Merges the remaining input devices on the drag source besides the inpout device that is down.
        /// </summary>
        /// <param name="existingInputDevices"></param>
        /// <param name="extraInputDevice"></param>
        /// <returns></returns>
        private static IEnumerable<InputDevice> MergeInputDevices(IEnumerable<TouchDevice> existingInputDevices, InputDevice extraInputDevice)
        {
            var result = new List<InputDevice> { extraInputDevice };

            foreach (InputDevice inputDevice in existingInputDevices)
            {
                if (inputDevice != extraInputDevice)
                {
                    result.Add(inputDevice);
                }
            }

            return result;
        }

#endregion


        #region RemovePhonePin

        public static readonly RoutedUICommand RemovePhonePin =
            new RoutedUICommand("_Checkout", "Checkout", typeof(MainWindow), null);

        /// <summary>
        /// Execute the Checkout command.
        /// </summary>
        /// <param name="target"></param>
        /// <param name="args"></param>
        private static void OnExecuteCheckoutCommand(object target, ExecutedRoutedEventArgs args)
        {
            MainWindow mainWindow = target as MainWindow;
            ScatterViewItem sourceCommandItem = args.Source as ScatterViewItem;

            if (mainWindow == null || sourceCommandItem == null)
            {
                return;
            }

            mainWindow.ScatterLayer.Items.Remove(sourceCommandItem);

            if (!areTagsSupported)
            {
                // DEVELOPER: In this case, tags aren't supported. If you are truly using identity validation,
                // you probably want to take another course of action here. For the purposes of this sample,
                // we'll just skip validation and allow shopping/checkout.
                mainWindow.OnIdentityValidated(null, null);
            }
        }

        #endregion

    } // END MainWindow

    
    
    /// <summary>
    /// Template selector class for the items in the ShoppingList.
    /// </summary>
    public class ShoppingListTemplateSelector : DataTemplateSelector
    {
        /// <summary>
        /// Template for the item selected as the starting item.
        /// </summary>
        public DataTemplate StartingItemTemplate { get; set; }

        /// <summary>
        /// Template for items that are not the starting item.
        /// </summary>
        public DataTemplate NormalItemTemplate { get; set; }

        /// <summary>
        /// Selects data templates for items in the ShoppingList.
        /// If an item has the content of "Age of Empires 3", which is the first item in the item list, the StartingItemTemplate will be used.
        /// Otherwise, the NormalItemTemplate will be used.
        /// </summary>
        /// <param name="item"></param>
        /// <param name="container"></param>
        /// <returns></returns>
        public override DataTemplate SelectTemplate(object item, DependencyObject container)
        {
            XmlElement data = item as XmlElement;
            return (data != null && data.GetAttribute("Name") == "Age Of Empires 3") ? StartingItemTemplate : NormalItemTemplate;
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