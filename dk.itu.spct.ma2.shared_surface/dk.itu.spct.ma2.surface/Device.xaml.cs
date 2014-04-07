using System;
using System.Windows;
using Microsoft.Surface.Presentation;
using Microsoft.Surface.Presentation.Controls;
using Microsoft.Surface.Presentation.Input;

using dk.itu.spct.tcp;

namespace TaggingLabClass
{
	public partial class Device{

        private bool isDevicePinned = false;
        private bool isDevicePresent = false;

		public Device(){
			InitializeComponent();
            GotTag += OnTagAddedOnTheSurface;
            LostTag += OnTagRemovedFromTheSurface;
		}

		void OnTagAddedOnTheSurface( object s, RoutedEventArgs e ){
			Visibility = Visibility.Visible;
            isDevicePresent = true;
            if(!isDevicePinned)
                TcpServer.Instance.requestDeviceGallery((int)this.VisualizedTag.Value);
		}

        void OnTagRemovedFromTheSurface(object s, RoutedEventArgs e)
        {
            isDevicePresent = false;
            if(!isDevicePinned)
                TcpServer.Instance.DisconnectDevice((int)this.VisualizedTag.Value);
        }

        void OnPinButtonClicked(object sender, RoutedEventArgs e)
        {
			if ( TagRemovedBehavior == TagRemovedBehavior.Wait ){
                //if (!isDevicePresent) {
                    //phone was unpinned so remove everything
                    isDevicePinned = false;
                    TcpServer.Instance.DisconnectDevice((int)this.VisualizedTag.Value);
                    TagRemovedBehavior = TagRemovedBehavior.Fade;
                    Visibility = Visibility.Hidden;
                //}
			}else{
                //phone was pinned so do not close socket do not remove shit
                isDevicePinned = true;
                TagRemovedBehavior = TagRemovedBehavior.Wait;
			}
		}
	}
}
