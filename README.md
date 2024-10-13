# Clans
Bukkit plugin that adds clans (and related things) to a minecraft server. Highly inspired by now-closed VanillaCraft server clan system.  
Currently uses 1.20.4 api, but may work back to the 1.13.x. May consider porting to even older version. (as VC was on 1.12.2)  

### Don't expect this to be completed any time soon - im doing this just for fun.  
### Don't expect high quality code, comments, commit names and other things - this is my personal project, not some enterprise-level product.

## Why?

Original plugin is obviously proprietary and private. Even if I had sources - I wouldn't been able to do anything with them including publishing.  
This plugin, on the other hand, is free (as in freedom), MIT licensed. I also want to improve some things - writing new plugin is the best way to do this.

## Why write a new plugin, when a bunch of others exists?
Read about NIH syndrome, yes. Also, there are no plugins, which are similar to VC clans.  
AFAIK, there are some servers, which are using modified version of vc's clans, but asking them to provide me a jar... is a bit silly, isn't it? Even if we don't look at licensing issues. (who cares about them if you're a pirate lol)

## Install

```bash
user@MacBookPro ~ % git clone https://github.com/whitebelyash/Clans
user@MacBookPro ~ % cd Clans
user@MacBookPro ~ % mvn package
```
Plugin jar will be in `./clans-bukkit/target/clans-bukkit-0.1-SNAPSHOT.jar`, if build was successful. If not - sorry, I can't do much - it works for me(TM).

## Structure
#### clans-common
Common clans logic including commands, clan management, database code, other things...
#### clans-bukkit
Bukkit-specific (actually using spigot-api) logic, including command implementations, init/shutdown logic, etc. Suffixed with *Bukkit.  

Ports to other platforms are possible, just not now.

## TODO

- [+] Basic clan management (add/remove)
- [+] Command system
- [+] Config wrapper
- [+] Switch to SLF4J - required for Fabric port
- [+] Locale/Language system instead of hardcoded strings
- - [ ] Multilocale support (consider getting language from player's settings?)
- - [ ] Improve PlayerActor/ConsoleActor message sending code
- [+] Databases (SQL)
- - [+] SQLite backend
- - [-] H2 backend - clan exporting broken now.
- - [ ] MySQL/MariaDB
- - [ ] other databases, maybe even not SQL...
- [ ] Member management
- [ ] All commands
#### and there goes the features list...
- [ ] UI system (using game inventory)
- [ ] reputation system?
- [ ] allies/rivals
- [ ] clan chats, ally chats
- [ ] permission management
#### more will come...



