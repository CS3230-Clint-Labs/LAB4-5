Create a chat client using the ConnectionServer code that was created in class.
This does not require use of the Student class, but make sure you are thinking about how that classes instances
would be integrated to a chat (and other functionality) server that required knowing which student was connected,
and writing code accordingly. The chat client should start the server thread and create the UI,
then send messages to the server using the Ctrl+Enter/Sent functionality. Instead of outputting to system out, have
the server send the message on to the client to output to the main chat window.


No exception handling is required, but you do have to accept an IP address in some manner. Whether that be
via console, command line, a dialog, or any other option. "ConnectionServer.java" is the listing for the file
we used in class. The server is already implemented, with an input and output stream, you just have to wire
those streams up to the correct place.

