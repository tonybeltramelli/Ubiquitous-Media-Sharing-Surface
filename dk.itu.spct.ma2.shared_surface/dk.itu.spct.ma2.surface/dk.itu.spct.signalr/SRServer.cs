using dk.itu.spct.signalr;
using Microsoft.AspNet.SignalR;
using Microsoft.Owin;
using Microsoft.Owin.Cors;
using Microsoft.Owin.Hosting;
using Owin;
using System;

[assembly: OwinStartup(typeof(SignalRSelfHost.Startup))]
namespace SignalRSelfHost
{
    class SRServer
    {
        public const string port = "5555";
        public const string url = "http://*:";

        //Run server
        
        public SRServer()
        {             
            using (WebApp.Start(url + port))
            {
                Console.WriteLine("--Server running on {0}", url + port);
                ConnectionManager.Instance.StartImageListener(Convert.ToInt32(port));
                while (true)
                {
                    Console.ReadLine();
                }
            }
        }
    }

    class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            app.UseCors(CorsOptions.AllowAll);
            app.MapSignalR();
        }
    }

    public class Listener : Hub
    {
        //Register user
        public void Register(int tag_id)
        {
            ConnectionManager.Instance.AddConnection(tag_id,Context.ConnectionId);
        }
        //Prepare for image sending
        public void SendImageMetadata(string file_name, int size)
        {
            ConnectionManager.Instance.ReceiveImageMetaData(Context.ConnectionId,file_name,size);
        }
    }
}
