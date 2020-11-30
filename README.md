### Eric Robertson created this plugin. I forked it from his repo, and it looks like this:
![img1](https://raw.githubusercontent.com/thenerdoflight/dynamic-marketplace/master/img1.PNG) ![img2](https://raw.githubusercontent.com/thenerdoflight/dynamic-marketplace/master/img2.PNG)

## Here's what I did

**Major Change:**  Added a permission node aptly named '***dynamicmarket.use***'
This node allows the usage of all dynamic market's commands that were previously accessible without a permission system.

**Planned Change:** Overhaul crafting calculation - this is in response to possible duping/ unlimited market.
Revise cost calculation - Possible negative selling price

**Other**
I have also written a Matlab program that determines the DynamicMarket cost given a selling price.
Ex. If you want to sell an item for $10. Using the Matlab program, with Quantity Scaler @ 25000 and tax at 2, the DynamicMarket cost would be 80.

**PS**
I can't code, so don't expect anything good. This page will be updated as the plugin updates.


# Also Install TL;DR: Push this into Eclipse

### Install Steps
- The Folder `Dynamic Market` should be put into the plugins folder of the server. It contains the data about recipies that is loaded by the plugin on startup.
- The file `DynamicMarketplace.jar` should be put into the plugins folder of the server
- Information about configuring the plugin is in the CONFIG.txt in Dynamic Marketplace

### Accessing Source
- Do whatever you like with my source for this project, just credit me
- The entire project is in the source folder and should run in any ide with maven
- Run `mvn install` when in the `source` directory and it should generate a build of the plugin
- The classes and files are in `source/main/java`
