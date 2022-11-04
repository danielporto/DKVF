# DKVF

Welcome! 

Please refer to Wiki. 
https://github.com/roohitavaf/DKVF/wiki


### 
List projects:
```
$ gradlew projects
$ gradle tasks --all
```


### implementing a new protocol
1. create a new folder for your new server consistency protocol
2. copy the build.gradle from one of the existing servers, open it and to the small necessary changes to refer to the new protocol
3. start with the protobuf message definition (use one the existing ones as example)
4. 