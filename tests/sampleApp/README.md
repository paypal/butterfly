# Sample application

This is a super simple JAX-RS application, based on RESTEasy and Spring framework.

## Starting the application

Just deploy its war file to a Servlet container and start it.

## Testing it

Send a **POST** request message, containing the payload below, to [http://localhost:8080/sample-app/echo](http://localhost:8080/sample-app/echo).

```
    is there anybody out there?
```

You should receive a response message with a payload similar to this as result:

``` json
    {
        "timestamp": "1484775122357",
        "echoText": "is there anybody out there?"
    }
```

The request message payload can be anything as plain text.
The response message is supposed to echo that, plus a timestamp of the moment the echo response was created on the server side. The response message will be in JSON format.
