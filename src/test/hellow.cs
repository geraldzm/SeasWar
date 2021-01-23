using System;
using System.Net;  
using System.Net.Sockets;  
using System.Text;  

namespace Client {

    public class Message {
        public String text { get; set; }
        public String[] texts { get; set; }

        public int number { get; set; }
        public int[] numbers { get; set; }
    }

    class MainClass {
        public static void Main (string[] args) {
            	
        	byte[] bytes = new byte[2048];
  
        	try {  
                // Connect to a Remote server  
                // Get Host IP Address that is used to establish a connection  
                // In this case, we get one IP address of localhost that is IP : 127.0.0.1  
                // If a host has multiple addresses, you will get a list of addresses  
                IPHostEntry host = Dns.GetHostEntry("localhost");  
                IPAddress ipAddress = host.AddressList[0];  
                IPEndPoint remoteEP = new IPEndPoint(ipAddress, 42069);  

                // Create a TCP/IP  socket.    
                Socket socket = new Socket(ipAddress.AddressFamily, 
                SocketType.Stream, ProtocolType.Tcp);  

                // Connect the socket to the remote endpoint. Catch any errors.    
                try {  
                    // Connect to Remote EndPoint  
                    socket.Connect(remoteEP);
    
                
                    // Receive the response from the remote device.    
                    int bytesRec = socket.Receive(bytes);  

                    Console.WriteLine(bytesRec + " bytes");
                    Console.WriteLine("Echoed test = {0}",  
                        Encoding.ASCII.GetString(bytes, 0, bytesRec));


                    //--------Response----------
                    Message message = new Message();
                    message.text = "Hellow the meaning of life is: ";
                    message.number = 43;

                    string jsonString = "{\"text\":"+message.text+",\"number\":"+message.number+"}";

                    // Encode the data string into a byte array.    
                    byte[] msg = Encoding.ASCII.GetBytes(jsonString);  
    
                    // Send the data through the socket.    
                    int bytesSent = socket.Send(msg);  
                    Console.WriteLine(bytesSent + " bytes sent");

                    // Release the socket.    
                    socket.Shutdown(SocketShutdown.Both);  
                    socket.Close();  

                } catch (ArgumentNullException ane) {  
                    Console.WriteLine("ArgumentNullException : {0}", ane.ToString());  
                } catch (SocketException se) {  
                    Console.WriteLine("SocketException : {0}", se.ToString());  
                } catch (Exception e) {  
                    Console.WriteLine("Unexpected exception : {0}", e.ToString());  
                }  
    
            } catch (Exception e) {  
                Console.WriteLine(e.ToString());  
            }      
    	
    	}
    }

}
