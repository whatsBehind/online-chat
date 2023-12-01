# online-chat

## Demo
https://github.com/whatsBehind/online-chat/assets/148703191/24f2159a-8c5e-4a8c-9e7f-5b7cc28f36a0

## High Level Architecture
This is an online chat system built with BIO (Blocking IO) Using Java. Each client has two socket connections with the server, one connection supports message push mode and another one supports message pull mode. 

The system now supports below features:
- Login
- Pull online users
- Online chat
- Logoff

Because of supports for push and pull modes, features like 
- Upload/Download files
- Group chat
can be easily added in the system. However, this project is just for learning purpose, so I didn't spend too much time on it. 

![online-chat-architecture](https://github.com/whatsBehind/online-chat/assets/148703191/9cc4a541-642a-40f1-897d-14d166889a71)


## Key Components
### Client
- Server Listener

It's basically a class extending `Thread` containing a socket connected with the server. In its `run()` method, it listens to `InputStream` of the socket which takes messages from the server. The socket in this component connects to `Publisher` from the server to support message push mode

``` Java
public class ServerListener extends Thread {
    private Socket socket;
    private boolean listening = true;
    public ServerListener(Socket socket, User user) {
        this.socket = socket;
        this.user = user;
    }
    public void run() {
        try {
            while(listening) {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message response = (Message) ois.readObject();
                MessageType type = response.getType();
                switch(type) {
                    ... // Operations
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
```

- Online Chat Client

It's a client class containing another socket connecting with the server. It supports message pull mode, which is that client sends a request to server and receives a response, and it's a synchronous request.
It has two private methods `sendRequest` and `receiveResponse` which are used to support communication with the server.

``` Java
public class OnlineChatClient {

    private Socket client;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private void sendRequest(Message request) throws IOException {
        oos = new ObjectOutputStream(client.getOutputStream());
        oos.writeObject(request);
    }

    private Message receiveResponse() throws IOException, ClassNotFoundException {
        ois = new ObjectInputStream(client.getInputStream());
        return (Message) ois.readObject();
    }
}
```

### Server
- Message Publisher

The name of this component is descriptive. Its duty is to publish a message to a corresponding client and receive the response from the server. It works with `Server Listener` in clients to support message push mode. Each message publisher has a 1 to 1 relationship with the client. All message publishers are managed by `Message Publisher Manager`

``` Java
public class MessagePublisher {
    private Socket socket;
    private ObjectOutputStream oos;

    public void publish(Object o) throws IOException {
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(o);
    }
}
```

- Message Publisher Manager

It maintains a `Map` to store all message publishers. The map's key is user id and the value is the message publisher. It provides methods like `add()` `get(String userId)` and `delete(String userId)` to manage all message publishers.
``` Java
public class MessagePublisherManager {
    private final static Map<String, MessagePublisher> publishers = new ConcurrentHashMap<>();
    public static boolean contains(User user) {
        final String id = user.getId();
        return publishers.containsKey(id);
    }
    public static MessagePublisher add(User user, Socket socket) {
        if (contains(user)) {
            return publishers.get(user);
        }
        final String id = user.getId();
        MessagePublisher publisher = new MessagePublisher(user, socket);
        return MessagePublisherManager.publishers.put(id, publisher);
    }

    public static MessagePublisher get(String userId) {
        return publishers.get(userId);
    }
    public static MessagePublisher get(User user) {
        return get(user.getId());
    }

    public static void remove(User user) {
        publishers.remove(user.getId());
    }
}
```

- Client Listener

It is similar to `Server Listener` in clients. It's a `Thread` subclass waiting for message from the server. The thread in the most time is blocked at line 
``` Java
Message response = (Message) ois.readObject();
```
Once messages sent to the socket's receive queue (`InputStream`), it reads the message and performs corresponding operation and returns a response to the client.

``` Java
public class ClientListener extends Thread {
    private Socket socket;
    private ObjectOutputStream oos;
    private boolean listening = true;

    public void run() {
        try {
            while(listening) {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message response = (Message) ois.readObject();
                MessageType type = response.getType();
                switch(type) {
                    ... // Operations
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void sendResponse(Message message) throws IOException {
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(message);
    }
}
```

- Client Listener Manager

It manages all client listeners in a `Map`, supporting methods like `add`, `get` and `remove`.

``` Java
public class ClientListenerManager {
    private final static Map<String, ClientListener> clientListeners = new ConcurrentHashMap<>();
    private static ObjectOutputStream oos;

    public static void add(Socket socket, User user) throws IOException {
        Message response = Message.builder()
                .type(MessageType.CONNECT)
                .content(gson.toJson(BaseResponse.builder().successful(true).build()))
                .timeStamp(new Date().toString()).build();
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(response);
        ClientListener listener = new ClientListener(socket, user);
        clientListeners.put(user.getId(), listener);
        listener.start();
    }

    public static ClientListener get(User user) {
        final String id = user.getId();
        return clientListeners.get(id);
    }

    public static ClientListener get(String id) {
        return clientListeners.get(id);
    }

    public static void remove(User user) {
        final String id = user.getId();
        clientListeners.remove(id);
    }
}
```

## Supported Features
### Login
- Client creates a new `Socket` connecting to port `9999` in local host (Server and clients are in local host) by sending below message. 
    - Message type is `USER_LOGIN`
    - Before sending the message, user needs to enter user id and password
    ``` Java
    Message request = Message.builder()
        .sender(user.getId())
        .timeStamp(new Date().toString())
        .type(MessageType.USER_LOGIN)
        .content(gson.toJson(user))
        .build();
    ```
![login1](https://github.com/whatsBehind/online-chat/assets/148703191/56247204-dd1c-4c99-af57-7b772c4d9650)



- Server receives the login message from the client
    - First server checks database if the user enters valid user id and password (Not implemented)
    - After id and password validations, server creates a new `MessagePublisher` and add it into `MessagePublisherManager`
    - In the end, server sends back a response to notify the client if login succeeds
- Client receives response from server. If login succeeded, client start a new `ServerListener` which is a subclass of `Thread` to listen to message from server. The thread is blocked when there is no messages from server. 
![login2](https://github.com/whatsBehind/online-chat/assets/148703191/0f297167-f541-483a-92fd-aa91afc7a809)


### Connect: 
This is not an API, but a process implicitly done in advance for features like `GetOnlineUsers` which utilizes `OnlineChatClient`

- Client creates a new `Socket` connecting to port `9999` in local host

![connect1](https://github.com/whatsBehind/online-chat/assets/148703191/60441e17-a36c-48eb-a4a2-d42540cfbe4e)


- After server receives the request, 
    - it starts a new `ClientListener`, the subclass of `Thread`, which is listening message from the client. 
    - Then server add the `ClientListener` to `ClientListenerManager`
    - Server send back response to the client
- Client receives response from server
  
![connect2](https://github.com/whatsBehind/online-chat/assets/148703191/ca70e0a5-0d2d-4f78-80e5-9254b719a2e4)


After above steps, a new socket connection between server and client is built, which supports message pull mode.

### GetOnlineUsers
- Client sends a request to server
- Server checks `MessagePublisherManager`, get all online users' id
- Server sends a response back to client
- Client renders online users' id in terminal

![pull-online-users](https://github.com/whatsBehind/online-chat/assets/148703191/e7fc406f-57cd-451b-add0-1a700121acd9)


### Chat
- ClientA sends a request to server containing receiver's id (ClientB) and the message
- Client listenerA in the server receives the request, it passes the message to message publisherB
- Message publisherB sends a request containing the message to clientB
- ClientB responds after receiving the message
- After message was successfully sent to clientB, client listenerA sends a response to clientA
- ClientA receives the response

![chat](https://github.com/whatsBehind/online-chat/assets/148703191/84aa51b6-5103-4032-a2d7-4c4ab8e363f2)

### Logoff

![logoff1](https://github.com/whatsBehind/online-chat/assets/148703191/f715e0a4-9053-4cea-9fd1-d86dd04c5b52)


- Client sends a request to the server to logoff
- Server receives the request, responds to the client. 
- Server close the socket in the client listener, and remove the listener from listener manager
- Server sends a request to client through message publisher
- Client receives the request, close socket in the server listen
- Client responds. Server receives the response from client. Then server closes the socket in message publisher and remove the message listener from listener manager

After all aboves steps, all sockets are closed and each listener thread (`ServerListener` in the client and `ClientListener` in the server) is terminated.

![logoff2](https://github.com/whatsBehind/online-chat/assets/148703191/7f6c278c-c686-4757-a32f-452b32e49d9d)


## To Be Improved
- This system is built on BIO. In server and client sides, there is one thread blocked to listen to messages sent to the input stream of the socket. Thread is expensive because of the memory it occupies and performance influence when CPU switching among different threads
- Listener is a resource shared by two threads, main thread and the listener thread. Current code doesn't ensure thread safety. The quick fix is to expose `InputStream` and `OutputStream` of the socket from the listener, and main thread can only access the two streams by calling the exposed methods. Also, the two methods should be synchronized.
