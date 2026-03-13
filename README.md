# crisafe

## Argon2

https://it.wikipedia.org/wiki/Argon2
https://en.wikipedia.org/wiki/Argon2


Create a Class with 2 method cript and declipt, that methods act in a file using Argon2id algo using a salt written in a properties file.


Argon2id

1) Create a class for code/decode a password using Argon2id, that class obtains salt from a property file. 
2) Create a class generate a random salt put salt in a
3) in @src/main/java/com/crisafe/Main.java when start program, in console ask for a password     
4) in @src/main/java/com/crisafe/Main.java when start program if there is no file called 'archive.json' generate it contain a empty json file like [Pasted text #1 +5 lines] and

## Start program

1) When the programme starts, ask whether you want to create a new archive (file)
2) Or whether you want to select an existing file from among the encrypted ones
3) If you want to create a new file, enter the password for the file; it must contain a JSON string formatted as follows: {‘00’: ‘Example’}, then be encrypted using AES-GCM with its own salt and the password encrypted using argon2Hash
4) The file must be saved at the same level as the project JAR

