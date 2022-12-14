package za.co.wethinkcode.weshare.app.model;

import za.co.wethinkcode.weshare.util.Strings;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * I represent a person who participates in sharing expenses. I am identified by an email address which
 * uniquely identifies me. (No other Persons may use the same email address.)
 * <p>
 * My sole responsibility is to identify an expense participant.
 */
public class Person {

    private final String email;
    private Long id;

    public Person( String email ){
        this.email = checkNotNull( email );
    }

    public String getEmail(){
        return email;
    }

    @Override
    public String toString(){
        return "Person{" +
            "id='" + email + '\'' +
            '}';
    }

    @Override
    public boolean equals( Object o ){
        if( this == o ) return true;
        if( o == null || getClass() != o.getClass() ) return false;
        Person person = (Person) o;
        return email.equalsIgnoreCase( person.email );
    }

    @Override
    public int hashCode(){
        return Objects.hash( email );
    }

    public String getName(){
        String pseudonym = this.email.split( "@" )[ 0 ];
        return Strings.capitaliseFirstLetter( pseudonym );
    }

    public Long getId(){
        return id;
    }

    public void setId( Long id ){
        this.id = id;
    }
}