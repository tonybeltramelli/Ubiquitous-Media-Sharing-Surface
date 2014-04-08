using System;
using System.Windows;
using Microsoft.Surface;
using Microsoft.Surface.Presentation.Controls;
using Microsoft.Surface.Presentation.Input;

using dk.itu.spct.tcp;

using dk.itu.spct.ma2.surface;

namespace TaggingLabClass
{
	public partial class Device{

        private int m_tag_id = 0;
        private bool isDevicePinned = false;
        private bool isDevicePresent = false;

		public Device(){
			InitializeComponent();
            GotTag += OnTagAddedOnTheSurface;
            LostTag += OnTagRemovedFromTheSurface;
		}

		void OnTagAddedOnTheSurface( object s, RoutedEventArgs e ){
            m_tag_id = (int)this.VisualizedTag.Value;
			Visibility = Visibility.Visible;
            isDevicePresent = true;
            if(!isDevicePinned)
                TcpServer.Instance.requestDeviceGallery(m_tag_id);
		}

        void OnTagRemovedFromTheSurface(object s, RoutedEventArgs e)
        {
            isDevicePresent = false;
            if (!isDevicePinned) {
                TcpServer.Instance.DisconnectDevice(m_tag_id);
                TagRemovedBehavior = TagRemovedBehavior.Fade;
                Visibility = Visibility.Hidden;
            }
        }

        void OnPinButtonClicked(object sender, RoutedEventArgs e){
            SurfaceButton button = (SurfaceButton)sender;
			if ( TagRemovedBehavior == TagRemovedBehavior.Wait ){
                //phone was unpinned so remove everything
                button.Content = "Lock";
                isDevicePinned = false;
                if(!isDevicePresent) {
                    TcpServer.Instance.DisconnectDevice(m_tag_id);
                    TagRemovedBehavior = TagRemovedBehavior.Fade;
                    Visibility = Visibility.Hidden;
                }
			}else{
                //phone was pinned so do not close socket do not remove shit
                button.Content = "Unlock";
                isDevicePinned = true;
                TagRemovedBehavior = TagRemovedBehavior.Wait;
			}
		}

        private void OnVisualizationDrop(object sender, DragEventArgs e) {
                Console.WriteLine("PreviewDrop");
                sendImage(e);
        }

        private void OnVisualizationEnter(object sender, DragEventArgs e) {
            Console.WriteLine("Enter");
            sendImage(e);
        }

        private void Grid_PreviewGiveFeedback(object sender, DragEventArgs e) {
                Console.WriteLine("Drop");
                sendImage(e);
        }

        private void sendImage(DragEventArgs e){
            if (e.Data.GetDataPresent("img_id")) {
                int img_id = Convert.ToInt32(e.Data.GetData("img_id"));
                //TODO uncomment
                //TcpServer.Instance.SendImage(m_tag_id, img_id);
            }
        }
	}
}
