# DKVF

Welcome!

Please refer to Wiki.
https://github.com/roohitavaf/DKVF/wiki

## Run

### Via Ansible Playbooks
- Change `vars.yml` and `inventory.ini` for your system
- Create a topology via yml file (example in `topology.yml`)
- Clean existing deploy with `ansible-playbook clean.yml`
- Provision clients and services `ansible-playbook init.yml provision-servers.yml provision-clients.yml`
- Start servers `ansible-playbook start-servers.yml`. On another terminal you can attach to the terminal output via tmux (do `tmux ls` for the open sessions).
- Start clients `ansible-playbook start-clients.yml`. You can also attach to their tmux sessions.


###
List projects:
```
$ gradlew projects
$ gradle tasks --all
```


### Implementing a new protocol
1. create a new folder for your new server consistency protocol
2. copy the build.gradle from one of the existing servers, open it and to the small necessary changes to refer to the new protocol
3. start with the protobuf message definition (use one the existing ones as example)


###