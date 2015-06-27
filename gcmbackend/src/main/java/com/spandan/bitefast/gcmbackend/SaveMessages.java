package com.spandan.bitefast.gcmbackend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by spandanv on 6/27/2015.
 */
@Entity
public class SaveMessages {
    @Id
    Long id;

    @Index
    String mesg;
    String from;
    String to;
}
