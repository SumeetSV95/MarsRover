Readme

Both NASASpace and NASAEarth take 2 arguments which are the file path and the IP address. IP addresses is of the entity to which the packet in being sent to.

NASASapce is the code that reads a file and sends the data through the network to NASA earth.

Order of execution is : 
First, build both the projects.
Run NASA earth first and attach the volume.
An example command is given below:-

docker run -it -p 8080:8080 -v /Users/sumeet95/Downloads/neeciever:/usr/src/myapp --cap-add=NET_ADMIN --net pipernet --ip 172.18.0.21 recievermain /usr/src/myapp/newfile.jpg 172.18.0.22

Then run the NASASpace project
An example command is given below:-

docker run -it -p 8081:8080 -v /Users/sumeet95/Downloads/networks/sendingfile:/usr/src/myapp --cap-add=NET_ADMIN --net pipernet --ip 172.18.0.22 javaapptest /usr/src/myapp/sent.jpg 172.18.0.21

To drop packets the commands are as follows:-

 curl "http://localhost:8081/?indrop=0.5"
 curl "http://localhost:8080/?indrop=0.5"

For commands start NasaEarth with command line argument as  “Move”/“move” followed by the IP address.
Then start NASASpace with command line argument as “Move”or “move” followed by the IP address.