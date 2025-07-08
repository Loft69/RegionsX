# How to use API

### Maven dependency

```xml
<dependency>
  <groupId>dev.thew</groupId>
  <artifactId>regionsx</artifactId>
  <version>LATEST</version>
</dependency>
```

### Example get Region

```java
Location location = getLocation();

RegionHandler regionHandler = RegionsAPI.getHandler(RegionService.class);
Region region = regionHandler.getRegion(location);
```

### OR

```java
String regionId = "world;x;y;z"; // example id

RegionHandler regionHandler = RegionsAPI.getHandler(RegionService.class);
Region region = regionHandler.getRegionById(regionId); 
```

# RegionEvents

## RegionCreateEvent

* Can be cancelled
* Called when the player puts a private block 

## RegionJoinEvent

* Can't be cancelled
* An event is called when a player enters a region

## RegionQuitEvent

* Can't be cancelled
* An event is called when a player quit a region

## RegionRemoveEvent

* Can't be cancelled
* The event informs that the region has been deleted

## RegionRemovePrimeEvent

* Can be cancelled
* The event is called before the region is removed

## RegionExplodeEvent

* Can be cancelled
* An event is triggered when a region takes damage (provided it has durability)

# config.yml

``` yml
database-url: "jdbc:mysql://user:password@ip:port/database"
```

# regionTypes.yml

``` yml
#placehodlers for holo lines: {region.x} {region.y} {region.z} {region.radius}
# {region.id} {region.size.render} {region.owner} {current.endurance} {max.endurance}
types:
  10:
    holo:
      hide:
        canHide: false # Can a private person hide his hologram
        canUse: OWNER # Who can hide their hologram OWNER/MEMBERS (OWNER can always hide the hologram if canHide true)
      titleItem: true # Item above the hologram
      appendY: 3.5 # How much to raise the hologram from the center of the private
      lines: # Hologram lines
        - "&fCustom region"
        - '{region.size.render}'
        - ''
        - '&fOwner &e{region.owner}'
    material: GOLD_BLOCK # Material region
    endurance: 0 # Endurance region 
    canExplode: true # Can the region explode
    radius: 10 # Region radius
    worlds:
      - world # Worlds Worlds where you can set a region
    color: "§f" # Color for {region.size.render}
```

# Addons

## RegionPotion

* The ability to give a potion effect to a player who has entered a certain region

### config.yml
``` yml
effects:
  'donate':
    regionTypeId: 'donate' # from Regions.regionTypes.yml
    list:
      '0':
        type: FAST_DIGGING
        duration: 99999
        amplifier: 2
        ambient: true
        particle: true
```

## RegionsTNT

* Adds custom TNT. By default, it prohibits the explosion of a region from the core

### config.yml
``` yml
tnts:
  'customtnt':
    corners:  # If TNT blows up the region, the borders will be replaced
      enable: false
      material: AIR
    fire: # Fire to all players within the TNT range
      enable: false
      radius: 0
      ticks: 0
    toremove: [] # Blocks in the radius (any) will be replaced
    explosion:
      yield: 4
      setGlowing: false
      fuseTicks: 40
      damaged: 0
    item:
      name: "&fCustomTNT"
      lore: []
```
