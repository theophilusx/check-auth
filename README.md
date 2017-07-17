# check-auth

This is a basic tool used for testing an Identity and Access Management
project. In this project, an IAM system is responsible for managing attributes
in openLDAP and Active Directory. The system also manages the password related
attributes in these two directories. 

As part of the project, we needed a tool which our user acceptance testers could
use to verify that password change and password reset functionality is working
correctly. This involves verifying that the hashed password values stored in
openLDAP and Active directory are correct and attributes relating to passwords,
such as attributes tracking the last change date, password expiration date etc
are all set correctly and that the hashing of values, such as the
sambaNTLMPassword attribute use the correct hash scheme. 

This tool uses the jcifs-1.3.18.jar library to do the Samba password hashing. It
does basic binds to LDAP and Active directory to verify the main password hashes
are correct and it queries the userpassword field in openLDAP to ensure that the
password has been encrypted using the correct hashing scheme. 

This is a diagnostic tool used for a limited reason. It is not terribly robust
and does not have the normal level of error checking you would expect in
production quality code.

## Installation

The first thing we need to do is install the Java jcifs library from the Samba
project. To do this, we use the lein localrepo plugin. 

Start by adding lien-localrepo to the plugins key in your lien profile.clj file

    [:user
      {:plugins [[lein-localrepo "0.5.4"]]}]
  
Then at the prompt with the jcifs-1.3.18.jar in the current directory, issue the
command

    lein localrepo coords ./jcifs-1.3.18.jar | xargs lein localrepo install

This will install the jcifs-1.3.18.jar into your local m2 repository. 

## Usage

Once everything is installed, you can run the program with 

    lein run <username> 
    
where <username> is the username you want to check. You will be prompted for the
password. Once you enter the password, the system will perform the checks and
display the results to you. 

Note that if you don't want to use lein, you can compile the program to a
standalone JAR file and run it directly using Java. 

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
