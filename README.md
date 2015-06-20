CS3230 Lab 4 - Clint Fowler & Tyler Cazier

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

CS3230 Lab 5 - Clint Fowler & Tyler Cazier

Time to get the project ready to be your baseline for the midterm. Add exception handling, in the form of try-catch
blocks at the highest logical level you can give sane error messages (messages that mean something to the user). Log
the error messages to a file (as you won't always have access to System.out as a user-viewable resource, and it's
helpful for debug). Be aware that I will be trying to break your code as part of turn-in for Lab 5, so catch or handle
everything you can think of that may go wrong. If you choose to pass errors up the chain, be sure to document why you
made that decision.

Also, now is the time to clean up your object-oriented code. Make use of encapsulation - you'll lose points for
variables with broader access than strictly required, and I will be asking for an explanation of any case where I
consider the architecture to require broader access than could have been set. Ensure you're enforcing your data input
to mutator methods, and only provide mutators if the variable really needs to be manipulated by an outside resource.
Tighten up your classes to get them closer to the "single responsibility" gold standard - methods and classes should do
as little as possible.

The more you adhere to these guidelines, the easier group work and fixes on your own work become, because the less has
to be changed to make it all work together. This is a subjective assignment, and will be graded as before on an
individual basis, as no two code styles are alike, but I expect significant improvement in code structure to be
realized, while still retaining functionality. Consider writing tests to verify the behavior of your application,
although that is not a requirement for this assignment.

A review of other students' work is also part of this requirement, so get finished and submitted before the due date to
allow other students to review your work and give feedback.

