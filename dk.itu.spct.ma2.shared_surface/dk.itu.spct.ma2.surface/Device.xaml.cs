using System;
using System.Windows;
using System.Windows.Input;
using Microsoft.Surface.Presentation.Controls;

using dk.itu.spct.tcp;
using dk.itu.spct.ma2.surface;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Media.Animation;

namespace TaggingLabClass
{
    public partial class Device
    {

        private int m_tag_id = 0;
        private bool isDevicePinned = false;
        private bool isDevicePresent = false;

        public int Id {
            get {
                return m_tag_id;
            }
        }

        public Device() {
            InitializeComponent();
            GotTag += OnTagAddedOnTheSurface;
            LostTag += OnTagRemovedFromTheSurface;
        }

        void OnTagAddedOnTheSurface(object s, RoutedEventArgs e) {
            m_tag_id = (int)this.VisualizedTag.Value;
            tag_id.Content = m_tag_id;
            Visibility = Visibility.Visible;
            isDevicePresent = true;
            if (!isDevicePinned)
                TcpServer.Instance.requestDeviceGallery(m_tag_id);
        }

        void OnTagRemovedFromTheSurface(object s, RoutedEventArgs e) {
            isDevicePresent = false;
            if (!isDevicePinned) {
                TcpServer.Instance.DisconnectDevice(m_tag_id);
                TagRemovedBehavior = TagRemovedBehavior.Fade;
                Visibility = Visibility.Hidden;
            }
        }

        void OnPinButtonClicked(object sender, RoutedEventArgs e) {
            SurfaceButton button = (SurfaceButton)sender;
            if (TagRemovedBehavior == TagRemovedBehavior.Wait) {
                //phone was unpinned so remove everything
                button.Content = "Lock";
                isDevicePinned = false;
                if (!isDevicePresent) {
                    TcpServer.Instance.DisconnectDevice(m_tag_id);
                    TagRemovedBehavior = TagRemovedBehavior.Fade;
                    Visibility = Visibility.Hidden;
                }
            } else {
                //phone was pinned so do not close socket do not remove shit
                button.Content = "Unlock";
                isDevicePinned = true;
                TagRemovedBehavior = TagRemovedBehavior.Wait;
            }
        }

        //Drop image in device event
        private void OnDrop(object sender, DragEventArgs e) {
            if (e.Data.GetDataPresent("data")) {
                SurfaceWindow1.EventData data = (SurfaceWindow1.EventData)e.Data.GetData("data");
                TcpServer.Instance.SendImage(m_tag_id, data.Img_id);
            }
            e.Handled = true;
        }
    }
}
