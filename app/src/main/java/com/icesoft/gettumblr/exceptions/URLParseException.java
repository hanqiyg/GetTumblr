package com.icesoft.gettumblr.exceptions;

import java.io.FileNotFoundException;

public class URLParseException extends Exception
{
    public URLParseException () {

    }

    public URLParseException (String message) {
        super (message);
    }

    public URLParseException (Throwable cause) {
        super (cause);
    }

    public URLParseException (String message, Throwable cause) {
        super (message, cause);
    }
}
